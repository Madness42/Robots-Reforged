package gui;

import audio.AudioPlayer;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        setJMenuBar(m_visualizer.getRobotMenuBar());

        AudioPlayer audio = AudioPlayer.getInstance();
        addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameClosing(InternalFrameEvent e) {
                audio.playSound("nextLevel.wav", false);
            }
            public void internalFrameIconified(InternalFrameEvent e){
                audio.playSound("Restart.wav", false);
            }
            public void internalFrameDeiconified(InternalFrameEvent e){
                audio.playSound("Restart.wav", false);
            }
        });
        pack();
    }
}
