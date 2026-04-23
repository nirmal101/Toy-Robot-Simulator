# Toy Robot Simulator Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Java CLI application that simulates a toy robot moving on a 5x5 tabletop, reading commands from a file and printing REPORT output to stdout.

**Architecture:** Command Pattern — each command (`PLACE`, `MOVE`, `LEFT`, `RIGHT`, `REPORT`) is its own class implementing a shared `Command` interface that returns `Optional<String>`. Domain classes (`Direction`, `Position`, `Robot`, `Table`) are pure logic with no I/O. `Simulator` owns the robot and table, runs commands, and prints non-empty results.

**Tech Stack:** Java 21, Maven 3.x, JUnit 5 (junit-jupiter 5.10.2)

---

## File Map

```
pom.xml

src/main/java/com/robot/
  Main.java                         ← entry point, reads file arg, prints output
  domain/
    Direction.java                  ← enum with switch-based rotation
    Position.java                   ← immutable record, produces new Position on move
    Robot.java                      ← owns state, all mutations through methods
    Table.java                      ← validates 5x5 bounds
  command/
    Command.java                    ← interface: Optional<String> execute(Robot, Table)
    PlaceCommand.java
    MoveCommand.java
    LeftCommand.java
    RightCommand.java
    ReportCommand.java
  parser/
    CommandParser.java              ← String line → Optional<Command>
  simulator/
    Simulator.java                  ← runs command list, owns Robot + Table

src/test/java/com/robot/
  domain/
    DirectionTest.java
    PositionTest.java
    RobotTest.java
    TableTest.java
  command/
    PlaceCommandTest.java
    MoveCommandTest.java
    LeftCommandTest.java
    RightCommandTest.java
    ReportCommandTest.java
  parser/
    CommandParserTest.java
  simulator/
    SimulatorTest.java              ← integration tests

src/test/resources/
  test_a.txt
  test_b.txt
  test_c.txt
```

---

## Task 1: Maven Project Scaffold

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/robot/.gitkeep`

- [ ] **Step 1: Create the Maven directory structure**

```bash
mkdir -p src/main/java/com/robot/{domain,command,parser,simulator}
mkdir -p src/test/java/com/robot/{domain,command,parser,simulator}
mkdir -p src/test/resources
```

- [ ] **Step 2: Create `pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.robot</groupId>
    <artifactId>robot-simulator</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.robot.Main</mainClass>
                        </manifest>
                    </archive>
                    <finalName>robot-simulator</finalName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Verify Maven resolves dependencies**

```bash
mvn validate
```

Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git init
git add pom.xml src/
git commit -m "chore: scaffold Maven project with JUnit 5"
```

---

## Task 2: Direction Enum

**Files:**
- Create: `src/main/java/com/robot/domain/Direction.java`
- Create: `src/test/java/com/robot/domain/DirectionTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/domain/DirectionTest.java`:

```java
package com.robot.domain;

import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectionTest {

    @Test void north_rotates_left_to_west()  { assertEquals(WEST,  NORTH.rotateLeft()); }
    @Test void west_rotates_left_to_south()  { assertEquals(SOUTH, WEST.rotateLeft());  }
    @Test void south_rotates_left_to_east()  { assertEquals(EAST,  SOUTH.rotateLeft()); }
    @Test void east_rotates_left_to_north()  { assertEquals(NORTH, EAST.rotateLeft());  }

    @Test void north_rotates_right_to_east() { assertEquals(EAST,  NORTH.rotateRight()); }
    @Test void east_rotates_right_to_south() { assertEquals(SOUTH, EAST.rotateRight());  }
    @Test void south_rotates_right_to_west() { assertEquals(WEST,  SOUTH.rotateRight()); }
    @Test void west_rotates_right_to_north() { assertEquals(NORTH, WEST.rotateRight());  }

    @Test void full_left_rotation_cycle_returns_to_origin() {
        Direction d = NORTH;
        for (int i = 0; i < 4; i++) d = d.rotateLeft();
        assertEquals(NORTH, d);
    }

    @Test void full_right_rotation_cycle_returns_to_origin() {
        Direction d = NORTH;
        for (int i = 0; i < 4; i++) d = d.rotateRight();
        assertEquals(NORTH, d);
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -pl . -Dtest=DirectionTest
```

Expected: FAIL — `Direction` does not exist yet.

- [ ] **Step 3: Implement `Direction`**

Create `src/main/java/com/robot/domain/Direction.java`:

```java
package com.robot.domain;

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

- [ ] **Step 4: Run tests to verify they pass**

```bash
mvn test -Dtest=DirectionTest
```

Expected: `Tests run: 10, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/robot/domain/Direction.java src/test/java/com/robot/domain/DirectionTest.java
git commit -m "feat: add Direction enum with switch-based rotation"
```

---

## Task 3: Position Record

**Files:**
- Create: `src/main/java/com/robot/domain/Position.java`
- Create: `src/test/java/com/robot/domain/PositionTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/domain/PositionTest.java`:

```java
package com.robot.domain;

import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionTest {

    @Test void move_north_increments_y() {
        assertEquals(new Position(2, 3), new Position(2, 2).move(NORTH));
    }

    @Test void move_south_decrements_y() {
        assertEquals(new Position(2, 1), new Position(2, 2).move(SOUTH));
    }

    @Test void move_east_increments_x() {
        assertEquals(new Position(3, 2), new Position(2, 2).move(EAST));
    }

    @Test void move_west_decrements_x() {
        assertEquals(new Position(1, 2), new Position(2, 2).move(WEST));
    }

    @Test void move_returns_new_position_leaving_original_unchanged() {
        Position original = new Position(2, 2);
        Position moved = original.move(NORTH);
        assertEquals(new Position(2, 2), original);
        assertEquals(new Position(2, 3), moved);
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest=PositionTest
```

Expected: FAIL — `Position` does not exist yet.

- [ ] **Step 3: Implement `Position`**

Create `src/main/java/com/robot/domain/Position.java`:

```java
package com.robot.domain;

public record Position(int x, int y) {

    public Position move(Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(x, y + 1);
            case SOUTH -> new Position(x, y - 1);
            case EAST  -> new Position(x + 1, y);
            case WEST  -> new Position(x - 1, y);
        };
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
mvn test -Dtest=PositionTest
```

Expected: `Tests run: 5, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/robot/domain/Position.java src/test/java/com/robot/domain/PositionTest.java
git commit -m "feat: add immutable Position record with directional move"
```

---

## Task 4: Table

**Files:**
- Create: `src/main/java/com/robot/domain/Table.java`
- Create: `src/test/java/com/robot/domain/TableTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/domain/TableTest.java`:

```java
package com.robot.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    private final Table table = new Table();

    @Test void origin_is_valid()     { assertTrue(table.isValidPosition(new Position(0, 0))); }
    @Test void max_corner_is_valid() { assertTrue(table.isValidPosition(new Position(4, 4))); }
    @Test void centre_is_valid()     { assertTrue(table.isValidPosition(new Position(2, 2))); }

    @Test void negative_x_is_invalid()  { assertFalse(table.isValidPosition(new Position(-1, 0))); }
    @Test void negative_y_is_invalid()  { assertFalse(table.isValidPosition(new Position(0, -1))); }
    @Test void x_of_five_is_invalid()   { assertFalse(table.isValidPosition(new Position(5, 0))); }
    @Test void y_of_five_is_invalid()   { assertFalse(table.isValidPosition(new Position(0, 5))); }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest=TableTest
```

Expected: FAIL — `Table` does not exist yet.

- [ ] **Step 3: Implement `Table`**

Create `src/main/java/com/robot/domain/Table.java`:

```java
package com.robot.domain;

public class Table {

    public boolean isValidPosition(Position pos) {
        return pos.x() >= 0 && pos.x() < 5
            && pos.y() >= 0 && pos.y() < 5;
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
mvn test -Dtest=TableTest
```

Expected: `Tests run: 7, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/robot/domain/Table.java src/test/java/com/robot/domain/TableTest.java
git commit -m "feat: add Table with 5x5 boundary validation"
```

---

## Task 5: Robot

**Files:**
- Create: `src/main/java/com/robot/domain/Robot.java`
- Create: `src/test/java/com/robot/domain/RobotTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/domain/RobotTest.java`:

```java
package com.robot.domain;

import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class RobotTest {

    private final Table table = new Table();

    @Test void robot_is_not_placed_initially() {
        assertFalse(new Robot().isPlaced());
    }

    @Test void robot_is_placed_after_place() {
        Robot robot = new Robot();
        robot.place(new Position(1, 2), NORTH);
        assertTrue(robot.isPlaced());
    }

    @Test void report_reflects_position_and_direction() {
        Robot robot = new Robot();
        robot.place(new Position(1, 2), NORTH);
        assertEquals("1,2,NORTH", robot.report());
    }

    @Test void move_updates_position() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.move(table);
        assertEquals("0,1,NORTH", robot.report());
    }

    @Test void move_ignored_when_not_placed() {
        Robot robot = new Robot();
        robot.move(table);
        assertFalse(robot.isPlaced());
    }

    @Test void move_ignored_when_it_would_fall_off_table() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), SOUTH);
        robot.move(table);
        assertEquals("0,0,SOUTH", robot.report());
    }

    @Test void rotate_left_updates_direction() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.rotateLeft();
        assertEquals("0,0,WEST", robot.report());
    }

    @Test void rotate_right_updates_direction() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.rotateRight();
        assertEquals("0,0,EAST", robot.report());
    }

    @Test void second_place_replaces_first() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        robot.place(new Position(3, 3), EAST);
        assertEquals("3,3,EAST", robot.report());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest=RobotTest
```

Expected: FAIL — `Robot` does not exist yet.

- [ ] **Step 3: Implement `Robot`**

Create `src/main/java/com/robot/domain/Robot.java`:

```java
package com.robot.domain;

public class Robot {

    private Position position;
    private Direction direction;

    public void place(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public void move(Table table) {
        if (!isPlaced()) return;
        Position next = position.move(direction);
        if (table.isValidPosition(next)) {
            position = next;
        }
    }

    public void rotateLeft() {
        if (!isPlaced()) return;
        direction = direction.rotateLeft();
    }

    public void rotateRight() {
        if (!isPlaced()) return;
        direction = direction.rotateRight();
    }

    public String report() {
        return position.x() + "," + position.y() + "," + direction;
    }

    public boolean isPlaced() {
        return position != null;
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
mvn test -Dtest=RobotTest
```

Expected: `Tests run: 9, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/robot/domain/Robot.java src/test/java/com/robot/domain/RobotTest.java
git commit -m "feat: add Robot with place, move, rotate, and report"
```

---

## Task 6: Command Interface + PlaceCommand

**Files:**
- Create: `src/main/java/com/robot/command/Command.java`
- Create: `src/main/java/com/robot/command/PlaceCommand.java`
- Create: `src/test/java/com/robot/command/PlaceCommandTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/command/PlaceCommandTest.java`:

```java
package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class PlaceCommandTest {

    private final Table table = new Table();

    @Test void places_robot_at_valid_position() {
        Robot robot = new Robot();
        new PlaceCommand(new Position(1, 2), NORTH).execute(robot, table);
        assertTrue(robot.isPlaced());
        assertEquals("1,2,NORTH", robot.report());
    }

    @Test void ignores_out_of_bounds_position() {
        Robot robot = new Robot();
        new PlaceCommand(new Position(5, 0), NORTH).execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void ignores_negative_position() {
        Robot robot = new Robot();
        new PlaceCommand(new Position(-1, 0), NORTH).execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        Optional<String> result = new PlaceCommand(new Position(1, 1), NORTH).execute(robot, table);
        assertTrue(result.isEmpty());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest=PlaceCommandTest
```

Expected: FAIL — `Command` and `PlaceCommand` do not exist yet.

- [ ] **Step 3: Implement `Command` interface**

Create `src/main/java/com/robot/command/Command.java`:

```java
package com.robot.command;

import com.robot.domain.Robot;
import com.robot.domain.Table;
import java.util.Optional;

public interface Command {
    Optional<String> execute(Robot robot, Table table);
}
```

- [ ] **Step 4: Implement `PlaceCommand`**

Create `src/main/java/com/robot/command/PlaceCommand.java`:

```java
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
```

- [ ] **Step 5: Run tests to verify they pass**

```bash
mvn test -Dtest=PlaceCommandTest
```

Expected: `Tests run: 4, Failures: 0, Errors: 0`

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/robot/command/ src/test/java/com/robot/command/PlaceCommandTest.java
git commit -m "feat: add Command interface and PlaceCommand"
```

---

## Task 7: MoveCommand

**Files:**
- Create: `src/main/java/com/robot/command/MoveCommand.java`
- Create: `src/test/java/com/robot/command/MoveCommandTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/command/MoveCommandTest.java`:

```java
package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveCommandTest {

    private final Table table = new Table();

    @Test void moves_robot_forward() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        new MoveCommand().execute(robot, table);
        assertEquals("0,1,NORTH", robot.report());
    }

    @Test void ignored_when_not_placed() {
        Robot robot = new Robot();
        new MoveCommand().execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void ignored_at_south_edge() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), SOUTH);
        new MoveCommand().execute(robot, table);
        assertEquals("0,0,SOUTH", robot.report());
    }

    @Test void ignored_at_north_edge() {
        Robot robot = new Robot();
        robot.place(new Position(4, 4), NORTH);
        new MoveCommand().execute(robot, table);
        assertEquals("4,4,NORTH", robot.report());
    }

    @Test void ignored_at_west_edge() {
        Robot robot = new Robot();
        robot.place(new Position(0, 2), WEST);
        new MoveCommand().execute(robot, table);
        assertEquals("0,2,WEST", robot.report());
    }

    @Test void ignored_at_east_edge() {
        Robot robot = new Robot();
        robot.place(new Position(4, 2), EAST);
        new MoveCommand().execute(robot, table);
        assertEquals("4,2,EAST", robot.report());
    }

    @Test void ignored_at_south_edge_non_corner() {
        Robot robot = new Robot();
        robot.place(new Position(2, 0), SOUTH);
        new MoveCommand().execute(robot, table);
        assertEquals("2,0,SOUTH", robot.report());
    }

    @Test void ignored_at_north_edge_non_corner() {
        Robot robot = new Robot();
        robot.place(new Position(2, 4), NORTH);
        new MoveCommand().execute(robot, table);
        assertEquals("2,4,NORTH", robot.report());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        assertTrue(new MoveCommand().execute(robot, table).isEmpty());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest=MoveCommandTest
```

Expected: FAIL — `MoveCommand` does not exist yet.

- [ ] **Step 3: Implement `MoveCommand`**

Create `src/main/java/com/robot/command/MoveCommand.java`:

```java
package com.robot.command;

import com.robot.domain.Robot;
import com.robot.domain.Table;
import java.util.Optional;

public class MoveCommand implements Command {

    @Override
    public Optional<String> execute(Robot robot, Table table) {
        if (robot.isPlaced()) {
            robot.move(table);
        }
        return Optional.empty();
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
mvn test -Dtest=MoveCommandTest
```

Expected: `Tests run: 9, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/robot/command/MoveCommand.java src/test/java/com/robot/command/MoveCommandTest.java
git commit -m "feat: add MoveCommand with boundary protection"
```

---

## Task 8: LeftCommand and RightCommand

**Files:**
- Create: `src/main/java/com/robot/command/LeftCommand.java`
- Create: `src/main/java/com/robot/command/RightCommand.java`
- Create: `src/test/java/com/robot/command/LeftCommandTest.java`
- Create: `src/test/java/com/robot/command/RightCommandTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/command/LeftCommandTest.java`:

```java
package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class LeftCommandTest {

    private final Table table = new Table();

    @Test void rotates_robot_left() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        new LeftCommand().execute(robot, table);
        assertEquals("0,0,WEST", robot.report());
    }

    @Test void ignored_when_not_placed() {
        Robot robot = new Robot();
        new LeftCommand().execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        assertTrue(new LeftCommand().execute(robot, table).isEmpty());
    }
}
```

Create `src/test/java/com/robot/command/RightCommandTest.java`:

```java
package com.robot.command;

import com.robot.domain.*;
import org.junit.jupiter.api.Test;
import static com.robot.domain.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class RightCommandTest {

    private final Table table = new Table();

    @Test void rotates_robot_right() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        new RightCommand().execute(robot, table);
        assertEquals("0,0,EAST", robot.report());
    }

    @Test void ignored_when_not_placed() {
        Robot robot = new Robot();
        new RightCommand().execute(robot, table);
        assertFalse(robot.isPlaced());
    }

    @Test void returns_empty() {
        Robot robot = new Robot();
        robot.place(new Position(0, 0), NORTH);
        assertTrue(new RightCommand().execute(robot, table).isEmpty());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest="LeftCommandTest+RightCommandTest"
```

Expected: FAIL — neither class exists yet.

- [ ] **Step 3: Implement `LeftCommand`**

Create `src/main/java/com/robot/command/LeftCommand.java`:

```java
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
```

- [ ] **Step 4: Implement `RightCommand`**

Create `src/main/java/com/robot/command/RightCommand.java`:

```java
package com.robot.command;

import com.robot.domain.Robot;
import com.robot.domain.Table;
import java.util.Optional;

public class RightCommand implements Command {

    @Override
    public Optional<String> execute(Robot robot, Table table) {
        if (robot.isPlaced()) {
            robot.rotateRight();
        }
        return Optional.empty();
    }
}
```

- [ ] **Step 5: Run tests to verify they pass**

```bash
mvn test -Dtest="LeftCommandTest+RightCommandTest"
```

Expected: `Tests run: 6, Failures: 0, Errors: 0`

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/robot/command/LeftCommand.java src/main/java/com/robot/command/RightCommand.java src/test/java/com/robot/command/LeftCommandTest.java src/test/java/com/robot/command/RightCommandTest.java
git commit -m "feat: add LeftCommand and RightCommand"
```

---

## Task 9: ReportCommand

**Files:**
- Create: `src/main/java/com/robot/command/ReportCommand.java`
- Create: `src/test/java/com/robot/command/ReportCommandTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/command/ReportCommandTest.java`:

```java
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
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest=ReportCommandTest
```

Expected: FAIL — `ReportCommand` does not exist yet.

- [ ] **Step 3: Implement `ReportCommand`**

Create `src/main/java/com/robot/command/ReportCommand.java`:

```java
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
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
mvn test -Dtest=ReportCommandTest
```

Expected: `Tests run: 2, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/robot/command/ReportCommand.java src/test/java/com/robot/command/ReportCommandTest.java
git commit -m "feat: add ReportCommand returning Optional<String>"
```

---

## Task 10: CommandParser

**Files:**
- Create: `src/main/java/com/robot/parser/CommandParser.java`
- Create: `src/test/java/com/robot/parser/CommandParserTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/com/robot/parser/CommandParserTest.java`:

```java
package com.robot.parser;

import com.robot.command.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test void parses_move()   { assertInstanceOf(MoveCommand.class,   parser.parse("MOVE").orElseThrow()); }
    @Test void parses_left()   { assertInstanceOf(LeftCommand.class,   parser.parse("LEFT").orElseThrow()); }
    @Test void parses_right()  { assertInstanceOf(RightCommand.class,  parser.parse("RIGHT").orElseThrow()); }
    @Test void parses_report() { assertInstanceOf(ReportCommand.class, parser.parse("REPORT").orElseThrow()); }
    @Test void parses_valid_place() { assertInstanceOf(PlaceCommand.class, parser.parse("PLACE 1,2,NORTH").orElseThrow()); }

    @Test void trims_leading_and_trailing_whitespace() {
        assertInstanceOf(MoveCommand.class, parser.parse("  MOVE  ").orElseThrow());
    }

    @Test void returns_empty_for_unknown_command() {
        assertTrue(parser.parse("JUMP").isEmpty());
    }

    @Test void returns_empty_for_empty_line() {
        assertTrue(parser.parse("").isEmpty());
    }

    @Test void returns_empty_for_whitespace_only_line() {
        assertTrue(parser.parse("   ").isEmpty());
    }

    @Test void returns_empty_for_place_with_invalid_direction() {
        assertTrue(parser.parse("PLACE 1,2,NORTHWEST").isEmpty());
    }

    @Test void returns_empty_for_place_with_non_numeric_coords() {
        assertTrue(parser.parse("PLACE x,y,NORTH").isEmpty());
    }

    @Test void returns_empty_for_place_with_wrong_arg_count() {
        assertTrue(parser.parse("PLACE 1").isEmpty());
    }

    @Test void returns_empty_for_place_with_spaces_in_args() {
        assertTrue(parser.parse("PLACE 1, 2, NORTH").isEmpty());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
mvn test -Dtest=CommandParserTest
```

Expected: FAIL — `CommandParser` does not exist yet.

- [ ] **Step 3: Implement `CommandParser`**

Create `src/main/java/com/robot/parser/CommandParser.java`:

```java
package com.robot.parser;

import com.robot.command.*;
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
        } catch (NumberFormatException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
mvn test -Dtest=CommandParserTest
```

Expected: `Tests run: 13, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/robot/parser/CommandParser.java src/test/java/com/robot/parser/CommandParserTest.java
git commit -m "feat: add CommandParser producing Optional<Command>"
```

---

## Task 11: Simulator + Integration Tests

**Files:**
- Create: `src/main/java/com/robot/simulator/Simulator.java`
- Create: `src/test/java/com/robot/simulator/SimulatorTest.java`
- Create: `src/test/resources/test_a.txt`
- Create: `src/test/resources/test_b.txt`
- Create: `src/test/resources/test_c.txt`

- [ ] **Step 1: Create the test resource files**

Create `src/test/resources/test_a.txt`:
```
PLACE 0,0,NORTH
MOVE
REPORT
```

Create `src/test/resources/test_b.txt`:
```
PLACE 0,0,NORTH
LEFT
REPORT
```

Create `src/test/resources/test_c.txt`:
```
PLACE 1,2,EAST
MOVE
MOVE
LEFT
MOVE
REPORT
```

- [ ] **Step 2: Write the failing integration tests**

Create `src/test/java/com/robot/simulator/SimulatorTest.java`:

```java
package com.robot.simulator;

import org.junit.jupiter.api.Test;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorTest {

    // --- Spec examples ---

    @Test void spec_example_a() throws Exception {
        assertEquals(List.of("0,1,NORTH"), run(resourcePath("test_a.txt")));
    }

    @Test void spec_example_b() throws Exception {
        assertEquals(List.of("0,0,WEST"), run(resourcePath("test_b.txt")));
    }

    @Test void spec_example_c() throws Exception {
        assertEquals(List.of("3,3,NORTH"), run(resourcePath("test_c.txt")));
    }

    // --- Edge cases ---

    @Test void commands_before_first_place_are_ignored() {
        List<String> output = new Simulator().run(List.of(
            "MOVE", "LEFT", "RIGHT", "REPORT",
            "PLACE 0,0,NORTH",
            "REPORT"
        ));
        assertEquals(List.of("0,0,NORTH"), output);
    }

    @Test void place_ignored_when_out_of_bounds() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 5,5,NORTH",
            "REPORT"
        ));
        assertTrue(output.isEmpty());
    }

    @Test void move_ignored_at_south_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,0,SOUTH",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("0,0,SOUTH"), output);
    }

    @Test void move_ignored_at_north_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 4,4,NORTH",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("4,4,NORTH"), output);
    }

    @Test void move_ignored_at_west_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,2,WEST",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("0,2,WEST"), output);
    }

    @Test void move_ignored_at_east_edge() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 4,2,EAST",
            "MOVE",
            "REPORT"
        ));
        assertEquals(List.of("4,2,EAST"), output);
    }

    @Test void second_place_replaces_first() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,0,NORTH",
            "MOVE",
            "PLACE 3,3,EAST",
            "REPORT"
        ));
        assertEquals(List.of("3,3,EAST"), output);
    }

    @Test void empty_and_unknown_lines_are_skipped() {
        List<String> output = new Simulator().run(List.of(
            "PLACE 0,0,NORTH",
            "",
            "JUMP",
            "REPORT"
        ));
        assertEquals(List.of("0,0,NORTH"), output);
    }

    // --- Helpers ---

    private List<String> run(Path path) throws Exception {
        return new Simulator().run(path);
    }

    private Path resourcePath(String filename) throws URISyntaxException {
        return Path.of(getClass().getClassLoader().getResource(filename).toURI());
    }
}
```

- [ ] **Step 3: Run tests to verify they fail**

```bash
mvn test -Dtest=SimulatorTest
```

Expected: FAIL — `Simulator` does not exist yet.

- [ ] **Step 4: Implement `Simulator`**

Create `src/main/java/com/robot/simulator/Simulator.java`:

```java
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
```

- [ ] **Step 5: Run tests to verify they pass**

```bash
mvn test -Dtest=SimulatorTest
```

Expected: `Tests run: 11, Failures: 0, Errors: 0`

- [ ] **Step 6: Run all tests to confirm nothing is broken**

```bash
mvn test
```

Expected: All tests pass, zero failures.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/robot/simulator/Simulator.java src/test/java/com/robot/simulator/SimulatorTest.java src/test/resources/
git commit -m "feat: add Simulator with integration tests and test data files"
```

---

## Task 12: Main Entry Point + Final Build

**Files:**
- Create: `src/main/java/com/robot/Main.java`

- [ ] **Step 1: Implement `Main`**

Create `src/main/java/com/robot/Main.java`:

```java
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
```

- [ ] **Step 2: Build the jar**

```bash
mvn package -DskipTests
```

Expected: `BUILD SUCCESS`, `target/robot-simulator.jar` created.

- [ ] **Step 3: Smoke test with spec example a**

```bash
java -jar target/robot-simulator.jar src/test/resources/test_a.txt
```

Expected output:
```
0,1,NORTH
```

- [ ] **Step 4: Smoke test with spec example b**

```bash
java -jar target/robot-simulator.jar src/test/resources/test_b.txt
```

Expected output:
```
0,0,WEST
```

- [ ] **Step 5: Smoke test with spec example c**

```bash
java -jar target/robot-simulator.jar src/test/resources/test_c.txt
```

Expected output:
```
3,3,NORTH
```

- [ ] **Step 6: Run full test suite one final time**

```bash
mvn test
```

Expected: All tests pass, zero failures.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/robot/Main.java
git commit -m "feat: add Main entry point and complete robot simulator"
```
