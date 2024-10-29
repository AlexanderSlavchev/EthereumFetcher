DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'admin') THEN
            CREATE ROLE admin WITH LOGIN SUPERUSER PASSWORD 'admin123';
        END IF;
    END

$$;