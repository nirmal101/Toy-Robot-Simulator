package com.robot.parser;

import com.robot.command.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test void parses_move()   { assertInstanceOf(MoveCommand.class,   parser.parse("MOVE").orElseThrow()); }
    @Test void parses_left()   { assertInstanceOf(LeftCommand.class,   parser.parse("LEFT").orElseThrow()); }
    @Test void parses_right()  { assertInstanceOf(RightCommand.class,  parser.parse("RIGHT").orElseThrow()); }
    @Test void parses_report() { assertInstanceOf(ReportCommand.class, parser.parse("REPORT").orElseThrow()); }
    @Test void parses_valid_place() { assertInstanceOf(PlaceCommand.class, parser.parse("PLACE 1,2,NORTH").orElseThrow()); }

    @Test void trims_leading_and_trailing_whitespace() {
        assertInstanceOf(MoveCommand.class, parser.parse("  MOVE  ").orElseThrow());
    }

    @Test void returns_empty_for_unknown_command() {
        assertTrue(parser.parse("JUMP").isEmpty());
    }

    @Test void returns_empty_for_empty_line() {
        assertTrue(parser.parse("").isEmpty());
    }

    @Test void returns_empty_for_whitespace_only_line() {
        assertTrue(parser.parse("   ").isEmpty());
    }

    @Test void returns_empty_for_place_with_invalid_direction() {
        assertTrue(parser.parse("PLACE 1,2,NORTHWEST").isEmpty());
    }

    @Test void returns_empty_for_place_with_non_numeric_coords() {
        assertTrue(parser.parse("PLACE x,y,NORTH").isEmpty());
    }

    @Test void returns_empty_for_place_with_wrong_arg_count() {
        assertTrue(parser.parse("PLACE 1").isEmpty());
    }

    @Test void returns_empty_for_place_with_spaces_in_args() {
        assertTrue(parser.parse("PLACE 1, 2, NORTH").isEmpty());
    }

    @Test void returns_empty_for_null_input() {
        assertTrue(parser.parse(null).isEmpty());
    }
}
