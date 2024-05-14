ALTER TABLE IF EXISTS public.tasks
    ADD COLUMN task_type text NOT NULL;

ALTER TABLE IF EXISTS public.tasks
    ADD COLUMN team_id bigint;

ALTER TABLE IF EXISTS public.tasks
    ADD CONSTRAINT ref_team_id FOREIGN KEY (team_id)
    REFERENCES public.teams (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

ALTER TABLE IF EXISTS public.tasks
    ADD COLUMN created_by bigint;

ALTER TABLE IF EXISTS public.tasks
    ADD CONSTRAINT ref_user_creator_id FOREIGN KEY (created_by)
    REFERENCES public.users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

ALTER TABLE IF EXISTS public.tasks DROP COLUMN IF EXISTS owner_name;