package com.robot.command;

import com.robot.domain.Robot;
import com.robot.domain.Table;
import java.util.Optional;

public class ReportCommand implements Command {

    @Override
    public Optional<String> execute(Robot robot, Table table) {
        if (!robot.isPlaced()) return Optional.empty();
        return Optional.of(robot.report());
    }
}
