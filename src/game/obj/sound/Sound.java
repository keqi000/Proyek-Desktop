package game.obj.sound;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import game.util.GameSettings;

import javax.sound.sampled.FloatControl;

public class Sound {

    private final URL shoot;
    private final URL hit;
    private final URL destroy;
    private Clip soundShoot;
    private Clip soundHit;
    private Clip soundDestroy;

    public Sound() {
        this.shoot = this.getClass().getClassLoader().getResource("game/obj/sound/shoot.wav");
        this.hit = this.getClass().getClassLoader().getResource("game/obj/sound/hit.wav");
        this.destroy = this.getClass().getClassLoader().getResource("game/obj/sound/destroy.wav");
    }

    public void soundShoot() {
        play(shoot);
    }

    public void soundHit() {
        play(hit);
    }

    public void soundDestroy() {
        play(destroy);
    }

    public void setVolume(float volume) {
        // volume should be between 0.0 and 1.0
        float normalizedVolume = volume / 100f;
        
        // Apply volume to all clips
        if (soundShoot != null) {
            setClipVolume(soundShoot, normalizedVolume);
        }
        if (soundHit != null) {
            setClipVolume(soundHit, normalizedVolume);
        }
        if (soundDestroy != null) {
            setClipVolume(soundDestroy, normalizedVolume);
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            dB = Math.max(dB, gainControl.getMinimum());
            dB = Math.min(dB, gainControl.getMaximum());
            gainControl.setValue(dB);
        }
    }

    private void play(URL url) {
    try {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            }
        });
        audioIn.close();
        
        // Store reference to the clip for volume control
        if (url.equals(shoot)) {
            soundShoot = clip;
        } else if (url.equals(hit)) {
            soundHit = clip;
        } else if (url.equals(destroy)) {
            soundDestroy = clip;
        }
        
        // Apply current volume from game settings before playing
        setVolume(GameSettings.getInstance().getVolume());
        
        clip.start();
    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
        System.err.println(e);
    }
}

}
