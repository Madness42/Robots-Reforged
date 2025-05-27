package audio;

import log.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayer {
    private static AudioPlayer instance;

    Clip music;
    ArrayList<Clip> sounds;

    float musicVolume = 1;
    float soundVolume = 1;

    public static AudioPlayer getInstance(){
        if(instance == null){
            instance = new AudioPlayer();
        }
        return instance;
    }

    public AudioPlayer(){
        playMusic(AudioHandler.MUSIC);
        this.sounds = new ArrayList<>();
    }

    public void playMusic(String name){
        Clip clip = null;
        try {
            clip = getClip(name);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            return;
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
        music = clip;
    }

    public void playSound(String name, boolean loop){
        Clip clip = null;
        try {
            clip = getClip(name);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            return;
        }
        if(loop){
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            sounds.add(clip);
        }
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(soundVolume));
        clip.start();
    }

    public void stopSounds(){
        for (int i = 0; i < sounds.size(); i++) {
            sounds.get(i).stop();
        }
        sounds.clear();
    }

    public void setMusicVolume(float volume){
        musicVolume = Math.min(1, Math.max(0, volume));
        FloatControl gainControl = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(musicVolume));
    }

    public void setSoundVolume(float volume){
        soundVolume = Math.min(1, Math.max(0, volume));
    }

    private Clip getClip(String name) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        try {
            name = "robots/src/main/resourses/audio/" + name;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(name).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;

        } catch (UnsupportedAudioFileException e) {
            Logger.error("Формат аудио файла должен быть .wav, .aiff или .au: " + e);
            throw e;
        } catch (LineUnavailableException | IOException e) {
            Logger.error(e.toString());
            throw e;
        }
    }
}
