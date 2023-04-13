console.log('Welcome to the Toy Robot Simulation...')

//possible directions the robot can face
type Direction = "NORTH" | "EAST" | "SOUTH" | "WEST";

//position interface to include (x,y) coordinates and a direction
interface Position{
    x: number;
    y: number;
    direction: Direction;
}

class Robot {
    // Robot's position
    private position?: Position;

    // Method to check if the given coordinates are within bounds
    private checkBounds(x:number, y:number): boolean{
        return (x < 0 || x > 4 || y < 0 || y > 4) ? true : false;
    }

    // Method to place the robot on the table
    public place(x: number, y: number, direction: "NORTH" | "EAST" | "SOUTH" | "WEST"): void {
        if (this.checkBounds(x, y)) {
            console.log("Invalid coordinates, X and Y Coordinates can only be between 0 and 4 to faciliate 5x5 tabletop, please try again");
            return;
        }
        this.position = { x, y, direction };
    }

    public move(): void {
        // Check if the robot has been placed on the table
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        const {x, y, direction} = this.position;
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
        if (this.checkBounds(newX, newY) ){
            console.log("Move not allowed, the robot will fall from the table");
            return;
        }
        // Update the robot's position
        this.position.x = newX;
        this.position.y = newY;
    }

    // Method to rotate the robot right
    public right(): void {
        this.rotate(1);
    }

    // Method to rotate the robot left
    public left(): void {
        this.rotate(-1);
    }

    // Helper method to rotate the robot in the given direction
    private rotate(rotationDirection: -1| 1): void {
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return
        }
        // Get current direction of the robot
        const { direction } = this.position;
        const directions: Direction[] = [ "NORTH", "EAST", "SOUTH", "WEST"];
        // Calculate the new direction index based on the current direction and rotation direction
        const currentDirectionIndex = directions.indexOf(direction);
        const newDirectionIndex = (currentDirectionIndex + rotationDirection + directions.length) % directions.length;
        //Update the robot's direction
        this.position.direction = directions[newDirectionIndex];
    }

    // Method to report the current position of the robot
    public report(): void {
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        const { x, y, direction} = this.position;
        console.log(`Coordinates: ${x},${y},${direction}`);
    }
   

}



// Example usage
const robot = new Robot();

const readline = require('readline').createInterface({
    input: process.stdin,
    output: process.stdout
});

function processCommand(input: string) {
    const [command, args] = input.split(" ");
    switch (command.toUpperCase()) {
        case "PLACE":
            const [x, y, direction] = args.split(",");
            robot.place(parseInt(x), parseInt(y), direction as Direction);
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

readline.on('line', (input: string) => {
    processCommand(input);
});