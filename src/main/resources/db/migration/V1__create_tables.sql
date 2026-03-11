CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150)  NOT NULL,
    email       VARCHAR(255)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    role        VARCHAR(20)   NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE accounts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100)  NOT NULL,
    type        VARCHAR(30)   NOT NULL,
    balance     NUMERIC(19,2) NOT NULL DEFAULT 0,
    user_id     UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100)  NOT NULL,
    description VARCHAR(255),
    custom      BOOLEAN       NOT NULL DEFAULT true,
    user_id     UUID          REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE transactions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount      NUMERIC(19,2) NOT NULL,
    type        VARCHAR(20)   NOT NULL,
    description VARCHAR(255),
    date        DATE          NOT NULL,
    account_id  UUID          NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    category_id UUID          NOT NULL REFERENCES categories(id),
    user_id     UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE budgets (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    limit_amount    NUMERIC(19,2) NOT NULL,
    reference_month VARCHAR(7)    NOT NULL,
    category_id     UUID          NOT NULL REFERENCES categories(id),
    user_id         UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at      TIMESTAMP     NOT NULL DEFAULT now(),
    UNIQUE (user_id, category_id, reference_month)
);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_date ON transactions(date);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, reference_month);
