CREATE OR REPLACE FUNCTION get_entities(filter text) RETURNS SETOF jsonb AS
$$
 SELECT e_data || ('{"last_modified": "' || e_last_modified::text || '"}')::jsonb  FROM zzm_data.entity WHERE e_data @> ANY ( ARRAY( SELECT jsonb_array_elements(filter::jsonb) ) )
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
