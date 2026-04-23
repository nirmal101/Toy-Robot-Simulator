package com.robot.parser;

import com.robot.command.Command;
import com.robot.command.LeftCommand;
import com.robot.command.MoveCommand;
import com.robot.command.PlaceCommand;
import com.robot.command.ReportCommand;
import com.robot.command.RightCommand;
import com.robot.domain.Direction;
import com.robot.domain.Position;
import java.util.Optional;

public class CommandParser {

    public Optional<Command> parse(String line) {
        if (line == null) return Optional.empty();
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return Optional.empty();

        return switch (trimmed) {
            case "MOVE"   -> Optional.of(new MoveCommand());
            case "LEFT"   -> Optional.of(new LeftCommand());
            case "RIGHT"  -> Optional.of(new RightCommand());
            case "REPORT" -> Optional.of(new ReportCommand());
            default       -> trimmed.startsWith("PLACE ") ? parsePlaceCommand(trimmed) : Optional.empty();
        };
    }

    private Optional<Command> parsePlaceCommand(String line) {
        String args = line.substring("PLACE ".length());
        String[] parts = args.split(",");
        if (parts.length != 3) return Optional.empty();
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            Direction direction = Direction.valueOf(parts[2]);
            return Optional.of(new PlaceCommand(new Position(x, y), direction));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
