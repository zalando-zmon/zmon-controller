CREATE OR REPLACE FUNCTION get_entities(filter text[]) RETURNS SETOF jsonb AS
$$
 SELECT e_data FROM zzm_data.entity WHERE e_data @> ANY filter::jsonb[]
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
