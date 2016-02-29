CREATE OR REPLACE FUNCTION create_onetime_token(IN "user_name" TEXT, IN "ip" TEXT, IN "token" TEXT) RETURNS SETOF INT AS
$$
  INSERT INTO zzm_data.onetime_access_token(oat_created_by, oat_created_ip, oat_token) VALUES(user_name, ip, token) RETURNING oat_id;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER
COST 100;

CREATE OR REPLACE FUNCTION bind_onetime_token(IN token TEXT, IN bind_ip TEXT, IN session_id TEXT) RETURNS SETOF INT AS
$$
  UPDATE zzm_data.onetime_access_token
     SET oat_bound_at = now(),
         oat_bound_ip = bind_ip,
         oat_bound_session_id = session_id
   WHERE oat_token = token
     AND (oat_bound_ip is NULL OR oat_bound_ip = bind_ip)
     AND (oat_bound_session_id is NULL or oat_bound_session_id = session_id)
    RETURNING oat_id;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
