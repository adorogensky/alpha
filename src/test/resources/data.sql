INSERT INTO alpha.user_profile (id, login, password, created, modified) VALUES (
	NEXTVAL('alpha.user_id_seq'), 'alex', '534b44a19bf18d20b71ecc4eb77c572f', TIMESTAMP '2019-04-02 07:58:28', TIMESTAMP '2019-04-02 07:58:28'
) ON CONFLICT DO NOTHING;