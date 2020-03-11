CREATE TYPE alert_comment_import AS (
    id                  int,
    created             timestamptz,
    created_by          text,
    last_modified       timestamptz,
    last_modified_by    text,
    comment             text,
    alert_definition_id int,
    entity_id           text
);
