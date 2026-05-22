package com.example.virsa.model;

public class AudioQuestion extends Question {
    private String audioFilePath;

    public AudioQuestion(String prompt, String correctAnswer, String wrongOption1, String wrongOption2, String audioFilePath) {
        super(prompt, correctAnswer, wrongOption1, wrongOption2);
        this.audioFilePath = audioFilePath;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    @Override
    public boolean checkAnswer(Object answer) {
        return correctAnswer.equalsIgnoreCase((String) answer);
    }
}