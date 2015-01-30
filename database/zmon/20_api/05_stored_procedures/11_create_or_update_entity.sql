CREATE OR REPLACE FUNCTION create_or_update_entity(entity_data text, team text, user_name text) RETURNS void AS
$$
BEGIN
  BEGIN
    INSERT INTO zzm_data.entity(e_data, e_created_by, e_last_modified_by) SELECT entity_data::jsonb, user_name, user_name;
  EXCEPTION WHEN UNIQUE_VIOLATION THEN
    UPDATE zzm_data.entity
       SET e_data = entity_data::jsonb,
           e_last_modified = now(),
           e_last_modified_by = user_name
     WHERE (e_data->'id')::text = ((entity_data::jsonb)->'id')::text
       AND (e_data->'team')::text = ((entity_data::jsonb)->'team')::text;
  END;
END;
$$ LANGUAGE PLPGSQL;
