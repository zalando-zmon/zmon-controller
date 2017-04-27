CREATE OR REPLACE FUNCTION get_alert_definitions_by_team_and_tag(
     IN status              zzm_data.definition_status,
     IN teams               text[],
     IN tags                text[]
) RETURNS SETOF alert_definition_type AS
$BODY$
DECLARE
  include_tags text[];
  exclude_tags text[];
BEGIN
    include_tags := ARRAY(SELECT t FROM unnest(tags) t(t) WHERE t not like '!%');
    exclude_tags := ARRAY(SELECT substring(t from 2) FROM unnest(tags) t(t) WHERE t like '!%');
    teams := ARRAY(SELECT lower(t) FROM unnest(teams) t(t));

    -- RAISE WARNING 'include: % % exclude: % %', include_tags, include_tags = array[]::text[], exclude_tags, exclude_tags = array[]::text[];

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
          FROM zzm_data.materialized_alert_definitions
         WHERE (status IS NULL OR ad_status = status)
           AND (teams IS NULL OR lower(ad_team) LIKE ANY (teams))
           AND (array[]::text[] = include_tags OR include_tags && ad_tags)
           AND (exclude_tags && ad_tags) IS DISTINCT FROM TRUE;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
