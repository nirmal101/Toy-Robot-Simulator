package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class ReportCommandTest {

    private final Table table = new Table();

    @Test void returns_report_string_when_placed() {
        Robot robot = new Robot();
        robot.place(new Position(1, 2), NORTH);
        assertEquals(Optional.of("1,2,NORTH"), new ReportCommand().execute(robot, table));
    }

    @Test void returns_empty_when_not_placed() {
        Robot robot = new Robot();
        assertTrue(new ReportCommand().execute(robot, table).isEmpty());
    }
}
