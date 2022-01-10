package sokoban;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class SokobanUI {

	public SokobanUI() {
		scnr = new Scanner(System.in);
		puzzle = new Sokoban(new File(FILENAME));
		player = new RandomPlayer();
	}

	public void menu() {
		String command = "";
		System.out.print(puzzle);
		while (!command.equalsIgnoreCase("Quit") && !puzzle.onTarget()) {
			displayMenu();
			command = getCommand();
			execute(command);
			System.out.print(puzzle);
			if (puzzle.onTarget())
				System.out.println("puzzle is complete");
			trace("onTarget: " + puzzle.numOnTarget());
		}
	}

	private void displayMenu() {
		System.out.println("Commands are:");
		System.out.println("   Move North         [N]");
		System.out.println("   Move South         [S]");
		System.out.println("   Move East          [E]");
		System.out.println("   Move West          [W]");
		System.out.println("   Player move        [P]");
		System.out.println("   Undo move          [U]");
		System.out.println("   Restart puzzle [Clear]");
		System.out.println("   Save to file    [Save]");
		System.out.println("   Load from file  [Load]");
		System.out.println("   To end program  [Quit]");
	}

	private String getCommand() {
		System.out.print("Enter command: ");
		return scnr.nextLine();
	}

	private void execute(String command) {
		if (command.equalsIgnoreCase("Quit")) {
			System.out.println("Program closing down");
			System.exit(0);
		} else if (command.equalsIgnoreCase("N")) {
			north();
		} else if (command.equalsIgnoreCase("S")) {
			south();
		} else if (command.equalsIgnoreCase("E")) {
			east();
		} else if (command.equalsIgnoreCase("W")) {
			west();
		} else if (command.equalsIgnoreCase("P")) {
			playerMove();
		} else if (command.equalsIgnoreCase("U")) {
			System.out.println("not implemented yet");
		} else if (command.equalsIgnoreCase("Clear")) {
			System.out.println("not implemented yet");
		} else if (command.equalsIgnoreCase("Save")) {
			System.out.println("not implemented yet");
		} else if (command.equalsIgnoreCase("Load")) {
			System.out.println("not implemented yet");
		} else {
			System.out.println("Unknown command (" + command + ")");
		}
	}

	private void north() {
		move(Direction.NORTH);
	}

	private void south() {
		move(Direction.SOUTH);
	}

	private void east() {
		move(Direction.EAST);
	}

	private void west() {
		move(Direction.WEST);
	}

	private void playerMove() {
		Vector<Direction> choices = puzzle.canMove();
		Direction choice = player.move(choices);
		move(choice);
	}

	private void move(Direction dir) {
		if (!puzzle.canMove(dir)) {
			System.out.println("invalid move");
			return;
		}
		puzzle.move(dir);
		if (puzzle.onTarget())
			System.out.println("game won!");
	}

	public static void main(String[] args) {
		SokobanUI ui = new SokobanUI();
		ui.menu();
	}

	public static void trace(String s) {
		if (traceOn)
			System.out.println("trace: " + s);
	}

	private Scanner scnr = null;
	private Sokoban puzzle = null;
	private Player player = null;

	private static String FILENAME = "screens/screen.1";

	private static boolean traceOn = false; // for debugging
}
