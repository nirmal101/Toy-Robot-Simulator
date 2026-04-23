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

    @Test void ignored_at_south_edge() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), SOUTH);
        new MoveCommand().execute(robot, table);
        assertEquals("0,0,SOUTH", robot.report());
    }

    @Test void ignored_at_north_edge() {
        Robot robot = new Robot();
        robot.place(new Position(4, 4), NORTH);
        new MoveCommand().execute(robot, table);
        assertEquals("4,4,NORTH", robot.report());
    }

    @Test void ignored_at_west_edge() {
        Robot robot = new Robot();
        robot.place(new Position(0, 2), WEST);
        new MoveCommand().execute(robot, table);
        assertEquals("0,2,WEST", robot.report());
    }

    @Test void ignored_at_east_edge() {
        Robot robot = new Robot();
        robot.place(new Position(4, 2), EAST);
        new MoveCommand().execute(robot, table);
        assertEquals("4,2,EAST", robot.report());
    }

    @Test void ignored_at_south_edge_non_corner() {
        Robot robot = new Robot();
        robot.place(new Position(2, 0), SOUTH);
        new MoveCommand().execute(robot, table);
        assertEquals("2,0,SOUTH", robot.report());
    }

    @Test void ignored_at_north_edge_non_corner() {
        Robot robot = new Robot();
        robot.place(new Position(2, 4), NORTH);
        new MoveCommand().execute(robot, table);
        assertEquals("2,4,NORTH", robot.report());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        assertTrue(new MoveCommand().execute(robot, table).isEmpty());
    }
}
