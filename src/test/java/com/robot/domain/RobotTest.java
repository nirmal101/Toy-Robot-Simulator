package com.robot.domain;

import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class RobotTest {

    private final Table table = new Table();

    @Test void robot_is_not_placed_initially() {
        assertFalse(new Robot().isPlaced());
    }

    @Test void robot_is_placed_after_place() {
        Robot robot = new Robot();
        robot.place(new Position(1, 2), NORTH);
        assertTrue(robot.isPlaced());
    }

    @Test void report_reflects_position_and_direction() {
        Robot robot = new Robot();
        robot.place(new Position(1, 2), NORTH);
        assertEquals("1,2,NORTH", robot.report());
    }

    @Test void move_updates_position() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.move(table);
        assertEquals("0,1,NORTH", robot.report());
    }

    @Test void move_ignored_when_not_placed() {
        Robot robot = new Robot();
        robot.move(table);
        assertFalse(robot.isPlaced());
    }

    @Test void move_ignored_when_it_would_fall_off_table() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), SOUTH);
        robot.move(table);
        assertEquals("0,0,SOUTH", robot.report());
    }

    @Test void rotate_left_updates_direction() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.rotateLeft();
        assertEquals("0,0,WEST", robot.report());
    }

    @Test void rotate_right_updates_direction() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.rotateRight();
        assertEquals("0,0,EAST", robot.report());
    }

    @Test void second_place_replaces_first() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.place(new Position(3, 3), EAST);
        assertEquals("3,3,EAST", robot.report());
    }
}
