package gui;

import java.io.*;
import javax.swing.*;
import java.awt.*;

public class SavedState implements Serializable {
    private static final long serialVersionUID = 1L;

    FrameState[] windowStates;

    public SavedState(JInternalFrame[] frames){
        windowStates = new FrameState[frames.length];
        int count = 0;
        for (JInternalFrame frame : frames){
            windowStates[count] = new FrameState(frame.getName(), frame.getLocation(), frame.getSize(), frame.isIcon(), (long) count);
            count++;
        }
    }

    public FrameState[] getWindowStates(){
        return windowStates;
    }

    public void saveFile() throws IOException {
        FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + File.separator + "save.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(this);

        objectOutputStream.close();
    }

    public static SavedState getFile() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + File.separator + "save.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        return (SavedState) objectInputStream.readObject();
    }
}
