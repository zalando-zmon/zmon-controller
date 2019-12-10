SET SEARCH_PATH TO zzm_api;

CREATE OR REPLACE FUNCTION create_or_update_entity(entity_data text, teams text[], user_name text) RETURNS text AS
$$
DECLARE
  _id text;
  _data jsonb;
BEGIN
  SELECT e_data INTO _data FROM zzm_data.entity WHERE (e_data->'id')::text = ((entity_data::jsonb)->'id')::text;

  IF (_data->'type')::text <> '' AND (entity_data::json->'type')::text IS DISTINCT FROM (_data->'type')::text THEN
      RAISE EXCEPTION 'Cannot update "type" of an entity'
          USING HINT = 'Please check your "id" and "type"';
  END IF;

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
       )
       AND (e_data IS DISTINCT FROM entity_data::jsonb OR (e_data->'type')::text='"local"')
     RETURNING (e_data->'id')::text INTO _id;
  END;
  RETURN _id;
END;
$$ LANGUAGE PLPGSQL VOLATILE SECURITY DEFINER;
