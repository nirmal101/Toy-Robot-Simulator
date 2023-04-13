# TOY-ROBOT-SIMULATOR
This is a simple simulation of a toy robot that can be placed on a 5x5 tabletop and can move and rotate.

## Prerequisites
Before you begin, make sure you have the following software installed:

* Node.js (version 14 or higher)
* npm (version 6 or higher)
## Installation
1. Clone this repository: git clone https://github.com/nirmal101/Toy-Robot-Simulator.git
2. Install the dependencies: 
```
npm install
```
## How to Build/Compile and Run
This simulation is written in TypeScript. To build and run it, you need to have TypeScript installed. You can install TypeScript using npm by running the following command:
```
npm install -g typescript
```
After installing TypeScript, navigate to the base directory and run the following command to compile the TypeScript code into JavaScript:
```
tsc
```
This will generate a JavaScript file named Game.js in the dist directory. Navigate to the Dist directory and then you can then run the simulation by executing the Game.js file using Node.js:

```
node Game.js
```

## Commands
* **PLACE X,Y,DIRECTION**    
X and Y are integers that indicate a location on the tabletop.
DIRECTION is a string indicating which direction the robot should face. It it one of the four cardinal directions: NORTH, EAST, SOUTH or WEST.
* **MOVE**  
Instructs the robot to move 1 square in the direction it is facing.
* **LEFT**  
Instructs the robot to rotate 90° anticlockwise/counterclockwise.
* **RIGHT**  
Instructs the robot to rotate 90° clockwise.
* **REPORT**  
Outputs the robot's current location on the tabletop and the direction it is facing.
## Sample Data
You can use the following sample commands to interact with the toy robot:
```
PLACE 0,0,NORTH
MOVE
REPORT // Output will be "0,1,NORTH"

PLACE 0,0,NORTH
LEFT
REPORT // Output will be  "0,0,WEST"

PLACE 1,2,EAST
MOVE
MOVE
LEFT
MOVE
REPORT // Output will be  "3,3,NORTH"

PLACE 4,4,NORTH
MOVE // Output will be  "Invalid Position"
```


## Assumptions
* The tabletop is a 5x5 grid.
* The robot cannot be placed outside of the tabletop.
* The robot can only move one step at a time.
* The robot cannot move outside of the tabletop.
* The robot can only rotate 90 degrees at a time.
* The robot's initial direction is always facing north.