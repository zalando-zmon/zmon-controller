CREATE SCHEMA IF NOT EXISTS zzm_api;

SET search_path TO zzm_api, public;

CREATE OR REPLACE FUNCTION get_grafana_mapping (IN p_id text) RETURNS text AS
$BODY$
    SELECT uid
      FROM zzm_data.grafana_mapping
     WHERE id = p_id
     LIMIT 1;
$BODY$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;