CREATE TYPE zzm_data.definition_runtime AS ENUM(
    'PYTHON_2',
    'PYTHON_3'
);

-- This column will be added as null initially and backfilled later
-- for backward compatibility and performance purposes.
ALTER TABLE zzm_data.check_definition
    ADD COLUMN cd_runtime definition_runtime NULL;

ALTER TYPE zzm_api.check_definition_import
    ADD ATTRIBUTE runtime zzm_data.definition_runtime;

ALTER TYPE zzm_api.check_definition_type
    ADD ATTRIBUTE runtime zzm_data.definition_runtime;
