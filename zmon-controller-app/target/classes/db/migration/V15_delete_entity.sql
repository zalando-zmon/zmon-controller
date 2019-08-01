SET SEARCH_PATH TO zzm_api;

CREATE OR REPLACE FUNCTION delete_entity(id text, teams text[], user_name text) RETURNS SETOF text AS
$$
 DELETE FROM zzm_data.entity
  WHERE (((e_data -> 'id'::text)::text)) = '"'||id||'"'
    AND (
        (e_data->'team'::text)::text IS NULL
        OR REPLACE((e_data -> 'team'::text)::text, '"', '') = ANY(teams)
        OR e_created_by = user_name
    )
  RETURNING (( (e_data || ('{"created":' || EXTRACT(EPOCH FROM e_created) || '}')::jsonb)::text))
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
