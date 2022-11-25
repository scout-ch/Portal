CREATE OR REPLACE FUNCTION tile_override_delete_cascade() RETURNS trigger AS
$$
BEGIN
    UPDATE master_tile SET override_id = NULL WHERE override_id = OLD.id;
    UPDATE user_tile SET override_id = NULL WHERE override_id = OLD.id;
    RETURN OLD;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER delete_cascade
    BEFORE DELETE
    ON tile_override
    FOR EACH ROW
EXECUTE PROCEDURE tile_override_delete_cascade();