CREATE TYPE alert_comment_record AS (
    id                  int,
    created             timestamptz,
    created_by          text,
    last_modified       timestamptz,
    last_modified_by    text,
    comment             text,
    alert_definition_id int,
    entity_id           text
);

CREATE TYPE dashboard_record AS (
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

CREATE OR REPLACE FUNCTION add_alert_comment_record (
     IN comment           alert_comment_record,
     OUT status           operation_status,
     OUT error_message    text,
     OUT entity           alert_comment_record
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

CREATE OR REPLACE FUNCTION delete_alert_comment_record (
     IN comment_id    int
) RETURNS alert_comment_record AS
$BODY$
DECLARE
    l_comment alert_comment_record;
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

CREATE OR REPLACE FUNCTION get_alert_comment_record_by_id (
     IN comment_id int
) RETURNS alert_comment_record AS
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
COST 100;

CREATE OR REPLACE FUNCTION get_alert_comment_records (
     IN p_alert_definition_id int,
     IN p_limit               int,
     IN p_offset              int
) RETURNS SETOF alert_comment_record AS
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
COST 100;

CREATE OR REPLACE FUNCTION create_or_update_dashboard_record (
     IN dashboard         dashboard_record,
     OUT entity           dashboard_record,
     OUT status           operation_status,
     OUT error_message    text
) AS
$BODY$
DECLARE
    l_view_mode   zzm_data.view_mode;
    l_edit_option zzm_data.edit_option;
BEGIN
    l_view_mode   = COALESCE(dashboard_record.view_mode, 'FULL');
    l_edit_option = COALESCE(dashboard_record.edit_option, 'PRIVATE');

    IF dashboard_record.id IS NOT NULL THEN
        UPDATE zzm_data.dashboard
           SET d_id                     = dashboard_record.id,
               d_name                   = dashboard_record.name,
               d_last_modified          = now(),
               d_last_modified_by       = dashboard_record.last_modified_by,
               d_widget_configuration   = dashboard_record.widget_configuration::json,
               d_alert_teams            = dashboard_record.alert_teams,
               d_view_mode              = l_view_mode,
               d_edit_option            = l_edit_option,
               -- only update shared teams when edit option is changed to team
               d_shared_teams           = CASE WHEN d_edit_option <> l_edit_option AND l_edit_option = 'TEAM'
                                               THEN dashboard_record.shared_teams
                                               ELSE d_shared_teams
                                          END,
               d_tags                   = dashboard_record.tags
         WHERE d_id  = dashboard_record.id
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
            dashboard_record.name,
            dashboard_record.created_by,
            dashboard_record.last_modified_by,
            dashboard_record.widget_configuration::json,
            dashboard_record.alert_teams,
            l_view_mode,
            l_edit_option,
            dashboard_record.shared_teams,
            dashboard_record.tags
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

CREATE OR REPLACE FUNCTION get_all_dashboard_records()
 RETURNS SETOF dashboard_record AS
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

CREATE OR REPLACE FUNCTION get_dashboard_records(
     IN dashboard_ids    int[]
) RETURNS SETOF dashboard_record AS
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
COST 100;
