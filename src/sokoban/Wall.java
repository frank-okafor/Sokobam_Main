package sokoban;

public class Wall extends Occupant {

	public Wall(Cell cell) {
		super(cell);
	}

	@Override
	public boolean isWall() {
		return true;
	}

	@Override
	public char getDisplay() {
		return Sokoban.WALL;
	}
}
