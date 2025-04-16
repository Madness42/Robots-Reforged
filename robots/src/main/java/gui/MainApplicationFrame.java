package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import enums.ConfirmInput;
import log.Logger;
import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Objects;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private SavedState savedState;

    public MainApplicationFrame() {
        try {
            this.savedState = SavedState.getFile();
        }
        catch (IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = createGameWindow();
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    protected GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setName("Game");
        gameWindow.setLocation(100, 10);
        gameWindow.setSize(400, 400);

        if (savedState != null){
            for (FrameState frameState : savedState.getWindowStates()){
                if (Objects.equals(frameState.getName(), gameWindow.getName())){
                    try{
                        gameWindow.setIcon(frameState.getMaximized());
                    }
                    catch (PropertyVetoException e){
                        System.out.println(e.getMessage());
                    }
                    gameWindow.setLocation(frameState.getLocation());
                    gameWindow.setSize(frameState.getSize());
                }
            }
        }

        setMinimumSize(gameWindow.getSize());
        return gameWindow;
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setName("Logger");
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);

        if (savedState != null){
            for (FrameState frameState : savedState.getWindowStates()){
                if (Objects.equals(frameState.getName(), logWindow.getName())){
                    try{
                        logWindow.setIcon(frameState.getMaximized());
                    }
                    catch (PropertyVetoException e){
                        System.out.println(e.getMessage());
                    }
                    logWindow.setLocation(frameState.getLocation());
                    logWindow.setSize(frameState.getSize());
                }
            }
        }

        setMinimumSize(logWindow.getSize());
        //logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    private void approveExitMenu()
    {
        ConfirmInput answer = getSelectedConfirmInput();
        if(answer == ConfirmInput.YES)
        {
            SavedState savedState = new SavedState(desktopPane.getAllFrames());
            try{
                savedState.saveFile();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            System.exit(0);
        }
    }

    private static ConfirmInput getSelectedConfirmInput() {
        String[] titles = ConfirmInput.getTitles();
        int selectedIndex = JOptionPane.showOptionDialog(null,
                "Вы уверены, что хотите выйти?", "Выход", JOptionPane.DEFAULT_OPTION,
                JOptionPane.DEFAULT_OPTION, null, titles,
                ConfirmInput.YES.getTitle());

        return ConfirmInput.values()[selectedIndex];
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = createFileMenu();
        JMenu lookAndFeelMenu = createLookAndFeelMenu();
        JMenu testMenu = createTestMenu();

        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_V);
        fileMenu.getAccessibleContext().setAccessibleDescription("Управление программой");

        fileMenu.add(createMenuItem("Выход", KeyEvent.VK_S, event -> approveExitMenu()));

        return fileMenu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

        lookAndFeelMenu.add(createMenuItem("Системная схема", KeyEvent.VK_S, event -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        }));

        lookAndFeelMenu.add(createMenuItem("Универсальная схема", KeyEvent.VK_S, event -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        }));

        return lookAndFeelMenu;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        testMenu.add(createMenuItem("Сообщение в лог", KeyEvent.VK_S, event -> Logger.debug("Новая строка")));

        return testMenu;
    }

    private JMenuItem createMenuItem(String title, int mnemonic, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(title, mnemonic);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
