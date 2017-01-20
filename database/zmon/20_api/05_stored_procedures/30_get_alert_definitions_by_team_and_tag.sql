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
           AND (include_tags IS NULL OR array[]::text[] = include_tags OR include_tags && ad_tags)
           AND NOT (exclude_tags IS NOT NULL AND exclude_tags && ad_tags);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
