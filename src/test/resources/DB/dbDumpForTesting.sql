PGDMP     (                
    y            netcracker_hospital_db #   12.9 (Ubuntu 12.9-0ubuntu0.20.04.1)     13.3 (Ubuntu 13.3-1.pgdg20.04+1)     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16738    netcracker_hospital_db    DATABASE     k   CREATE DATABASE netcracker_hospital_db WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'ru_RU.UTF-8';
 &   DROP DATABASE netcracker_hospital_db;
                postgres    false            �            1259    38388    doctor    TABLE     �   CREATE TABLE public.doctor (
    id bigint NOT NULL,
    name character varying(255),
    specialization character varying(255)
);
    DROP TABLE public.doctor;
       public         heap    remote_user    false            �            1259    38396    doctor_schedule    TABLE     V   CREATE TABLE public.doctor_schedule (
    id bigint NOT NULL,
    doctor_id bigint
);
 #   DROP TABLE public.doctor_schedule;
       public         heap    remote_user    false            �            1259    38386    hibernate_sequence    SEQUENCE     {   CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.hibernate_sequence;
       public          remote_user    false            �            1259    38401    schedule_interval    TABLE     �   CREATE TABLE public.schedule_interval (
    doctor_schedule_id bigint NOT NULL,
    interval_start_time timestamp without time zone NOT NULL,
    is_assigned boolean
);
 %   DROP TABLE public.schedule_interval;
       public         heap    remote_user    false            �            1259    38406    schedule_pattern    TABLE     �   CREATE TABLE public.schedule_pattern (
    id bigint NOT NULL,
    name character varying(255),
    doctor_id bigint,
    days_length integer
);
 $   DROP TABLE public.schedule_pattern;
       public         heap    remote_user    false            �            1259    38411    schedule_pattern_interval    TABLE     �   CREATE TABLE public.schedule_pattern_interval (
    day_number integer NOT NULL,
    interval_start_time time without time zone NOT NULL,
    schedule_pattern_id bigint NOT NULL
);
 -   DROP TABLE public.schedule_pattern_interval;
       public         heap    remote_user    false            �          0    38388    doctor 
   TABLE DATA           :   COPY public.doctor (id, name, specialization) FROM stdin;
    public          remote_user    false    203   7        �          0    38396    doctor_schedule 
   TABLE DATA           8   COPY public.doctor_schedule (id, doctor_id) FROM stdin;
    public          remote_user    false    204   �        �          0    38401    schedule_interval 
   TABLE DATA           a   COPY public.schedule_interval (doctor_schedule_id, interval_start_time, is_assigned) FROM stdin;
    public          remote_user    false    205   �        �          0    38406    schedule_pattern 
   TABLE DATA           L   COPY public.schedule_pattern (id, name, doctor_id, days_length) FROM stdin;
    public          remote_user    false    206   �        �          0    38411    schedule_pattern_interval 
   TABLE DATA           i   COPY public.schedule_pattern_interval (day_number, interval_start_time, schedule_pattern_id) FROM stdin;
    public          remote_user    false    207   f!       �           0    0    hibernate_sequence    SEQUENCE SET     A   SELECT pg_catalog.setval('public.hibernate_sequence', 43, true);
          public          remote_user    false    202            $           2606    38395    doctor doctor_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.doctor DROP CONSTRAINT doctor_pkey;
       public            remote_user    false    203            &           2606    38400 $   doctor_schedule doctor_schedule_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.doctor_schedule
    ADD CONSTRAINT doctor_schedule_pkey PRIMARY KEY (id);
 N   ALTER TABLE ONLY public.doctor_schedule DROP CONSTRAINT doctor_schedule_pkey;
       public            remote_user    false    204            (           2606    38405 (   schedule_interval schedule_interval_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY public.schedule_interval
    ADD CONSTRAINT schedule_interval_pkey PRIMARY KEY (doctor_schedule_id, interval_start_time);
 R   ALTER TABLE ONLY public.schedule_interval DROP CONSTRAINT schedule_interval_pkey;
       public            remote_user    false    205    205            .           2606    38415 8   schedule_pattern_interval schedule_pattern_interval_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY public.schedule_pattern_interval
    ADD CONSTRAINT schedule_pattern_interval_pkey PRIMARY KEY (day_number, interval_start_time, schedule_pattern_id);
 b   ALTER TABLE ONLY public.schedule_pattern_interval DROP CONSTRAINT schedule_pattern_interval_pkey;
       public            remote_user    false    207    207    207            *           2606    38410 &   schedule_pattern schedule_pattern_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.schedule_pattern
    ADD CONSTRAINT schedule_pattern_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.schedule_pattern DROP CONSTRAINT schedule_pattern_pkey;
       public            remote_user    false    206            ,           2606    38417 -   schedule_pattern uk_7d69ema2n6fgu3qfobibwojm6 
   CONSTRAINT     h   ALTER TABLE ONLY public.schedule_pattern
    ADD CONSTRAINT uk_7d69ema2n6fgu3qfobibwojm6 UNIQUE (name);
 W   ALTER TABLE ONLY public.schedule_pattern DROP CONSTRAINT uk_7d69ema2n6fgu3qfobibwojm6;
       public            remote_user    false    206            0           2606    38423 -   schedule_interval fk8u7hop61yrh8wt7xugnmxjmi4    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule_interval
    ADD CONSTRAINT fk8u7hop61yrh8wt7xugnmxjmi4 FOREIGN KEY (doctor_schedule_id) REFERENCES public.doctor_schedule(id);
 W   ALTER TABLE ONLY public.schedule_interval DROP CONSTRAINT fk8u7hop61yrh8wt7xugnmxjmi4;
       public          remote_user    false    2854    204    205            1           2606    38428 ,   schedule_pattern fkhcj8ys1olauxndlv9grpyiqph    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule_pattern
    ADD CONSTRAINT fkhcj8ys1olauxndlv9grpyiqph FOREIGN KEY (doctor_id) REFERENCES public.doctor(id);
 V   ALTER TABLE ONLY public.schedule_pattern DROP CONSTRAINT fkhcj8ys1olauxndlv9grpyiqph;
       public          remote_user    false    2852    203    206            2           2606    38433 5   schedule_pattern_interval fkqk37lbn6lj32tvab5r2rtquqo    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule_pattern_interval
    ADD CONSTRAINT fkqk37lbn6lj32tvab5r2rtquqo FOREIGN KEY (schedule_pattern_id) REFERENCES public.schedule_pattern(id);
 _   ALTER TABLE ONLY public.schedule_pattern_interval DROP CONSTRAINT fkqk37lbn6lj32tvab5r2rtquqo;
       public          remote_user    false    206    2858    207            /           2606    38418 +   doctor_schedule fkrresxag4ex638q3fincrya0wr    FK CONSTRAINT     �   ALTER TABLE ONLY public.doctor_schedule
    ADD CONSTRAINT fkrresxag4ex638q3fincrya0wr FOREIGN KEY (doctor_id) REFERENCES public.doctor(id);
 U   ALTER TABLE ONLY public.doctor_schedule DROP CONSTRAINT fkrresxag4ex638q3fincrya0wr;
       public          remote_user    false    2852    204    203            �   9   x�3�t��/N���22�3ALS��B�!jh�64F7A7G�@���qqq }!�      �      x�3�4����� T      �      x������ � �      �   �   x�%��	�@��nk�Y;��9��Ao&#d�b���bb3���?̛o����� ���DY��Zmw��9��F��v����!���5�,Q3G�����G��3+qs�
w�ӡ���9=����O��h[����z����Z�      �   �   x�U���0ѳUL�!�-���:�F8�.�;|a�B<���%b���JY�JV�Svv��7����:ZG�h=����Y�\ⶢW�����d�m�����l2d!Y�RV����U��6�.�e�쐝��ꂺ�.�ꂺ�.�ꂺ�.�ꂺ�.�ꂺ�.�K�K�K�K�K�K�K�.���j���     