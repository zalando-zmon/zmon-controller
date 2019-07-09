CREATE SCHEMA IF NOT EXISTS zzm_data;

SET search_path TO zzm_data, public;

CREATE TABLE zzm_data.grafana_mapping (
  id text not null, -- id in Grafana 3
  uid text not null, -- uid in Grafana 6
  PRIMARY KEY(id)
);