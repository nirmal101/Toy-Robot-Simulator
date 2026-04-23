package com.robot.command;

import com.robot.domain.Robot;
import com.robot.domain.Table;
import java.util.Optional;

public interface Command {
    Optional<String> execute(Robot robot, Table table);
}
