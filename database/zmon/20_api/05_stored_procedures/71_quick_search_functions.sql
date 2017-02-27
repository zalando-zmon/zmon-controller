CREATE OR REPLACE FUNCTION quick_search_alerts(IN search TEXT, IN teams TEXT[], IN maxRows INT, OUT id TEXT, OUT title TEXT, OUT team TEXT) RETURNS SETOF record
AS $$
 SELECT ad_id::text AS "id", ad_name AS "title", ad_team AS "team"
   FROM zzm_data.alert_definition
  WHERE (ad_id::text ILIKE search||'%' OR ad_name ILIKE '%'||search||'%')
    AND (teams IS NULL OR ad_team = ANY(teams))
    LIMIT maxRows
$$ LANGUAGE sql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION quick_search_checks(IN search TEXT, IN teams TEXT[], IN maxRows INT, OUT id TEXT, OUT title TEXT, OUT team TEXT) RETURNS SETOF record
AS $$
 SELECT cd_id::text AS "id", cd_name AS "title", cd_owning_team AS "team"
   FROM zzm_data.check_definition
  WHERE (cd_id::text ILIKE search||'%' OR cd_name ILIKE '%'||search||'%')
    AND (teams IS NULL OR cd_owning_team = ANY(teams))
    LIMIT maxRows
$$ LANGUAGE sql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION quick_search_grafana_dashboards(IN search TEXT, IN teams TEXT[], IN maxRows INT, OUT id TEXT, OUT title TEXT, OUT team TEXT) RETURNS SETOF record
AS $$
SELECT gd_id::text AS "id", gd_title::text AS "title", ''::text AS "team"
  FROM zzm_data.grafana_dashboard
 WHERE gd_title ilike '%' || search || '%'
   AND (teams IS NULL OR (gd_dashboard->'tags') @> to_jsonb(teams))
 ORDER BY gd_title ASC
 LIMIT maxRows
$$ LANGUAGE sql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION quick_search_dashboards(IN search TEXT, IN teams TEXT[], IN maxRows INT, OUT id TEXT, OUT title TEXT, OUT team TEXT) RETURNS SETOF record
AS $$
SELECT d_id::text AS "id", d_name AS "title", ''::text AS "team"
  FROM zzm_data.dashboard
 WHERE d_id::text ilike search || '%'
    OR d_name ilike '%' || search || '%'
ORDER BY d_name ASC
LIMIT maxRows
$$ LANGUAGE sql SECURITY DEFINER;
