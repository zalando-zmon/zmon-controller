CREATE TABLE zzm_data.onetime_access_tokens (
  oat_id serial not null,
  oat_token text not null,
  oat_valid_until timestamp not null default now() + '1 hours'::interval, -- until when token can be used to sign in
  oat_bound_ip text, -- IP device using token
  oat_bound_at timestamp, -- when token was used
  oat_bound_session_id text, -- frontend session id/cookie id if session is bound
  oat_bound_expires timestamp not null default now() + '365 days'::interval,
  oat_created_from text not null, -- IP
  oat_created_by text not null, -- USER requesting a token
  oat_created timestamp not null default now(),
);