CREATE OR REPLACE FUNCTION create_or_update_dashboard_record (
     IN dashboard         dashboard_record,
     OUT entity           dashboard_record,
     OUT status           operation_status,
     OUT error_message    text
) AS
$BODY$
DECLARE
    l_view_mode   zzm_data.view_mode;
    l_edit_option zzm_data.edit_option;
BEGIN
    l_view_mode   = COALESCE(dashboard_record.view_mode, 'FULL');
    l_edit_option = COALESCE(dashboard_record.edit_option, 'PRIVATE');

    IF dashboard_record.id IS NOT NULL THEN
        UPDATE zzm_data.dashboard
           SET d_id                     = dashboard_record.id,
               d_name                   = dashboard_record.name,
               d_last_modified          = now(),
               d_last_modified_by       = dashboard_record.last_modified_by,
               d_widget_configuration   = dashboard_record.widget_configuration::json,
               d_alert_teams            = dashboard_record.alert_teams,
               d_view_mode              = l_view_mode,
               d_edit_option            = l_edit_option,
               -- only update shared teams when edit option is changed to team
               d_shared_teams           = CASE WHEN d_edit_option <> l_edit_option AND l_edit_option = 'TEAM'
                                               THEN dashboard_record.shared_teams
                                               ELSE d_shared_teams
                                          END,
               d_tags                   = dashboard_record.tags
         WHERE d_id  = dashboard_record.id
     RETURNING d_id,
               d_name,
               d_created_by,
               d_last_modified,
               d_last_modified_by,
               d_widget_configuration,
               d_alert_teams,
               d_view_mode,
               d_edit_option,
               d_shared_teams,
               d_tags
          INTO entity.id,
               entity.name,
               entity.created_by,
               entity.last_modified,
               entity.last_modified_by,
               entity.widget_configuration,
               entity.alert_teams,
               entity.view_mode,
               entity.edit_option,
               entity.shared_teams,
               entity.tags;
    ELSE
        -- if it's not there, we should create a new one
        INSERT INTO zzm_data.dashboard (
            d_name,
            d_created_by,
            d_last_modified_by,
            d_widget_configuration,
            d_alert_teams,
            d_view_mode,
            d_edit_option,
            d_shared_teams,
            d_tags
        )
        VALUES (
            dashboard_record.name,
            dashboard_record.created_by,
            dashboard_record.last_modified_by,
            dashboard_record.widget_configuration::json,
            dashboard_record.alert_teams,
            l_view_mode,
            l_edit_option,
            dashboard_record.shared_teams,
            dashboard_record.tags
        )
         RETURNING d_id,
               d_name,
               d_created_by,
               d_last_modified,
               d_last_modified_by,
               d_widget_configuration,
               d_alert_teams,
               d_view_mode,
               d_edit_option,
               d_shared_teams,
               d_tags
          INTO entity.id,
               entity.name,
               entity.created_by,
               entity.last_modified,
               entity.last_modified_by,
               entity.widget_configuration,
               entity.alert_teams,
               entity.view_mode,
               entity.edit_option,
               entity.shared_teams,
               entity.tags;
    END IF;

    status := 'SUCCESS';
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
