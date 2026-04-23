package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class LeftCommandTest {

    private final Table table = new Table();

    @Test void rotates_robot_left() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        new LeftCommand().execute(robot, table);
        assertEquals("0,0,WEST", robot.report());
    }

    @Test void ignored_when_not_placed() {
        Robot robot = new Robot();
        new LeftCommand().execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        assertTrue(new LeftCommand().execute(robot, table).isEmpty());
    }

}
