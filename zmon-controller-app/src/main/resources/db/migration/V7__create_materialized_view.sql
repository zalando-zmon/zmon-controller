CREATE MATERIALIZED VIEW zzm_data.materialized_alert_definitions
AS SELECT
    ad_id,
    ad_created,
    ad_created_by,
    ad_last_modified,
    ad_last_modified_by,
    ad_template,
    ad_parent_id,
    ad_name,
    ad_description,
    ad_team,
    ad_responsible_team,
    ad_entities,
    ad_condition,
    ad_notifications,
    ad_status,
    ad_priority,
    ad_period,
    ad_check_definition_id,
    ad_parameters,
    ad_tags,
    ad_entities_exclude
FROM zzm_data.alert_definition;

CREATE INDEX ON zzm_data.materialized_alert_definitions(ad_team varchar_pattern_ops);