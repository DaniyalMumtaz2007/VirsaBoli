package com.example.virsa.model;

public class TranslationQuestion extends Question {
    public TranslationQuestion(String prompt, String correctAnswer, String wrongOption1, String wrongOption2) {
        super(prompt, correctAnswer, wrongOption1, wrongOption2);
    }

    @Override
    public boolean checkAnswer(Object answer) {
        return correctAnswer.equalsIgnoreCase((String) answer);
    }
}
