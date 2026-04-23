package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveCommandTest {

    private final Table table = new Table();

    @Test void moves_robot_forward() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        new MoveCommand().execute(robot, table);
        assertEquals("0,1,NORTH", robot.report());
    }

    @Test void ignored_when_not_placed() {
        Robot robot = new Robot();
        new MoveCommand().execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        assertTrue(new MoveCommand().execute(robot, table).isEmpty());
    }
}
