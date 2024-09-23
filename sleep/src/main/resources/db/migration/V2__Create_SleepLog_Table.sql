CREATE TABLE sleep_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sleep_date TIMESTAMP NOT NULL,
    time_in_bed_start TIME NOT NULL,
    time_in_bed_end TIME NOT NULL,
    total_time_in_bed_in_seconds NUMERIC NOT NULL ,
    feeling VARCHAR NOT NULL CHECK (feeling IN ('BAD', 'OK', 'GOOD')),
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);
