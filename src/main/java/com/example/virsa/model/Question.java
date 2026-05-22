package com.example.virsa.model;

public abstract class Question {
    protected String prompt;
    protected String correctAnswer;
    protected String wrongOption1;
    protected String wrongOption2;

    public Question(String prompt, String correctAnswer, String wrongOption1, String wrongOption2) {
        this.prompt = prompt;
        this.correctAnswer = correctAnswer;
        this.wrongOption1 = wrongOption1;
        this.wrongOption2 = wrongOption2;
    }

    public abstract boolean checkAnswer(Object answer);

    public String getPrompt() { return prompt; }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getWrongOption1() { return wrongOption1; }
    public String getWrongOption2() { return wrongOption2; }
}
