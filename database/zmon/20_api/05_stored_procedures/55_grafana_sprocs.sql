CREATE OR REPLACE FUNCTION create_or_update_grafana_dashboard(id text, title text, dashboard text, user_name text, version text) RETURNS void AS
$$
BEGIN
  RAISE WARNING 'dashboard: % title: %', dashboard, title;

  BEGIN

    INSERT INTO zzm_data.grafana_dashboard(gd_id, gd_title, gd_dashboard, gd_grafana_version, gd_created_by, gd_last_modified_by)
         SELECT id, title, dashboard::jsonb, version, user_name, user_name;

  EXCEPTION WHEN UNIQUE_VIOLATION THEN

    UPDATE zzm_data.grafana_dashboard
       SET gd_title = title,
           gd_dashboard = dashboard::jsonb,
           gd_last_modified = now(),
           gd_last_modified_by = user_name
     WHERE gd_id = id;

  END;
END;
$$ LANGUAGE PLPGSQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_grafana_dashboards(IN s_title TEXT, IN s_tags TEXT, IN s_starred TEXT, OUT id TEXT, OUT title TEXT, OUT dashboard TEXT, OUT "user" TEXT, OUT "tags" TEXT) RETURNS SETOF record AS
$$
  SELECT gd_id id, gd_title title, gd_dashboard::text dashboard, gd_created_by "user", (gd_dashboard->'tags')::text "tags"
    FROM zzm_data.grafana_dashboard
   WHERE gd_title ilike '%' || s_title || '%'
     AND (s_tags IS NULL OR (gd_dashboard->'tags') @> s_tags::jsonb)
     AND (s_starred IS NULL OR s_starred =ANY(gd_starred_by))
   ORDER BY gd_title ASC;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_grafana_dashboard(INOUT id text, OUT title text, OUT dashboard text, OUT "user" TEXT) RETURNS SETOF record AS
$$
  SELECT gd_id id, gd_title title, gd_dashboard::text dashboard, gd_created_by "user"
    FROM zzm_data.grafana_dashboard
   WHERE gd_id = id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION delete_grafana_dashboard(INOUT id text, IN user_name TEXT) RETURNS SETOF text AS
$$
  DELETE FROM zzm_data.grafana_dashboard
   WHERE gd_id = id
     AND gd_created_by = user_name
     RETURNING gd_id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_tags_with_count(OUT tag TEXT, OUT "count" INT) RETURNS SETOF RECORD AS
$$
select tag_name, count(1)::INT AS "count" from
          (select json_array_elements_text((gd_dashboard->'tags')::json) AS tag_name
             from zzm_data.grafana_dashboard) t
            group by 1;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION star_grafana_dashboard(INOUT id TEXT, IN user_name TEXT) RETURNS SETOF TEXT AS
$$
update zzm_data.grafana_dashboard
   set gd_starred_by = gd_starred_by || user_name
 where gd_id = id
   and not user_name =ANY(gd_starred_by)
 returning gd_id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION unstar_grafana_dashboard(INOUT id TEXT, IN user_name TEXT) RETURNS SETOF TEXT AS
$$
update zzm_data.grafana_dashboard
   set gd_starred_by = array_remove(gd_starred_by, user_name)
 where gd_id = id
   and user_name =ANY(gd_starred_by)
 returning gd_id;
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;