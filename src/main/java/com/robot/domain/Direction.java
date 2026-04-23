package com.robot.domain;

public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public Direction rotateLeft() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST  -> SOUTH;
            case SOUTH -> EAST;
            case EAST  -> NORTH;
        };
    }

    public Direction rotateRight() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST  -> SOUTH;
            case SOUTH -> WEST;
            case WEST  -> NORTH;
        };
    }
}
