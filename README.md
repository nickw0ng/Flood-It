# Flood It Game
This Java code represents a simple implementation of the Flood It game. In Flood It, the player's goal is to fill the entire game board with a single color within a limited number of moves.

## CODE STRUCTURE
The code consists of several classes:

### ICell:
An interface representing a cell in the game board. It includes methods to check if the cell is flooded and if it's a regular cell.

### Cell: 
Represents a single square on the game board. Each cell has coordinates, a color, and information about whether it's flooded. It also has references to its adjacent cells. The connectCells method establishes connections between cells based on their positions in the grid.

### MtCell: 
Represents a non-existent cell. It's used to handle border cases where a cell might not have all adjacent neighbors.

### FloodItWorld: 
The main class represents the game world. It manages the game board, tracks the player's clicks, and handles game logic such as flooding cells with the same color. It also provides methods for drawing the game board, handling player input, and determining game outcomes.


## Game Mechanics
The game board is represented as a two-dimensional grid of cells, with each cell having a color.
The player's objective is to flood the entire board with a single color within a limited number of clicks.
Clicking on a cell floods it and adjacent cells of the same color, gradually expanding the flooded area.
The game ends when either the entire board is flooded with a single color (player wins) or the player exceeds the maximum allowed number of clicks (player loses).
Usage
To use this code, you can create a FloodItWorld instance with the desired board size and number of colors. You can then interact with the game world by clicking on cells to flood them and observing the game's progress.

## Examples
The ExamplesFloodIt class provides examples and tests for the game logic, including scenarios with different board configurations and boundary cases.

## Note
Ensure that the provided size and number of colors are within valid ranges to avoid exceptions during initialization. 
