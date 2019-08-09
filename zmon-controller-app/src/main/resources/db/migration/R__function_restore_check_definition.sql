CREATE OR REPLACE FUNCTION zzm_api.restore_check_definition(
    IN change_id INTEGER
) RETURNS BOOLEAN AS $$
DECLARE
    check_definition_id INTEGER;
    previous_check_definition HSTORE;
    changed_fields HSTORE;
    restored_fields HSTORE;
BEGIN
    SELECT cdh_check_definition_id, cdh_row_data, cdh_changed_fields
    INTO check_definition_id, previous_check_definition, changed_fields
    FROM zzm_data.check_definition_history
    WHERE cdh_id = change_id;

    IF FOUND = FALSE THEN
        RETURN FALSE;
    END IF;

    restored_fields := slice(previous_check_definition, akeys(changed_fields)) - array[
        'cd_created',
        'cd_created_by',
        'cd_last_modified',
        'cd_last_modified_by'
    ];

    UPDATE zzm_data.check_definition
    SET --- NOT NULL
        cd_name = coalesce(restored_fields->'cd_name', cd_name),
        cd_description = coalesce(restored_fields->'cd_description', cd_description),
        cd_owning_team = coalesce(restored_fields->'cd_owning_team', cd_owning_team),
        cd_entities = coalesce((restored_fields->'cd_entities')::hstore[], cd_entities),
        cd_interval = coalesce((restored_fields->'cd_interval')::INTEGER, cd_interval),
        cd_command = coalesce(restored_fields->'cd_command', cd_command),
        cd_source_url = coalesce(restored_fields->'cd_source_url', cd_source_url),
        cd_status = coalesce((restored_fields->'cd_status')::zzm_data.definition_status, cd_status),
        cd_runtime = coalesce((restored_fields->'cd_runtime')::zzm_data.definition_runtime, cd_runtime),
        --- NULL
        cd_technical_details = (CASE WHEN restored_fields ? 'cd_technical_details' THEN restored_fields->'cd_technical_details' ELSE cd_technical_details END),
        cd_potential_analysis = (CASE WHEN restored_fields ? 'cd_potential_analysis' THEN restored_fields->'cd_potential_analysis' ELSE cd_potential_analysis END),
        cd_potential_impact = (CASE WHEN restored_fields ? 'cd_potential_impact' THEN restored_fields->'cd_potential_impact' ELSE cd_potential_impact END),
        cd_potential_solution = (CASE WHEN restored_fields ? 'cd_potential_solution' THEN restored_fields->'cd_potential_solution' ELSE cd_potential_solution END)
    WHERE cd_id = check_definition_id;

    RETURN FOUND;
END
$$
    LANGUAGE 'plpgsql'
    VOLATILE
    SECURITY DEFINER
    COST 100;