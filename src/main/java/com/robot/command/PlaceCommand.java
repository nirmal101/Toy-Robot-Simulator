package com.robot.command;

import com.robot.domain.Direction;
import com.robot.domain.Position;
import com.robot.domain.Robot;
import com.robot.domain.Table;
import java.util.Optional;

public class PlaceCommand implements Command {

    private final Position position;
    private final Direction direction;

    public PlaceCommand(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    @Override
    public Optional<String> execute(Robot robot, Table table) {
        if (table.isValidPosition(position)) {
            robot.place(position, direction);
        }
        return Optional.empty();
    }
}
