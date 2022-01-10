package sokoban;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;
import java.util.Vector;

@SuppressWarnings("deprecation")
public class Sokoban extends Observable {

	public Sokoban(File file) {
		this(fileAsString(file));
	}

	public Sokoban(String screen) {
		if (screen == null)
			throw new IllegalArgumentException("screen cannot be null");
		startScreen = screen;
		Scanner scnr = null;
		ArrayList<String> lines = new ArrayList<>();
		scnr = new Scanner(screen);
		while (scnr.hasNextLine()) {
			String line = scnr.nextLine();
			if (line.length() > 0) {
				lines.add(line);
				numRows++;
				if (line.length() > numCols)
					numCols = line.length();
			}
		}
		scnr.close();
		cells = new Cell[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			String line = lines.get(row);
			for (int col = 0; col < numCols; col++) {
				char display = (col < line.length()) ? line.charAt(col) : Sokoban.EMPTY;
				cells[row][col] = new Cell(display, this, row, col);
				if (display == ACTOR || display == TARGET_ACTOR)
					actorCell = cells[row][col];
			}
		}
		checkValid();
	}

	private void checkValid() {
		assert numBoxes() == numTargets() : "number of boxes and targets must be equal";
		assert numActors() == 1 : "must be exactly one actor";
		assert actorCell.hasActor() : "actorCell must be valid";
	}

	public void clear() {
		if (startScreen == null)
			throw new IllegalStateException("startScreen cannot be null");
		Scanner scnr = null;
		ArrayList<String> lines = new ArrayList<>();
		scnr = new Scanner(startScreen);
		while (scnr.hasNextLine()) {
			String line = scnr.nextLine();
			if (line.length() > 0)
				lines.add(line);
		}
		scnr.close();
		for (int row = 0; row < numRows; row++) {
			String line = lines.get(row);
			for (int col = 0; col < numCols; col++) {
				char display = (col < line.length()) ? line.charAt(col) : Sokoban.EMPTY;
				if (cells[row][col].getDisplay() != display) {
					cells[row][col].setDisplay(display);
					if (display == ACTOR || display == TARGET_ACTOR)
						actorCell = cells[row][col];
					trace("clear: changing display in (" + row + "," + col + ")");
					setChanged();
					notifyObservers(cells[row][col]);
				}
			}
		}
		checkValid();
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	Cell getCell(int row, int col) {
		if ((row < 0) || (row >= numRows) || (col < 0) || (col >= numCols))
			return null;
		return cells[row][col];
	}

	Cell getActorCell() {
		return actorCell;
	}

	public int numTargets() {
		int num = 0;
		for (int row = 0; row < numRows; row++)
			for (int col = 0; col < numCols; col++)
				if (cells[row][col].isTarget())
					num++;
		return num;
	}

	public int numBoxes() {
		int num = 0;
		for (int row = 0; row < numRows; row++)
			for (int col = 0; col < numCols; col++)
				if (cells[row][col].hasBox())
					num++;
		return num;
	}

	public int numActors() {
		int num = 0;
		for (int row = 0; row < numRows; row++)
			for (int col = 0; col < numCols; col++)
				if (cells[row][col].hasActor())
					num++;
		return num;
	}

	public int numOnTarget() {
		int num = 0;
		for (int row = 0; row < numRows; row++)
			for (int col = 0; col < numCols; col++)
				if (cells[row][col].onTarget())
					num++;
		return num;
	}

	public boolean onTarget() {
		return numOnTarget() == numTargets();
	}

	public boolean canMove(Direction dir) {
		return actorCell.canMove(dir);
	}

	public Vector<Direction> canMove() {
		Vector<Direction> dirs = new Vector<>();
		for (Direction dir : Direction.values()) {
			if (canMove(dir))
				dirs.add(dir);
		}
		return dirs;
	}

	public void move(Direction dir) {
		if (!canMove(dir))
			throw new IllegalArgumentException("cannot move " + dir);
		Cell oldActorCell = actorCell;
		actorCell.move(dir);
		actorCell = actorCell.getCell(dir);
		Cell next = actorCell.getCell(dir);
		if (!actorCell.hasActor())
			throw new IllegalStateException("actorCell must have Actor");
		setChanged();
		notifyObservers(oldActorCell); // where actor was
		setChanged();
		notifyObservers(actorCell); // where actor is now
		if (next != null) {
			setChanged();
			notifyObservers(next); // to where box may have been pushed
		}
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++)
				b.append("" + cells[row][col]);
			b.append("\n");
		}
		return b.toString();
	}

	public static boolean validDisplay(char c) {
		// not valid for TARGET or EMPTY
		return ((c == WALL) || (c == BOX) || (c == TARGET_BOX) || (c == ACTOR) || (c == TARGET_ACTOR));
	}

	public static String fileAsString(File file) {
		if (file == null)
			throw new IllegalArgumentException("file cannot be null");
		Scanner fscnr = null;
		StringBuffer sb = new StringBuffer();
		try {
			fscnr = new Scanner(file);
			while (fscnr.hasNextLine())
				sb.append(fscnr.nextLine() + "\n");
		} catch (IOException e) {
			throw new SokobanException("" + e);
		} finally {
			if (fscnr != null)
				fscnr.close();
		}
		return sb.toString();
	}

	public static void trace(String s) {
		if (traceOn)
			System.out.println("trace: " + s);
	}

	public static final char WALL = '#';
	public static final char BOX = '$';
	public static final char ACTOR = '@';
	public static final char TARGET = '.';
	public static final char EMPTY = ' ';
	public static final char TARGET_BOX = '*';
	public static final char TARGET_ACTOR = '+';

	private int numRows = 0;
	private int numCols = 0;
	private Cell actorCell = null;
	private Cell[][] cells = null;
	private String startScreen = null;

	private static boolean traceOn = false; // for debugging
}
