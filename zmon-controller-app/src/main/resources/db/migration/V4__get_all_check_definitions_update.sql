-- update to fix return of null if there are no checks at all.

CREATE OR REPLACE FUNCTION get_all_check_definitions(
     IN status              zzm_data.definition_status,
    OUT snapshot_id         text,
    OUT check_definitions   check_definition_type[]
) AS
$BODY$
BEGIN
    SELECT MAX(cdh_id)
      INTO snapshot_id
      FROM zzm_data.check_definition_history;

    SELECT COALESCE(array_agg((
           cd_id,
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
           cd_last_modified_by,
           cd_last_modified)::check_definition_type), '{}'::check_definition_type[])
      INTO check_definitions
      FROM zzm_data.check_definition
     WHERE (status IS NULL OR cd_status = status);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION get_all_check_definitions(
     IN status              text,
    OUT snapshot_id         text,
    OUT check_definitions   check_definition_type[]
) AS
$BODY$
BEGIN
        SELECT MAX(cdh_id)
          INTO snapshot_id
          FROM zzm_data.check_definition_history;

        SELECT COALESCE(array_agg((
               cd_id,
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
               cd_last_modified_by,
               cd_last_modified)::check_definition_type), '{}'::check_definition_type[])
          INTO check_definitions
          FROM zzm_data.check_definition
         WHERE (status IS NULL OR cd_status = status::zzm_data.definition_status);
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
