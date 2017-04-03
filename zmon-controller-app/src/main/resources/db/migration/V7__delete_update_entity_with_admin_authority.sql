-- update to fix return of null if there are no checks at all.

SET search_path TO zzm_api, public;

DROP FUNCTION IF EXISTS create_or_update_entity(entity_data text, teams text[], user_name text);
DROP FUNCTION IF EXISTS delete_entity(id text, teams text[], user_name text);

CREATE OR REPLACE FUNCTION create_or_update_entity(entity_data text, teams text[], user_name text, user_is_admin boolean) RETURNS text AS
$BODY$
DECLARE
  _id text;
  _data jsonb;

BEGIN
  SELECT e_data INTO _data FROM zzm_data.entity WHERE (e_data->'id')::text = ((entity_data::jsonb)->'id')::text;
  IF (entity_data::json->'type')::text IS DISTINCT FROM '"local"' AND _data IS NOT DISTINCT FROM entity_data::jsonb THEN
    RETURN (_data->'id')::text;
  END IF;

  BEGIN
    INSERT INTO zzm_data.entity(e_data, e_created_by, e_last_modified_by) SELECT entity_data::jsonb, user_name, user_name RETURNING (e_data->'id')::text INTO _id;
  EXCEPTION WHEN UNIQUE_VIOLATION THEN
    UPDATE zzm_data.entity
       SET e_data = entity_data::jsonb,
           e_last_modified = now(),
           e_last_modified_by = user_name
     WHERE (e_data->'id')::text = ((entity_data::jsonb)->'id')::text
       AND (
           (e_data->'team'::text)::text IS NULL
           OR REPLACE((e_data -> 'team'::text)::text, '"', '') = ANY(teams)
           OR e_created_by = user_name
           OR user_is_admin IS TRUE
       )
       AND (e_data IS DISTINCT FROM entity_data::jsonb OR (e_data->'type')::text='"local"')
     RETURNING (e_data->'id')::text INTO _id;
  END;
  RETURN _id;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;


CREATE OR REPLACE FUNCTION delete_entity(id text, teams text[], user_name text, user_is_admin boolean) RETURNS SETOF text AS
$BODY$
 DELETE FROM zzm_data.entity
  WHERE (((e_data -> 'id'::text)::text)) = '"'||id||'"'
    AND (
        (e_data->'team'::text)::text IS NULL
        OR REPLACE((e_data -> 'team'::text)::text, '"', '') = ANY(teams)
        OR e_created_by = user_name
        OR user_is_admin IS TRUE
    )
  RETURNING (((e_data -> 'id'::text)::text));
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
COST 100;
