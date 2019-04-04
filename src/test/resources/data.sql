INSERT INTO user_profile (id, login, password, created, modified) VALUES (
	1, 'alex', '534b44a19bf18d20b71ecc4eb77c572f', TIMESTAMP '2019-04-02 07:58:28', NULL
) ON CONFLICT DO NOTHING;