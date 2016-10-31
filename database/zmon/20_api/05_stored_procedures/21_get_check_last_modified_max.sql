CREATE OR REPLACE FUNCTION get_check_last_modified_max() RETURNS timestamptz AS
$$
 select coalesce(max(cd_last_modified), '2000-01-01'::timestamp) from zzm_data.check_definition;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
