package de.nandi.junipergreen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class JuniperGreen {
	public static void main(String[] args) {
		new JuniperGreen();
	}

	private ArrayList<Integer> already;
	private boolean player;//true, ich dran, false, gegner dran
	private final int max = 12;//alle gewinn bis auf 10


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

	public JuniperGreen() {
//		for (int i = 2; i <= max; i += 2) {
//			already = new ArrayList<>();
//			already.add(i);
//			player = false;
//			System.out.println(i + ":" + optimizeJG(i));
//		}
//		for (int i = 2; i <= max; i += 2) {
//			already = new ArrayList<>();
//			already.add(i);
//			player = false;
//			System.out.println(i + ":" + jg(i));
//		}


		List<Callable<Integer>> completeResults = new ArrayList<>();
		for (int i = 2; i <= max; i += 2) {
			int finalI = i;
			completeResults.add(new Callable<>() {
				@Override
				public Integer call() {
					already = new ArrayList<>();
					already.add(finalI);
					return jg(finalI, false, already, 0);
				}

				private int jg(int zahl, boolean player, ArrayList<Integer> already, int depth) {
					int k = player ? -1 : 1;
					if (depth < 5) {
						List<Callable<Integer>> results = new ArrayList<>();
						for (int i = 1; i <= max; i++) {
							if (!already.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
								int finalI = i;
								results.add(() -> {
									ArrayList<Integer> alreadyFinal = new ArrayList<>(already);
									alreadyFinal.add(finalI);
									return jg(finalI, !player, alreadyFinal, depth + 1);
								});
							}
						}
						if (results.size() == 0)
							return k;
						try (ExecutorService service = Executors.newFixedThreadPool(max)) {
							for (Future<Integer> result : service.invokeAll(results)) {//Methode die sobald ein Thread -1 zurückgibt alle anderen Threads stopped und -1 weiter zurückgibt
								int jg = result.get();
								if (player) {
									if (jg == 1)
										return 1;
								} else if (jg == -1)
									return -1;
							}
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
							return -404;
						}
					} else {//optimized = slower ?
						globalAlready = new ArrayList<>(already);
						for (int i = 1; i <= max; i++) {
							if (!globalAlready.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
								globalAlready.add(i);
								globalPlayer = !player;
								if (!globalPlayer) {
									if (jg(i) == 1) {
										globalAlready.remove(Integer.valueOf(i));
										return 1;
									}
								} else if (jg(i) == -1) {
									globalAlready.remove(Integer.valueOf(i));
									return -1;
								}
								globalAlready.remove(Integer.valueOf(i));
							}
						}
					}
					return k;
				}

				private boolean globalPlayer;
				private ArrayList<Integer> globalAlready;

				private int jg(int zahl) {//int 1 gegner verloren -1 wir verloren
					int k = globalPlayer ? -1 : 1;
					boolean tempFirst = !globalPlayer;
					for (int i = 1; i <= max; i++) {
						if (!globalAlready.contains(i) && (zahl % i == 0 || i % zahl == 0)) {
							globalAlready.add(i);
							globalPlayer = tempFirst;
							if (!globalPlayer) {
								if (jg(i) == 1) {
									globalAlready.remove(Integer.valueOf(i));
									return 1;
								}
							} else if (jg(i) == -1) {
								globalAlready.remove(Integer.valueOf(i));
								return -1;
							}
							globalAlready.remove(Integer.valueOf(i));
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

//non optimized
		/*
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
}
