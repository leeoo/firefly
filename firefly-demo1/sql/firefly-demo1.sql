DROP TABLE IF EXISTS cms_user;
CREATE TABLE cms_user(
	id char(36) PRIMARY KEY,
	name varchar(25),
	password varchar(32)
);

INSERT INTO cms_user VALUES('073f6d62-1405-448d-abec-93361be391af', 'firefly','123456');
INSERT INTO cms_user VALUES('0965008c-2f2e-46bc-999c-547e44446052', 'bob','123456');
INSERT INTO cms_user VALUES('0d022458-8705-4bf4-a156-1e1ca800001c', 'jack','123456');
INSERT INTO cms_user VALUES('1c50c7ac-11af-48c6-bcf7-09cfe71e2d34', 'foo','123456');
INSERT INTO cms_user VALUES('1f52eca5-9404-4c94-b627-6aa2c54ece7c', 'bar','123456');