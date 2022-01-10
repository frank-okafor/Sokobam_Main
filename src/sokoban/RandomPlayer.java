package sokoban;

import java.util.Random;
import java.util.Vector;

public class RandomPlayer implements Player {

	public RandomPlayer() {
		rnd = new Random();
	}

	@Override
	public Direction move(Vector<Direction> choices) {
		if (choices == null)
			throw new IllegalArgumentException("cannot have null choices");
		if (choices.isEmpty())
			throw new IllegalArgumentException("cannot have empty choices");
		int size = choices.size();
		int idx = rnd.nextInt(size);
		return choices.get(idx);
	}

	private Random rnd = null;
}
