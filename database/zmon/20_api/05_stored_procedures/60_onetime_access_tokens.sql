CREATE OR REPLACE FUNCTION create_onetime_token(IN "user_name" TEXT, IN "ip" TEXT, IN "token" TEXT, IN "expires_in" INT DEFAULT 365) RETURNS SETOF INTEGER AS
$$
  INSERT INTO zzm_data.onetime_access_token(oat_created_by, oat_created_ip, oat_token, oat_bound_expires) VALUES(user_name, ip, token, NOW() + expires_in * '1 day'::interval) RETURNING oat_id;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER
COST 100;

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
     AND (oat_bound_session_id is NULL or oat_bound_session_id = session_id)
     AND oat_bound_expires IS NULL OR oat_bound_expires > NOW()
    RETURNING oat_token, oat_created, oat_bound_at, oat_bound_ip, oat_bound_expires;
$$
LANGUAGE 'sql' VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_onetime_tokens_by_user(IN "user_name" TEXT,
                                                       OUT token TEXT,
                                                       OUT created TIMESTAMP,
                                                       OUT bound_at TIMESTAMP,
                                                       OUT bound_ip TEXT,
                                                       OUT bound_expires TIMESTAMP) RETURNS SETOF RECORD
AS
$$
  SELECT oat_token, oat_created, oat_bound_at, oat_bound_ip, oat_bound_expires
    FROM zzm_data.onetime_access_token
   WHERE oat_created_by = user_name;
$$ LANGUAGE 'sql' VOLATILE SECURITY DEFINER;