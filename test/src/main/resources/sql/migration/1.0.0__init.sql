CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE product(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  code VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255),
  description TEXT,
  price DECIMAL(5, 2),
  created_at TIMESTAMP,
  created_by VARCHAR(255)
);
