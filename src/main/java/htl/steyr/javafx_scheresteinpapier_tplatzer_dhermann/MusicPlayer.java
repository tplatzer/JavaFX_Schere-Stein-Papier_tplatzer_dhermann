package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer
{
    public void playMusic(String filePath)
    {
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
