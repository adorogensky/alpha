CREATE SCHEMA alpha;
CREATE TABLE user_profile (
	id INT,
	login VARCHAR (50),
	password CHAR (32),
	created TIMESTAMP WITH TIME ZONE,
	modified TIMESTAMP WITH TIME ZONE
)