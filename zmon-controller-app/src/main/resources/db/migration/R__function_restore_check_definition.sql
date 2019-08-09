CREATE OR REPLACE FUNCTION zzm_api.restore_check_definition(
    IN check_definition_history_id INTEGER,
    IN user_name TEXT,
    IN user_teams TEXT[],
    IN user_is_admin BOOLEAN
) RETURNS BOOLEAN AS $$
DECLARE
    previous_check_definition HSTORE;
    current_check_definition zzm_api.check_definition_type;
    new_check_definition zzm_api.check_definition_import;

    changed_fields HSTORE;
    restored_fields HSTORE;
BEGIN
    -- Fetch check definition change
    SELECT cdh_check_definition_id, cdh_row_data, cdh_changed_fields
    INTO current_check_definition.id, previous_check_definition, changed_fields
    FROM zzm_data.check_definition_history
    WHERE cdh_id = check_definition_history_id;
    -- Return immediately if not found
    IF FOUND = FALSE THEN
        RETURN FALSE;
    END IF;

    -- Fetch current check definition for update
    SELECT *
    INTO current_check_definition
    FROM zzm_api.get_check_definitions('ACTIVE'::zzm_data.definition_status, ARRAY[current_check_definition.id])
    LIMIT 1;
    -- Build a map of check definition fields to restore based on the change
    restored_fields := slice(previous_check_definition, akeys(changed_fields)) - ARRAY[
        'cd_created',
        'cd_created_by',
        'cd_last_modified',
        'cd_last_modified_by'
    ];
    -- Update check definition
    -- NOT NULL
    new_check_definition.name = COALESCE(restored_fields->'cd_name', current_check_definition.name);
    new_check_definition.description = COALESCE(restored_fields->'cd_description', current_check_definition.description);
    new_check_definition.owning_team =  COALESCE(restored_fields->'cd_owning_team', current_check_definition.owning_team);
    new_check_definition.entities = COALESCE((restored_fields->'cd_entities')::hstore[], current_check_definition.entities);
    new_check_definition.interval = COALESCE((restored_fields->'cd_interval')::INTEGER, current_check_definition.interval);
    new_check_definition.command = COALESCE(restored_fields->'cd_command', current_check_definition.command);
    new_check_definition.source_url = COALESCE(restored_fields->'cd_source_url', current_check_definition.source_url);
    new_check_definition.status = COALESCE((restored_fields->'cd_status')::zzm_data.definition_status, current_check_definition.status);
    new_check_definition.runtime = COALESCE((restored_fields->'cd_runtime')::zzm_data.definition_runtime, current_check_definition.runtime);
    new_check_definition.last_modified_by = user_name;
    --- NULL
    new_check_definition.technical_details = (CASE WHEN restored_fields ? 'cd_technical_details' THEN restored_fields->'cd_technical_details' ELSE current_check_definition.technical_details END);
    new_check_definition.potential_analysis = (CASE WHEN restored_fields ? 'cd_potential_analysis' THEN restored_fields->'cd_potential_analysis' ELSE current_check_definition.potential_analysis END);
    new_check_definition.potential_impact = (CASE WHEN restored_fields ? 'cd_potential_impact' THEN restored_fields->'cd_potential_impact' ELSE current_check_definition.potential_impact END);
    new_check_definition.potential_solution = (CASE WHEN restored_fields ? 'cd_potential_solution' THEN restored_fields->'cd_potential_solution' ELSE current_check_definition.potential_solution END);
    PERFORM zzm_api.create_or_update_check_definition(new_check_definition, user_name, user_teams, user_is_admin);

    RETURN TRUE;
END
$$
    LANGUAGE 'plpgsql'
    VOLATILE
    SECURITY DEFINER
    COST 100;