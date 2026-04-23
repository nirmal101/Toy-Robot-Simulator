package com.robot.simulator;

import com.robot.domain.Robot;
import com.robot.domain.Table;
import com.robot.parser.CommandParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private final CommandParser parser = new CommandParser();

    public List<String> run(List<String> lines) {
        Robot robot = new Robot();
        Table table = new Table();
        List<String> output = new ArrayList<>();

        for (String line : lines) {
            parser.parse(line)
                  .flatMap(cmd -> cmd.execute(robot, table))
                  .ifPresent(output::add);
        }
        return output;
    }

    public List<String> run(Path filePath) throws IOException {
        return run(Files.readAllLines(filePath));
    }
}
