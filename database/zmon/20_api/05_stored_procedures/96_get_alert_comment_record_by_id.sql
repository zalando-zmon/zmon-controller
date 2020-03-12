CREATE OR REPLACE FUNCTION zzm_api.get_alert_comment_record_by_id (
     IN comment_id int
) RETURNS zzm_api.alert_comment_record AS
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
