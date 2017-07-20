CREATE OR REPLACE FUNCTION create_or_update_grafana_dashboard(id text, title text, dashboard text, user_name text, version text) RETURNS void AS
$$
BEGIN
  -- RAISE WARNING 'dashboard: % title: %', dashboard, title;

  BEGIN

    INSERT INTO zzm_data.grafana_dashboard(gd_id, gd_title, gd_dashboard, gd_grafana_version, gd_created_by, gd_last_modified_by)
         SELECT id, title, dashboard::jsonb, version, user_name, user_name;

  EXCEPTION WHEN UNIQUE_VIOLATION THEN

    UPDATE zzm_data.grafana_dashboard
       SET gd_title = title,
           gd_dashboard = dashboard::jsonb,
           gd_last_modified = now(),
           gd_last_modified_by = user_name,
           gd_grafana_version = version
     WHERE gd_id = id;

  END;
END;
$$ LANGUAGE PLPGSQL VOLATILE SECURITY DEFINER;
