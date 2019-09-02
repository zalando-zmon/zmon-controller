UPDATE zzm_data.check_definition SET cd_runtime = 'PYTHON_2'::zzm_data.definition_runtime WHERE cd_runtime IS NULL;

ALTER TABLE zzm_data.check_definition ALTER COLUMN cd_runtime SET NOT NULL;