package com.robot.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    private final Table table = new Table();

    @Test void origin_is_valid()     { assertTrue(table.isValidPosition(new Position(0, 0))); }
    @Test void max_corner_is_valid() { assertTrue(table.isValidPosition(new Position(4, 4))); }
    @Test void centre_is_valid()     { assertTrue(table.isValidPosition(new Position(2, 2))); }

    @Test void negative_x_is_invalid()  { assertFalse(table.isValidPosition(new Position(-1, 0))); }
    @Test void negative_y_is_invalid()  { assertFalse(table.isValidPosition(new Position(0, -1))); }
    @Test void x_of_five_is_invalid()   { assertFalse(table.isValidPosition(new Position(5, 0))); }
    @Test void y_of_five_is_invalid()   { assertFalse(table.isValidPosition(new Position(0, 5))); }
}
