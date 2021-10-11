SELECT *
FROM pg_catalog.pg_tables
WHERE schemaname = 'public'

DELETE FROM doctor_schedule

SELECT * FROM doctor_schedule

SELECT * FROM doctor_schedule_interval_status_list

SELECT * FROM doctor

DO $$ DECLARE
  r RECORD;
BEGIN
  FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP
    EXECUTE 'DROP TABLE ' || quote_ident(r.tablename) || ' CASCADE';
  END LOOP;
END $$;