-- Links local user to the external auth microservice identity (JWT "sub" claim).
ALTER TABLE users ADD COLUMN external_auth_id VARCHAR(255);

CREATE UNIQUE INDEX idx_users_external_auth_id ON users(external_auth_id);
