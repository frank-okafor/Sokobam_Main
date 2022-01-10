package sokoban;

public abstract class Occupant {
	public Occupant(Cell cell) {

		if (cell == null)
			throw new IllegalArgumentException("cell cannot be null");
		this.cell = cell;
	}

	public boolean isActor() {
		return false;
	}

	public boolean isBox() {
		return false;
	}

	public boolean isWall() {
		return false;
	}

	public abstract char getDisplay();

	void setCell(Cell cell) {
		if (cell == null)
			throw new IllegalArgumentException("cell cannot be null");
		this.cell = cell;
	}

	public boolean isStuckSafe(Direction dir) {
		return false;
	}

	public boolean canMove(Direction dir) {
		return false;
	}

	public void move(Direction dir) {
		if (!canMove(dir))
			throw new IllegalArgumentException("cannot move " + dir);
		Cell next = cell.getCell(dir);
		if (!next.isEmpty())
			next.move(dir);
		cell.setOccupant(null);
		next.setOccupant(this);
	}

	public boolean onTarget() {
		return false;
	}

	@Override
	public String toString() {
		return "" + getDisplay();
	}

	public static Occupant getInstance(char display, Cell cell) {
		if (!Sokoban.validDisplay(display))
			throw new IllegalArgumentException("not valid display character for an Occupant");
		if (cell == null)
			throw new IllegalArgumentException("cell cannot be null");
		if (cell.isTarget() && (display != Sokoban.TARGET_BOX) && (display != Sokoban.TARGET_ACTOR))
			throw new IllegalArgumentException("if cell is target, display must be '" + Sokoban.TARGET_BOX + "' or '"
					+ Sokoban.TARGET_ACTOR + "'");
		if (display == Sokoban.WALL)
			return new Wall(cell);
		else if (display == Sokoban.BOX || display == Sokoban.TARGET_BOX)
			return new Box(cell);
		else // is ACTOR or TARGET_ACTOR
			return new Actor(cell);
	}

	protected Cell cell = null;
}
