package TeamiumPremium;

import ArmyOfBaab.*;
import ProjectOneEngine.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Custom Match analyzer to test the AIs.
 *
 * Runs a number of matches between two AIs to test custom, module stats
 *
 * @author Jan Fic
 */
public class PremiumAnalyzer {

    /**
     * Length of Analysis in # of games between two players to evaluate.
     */
    private int lengthOfAnalysis, maxScorelessTurns, scorelessTurns;

    /**
     * List of functions ( BiConsumer ) that extract stats every turn of each
     * game. See URL
     * (https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html)
     * for BiConsumer and other useful tools from the java.util.function package
     */
    private final List<BiConsumer<GameState, Map<String, Object>>> extractStatsFunctions;

    /**
     * Using Maps of Objects ( usually numbers ) to hold stats per game.
     */
    List<Map<String, Object>> gameStats;

    /**
     * List of general stats extraction ( See constructor )
     */
    private final List<BiConsumer<GameState, Map<String, Object>>> generalStats = new ArrayList<BiConsumer<GameState, Map<String, Object>>>(
            Arrays.asList(
                    this::recordWinner, // Method Reference notation ( Java 8 Syntax ) See:  https://www.geeksforgeeks.org/double-colon-operator-in-java/
                    this::recordScores,
                    this::recordScoreDifference,
                    this::recordMoves,
                    //this::recordBucketStonesSum,
                    this::recordBiggestBucket
            )
    );

    public PremiumAnalyzer() {
        this.extractStatsFunctions = new ArrayList<>();
        this.extractStatsFunctions.addAll(generalStats); // Adding General Stat 

        this.gameStats = new ArrayList<>();
        this.lengthOfAnalysis = 30;
        this.maxScorelessTurns = 20;
        this.scorelessTurns = 0;
    }

    public static void main(String[] args) {
        PremiumAnalyzer analyzer = new PremiumAnalyzer();
        analyzer.start(new SafiumBaabPremium(11), new WiseOldBaab());
        analyzer.analyzeStats();
    }

    /**
     * Start Premium Analyzer. Added Infinite Game catching. Max non-scoring
     * turns : 20
     *
     * @param top Top Player
     * @param bottom Bottom Player
     */
    public void start(Player top, Player bottom) {
        System.out.println("TOP: " + top.getPlayName() + "\nBOTTOM: " + bottom.getPlayName());
        for (int i = 0; i < lengthOfAnalysis; i++) {
            System.out.println("PLAYING GAME : " + i);
            gameStats.add(new HashMap<>());
            GameState state = new GameState(top.getPlayName(), bottom.getPlayName());
            this.scorelessTurns = 0;
            while (!state.isGameOver()) {
                GameState nextState = playTurn(top, bottom, state);
                if (isInfiniteGame(state, nextState)) {
                    gameStats.get(i).put("INFINITE GAME", true);
                    break;
                }
                state = nextState;
                for (BiConsumer<GameState, Map<String, Object>> function : extractStatsFunctions) {
                    function.accept(state, gameStats.get(i));
                }
            }
        }
    }

    /**
     * Copied from AIGameText to simulate playing a turn based of player's move
     * method.
     *
     * @param top Top Player
     * @param bottom Bottom Player
     * @param state Current Game State
     * @return Next Game State
     */
    private GameState playTurn(Player top, Player bottom, GameState state) {
        PlayerID cur_player = state.getCurPlayer();
        GameState copy_state = new GameState(state);
        Move nextMove = null;
        if (cur_player == PlayerID.TOP) {
            nextMove = top.getMove(copy_state);
        } else {
            nextMove = bottom.getMove(copy_state);
        }
        if ((nextMove == null) || (GameRules.makeMove(state, nextMove) == null)) {
            if (cur_player == PlayerID.TOP) {
                state = GameState.concedeState(cur_player);
            } else {
                state = GameState.concedeState(cur_player);
            }
        }
        int bin_num = nextMove.getBin();
        return GameRules.makeMove(state, bin_num);
    }

    public boolean isInfiniteGame(GameState current, GameState nextState) {
        if (nextState.getHome(PlayerID.TOP) == current.getHome(PlayerID.TOP) && nextState.getHome(PlayerID.BOT) == current.getHome(PlayerID.BOT)) {
            scorelessTurns += 1;
        } else {
            scorelessTurns = 0;
        }
        if (scorelessTurns >= maxScorelessTurns) {
            //nextState.makeGameOver();
            return true;
        }
        return false;
    }

    public void analyzeStats() {
        double topWins = 0, botWins = 0, ties = 0;
        for (Map<String, Object> gameStat : gameStats) {
            if (gameStat.get("WINNER").equals("TOP")) {
                topWins++;
            } else if (gameStat.get("WINNER").equals("BOT")) {
                botWins++;
            } else {
                ties++;
            }
        }
        System.out.println("GAMES: " + lengthOfAnalysis);
        System.out.println("TOP WINS: " + topWins + " | " + (topWins * 100 / lengthOfAnalysis) + "%");
        System.out.println("BOT WINS: " + botWins + " | " + (botWins * 100 / lengthOfAnalysis) + "%");
        System.out.println("TIES: " + ties);
        System.out.println("---------------------");
        for (int i = 0; i < gameStats.size(); i++) {
            System.out.println("GAME #" + i);
            List<String> keys = new ArrayList<>(gameStats.get(i).keySet());
            Collections.sort(keys);
            for (String key : keys) {
                System.out.println("\t" + key + " : " + gameStats.get(i).get(key));
            }
        }
    }

    /**
     * General Stat Recorders ----------------------
     */
    // BiConsumer : Records Winning Player at end of game
    private void recordWinner(GameState state, Map<String, Object> stats) {
        if (state.getHome(PlayerID.TOP) > state.getHome(PlayerID.BOT)) {
            stats.put("WINNER", "TOP");
        } else if (state.getHome(PlayerID.TOP) < state.getHome(PlayerID.BOT)) {
            stats.put("WINNER", "BOT");
        } else {
            stats.put("WINNER", "TIE");
        }
    }

    // BiConsumer : Records number of moves per game
    private void recordMoves(GameState state, Map<String, Object> stats) {
        if (stats.get("TOTAL TURNS") == null) {
            stats.put("TOTAL TURNS", 1);
        }
        stats.put("TOTAL TURNS", (Integer) ((int) stats.get("TOTAL TURNS") + 1));
    }

    // BiConsumer : Records Scores at end of game
    private void recordScores(GameState state, Map<String, Object> stats) {
        stats.put("SCORE TOP", (Integer) (state.getHome(PlayerID.TOP)));
        stats.put("SCORE BOT", (Integer) (state.getHome(PlayerID.BOT)));
    }

    // BiConsumer : Records Score Difference
    private void recordScoreDifference(GameState state, Map<String, Object> stats) {
        stats.put("SCORE DIFFERENCE", (Integer) (state.getHome(PlayerID.TOP) - state.getHome(PlayerID.BOT)));
    }

    /**
     * Bucket Based Stat Recorders ---------------------------
     */
    // BiConsumer : Records Buckets sum of Stones per entire game
    private void recordBucketStonesSum(GameState state, Map<String, Object> stats) {
        for (PlayerID value : PlayerID.values()) {
            for (int i = 0; i < 6; i++) {
                int stones = state.getStones(value, i);
                String key = "BUCKET STONES SUM [" + value + " , " + i + " ]";
                if (stats.containsKey(key)) {
                    stones += (Integer) stats.get(key);
                }
                stats.put(key, stones);
            }
        }
    }

    //BiConsumer : Records Largest Bucket Stone Value
    private void recordBiggestBucket(GameState state, Map<String, Object> stats) {
        for (PlayerID value : PlayerID.values()) {
            for (int i = 0; i < 6; i++) {
                int stones = state.getStones(value, i);
                String key = "MAX STONES " + value;
                if (stats.containsKey(key) && stones < (Integer) stats.get(key)) {
                    stones = (int) stats.get(key);
                }
                stats.put(key, stones);
            }
        }
    }
}
