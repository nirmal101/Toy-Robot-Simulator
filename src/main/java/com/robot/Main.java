package com.robot;

import com.robot.simulator.Simulator;
import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java -jar robot-simulator.jar <commands-file>");
            System.exit(1);
        }
        new Simulator().run(Path.of(args[0])).forEach(System.out::println);
    }
}
