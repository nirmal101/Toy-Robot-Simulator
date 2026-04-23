package com.robot.domain;

public class Table {

    public boolean isValidPosition(Position pos) {
        return pos.x() >= 0 && pos.x() < 5
            && pos.y() >= 0 && pos.y() < 5;
    }
}
