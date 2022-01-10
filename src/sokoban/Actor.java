package sokoban;

public class Actor extends Occupant {

	public Actor(Cell cell) {
		super(cell);
	}

	@Override
	public boolean isActor() {
		return true;
	}

	@Override
	public char getDisplay() {
		return (cell.isTarget()) ? Sokoban.TARGET_ACTOR : Sokoban.ACTOR;
	}

	@Override
	public boolean canMove(Direction dir) {
		Cell next = cell.getCell(dir);
		return (next != null) && (next.isEmpty() || next.canMove(dir));
	}

	public static void trace(String s) {
		if (traceOn)
			System.out.println("trace: " + s);
	}

	private static boolean traceOn = false; // for debugging
}
