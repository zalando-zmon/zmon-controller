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
