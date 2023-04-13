"use strict";
console.log('Welcome to the Toy Robot Simulation');
class Robot {
    place(x, y, direction) {
        if (x < 0 || x > 4 || y < 0 || y > 4) {
            console.log("Oops! Invalid coordinates, please pick a place inside the table");
            return;
        }
        this.position = { x, y, direction };
    }
    move() {
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        const { x, y, direction } = this.position;
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
        if (newX < 0 || newX > 4 || newY < 0 || newY > 4) {
            console.log("Move not allowed, the robot will fall from the table");
            return;
        }
        this.position.x = newX;
        this.position.y = newY;
    }
    right() {
        this.rotate(1);
    }
    left() {
        this.rotate(-1);
    }
    rotate(rotationDirection) {
        //dry
        if (!this.position) {
            console.log("Robot has not been placed yet");
            return;
        }
        const { direction } = this.position;
        //dry
        const directions = ["NORTH", "EAST", "SOUTH", "WEST"];
        const currentDirectionIndex = directions.indexOf(direction);
        const newDirectionIndex = (currentDirectionIndex + rotationDirection + directions.length) % directions.length;
        this.position.direction = directions[newDirectionIndex];
    }
}
