package de.nandi.junipergreen;

import de.nandi.junipergreen.Tree.Tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class JuniperGreen {
	public static void main(String[] args) {
		new JuniperGreen();
	}

	private ArrayList<Integer> already;
	/**
	 * true, ich dran, false, gegner dran
	 */
	private boolean player;


	private int optimizeJG(int zahl) {//int 1 gegner verloren -1 wir verloren
		for (int i = 1; i <= max; i++) {
			int o = jgOptimized(zahl, i);
			if (o != 0)
				return o;
		}
		//nie
		return -404;
	}

	private int jgOptimized(int zahl, int depth) {//int n gegner verloren -n wir verloren n=1 tiefstes
		if (depth == 0)
			return 0;
		int k = player ? -1 : 1;
		boolean tempFirst = !player;
		for (int i = 1; i <= max; i++) {
			if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
				already.add(i);
				player = tempFirst;
				if (!player) {
					if (jgOptimized(i, depth - 1) == 1) {
						already.remove(Integer.valueOf(i));
						return 1;
					}
				} else if (jgOptimized(i, depth - 1) == -1) {
					already.remove(Integer.valueOf(i));
					return -1;
				}
				already.remove(Integer.valueOf(i));
			}
		}
		return k;
	}

	/**
	 * Benutze lieber whoWinsKonstruktor.
	 * <p>
	 * Wahrscheinlich nicht sinnvoll für max > 60
	 */
	private int jg(int zahl) {//int 1 gegner verloren -1 wir verloren
		int k = player ? -1 : 1;
		boolean tempFirst = !player;
		for (int i = max; i > 1; i--) {
			if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
				already.add(i);
				player = tempFirst;
				if (!player) {
					if (jg(i) == 1) {
						already.remove(Integer.valueOf(i));
						return 1;
					}
				} else if (jg(i) == -1) {
					already.remove(Integer.valueOf(i));
					return -1;
				}
				already.remove(Integer.valueOf(i));
			}
		}
		return k;
	}

	/**
	 * Benutze lieber jgBestMovesKonstruktor.
	 * <p>
	 * Wahrscheinlich nicht sinnvoll für max > 45
	 */
	private JGTrio jgBestMoves(int zahl, int depth) {//int n gegner in n zügen verloren -n wir in n verloren
		int mateIn = player ? -1 : 1;
		if (zahl == 1) {
			return new JGTrio(player ? 3 : -3, new ArrayList<>(Arrays.asList(10001, -1)), new Tree(1, false, depth));//player egal
		}
		//tempFirst true = ich bin danach dran, false gegner ist danach dran
		boolean tempFirst = !player;
		int bestMove = -1;
		ArrayList<Integer> bestMoves = new ArrayList<>();
		Tree winningMoves = new Tree(zahl, player, depth);
		Tree losingMoves = new Tree(zahl, player, depth);
		for (int i = 1; i <= max; i++) {
			if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
				already.add(i);
				player = tempFirst;
				JGTrio jgBestMoves = jgBestMoves(i, depth + 1);
				int jgMoves = jgBestMoves.mateIn();
				if (!tempFirst) { //tempFirst == false, gegner danach dran, ich gerade
					if (jgMoves >= 1) { //gibt zug mit dem ich gewinne
						winningMoves.addTree(jgBestMoves.allMoves());
						if (jgMoves < mateIn || mateIn <= -1) {//niedrigstes mate, || überhaupt eins
							mateIn = jgMoves;
							bestMove = i;
							bestMoves = jgBestMoves.bestMoves();
						}
					} else {//mit dem Zug gewinne ich nicht
						losingMoves.addTree(jgBestMoves.allMoves());
						if (jgMoves < mateIn && mateIn <= -1) {//höchstes mate für gegner && bis jetzt nur mate für gegner
							mateIn = jgMoves;
							bestMove = i;
							bestMoves = jgBestMoves.bestMoves();
						}
					}
				} else //ich danach dran, gegner gerade
					if (jgMoves <= -1) {//gegner kan gewinnen
						winningMoves.addTree(jgBestMoves.allMoves());
						if (jgMoves > mateIn || mateIn >= 1) {//niedrigstes mate, || überhaupt eins
							mateIn = jgMoves;
							bestMove = i;
							bestMoves = jgBestMoves.bestMoves();
						}
					} else {//mit dem Zug gewinne gegner nicht
						losingMoves.addTree(jgBestMoves.allMoves());
						if (jgMoves > mateIn && mateIn >= 1) {//gegner sucht höchstes mate für mich && bis jetzt nur mate für mich
							mateIn = jgMoves;
							bestMove = i;
							bestMoves = jgBestMoves.bestMoves();
						}
					}
				already.remove(Integer.valueOf(i));
			}
		}
		bestMoves.add(0, bestMove);
		mateIn += mateIn >= 1 ? 1 : -1;
		return new JGTrio(mateIn, bestMoves, winningMoves.hasKnoten() ? winningMoves : losingMoves);
	}

	private void possibleMoves(List<Integer> already) {
		if (already.get(already.size() - 1) == 1) {
			System.out.println((player ? "Gegner spielt: " : "Ich spiele: ") + "Primzahl (ich " + (!player ? "gewonnen." : "verloren.") + ")");
			return;
		}
		StringBuilder possibleMoves = new StringBuilder();
		for (int i = 2; i <= max; i++) {
			if (!already.contains(i) && (already.get(already.size() - 1) % i == 0 || i % already.get(already.size() - 1) == 0)) {
				this.already = new ArrayList<>(already);
				this.already.add(i);
				player = this.already.size() % 2 == 0;//ich habe i gespielt, jetzt ist anzahl odd = false = gegner dran
				possibleMoves.append(player ? "Gegner spielt: " : "Ich spiele: ")
						.append(i).append(" (ich ").append(jg(i) == 1 ? "gewonnen." : "verloren.").append(")").append("\n");
			}
		}
		if (possibleMoves.length() == 0)
			System.out.println((player ? "Gegner spielt: " : "Ich spiele: ") + "1 (ich " + (player ? "gewonnen." : "verloren.") + ")");
		else
			System.out.println(possibleMoves.deleteCharAt(possibleMoves.length() - 1));
	}

	private final int max = 50;

	public JuniperGreen() {
		bestMovesKonstruktor(false, 11 * 2, 13 * 2);
		whoWinsKonstruktor(false);
		possibleMovesKonstruktor(Arrays.asList(26, 13, 39, 3, 33, 11, 44, 4, 32, 16, 48, 24, 12, 6, 30, 10, 40, 8, 2, 22, 1, 43), 1);
		possibleMovesKonstruktor(Arrays.asList(26, 13, 39, 3, 33, 11, 44, 4, 32, 16, 48), 10);


		/*Versuch eines optimierter Algorithmus mit parallelem Ausführen, funktoniert aber nicht bei großen Zahlen > 12
		List<Callable<Integer>> completeResults = new ArrayList<>();
		for (int i = 2; i <= max; i += 2) {
			int finalI = i;
			completeResults.add(new Callable<>() {
				@Override
				public Integer call() {
					already = new ArrayList<>();
					already.add(finalI);
					return jg(finalI, false, already, 0, new ArrayList<>());
				}

				private int jg(int zahl, boolean player, ArrayList<Integer> already, int depth, List<Stop> stops) {
					for (Stop stop : stops) {
						if (stop.isStop())
							return -10;
					}
					int k = player ? -1 : 1;
					if (depth < 0) {
						Thread[] threads = new Thread[max];
						int[] results = new int[max];
						List<Stop> stopsFinal = new ArrayList<>(stops);
						stopsFinal.add(new Stop());
						for (int i = 1; i <= max; i++) {
							if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
								int finalI = i;
								Thread thread = new Thread(() -> {
									ArrayList<Integer> alreadyFinal = new ArrayList<>(already);
									alreadyFinal.add(finalI);
									results[finalI - 1] = jg(finalI, !player, alreadyFinal, depth + 1, stopsFinal);
								});
								thread.setName("Depth: " + depth + " jg:" + finalI + " start:" + zahl);
								thread.start();
								threads[finalI - 1] = thread;
							}
						}
						long threadNumber = Arrays.stream(threads).filter(Objects::nonNull).count();
						if (threadNumber == 0)
							return k;
						while (true) {
							if (Arrays.stream(results).filter(value -> value != 0).count() == threadNumber)
								return k;
							for (int j = 0; j < max; j++) {
								Thread thread = threads[j];
								if (thread != null && !thread.isAlive())
									if (player) {
										if (results[j] == 1) {
											for (Thread threadKill : threads)
												if (threadKill != null && threadKill.isAlive())
													stopsFinal.get(stopsFinal.size() - 1).stop();
											return 1;
										}
									} else if (results[j] == -1) {
										for (Thread threadKill : threads)
											if (threadKill != null && threadKill.isAlive())
												stopsFinal.get(stopsFinal.size() - 1).stop();
										return -1;
									}
							}
						}
					} else {//optimized = slower ?
						for (int i = 1; i <= max; i++) {
							if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
								ArrayList<Integer> alreadyFinal = new ArrayList<>(already);
								alreadyFinal.add(i);
								if (player) {
									if (jg(i, alreadyFinal, false) == 1) {
										return 1;
									}
								} else if (jg(i, alreadyFinal, true) == -1) {
									return -1;
								}
							}
						}
					}
					return k;
				}

				private int jg(int zahl, List<Integer> already, boolean player) {//int 1 gegner verloren -1 wir verloren
					int k = player ? -1 : 1;
					for (int i = 1; i <= max; i++) {
						if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
							ArrayList<Integer> alreadyFinal = new ArrayList<>(already);
							alreadyFinal.add(i);
							if (player) {
								if (jg(i, alreadyFinal, false) == 1)
									return 1;
							} else if (jg(i, alreadyFinal, true) == -1)
								return -1;
						}
					}
					return k;
				}
			});
		}
		try (ExecutorService service = Executors.newFixedThreadPool(max / 2 + 1)) {
			int i = 2;
			for (Future<Integer> result : service.invokeAll(completeResults)) {
				System.out.println("Mit " + i + " als start hat mann immer " + (result.get() == -1 ? "verloren." : "gewonnen."));
				i += 2;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
*/

		/*Leicht optimierter Algorithmus mit einem parallel laufen, aber trotzdem langsamer
		for (int i = 2; i <= max; i += 2) {
			int finalI = i;
			new Thread(new Runnable() {
				private ArrayList<Integer> already;
				private boolean player;//true, ich dran, false, gegner dran

				@Override
				public void run() {
					already = new ArrayList<>();
					player = false;
					already.add(finalI);
					System.out.println("Mit " + finalI + " als start hat mann immer " + (jg(finalI) == -1 ? "verloren." : "gewonnen."));
				}

				private int jg(int zahl) {//int 1 gegner verloren -1 wir verloren
					int k = player ? -1 : 1;
					boolean tempFirst = !player;
					for (int i = 1; i <= max; i++) {
						if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
							already.add(i);
							player = tempFirst;
							if (!player) {
								if (jg(i) == 1) {
									already.remove(Integer.valueOf(i));
									return 1;
								}
							} else if (jg(i) == -1) {
								already.remove(Integer.valueOf(i));
								return -1;
							}
							already.remove(Integer.valueOf(i));
						}
					}
					return k;
				}
			}).start();
		}
		*/
	}

	/**
	 * Ähnliche performance wie jg
	 *
	 * @param already A list of Moves wich should be commented for the best kontinuation
	 * @param start   At what place to start in the list. from 1 to already.size() , inklusive
	 */
	private void possibleMovesKonstruktor(List<Integer> already, int start) {
		if (start < 1 || start > already.size())
			throw new IndexOutOfBoundsException("start must be between 1 and already.size()=" + already.size());
		for (int i = start; i <= already.size(); i++) {
			List<Integer> subList = already.subList(0, i);
			System.out.println("---- Mögliche züge für " + nicePossibleMoves(subList) + i + " Züge ----------------");
			possibleMoves(subList);
			System.out.print(subList.size() % 2 == 0 ? "Ich habe gespielt: " : "Gegner hat gespielt: ");
			if (i != already.size())
				System.out.println(already.get(i));
			else
				System.out.println("Noch nichts");
		}
	}


	/**
	 * Ähnliche performance wie jgBestMoves.
	 * Wahrscheinlich nicht sinnvoll für max > 45
	 * Gibt den optimalen weg für beide Spieler aus, Sieger versucht so kurz wie möglich, Verlierer so lang wie möglich.
	 * Wie lang es beim optimalen Spielen am längsten fürs Verlieren oder gewinnen braucht.
	 * Und schreibt einen in <a href="https://app.diagrams.net/?src=about">app.diagrams.net</a> einsetzbaren Text
	 * für ein Diagramm für alle optimalen zügen in eine Datei.
	 *
	 * @param write  true if the info should be written to a file, only advisable if file doesn't exist or starts.length == 0;
	 * @param starts The starting numbers or all if left empty.
	 */
	private void bestMovesKonstruktor(boolean write, int... starts) {
		File allMovesDiagrammDir = new File("D:\\nandi\\Desktop\\Programieren\\Workspace\\JuniperGreen\\src\\de\\nandi\\junipergreen\\Diagramme\\" + max);
		if (allMovesDiagrammDir.mkdir())
			player = false;
		File infoFile = new File(allMovesDiagrammDir, "Info.txt.txt");
		StringBuilder infoText = new StringBuilder();
		for (int i = 2; i <= max; i += 2) {
			int finalI = i;
			if (starts.length != 0 && Arrays.stream(starts).noneMatch(value -> value == finalI))
				continue;
			File allMovesDiagramm = new File(allMovesDiagrammDir, "Start_" + i + ".txt");
			already = new ArrayList<>();
			already.add(i);
			player = false;
			JGTrio jgBestMoves = jgBestMoves(i, 1);
			jgBestMoves.bestMoves().add(0, i);
			String info = "Mit " + i + " als start hat mann immer in " + Math.abs(jgBestMoves.mateIn()) + " Moves "
					+ (jgBestMoves.mateIn() <= -1 ? "verloren." : "gewonnen.") +
					" (Optimaler Weg " + nicePossibleMoves(jgBestMoves.bestMoves().subList(0, Math.abs(jgBestMoves.mateIn()))) + ")";
			System.out.println(info);
			infoText.append(info).append("\n");
			try (FileWriter writer = new FileWriter(allMovesDiagramm)) {
				jgBestMoves.allMoves().diagrammFile(writer);
			} catch (IOException e) {
				jgBestMoves.allMoves().diagramm();
				e.printStackTrace();
			}
		}
		saveInfo(write, infoFile, infoText, starts.length != 0);
	}

	/**
	 * Ähnliche performance wie jg.
	 * Wahrscheinlich nicht sinnvoll für max > 60
	 * Gibt aus welcher Spieler bei welchem Startwert gewinnt.
	 *
	 * @param write  true if the info should be written to a file, only advisable if file doesn't exist;
	 * @param starts The starting numbers or all if left empty.
	 */
	private void whoWinsKonstruktor(boolean write, int... starts) {
		File allMovesDiagrammDir = new File("D:\\nandi\\Desktop\\Programieren\\Workspace\\JuniperGreen\\src\\de\\nandi\\junipergreen\\Diagramme\\" + max);
		if (allMovesDiagrammDir.mkdir())
			player = false;
		File infoFile = new File(allMovesDiagrammDir, "Info.txt.txt");
		StringBuilder infoText = new StringBuilder();
		for (int i = 2; i <= max; i += 2) {
			int finalI = i;
			if (starts.length != 0 && Arrays.stream(starts).noneMatch(value -> value == finalI))
				continue;
			already = new ArrayList<>();
			already.add(i);
			player = false;
			String info = "Mit " + i + " als start hat mann immer " + (jg(i) == 1 ? "gewonnen." : "verloren.");
			System.out.println(info);
			infoText.append(info).append("\n");
		}
		saveInfo(write, infoFile, infoText, true);
	}

	/**
	 * @param starts Is it not all numbers or not the moves info
	 */
	private void saveInfo(boolean write, File infoFile, StringBuilder infoText, boolean starts) {
		if (write) {
			if (infoFile.exists() && starts) {
				System.out.println("Du überschreibst eine datei, willst du wirklich? Ja/Nein");
				try (Scanner scanner = new Scanner(System.in)) {
					if (!scanner.nextLine().equalsIgnoreCase("ja")) {
						System.out.println("Wurde nicht");
						return;
					}
				}
			}
			try (FileWriter infoWriter = new FileWriter(infoFile)) {
				infoWriter.write(infoText.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}


	private String nicePossibleMoves(List<Integer> already) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < already.size(); i++) {
			stringBuilder.append(already.get(i)).append(i % 2 == 0 ? "(ich) " : "(g) ");
		}
		return stringBuilder.toString();
	}

	static class Stop {
		boolean stop;

		public Stop() {
			stop = false;
		}

		public boolean isStop() {
			return stop;
		}

		public void stop() {
			stop = true;
		}
	}

	static class Pair<V, K> {
		private V typ1;
		private K typ2;

		public Pair(V typ1, K typ2) {
			this.typ1 = typ1;
			this.typ2 = typ2;
		}

		public V getTyp1() {
			return typ1;
		}

		public K getTyp2() {
			return typ2;
		}

		@Override
		public String toString() {
			return "Pair{" +
					"typ1=" + typ1 +
					", typ2=" + typ2 +
					'}';
		}
	}

	record JGTrio(int mateIn, ArrayList<Integer> bestMoves, Tree allMoves) {
		@Override
		public String toString() {
			return "Pair{mateIn=%d, bestMoves=%s}".formatted(mateIn, bestMoves.toString());
		}
	}
}

