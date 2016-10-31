CREATE OR REPLACE FUNCTION get_alert_last_modified_max() RETURNS timestamptz AS
$$
 select coalesce(max(adt_last_modified), '2000-01-01'::timestamp) from zzm_data.alert_definition_tree;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
