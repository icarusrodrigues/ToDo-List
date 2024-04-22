CREATE TABLE public.users
(
    id bigint NOT NULL,
    code text NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    email text NOT NULL,
    user_type text NOT NULL,
    team text,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;

CREATE SEQUENCE public.user_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

ALTER SEQUENCE public.user_id_seq
    OWNER TO postgres;

ALTER TABLE public.users ALTER COLUMN id SET DEFAULT NEXTVAL('user_id_seq'::regclass);

ALTER TABLE IF EXISTS public.tasks
    ADD COLUMN owner_id bigint;

ALTER TABLE IF EXISTS public.tasks
    ADD CONSTRAINT ref_user_id FOREIGN KEY (owner_id)
    REFERENCES public.users (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    NOT VALID;