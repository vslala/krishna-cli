package org.main;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

@Slf4j
class FlywayMigration {
    public static final String DB_URL = "jdbc:sqlite:chatbot.db";

    public static void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(DB_URL, null, null)
                .locations("classpath:db/migrations")
                .load();

        flyway.migrate();
        log.info("Database migration complete.");
    }
}
