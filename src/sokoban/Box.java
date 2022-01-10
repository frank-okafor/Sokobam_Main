package sokoban;

public class Box extends Occupant {

	public Box(Cell cell) {
		super(cell);
	}

	@Override
	public boolean isBox() {
		return true;
	}

	@Override
	public char getDisplay() {
		return (cell.isTarget()) ? Sokoban.TARGET_BOX : Sokoban.BOX;
	}

	@Override
	public boolean onTarget() {
		return cell.isTarget();
	}

	@Override
	public boolean isStuckSafe(Direction dir) {
		Cell next = cell.getCell(dir);
		if (next == null)
			throw new SokobanException("next cannot be null");
		if (!next.isEmpty())
			throw new SokobanException("next must be empty");
		if (!next.isWallSafe())
			throw new SokobanException("next must be wall safe");
		if (next.isTarget()) // okay to get stuck on a target
			return true;
		Cell nextAhead = next.getCell(dir);
		Cell nextLeft = next.getCell(dir.left());
		Cell nextRight = next.getCell(dir.right());
		// might get stuck if we move next to a box
		boolean stuckSafe = (nextAhead.hasBox() || nextLeft.hasBox() || nextRight.hasBox()) ? false : true;
		// ... but not if the box ahead can be moved, or we still can
		if (nextAhead.hasBox()
				&& (next.isMoveableBoxLocation(cell, dir) || nextAhead.isMoveableBoxLocation(cell, next, dir)))
			stuckSafe = true;
		// ... but not if the box on the left can be moved, or we still can
		if (nextLeft.hasBox() && (next.isMoveableBoxLocation(cell, dir.left())
				|| nextLeft.isMoveableBoxLocation(cell, next, dir.left())))
			stuckSafe = true;
		// ... but not if the box on the right can be moved, or we still can
		if (nextRight.hasBox() && (next.isMoveableBoxLocation(cell, dir.right())
				|| nextRight.isMoveableBoxLocation(cell, next, dir.right())))
			stuckSafe = true;
		return stuckSafe;
	}

	@Override
	public boolean canMove(Direction dir) {
		Cell next = cell.getCell(dir);
		return (next != null) && next.isEmpty() && next.isWallSafe() && isStuckSafe(dir);
	}
}
