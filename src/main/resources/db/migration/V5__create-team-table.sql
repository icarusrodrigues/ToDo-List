CREATE TABLE public.teams
(
    id bigint NOT NULL,
    code text NOT NULL,
    name text NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.teams
    OWNER to postgres;

CREATE SEQUENCE public.team_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

ALTER SEQUENCE public.team_id_seq
    OWNER TO postgres;

ALTER TABLE public.teams ALTER COLUMN id SET DEFAULT NEXTVAL('team_id_seq'::regclass);

ALTER TABLE IF EXISTS public.users DROP COLUMN IF EXISTS team;

CREATE TABLE public.users_teams
(
    user_id bigint,
    team_id bigint,
    PRIMARY KEY (user_id, team_id),
    CONSTRAINT ref_user_id FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT ref_team_id FOREIGN KEY (team_id)
        REFERENCES public.teams (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

ALTER TABLE IF EXISTS public.users_teams
    OWNER to postgres;