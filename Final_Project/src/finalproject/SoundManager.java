package finalproject;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    public static void playSound(String fileName) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL soundURL = SoundManager.class.getResource("/" + fileName); 
                    
                    if (soundURL == null) {
                        soundURL = SoundManager.class.getResource(fileName);
                    }

                    if (soundURL == null) {
                        System.err.println("ERROR: Sound file not found: " + fileName);
                        return;
                    }

                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();
                    
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}