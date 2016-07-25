CREATE OR REPLACE FUNCTION get_alert_last_modified_max() RETURNS timestamptz AS
$$
 select max(adt_last_modified) from zzm_data.alert_definition_tree;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
