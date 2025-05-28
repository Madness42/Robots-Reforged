package audio;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import audio.AudioPlayer;
import javax.swing.JInternalFrame;

public class AudioHandler extends InternalFrameAdapter {
    public static String FRAME_CLOSE_SOUND = "heavy_click.wav";
    public static String FRAME_ICONIFIED_SOUND = "blink.wav";
    public static String FRAME_DEICONIFIED_SOUND = "blink.wav";
    public static String CAR_SOUND = "step.wav";
    public static String MUSIC = "Music.wav";

    public static void addWindowSounds(JInternalFrame frame) {
        AudioPlayer audio = AudioPlayer.getInstance();

        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                audio.playSound(FRAME_CLOSE_SOUND, false);
            }

            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                audio.playSound(FRAME_ICONIFIED_SOUND, false);
            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                audio.playSound(FRAME_DEICONIFIED_SOUND, false);
            }
        });
    }
}