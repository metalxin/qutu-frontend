-- Quartz table name case migration for case-sensitive MySQL
--
-- Why:
-- Quartz SQL appends uppercase suffixes (e.g. TRIGGERS, LOCKS) to tablePrefix.
-- With tablePrefix=qrtz_, runtime looks for names like qrtz_TRIGGERS.
-- If your schema has all-lowercase names (e.g. qrtz_triggers), Quartz fails with "table doesn't exist".
--
-- Run this once in schema: qutu

USE qutu;
SET FOREIGN_KEY_CHECKS = 0;

RENAME TABLE
  qrtz_job_details TO qrtz_JOB_DETAILS,
  qrtz_triggers TO qrtz_TRIGGERS,
  qrtz_simple_triggers TO qrtz_SIMPLE_TRIGGERS,
  qrtz_cron_triggers TO qrtz_CRON_TRIGGERS,
  qrtz_simprop_triggers TO qrtz_SIMPROP_TRIGGERS,
  qrtz_blob_triggers TO qrtz_BLOB_TRIGGERS,
  qrtz_calendars TO qrtz_CALENDARS,
  qrtz_paused_trigger_grps TO qrtz_PAUSED_TRIGGER_GRPS,
  qrtz_fired_triggers TO qrtz_FIRED_TRIGGERS,
  qrtz_scheduler_state TO qrtz_SCHEDULER_STATE,
  qrtz_locks TO qrtz_LOCKS;

SET FOREIGN_KEY_CHECKS = 1;

-- Verify expected names exist
SHOW TABLES LIKE 'qrtz_%';
