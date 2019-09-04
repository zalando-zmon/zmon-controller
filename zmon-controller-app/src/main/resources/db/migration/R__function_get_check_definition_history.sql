CREATE OR REPLACE FUNCTION zzm_api.get_check_definition_history (
    IN p_check_definition_id int,
    IN p_limit               int,
    IN p_from                timestamptz,
    IN p_to                  timestamptz,
    IN p_history_action      zzm_data.history_action
) RETURNS SETOF zzm_api.history_entry AS
$BODY$
SELECT cdh_id,
       cdh_timestamp,
       cdh_action,
       cdh_row_data,
       cdh_changed_fields,
       cdh_user_name,
       cdh_check_definition_id,
       'CHECK_DEFINITION'::zzm_api.history_type
FROM zzm_data.check_definition_history
WHERE cdh_check_definition_id = p_check_definition_id
  AND (p_from IS NULL OR cdh_timestamp >= p_from)
  AND (p_to IS NULL OR cdh_timestamp <= p_to)
  AND (p_history_action IS NULL OR cdh_action = p_history_action::zzm_data.history_action)
ORDER BY cdh_timestamp DESC, cdh_id DESC
LIMIT p_limit;
$BODY$
    LANGUAGE SQL VOLATILE SECURITY DEFINER
                 COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION zzm_api.get_check_definition_history (
    IN p_check_definition_id int,
    IN p_limit               int,
    IN p_from                timestamptz,
    IN p_to                  timestamptz,
    IN p_history_action      text
) RETURNS SETOF zzm_api.history_entry AS
$BODY$
    SELECT * FROM zzm_api.get_check_definition_history(
        p_check_definition_id,
        p_limit,
        p_from,
        p_to,
        p_history_action::zzm_data.history_action
    );
$BODY$
    LANGUAGE SQL VOLATILE SECURITY DEFINER
                 COST 100;