# Toy Robot Simulator — Design Spec
**Date:** 2026-04-23
**Language:** Java 21
**Build tool:** Maven
**Test framework:** JUnit 5

---

## Context

Interview code exercise for BGL Corporate Solutions (Java Spring Boot + React stack). The application simulates a toy robot moving on a 5x5 tabletop. Evaluated on coding quality, OOP design, and testing practices.

---

## Architecture

The Command Pattern is chosen to demonstrate extensibility and testability — each command is its own class implementing a shared interface, keeping each unit independently testable and new commands trivial to add.

```
src/main/java/com/robot/
├── domain/
│   ├── Direction.java        ← enum: NORTH, EAST, SOUTH, WEST + rotation logic
│   ├── Position.java         ← immutable value class: x, y + move calculation
│   ├── Robot.java            ← owns Position + Direction, tracks placed state
│   └── Table.java            ← validates positions against 5x5 bounds
├── command/
│   ├── Command.java          ← interface: execute(Robot, Table)
│   ├── PlaceCommand.java
│   ├── MoveCommand.java
│   ├── LeftCommand.java
│   ├── RightCommand.java
│   └── ReportCommand.java
├── parser/
│   └── CommandParser.java    ← parses a string line → Optional<Command>
├── simulator/
│   └── Simulator.java        ← reads file, builds commands, executes in sequence
└── Main.java                 ← entry point, wires everything together

src/test/java/com/robot/
├── domain/
│   ├── DirectionTest.java
│   ├── PositionTest.java
│   ├── RobotTest.java
│   └── TableTest.java
├── command/
│   ├── PlaceCommandTest.java
│   ├── MoveCommandTest.java
│   ├── LeftCommandTest.java
│   ├── RightCommandTest.java
│   └── ReportCommandTest.java
├── parser/
│   └── CommandParserTest.java
└── simulator/
    └── SimulatorTest.java    ← integration tests using spec examples

src/test/resources/
├── test_a.txt
├── test_b.txt
└── test_c.txt
```

**Data flow:**
`Main` → `Simulator` reads file line by line → `CommandParser` converts each line to `Optional<Command>` → each `Command.execute(robot, table)` returns `Optional<String>` → `Simulator` prints any non-empty result to stdout.

---

## Domain Model

### `Direction` (enum)
Four values: `NORTH`, `EAST`, `SOUTH`, `WEST`. Rotation logic lives on the enum via switch expressions — no switch statements scattered across command classes.

```java
public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public Direction rotateLeft() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST  -> SOUTH;
            case SOUTH -> EAST;
            case EAST  -> NORTH;
        };
    }

    public Direction rotateRight() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST  -> SOUTH;
            case SOUTH -> WEST;
            case WEST  -> NORTH;
        };
    }
}
```

### `Position` (immutable value class)
Holds `x` and `y` as integers. `move(Direction)` returns a **new** `Position` without mutating the original. Immutability means a failed move attempt has zero side effects.

```java
public Position move(Direction direction) {
    return switch (direction) {
        case NORTH -> new Position(x, y + 1);
        case SOUTH -> new Position(x, y - 1);
        case EAST  -> new Position(x + 1, y);
        case WEST  -> new Position(x - 1, y);
    };
}
```

### `Robot`
Owns a `Position` and `Direction`. Tracks placed state via `isPlaced()`. All state changes go through public methods — no fields exposed. Movement is only committed after `Table` validates the new position.

```java
public void place(Position position, Direction direction) { ... }
public void move(Table table) { ... }
public void rotateLeft()  { ... }
public void rotateRight() { ... }
public String report() { ... }   // returns "X,Y,DIRECTION", e.g. "0,1,NORTH"
public boolean isPlaced() { ... }
```

### `Table`
Fixed at 5x5 (x: 0–4, y: 0–4). Exposes one method:

```java
public boolean isValidPosition(Position pos) {
    return pos.x() >= 0 && pos.x() < 5 && pos.y() >= 0 && pos.y() < 5;
}
```

`Robot.move()` calls `table.isValidPosition(newPosition)` before committing. `PlaceCommand` calls it before placing.

---

## Command Layer

### `Command` (interface)
```java
public interface Command {
    Optional<String> execute(Robot robot, Table table);
}
```

All commands return `Optional.empty()` except `ReportCommand`, which returns `Optional.of(robot.report())` when the robot is placed. `Simulator` prints any non-empty result to stdout — keeping I/O out of the command classes.

```java
// In Simulator:
parser.parse(line)
      .flatMap(cmd -> cmd.execute(robot, table))
      .ifPresent(System.out::println);
```

### Command implementations

| Class | Behaviour |
|---|---|
| `PlaceCommand` | Validates position with `Table`, calls `robot.place()` if valid; returns `Optional.empty()` |
| `MoveCommand` | No-op if robot not placed; delegates to `robot.move(table)`; returns `Optional.empty()` |
| `LeftCommand` | No-op if robot not placed; calls `robot.rotateLeft()`; returns `Optional.empty()` |
| `RightCommand` | No-op if robot not placed; calls `robot.rotateRight()`; returns `Optional.empty()` |
| `ReportCommand` | Returns `Optional.of(robot.report())` if placed, `Optional.empty()` otherwise |

### `CommandParser`
Parses a raw string line into `Optional<Command>`. Returns `Optional.empty()` for any invalid or unrecognised input — callers never deal with nulls.

**Parsing rules:**
- Leading and trailing whitespace on the full line is trimmed before processing
- Blank lines and lines with only whitespace → empty
- Unknown command names → empty
- `PLACE` arguments must be exactly `X,Y,F` with no spaces between them — `PLACE 1, 2, NORTH` is rejected
- `PLACE` with wrong argument count → empty
- `PLACE` with non-numeric x/y → empty
- `PLACE` with invalid direction string → empty

### `Simulator`
Reads the input file line by line, passes each line to `CommandParser`, and calls `execute(robot, table)` on each non-empty result. Owns the `Robot` and `Table` instances.

---

## Input / Output

**Decision:** The implementation accepts a file path as a CLI argument. stdin support is out of scope for this solution — a conscious tradeoff aligned with the spec's "developer chooses" clause.

**Invocation:**
```bash
java -jar robot-simulator.jar path/to/commands.txt
```

**Input format:** Plain `.txt` file, one command per line, uppercase. Empty lines are skipped.

**Output:** `REPORT` prints to stdout in the format `X,Y,DIRECTION` exactly as shown in the spec examples (e.g. `0,1,NORTH`). All other commands produce no output.

---

## Edge Cases

### Pre-placement
- `MOVE`, `LEFT`, `RIGHT`, `REPORT` before any valid `PLACE` → silently ignored

### Invalid PLACE
- Out-of-bounds coordinates: `PLACE 5,0,NORTH`, `PLACE -1,0,NORTH` → ignored
- Non-numeric coordinates: `PLACE x,y,NORTH` → ignored
- Invalid direction: `PLACE 1,1,NORTHWEST` → ignored
- Wrong argument count: `PLACE 1` → ignored
- Spaces between arguments: `PLACE 1, 2, NORTH` → ignored

### Boundary movement (all four edges)
| Position | Facing | Result |
|---|---|---|
| `(0, 0, SOUTH)` | SOUTH | ignored — y → -1 |
| `(0, 0, WEST)` | WEST | ignored — x → -1 |
| `(4, 4, NORTH)` | NORTH | ignored — y → 5 |
| `(4, 4, EAST)` | EAST | ignored — x → 5 |
| `(0, 2, WEST)` | WEST | ignored — x → -1 |
| `(2, 0, SOUTH)` | SOUTH | ignored — y → -1 |
| `(4, 2, EAST)` | EAST | ignored — x → 5 |
| `(2, 4, NORTH)` | NORTH | ignored — y → 5 |

One `Table.isValidPosition()` check catches all cases — no direction-specific logic needed.

### Multiple PLACE commands
- Second `PLACE` mid-sequence is valid and fully replaces robot state

### Input hygiene
- Empty lines and whitespace-only lines → skipped
- Unknown commands → skipped
- Commands are uppercase only — case-insensitive handling is out of scope

---

## Testing Strategy

### Unit tests

**`DirectionTest`**
- All 8 rotation cases (left and right from each direction)
- Full left rotation cycle returns to origin
- Full right rotation cycle returns to origin

**`PositionTest`**
- Move in all 4 directions produces correct new coordinates
- Original position is unchanged after move (immutability)

**`TableTest`**
- Valid positions pass: `(0,0)`, `(4,4)`, `(2,2)`
- All boundary violations fail: negative x, negative y, x≥5, y≥5

**`RobotTest`**
- Unplaced robot: `isPlaced()` is false
- After PLACE: `isPlaced()` is true, position and direction set correctly
- Second PLACE replaces first

**`CommandParserTest`**
- Valid PLACE parses to `PlaceCommand`
- Invalid PLACE formats all return `Optional.empty()`
- MOVE, LEFT, RIGHT, REPORT parse to correct command types
- Unknown command returns `Optional.empty()`
- Empty line returns `Optional.empty()`
- Whitespace-only line returns `Optional.empty()`

**Per-command tests**
- Each command tested in placed and unplaced robot states
- Boundary moves verified to be no-ops

### Integration tests (`SimulatorTest`)

The three spec examples as end-to-end tests using test resource files:

```
test_a.txt → REPORT output: "0,1,NORTH"
test_b.txt → REPORT output: "0,0,WEST"
test_c.txt → REPORT output: "3,3,NORTH"
```

Named edge case scenarios tested as sequences:
- `move_ignored_when_robot_not_placed`
- `place_ignored_when_out_of_bounds`
- `move_ignored_at_south_edge`
- `move_ignored_at_north_edge`
- `move_ignored_at_west_edge`
- `move_ignored_at_east_edge`
- `second_place_replaces_first`
- `commands_before_first_place_are_ignored`

### Test data files
`src/test/resources/test_a.txt`, `test_b.txt`, `test_c.txt` — reviewer can run manually to verify output.

---

## How to Run

**Requirements:** Java 21, Maven 3.x

**Run tests:**
```bash
mvn test
```

**Build:**
```bash
mvn package
```

**Run:**
```bash
java -jar target/robot-simulator.jar path/to/commands.txt
```
