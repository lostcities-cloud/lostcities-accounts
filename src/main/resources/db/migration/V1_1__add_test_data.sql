INSERT INTO public.authority VALUES ('ROLE_USER');
INSERT INTO public.authority VALUES ('ROLE_ADMIN');
INSERT INTO public.authority VALUES ('ROLE_SYSTEM');

INSERT INTO public.users VALUES (1, 'anonymousUser', '2021-08-19 20:44:40.446768', 'anonymousUser', '2021-08-19 20:44:40.446768', true, NULL, 'test@example.com', 'Test', NULL, 'en_US', 'Testerson', 'ttesterson', '$2a$10$rsDeKCKJedzNgOienIRmku/YSMI3myF7K4Xol.0C/KcXQBrB2b7HO', NULL, NULL);

INSERT INTO public.users_authority VALUES (1, 'ROLE_USER');
