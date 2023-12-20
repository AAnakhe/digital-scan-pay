
CREATE TABLE users (
    uuid UUID DEFAULT gen_random_uuid() NOT NULL,
    name VARCHAR(255),
    phoneNumber VARCHAR(30),
    email VARCHAR(255),
    password VARCHAR(255),
    unique_id VARCHAR(255),
    gender VARCHAR(10),
    nin VARCHAR(30),
    vehicleType VARCHAR(255),
    manufacturer VARCHAR(255),
    plateNumber VARCHAR(30),
    drivers_license VARCHAR(500),
    roles JSONB,
    isVerified BOOLEAN,
    secretKey VARCHAR(255),
    otp VARCHAR(10)
);

ALTER TABLE users ADD CONSTRAINT unique_uuid UNIQUE (uuid);


--ALTER TABLE users
--ADD COLUMN email VARCHAR(255),
--ADD COLUMN driversLicence VARCHAR(255),
--ADD COLUMN roles JSONB;


CREATE TABLE Transaction (
    transaction_id UUID PRIMARY KEY,
    user_uuid UUID NOT NULL,
    service_type VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    transaction_time TIMESTAMPTZ NOT NULL
);

-- Create an index on user_uuid for efficient retrieval of user transactions
CREATE INDEX idx_user_uuid ON Transaction (user_uuid);

-- Add foreign key constraint to link transactions to users
ALTER TABLE Transaction
ADD CONSTRAINT fk_user_uuid
FOREIGN KEY (user_uuid)
REFERENCES users (uuid);


CREATE TABLE Dashboard (
    user_uuid UUID PRIMARY KEY,
    bus_fare DECIMAL(19, 4),
    garage_ticket DECIMAL(19, 4),
    tax DECIMAL(19, 4)
);

-- Add foreign key constraint to link dashboard data to users
ALTER TABLE Dashboard
ADD CONSTRAINT fk_user_uuid
FOREIGN KEY (user_uuid)
REFERENCES users (uuid);



CREATE TABLE Payments (
    payment_id UUID PRIMARY KEY,
    user_uuid UUID NOT NULL,
    plate_number VARCHAR(255) NOT NULL,
    service VARCHAR(255) NOT NULL,
    park VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    plan VARCHAR(255) NOT NULL,
    payment_time TIMESTAMPTZ NOT NULL,
    status VARCHAR(255) NOT NULL
);

ALTER TABLE Payments
ADD CONSTRAINT fk_user_uuid
FOREIGN KEY (user_uuid)
REFERENCES users (uuid);

-- for existing table not new
--ALTER TABLE Payments
--ALTER COLUMN payment_id SET DEFAULT gen_random_uuid();


CREATE TABLE agent (
    uuid UUID DEFAULT gen_random_uuid() NOT NULL,
    name VARCHAR(255),
    phoneNumber VARCHAR(25),
    password VARCHAR(255),
    gender VARCHAR(10),
    nin VARCHAR(30),
    driversLicense VARCHAR(500),
    roles JSONB,
    isVerified BOOLEAN,
    secretKey VARCHAR(255),
    otp VARCHAR(10)
);
