SELECT *
FROM pg_catalog.pg_tables
WHERE schemaname = 'public'

DELETE FROM doctor_schedule

SELECT * FROM doctor_schedule

SELECT * FROM schedule_interval

SELECT * FROM schedule_pattern

SELECT * FROM schedule_pattern_interval WHERE schedule_pattern_id = 26 ORDER BY day_number, interval_start_time

DELETE FROM schedule_pattern

DELETE FROM schedule_pattern_interval

SELECT * FROM doctor

SELECT * FROM ((SELECT * FROM doctor_schedule doc_sch WHERE doctor_id = 1) ds JOIN schedule_interval si ON ds.id = si.doctor_schedule_id) sch_int WHERE interval_start_time = '1970-01-01 05:30:00'

SELECT * FROM schedule_interval WHERE doctor_schedule_id = 3 AND interval_start_time BETWEEN '1970-01-01' AND '1970-01-02' ORDER BY interval_start_time

CREATE TABLE IF NOT EXISTS public.doctor_schedule
(
    id bigint NOT NULL,
    doctor_id bigint,
    CONSTRAINT doctor_schedule_pkey PRIMARY KEY (id),
    CONSTRAINT fkrresxag4ex638q3fincrya0wr FOREIGN KEY (doctor_id)
        REFERENCES public.doctor (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS public.schedule_interval
(
    doctor_schedule_id bigint NOT NULL,
    interval_start_time timestamp without time zone NOT NULL,
    is_assigned boolean,
    CONSTRAINT schedule_interval_pkey PRIMARY KEY (doctor_schedule_id, interval_start_time),
    CONSTRAINT fk8u7hop61yrh8wt7xugnmxjmi4 FOREIGN KEY (doctor_schedule_id)
        REFERENCES public.doctor_schedule (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS public.schedule_pattern
(
    id bigint NOT NULL,
	name varchar(100),
    CONSTRAINT schedule_pattern_pkey PRIMARY KEY (id)
)

CREATE TABLE IF NOT EXISTS public.schedule_pattern_interval
(
    interval_start_time timestamp without time zone NOT NULL,
    schedule_pattern_id bigint NOT NULL,
    CONSTRAINT schedule_pattern_interval_pkey PRIMARY KEY (interval_start_time, schedule_pattern_id),
    CONSTRAINT fkqk37lbn6lj32tvab5r2rtquqo FOREIGN KEY (schedule_pattern_id)
        REFERENCES public.schedule_pattern (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)