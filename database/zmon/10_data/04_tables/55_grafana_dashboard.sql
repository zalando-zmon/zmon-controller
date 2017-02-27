CREATE TABLE zzm_data.grafana_dashboard
(
  gd_id text not null,
  gd_title text not null,
  gd_dashboard jsonb not null,
  gd_grafana_version text default 'v1',
  gd_starred_by text[] default '{}'::text[], -- yes not perfect but most likey more than good enough
  gd_created_by text,
  gd_created timestamp default now(),
  gd_last_modified_by text,
  gd_last_modified timestamp default now(),
  primary key ( gd_id )
);
