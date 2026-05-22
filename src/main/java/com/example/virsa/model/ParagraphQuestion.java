package com.example.virsa.model;

public class ParagraphQuestion extends Question {
    public ParagraphQuestion(String prompt, String correctAnswer) {
        
        super(prompt, correctAnswer, "", "");
    }

    @Override
    public boolean checkAnswer(Object answer) {
        return correctAnswer.trim().equalsIgnoreCase(((String) answer).trim());
    }
}
