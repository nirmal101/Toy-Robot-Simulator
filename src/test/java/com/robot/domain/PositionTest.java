package com.robot.domain;

import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionTest {

    @Test void move_north_increments_y() {
        assertEquals(new Position(2, 3), new Position(2, 2).move(NORTH));
    }

    @Test void move_south_decrements_y() {
        assertEquals(new Position(2, 1), new Position(2, 2).move(SOUTH));
    }

    @Test void move_east_increments_x() {
        assertEquals(new Position(3, 2), new Position(2, 2).move(EAST));
    }

    @Test void move_west_decrements_x() {
        assertEquals(new Position(1, 2), new Position(2, 2).move(WEST));
    }

    @Test void move_returns_new_position_leaving_original_unchanged() {
        Position original = new Position(2, 2);
        Position moved = original.move(NORTH);
        assertEquals(new Position(2, 2), original);
        assertEquals(new Position(2, 3), moved);
    }
}
