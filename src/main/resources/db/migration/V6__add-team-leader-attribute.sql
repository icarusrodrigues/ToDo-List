ALTER TABLE IF EXISTS public.teams
    ADD COLUMN team_leader_id bigint NOT NULL;
ALTER TABLE IF EXISTS public.teams
    ADD CONSTRAINT ref_team_leader FOREIGN KEY (team_leader_id)
    REFERENCES public.users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

ALTER TABLE IF EXISTS public.users DROP COLUMN IF EXISTS user_type;

ALTER TABLE IF EXISTS public.users
    ADD COLUMN user_types text[];