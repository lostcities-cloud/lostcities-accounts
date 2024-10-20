INSERT INTO authority VALUES ('ROLE_USER');
INSERT INTO authority VALUES ('ROLE_ADMIN');
INSERT INTO authority VALUES ('ROLE_SYSTEM');

insert into users (id, activated, activation_key, email, first_name, image_url, lang_key, last_name, login,
                   password_hash, reset_date, reset_key, created_by, created_date, last_modified_by, last_modified_date)
values (1,true, null,'dnelson@test.com','Derek', null, 'en_US','Nelson', 'dnelson','$2a$10$joFJ4cTr24coog2XhAJtru5aFSdQT10rL4y4qKrUSDYm3TGn7hSIi',
        null, null, 'system', '2024-02-23 00:58:32.817828 +00:00', 'system','2024-02-23 00:58:32.817828 +00:00'
       );


insert into users (id, activated, activation_key, email, first_name, image_url, lang_key, last_name, login,
                   password_hash, reset_date, reset_key, created_by, created_date, last_modified_by, last_modified_date)
values (2,true, null,'ttesterson@test.com','Test', null, 'en_US','Testerson', 'ttesterson','$2a$10$rsDeKCKJedzNgOienIRmku/YSMI3myF7K4Xol.0C/KcXQBrB2b7HO',
        null, null, 'system', '2024-02-23 00:58:32.817828 +00:00', 'system','2024-02-23 00:58:32.817828 +00:00'
       );

INSERT INTO users_authority VALUES (1, 'ROLE_USER');
INSERT INTO users_authority VALUES (2, 'ROLE_USER');
