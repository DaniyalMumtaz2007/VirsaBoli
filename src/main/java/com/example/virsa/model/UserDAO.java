package com.example.virsa.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    
    public boolean registerUser(String email, String username, String password, String fullName, String dob, boolean isContributor) {
        String sql = "INSERT INTO users(email, username, password, full_name, dob, is_contributor) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, fullName);
            pstmt.setString(5, dob);
            pstmt.setInt(6, isContributor ? 1 : 0);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public UserSession authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                long currentTime = System.currentTimeMillis();
                long lastLogin = rs.getLong("last_login_time");
                int hearts = rs.getInt("hearts");
                int userId = rs.getInt("id");

                
                if (lastLogin > 0 && hearts < 5) {
                    int hoursPassed = (int) ((currentTime - lastLogin) / 3600000);
                    if (hoursPassed > 0) {
                        hearts = Math.min(5, hearts + hoursPassed);
                    }
                }

                try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET last_login_time = ?, hearts = ? WHERE id = ?")) {
                    updateStmt.setLong(1, currentTime);
                    updateStmt.setInt(2, hearts);
                    updateStmt.setInt(3, userId);
                    updateStmt.executeUpdate();
                }

                
                UserSession session = UserSession.getInstance();
                session.login(rs.getString("username"), rs.getString("email"));
                session.setUserId(userId);
                session.setIsLearner(rs.getInt("is_learner") == 1); 
                session.setIsContributor(rs.getInt("is_contributor") == 1);
                session.setHearts(hearts);
                session.setXp(rs.getInt("xp"));
                session.setStreak(rs.getInt("streak"));

                
                session.getActiveLanguages().clear();
                session.getActiveLanguages().addAll(getUserLanguages(session.getUserId()));
                return session;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

    
    public void addLanguage(int userId, String language) {
        String sql = "INSERT OR IGNORE INTO user_languages(user_id, language_name, current_lesson_level) VALUES(?,?, 1)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, language);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getUserLanguages(int userId) {
        List<String> langs = new ArrayList<>();
        String sql = "SELECT language_name FROM user_languages WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                langs.add(rs.getString("language_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return langs;
    }

    public int getCurrentLessonLevel(int userId, String language) {
        String sql = "SELECT current_lesson_level FROM user_languages WHERE user_id = ? AND language_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, language);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("current_lesson_level");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; 
    }

    public void incrementLessonLevel(int userId, String language) {
        String sql = "UPDATE user_languages SET current_lesson_level = current_lesson_level + 1 WHERE user_id = ? AND language_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, language);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void saveUserProgress(UserSession session) {
        String sql = "UPDATE users SET hearts = ?, xp = ?, streak = ?, last_login_time = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, session.getHearts());
            pstmt.setInt(2, session.getXp());
            pstmt.setInt(3, session.getStreak());
            pstmt.setLong(4, System.currentTimeMillis()); 
            pstmt.setInt(5, session.getUserId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Integer> getAvailableUnits(String language) {
        List<Integer> units = new ArrayList<>();
        String sql = "SELECT DISTINCT unit FROM questions WHERE language = ? ORDER BY unit ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, language);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                units.add(rs.getInt("unit"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return units;
    }

    public boolean createUnit(String language, int unitNumber, String unitName) {
        String sql = "INSERT OR IGNORE INTO unit_names(language, unit, unit_name) VALUES(?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, language);
            pstmt.setInt(2, unitNumber);
            pstmt.setString(3, unitName);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUnitName(String language, int unitNumber) {
        String sql = "SELECT unit_name FROM unit_names WHERE language = ? AND unit = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, language);
            pstmt.setInt(2, unitNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("unit_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unit " + unitNumber;
    }

    public int getNextUnitNumber(String language) {
        String sql = "SELECT MAX(unit) as max_unit FROM unit_names WHERE language = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, language);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("max_unit") + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public List<Integer> getAvailableLessons(String language, int unit) {
        List<Integer> lessons = new ArrayList<>();
        String sql = "SELECT DISTINCT lesson_number FROM questions WHERE language = ? AND unit = ? ORDER BY lesson_number ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, language);
            pstmt.setInt(2, unit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lessons.add(rs.getInt("lesson_number"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lessons;
    }

    public List<Question> getLessonQuestions(String language, int unit) {
        List<Question> lesson = new ArrayList<>();
        
        String sql = "SELECT * FROM questions WHERE language = ? AND unit = ? ORDER BY RANDOM() LIMIT 3";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, language);
            pstmt.setInt(2, unit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("question_type");
                String prompt = rs.getString("prompt");
                String correct = rs.getString("correct_answer");
                String wrong1 = rs.getString("wrong_option_1");
                String wrong2 = rs.getString("wrong_option_2");

                if ("Translation".equals(type)) {
                    TranslationQuestion q = new TranslationQuestion(prompt, correct, wrong1, wrong2);
                    lesson.add(q);
                } else if ("Audio".equals(type)) {
                    String audioFile = prompt;
                    if (!audioFile.contains("/") && !audioFile.contains("\\")) {
                        audioFile = prompt.toLowerCase().replace(" ", "_") + ".mp3";
                    }
                    AudioQuestion q = new AudioQuestion(prompt, correct, wrong1, wrong2, audioFile);
                    lesson.add(q);
                } else if ("Paragraph".equals(type)) {
                    ParagraphQuestion q = new ParagraphQuestion(prompt, correct);
                    lesson.add(q);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lesson;
    }

    
    public boolean insertQuestion(String language, int unit, int lessonNumber, String type, String prompt, String correct, String wrong1, String wrong2) {
        String sql = "INSERT INTO questions(language, unit, lesson_number, question_type, prompt, correct_answer, wrong_option_1, wrong_option_2) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, language);
            pstmt.setInt(2, unit);
            pstmt.setInt(3, lessonNumber);
            pstmt.setString(4, type);
            pstmt.setString(5, prompt);
            pstmt.setString(6, correct);
            pstmt.setString(7, wrong1);
            pstmt.setString(8, wrong2);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}