package com.example.virsa.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, Media> soundCache = new HashMap<>();
    private static MediaPlayer currentPlayer;

    public static void playSound(String filePath) {
        try {
            
            if (currentPlayer != null) {
                currentPlayer.stop();
            }

            Media sound = soundCache.get(filePath);
            if (sound == null) {
                
                File localFile = new File(filePath);
                if (localFile.isAbsolute() && localFile.exists()) {
                    sound = new Media(localFile.toURI().toString());
                } else {
                    
                    URL resource = SoundManager.class.getResource("/com/example/virsa/sounds/" + filePath);
                    if (resource != null) {
                        sound = new Media(resource.toString());
                    }
                }
                if (sound != null) {
                    soundCache.put(filePath, sound);
                }
            }
            if (sound != null) {
                currentPlayer = new MediaPlayer(sound);
                currentPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + filePath);
            e.printStackTrace();
        }
    }
}
