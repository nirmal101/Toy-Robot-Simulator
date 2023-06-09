console.log('Welcome to the Toy Robot Simulation...');
console.log('Enter your commands: ');
class Robot {
    // Method to check if the given coordinates are within bounds
    checkBounds(x, y) {
        return (x < 0 || x > 4 || y < 0 || y > 4) ? true : false;
    }
    // Method to place the robot on the table
    place(x, y, direction) {
        if (this.checkBounds(x, y)) {
            console.log("Invalid coordinates, X and Y Coordinates can only be between 0 and 4 to faciliate 5x5 tabletop, please try again");
            return;
        }
        this.position = { x, y, direction };
    }
    move() {
        // Check if the robot has been placed on the table
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        const { x, y, direction } = this.position;
        let newX = x;
        let newY = y;
        //calculate new position based on robot's facing direction
        switch (direction) {
            case "NORTH":
                newY += 1;
                break;
            case "EAST":
                newX += 1;
                break;
            case "SOUTH":
                newY -= 1;
                break;
            case "WEST":
                newX -= 1;
                break;
        }
        if (this.checkBounds(newX, newY)) {
            console.log("Move not allowed, the robot will fall from the table");
            return;
        }
        // Update the robot's position
        this.position.x = newX;
        this.position.y = newY;
    }
    // Method to rotate the robot right
    right() {
        this.rotate(1);
    }
    // Method to rotate the robot left
    left() {
        this.rotate(-1);
    }
    // Helper method to rotate the robot in the given direction
    rotate(rotationDirection) {
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        // Get current direction of the robot
        const { direction } = this.position;
        const directions = ["NORTH", "EAST", "SOUTH", "WEST"];
        // Calculate the new direction index based on the current direction and rotation direction
        const currentDirectionIndex = directions.indexOf(direction);
        const newDirectionIndex = (currentDirectionIndex + rotationDirection + directions.length) % directions.length;
        //Update the robot's direction
        this.position.direction = directions[newDirectionIndex];
    }
    // Method to report the current position of the robot
    report() {
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        const { x, y, direction } = this.position;
        console.log(`Coordinates: ${x},${y},${direction}`);
    }
}

const robot = new Robot();
const readline = require('readline').createInterface({
    input: process.stdin,
    output: process.stdout
});
function processCommand(input) {
    const [command, args] = input.split(" ");
    switch (command.toUpperCase()) {
        case "PLACE":
            const [x, y, direction] = args.split(",");
            robot.place(parseInt(x), parseInt(y), direction);
            break;
        case "MOVE":
            robot.move();
            break;
        case "LEFT":
            robot.left();
            break;
        case "RIGHT":
            robot.right();
            break;
        case "REPORT":
            robot.report();
            break;
        default:
            console.log(`Invalid command: ${input}`);
    }
}
readline.on('line', (input) => {
    processCommand(input);
});
