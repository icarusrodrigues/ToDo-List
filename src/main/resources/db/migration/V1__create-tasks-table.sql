CREATE TABLE public.tasks
(
    id bigint NOT NULL,
    code text NOT NULL,
    title text NOT NULL,
    description text,
    due_date text,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.tasks
    OWNER to postgres;