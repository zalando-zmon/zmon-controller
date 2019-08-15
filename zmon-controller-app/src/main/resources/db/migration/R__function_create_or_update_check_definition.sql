CREATE OR REPLACE FUNCTION zzm_api.create_or_update_check_definition(
    IN check_definition_import check_definition_import,
    IN user_name text,
    IN user_teams text[],
    IN user_is_admin boolean,
    IN runtime_enabled boolean,
    IN runtime_default zzm_data.definition_runtime,
    OUT entity                 check_definition_type,
    OUT new_entity             boolean,
    OUT permission_denied      boolean
) AS
$BODY$
DECLARE
    new_runtime text;
    previous_runtime text;
BEGIN
    -- Check if user has permissions to create/edit the check
    permission_denied = FALSE;
    user_teams = lower(user_teams::text)::text[];
    IF check_definition_import.id IS NOT NULL AND NOT user_is_admin THEN
        IF NOT EXISTS (SELECT 1 FROM zzm_data.check_definition
                       WHERE cd_id = check_definition_import.id
                         AND (lower(cd_owning_team) = ANY(user_teams) OR cd_created_by = user_name)
                         AND (lower(check_definition_import.owning_team) = ANY(user_teams)) )
        THEN
            permission_denied = true;
            RETURN;
        END IF;
    ELSIF user_is_admin IS FALSE AND NOT lower(check_definition_import.owning_team) = ANY(user_teams) THEN
        permission_denied = true;
        RETURN;
    END IF;

    -- Fill in entity
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
    entity.runtime              = check_definition_import.runtime;

    -- Find id for the check and then lock it for update
    SELECT cd_id, COALESCE(cd_runtime::text, 'null')
    INTO entity.id, previous_runtime
    FROM zzm_data.check_definition
    WHERE (lower(cd_source_url) = lower(check_definition_import.source_url) AND check_definition_import.id IS NULL)
       OR (lower(cd_name) = lower(check_definition_import.name) AND lower(cd_owning_team) = lower(check_definition_import.owning_team) AND check_definition_import.id IS NULL)
       OR (cd_id = check_definition_import.id)
    FOR UPDATE;
    new_runtime := COALESCE(check_definition_import.runtime::text, 'null');

    IF FOUND THEN
        IF (
           -- Runtime change but runtime is globally disabled.
            NOT runtime_enabled AND
            new_runtime != previous_runtime
        ) OR (
            -- Runtime changed back to the old value and runtime is globally enabled.
            -- REMARK: Restore can be used if needed.
            runtime_enabled
            AND new_runtime != runtime_default::text
            AND previous_runtime = runtime_default::text
        ) THEN
            permission_denied := TRUE;
            RETURN;
        END IF;

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
            cd_last_modified        = now(),
            cd_runtime              = check_definition_import.runtime
        WHERE cd_id = entity.id
        RETURNING cd_id INTO entity.id;
        new_entity := FALSE;
    ELSIF NOT FOUND AND check_definition_import.id IS NULL THEN
        IF (
            -- Runtime is set to the new default but it is globally disabled
            NOT runtime_enabled
            AND new_runtime = runtime_default::text
        ) OR (
            -- Runtime is not set to the default but is globally enabled
            runtime_enabled
            AND new_runtime != runtime_default::text
        ) THEN
            permission_denied := TRUE;
            RETURN;
        END IF;

        INSERT INTO zzm_data.check_definition(
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
            cd_last_modified_by,
            cd_runtime
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
            check_definition_import.last_modified_by,
            check_definition_import.runtime
        )
        RETURNING cd_id INTO entity.id;
        new_entity := TRUE;
    END IF;
END
$BODY$
    LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
                       COST 100;