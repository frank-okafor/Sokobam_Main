package sokoban;

import java.util.Iterator;
import java.util.TreeSet;

public class Cell implements Comparable<Cell> {

	public Cell(char display, Sokoban puzzle, int row, int col) {
		if (puzzle == null)
			throw new IllegalArgumentException("puzzle cannot be null");
		if ((row < 0) || (row >= puzzle.getNumRows()))
			throw new IllegalArgumentException("invalid row");
		if ((col < 0) || (col >= puzzle.getNumCols()))
			throw new IllegalArgumentException("invalid col");
		this.puzzle = puzzle;
		this.row = row;
		this.col = col;
		this.target = (display == Sokoban.TARGET || display == Sokoban.TARGET_BOX || display == Sokoban.TARGET_ACTOR)
				? true
				: false;
		this.occ = (display == Sokoban.EMPTY || display == Sokoban.TARGET) ? null : Occupant.getInstance(display, this);
	}

	@Override
	public int compareTo(Cell other) {
		// consider row and col only
		if ((row < other.row) || (col < other.col))
			return -1;
		else if ((row > other.row) || (col > other.col))
			return 1;
		else
			return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof Cell))
			return false;
		Cell other = (Cell) obj;
		if ((row == other.row) && (col == other.col))
			return true;
		else
			return false;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	void setDisplay(char display) {
		this.target = (display == Sokoban.TARGET || display == Sokoban.TARGET_BOX || display == Sokoban.TARGET_ACTOR)
				? true
				: false;
		this.occ = (display == Sokoban.EMPTY || display == Sokoban.TARGET) ? null : Occupant.getInstance(display, this);
	}

	public char getDisplay() {
		return toString().charAt(0);
	}

	void setOccupant(Occupant occ) {
		this.occ = occ;
		if (occ != null)
			occ.setCell(this);
	}

	public boolean isTarget() {
		return target;
	}

	public boolean isEmpty() {
		return (occ == null);
	}

	public boolean hasActor() {
		return (occ == null) ? false : occ.isActor();
	}

	public boolean hasBox() {
		return (occ == null) ? false : occ.isBox();
	}

	public boolean hasWall() {
		return (occ == null) ? false : occ.isWall();
	}

	Cell getCell(Direction dir) {
		if (dir == Direction.NORTH)
			return puzzle.getCell(row - 1, col);
		else if (dir == Direction.SOUTH)
			return puzzle.getCell(row + 1, col);
		else if (dir == Direction.EAST)
			return puzzle.getCell(row, col + 1);
		else // WEST
			return puzzle.getCell(row, col - 1);
	}

	public int numAdjacentWalls() {
		int num = 0;
		for (Direction dir : Direction.values()) {
			Cell next = getCell(dir);
			if ((next != null) && next.hasWall())
				num++;
		}
		return num;
	}

	public boolean isWallSafe() {
		if (hasWall())
			return false;
		else if (isTarget())
			return true;
		else if (numAdjacentWalls() >= 3)
			return false;
		else if (numAdjacentWalls() == 2) {
			// two parallel Walls
			Cell north = getCell(Direction.NORTH);
			Cell south = getCell(Direction.SOUTH);
			Cell east = getCell(Direction.EAST);
			Cell west = getCell(Direction.WEST);
			if (((north != null) && north.hasWall() && (south != null) && south.hasWall())
					|| ((east != null) && east.hasWall() && (west != null) && west.hasWall()))
				return true;
			else // two Walls at 90 degrees
				return false;
		} else // only one or zero adjacent Walls
			return true;
	}

	public boolean isStuckSafe(Direction dir) {
		return isEmpty() ? false : occ.isStuckSafe(dir);
	}

	public boolean isMoveableBoxLocation(Cell origin, Direction dir) {
		if (origin == null)
			throw new IllegalArgumentException("origin cannot be null");
		TreeSet<Cell> visited = new TreeSet<Cell>();
		return isMoveableBoxLocation(origin, dir, visited);
	}

	public boolean isMoveableBoxLocation(Cell origin, Cell taboo, Direction dir) {
		if (origin == null)
			throw new IllegalArgumentException("origin cannot be null");
		if (taboo == null)
			throw new IllegalArgumentException("taboo cannot be null");
		if (taboo.equals(this))
			throw new IllegalArgumentException("taboo cannot be this cell");
		TreeSet<Cell> visited = new TreeSet<Cell>();
		visited.add(taboo);
		return isMoveableBoxLocation(origin, dir, visited);
	}

	private boolean isMoveableBoxLocation(Cell origin, Direction dir, TreeSet<Cell> visited) {
		if (origin == null)
			throw new IllegalArgumentException("origin cannot be null");
		if (visited == null)
			throw new IllegalArgumentException("visited cannot be null");
		Cell left = getCell(dir.left());
		Cell right = getCell(dir.right());

		// check for loops
		if (visited.contains(this))
			return false;
		visited.add(this);
		boolean visitedLeft = visited.contains(left);
		boolean visitedRight = visited.contains(right);

		// could move left
		if ((left != null) && (!visitedLeft)
				&& (((left.isEmpty() || left.hasActor() || left.equals(origin)) && left.isWallSafe())
						|| (left.hasBox() && left.isMoveableBoxLocation(origin, dir.left(), visited)))
				&& (right != null) && (!visitedRight) && (right.isEmpty() || right.hasActor() || right.equals(origin)
						|| (right.hasBox() && right.isMoveableBoxLocation(origin, dir.right(), visited))))
			return true;
		// could move right
		else if ((right != null) && (!visitedRight)
				&& (((right.isEmpty() || right.hasActor() || right.equals(origin)) && right.isWallSafe())
						|| (right.hasBox() && right.isMoveableBoxLocation(origin, dir.right(), visited)))
				&& (left != null) && (!visitedLeft) && (left.isEmpty() || left.hasActor() || left.equals(origin)
						|| (left.hasBox() && left.isMoveableBoxLocation(origin, dir.left(), visited))))
			return true;
		return false;
	}

	public boolean canMove(Direction dir) {
		return isEmpty() ? false : occ.canMove(dir);
	}

	public void move(Direction dir) {
		if (!canMove(dir))
			throw new IllegalArgumentException("cannot move " + dir);
		occ.move(dir);
	}

	public boolean onTarget() {
		return isEmpty() ? false : occ.onTarget();
	}

	@Override
	public String toString() {
		if (isEmpty() && !isTarget())
			return "" + Sokoban.EMPTY;
		else if (isEmpty() && isTarget())
			return "" + Sokoban.TARGET;
		else
			return "" + occ;
	}

	public String toStringFull() {
		StringBuffer b = new StringBuffer("Cell(");
		b.append(row);
		b.append(",");
		b.append(col);
		b.append(",");
		b.append("" + this);
		b.append(")");
		return b.toString();
	}

	public static String TreeSetToString(TreeSet<Cell> visited) {
		if (visited == null)
			throw new IllegalArgumentException("visited cannot be null");
		StringBuffer b = new StringBuffer("[");
		Iterator<Cell> iter = visited.iterator();
		while (iter.hasNext()) {
			b.append(iter.next().toStringFull());
			if (iter.hasNext())
				b.append(",");
		}
		b.append("]");
		return b.toString();
	}

	public static void trace(String s) {
		if (traceOn)
			System.out.println("trace: " + s);
	}

	private Sokoban puzzle = null;
	private int row;
	private int col;
	private Occupant occ = null;
	private boolean target = false;

	private static boolean traceOn = false; // for debugging
}
