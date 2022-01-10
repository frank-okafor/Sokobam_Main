package sokoban;

import java.util.Vector;

public interface Player {

	public Direction move(Vector<Direction> choices);
}
