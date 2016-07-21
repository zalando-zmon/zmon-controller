CREATE OR REPLACE FUNCTION get_check_last_modified_max() RETURNS timestamptz AS
$$
 select max(cd_last_modified) from zzm_data.check_definition;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
