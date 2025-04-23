package robots;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Cross implements Robot {
    private final double maxVelocity;
    private double x;
    private double y;
    private double direction;
    private Point targetPosition;

    public Cross() {
        this.maxVelocity = 0.5;
        this.x = 100;
        this.y = 100;
    }

    @Override
    public void move(Point target, double duration) {
        targetPosition = target;
        direction = 0;
        double epsilon = 0.0001;

        if (x > targetPosition.x + epsilon)
        {
            x = newCoordinate(x, duration, -1);
        }
        else if (x < targetPosition.x - epsilon)
        {
            x = newCoordinate(x, duration, 1);
        }
        else if (y > targetPosition.y + epsilon)
        {
            y = newCoordinate(y, duration, -1);
        }
        else if (y < targetPosition.y - epsilon)
        {
            y = newCoordinate(y, duration, 1);
        }
    }
    private double newCoordinate(double coordinate, double duration, int direction)
    {
        double newCoordinate = coordinate + this.maxVelocity * direction;
        if (!Double.isFinite(newCoordinate)) {
            newCoordinate = coordinate + this.maxVelocity * duration * direction;
        }
        return newCoordinate;
    }

    @Override
    public void draw(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);

        g.setColor(Color.BLACK);
        g.fillRect(x - 20, y - 7, 40, 14);
        g.fillRect(x - 7, y - 20, 14, 40);

        g.setColor(Color.YELLOW);
        g.fillRect(x - 20, y - 5, 40, 10);
        g.fillRect(x - 5, y - 20, 10, 40);
    }

    @Override
    public double getPositionX() { return x; }
    @Override
    public double getPositionY() { return y; }
    @Override
    public double getDirection() { return direction; }
    @Override
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    @Override
    public void setDirection(double direction) { this.direction = direction; }

    private double limit(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    private double calculateVelocity() {
        return maxVelocity;
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2*Math.PI;
        while (angle >= 2*Math.PI) angle -= 2*Math.PI;
        return angle;
    }
}