CREATE SCHEMA IF NOT EXISTS zzm_data;

SET search_path TO zzm_data, public;

CREATE TYPE zzm_data.definition_status AS ENUM
(
    'ACTIVE',
    'INACTIVE',
    'REJECTED',
    'DELETED'
);

CREATE TYPE zzm_data.history_action AS ENUM
(
    'INSERT',
    'DELETE',
    'UPDATE'
);

CREATE TYPE zzm_data.edit_option AS ENUM
(
    'PUBLIC',
    'TEAM',
    'PRIVATE'
);

CREATE TYPE zzm_data.view_mode AS ENUM
(
    'COMPACT',
    'FULL'
);

CREATE TABLE zzm_data.check_definition (
    cd_id                          serial          NOT NULL    PRIMARY KEY,
    cd_created                     timestamptz     NOT NULL    DEFAULT now(),
    cd_created_by                  text            NOT NULL,
    cd_last_modified               timestamptz     NOT NULL    DEFAULT now(),
    cd_last_modified_by            text            NOT NULL,
    cd_name                        varchar(256)    NOT NULL    CHECK ( cd_name = btrim(cd_name) ),
    cd_description                 text            NOT NULL,
    cd_technical_details           text            NULL        DEFAULT '',
    cd_potential_analysis          text            NULL        DEFAULT '',
    cd_potential_impact            text            NULL        DEFAULT '',
    cd_potential_solution          text            NULL        DEFAULT '',
    cd_owning_team                 varchar(256)    NOT NULL    CHECK ( cd_owning_team = btrim(cd_owning_team) ),
    cd_entities                    hstore[]        NOT NULL    CHECK ( array_length(cd_entities, 1) > 0 ),
    cd_interval                    int             NOT NULL,
    cd_command                     text            NOT NULL,
    cd_source_url                  text            NULL,
    cd_status                      zzm_data.definition_status NOT NULL     DEFAULT 'ACTIVE'
);

CREATE UNIQUE INDEX ON zzm_data.check_definition (lower(cd_name), lower(cd_owning_team));
CREATE UNIQUE INDEX ON zzm_data.check_definition (lower(cd_source_url));

CREATE TABLE zzm_data.entity (
    e_data jsonb not null,
    e_created timestamp default now() not null,
    e_created_by text not null,
    e_last_modified timestamp default now() not null,
    e_last_modified_by text not null,
    CONSTRAINT proper_id CHECK ((e_data->'id')::text is not null and ((e_data->'id')::text ~ '^"[a-z][a-z0-9@._\-\:\[\]]+"$')),
    CONSTRAINT proper_type CHECK ((e_data->'type')::text is not null and ((e_data->'type')::text ~ '^"[a-z][a-z0-9_\-]+"$'))
);

CREATE UNIQUE INDEX ON zzm_data.entity (CAST((e_data->'id') AS text));
CREATE INDEX ON zzm_data.entity USING gin (e_data);

CREATE TABLE zzm_data.alert_definition_tree   (
    adt_id                          serial                       NOT NULL    PRIMARY KEY,
    adt_created                     timestamptz                  NOT NULL    DEFAULT now(),
    adt_created_by                  text                         NOT NULL,
    adt_last_modified               timestamptz                  NOT NULL    DEFAULT now(),
    adt_last_modified_by            text                         NOT NULL,
    adt_template                    boolean                      NOT NULL    DEFAULT FALSE,
    adt_team                        varchar(256)                 NOT NULL    CHECK ( adt_team = btrim(adt_team) ),
    adt_responsible_team            varchar(256)                 NOT NULL    CHECK ( adt_responsible_team = btrim(adt_responsible_team) ),
    adt_status                      zzm_data.definition_status   NOT NULL,
    adt_parent_id                   int                          NULL,
    adt_name                        varchar(256)                 NULL        CHECK ( adt_name = btrim(adt_name) ),
    adt_description                 text                         NULL,
    adt_entities                    hstore[]                     NULL,
    adt_entities_exclude            hstore[]                     NULL,
    adt_condition                   text                         NULL,
    adt_notifications               text[]                       NULL,
    adt_priority                    int                          NULL        CHECK ( adt_priority >= 1 AND adt_priority <= 3 ),
    adt_period                      text                         NULL,
    adt_check_definition_id         int                          NULL        REFERENCES zzm_data.check_definition (cd_id),
    adt_parameters                  hstore                       NULL,
    adt_tags                        text[]                       NULL
);

CREATE INDEX ON zzm_data.alert_definition_tree(adt_parent_id);
CREATE INDEX ON zzm_data.alert_definition_tree(adt_check_definition_id);

COMMENT ON COLUMN zzm_data.alert_definition_tree.adt_team IS 'The team that should see the alert';
COMMENT ON COLUMN zzm_data.alert_definition_tree.adt_responsible_team IS 'The team that should fix the alert when triggered';
CREATE TABLE zzm_data.check_definition_history (
    cdh_id                  bigserial               NOT NULL PRIMARY KEY,
    cdh_timestamp           timestamptz             NOT NULL DEFAULT now(),
    cdh_action              zzm_data.history_action NOT NULL,
    cdh_row_data            hstore                  NOT NULL,
    cdh_changed_fields      hstore                  NULL,
    cdh_user_name           text                    NOT NULL,
    cdh_query               text                    NOT NULL,
    cdh_check_definition_id int                     NOT NULL
);

CREATE TABLE zzm_data.alert_definition_history (
    adh_id                  bigserial               NOT NULL PRIMARY KEY,
    adh_timestamp           timestamptz             NOT NULL DEFAULT now(),
    adh_action              zzm_data.history_action NOT NULL,
    adh_row_data            hstore                  NOT NULL,
    adh_changed_fields      hstore                  NULL,
    adh_user_name           text                    NOT NULL,
    adh_query               text                    NOT NULL,
    adh_alert_definition_id int                     NOT NULL
);

CREATE TABLE zzm_data.dashboard (
    d_id                      serial                NOT NULL    PRIMARY KEY,
    d_name                    text                  NOT NULL,
    d_created                 timestamptz           NOT NULL    DEFAULT now(),
    d_created_by              text                  NOT NULL,
    d_last_modified           timestamptz           NOT NULL    DEFAULT now(),
    d_last_modified_by        text                  NOT NULL,
    d_widget_configuration    json                  NOT NULL,
    d_alert_teams             text[]                NULL    ,
    d_view_mode               zzm_data.view_mode    NOT NULL    DEFAULT 'FULL',
    d_edit_option             zzm_data.edit_option  NOT NULL    DEFAULT 'PRIVATE',
    d_shared_teams            text[]                NOT NULL    DEFAULT '{}',
    d_tags                    text[]                NULL
);

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

CREATE TABLE zzm_data.alert_comment (
    ac_id                          serial          NOT NULL    PRIMARY KEY,
    ac_created                     timestamptz     NOT NULL    DEFAULT now(),
    ac_created_by                  text            NOT NULL,
    ac_last_modified               timestamptz     NOT NULL    DEFAULT now(),
    ac_last_modified_by            text            NOT NULL,
    ac_comment                     text            NOT NULL,
    ac_alert_definition_id         int             NOT NULL,
    ac_entity_id                   text            NULL        CHECK (ac_entity_id ~'^[a-zA-Z0-9:_-]+$') ,
    FOREIGN KEY (ac_alert_definition_id) REFERENCES zzm_data.alert_definition_tree (adt_id)
);

CREATE TABLE zzm_data.onetime_access_token (
  oat_id serial not null,
  oat_token text not null,
  oat_valid_until timestamp not null default now() + '1 hours'::interval, -- until when token can be used to sign in
  oat_bound_ip text, -- IP device using token
  oat_bound_at timestamp, -- when token was used
  oat_bound_session_id text, -- frontend session id/cookie id if session is bound
  oat_bound_expires timestamp not null default now() + '365 days'::interval,
  oat_created_ip text not null, -- IP
  oat_created_by text not null, -- USER requesting a token
  oat_created timestamp not null default now(),
  PRIMARY KEY(oat_id)
);

CREATE OR REPLACE FUNCTION zzm_data.create_check_definition_history_trigger() RETURNS trigger AS
$BODY$
DECLARE
    l_check_definition_id   int;
    l_history_action        zzm_data.history_action;
    l_row_data              hstore;
    l_changed_fields        hstore;
BEGIN
    IF (TG_OP = 'INSERT') THEN
        l_check_definition_id = NEW.cd_id;
        l_history_action = 'INSERT';
        l_row_data = hstore(NEW.*);
    ELSE
        l_check_definition_id = OLD.cd_id;
        l_row_data = hstore(OLD.*);

        IF (TG_OP = 'UPDATE') THEN
            l_history_action = 'UPDATE';
            l_changed_fields = hstore(NEW.*) - l_row_data;
        ELSE
            l_history_action = 'DELETE';
        END IF;
    END IF;

    INSERT INTO zzm_data.check_definition_history (
                    cdh_action,
                    cdh_row_data,
                    cdh_changed_fields,
                    cdh_user_name,
                    cdh_query,
                    cdh_check_definition_id
                )
         VALUES (
                    l_history_action,
                    l_row_data,
                    l_changed_fields,
                    session_user::text,
                    current_query(),
                    l_check_definition_id
                );

    RETURN NULL;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

CREATE OR REPLACE FUNCTION zzm_data.create_alert_definition_tree_history_trigger() RETURNS trigger AS
$BODY$
DECLARE
    l_alert_definition_id   int;
    l_history_action        zzm_data.history_action;
    l_row_data              hstore;
    l_changed_fields        hstore;
BEGIN
    IF (TG_OP = 'INSERT') THEN
        l_alert_definition_id = NEW.adt_id;
        l_history_action = 'INSERT';
        l_row_data = hstore(NEW.*);
    ELSE
        l_alert_definition_id = OLD.adt_id;
        l_row_data = hstore(OLD.*);

        IF (TG_OP = 'UPDATE') THEN
            l_history_action = 'UPDATE';
            l_changed_fields = hstore(NEW.*) - l_row_data;
        ELSE
            l_history_action = 'DELETE';
        END IF;
    END IF;

    INSERT INTO zzm_data.alert_definition_history (
                    adh_action,
                    adh_row_data,
                    adh_changed_fields,
                    adh_user_name,
                    adh_query,
                    adh_alert_definition_id
                )
         VALUES (
                    l_history_action,
                    l_row_data,
                    l_changed_fields,
                    session_user::text,
                    current_query(),
                    l_alert_definition_id
                );

    RETURN NULL;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;

CREATE TRIGGER check_definition_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON zzm_data.check_definition
    FOR EACH ROW EXECUTE PROCEDURE zzm_data.create_check_definition_history_trigger();

CREATE TRIGGER alert_definition_tree_history_trigger
AFTER INSERT OR UPDATE OR DELETE ON zzm_data.alert_definition_tree
    FOR EACH ROW EXECUTE PROCEDURE zzm_data.create_alert_definition_tree_history_trigger();

CREATE OR REPLACE RECURSIVE VIEW zzm_data.alert_definition (
    ad_id,
    ad_created,
    ad_created_by,
    ad_last_modified,
    ad_last_modified_by,
    ad_template,
    ad_parent_id,
    ad_name,
    ad_description,
    ad_team,
    ad_responsible_team,
    ad_entities,
    ad_condition,
    ad_notifications,
    ad_status,
    ad_priority,
    ad_period,
    ad_check_definition_id,
    ad_parameters,
    ad_tags,
    ad_entities_exclude
) AS
   SELECT adt_id,
          adt_created,
          adt_created_by,
          adt_last_modified,
          adt_last_modified_by,
          adt_template,
          adt_parent_id,
          adt_name,
          adt_description,
          adt_team,
          adt_responsible_team,
          adt_entities,
          adt_condition,
          adt_notifications,
          adt_status,
          adt_priority,
          adt_period,
          adt_check_definition_id,
          adt_parameters,
          adt_tags,
          adt_entities_exclude
     FROM zzm_data.alert_definition_tree
    WHERE adt_parent_id IS NULL
UNION ALL
   SELECT adt_id,
          adt_created,
          adt_created_by,
          adt_last_modified,
          adt_last_modified_by,
          adt_template,
          adt_parent_id,
          COALESCE(adt.adt_name,                 ad.ad_name),
          COALESCE(adt.adt_description,          ad.ad_description),
          COALESCE(adt.adt_team,                 ad.ad_team),
          COALESCE(adt.adt_responsible_team,     ad.ad_responsible_team),
          COALESCE(adt.adt_entities,             ad.ad_entities),
          COALESCE(adt.adt_condition,            ad.ad_condition),
          COALESCE(adt.adt_notifications,        ad.ad_notifications),
          COALESCE(adt.adt_status,               ad.ad_status),
          COALESCE(adt.adt_priority,             ad.ad_priority),
          COALESCE(adt.adt_period,               ad.ad_period),
          COALESCE(adt.adt_check_definition_id,  ad.ad_check_definition_id),
          CASE WHEN adt.adt_parameters IS NULL
            THEN ad.ad_parameters
            ELSE COALESCE(ad.ad_parameters, hstore(array[]::varchar[])) || adt.adt_parameters
          END,
          COALESCE(adt.adt_tags,                 ad.ad_tags),
          COALESCE(adt.adt_entities_exclude,     ad.ad_entities_exclude)
     FROM zzm_data.alert_definition_tree adt
     JOIN alert_definition ad
       ON adt.adt_parent_id = ad.ad_id;
