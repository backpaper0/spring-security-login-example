
insert into accounts (id, username, expiration_date, authentication_failure_count, locked_until, enabled)
values (1, 'user01', timestamp '2999-12-31', 0, null, true),
       (2, 'user02', timestamp '2999-12-31', 0, null, true),
       (3, 'user03', timestamp '2000-01-01', 0, null, true),
       (4, 'user04', timestamp '2999-12-31', 0, '2999-12-31', true),
       (5, 'user05', timestamp '2999-12-31', 0, '2000-01-01', true),
       (6, 'user06', timestamp '2999-12-31', 0, null, false),
       (7, 'user07', timestamp '2999-12-31', 0, null, true),
       (8, 'user08', timestamp '2999-12-31', 2, null, true),
       (9, 'user09', timestamp '2999-12-31', 0, null, true),
       (10, 'user10', timestamp '2999-12-31', 0, null, true),
       (11, 'user11', timestamp '2999-12-31', 0, null, true);

insert into account_passwords (account_id, hashed_password, expiration_date, needs_to_change)
values (1, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false),
       (2, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', true),
       (3, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false),
       (4, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false),
       (5, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false),
       (6, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false),
       (7, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2000-01-01', false),
       (8, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false),
       (9, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false),
       (10, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', true),
       (11, '1N+tEPt0VjnplCfo+KiUZa+aT/YvHMIu4F0cxMeC84dO/Wxwcjec/fz179Z65Dwt', timestamp '2999-12-31', false);

insert into account_authorities (account_id, authority)
values (1, 'AUTH1'),
       (2, 'AUTH1'),
       (3, 'AUTH1'),
       (4, 'AUTH1'),
       (5, 'AUTH1'),
       (6, 'AUTH1'),
       (7, 'AUTH1'),
       (8, 'AUTH1'),
       (9, 'AUTH1'),
       (10, 'AUTH1'),
       (11, 'AUTH1');
