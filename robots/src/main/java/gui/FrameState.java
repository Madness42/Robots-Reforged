package gui;
import java.awt.*;
import java.io.Serializable;

public class FrameState implements Serializable {
    private final long serialVersionUID;
    private final String name;
    private final int locationX;
    private final int locationY;
    private final int width;
    private final int height;
    private final boolean maximized;

    public FrameState(String name, Point location, Dimension size, boolean maximized, long versionID){
        this.serialVersionUID = versionID;
        this.name = name;
        this.locationX = location.x;
        this.locationY = location.y;
        this.width = size.width;
        this.height = size.height;
        this.maximized = maximized;
    }

    public String getName(){
        return name;
    }

    public Point getLocation(){
        return new Point(locationX, locationY);
    }

    public Dimension getSize(){
        return new Dimension(width, height);
    }

    public boolean getMaximized(){
        return maximized;
    }
}
