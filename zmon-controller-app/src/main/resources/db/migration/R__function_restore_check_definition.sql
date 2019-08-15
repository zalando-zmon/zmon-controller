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

    is_permission_denied BOOLEAN;
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

    -- Build a map of check definition fields to restore based on the change
    restored_fields := (previous_check_definition || COALESCE(changed_fields, ''::hstore)) - ARRAY[
        'cd_created',
        'cd_created_by',
        'cd_last_modified',
        'cd_last_modified_by'
    ];
    -- Update check definition
    new_check_definition.name = restored_fields->'cd_name';
    new_check_definition.description = restored_fields->'cd_description';
    new_check_definition.owning_team =  restored_fields->'cd_owning_team';
    new_check_definition.entities = restored_fields->'cd_entities';
    new_check_definition.interval = restored_fields->'cd_interval';
    new_check_definition.command = restored_fields->'cd_command';
    new_check_definition.source_url = restored_fields->'cd_source_url';
    new_check_definition.status = restored_fields->'cd_status'::zzm_data.definition_status;
    new_check_definition.runtime = restored_fields->'cd_runtime'::zzm_data.definition_runtime;
    new_check_definition.last_modified_by = user_name;
    new_check_definition.technical_details = restored_fields->'cd_technical_details';
    new_check_definition.potential_analysis = restored_fields->'cd_potential_analysis';
    new_check_definition.potential_impact = restored_fields->'cd_potential_impact';
    new_check_definition.potential_solution = restored_fields->'cd_potential_solution';

    SELECT permission_denied INTO is_permission_denied
    FROM zzm_api.create_or_update_check_definition(new_check_definition, user_name, user_teams, user_is_admin, TRUE, new_check_definition.runtime);

    RETURN NOT is_permission_denied;
END
$$
    LANGUAGE 'plpgsql'
    VOLATILE
    SECURITY DEFINER
    COST 100;