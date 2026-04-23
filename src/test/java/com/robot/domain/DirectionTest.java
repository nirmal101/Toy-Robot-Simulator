package com.robot.domain;

import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectionTest {

    @Test void north_rotates_left_to_west()  { assertEquals(WEST,  NORTH.rotateLeft()); }
    @Test void west_rotates_left_to_south()  { assertEquals(SOUTH, WEST.rotateLeft());  }
    @Test void south_rotates_left_to_east()  { assertEquals(EAST,  SOUTH.rotateLeft()); }
    @Test void east_rotates_left_to_north()  { assertEquals(NORTH, EAST.rotateLeft());  }

    @Test void north_rotates_right_to_east() { assertEquals(EAST,  NORTH.rotateRight()); }
    @Test void east_rotates_right_to_south() { assertEquals(SOUTH, EAST.rotateRight());  }
    @Test void south_rotates_right_to_west() { assertEquals(WEST,  SOUTH.rotateRight()); }
    @Test void west_rotates_right_to_north() { assertEquals(NORTH, WEST.rotateRight());  }

    @Test void full_left_rotation_cycle_returns_to_origin() {
        Direction d = NORTH;
        for (int i = 0; i < 4; i++) d = d.rotateLeft();
        assertEquals(NORTH, d);
    }

    @Test void full_right_rotation_cycle_returns_to_origin() {
        Direction d = NORTH;
        for (int i = 0; i < 4; i++) d = d.rotateRight();
        assertEquals(NORTH, d);
    }
}
