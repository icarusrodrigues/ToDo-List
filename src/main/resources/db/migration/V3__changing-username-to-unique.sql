ALTER TABLE IF EXISTS public.users
    ADD CONSTRAINT unique_user_username UNIQUE (username);