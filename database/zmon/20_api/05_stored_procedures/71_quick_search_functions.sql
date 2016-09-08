CREATE OR REPLACE FUNCTION quick_search_alerts(IN search TEXT, IN teams TEXT[], IN maxRows INT, OUT id TEXT, OUT title TEXT, OUT team TEXT) RETURNS SETOF RECORD
AS $$
 SELECT ad_id AS "id", ad_name AS "title", ad_team AS "team"
   FROM zzm_data.alert_definition
  WHERE (ad_id::text ILIKE search||'%' OR ad_name ILIKE '%'||search||'%')
    AND (teams IS NULL OR ad_team = ANY(teams))
    LIMIT maxRows
$$ LANGUAGE sql;

CREATE OR REPLACE FUNCTION quick_search_checks(IN search TEXT, IN teams TEXT[], IN maxRows INT OUT id TEXT, OUT title TEXT, OUT team TEXT) RETURNS SETOF RECORD
AS $$
 SELECT cd_id AS "id", cd_name AS "title", cd_owning_team AS "team"
   FROM zzm_data.check_definition
  WHERE (cd_id::text ILIKE search||'%' OR cd_name ILIKE '%'||search||'%')
    AND (teams IS NULL OR cd_owning_team = ANY(teams))
    LIMIT maxRows
$$ LANGUAGE sql;