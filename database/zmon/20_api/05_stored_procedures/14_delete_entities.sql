CREATE OR REPLACE FUNCTION delete_entity(id text, teams text[]) RETURNS SETOF text AS
$$
 DELETE FROM zzm_data.entity WHERE (((e_data -> 'id'::text)::text)) = '"'||id||'"' and replace((((e_data -> 'team'::text)::text)),'"','') = any(teams) RETURNING (((e_data -> 'id'::text)::text));
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;