CREATE OR REPLACE FUNCTION zzm_api.get_dashboard_records(
     IN dashboard_ids    int[]
) RETURNS SETOF zzm_api.dashboard_record AS
$BODY$
BEGIN
    RETURN QUERY
        SELECT d_id,
               d_name,
               d_created_by,
               d_last_modified,
               d_last_modified_by,
               d_widget_configuration::text,
               d_alert_teams,
               d_view_mode,
               d_edit_option,
               d_shared_teams,
               d_tags
          FROM zzm_data.dashboard
         WHERE (d_id = ANY (dashboard_ids));
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
