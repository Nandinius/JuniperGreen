package de.nandi.junipergreen.Tree;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {
	private final int move;
	private final boolean player;
	private final ArrayList<Tree> naechste;
	private final int depth;

	/**
	 * @param move   Welchen move du gemacht hast
	 * @param player Wer als nächster dran ist, true = ich
	 * @param depth  depth
	 */
	public Tree(int move, boolean player, int depth) {
		this.move = move;
		this.player = player;
		this.depth = depth;
		naechste = new ArrayList<>();
	}

	public int getMove() {
		return move;
	}

	public int getDepth() {
		return depth;
	}

	public void addTree(Tree tree) {
		naechste.add(tree);
	}

	public boolean hasKnoten() {
		return !naechste.isEmpty();
	}

	public void cleansOnes() {
		if (naechste.size() > 1)
			naechste.removeIf(knoten -> knoten.getMove() == 1);
	}

	public void diagramm() {
		cleansOnes();
		for (Tree knoten : naechste) {
			System.out.println(move + "(" + depth + ")->" + (player ? "Ich" : "Gegner") + "->"
					+ knoten.getMove() + "(" + knoten.getDepth() + ")");
		}
		for (Tree knoten : naechste) {
			knoten.diagramm();
		}
	}

	public void diagrammFile(FileWriter writer) throws IOException {
		cleansOnes();
		for (Tree knoten : naechste) {
			writer.write(move + "(" + depth + ")->" + (player ? "Ich" : "Gegner") + "->"
					+ knoten.getMove() + "(" + knoten.getDepth() + ")\n");
		}
		for (Tree knoten : naechste) {
			knoten.diagrammFile(writer);
		}
	}
}
