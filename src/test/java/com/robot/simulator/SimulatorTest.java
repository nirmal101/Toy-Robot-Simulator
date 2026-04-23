package com.robot.simulator;

import org.junit.jupiter.api.Test;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorTest {

    // --- Spec examples ---

    @Test void spec_example_a() throws Exception {
        assertEquals(List.of("0,1,NORTH"), run(resourcePath("test_a.txt")));
    }

    @Test void spec_example_b() throws Exception {
        assertEquals(List.of("0,0,WEST"), run(resourcePath("test_b.txt")));
    }

    @Test void spec_example_c() throws Exception {
        assertEquals(List.of("3,3,NORTH"), run(resourcePath("test_c.txt")));
    }

    // --- Edge cases ---

    @Test void commands_before_first_place_are_ignored() {
        List<String> output = new Simulator().run(List.of(
            "MOVE", "LEFT", "RIGHT", "REPORT",
            "PLACE 0,0,NORTH",
            "REPORT"
        ));
        assertEquals(List.of("0,0,NORTH"), output);
    }

    @Test void place_ignored_when_out_of_bounds() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 5,5,NORTH",
            "REPORT"
        ));
        assertTrue(output.isEmpty());
    }

    @Test void move_ignored_at_south_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,0,SOUTH",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("0,0,SOUTH"), output);
    }

    @Test void move_ignored_at_north_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 4,4,NORTH",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("4,4,NORTH"), output);
    }

    @Test void move_ignored_at_west_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,2,WEST",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("0,2,WEST"), output);
    }

    @Test void move_ignored_at_east_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 4,2,EAST",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("4,2,EAST"), output);
    }

    @Test void second_place_replaces_first() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,0,NORTH",
            "MOVE",
            "PLACE 3,3,EAST",
            "REPORT"
        ));
        assertEquals(List.of("3,3,EAST"), output);
    }

    @Test void empty_and_unknown_lines_are_skipped() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,0,NORTH",
            "",
            "JUMP",
            "REPORT"
        ));
        assertEquals(List.of("0,0,NORTH"), output);
    }

    @Test void malformed_place_lines_are_skipped() {
        List<String> output = new Simulator().run(List.of(
            "PLACE x,y,NORTH",
            "PLACE 1",
            "PLACE 0,0,NORTH",
            "REPORT"
        ));
        assertEquals(List.of("0,0,NORTH"), output);
    }

    @Test void invalid_second_place_does_not_alter_robot_state() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 2,2,EAST",
            "PLACE 5,5,NORTH",
            "REPORT"
        ));
        assertEquals(List.of("2,2,EAST"), output);
    }

    // --- Helpers ---

    private List<String> run(Path path) throws Exception {
        return new Simulator().run(path);
    }

    private Path resourcePath(String filename) throws URISyntaxException {
        return Path.of(getClass().getClassLoader().getResource(filename).toURI());
    }
}
