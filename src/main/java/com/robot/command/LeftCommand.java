package com.robot.command;

import com.robot.domain.Robot;
import com.robot.domain.Table;
import java.util.Optional;

public class LeftCommand implements Command {

    @Override
    public Optional<String> execute(Robot robot, Table table) {
        if (robot.isPlaced()) {
            robot.rotateLeft();
        }
        return Optional.empty();
    }
}
