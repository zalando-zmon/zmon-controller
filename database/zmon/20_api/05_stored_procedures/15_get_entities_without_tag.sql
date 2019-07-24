CREATE OR REPLACE FUNCTION get_entities_without_tag(filter text) RETURNS SETOF jsonb AS
$$
 SELECT e_data || ('{"last_modified": "' || e_last_modified::text || '"}')::jsonb  FROM zzm_data.entity WHERE NOT e_data ? filter
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
