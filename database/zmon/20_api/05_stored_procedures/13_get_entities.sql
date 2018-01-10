CREATE OR REPLACE FUNCTION get_entities(filter text) RETURNS SETOF jsonb AS
$$
 SELECT e_data || ('{"last_modified": "' || e_last_modified::text || '"created": ' || e_created::text || '"}')::jsonb  FROM zzm_data.entity WHERE e_data @> ANY ( ARRAY( SELECT jsonb_array_elements(filter::jsonb) ) )
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
