package com.example.vibestudycoach;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS study_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject TEXT NOT NULL,
                minutes INTEGER NOT NULL,
                difficulty INTEGER NOT NULL,
                confidence INTEGER NOT NULL,
                note TEXT,
                focus_score INTEGER NOT NULL,
                risk_level TEXT NOT NULL,
                suggestion TEXT NOT NULL,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }
}
