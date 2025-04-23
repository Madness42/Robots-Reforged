package robots;

import java.awt.*;

public interface Robot {
    void move(Point target, double duration);
    void draw(Graphics2D g, int x, int y, double direction);
    double getPositionX();
    double getPositionY();
    double getDirection();
    void setPosition(double x, double y);
    void setDirection(double direction);
}