package com.example.virsa.model;

import java.util.ArrayList;
import java.util.List;

public class UserSession {
    
    private static UserSession instance;
    private boolean isDarkMode = false;

    
    private int userId;
    private String username;
    private String email;
    private List<String> activeLanguages;
    private int hearts = 5;
    private int xp = 0;
    private int streak = 0;
    private String currentLearningLanguage;
    private int currentLessonLevel = 1;
    private boolean isLearner;
    private boolean isContributor;

    
    private UserSession() {
        activeLanguages = new ArrayList<>();
    }

    public int getHearts() { return hearts; }
    public void setHearts(int hearts) { this.hearts = hearts; }
    public void decrementHeart() { if(this.hearts > 0) this.hearts--; }

    public int getXp() { return xp; }
    public void addXp(int amount) { this.xp += amount; }

    public int getStreak() { return streak; }
    public void incrementStreak() { this.streak++; }
    public String getCurrentLearningLanguage() {
        return currentLearningLanguage;
    }

    public void setCurrentLearningLanguage(String language) {
        if (activeLanguages.contains(language)) {
            this.currentLearningLanguage = language;
        }
    }

    public int getCurrentLessonLevel() { return currentLessonLevel; }
    public void setCurrentLessonLevel(int currentLessonLevel) { this.currentLessonLevel = currentLessonLevel; }

    public boolean isLearner() { return isLearner; }
    public void setIsLearner(boolean isLearner) { this.isLearner = isLearner; }

    public boolean isContributor() { return isContributor; }
    public void setIsContributor(boolean isContributor) { this.isContributor = isContributor; }

    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    
    public void login(String username, String email) {
        this.username = username;
        this.email = email;
        this.activeLanguages.clear();
        this.currentLearningLanguage = null;
    }

    public void logout() {
        this.userId = 0;
        this.username = null;
        this.email = null;
        this.activeLanguages.clear();
        this.currentLearningLanguage = null;
    }

    
    public void addLanguage(String language) {
        if (!activeLanguages.contains(language)) {
            activeLanguages.add(language);
        }
    }

    public void removeLanguage(String language) {
        activeLanguages.remove(language);
    }

    
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getActiveLanguages() { return activeLanguages; }

    
    public String getColorForLanguage(String lang) {
        switch (lang) {
            case "Punjabi": return "#FF9600";
            case "Sindhi": return "#1CB0F6";
            case "Pashto": return "#CE82FF";
            case "Balochi": return "#FF4B4B";
            default: return "#AFBFC9";
        }
    }

    public void setUserId(int id) { this.userId=id;
    }

    public int getUserId() {
        return userId;
    }

    public void setXp(int xp) { this.xp =xp;
    }

    public void setStreak(int streak) { this.streak =streak;
    }
    public boolean isDarkMode() { return isDarkMode; }
    public void setDarkMode(boolean darkMode) { this.isDarkMode = darkMode; }
}