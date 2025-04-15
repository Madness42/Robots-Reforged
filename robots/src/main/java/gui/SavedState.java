package gui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;

public class SavedState implements Serializable {
    private static final long serialVersionUID = 1L;

    private FrameState[] windowStates;

    public SavedState(JInternalFrame[] frames){
        windowStates = new FrameState[frames.length];
        int count = 0;
        for (JInternalFrame frame : frames){
            windowStates[count] = new FrameState(frame.getName(), frame.getLocation(), frame.isMaximum());
            System.out.println(frame.getName());
            count++;
        }
    }

    public void saveFile() throws IOException {
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Username\\Desktop\\save.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // сохраняем игру в файл
        objectOutputStream.writeObject(this);

        //закрываем поток и освобождаем ресурсы
        objectOutputStream.close();
    }
}
