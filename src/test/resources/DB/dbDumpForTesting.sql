PGDMP     %    '                y            netcracker_hospital_db #   12.9 (Ubuntu 12.9-0ubuntu0.20.04.1)     13.3 (Ubuntu 13.3-1.pgdg20.04+1) /    �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
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
       public          remote_user    false            �            1259    38438    patients    TABLE     �   CREATE TABLE public.patients (
    passport character varying(255),
    polys character varying(255),
    user_id bigint NOT NULL
);
    DROP TABLE public.patients;
       public         heap    remote_user    false            �            1259    38448    roles    TABLE     W   CREATE TABLE public.roles (
    id integer NOT NULL,
    name character varying(20)
);
    DROP TABLE public.roles;
       public         heap    remote_user    false            �            1259    38446    roles_id_seq    SEQUENCE     �   ALTER TABLE public.roles ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public          remote_user    false    210            �            1259    38401    schedule_interval    TABLE     �   CREATE TABLE public.schedule_interval (
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
       public         heap    remote_user    false            �            1259    38453 
   user_roles    TABLE     ^   CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role_id integer NOT NULL
);
    DROP TABLE public.user_roles;
       public         heap    remote_user    false            �            1259    38460    users    TABLE     8  CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    password character varying(255),
    patronymic character varying(255),
    phone character varying(255),
    username character varying(255)
);
    DROP TABLE public.users;
       public         heap    remote_user    false            �            1259    38458    users_id_seq    SEQUENCE     �   ALTER TABLE public.users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public          remote_user    false    213            �          0    38388    doctor 
   TABLE DATA           :   COPY public.doctor (id, name, specialization) FROM stdin;
    public          remote_user    false    203   �8       �          0    38396    doctor_schedule 
   TABLE DATA           8   COPY public.doctor_schedule (id, doctor_id) FROM stdin;
    public          remote_user    false    204   U9       �          0    38438    patients 
   TABLE DATA           <   COPY public.patients (passport, polys, user_id) FROM stdin;
    public          remote_user    false    208   v9       �          0    38448    roles 
   TABLE DATA           )   COPY public.roles (id, name) FROM stdin;
    public          remote_user    false    210   �9       �          0    38401    schedule_interval 
   TABLE DATA           a   COPY public.schedule_interval (doctor_schedule_id, interval_start_time, is_assigned) FROM stdin;
    public          remote_user    false    205   �9       �          0    38406    schedule_pattern 
   TABLE DATA           L   COPY public.schedule_pattern (id, name, doctor_id, days_length) FROM stdin;
    public          remote_user    false    206   �:       �          0    38411    schedule_pattern_interval 
   TABLE DATA           i   COPY public.schedule_pattern_interval (day_number, interval_start_time, schedule_pattern_id) FROM stdin;
    public          remote_user    false    207   �;       �          0    38453 
   user_roles 
   TABLE DATA           6   COPY public.user_roles (user_id, role_id) FROM stdin;
    public          remote_user    false    211   7=       �          0    38460    users 
   TABLE DATA           h   COPY public.users (id, email, first_name, last_name, password, patronymic, phone, username) FROM stdin;
    public          remote_user    false    213   \=       �           0    0    hibernate_sequence    SEQUENCE SET     A   SELECT pg_catalog.setval('public.hibernate_sequence', 51, true);
          public          remote_user    false    202            �           0    0    roles_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.roles_id_seq', 1, false);
          public          remote_user    false    209            �           0    0    users_id_seq    SEQUENCE SET     :   SELECT pg_catalog.setval('public.users_id_seq', 3, true);
          public          remote_user    false    212            :           2606    38395    doctor doctor_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.doctor DROP CONSTRAINT doctor_pkey;
       public            remote_user    false    203            <           2606    38400 $   doctor_schedule doctor_schedule_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.doctor_schedule
    ADD CONSTRAINT doctor_schedule_pkey PRIMARY KEY (id);
 N   ALTER TABLE ONLY public.doctor_schedule DROP CONSTRAINT doctor_schedule_pkey;
       public            remote_user    false    204            F           2606    38445    patients patients_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_pkey PRIMARY KEY (user_id);
 @   ALTER TABLE ONLY public.patients DROP CONSTRAINT patients_pkey;
       public            remote_user    false    208            H           2606    38452    roles roles_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_pkey;
       public            remote_user    false    210            >           2606    38405 (   schedule_interval schedule_interval_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY public.schedule_interval
    ADD CONSTRAINT schedule_interval_pkey PRIMARY KEY (doctor_schedule_id, interval_start_time);
 R   ALTER TABLE ONLY public.schedule_interval DROP CONSTRAINT schedule_interval_pkey;
       public            remote_user    false    205    205            D           2606    38415 8   schedule_pattern_interval schedule_pattern_interval_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY public.schedule_pattern_interval
    ADD CONSTRAINT schedule_pattern_interval_pkey PRIMARY KEY (day_number, interval_start_time, schedule_pattern_id);
 b   ALTER TABLE ONLY public.schedule_pattern_interval DROP CONSTRAINT schedule_pattern_interval_pkey;
       public            remote_user    false    207    207    207            @           2606    38410 &   schedule_pattern schedule_pattern_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.schedule_pattern
    ADD CONSTRAINT schedule_pattern_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.schedule_pattern DROP CONSTRAINT schedule_pattern_pkey;
       public            remote_user    false    206            L           2606    38471 !   users uk6dotkott2kjsp8vw4d0m25fb7 
   CONSTRAINT     ]   ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);
 K   ALTER TABLE ONLY public.users DROP CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7;
       public            remote_user    false    213            B           2606    38417 -   schedule_pattern uk_7d69ema2n6fgu3qfobibwojm6 
   CONSTRAINT     h   ALTER TABLE ONLY public.schedule_pattern
    ADD CONSTRAINT uk_7d69ema2n6fgu3qfobibwojm6 UNIQUE (name);
 W   ALTER TABLE ONLY public.schedule_pattern DROP CONSTRAINT uk_7d69ema2n6fgu3qfobibwojm6;
       public            remote_user    false    206            N           2606    38469 !   users ukr43af9ap4edm43mmtq01oddj6 
   CONSTRAINT     `   ALTER TABLE ONLY public.users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);
 K   ALTER TABLE ONLY public.users DROP CONSTRAINT ukr43af9ap4edm43mmtq01oddj6;
       public            remote_user    false    213            J           2606    38457    user_roles user_roles_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);
 D   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT user_roles_pkey;
       public            remote_user    false    211    211            P           2606    38467    users users_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public            remote_user    false    213            R           2606    38423 -   schedule_interval fk8u7hop61yrh8wt7xugnmxjmi4    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule_interval
    ADD CONSTRAINT fk8u7hop61yrh8wt7xugnmxjmi4 FOREIGN KEY (doctor_schedule_id) REFERENCES public.doctor_schedule(id);
 W   ALTER TABLE ONLY public.schedule_interval DROP CONSTRAINT fk8u7hop61yrh8wt7xugnmxjmi4;
       public          remote_user    false    2876    205    204            V           2606    38477 &   user_roles fkh8ciramu9cc9q3qcqiv4ue8a6    FK CONSTRAINT     �   ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES public.roles(id);
 P   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6;
       public          remote_user    false    211    2888    210            S           2606    38428 ,   schedule_pattern fkhcj8ys1olauxndlv9grpyiqph    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule_pattern
    ADD CONSTRAINT fkhcj8ys1olauxndlv9grpyiqph FOREIGN KEY (doctor_id) REFERENCES public.doctor(id);
 V   ALTER TABLE ONLY public.schedule_pattern DROP CONSTRAINT fkhcj8ys1olauxndlv9grpyiqph;
       public          remote_user    false    206    203    2874            W           2606    38482 &   user_roles fkhfh9dx7w3ubf1co1vdev94g3f    FK CONSTRAINT     �   ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);
 P   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f;
       public          remote_user    false    213    211    2896            T           2606    38433 5   schedule_pattern_interval fkqk37lbn6lj32tvab5r2rtquqo    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule_pattern_interval
    ADD CONSTRAINT fkqk37lbn6lj32tvab5r2rtquqo FOREIGN KEY (schedule_pattern_id) REFERENCES public.schedule_pattern(id);
 _   ALTER TABLE ONLY public.schedule_pattern_interval DROP CONSTRAINT fkqk37lbn6lj32tvab5r2rtquqo;
       public          remote_user    false    2880    207    206            Q           2606    38418 +   doctor_schedule fkrresxag4ex638q3fincrya0wr    FK CONSTRAINT     �   ALTER TABLE ONLY public.doctor_schedule
    ADD CONSTRAINT fkrresxag4ex638q3fincrya0wr FOREIGN KEY (doctor_id) REFERENCES public.doctor(id);
 U   ALTER TABLE ONLY public.doctor_schedule DROP CONSTRAINT fkrresxag4ex638q3fincrya0wr;
       public          remote_user    false    204    203    2874            U           2606    38472 #   patients fkuwca24wcd1tg6pjex8lmc0y7    FK CONSTRAINT     �   ALTER TABLE ONLY public.patients
    ADD CONSTRAINT fkuwca24wcd1tg6pjex8lmc0y7 FOREIGN KEY (user_id) REFERENCES public.users(id);
 M   ALTER TABLE ONLY public.patients DROP CONSTRAINT fkuwca24wcd1tg6pjex8lmc0y7;
       public          remote_user    false    2896    213    208            �   F   x�3�t��/N�,K-I-��K,�L��2
�g$�
�bSi�M�%6��ؔcUk�U�9V�����qqq �lJ�      �      x�3�4����� T      �      x������4���\1z\\\ 1��      �   6   x�3���q�v�2��C<]�B��!\�� .����ӏ+F��� Ƽ�      �   �   x�}��	1ϻU�K�O-9��1�k{�b�Hur��T|�j}|��5ן|������/�/�����o�o�����������_�_���������������������5�5�5�5�5�5���ץ*����q�z��̏=�y�G_���梸��g>�Aq/-OwP�K�������^z��q/=������=�y�R��Z��؟�}��}��7�Ff      �   �   x�-O�m�@<�V�`��6()�.r儐r@B�b[
��ȁH:0[Κ�u����7_�N�pEi�J.{�@�f���:�j�~���v�\��đOZ��Dw��E%{����C� T��ˁ:��X�̜���I!�Q�7��Cu����?��O�����xV�G.�)A>lf���tk�=8�ąyiN����5S<�+�7p(��~z`�mp$;�/S����f      �   b  x�m�Q�1�o{1��Z�og-��:&���!��G	�\]����8~�nc܏?ӹM'�M؄]�kg��ba{����a{�>n�6��vX`��7��-��v�&l��x�s���ܦ��;a'l�&l��{���'���.ص��%x	^���%x	^���%x	^���%x	^���%x	^��������������a/�J���Rd)�Y�,E�"K���Rd)�Y�,E�"K���Rd)�Y�,E�"K���Rd)�Y�,E�"K���Rd)�Y�,E�"K���Rd)�Y��7����k�����k��	{~M�d��|����K��7[���a�>,އ���l���	��M�'íííí����?YZ�      �      x�3�4�2�4����� ��      �   �   x�u�M�0 ���wx�nӢ[���b�,�.b�u��f�>�ty��g[�;�*��^Wc���3i
IH���ºQ[�a�R��a�e�:�q@Qq��w�`A��4���2��s��_�N��a�������
I=J
�Ǚ�O������몡.�h������+� � <�S�     