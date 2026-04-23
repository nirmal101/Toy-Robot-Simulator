package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class RightCommandTest {

    private final Table table = new Table();

    @Test void rotates_robot_right() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        new RightCommand().execute(robot, table);
        assertEquals("0,0,EAST", robot.report());
    }

    @Test void ignored_when_not_placed() {
        Robot robot = new Robot();
        new RightCommand().execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        assertTrue(new RightCommand().execute(robot, table).isEmpty());
    }

}
