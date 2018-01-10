CREATE OR REPLACE FUNCTION get_entity_by_id(id text) RETURNS SETOF jsonb AS
$$
  SELECT e_data  || ('{"last_modified": "' || e_last_modified::text || '", "created": "' || e_created::text || '"}')::jsonb FROM zzm_data.entity WHERE (e_data->'id')::text = '"'||id||'"';
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;
