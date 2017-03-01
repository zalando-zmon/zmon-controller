-- inject the fixed onetime token function via flyway to make sure everyone uses most current version

SET search_path TO zzm_api, public;

CREATE OR REPLACE FUNCTION bind_onetime_token(IN token TEXT, IN bind_ip TEXT, IN session_id TEXT,
                                                       OUT token TEXT,
                                                       OUT created TIMESTAMP,
                                                       OUT bound_at TIMESTAMP,
                                                       OUT bound_ip TEXT,
                                                       OUT bound_expires TIMESTAMP) RETURNS SETOF RECORD AS
$$
  UPDATE zzm_data.onetime_access_token
     SET oat_bound_at = now(),
         oat_bound_ip = bind_ip,
         oat_bound_session_id = session_id
   WHERE oat_token = token
     AND (oat_bound_ip is NULL OR oat_bound_ip = bind_ip)
     AND (oat_bound_session_id is NULL OR oat_bound_session_id = session_id)
     -- check initial token validity for using the token to create session
     AND (oat_valid_until > now() OR oat_bound_session_id = session_id)
     -- check that bound session is not expired yet
     AND (oat_bound_expires > now())
    RETURNING oat_token, oat_created, oat_bound_at, oat_bound_ip, oat_bound_expires;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER;
