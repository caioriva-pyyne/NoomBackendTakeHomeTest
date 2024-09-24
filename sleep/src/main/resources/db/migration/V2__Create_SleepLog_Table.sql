CREATE TABLE sleep_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sleep_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    date_time_in_bed_start TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    date_time_in_bed_end TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    feeling VARCHAR NOT NULL CHECK (feeling IN ('BAD', 'OK', 'GOOD')),
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);
