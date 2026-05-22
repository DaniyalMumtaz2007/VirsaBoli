package com.example.virsa.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:sqlite:virsaboli.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "username TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "full_name TEXT, " +
                    "dob TEXT, " +
                    "hearts INTEGER DEFAULT 5, " +
                    "xp INTEGER DEFAULT 0, " +
                    "streak INTEGER DEFAULT 0, " +
                    "last_login_time INTEGER DEFAULT 0, " +
                    "is_learner INTEGER DEFAULT 1, " +
                    "is_contributor INTEGER DEFAULT 0" +
                    ");";
            stmt.execute(createUsersTable);

            
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN email TEXT;");
            } catch (Exception ignore) { }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN last_login_time INTEGER DEFAULT 0;");
            } catch (Exception ignore) { }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN is_learner INTEGER DEFAULT 1;");
            } catch (Exception ignore) { }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN is_contributor INTEGER DEFAULT 0;");
            } catch (Exception ignore) { }

            
            String createLanguagesTable = "CREATE TABLE IF NOT EXISTS user_languages (" +
                    "user_id INTEGER, " +
                    "language_name TEXT, " +
                    "current_lesson_level INTEGER DEFAULT 1, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id), " +
                    "UNIQUE(user_id, language_name)" +
                    ");";
            stmt.execute(createLanguagesTable);

            
            try {
                stmt.execute("ALTER TABLE user_languages ADD COLUMN current_lesson_level INTEGER DEFAULT 1;");
            } catch (Exception ignore) {
                
            }

            
            String createQuestionsTable = "CREATE TABLE IF NOT EXISTS questions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "language TEXT NOT NULL, " +
                    "unit INTEGER DEFAULT 1, " +
                    "lesson_number INTEGER DEFAULT 1, " + 
                    "question_type TEXT NOT NULL, " +
                    "prompt TEXT NOT NULL, " +
                    "correct_answer TEXT NOT NULL, " +
                    "wrong_option_1 TEXT NOT NULL, " +
                    "wrong_option_2 TEXT NOT NULL" +
                    ");";
            stmt.execute(createQuestionsTable);

            
            try {
                stmt.execute("ALTER TABLE questions ADD COLUMN lesson_number INTEGER DEFAULT 1;");
            } catch (Exception ignore) {
            }

            
            String createUnitNamesTable = "CREATE TABLE IF NOT EXISTS unit_names (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "language TEXT NOT NULL, " +
                    "unit INTEGER NOT NULL, " +
                    "unit_name TEXT NOT NULL, " +
                    "UNIQUE(language, unit)" +
                    ");";
            stmt.execute(createUnitNamesTable);

        } catch (Exception e) {
            System.err.println("Database initialization failed!");
            e.printStackTrace();
        }
    }
}