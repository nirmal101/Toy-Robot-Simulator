package com.robot.domain;

public class Robot {

    private Position position;
    private Direction direction;

    public void place(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public void move(Table table) {
        if (!isPlaced()) return;
        Position next = position.move(direction);
        if (table.isValidPosition(next)) {
            position = next;
        }
    }

    public void rotateLeft() {
        if (!isPlaced()) return;
        direction = direction.rotateLeft();
    }

    public void rotateRight() {
        if (!isPlaced()) return;
        direction = direction.rotateRight();
    }

    public String report() {
        if (!isPlaced()) throw new IllegalStateException("Robot is not placed");
        return position.x() + "," + position.y() + "," + direction;
    }

    public boolean isPlaced() {
        return position != null;
    }
}
