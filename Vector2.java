package org.cis1200.maze;

public class Vector2 {
    private double x;
    private double y;

    public Vector2() {
        this.x = 0.0d;
        this.y = 0.0d;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 copy() {
        return new Vector2(this.x, this.y);
    }

    public Vector2 rotate(double radians) {
        double sine = Math.sin(radians);
        double cosine = Math.cos(radians);

        Vector2 rotated = this.copy();
        rotated.x = (cosine * this.x) - (sine * this.y);
        rotated.y = (sine * this.x) + (cosine * this.y);

        return rotated;
    }

    public Vector2 scale(double scalar) {
        Vector2 scaled = this.copy();

        scaled.x *= scalar;
        scaled.y *= scalar;

        return scaled;
    }

    public Vector2 add(Vector2 other) {
        Vector2 added = this.copy();

        added.x += other.x;
        added.y += other.y;

        return added;
    }

    public double distance(Vector2 other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Vector2)) {
            return false;
        }

        Vector2 vector2 = (Vector2) object;

        return this.x == vector2.x && this.y == vector2.y;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
