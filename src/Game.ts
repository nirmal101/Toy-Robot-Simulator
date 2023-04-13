console.log('Welcome to the Toy Robot Simulation')

interface Position{
    x: number;
    y: number;
    direction: "NORTH"| "EAST" | "SOUTH" | "WEST";
}

class Robot {
    private position?: Position;

    public place(x: number, y: number, direction: "NORTH" | "EAST" | "SOUTH" | "WEST"): void {
        if (x < 0 || x > 4 || y < 0 || y > 4) {
            console.log("Oops! Invalid coordinates, please pick a place inside the table");
            return;
        }
        this.position = { x, y, direction };
    }

    public move(): void {
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        
        const {x, y, direction} = this.position;
        let newX = x;
        let newY = y;

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

        if (newX < 0 || newX > 4 || newY < 0 || newY > 4 ){
            console.log("Move not allowed, the robot will fall from the table");
            return;
        }

        this.position.x = newX;
        this.position.y = newY;

    }
}