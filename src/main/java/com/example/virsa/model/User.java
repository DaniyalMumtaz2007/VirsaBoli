package com.example.virsa.model;

import java.time.LocalDate;

public class User {
    private String username;
    private int hearts;
    private static final int MAX_HEARTS = 5;
    private LocalDate lastLoginDate;
    private int dailyGoalProgress; 

    public User(String username) {
        this.username = username;
        this.hearts = MAX_HEARTS;
        this.lastLoginDate = LocalDate.now();
        this.dailyGoalProgress = 0;
    }

    public void decrementHeart() {
        if (hearts > 0) {
            hearts--;
        }
    }

    public void restoreHearts() {
        this.hearts = MAX_HEARTS;
    }

    public void checkDailyReset() {
        LocalDate today = LocalDate.now();
        if (lastLoginDate != null && lastLoginDate.isBefore(today)) {
            resetDailyQuests();
            lastLoginDate = today;
        }
    }

    private void resetDailyQuests() {
        this.dailyGoalProgress = 0;
        
    }

    
    public int getHearts() { return hearts; }
    public String getUsername() { return username; }
    public int getDailyGoalProgress() { return dailyGoalProgress; }
    public void setDailyGoalProgress(int progress) { this.dailyGoalProgress = progress; }
}
