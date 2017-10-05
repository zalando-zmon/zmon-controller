SET SEARCH_PATH TO zzm_api;

DROP FUNCTION delete_unused_check(INT);

CREATE OR REPLACE FUNCTION delete_unused_check_definition(IN id INT) RETURNS SETOF INT AS
$$
DELETE FROM zzm_data.check_definition
 WHERE cd_id = id
   AND NOT EXISTS(SELECT 1 FROM zzm_data.alert_definition_tree WHERE adt_check_definition_id = cd_id)
 RETURNING cd_id;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER;