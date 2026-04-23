# Toy Robot Simulator

A simulation of a toy robot moving on a 5×5 tabletop, built in Java 21 with Maven.

The robot responds to commands from a plain-text file and reports its position on request. It ignores any command that would move it off the table or that arrives before it has been placed.

## Commands

| Command | Description |
|---|---|
| `PLACE X,Y,F` | Place the robot at position (X, Y) facing direction F (`NORTH`, `EAST`, `SOUTH`, `WEST`) |
| `MOVE` | Move one step forward in the current facing direction |
| `LEFT` | Rotate 90° left without moving |
| `RIGHT` | Rotate 90° right without moving |
| `REPORT` | Print the robot's current position and direction to stdout |

## Input format

A plain `.txt` file with one command per line, uppercase. Empty lines are skipped. Unknown or invalid commands are silently ignored.

Example (`commands.txt`):
```
PLACE 1,2,EAST
MOVE
MOVE
LEFT
MOVE
REPORT
```

Output:
```
3,3,NORTH
```

## Requirements

- Java 21
- Maven 3.x

## Running tests

```bash
mvn test
```

## Build

```bash
mvn package
```

## Run

```bash
java -jar target/robot-simulator.jar path/to/commands.txt
```

## Architecture

Built with the **Command Pattern** — each instruction (`PlaceCommand`, `MoveCommand`, etc.) implements a shared `Command` interface, keeping each unit independently testable and making new commands trivial to add.

```
src/main/java/com/robot/
├── domain/          # Direction, Position, Robot, Table
├── command/         # Command interface + one class per instruction
├── parser/          # CommandParser — raw string → Optional<Command>
├── simulator/       # Simulator — reads file, runs commands, collects output
└── Main.java        # Entry point
```
