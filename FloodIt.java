import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*; 

// Represents a cell (interface datatype which includes the MtCell class 
// to deal with the border)
interface ICell {
  // to check if this ICell is flooded
  boolean floodedICell();

  // to check if this ICell is a cell
  boolean isCell();
}

// Represents a single square of the game area
class Cell implements ICell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int row;
  int col;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  ICell left;
  ICell top;
  ICell right;
  ICell bottom;

  Cell(int row, int col, Color color) {
    this.row = row;
    this.col = col;
    this.color = color;
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // EFFECT: properly connect this cell with the neighbors based on
  // the given size and given board
  void connectCells(int size, ArrayList<ArrayList<Cell>> board) {
    // deal with the top left corner
    if (row == 0 && col == 0) {
      this.left = new MtCell();
      this.top = new MtCell();
      this.right = board.get(row).get(col + 1);
      this.bottom = board.get(row + 1).get(col);
    }
    // deal with the top right corner
    else if (row == 0 && col == size - 1) {
      this.left = board.get(row).get(col - 1);
      this.top = new MtCell();
      this.right = new MtCell();
      this.bottom = board.get(row + 1).get(col);
    }
    // deal with the bottom left corner
    else if (row == size - 1 && col == 0) {
      this.left = new MtCell();
      this.top = board.get(row - 1).get(col);
      this.right = board.get(row).get(col + 1);
      this.bottom = new MtCell();
    }
    // deal with the bottom right corner
    else if (row == size - 1 && col == size - 1) {
      this.left = board.get(row).get(col - 1);
      this.top = board.get(row - 1).get(col);
      this.right = new MtCell();
      this.bottom = new MtCell();
    }
    // deal with the top middle cell
    else if (row == 0 && col > 0 && col < size - 1) {
      this.left = board.get(row).get(col - 1);
      this.top = new MtCell();
      this.right = board.get(row).get(col + 1);
      this.bottom = board.get(row + 1).get(col);
    }
    // deal with the bottom middle cell
    else if (row == size - 1 && col > 0 && col < size - 1) {
      this.left = board.get(row).get(col - 1);
      this.top = board.get(row - 1).get(col);
      this.right = board.get(row).get(col + 1);
      this.bottom = new MtCell();
    }
    // deal with the left border middle cell
    else if (col == 0 && row > 0 && row < size - 1) {
      this.left = new MtCell();
      this.top = board.get(row - 1).get(col);
      this.right = board.get(row).get(col + 1);
      this.bottom = board.get(row + 1).get(col);
    }
    // deal with the right border middle cell
    else if (col == size - 1 && row > 0 && row < size - 1) {
      this.left = board.get(row).get(col - 1);
      this.top = board.get(row - 1).get(col);
      this.right = new MtCell();
      this.bottom = board.get(row + 1).get(col);
    }
    // deal with the middle cell
    else {
      this.left = board.get(row).get(col - 1);
      this.top = board.get(row - 1).get(col);
      this.right = board.get(row).get(col + 1);
      this.bottom = board.get(row + 1).get(col);
    }
  }

  // EFFECT: to update this cell to be flooded
  void updateFlooded() {
    this.flooded = true;
  }

  // to check if this cell is flooded
  boolean flooded() {
    return this.flooded;
  }

  // to check if this cell had the same color as the given cell
  boolean sameColorCell(Cell that) {
    return this.color.equals(that.color);
  }

  // to return the color of this cell
  Color getColor() {
    return this.color;
  }

  // to check if this Cell is flooded
  public boolean floodedICell() {
    return this.flooded;
  }

  // to check if this cell is adjacent to flooded cell
  boolean adjToFlooded() {
    return this.left.floodedICell() || this.top.floodedICell() || this.right.floodedICell()
        || this.bottom.floodedICell();
  }

  // EFFECT: to modify the color of this cell to be the given color
  void updateColor(Color floodColor) {
    this.color = floodColor;
  }

  // to check if this Cell is a cell
  public boolean isCell() {
    return true;
  }

  // to check if this cell is the same as the given cell (has the same row and
  // column number)
  boolean sameCell(Cell that) {
    return this.row == that.row && this.col == that.col;
  }

  // draw the image of this cell (rectangle with size 20)
  WorldImage drawCell() {
    return new RectangleImage(20, 20, OutlineMode.SOLID, this.color);
  }

  // to check if the color of this cell is the same as the given color
  boolean sameColor(Color floodColor) {
    return this.color.equals(floodColor);
  }
}

// Represents an unexisted cell (to deal with the border)
class MtCell implements ICell {
  MtCell() {

  }

  // to check if this MtCell is flooded
  public boolean floodedICell() {
    return false;
  }

  // to check if this MtCell is a cell
  public boolean isCell() {
    return false;
  }
}

// to represent the Flood It World
class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;
  ArrayList<Cell> worklist = new ArrayList<Cell>();
  // ArrayList of the default colors
  ArrayList<Color> colors = new ArrayList<Color>(Arrays.asList(Color.RED, Color.YELLOW, Color.GREEN,
      Color.ORANGE, Color.BLACK, Color.CYAN, Color.BLUE, Color.PINK));
  Random rand = new Random();
  Color floodColor = null;
  int clicks = 0;
  int maxClicks;
  int time = 0;
  int numColor;

  // the consructor: make sure the number of color is within the range of [3, 8]
  // and the minimum of size is 2 so that the new created board is reasonable to
  // be played
  FloodItWorld(int size, int numColor) {
    if (numColor > 8) {
      throw new IllegalArgumentException("Invalid number of colors, the maximum is 8");
    }
    else if (numColor < 3) {
      throw new IllegalArgumentException("Invalid number of colors, the minimum is 3");
    }
    else if (size < 2) {
      throw new IllegalArgumentException("Invalid size, the minimum is 2");
    }
    else {
      this.numColor = numColor;
      this.board = this.createBoard(size, numColor);
      this.connectCells(size);
      this.startGame();
      this.maxClicks = this.calMaxClicks(size, numColor);
    }
  }

  // the constructor for testing
  FloodItWorld(int size, int numColor, int seed) {
    this.numColor = numColor;
    this.rand = new Random(seed);
    this.board = this.createBoard(size, numColor);
    this.maxClicks = this.calMaxClicks(size, numColor);
  }

  // the constructor for testing
  FloodItWorld(int seed) {
    this.rand = new Random(seed);
    this.board = null;
  }

  // to create the two-dimensional grid using the given size and number of color
  ArrayList<ArrayList<Cell>> createBoard(int size, int numColor) {
    ArrayList<Cell> rowBoard; // to store the each created row
    ArrayList<ArrayList<Cell>> board = new ArrayList<ArrayList<Cell>>();

    for (int row = 0; row < size; row = row + 1) {
      rowBoard = new ArrayList<Cell>();
      for (int col = 0; col < size; col = col + 1) {
        rowBoard.add(new Cell(row, col, this.colors.get(this.rand.nextInt(numColor))));
      }
      board.add(rowBoard);
    }
    return board;
  }

  // EFFECT: updates the upper left cell of the board and also the adjacent cells
  // with the same color to be flooded
  void startGame() {
    this.board.get(0).get(0).updateFlooded();
    this.floodColor = this.board.get(0).get(0).getColor();
    this.floodedAdjSameColor();
  }

  // EFFECT: to connect the cells in this world based on the given size of grid
  void connectCells(int size) {
    for (int row = 0; row < size; row = row + 1) {
      for (int col = 0; col < size; col = col + 1) {
        this.board.get(row).get(col).connectCells(size, this.board);
      }
    }
  }

  // to calculate the max clicks based on the size and number of color
  int calMaxClicks(int size, int numColor) {
    if (numColor % 2 != 0) {
      return (size / 2) * ((numColor + 1) / 2);
    }
    else {
      return (size / 2) * (numColor / 2 + 1);
    }
  }

  // to make the scene of this world
  public WorldScene makeScene() {
    int size = this.board.size();
    WorldScene ws = new WorldScene(size * 20 + 250, size * 20 + 60); // the size of each cell is 20
    for (int row = 0; row < size; row = row + 1) {
      for (int col = 0; col < size; col = col + 1) {
        ws.placeImageXY(this.board.get(row).get(col).drawCell(), col * 20 + 10, row * 20 + 10);
      }
    }
    ws.placeImageXY(new TextImage(
        "Clicks: " + Integer.toString(this.clicks) + "/" + Integer.toString(this.maxClicks), 20,
        Color.BLACK), size * 20 + 125, 20);
    ws.placeImageXY(new TextImage("Time: " + Integer.toString(this.time), 20, Color.BLACK),
        size * 20 + 125, 50);
    ws.placeImageXY(new TextImage("Press the ‘r’ key to reset the game ", 16, Color.BLACK),
        size * 10 + 125, size * 20 + 30);
    return ws;
  }

  // EFFECT: if the cell in the given mouse location is unflooded and has the
  // different color
  // as the flooded area, its color will be recorded, the method
  // floodedAdjSameColor will be called
  // and the top left cell will be added to the worklist
  public void onMouseClicked(Posn pos) {
    int row = pos.y / 20;
    int col = pos.x / 20;

    // check if the cell is unflooded
    if (!this.board.get(row).get(col).flooded()) {
      // check if the cell has the different color as the flooded area
      if (!this.board.get(row).get(col).sameColorCell(this.board.get(0).get(0))) {
        this.floodColor = this.board.get(row).get(col).getColor();
        this.floodedAdjSameColor();
        this.worklist.add(this.board.get(0).get(0));
        this.clicks = this.clicks + 1;
      }
    }
  }

  // EFFECT: to update the unflooded cell which is adjacent to the flooded ones
  // and has the same color as floodColor starting from the top left corner
  void floodedAdjSameColor() {
    int size = this.board.size();

    for (int row = 0; row < size; row = row + 1) {
      for (int col = 0; col < size; col = col + 1) {
        Cell cell = this.board.get(row).get(col);

        if (!cell.flooded() // to check if current cell is unflooded
            && cell.adjToFlooded() // and if this cell is adjacent to flooded cell
            // and if this cell has the same color as flooded color
            && cell.getColor().equals(this.floodColor)) {
          cell.updateFlooded();
        }
      }
    }
  }

  // EFFECT: handles ticking of the clock and updating the world if needed
  public void onTick() {
    if (this.worklist.size() > 0) {
      Cell cell = this.worklist.get(0);
      // change the color of cell if it is already flooded
      if (cell.flooded()) {
        cell.updateColor(this.floodColor);
      }
      // if this cell's right and bottom cell is not MtCell and not contained in the
      // worklist, add them to the worklist
      this.addIfNotMtCellAndContained(cell.right);
      this.addIfNotMtCellAndContained(cell.bottom);
      this.worklist.remove(0); // remove the first one in the worklist
    }
    else {
      // to check if the user wins
      if (this.allFlooded() && this.clicks <= this.maxClicks) {
        this.endOfWorld("YOU WIN!");
      }
      // to check if the user loses
      else if (!this.allFlooded() && this.clicks == this.maxClicks) {
        this.endOfWorld("YOU LOSE!");
      }
    }
    this.time = this.time + 1;
  }

  // EFFECT: add the given ICell to the worklist if it is not MtCell and not
  // already
  // contained in the worklist
  void addIfNotMtCellAndContained(ICell item) {
    if (item.isCell()) {
      boolean contained = false;
      Cell specificItem = (Cell) item;

      // to chekc if the cell is already contained in the worklist
      for (Cell cell : worklist) {
        contained = contained || cell.sameCell(specificItem);
      }
      if (!contained) {
        this.worklist.add(specificItem);
      }
    }
  }

  // EFFECT: to reset the game if user presses the ‘r’ key
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      int size = this.board.size();
      this.board = this.createBoard(size, this.numColor);
      this.worklist = new ArrayList<Cell>();
      this.floodColor = null;
      this.clicks = 0;
      this.connectCells(size);
      this.startGame();
      this.maxClicks = this.calMaxClicks(size, this.numColor);
      this.time = 0;
    }
  }

  // to check if the whole board is all flooded as the same color
  boolean allFlooded() {
    boolean allFlooded = true;
    int size = this.board.size();

    for (int row = 0; row < size; row = row + 1) {
      for (int col = 0; col < size; col = col + 1) {
        allFlooded = allFlooded && this.board.get(row).get(col).sameColor(this.floodColor);
      }
    }
    return allFlooded;
  }

  // EFFECT: update the scene based on the win or lose message
  public WorldScene lastScene(String msg) {
    int size = this.board.size();
    WorldScene ws = new WorldScene(size * 20 + 250, size * 20 + 60);
    if (msg.equals("YOU WIN!")) {
      ws.placeImageXY(new TextImage("YOU WIN!", 30, Color.BLACK), size * 10 + 125, size * 6 + 20);
      ws.placeImageXY(new TextImage("Score: " + Integer.toString(this.clicks), 20, Color.BLACK),
          size * 10 + 125, size * 12 + 40);
      ws.placeImageXY(new TextImage("Time: " + Integer.toString(this.time), 20, Color.BLACK),
          size * 10 + 125, size * 12 + 60);
    }
    else if (msg.equals("YOU LOSE!")) {
      ws.placeImageXY(new TextImage("YOU LOSE!", 30, Color.BLACK), size * 10 + 125, size * 6 + 20);
      ws.placeImageXY(new TextImage("Score: " + Integer.toString(this.clicks), 20, Color.BLACK),
          size * 10 + 125, size * 12 + 40);
      ws.placeImageXY(new TextImage("Time: " + Integer.toString(this.time), 20, Color.BLACK),
          size * 10 + 125, size * 12 + 60);
    }
    return ws;
  }
} 

// to represent the examples and tests for FloodItWorld class
class ExamplesFloodIt {
  FloodItWorld world1;
  FloodItWorld world2;
  ICell mtCell;
  MtCell mtCell1;
  Cell cell11;
  Cell cell12;
  Cell cell13;
  Cell cell14;
  Cell cell21;
  Cell cell22;
  Cell cell23;
  Cell cell24;
  Cell cell25;
  Cell cell26;
  Cell cell27;
  Cell cell28;
  Cell cell29;
  Cell cell31;
  Cell cell32;
  Cell cell33;
  Cell cell34;
  Cell cell35;
  Cell cell36;
  Cell cell37;
  Cell cell38;
  Cell cell39;
  ICell cell41;
  ArrayList<ArrayList<Cell>> board1;
  ArrayList<ArrayList<Cell>> board2;
  ArrayList<Posn> posns;

  // to initialize the examples
  void initFloodIt() {
    this.world1 = new FloodItWorld(5); // board is null
    this.world2 = new FloodItWorld(3, 4, 5); // not connected board
    this.mtCell = new MtCell();

    this.cell11 = new Cell(0, 0, Color.GREEN);
    this.cell12 = new Cell(0, 1, Color.YELLOW);
    this.cell13 = new Cell(1, 0, Color.GREEN);
    this.cell14 = new Cell(1, 1, Color.GREEN);
    this.board1 = new ArrayList<ArrayList<Cell>>(
        Arrays.asList(new ArrayList<Cell>(Arrays.asList(this.cell11, this.cell12)),
            new ArrayList<Cell>(Arrays.asList(this.cell13, this.cell14))));

    this.cell21 = new Cell(0, 0, Color.YELLOW);
    this.cell22 = new Cell(0, 1, Color.ORANGE);
    this.cell23 = new Cell(0, 2, Color.YELLOW);
    this.cell24 = new Cell(1, 0, Color.ORANGE);
    this.cell25 = new Cell(1, 1, Color.YELLOW);
    this.cell26 = new Cell(1, 2, Color.YELLOW);
    this.cell27 = new Cell(2, 0, Color.GREEN);
    this.cell28 = new Cell(2, 1, Color.ORANGE);
    this.cell29 = new Cell(2, 2, Color.YELLOW);
    this.board2 = new ArrayList<ArrayList<Cell>>(
        Arrays.asList(new ArrayList<Cell>(Arrays.asList(this.cell21, this.cell22, this.cell23)),
            new ArrayList<Cell>(Arrays.asList(this.cell24, this.cell25, this.cell26)),
            new ArrayList<Cell>(Arrays.asList(this.cell27, this.cell28, this.cell29))));

    this.cell31 = new Cell(0, 0, Color.GREEN);
    this.cell32 = new Cell(0, 1, Color.RED);
    this.cell33 = new Cell(0, 2, Color.RED);
    this.cell34 = new Cell(1, 0, Color.GREEN);
    this.cell35 = new Cell(1, 1, Color.YELLOW);
    this.cell36 = new Cell(1, 2, Color.ORANGE);
    this.cell37 = new Cell(2, 0, Color.YELLOW);
    this.cell38 = new Cell(2, 1, Color.ORANGE);
    this.cell39 = new Cell(2, 2, Color.YELLOW);

    this.posns = new ArrayList<Posn>(Arrays.asList(new Posn(1, 0), new Posn(0, 1)));

    this.cell41 = new Cell(0, 0, Color.BLACK);
    this.mtCell1 = new MtCell();
  }

  // test the method createBoard in FloodItWorld class
  void testCreateBoard(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.world1.createBoard(2, 3), this.board1);
    t.checkExpect(this.world1.createBoard(3, 4), this.board2);
  }

  // test the method connectCells in Cell class
  void testCellConnectCells(Tester t) {
    // test the top left corner
    this.initFloodIt();
    t.checkExpect(this.cell21.left, null);
    t.checkExpect(this.cell21.top, null);
    t.checkExpect(this.cell21.right, null);
    t.checkExpect(this.cell21.bottom, null);
    this.cell21.connectCells(3, this.board2);
    t.checkExpect(this.cell21.left, this.mtCell);
    t.checkExpect(this.cell21.top, this.mtCell);
    t.checkExpect(this.cell21.right, this.cell22);
    t.checkExpect(this.cell21.bottom, this.cell24);

    // test the top right corner
    this.initFloodIt();
    t.checkExpect(this.cell23.left, null);
    t.checkExpect(this.cell23.top, null);
    t.checkExpect(this.cell23.right, null);
    t.checkExpect(this.cell23.bottom, null);
    this.cell23.connectCells(3, this.board2);
    t.checkExpect(this.cell23.left, this.cell22);
    t.checkExpect(this.cell23.top, this.mtCell);
    t.checkExpect(this.cell23.right, this.mtCell);
    t.checkExpect(this.cell23.bottom, this.cell26);

    // test the bottom left corner
    this.initFloodIt();
    t.checkExpect(this.cell27.left, null);
    t.checkExpect(this.cell27.top, null);
    t.checkExpect(this.cell27.right, null);
    t.checkExpect(this.cell27.bottom, null);
    this.cell27.connectCells(3, this.board2);
    t.checkExpect(this.cell27.left, this.mtCell);
    t.checkExpect(this.cell27.top, this.cell24);
    t.checkExpect(this.cell27.right, this.cell28);
    t.checkExpect(this.cell27.bottom, this.mtCell);

    // test the bottom right corner
    this.initFloodIt();
    t.checkExpect(this.cell29.left, null);
    t.checkExpect(this.cell29.top, null);
    t.checkExpect(this.cell29.right, null);
    t.checkExpect(this.cell29.bottom, null);
    this.cell29.connectCells(3, this.board2);
    t.checkExpect(this.cell29.left, this.cell28);
    t.checkExpect(this.cell29.top, this.cell26);
    t.checkExpect(this.cell29.right, this.mtCell);
    t.checkExpect(this.cell29.bottom, this.mtCell);

    // test the top middle cell
    this.initFloodIt();
    t.checkExpect(this.cell22.left, null);
    t.checkExpect(this.cell22.top, null);
    t.checkExpect(this.cell22.right, null);
    t.checkExpect(this.cell22.bottom, null);
    this.cell22.connectCells(3, this.board2);
    t.checkExpect(this.cell22.left, this.cell21);
    t.checkExpect(this.cell22.top, this.mtCell);
    t.checkExpect(this.cell22.right, this.cell23);
    t.checkExpect(this.cell22.bottom, this.cell25);

    // test the bottom middle cell
    this.initFloodIt();
    t.checkExpect(this.cell28.left, null);
    t.checkExpect(this.cell28.top, null);
    t.checkExpect(this.cell28.right, null);
    t.checkExpect(this.cell28.bottom, null);
    this.cell28.connectCells(3, this.board2);
    t.checkExpect(this.cell28.left, this.cell27);
    t.checkExpect(this.cell28.top, this.cell25);
    t.checkExpect(this.cell28.right, this.cell29);
    t.checkExpect(this.cell28.bottom, this.mtCell);

    // test the left border middle cell
    this.initFloodIt();
    t.checkExpect(this.cell24.left, null);
    t.checkExpect(this.cell24.top, null);
    t.checkExpect(this.cell24.right, null);
    t.checkExpect(this.cell24.bottom, null);
    this.cell24.connectCells(3, this.board2);
    t.checkExpect(this.cell24.left, this.mtCell);
    t.checkExpect(this.cell24.top, this.cell21);
    t.checkExpect(this.cell24.right, this.cell25);
    t.checkExpect(this.cell24.bottom, this.cell27);

    // test the right border middle cell
    this.initFloodIt();
    t.checkExpect(this.cell26.left, null);
    t.checkExpect(this.cell26.top, null);
    t.checkExpect(this.cell26.right, null);
    t.checkExpect(this.cell26.bottom, null);
    this.cell26.connectCells(3, this.board2);
    t.checkExpect(this.cell26.left, this.cell25);
    t.checkExpect(this.cell26.top, this.cell23);
    t.checkExpect(this.cell26.right, this.mtCell);
    t.checkExpect(this.cell26.bottom, this.cell29);

    // test the middle cell
    this.initFloodIt();
    t.checkExpect(this.cell25.left, null);
    t.checkExpect(this.cell25.top, null);
    t.checkExpect(this.cell25.right, null);
    t.checkExpect(this.cell25.bottom, null);
    this.cell25.connectCells(3, this.board2);
    t.checkExpect(this.cell25.left, this.cell24);
    t.checkExpect(this.cell25.top, this.cell22);
    t.checkExpect(this.cell25.right, this.cell26);
    t.checkExpect(this.cell25.bottom, this.cell28);
  }

  // test the method connectCells in FloodItWorld class
  void testFloodItWorldConnectCells(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.world2.board.get(0).get(0).left, null);
    t.checkExpect(this.world2.board.get(0).get(0).top, null);
    t.checkExpect(this.world2.board.get(0).get(0).right, null);
    t.checkExpect(this.world2.board.get(0).get(0).bottom, null);
    t.checkExpect(this.world2.board.get(0).get(1).left, null);
    t.checkExpect(this.world2.board.get(0).get(1).top, null);
    t.checkExpect(this.world2.board.get(0).get(1).right, null);
    t.checkExpect(this.world2.board.get(0).get(1).bottom, null);
    t.checkExpect(this.world2.board.get(1).get(1).left, null);
    t.checkExpect(this.world2.board.get(1).get(1).top, null);
    t.checkExpect(this.world2.board.get(1).get(1).right, null);
    t.checkExpect(this.world2.board.get(1).get(1).bottom, null);
    this.world2.connectCells(3);
    t.checkExpect(this.world2.board.get(0).get(0).left, this.mtCell);
    t.checkExpect(this.world2.board.get(0).get(0).top, this.mtCell);
    t.checkExpect(this.world2.board.get(0).get(0).right, this.world2.board.get(0).get(1));
    t.checkExpect(this.world2.board.get(0).get(0).bottom, this.world2.board.get(1).get(0));
    t.checkExpect(this.world2.board.get(0).get(1).left, this.world2.board.get(0).get(0));
    t.checkExpect(this.world2.board.get(0).get(1).top, this.mtCell);
    t.checkExpect(this.world2.board.get(0).get(1).right, this.world2.board.get(0).get(2));
    t.checkExpect(this.world2.board.get(0).get(1).bottom, this.world2.board.get(1).get(1));
    t.checkExpect(this.world2.board.get(1).get(1).left, this.world2.board.get(1).get(0));
    t.checkExpect(this.world2.board.get(1).get(1).top, this.world2.board.get(0).get(1));
    t.checkExpect(this.world2.board.get(1).get(1).right, this.world2.board.get(1).get(2));
    t.checkExpect(this.world2.board.get(1).get(1).bottom, this.world2.board.get(2).get(1));
  }

  // test the method calMaxClicks in FloodItWorld class
  void testCalMaxClicks(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.world2.calMaxClicks(3, 4), 3);
    t.checkExpect(this.world2.calMaxClicks(3, 5), 3);
  }

  // test the method drawCell in Cell class
  void testDrawCell(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell11.drawCell(),
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.GREEN));
    t.checkExpect(this.cell12.drawCell(),
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.YELLOW));
  }

  // test the method makeScene in FloodItWorld class
  void testMakeScene(Tester t) {
    this.initFloodIt();
    WorldScene bg = new WorldScene(310, 120);
    bg.placeImageXY(this.cell31.drawCell(), 10, 10);
    bg.placeImageXY(this.cell32.drawCell(), 30, 10);
    bg.placeImageXY(this.cell33.drawCell(), 50, 10);
    bg.placeImageXY(this.cell34.drawCell(), 10, 30);
    bg.placeImageXY(this.cell35.drawCell(), 30, 30);
    bg.placeImageXY(this.cell36.drawCell(), 50, 30);
    bg.placeImageXY(this.cell37.drawCell(), 10, 50);
    bg.placeImageXY(this.cell38.drawCell(), 30, 50);
    bg.placeImageXY(this.cell39.drawCell(), 50, 50);
    bg.placeImageXY(new TextImage("Clicks: " + Integer.toString(0) + "/" + Integer.toString(3), 20,
        Color.BLACK), 185, 20);
    bg.placeImageXY(new TextImage("Time: " + Integer.toString(0), 20, Color.BLACK), 185, 50);
    bg.placeImageXY(new TextImage("Press the ‘r’ key to reset the game ", 16, Color.BLACK), 155,
        90);
    t.checkExpect(this.world2.makeScene(), bg);
  }

  // test the method updateFlooded in Cell class
  void testUpdateFlooded(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.flooded, false);
    this.cell31.updateFlooded();
    t.checkExpect(this.cell31.flooded, true);
  }

  // test the method startGame in FloodItWorld class
  void testStartGame(Tester t) {
    this.initFloodIt();
    this.world2.connectCells(3);
    t.checkExpect(this.world2.board.get(0).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(0).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(0).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(2).flooded, false);
    t.checkExpect(this.world2.floodColor, null);
    this.world2.startGame();
    t.checkExpect(this.world2.board.get(0).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(0).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(0).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(1).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(2).flooded, false);
    t.checkExpect(this.world2.floodColor, Color.GREEN);
  }

  // test the method flooded in Cell class
  void testFlooded(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.flooded(), false);
    t.checkExpect(this.cell39.flooded(), false);
  }

  // test the method sameColor in Cell class
  void testSameColor(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.sameColorCell(this.cell34), true);
    t.checkExpect(this.cell31.sameColorCell(this.cell32), false);
  }

  // test the method getColor in Cell class
  void testGetColor(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.getColor(), Color.GREEN);
    t.checkExpect(this.cell32.getColor(), Color.RED);
  }

  // test the method floodedICell in MtCell class
  void testMtCellFloodedICell(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.mtCell.floodedICell(), false);
  }

  // test the method floodedICell in Cell class
  void testCellFloodedICell(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.floodedICell(), false);
    t.checkExpect(this.cell39.floodedICell(), false);
  }

  // test the method adjToFlooded in Cell class
  void testAdjToFlooded(Tester t) {
    this.initFloodIt();
    this.world2.connectCells(3);
    this.world2.startGame();

    t.checkExpect(this.world2.board.get(0).get(1).adjToFlooded(), true);
    t.checkExpect(this.world2.board.get(2).get(0).adjToFlooded(), true);
    t.checkExpect(this.world2.board.get(0).get(2).adjToFlooded(), false);
    t.checkExpect(this.world2.board.get(2).get(2).adjToFlooded(), false);
  }

  // test the method floodedAdjSameColor in FloodItWorld class
  void testFloodedAdjSameColor(Tester t) {
    this.initFloodIt();
    this.world2.connectCells(3);
    this.world2.startGame();
    this.world2.floodColor = Color.RED;

    t.checkExpect(this.world2.board.get(0).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(0).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(0).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(1).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(2).flooded, false);
    this.world2.floodedAdjSameColor();
    t.checkExpect(this.world2.board.get(0).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(0).get(1).flooded, true);
    t.checkExpect(this.world2.board.get(0).get(2).flooded, true);
    t.checkExpect(this.world2.board.get(1).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(1).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(2).flooded, false);
  }

  // test the method onMouseClicked in FloodItWorld class
  void testOnMouseClicked(Tester t) {
    this.initFloodIt();
    this.world2.connectCells(3);
    this.world2.startGame();

    t.checkExpect(this.world2.floodColor, Color.GREEN);
    t.checkExpect(this.world2.board.get(0).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(0).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(0).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(1).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(2).flooded, false);
    t.checkExpect(this.world2.worklist, new ArrayList<Cell>());
    t.checkExpect(this.world2.clicks, 0);
    this.world2.onMouseClicked(new Posn(30, 10));
    t.checkExpect(this.world2.floodColor, Color.RED);
    t.checkExpect(this.world2.board.get(0).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(0).get(1).flooded, true);
    t.checkExpect(this.world2.board.get(0).get(2).flooded, true);
    t.checkExpect(this.world2.board.get(1).get(0).flooded, true);
    t.checkExpect(this.world2.board.get(1).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(1).get(2).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(0).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(1).flooded, false);
    t.checkExpect(this.world2.board.get(2).get(2).flooded, false);
    t.checkExpect(this.world2.worklist,
        new ArrayList<Cell>(Arrays.asList(this.world2.board.get(0).get(0))));
    t.checkExpect(this.world2.clicks, 1);
  }

  // test the method addIfNotMtCellAndContained in FloodItWorld class
  void testAddIfNotMtCellAndContained(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.world2.worklist, new ArrayList<Cell>());
    this.world2.addIfNotMtCellAndContained(this.cell41);
    t.checkExpect(this.world2.worklist, new ArrayList<Cell>(Arrays.asList((Cell) this.cell41)));
  }

  // test the method allFlooded in FloodItWorld class
  void testAllFlooded(Tester t) {
    this.initFloodIt();
    this.world1.board = new ArrayList<ArrayList<Cell>>(Arrays.asList(
        new ArrayList<Cell>(Arrays.asList(new Cell(0, 0, Color.RED), new Cell(0, 1, Color.RED))),
        new ArrayList<Cell>(Arrays.asList(new Cell(1, 0, Color.RED), new Cell(1, 1, Color.RED)))));
    this.world1.floodColor = Color.RED;
    this.world2.floodColor = Color.RED;
    t.checkExpect(this.world2.allFlooded(), false);
    t.checkExpect(this.world1.allFlooded(), true);
  }

  // test the method onTick in FloodItWorld class
  void testOnTick(Tester t) {
    this.initFloodIt();
    this.world2.connectCells(3);
    this.world2.startGame();
    this.world2.worklist.add(this.world2.board.get(0).get(0));
    t.checkExpect(this.world2.worklist,
        new ArrayList<Cell>(Arrays.asList(this.world2.board.get(0).get(0))));
    this.world2.onTick();
    t.checkExpect(this.world2.worklist, new ArrayList<Cell>(
        Arrays.asList(this.world2.board.get(0).get(1), this.world2.board.get(1).get(0))));
  }

  // test the method onKeyEvent in FloodItWorld class
  void testOnKeyEvent(Tester t) {
    this.initFloodIt();
    this.world2.connectCells(3);
    this.world2.startGame();
    this.world2.worklist.add(this.world2.board.get(0).get(0));
    this.world2.clicks = 20;
    this.world2.time = 20;
    t.checkExpect(this.world2.worklist,
        new ArrayList<Cell>(Arrays.asList(this.world2.board.get(0).get(0))));
    t.checkExpect(this.world2.clicks, 20);
    t.checkExpect(this.world2.time, 20);
    this.world2.onKeyEvent("r");
    t.checkExpect(this.world2.worklist, new ArrayList<Cell>(Arrays.asList()));
    t.checkExpect(this.world2.clicks, 0);
    t.checkExpect(this.world2.time, 0);
  }

  // test the method lastScene in FloodItWorld class
  void testLastScene(Tester t) {
    this.initFloodIt();
    WorldScene ws1 = new WorldScene(310, 120);
    ws1.placeImageXY(new TextImage("YOU WIN!", 30, Color.BLACK), 155, 38);
    ws1.placeImageXY(new TextImage("Score: " + Integer.toString(0), 20, Color.BLACK), 155, 76);
    ws1.placeImageXY(new TextImage("Time: " + Integer.toString(0), 20, Color.BLACK), 155, 96);

    WorldScene ws2 = new WorldScene(310, 120);
    ws2.placeImageXY(new TextImage("YOU LOSE!", 30, Color.BLACK), 155, 38);
    ws2.placeImageXY(new TextImage("Score: " + Integer.toString(0), 20, Color.BLACK), 155, 76);
    ws2.placeImageXY(new TextImage("Time: " + Integer.toString(0), 20, Color.BLACK), 155, 96);

    t.checkExpect(this.world2.lastScene("YOU XXX"), new WorldScene(310, 120));
    t.checkExpect(this.world2.lastScene("YOU WIN!"), ws1);
    t.checkExpect(this.world2.lastScene("YOU LOSE!"), ws2);
  }

  // test the method updateColor in Cell class
  void testUpdateColor(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.color, Color.GREEN);
    this.cell31.updateColor(Color.RED);
    t.checkExpect(this.cell31.color, Color.RED);
  }

  // test the method isCell in Cell class
  void testCellIsCell(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.isCell(), true);
    t.checkExpect(this.cell32.isCell(), true);
  }

  // test the method isCell in MtCell class
  void testMtCellIsCell(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.mtCell1.isCell(), false);
  }

  // test the method sameCell in Cell class
  void testSameCell(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.sameCell(this.cell21), true);
    t.checkExpect(this.cell31.sameCell(this.cell22), false);
  }

  // test the method sameColor in Cell class
  void testSecSameColor(Tester t) {
    this.initFloodIt();
    t.checkExpect(this.cell31.sameColor(Color.GREEN), true);
    t.checkExpect(this.cell21.sameColor(Color.RED), false);
  }

  // to start the game and render the world
  void testFloodIt(Tester t) {
    FloodItWorld world3 = new FloodItWorld(10, 5);
    int sceneSize = world3.board.size();
    world3.bigBang(sceneSize * 20 + 250, sceneSize * 20 + 60, 0.01);
  }
}