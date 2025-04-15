package gui;
import java.awt.*;
import java.io.Serializable;

public class FrameState implements Serializable {
    private final String name;
    private final int locationX;
    private final int locationY;
    private final boolean maximized;

    public FrameState(String name, Point location, boolean maximized){
        this.name = name;
        this.locationX = location.x;
        this.locationY = location.y;
        this.maximized = maximized;
    }

    public String getName(){
        return name;
    }

    public Point getLocation(){
        return new Point(locationX, locationY);
    }

    public boolean getMaximized(){
        return maximized;
    }
}
