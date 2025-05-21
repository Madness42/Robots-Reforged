package robots;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class WheeledRobot implements Robot {
    private final double maxVelocity;
    private final double maxAngularVelocity;
    private double x;
    private double y;
    private double direction;
    private Point targetPosition;

    public WheeledRobot() {
        this.maxVelocity = 0.05;
        this.maxAngularVelocity = 0.005;
        this.x = 100;
        this.y = 100;
        this.direction = 0;
    }

    @Override
    public void move(Point target, double duration) {
        targetPosition = target;
        double velocity = calculateVelocity();
        double angularVelocity = calculateAngularVelocity();
        velocity = limit(velocity, 0, maxVelocity);
        angularVelocity = limit(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX = x + velocity / angularVelocity *
                (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
        if (!Double.isFinite(newX)) {
            newX = x + velocity * duration * Math.cos(direction);
        }

        double newY = y - velocity / angularVelocity *
                (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        if (!Double.isFinite(newY)) {
            newY = y + velocity * duration * Math.sin(direction);
        }

        x = newX;
        y = newY;
        direction = asNormalizedRadians(direction + angularVelocity * duration);
    }

    @Override
    public void draw(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);

        g.setColor(Color.RED);
        g.fillRect(x - 15, y - 10, 30, 20);

        g.setColor(Color.BLACK);
        g.fillOval(x - 15, y - 15, 10, 10);
        g.fillOval(x + 5, y - 15, 10, 10);
        g.fillOval(x - 15, y + 5, 10, 10);
        g.fillOval(x + 5, y + 5, 10, 10);
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

    private double calculateAngularVelocity() {
        double angleToTarget = angleTo(getPositionX(), getPositionY(),
                targetPosition.x, targetPosition.y);
        double direction = getDirection();

        if (angleToTarget > direction) {
            return maxAngularVelocity;
        } else if (angleToTarget < direction) {
            return -maxAngularVelocity;
        }
        return 0;
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