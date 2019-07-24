CREATE OR REPLACE FUNCTION zzm_api.get_check_definitions_by_owning_team(
    IN status       zzm_data.definition_status,
    IN owning_teams text[]
) RETURNS SETOF check_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT cd_id,
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
               cd_last_modified,
               cd_runtime
        FROM zzm_data.check_definition
        WHERE (status IS NULL OR cd_status = status)
          AND cd_owning_team ILIKE ANY (owning_teams);
END
$BODY$
    LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
                       COST 100;

-- HACK: create same sproc with different signature to work around JDBC/SProcWrapper problem/bug
CREATE OR REPLACE FUNCTION zzm_api.get_check_definitions_by_owning_team(
    IN status              text,
    IN owning_teams        text[]
) RETURNS SETOF check_definition_type AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT *
        FROM get_check_definitions_by_owning_team(status::zzm_data.definition_status, owning_teams);
END
$BODY$
    LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
                       COST 100;