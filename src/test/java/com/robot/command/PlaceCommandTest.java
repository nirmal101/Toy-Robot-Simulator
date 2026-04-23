package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class PlaceCommandTest {

    private final Table table = new Table();

    @Test void places_robot_at_valid_position() {
        Robot robot = new Robot();
        new PlaceCommand(new Position(1, 2), NORTH).execute(robot, table);
        assertTrue(robot.isPlaced());
        assertEquals("1,2,NORTH", robot.report());
    }

    @Test void ignores_out_of_bounds_position() {
        Robot robot = new Robot();
        new PlaceCommand(new Position(5, 0), NORTH).execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void ignores_negative_position() {
        Robot robot = new Robot();
        new PlaceCommand(new Position(-1, 0), NORTH).execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        Optional<String> result = new PlaceCommand(new Position(1, 1), NORTH).execute(robot, table);
        assertTrue(result.isEmpty());
    }
}
