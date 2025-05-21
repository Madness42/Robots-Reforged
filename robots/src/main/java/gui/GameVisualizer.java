package gui;

import audio.AudioPlayer;
import robots.Robot;
import robots.DefaultRobot;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

public class GameVisualizer extends JPanel {
    private static final int REPAINT_DELAY_MS = 50;
    private static final int MODEL_UPDATE_DELAY_MS = 10;

    private final Timer repaintTimer;
    private final Timer modelUpdateTimer;
    private final JMenuBar robotMenuBar;

    private Robot robot;
    private Point targetPosition;

    private final AudioPlayer audio;
    private boolean isCarSoundPlaying;

    public GameVisualizer() {
        this.audio = AudioPlayer.getInstance();
        this.robot = createDefaultRobot();
        this.targetPosition = new Point(150, 100);
        this.repaintTimer = createTimer("Repaint Timer", REPAINT_DELAY_MS, this::onRedrawEvent);
        this.modelUpdateTimer = createTimer("Model Update Timer", MODEL_UPDATE_DELAY_MS, this::onModelUpdateEvent);
        this.robotMenuBar = createRobotMenuBar();


        initializeUI();
        startTimers();
    }

    private Robot createDefaultRobot() {
        try {
            return (Robot) Class.forName("robots.DefaultRobot").newInstance();
        } catch (Exception e) {
            return new DefaultRobot();
        }
    }

    private Timer createTimer(String name, int delay, Runnable action) {
        Timer timer = new Timer(name, true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        }, 0, delay);
        return timer;
    }

    private JMenuBar createRobotMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu robotMenu = new JMenu("Роботы");
        JMenuItem loadRobotItem = new JMenuItem("Загрузить робота...");
        loadRobotItem.addActionListener(this::handleLoadRobotAction);
        robotMenu.add(loadRobotItem);
        menuBar.add(robotMenu);
        return menuBar;
    }

    private void initializeUI() {
        setDoubleBuffered(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
            }
        });
    }

    private void startTimers() {
        repaintTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, REPAINT_DELAY_MS);

        modelUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, MODEL_UPDATE_DELAY_MS);
    }

    public JMenuBar getRobotMenuBar() {
        return robotMenuBar;
    }

    protected void setTargetPosition(Point p) {
        targetPosition = new Point(p);
        repaint();
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent() {
        if (shouldStopMoving()) {
            handleCarSound(false);
            return;
        }
        handleCarSound(true);

        robot.move(targetPosition, MODEL_UPDATE_DELAY_MS);
    }

    private void handleCarSound(boolean isMoving){
        if(isCarSoundPlaying == isMoving){
            return;
        }
        if(isMoving){
            audio.playSound("step.wav", true);
            isCarSoundPlaying = true;
            return;
        }
        audio.stopSounds();
        isCarSoundPlaying = false;
    }

    private boolean shouldStopMoving() {
        return distance(robot.getPositionX(), robot.getPositionY(),
                targetPosition.x, targetPosition.y) < 0.5;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawRobot(g2d);
        drawTarget(g2d);
    }

    private void drawRobot(Graphics2D g2d) {
        robot.draw(g2d,
                round(robot.getPositionX()),
                round(robot.getPositionY()),
                robot.getDirection());
    }

    private void drawTarget(Graphics2D g2d) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g2d.setTransform(t);
        g2d.setColor(Color.GREEN);
        fillOval(g2d, targetPosition.x, targetPosition.y, 5, 5);
        g2d.setColor(Color.BLACK);
        drawOval(g2d, targetPosition.x, targetPosition.y, 5, 5);
    }

    private void handleLoadRobotAction(ActionEvent e) {
        File jarFile = selectJarFile();
        if (jarFile != null) {
            loadRobotFromJar(jarFile);
        }
    }

    private File selectJarFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new JarFileFilter());

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void loadRobotFromJar(File jarFile) {
        try {
            JarFile jar = new JarFile(jarFile);
            URLClassLoader classLoader = createClassLoader(jarFile);

            Robot newRobot = findRobotInJar(jar, classLoader);
            if (newRobot != null) {
                updateRobot(newRobot);
            } else {
                showRobotNotFoundError();
            }
        } catch (Exception ex) {
            showLoadingError(ex);
        }
    }

    private URLClassLoader createClassLoader(File jarFile) throws Exception {
        return new URLClassLoader(
                new URL[] { jarFile.toURI().toURL() },
                getClass().getClassLoader()
        );
    }

    private Robot findRobotInJar(JarFile jar, URLClassLoader classLoader) throws Exception {
        for (JarEntry entry : jar.stream().toArray(JarEntry[]::new)) {
            if (isClassFile(entry)) {
                Class<?> cls = loadClass(entry, classLoader);
                if (isRobotClass(cls)) {
                    return (Robot) cls.newInstance();
                }
            }
        }
        return null;
    }

    private boolean isClassFile(JarEntry entry) {
        return entry.getName().endsWith(".class");
    }

    private Class<?> loadClass(JarEntry entry, URLClassLoader classLoader) throws Exception {
        String className = entry.getName()
                .replace("/", ".")
                .replace(".class", "");
        return classLoader.loadClass(className);
    }

    private boolean isRobotClass(Class<?> cls) {
        return Robot.class.isAssignableFrom(cls) && !cls.isInterface();
    }

    private void updateRobot(Robot newRobot) {
        newRobot.setPosition(robot.getPositionX(), robot.getPositionY());
        newRobot.setDirection(robot.getDirection());
        this.robot = newRobot;
        repaint();
    }

    private void showRobotNotFoundError() {
        JOptionPane.showMessageDialog(this,
                "Не найдено классов роботов в выбранном JAR-файле",
                "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void showLoadingError(Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Ошибка загрузки робота: " + ex.getMessage(),
                "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2*Math.PI;
        while (angle >= 2*Math.PI) angle -= 2*Math.PI;
        return angle;
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static class JarFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".jar") || f.isDirectory();
        }

        public String getDescription() {
            return "JAR файлы (*.jar)";
        }
    }


}