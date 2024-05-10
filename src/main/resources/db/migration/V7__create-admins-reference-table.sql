CREATE TABLE public.admins_teams
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