insert into member values ('test','LOCAL','city','mail','name','$2a$12$WNrcfrhC3LBsJmcswhvySOMuFr28o2zPIzCni/zJ1.gFC.DUJzK.a','num','pic','surname','false');

INSERT INTO role (name)
VALUES ('ROLE_USER');
INSERT INTO role (name)
VALUES ('ROLE_ADMIN');
INSERT INTO role (name)
VALUES ('ROLE_PASSENGER');
INSERT INTO role (name)
VALUES ('ROLE_DRIVER');

INSERT INTO user_role (user_id, role_id)
VALUES ('test', 3);