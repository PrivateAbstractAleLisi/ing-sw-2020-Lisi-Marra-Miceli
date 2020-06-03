package it.polimi.ingsw.psp58.view.UI.GUI;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SantoriniAudioPlayer {

    Clip clip;
    AudioInputStream audioInputStream;



    public void loadSound(String url) throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        audioInputStream =
                AudioSystem.getAudioInputStream(new File(url).getAbsoluteFile());

        // create clip reference
        clip = AudioSystem.getClip();

        // open audioInputStream to the clip
        clip.open(audioInputStream);

    }

    public void play() {
        clip.start();
    }
}
