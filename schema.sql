CREATE SCHEMA zzm_api_os_01;

SET SEARCH_PATH TO zzm_api_os_01, zzm_data, public;

CREATE TYPE history_type AS ENUM
(
    'CHECK_DEFINITION',
    'ALERT_DEFINITION'
);
CREATE TYPE alert_comment AS (
    id                  int,
    created             timestamptz,
    created_by          text,
    last_modified       timestamptz,
    last_modified_by    text,
    comment             text,
    alert_definition_id int,
    entity_id           text
);
CREATE TYPE alert_definition_type AS (
    id                  int,
    name                varchar(256),
    description         text,
    team                varchar(256),
    responsible_team    varchar(256),
    entities            hstore[],
    entities_exclude    hstore[],
    condition           text,
    notifications       text[],
    check_definition_id int,
    status              zzm_data.definition_status,
    priority            int,
    last_modified       timestamptz,
    last_modified_by    text,
    period              text,
    template            boolean,
    parent_id           int,
    parameters          hstore,
    tags                text[]
);
CREATE TYPE check_definition_import AS (
    name                varchar(256),
    description         text,
    technical_details   text,
    potential_analysis  text,
    potential_impact    text,
    potential_solution  text,
    owning_team         varchar(256),
    entities            hstore[],
    "interval"          int,
    command             text,
    status              zzm_data.definition_status,
    source_url          text,
    last_modified_by    text,
    id                  int
);
CREATE TYPE check_definition_type AS (
    id                  int,
    name                varchar(256),
    description         text,
    technical_details   text,
    potential_analysis  text,
    potential_impact    text,
    potential_solution  text,
    owning_team         varchar(256),
    entities            hstore[],
    "interval"          int,
    command             text,
    status              zzm_data.definition_status,
    source_url          text,
    last_modified_by    text,
    last_modified       timestamptz
);
CREATE TYPE dashboard AS (
    id                      int,
    name                    text,
    created_by              text,
    last_modified           timestamptz,
    last_modified_by        text,
    widget_configuration    text,
    alert_teams             text[],
    view_mode               zzm_data.view_mode,
    edit_option             zzm_data.edit_option,
    shared_teams            text[],
    tags                    text[]
);

CREATE TYPE history_entry AS (
    id                  bigint,
    "timestamp"         timestamptz,
    "action"            zzm_data.history_action,
    row_data            hstore,
    changed_fields      hstore,
    user_name           text,
    record_id           int,
    history_type        history_type
);
CREATE TYPE operation_status AS ENUM
(
    'SUCCESS',
    'ALERT_DEFINITION_NOT_FOUND',
    'CHECK_DEFINITION_NOT_ACTIVE',
    'DELETE_NON_LEAF_ALERT_DEFINITION',
    'ALERT_DEFINITION_FIELD_MISSING'
);
CREATE OR REPLACE FUNCTION validate_alert_definition_children (
     IN  p_alert_definition     alert_definition_type,
     OUT status                 operation_status,
     OUT error_message          text
) AS
$BODY$
DECLARE
    l_alert_ids int[];
BEGIN
    WITH RECURSIVE tree(
         id                  ,
         name                ,
         description         ,
         entities            ,
         entities_exclude    ,
         condition           ,
         check_definition_id ,
         priority            ,
         template            ,
         parent_id
    ) AS (
      SELECT adt_id,
             coalesce(adt_name,                 p_alert_definition.name),
             coalesce(adt_description,          p_alert_definition.description),
             coalesce(adt_entities,             p_alert_definition.entities),
             coalesce(adt_entities_exclude,     p_alert_definition.entities_exclude),
             coalesce(adt_condition,            p_alert_definition.condition),
             coalesce(adt_check_definition_id,  p_alert_definition.check_definition_id),
             coalesce(adt_priority,             p_alert_definition.priority),
             adt_template,
             adt_parent_id
        FROM zzm_data.alert_definition_tree
       WHERE adt_parent_id = p_alert_definition.id
   UNION ALL
      SELECT c.adt_id,
             coalesce(c.adt_name,                 t.name),
             coalesce(c.adt_description,          t.description),
             coalesce(c.adt_entities,             t.entities),
             coalesce(c.adt_entities_exclude,     t.entities_exclude),
             coalesce(c.adt_condition,            t.condition),
             coalesce(c.adt_check_definition_id,  t.check_definition_id),
             coalesce(c.adt_priority,             t.priority),
             c.adt_template,
             c.adt_parent_id
        FROM zzm_data.alert_definition_tree c
        JOIN tree t
          ON c.adt_parent_id = t.id)
      SELECT array_agg(id)
        INTO l_alert_ids
        FROM tree t
       WHERE template = 'f'
         -- and at least one column is null
         AND (row(t.*) = row(t.*)) IS NULL;

    IF ARRAY_LENGTH(l_alert_ids, 1) IS NOT NULL AND ARRAY_LENGTH(l_alert_ids, 1) > 0 THEN
        status := 'ALERT_DEFINITION_FIELD_MISSING';
        error_message := 'Update breaks the following alert definitions: ' || l_alert_ids::text;
    ELSE
        status := 'SUCCESS';
    END IF;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION create_or_update_entity(entity_data text, teams text[], user_name text) RETURNS text AS
$$
DECLARE
  _id text;
  _data jsonb;
BEGIN
  SELECT e_data INTO _data FROM zzm_data.entity WHERE (e_data->'id')::text = ((entity_data::jsonb)->'id')::text;
  IF (entity_data::json->'type')::text IS DISTINCT FROM '"local"' AND _data IS NOT DISTINCT FROM entity_data::jsonb THEN
    RETURN (_data->'id')::text;
  END IF;

  BEGIN
    INSERT INTO zzm_data.entity(e_data, e_created_by, e_last_modified_by) SELECT entity_data::jsonb, user_name, user_name RETURNING (e_data->'id')::text INTO _id;
  EXCEPTION WHEN UNIQUE_VIOLATION THEN
    UPDATE zzm_data.entity
       SET e_data = entity_data::jsonb,
           e_last_modified = now(),
           e_last_modified_by = user_name
     WHERE (e_data->'id')::text = ((entity_data::jsonb)->'id')::text
       AND (
           (e_data->'team'::text)::text IS NULL
           OR REPLACE((e_data -> 'team'::text)::text, '"', '') = ANY(teams)
           OR e_created_by = user_name
       )
       AND (e_data IS DISTINCT FROM entity_data::jsonb OR (e_data->'type')::text='"local"')
     RETURNING (e_data->'id')::text INTO _id;
  END;
  RETURN _id;
END;
$$ LANGUAGE PLPGSQL VOLATILE SECURITY DEFINER;
CREATE OR REPLACE FUNCTION get_entity_by_id(id text) RETURNS SETOF jsonb AS
$$
  SELECT e_data FROM zzm_data.entity WHERE (e_data->'id')::text = '"'||id||'"';
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;
CREATE OR REPLACE FUNCTION get_entities(filter text) RETURNS SETOF jsonb AS
$$
 SELECT e_data || ('{"last_modified": "' || e_last_modified::text || '"}')::jsonb  FROM zzm_data.entity WHERE e_data @> ANY ( ARRAY( SELECT jsonb_array_elements(filter::jsonb) ) )
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
CREATE OR REPLACE FUNCTION delete_entity(id text, teams text[], user_name text) RETURNS SETOF text AS
$$
 DELETE FROM zzm_data.entity
  WHERE (((e_data -> 'id'::text)::text)) = '"'||id||'"'
    AND (
        (e_data->'team'::text)::text IS NULL
        OR REPLACE((e_data -> 'team'::text)::text, '"', '') = ANY(teams)
        OR e_created_by = user_name
    )
  RETURNING (((e_data -> 'id'::text)::text));
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
CREATE OR REPLACE FUNCTION get_alert_ids_by_check_id (
    IN p_check_definition_id int
) RETURNS SETOF int AS
$BODY$
    WITH RECURSIVE alert_definition(ad_id, ad_parent_id, ad_check_definition_id, ad_template) AS (
            SELECT adt_id,
                   adt_parent_id,
                   adt_check_definition_id,
                   adt_template
              FROM zzm_data.alert_definition_tree
             WHERE adt_check_definition_id = p_check_definition_id
             UNION
            SELECT adt_id,
                   adt_parent_id,
                   COALESCE(adt_check_definition_id, ad_check_definition_id),
                   adt_template
              FROM zzm_data.alert_definition_tree
              JOIN alert_definition ON adt_parent_id = ad_id
             WHERE COALESCE(adt_check_definition_id, ad_check_definition_id) = p_check_definition_id
    )
    SELECT ad_id
      FROM alert_definition
     WHERE NOT ad_template;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_alert_ids_by_status (
    IN status zzm_data.definition_status
) RETURNS SETOF int AS
$BODY$
    SELECT adt_id
      FROM zzm_data.alert_definition_tree
     WHERE adt_status = status
       AND adt_template = 'f';
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION get_all_check_definitions(
     IN status              zzm_data.definition_status,
    OUT snapshot_id         text,
    OUT check_definitions   check_definition_type[]
) AS
$BODY$
BEGIN
    SELECT MAX(cdh_id)
      INTO snapshot_id
      FROM zzm_data.check_definition_history;

    SELECT array_agg((
           cd_id,
           cd_name,
           cd_description,
           cd_technical_details,
           cd_potential_analysis,
           cd_potential_impact,
           cd_potential_solution,
           cd_owning_team,
           cd_entities,
           cd_interval,
           cd_command,
           cd_status,
           cd_source_url,
           cd_last_modified_by,
           cd_last_modified)::check_definition_type)
      INTO check_definitions
      FROM zzm_data.check_definition
     WHERE (status IS NULL OR cd_status = status);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION get_all_check_definitions(
     IN status              text,
    OUT snapshot_id         text,
    OUT check_definitions   check_definition_type[]
) AS
$BODY$
BEGIN
    SELECT MAX(cdh_id)
          INTO snapshot_id
          FROM zzm_data.check_definition_history;

        SELECT array_agg((
               cd_id,
               cd_name,
               cd_description,
               cd_technical_details,
               cd_potential_analysis,
               cd_potential_impact,
               cd_potential_solution,
               cd_owning_team,
               cd_entities,
               cd_interval,
               cd_command,
               cd_status,
               cd_source_url,
               cd_last_modified_by)::check_definition_type)
          INTO check_definitions
          FROM zzm_data.check_definition
         WHERE (status IS NULL OR cd_status = status::zzm_data.definition_status);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_all_tags (
) RETURNS SETOF text AS
$BODY$
    SELECT DISTINCT unnest(adt_tags)
      FROM zzm_data.alert_definition_tree
     WHERE adt_tags IS NOT NULL;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_all_teams (
) RETURNS SETOF text AS
$BODY$
    SELECT ad_team
      FROM zzm_data.alert_definition
     WHERE ad_team IS NOT NULL
     UNION
    SELECT ad_responsible_team
      FROM zzm_data.alert_definition
     WHERE ad_responsible_team IS NOT NULL
     UNION
    SELECT cd_owning_team
      FROM zzm_data.check_definition
     WHERE cd_owning_team IS NOT NULL;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_check_definitions(
     IN status              zzm_data.definition_status,
     IN p_ids               int[]
) RETURNS SETOF check_definition_type AS
$BODY$
BEGIN
    RETURN QUERY SELECT
           cd_id,
           cd_name,
           cd_description,
           cd_technical_details,
           cd_potential_analysis,
           cd_potential_impact,
           cd_potential_solution,
           cd_owning_team,
           cd_entities,
           cd_interval,
           cd_command,
           cd_status,
           cd_source_url,
           cd_last_modified_by
      FROM zzm_data.check_definition
     WHERE (status IS NULL OR cd_status = status)
       AND (p_ids IS NULL OR cd_id = ANY(p_ids));
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION get_check_definitions(
     IN status              text,
     IN alert_ids           int[]
) RETURNS SETOF check_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT *
          FROM get_check_definitions(status::zzm_data.definition_status, alert_ids);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_alert_last_modified_max() RETURNS timestamptz AS
$$
 select max(adt_last_modified) from zzm_data.alert_definition_tree;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
CREATE OR REPLACE FUNCTION get_check_last_modified_max() RETURNS timestamptz AS
$$
 select max(cd_last_modified) from zzm_data.check_definition;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
CREATE OR REPLACE FUNCTION get_alert_definition_children (
     IN alert_definition_id int
) RETURNS SETOF alert_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT ad_id,
               ad_name,
               ad_description,
               ad_team,
               ad_responsible_team,
               ad_entities,
               ad_entities_exclude,
               ad_condition,
               ad_notifications,
               ad_check_definition_id,
               ad_status,
               ad_priority,
               ad_last_modified,
               ad_last_modified_by,
               ad_period,
               ad_template,
               ad_parent_id,
               ad_parameters,
               ad_tags
          FROM zzm_data.alert_definition
         WHERE ad_parent_id = alert_definition_id;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION get_alert_definition_node (
     IN alert_definition_id int
) RETURNS alert_definition_type AS
$BODY$
    SELECT adt_id,
           adt_name,
           adt_description,
           adt_team,
           adt_responsible_team,
           adt_entities,
           adt_entities_exclude,
           adt_condition,
           adt_notifications,
           adt_check_definition_id,
           adt_status,
           adt_priority,
           adt_last_modified,
           adt_last_modified_by,
           adt_period,
           adt_template,
           adt_parent_id,
           adt_parameters,
           adt_tags
      FROM zzm_data.alert_definition_tree
     WHERE adt_id = alert_definition_id;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_alert_definitions_by_team_and_tag(
     IN status              zzm_data.definition_status,
     IN teams               text[],
     IN tags                text[]
) RETURNS SETOF alert_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT ad_id,
               ad_name,
               ad_description,
               ad_team,
               ad_responsible_team,
               ad_entities,
               ad_entities_exclude,
               ad_condition,
               ad_notifications,
               ad_check_definition_id,
               ad_status,
               ad_priority,
               ad_last_modified,
               ad_last_modified_by,
               ad_period,
               ad_template,
               ad_parent_id,
               ad_parameters,
               ad_tags
          FROM zzm_data.alert_definition
         WHERE (status IS NULL OR ad_status = status)
           AND (teams IS NULL OR ad_team ILIKE ANY (teams))
           AND (tags IS NULL OR tags && ad_tags);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_alert_definitions_by_team(
     IN status              zzm_data.definition_status,
     IN teams               text[]
) RETURNS SETOF alert_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT ad_id,
               ad_name,
               ad_description,
               ad_team,
               ad_responsible_team,
               ad_entities,
               ad_entities_exclude,
               ad_condition,
               ad_notifications,
               ad_check_definition_id,
               ad_status,
               ad_priority,
               ad_last_modified,
               ad_last_modified_by,
               ad_period,
               ad_template,
               ad_parent_id,
               ad_parameters,
               ad_tags
          FROM zzm_data.alert_definition
         WHERE (status IS NULL OR ad_status = status)
           AND (
                (ad_team ILIKE ANY (teams))
                OR
                (ad_responsible_team ILIKE ANY (teams))
               );
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION get_alert_definitions_by_team(
     IN status              text,
     IN teams               text[]
) RETURNS SETOF alert_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT *
          FROM get_alert_definitions_by_team(status::zzm_data.definition_status, teams);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_alert_definitions(
     IN status              zzm_data.definition_status,
     IN alert_ids           int[]
) RETURNS SETOF alert_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT ad_id,
               ad_name,
               ad_description,
               ad_team,
               ad_responsible_team,
               ad_entities,
               ad_entities_exclude,
               ad_condition,
               ad_notifications,
               ad_check_definition_id,
               ad_status,
               ad_priority,
               ad_last_modified,
               ad_last_modified_by,
               ad_period,
               ad_template,
               ad_parent_id,
               ad_parameters,
               ad_tags
          FROM zzm_data.alert_definition
         WHERE (ad_id = ANY (alert_ids))
           AND (status IS NULL OR ad_status = status);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION get_alert_definitions(
     IN status              text,
     IN alert_ids           int[]
) RETURNS SETOF alert_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT *
          FROM get_alert_definitions(status::zzm_data.definition_status, alert_ids);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION get_all_alert_definitions(
) RETURNS SETOF alert_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT ad_id,
               ad_name,
               ad_description,
               ad_team,
               ad_responsible_team,
               ad_entities,
               ad_entities_exclude,
               ad_condition,
               ad_notifications,
               ad_check_definition_id,
               ad_status,
               ad_priority,
               ad_last_modified,
               ad_last_modified_by,
               ad_period,
               ad_template,
               ad_parent_id,
               ad_parameters,
               ad_tags
          FROM zzm_data.alert_definition;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_check_definitions_by_owning_team(
     IN status       zzm_data.definition_status,
     IN owning_teams text[]
) RETURNS SETOF check_definition_type AS
$BODY$
BEGIN
  RETURN QUERY
        SELECT cd_id,
               cd_name,
               cd_description,
               cd_technical_details,
               cd_potential_analysis,
               cd_potential_impact,
               cd_potential_solution,
               cd_owning_team,
               cd_entities,
               cd_interval,
               cd_command,
               cd_status,
               cd_source_url,
               cd_last_modified_by
          FROM zzm_data.check_definition
         WHERE (status IS NULL OR cd_status = status)
           AND cd_owning_team ILIKE ANY (owning_teams);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION get_check_definitions_by_owning_team(
     IN status              text,
     IN owning_teams        text[]
) RETURNS SETOF check_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT *
          FROM get_check_definitions_by_owning_team(status::zzm_data.definition_status, owning_teams);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION get_check_definitions_diff(
     IN last_snapshot_id    bigint,
    OUT snapshot_id         bigint,
    OUT check_definitions   check_definition_type[]
) AS
$BODY$
BEGIN
    SELECT MAX(cdh_id)
      INTO snapshot_id
      FROM zzm_data.check_definition_history;

    SELECT array_agg((
           h.cdh_check_definition_id,
           c.cd_name,
           c.cd_description,
           c.cd_technical_details,
           c.cd_potential_analysis,
           c.cd_potential_impact,
           c.cd_potential_solution,
           c.cd_owning_team,
           c.cd_entities,
           c.cd_interval,
           c.cd_command,
           c.cd_status,
           c.cd_source_url,
           c.cd_last_modified_by)::check_definition_type)
      INTO check_definitions
      FROM (
                SELECT DISTINCT cdh_check_definition_id
                           FROM zzm_data.check_definition_history
                          WHERE (last_snapshot_id IS NULL OR cdh_id > last_snapshot_id)
                            AND snapshot_id IS NOT NULL
            ) h
  LEFT JOIN zzm_data.check_definition c ON (c.cd_id = h.cdh_check_definition_id);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_active_alert_definitions_diff(
    OUT snapshot_id         bigint,
    OUT alert_definitions   alert_definition_type[]
) AS
$BODY$
BEGIN
    SELECT MAX(adh_id)
      INTO snapshot_id
      FROM zzm_data.alert_definition_history;

    SELECT array_agg((
           ad_id,
           ad_name,
           ad_description,
           ad_team,
           ad_responsible_team,
           ad_entities,
           ad_entities_exclude,
           ad_condition,
           ad_notifications,
           ad_check_definition_id,
           ad_status,
           ad_priority,
           ad_last_modified,
           ad_last_modified_by,
           ad_period,
           ad_template,
           ad_parent_id,
           ad_parameters,
           ad_tags)::alert_definition_type)
      INTO alert_definitions
      FROM zzm_data.alert_definition
     WHERE ad_template = 'f'
       AND ad_status = 'ACTIVE';
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_alert_definitions_diff(
     IN last_snapshot_id    bigint,
    OUT snapshot_id         bigint,
    OUT alert_definitions   alert_definition_type[]
) AS
$BODY$
BEGIN
    SELECT MAX(adh_id)
      INTO snapshot_id
      FROM zzm_data.alert_definition_history;

    IF last_snapshot_id IS NULL THEN
        SELECT array_agg((
               ad_id,
               ad_name,
               ad_description,
               ad_team,
               ad_responsible_team,
               ad_entities,
               ad_entities_exclude,
               ad_condition,
               ad_notifications,
               ad_check_definition_id,
               ad_status,
               ad_priority,
               ad_last_modified,
               ad_last_modified_by,
               ad_period,
               ad_template,
               ad_parent_id,
               ad_parameters,
               ad_tags)::alert_definition_type)
          INTO alert_definitions
          FROM zzm_data.alert_definition
         WHERE ad_template = 'f';
    ELSE
        WITH RECURSIVE tree(id) AS (
             SELECT adh_alert_definition_id
               FROM zzm_data.alert_definition_history
              WHERE adh_id > last_snapshot_id
              UNION
             SELECT adt_id
               FROM zzm_data.alert_definition_tree a
               JOIN tree t
                 ON a.adt_parent_id = t.id)
        SELECT array_agg((
               t.id,
               ad_name,
               ad_description,
               ad_team,
               ad_responsible_team,
               ad_entities,
               ad_entities_exclude,
               ad_condition,
               ad_notifications,
               ad_check_definition_id,
               ad_status,
               ad_priority,
               ad_last_modified,
               ad_last_modified_by,
               ad_period,
               ad_template,
               ad_parent_id,
               ad_parameters,
               ad_tags)::alert_definition_type)
          INTO alert_definitions
          FROM tree t
      LEFT JOIN zzm_data.alert_definition a
             ON (a.ad_id = t.id
            AND a.ad_template = 'f');
    END IF;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION create_or_update_grafana_dashboard(id text, title text, dashboard text, user_name text, version text) RETURNS void AS
$$
BEGIN
  RAISE WARNING 'dashboard: % title: %', dashboard, title;

  BEGIN

    INSERT INTO zzm_data.grafana_dashboard(gd_id, gd_title, gd_dashboard, gd_grafana_version, gd_created_by, gd_last_modified_by)
         SELECT id, title, dashboard::jsonb, version, user_name, user_name;

  EXCEPTION WHEN UNIQUE_VIOLATION THEN

    UPDATE zzm_data.grafana_dashboard
       SET gd_title = title,
           gd_dashboard = dashboard::jsonb,
           gd_last_modified = now(),
           gd_last_modified_by = user_name,
           gd_grafana_version = version
     WHERE gd_id = id;

  END;
END;
$$ LANGUAGE PLPGSQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_grafana_dashboards(IN s_title TEXT, IN s_tags TEXT, IN s_starred TEXT, IN s_user TEXT, OUT id TEXT, OUT title TEXT, OUT dashboard TEXT, OUT "user" TEXT, OUT "tags" TEXT, OUT "starred" BOOLEAN, OUT "grafana_version" TEXT) RETURNS SETOF record AS
$$
  SELECT gd_id id, gd_title title, gd_dashboard::text dashboard, gd_created_by "user", (gd_dashboard->'tags')::text "tags", COALESCE(s_user =ANY(gd_starred_by), FALSE) "starred", gd_grafana_version "grafana_version"
    FROM zzm_data.grafana_dashboard
   WHERE gd_title ilike '%' || s_title || '%'
     AND (s_tags IS NULL OR (gd_dashboard->'tags') @> s_tags::jsonb)
     AND (s_starred IS NULL OR s_starred =ANY(gd_starred_by))
   ORDER BY gd_title ASC;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_grafana_dashboard(INOUT id text, IN user_name TEXT, OUT title text, OUT dashboard text, OUT "user" TEXT, OUT starred BOOLEAN, OUT "grafana_version" TEXT) RETURNS SETOF record AS
$$
  SELECT gd_id id, gd_title title, gd_dashboard::text dashboard, gd_created_by "user", COALESCE(user_name=ANY(gd_starred_by), FALSE) "starred", gd_grafana_version "grafana_version"
    FROM zzm_data.grafana_dashboard
   WHERE gd_id = id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION delete_grafana_dashboard(INOUT id text, IN user_name TEXT) RETURNS SETOF text AS
$$
  DELETE FROM zzm_data.grafana_dashboard
   WHERE gd_id = id
     AND gd_created_by = user_name
     RETURNING gd_id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_tags_with_count(OUT tag TEXT, OUT "count" INT) RETURNS SETOF RECORD AS
$$
select tag_name, count(1)::INT AS "count" from
          (select json_array_elements_text((gd_dashboard->'tags')::json) AS tag_name
             from zzm_data.grafana_dashboard) t
            group by 1;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION star_grafana_dashboard(INOUT id TEXT, IN user_name TEXT) RETURNS SETOF TEXT AS
$$
update zzm_data.grafana_dashboard
   set gd_starred_by = gd_starred_by || user_name
 where gd_id = id
   and not user_name =ANY(gd_starred_by)
 returning gd_id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION unstar_grafana_dashboard(INOUT id TEXT, IN user_name TEXT) RETURNS SETOF TEXT AS
$$
update zzm_data.grafana_dashboard
   set gd_starred_by = array_remove(gd_starred_by, user_name)
 where gd_id = id
   and user_name =ANY(gd_starred_by)
 returning gd_id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;CREATE OR REPLACE FUNCTION create_or_update_alert_definition_tree (
     IN  p_alert_definition     alert_definition_type,
     OUT status                 operation_status,
     OUT error_message          text,
     OUT entity                 alert_definition_type
) AS
$BODY$
DECLARE
    l_check_definiton_status zzm_data.definition_status;
    l_merged_entity          alert_definition_type;
BEGIN
    -- validate entity

    -- prevent circular references
    IF p_alert_definition.parent_id IS NOT NULL THEN
        PERFORM 1
           FROM zzm_data.alert_definition_tree
          WHERE adt_id = p_alert_definition.parent_id
          limit 1;

            -- if the parent doesn't exist, return an error
             IF NOT FOUND THEN
                status := 'ALERT_DEFINITION_NOT_FOUND';
                error_message := 'Parent alert definition with id ' || p_alert_definition.parent_id || ' not found';
                RETURN;
            END IF;
    END IF;

    -- validate mandatory fields
    l_merged_entity := p_alert_definition;
    IF l_merged_entity.parent_id IS NOT NULL THEN
        SELECT coalesce(l_merged_entity.name, ad_name),
               coalesce(l_merged_entity.description, ad_description),
               coalesce(l_merged_entity.entities, ad_entities),
               coalesce(l_merged_entity.entities_exclude, ad_entities_exclude),
               coalesce(l_merged_entity.condition, ad_condition),
               coalesce(l_merged_entity.check_definition_id, ad_check_definition_id),
               coalesce(l_merged_entity.priority, ad_priority)
          FROM zzm_data.alert_definition
          INTO l_merged_entity.name,
               l_merged_entity.description,
               l_merged_entity.entities,
               l_merged_entity.entities_exclude,
               l_merged_entity.condition,
               l_merged_entity.check_definition_id,
               l_merged_entity.priority
         WHERE ad_id = l_merged_entity.parent_id;
    END IF;

    -- TODO fix concurrency issues
    IF l_merged_entity.check_definition_id IS NULL THEN
        status = 'ALERT_DEFINITION_FIELD_MISSING';
        error_message := 'check definition id is mandatory';
        RETURN;
    END IF;

    IF l_merged_entity.template = 'f' THEN
        IF l_merged_entity.name IS NULL THEN
            status = 'ALERT_DEFINITION_FIELD_MISSING';
            error_message := 'name is mandatory';
            RETURN;
        ELSIF l_merged_entity.description IS NULL THEN
            status = 'ALERT_DEFINITION_FIELD_MISSING';
            error_message := 'description is mandatory';
            RETURN;
        ELSIF l_merged_entity.entities IS NULL THEN
            status = 'ALERT_DEFINITION_FIELD_MISSING';
            error_message := 'entities filter is mandatory';
            RETURN;
        ELSIF l_merged_entity.entities_exclude IS NULL THEN
          status = 'ALERT_DEFINITION_FIELD_MISSING';
          error_message := 'exclude entities filter is mandatory';
          RETURN;
        ELSIF l_merged_entity.condition IS NULL THEN
            status = 'ALERT_DEFINITION_FIELD_MISSING';
            error_message := 'condition is mandatory';
            RETURN;
        ELSIF l_merged_entity.priority IS NULL THEN
            status = 'ALERT_DEFINITION_FIELD_MISSING';
            error_message := 'priority is mandatory';
            RETURN;
        END IF;
    END IF;

    SELECT v.status,
           v.error_message
      FROM validate_alert_definition_children(l_merged_entity) v
      INTO status,
           error_message;

     IF status <> 'SUCCESS' THEN
        RETURN;
     END IF;

    -- create entity
    entity := p_alert_definition;
    IF p_alert_definition.id IS NOT NULL THEN
        UPDATE zzm_data.alert_definition_tree
           SET adt_name                 = p_alert_definition.name,
               adt_description          = p_alert_definition.description,
               adt_priority             = p_alert_definition.priority,
               adt_team                 = p_alert_definition.team,
               adt_responsible_team     = p_alert_definition.responsible_team,
               adt_entities             = p_alert_definition.entities,
               adt_entities_exclude     = p_alert_definition.entities_exclude,
               adt_condition            = p_alert_definition.condition,
               adt_notifications        = p_alert_definition.notifications,
               adt_status               = p_alert_definition.status,
               adt_last_modified_by     = p_alert_definition.last_modified_by,
               adt_period               = p_alert_definition.period,
               adt_template             = p_alert_definition.template,
               adt_last_modified        = now(),
               adt_parameters           = p_alert_definition.parameters,
               adt_tags                 = p_alert_definition.tags
          FROM zzm_data.alert_definition
         WHERE adt_id = ad_id
           AND adt_id = p_alert_definition.id
           -- Updates to 'DELETED' alerts are not allowed
           AND ad_status <> 'DELETED'
     RETURNING adt_last_modified
          INTO entity.last_modified;

         IF NOT FOUND THEN
            status := 'ALERT_DEFINITION_NOT_FOUND';
            error_message := 'Alert definition with id ' || p_alert_definition.id || ' is not available for edition';
            RETURN;
         END IF;
    ELSE
        SELECT cd_status
          FROM zzm_data.check_definition
          INTO l_check_definiton_status
          -- use merged check_definition since it can be inherited
         WHERE cd_id = l_merged_entity.check_definition_id FOR SHARE;

        IF l_check_definiton_status IS NULL OR l_check_definiton_status <> 'ACTIVE' THEN
            status := 'CHECK_DEFINITION_NOT_ACTIVE';
            error_message := 'Check definition with id ' || p_alert_definition.check_definition_id || ' is not active';
            RETURN;
        END IF;

        INSERT INTO zzm_data.alert_definition_tree (
            adt_name,
            adt_description,
            adt_priority,
            adt_check_definition_id,
            adt_team,
            adt_responsible_team,
            adt_entities,
            adt_entities_exclude,
            adt_condition,
            adt_notifications,
            adt_status,
            adt_created_by,
            adt_last_modified_by,
            adt_period,
            adt_template,
            adt_parent_id,
            adt_parameters,
            adt_tags
        )
        VALUES (
            p_alert_definition.name,
            p_alert_definition.description,
            p_alert_definition.priority,
            p_alert_definition.check_definition_id,
            p_alert_definition.team,
            p_alert_definition.responsible_team,
            p_alert_definition.entities,
            p_alert_definition.entities_exclude,
            p_alert_definition.condition,
            p_alert_definition.notifications,
            p_alert_definition.status,
            p_alert_definition.last_modified_by,
            p_alert_definition.last_modified_by,
            p_alert_definition.period,
            p_alert_definition.template,
            p_alert_definition.parent_id,
            p_alert_definition.parameters,
            p_alert_definition.tags
        ) RETURNING
            adt_id,
            adt_last_modified
          INTO
            entity.id,
            entity.last_modified;
    END IF;

    status := 'SUCCESS';
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION create_or_update_check_definition (
     IN check_definition_import check_definition_import,
     OUT entity                 check_definition_type,
     OUT new_entity             boolean
) AS
$BODY$
BEGIN
    entity.name                 = check_definition_import.name;
    entity.description          = check_definition_import.description;
    entity.technical_details    = check_definition_import.technical_details;
    entity.potential_analysis   = check_definition_import.potential_analysis;
    entity.potential_impact     = check_definition_import.potential_impact;
    entity.potential_solution   = check_definition_import.potential_solution;
    entity.owning_team          = check_definition_import.owning_team;
    entity.entities             = check_definition_import.entities;
    entity.interval             = check_definition_import.interval;
    entity.command              = check_definition_import.command;
    entity.status               = check_definition_import.status;
    entity.source_url           = check_definition_import.source_url;
    entity.last_modified_by     = check_definition_import.last_modified_by;

    new_entity := FALSE;

    UPDATE zzm_data.check_definition
       SET cd_name                 = check_definition_import.name,
           cd_description          = check_definition_import.description,
           cd_technical_details    = check_definition_import.technical_details,
           cd_potential_analysis   = check_definition_import.potential_analysis,
           cd_potential_impact     = check_definition_import.potential_impact,
           cd_potential_solution   = check_definition_import.potential_solution,
           cd_owning_team          = check_definition_import.owning_team,
           cd_entities             = check_definition_import.entities,
           cd_interval             = check_definition_import.interval,
           cd_command              = check_definition_import.command,
           cd_status               = check_definition_import.status,
           cd_source_url           = check_definition_import.source_url,
           cd_last_modified_by     = check_definition_import.last_modified_by,
           cd_last_modified        = now()
     WHERE (lower(cd_source_url) = lower(check_definition_import.source_url) AND check_definition_import.id IS NULL)
        OR (lower(cd_name) = lower(check_definition_import.name) AND lower(cd_owning_team) = lower(check_definition_import.owning_team) AND check_definition_import.id IS NULL)
        OR (cd_id = check_definition_import.id)
 RETURNING cd_id INTO entity.id;

    IF NOT FOUND AND check_definition_import.id IS NULL THEN

        -- if it's not there, we should create a new one
        INSERT INTO zzm_data.check_definition (
            cd_name,
            cd_description,
            cd_technical_details,
            cd_potential_analysis,
            cd_potential_impact,
            cd_potential_solution,
            cd_owning_team,
            cd_entities,
            cd_interval,
            cd_command,
            cd_status,
            cd_source_url,
            cd_created_by,
            cd_last_modified_by
        )
        VALUES (
            check_definition_import.name,
            check_definition_import.description,
            check_definition_import.technical_details,
            check_definition_import.potential_analysis,
            check_definition_import.potential_impact,
            check_definition_import.potential_solution,
            check_definition_import.owning_team,
            check_definition_import.entities,
            check_definition_import.interval,
            check_definition_import.command,
            check_definition_import.status,
            check_definition_import.source_url,
            check_definition_import.last_modified_by,
            check_definition_import.last_modified_by
        ) RETURNING cd_id INTO entity.id;

        new_entity := TRUE;
    END IF;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION create_onetime_token(IN "user_name" TEXT, IN "ip" TEXT, IN "token" TEXT, IN "expires_in" INT DEFAULT 365) RETURNS INTEGER AS
$$
  WITH i AS (
  INSERT INTO zzm_data.onetime_access_token(oat_created_by, oat_created_ip, oat_token, oat_bound_expires)
       SELECT user_name, ip, token, NOW() + expires_in * '1 day'::interval
        WHERE NOT EXISTS(SELECT 1 FROM zzm_data.onetime_access_token WHERE oat_created_by = user_name AND oat_created > now() - '15 sec'::interval)
        RETURNING oat_id AS v)
  SELECT v FROM i
  UNION ALL
       SELECT -1 WHERE EXISTS(SELECT 1 FROM zzm_data.onetime_access_token WHERE oat_created_by = user_name AND oat_created > now() - '15 sec'::interval)
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER
COST 100;

CREATE OR REPLACE FUNCTION bind_onetime_token(IN token TEXT, IN bind_ip TEXT, IN session_id TEXT,
                                                       OUT token TEXT,
                                                       OUT created TIMESTAMP,
                                                       OUT bound_at TIMESTAMP,
                                                       OUT bound_ip TEXT,
                                                       OUT bound_expires TIMESTAMP) RETURNS SETOF RECORD AS
$$
  UPDATE zzm_data.onetime_access_token
     SET oat_bound_at = now(),
         oat_bound_ip = bind_ip,
         oat_bound_session_id = session_id
   WHERE oat_token = token
     AND (oat_bound_ip is NULL OR oat_bound_ip = bind_ip)
     AND (oat_bound_session_id is NULL or oat_bound_session_id = session_id)
     AND oat_bound_expires IS NULL OR oat_bound_expires > NOW()
    RETURNING oat_token, oat_created, oat_bound_at, oat_bound_ip, oat_bound_expires;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_onetime_tokens_by_user(IN "user_name" TEXT,
                                                       OUT token TEXT,
                                                       OUT created TIMESTAMP,
                                                       OUT bound_at TIMESTAMP,
                                                       OUT bound_ip TEXT,
                                                       OUT bound_expires TIMESTAMP) RETURNS SETOF RECORD
AS
$$
  SELECT oat_token, oat_created, oat_bound_at, oat_bound_ip, oat_bound_expires
    FROM zzm_data.onetime_access_token
   WHERE oat_created_by = user_name;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;CREATE OR REPLACE FUNCTION create_or_update_dashboard (
     IN dashboard         dashboard,
     OUT entity           dashboard,
     OUT status           operation_status,
     OUT error_message    text
) AS
$BODY$
DECLARE
    l_view_mode   zzm_data.view_mode;
    l_edit_option zzm_data.edit_option;
BEGIN
    l_view_mode   = COALESCE(dashboard.view_mode, 'FULL');
    l_edit_option = COALESCE(dashboard.edit_option, 'PRIVATE');

    IF dashboard.id IS NOT NULL THEN
        UPDATE zzm_data.dashboard
           SET d_id                     = dashboard.id,
               d_name                   = dashboard.name,
               d_last_modified          = now(),
               d_last_modified_by       = dashboard.last_modified_by,
               d_widget_configuration   = dashboard.widget_configuration::json,
               d_alert_teams            = dashboard.alert_teams,
               d_view_mode              = l_view_mode,
               d_edit_option            = l_edit_option,
               -- only update shared teams when edit option is changed to team
               d_shared_teams           = CASE WHEN d_edit_option <> l_edit_option AND l_edit_option = 'TEAM'
                                               THEN dashboard.shared_teams
                                               ELSE d_shared_teams
                                          END,
               d_tags                   = dashboard.tags
         WHERE d_id  = dashboard.id
     RETURNING d_id,
               d_name,
               d_created_by,
               d_last_modified,
               d_last_modified_by,
               d_widget_configuration,
               d_alert_teams,
               d_view_mode,
               d_edit_option,
               d_shared_teams,
               d_tags
          INTO entity.id,
               entity.name,
               entity.created_by,
               entity.last_modified,
               entity.last_modified_by,
               entity.widget_configuration,
               entity.alert_teams,
               entity.view_mode,
               entity.edit_option,
               entity.shared_teams,
               entity.tags;
    ELSE
        -- if it's not there, we should create a new one
        INSERT INTO zzm_data.dashboard (
            d_name,
            d_created_by,
            d_last_modified_by,
            d_widget_configuration,
            d_alert_teams,
            d_view_mode,
            d_edit_option,
            d_shared_teams,
            d_tags
        )
        VALUES (
            dashboard.name,
            dashboard.created_by,
            dashboard.last_modified_by,
            dashboard.widget_configuration::json,
            dashboard.alert_teams,
            l_view_mode,
            l_edit_option,
            dashboard.shared_teams,
            dashboard.tags
        )
         RETURNING d_id,
               d_name,
               d_created_by,
               d_last_modified,
               d_last_modified_by,
               d_widget_configuration,
               d_alert_teams,
               d_view_mode,
               d_edit_option,
               d_shared_teams,
               d_tags
          INTO entity.id,
               entity.name,
               entity.created_by,
               entity.last_modified,
               entity.last_modified_by,
               entity.widget_configuration,
               entity.alert_teams,
               entity.view_mode,
               entity.edit_option,
               entity.shared_teams,
               entity.tags;
    END IF;

    status := 'SUCCESS';
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION delete_dashboard(
     IN dashboard_id    int
) RETURNS VOID AS
$BODY$
BEGIN
    DELETE FROM zzm_data.dashboard
          WHERE d_id = dashboard_id;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION get_all_dashboards()
 RETURNS SETOF dashboard AS
$BODY$
BEGIN

    RETURN QUERY
        SELECT d_id,
               d_name,
               d_created_by,
               d_last_modified,
               d_last_modified_by,
               d_widget_configuration::text,
               d_alert_teams,
               d_view_mode,
               d_edit_option,
               d_shared_teams,
               d_tags
          FROM zzm_data.dashboard;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_dashboards(
     IN dashboard_ids    int[]
) RETURNS SETOF dashboard AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT d_id,
               d_name,
               d_created_by,
               d_last_modified,
               d_last_modified_by,
               d_widget_configuration::text,
               d_alert_teams,
               d_view_mode,
               d_edit_option,
               d_shared_teams,
               d_tags
          FROM zzm_data.dashboard
         WHERE (d_id = ANY (dashboard_ids));
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION add_alert_comment (
     IN comment           alert_comment,
     OUT status           operation_status,
     OUT error_message    text,
     OUT entity           alert_comment
) AS
$BODY$
BEGIN
    INSERT INTO zzm_data.alert_comment (
        ac_created_by,
        ac_last_modified_by,
        ac_comment,
        ac_alert_definition_id,
        ac_entity_id
        )
      VALUES (
         comment.created_by,
         comment.last_modified_by,
         comment.comment,
         comment.alert_definition_id,
         comment.entity_id
      ) RETURNING ac_id,
                  ac_created,
                  ac_created_by,
                  ac_last_modified,
                  ac_last_modified_by,
                  ac_comment,
                  ac_alert_definition_id,
                  ac_entity_id I
             INTO entity.id,
                  entity.created,
                  entity.created_by,
                  entity.last_modified,
                  entity.last_modified_by,
                  entity.comment,
                  entity.alert_definition_id,
                  entity.entity_id;

    status := 'SUCCESS';

 -- handle foreign key violation and return an error code
EXCEPTION WHEN foreign_key_violation THEN
    status := 'ALERT_DEFINITION_NOT_FOUND';
    error_message := 'Alert definition with id ' || comment.alert_definition_id || ' not found';
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION delete_alert_comment (
     IN comment_id    int
) RETURNS alert_comment AS
$BODY$
DECLARE
    l_comment alert_comment;
BEGIN
    DELETE FROM zzm_data.alert_comment
          WHERE ac_id = comment_id
      RETURNING ac_id,
                ac_created,
                ac_created_by,
                ac_last_modified,
                ac_last_modified_by,
                ac_comment,
                ac_alert_definition_id,
                ac_entity_id
           INTO l_comment.id,
                l_comment.created,
                l_comment.created_by,
                l_comment.last_modified,
                l_comment.last_modified_by,
                l_comment.comment,
                l_comment.alert_definition_id,
                l_comment.entity_id;

    RETURN l_comment;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION delete_alert_definition (
     IN p_alert_definition_id  int,
    OUT status                 operation_status,
    OUT error_message          text,
    OUT entity                 alert_definition_type
) AS
$BODY$
DECLARE
    l_children int[];
BEGIN

         SELECT array_agg(adt_id)
           INTO l_children
           FROM zzm_data.alert_definition_tree
          WHERE adt_parent_id = p_alert_definition_id;

             IF ARRAY_LENGTH(l_children, 1) IS NOT NULL AND ARRAY_LENGTH(l_children, 1) > 0 THEN
                status := 'DELETE_NON_LEAF_ALERT_DEFINITION';
                error_message := 'Could not delete an alert definition with descendants: ' || l_children::text;
                RETURN;
            END IF;

    -- delete all comments
    DELETE FROM zzm_data.alert_comment
          WHERE ac_alert_definition_id = p_alert_definition_id;

    -- delete alert definition
    DELETE FROM zzm_data.alert_definition_tree
          WHERE adt_id = p_alert_definition_id
      RETURNING adt_id,
                adt_name,
                adt_description,
                adt_team,
                adt_responsible_team,
                adt_entities,
                adt_condition,
                adt_notifications,
                adt_check_definition_id,
                adt_status,
                adt_priority,
                adt_last_modified,
                adt_last_modified_by,
                adt_period,
                adt_template,
                adt_parent_id,
                adt_parameters
           INTO entity.id,
                entity.name,
                entity.description,
                entity.team,
                entity.responsible_team,
                entity.entities,
                entity.condition,
                entity.notifications,
                entity.check_definition_id,
                entity.status,
                entity.priority,
                entity.last_modified,
                entity.last_modified_by,
                entity.period,
                entity.template,
                entity.parent_id,
                entity.parameters;

    status := 'SUCCESS';
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION delete_check_definition (
     IN p_user_name   text,
     IN p_name        text,
     IN p_owning_team text,
    OUT p_check_definition check_definition_type
) AS
$BODY$
DECLARE
    l_updated_alerts_count int;
BEGIN

     -- set check as deleted
     UPDATE zzm_data.check_definition
        SET cd_status           = 'DELETED',
            cd_last_modified_by = p_user_name,
            cd_last_modified    = now()
      WHERE cd_name = p_name
        AND cd_owning_team = p_owning_team
  RETURNING cd_id,
            cd_name,
            cd_description,
            cd_technical_details,
            cd_potential_analysis,
            cd_potential_impact,
            cd_potential_solution,
            cd_owning_team,
            cd_entities,
            cd_interval,
            cd_command,
            cd_status,
            cd_source_url,
            cd_last_modified_by
       INTO p_check_definition;

       --  recursively set all alerts as deleted
       WITH RECURSIVE tree AS (
         SELECT adt_id
           FROM zzm_data.alert_definition_tree
          WHERE adt_check_definition_id = p_check_definition.id
      UNION ALL
         SELECT adt.adt_id
           FROM zzm_data.alert_definition_tree adt
           JOIN tree t
             ON adt.adt_parent_id = t.adt_id
       )
     UPDATE zzm_data.alert_definition_tree adt
        SET adt_status           = 'DELETED',
            adt_last_modified_by = p_user_name,
            adt_last_modified    = now()
       FROM tree t
      WHERE adt.adt_id = t.adt_id
        AND adt_status <> 'DELETED';
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

CREATE OR REPLACE FUNCTION delete_unused_check_definition(IN id INT, IN teams TEXT[]) RETURNS SETOF INT AS
$$
DELETE FROM zzm_data.check_definition
 WHERE cd_id = id
   AND cd_owning_team=ANY(teams)
   AND NOT EXISTS(SELECT 1 FROM zzm_data.alert_definition_tree WHERE adt_check_definition_id = cd_id)
 RETURNING cd_id;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
CREATE OR REPLACE FUNCTION delete_detached_check_definitions (
) RETURNS SETOF check_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
       DELETE
         FROM zzm_data.check_definition
        WHERE cd_status = 'DELETED'
          AND NOT EXISTS (SELECT 1
                            FROM zzm_data.alert_definition
                           WHERE ad_check_definition_id = cd_id)
    RETURNING cd_id,
              cd_name,
              cd_description,
              cd_technical_details,
              cd_potential_analysis,
              cd_potential_impact,
              cd_potential_solution,
              cd_owning_team,
              cd_entities,
              cd_interval,
              cd_command,
              cd_status,
              cd_source_url,
              cd_last_modified_by;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_alert_comment_by_id (
     IN comment_id int
) RETURNS alert_comment AS
$BODY$
    SELECT ac_id,
           ac_created,
           ac_created_by,
           ac_last_modified,
           ac_last_modified_by,
           ac_comment,
           ac_alert_definition_id,
           ac_entity_id
      FROM zzm_data.alert_comment
     WHERE ac_id = comment_id;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION get_alert_comments (
     IN p_alert_definition_id int,
     IN p_limit               int,
     IN p_offset              int
) RETURNS SETOF alert_comment AS
$BODY$
    SELECT ac_id,
           ac_created,
           ac_created_by,
           ac_last_modified,
           ac_last_modified_by,
           ac_comment,
           ac_alert_definition_id,
           ac_entity_id
      FROM zzm_data.alert_comment
     WHERE ac_alert_definition_id = p_alert_definition_id
  ORDER BY ac_last_modified DESC, ac_id DESC
     LIMIT p_limit
    OFFSET p_offset;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;CREATE OR REPLACE FUNCTION get_alert_definition_history (
     IN p_alert_definition_id int,
     IN p_limit               int,
     IN p_from                timestamptz,
     IN p_to                  timestamptz
) RETURNS SETOF history_entry AS
$BODY$
      WITH RECURSIVE tree(id, parent_id) AS (
    SELECT adt_id, adt_parent_id
      FROM zzm_data.alert_definition_tree
     WHERE adt_id = p_alert_definition_id
 UNION ALL
    SELECT a.adt_id, a.adt_parent_id
      FROM zzm_data.alert_definition_tree a
      JOIN tree t
        -- prevent circular references
        ON a.adt_id = t.parent_id)

    SELECT adh_id,
           adh_timestamp,
           adh_action,
           adh_row_data,
           adh_changed_fields,
           adh_user_name,
           adh_alert_definition_id,
           'ALERT_DEFINITION'::history_type
      FROM zzm_data.alert_definition_history
      JOIN tree t
        ON adh_alert_definition_id = t.id
     WHERE (p_from IS NULL OR adh_timestamp >= p_from)
       AND (p_to IS NULL OR adh_timestamp <= p_to)
  ORDER BY adh_timestamp DESC, adh_id DESC
     LIMIT p_limit;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
CREATE OR REPLACE FUNCTION get_check_definition_history (
     IN p_check_definition_id int,
     IN p_limit               int,
     IN p_from                timestamptz,
     IN p_to                  timestamptz
) RETURNS SETOF history_entry AS
$BODY$
    SELECT cdh_id,
           cdh_timestamp,
           cdh_action,
           cdh_row_data,
           cdh_changed_fields,
           cdh_user_name,
           cdh_check_definition_id,
           'CHECK_DEFINITION'::history_type
      FROM zzm_data.check_definition_history
     WHERE cdh_check_definition_id = p_check_definition_id
       AND (p_from IS NULL OR cdh_timestamp >= p_from)
       AND (p_to IS NULL OR cdh_timestamp <= p_to)
  ORDER BY cdh_timestamp DESC, cdh_id DESC
     LIMIT p_limit;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
