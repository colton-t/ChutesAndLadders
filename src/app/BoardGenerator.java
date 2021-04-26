package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.KosarajuSharirSCC;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Generates a randomly generated board filled with a number of chutes and
 * ladders, and a shortest path of that board.
 * 
 * @author Trevor Colton & Joshua Gray
 *
 */
public class BoardGenerator {
	protected EdgeWeightedDigraph graph;
	protected Iterable<Integer> shortestPath;

	final int startingVertex = 0;
	final int numChutes = 9;
	final int numLadders = 9;

	/**
	 * Generates the graphs of the board.
	 */
	public BoardGenerator() {
		graph = new EdgeWeightedDigraph(101);
		Digraph digraph = new Digraph(101);

		// Creates an edge between each tile and its 6 following neighbors
		for (int v = 0; v < 100; v++) {
			for (int w = v + 1; w <= v + 6; w++) {
				if (w <= 100) {
					int weight = (w - v);
					DirectedEdge edge = new DirectedEdge(v, w, weight);
					graph.addEdge(edge);
					digraph.addEdge(v, w);
				}
			}
		}

		// Adds chutes and ladders
		Queue<Integer> ends = new Queue<Integer>(); // head and tail values that are already taken
		addChutes(numChutes, ends, digraph);
		addLadders(numLadders, ends, digraph);
	}

	/**
	 * Generates a set number of randomly generated unique ladders.
	 * 
	 * @param amount of ladders
	 * @param ends existing head/tails of ladder
	 */
	private void addLadders(int numLadders, Queue<Integer> ends, Digraph digraph) {
		Queue<DirectedEdge> ladders = new Queue<>();
		MinPQ<DirectedEdgeComparable> heap = new MinPQ<>();

		// Generates random ladders
		for (int l = 0; l < numLadders; l++) {
			int head;
			int tail;
			do {
				head = StdRandom.uniform(2, 92);
			} while (inQueue(ends, head) || !isCycle(digraph, head, head - 1));
			ends.enqueue(head);
			do {
				tail = StdRandom.uniform(head + 7, 101);
			} while (inQueue(ends, tail));
			ends.enqueue(tail);

			// Calculates the weight of the ladder
			int length = tail - head;
			int weight = 0;
			if (length < 40)
				weight = 20;
			else if (length < 60)
				weight = 30;
			else
				weight = 40;

			// Adds the ladder to a MinPQ
			DirectedEdgeComparable edge = new DirectedEdgeComparable(head, tail, weight);
			heap.insert(edge);
		}

		// Adds only the ladders that can be passed on the first pass
		int addedWeight = 0;
		for (DirectedEdgeComparable edge : heap) {
			if ((edge.weight() / 2) <= edge.from() + addedWeight) {
				graph.addEdge(edge);
				digraph.addEdge(edge.from(), edge.to());
				addedWeight += edge.weight();
				// System.out.println("aWeight: " + addedWeight + " Ladder: " + edge.from() + "
				// -> " + edge.to());
			} else {
				ladders.enqueue(edge);
			}
		}

		// Calculates the shortest path
		shortestPath = new BreadthFirstDirectedPaths(digraph, startingVertex).pathTo(100);
		digraph = null;

		// Adds the rest of the ladders
		for (DirectedEdge edge : ladders) {
			graph.addEdge(edge);
			// StdOut.println(edge);
		}
	}

	/**
	 * Adds <code>numChutes</code> of randomly generated chutes.
	 * 
	 * @param amount
	 * @param ends
	 */
	private void addChutes(int numChutes, Queue<Integer> ends, Digraph digraph) {
		// Generates random chutes
		for (int l = 0; l < numChutes; l++) {
			int end1;
			int end2;
			do {
				end1 = StdRandom.uniform(11, 100);
			} while (inQueue(ends, end1));
			ends.enqueue(end1);
			do {
				end2 = StdRandom.uniform(1, end1 - 9);
			} while (inQueue(ends, end2));
			ends.enqueue(end2);

			// Calculates the weight of the chute
			int length = end2 - end1;
			int weight = 0;
			if (length < 40)
				weight = 5;
			else if (length < 60)
				weight = 10;
			else
				weight = 15;

			// Adds the chute to the board.
			DirectedEdge edge = new DirectedEdge(end1, end2, -weight);
			graph.addEdge(edge);
			digraph.addEdge(end1, end2);
		}
	}

	// Helper method to determine if a ladder is part of a strong component and
	// therefore can be reached again later.
	private boolean isCycle(Digraph digraph, int v, int w) {
		if (v >= 0 && w >= 0) {
			KosarajuSharirSCC scc = new KosarajuSharirSCC(digraph);
			return scc.stronglyConnected(v, w);
		}
		return false;
	}

	// Helper method for addChutes() and addLadders(). Checks if num is in the
	// queue.
	private boolean inQueue(Queue<Integer> q, int num) {
		for (int i : q) {
			if (num == i)
				return true;
		}
		return false;
	}

	/**
	 * Returns the shortest path of the generated graph.
	 * 
	 * @return Shortest Path
	 */
	public Iterable<Integer> getShortestPath() {
		return shortestPath;
	}

	// For unit testing on generating the board.
	public void testTextFile() {
		File file = new File("src/app/resources/boardWeighted.txt");
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.write(graph.V() + "\n");
			pw.write(graph.E() + "\n");
			for (DirectedEdge de : graph.edges()) {
				pw.write(de.from() + " " + de.to() + " " + de.weight() + "\n");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not found");
		}
	}

	/**
	 * Represents a DirectedEdge but implements Comparable so that it can be used
	 * with a MinPQ. Compares each edge based upon its from value. An edge with a
	 * smaller from value is considered smaller and will return a negative number, 0
	 * if they are equal, and a positive number if it is greater than the other.
	 * 
	 * @author Joshua Gray & Trevor Colton
	 *
	 */
	private class DirectedEdgeComparable extends DirectedEdge implements Comparable<DirectedEdgeComparable> {

		public DirectedEdgeComparable(int v, int w, double weight) {
			super(v, w, weight);
		}

		@Override
		public int compareTo(DirectedEdgeComparable that) {
			return this.from() - that.from();
		}
	}
}
