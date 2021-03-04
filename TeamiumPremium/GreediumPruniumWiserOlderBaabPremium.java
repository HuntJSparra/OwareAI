package TeamiumPremium;

import ProjectOneEngine.GameRules;
import ProjectOneEngine.GameState;
import ProjectOneEngine.Move;
import ProjectOneEngine.Player;
import ProjectOneEngine.PlayerID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Teamium Premium
 */
public class GreediumPruniumWiserOlderBaabPremium implements Player {

    final int maxDepth;

    public GreediumPruniumWiserOlderBaabPremium() {
        this(3);
    }

    public GreediumPruniumWiserOlderBaabPremium(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public Move getMove(GameState state) {

        Random rand = new Random();
        PlayerID cur_player = state.getCurPlayer();

        int bestBinToMove = -1;
        double bestScore = Double.NEGATIVE_INFINITY; // If scoreFutureState returns MIN_VALUE there will be problems

        ArrayList<Integer> bins = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        Collections.shuffle(bins, rand);

        for (Integer i : bins) {
            GameState testState = GameRules.makeMove(state, i);
            if (testState != null) {
                double score = minimize(state, testState, maxDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                if (score > bestScore) {
                    bestScore = score;
                    bestBinToMove = i;
                }
            }
        }
        return new Move(bestBinToMove, cur_player);
    }

    @Override
    public String getPlayName() {
        return "Greedium Prunium Wiser Older Baab Premium";
    }

    private double maximize(GameState startState, GameState futureState, int depthRemaining, double alpha, double beta) {
        //Check for End Condition
        if (depthRemaining <= 0 || futureState.isGameOver()) {
            return scoreFutureState(startState, futureState);
        }
        
        //Find best Score
        double bestScore = alpha;
        ArrayList<Integer> bins = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        Collections.shuffle(bins);
        for (Integer i : bins) {
            GameState testState = GameRules.makeMove(futureState, i);
            if (testState != null) {
                double score = minimize(startState, testState, depthRemaining - 1, bestScore, beta);

                if (score > bestScore) {
                    bestScore = score;
                }

                // Prune (ignore other branches) if our best score so far is greater than beta (upper-bound)
                if (bestScore >= beta) {
                    break;
                }
            }
        }

        //Return Score
        return bestScore;
    }

    private double minimize(GameState startState, GameState futureState, int depthRemaining, double alpha, double beta) {
        if (depthRemaining <= 0 || futureState.isGameOver()) {
            return scoreFutureState(startState, futureState);
        }

        //Find best Score
        double bestScore = beta;
        ArrayList<Integer> bins = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        Collections.shuffle(bins);
        for (Integer i : bins) {
            GameState testState = GameRules.makeMove(futureState, i);
            if (testState != null) {
                double score = maximize(startState, testState, depthRemaining - 1, alpha, bestScore);

                if (score < bestScore) {
                    bestScore = score;
                }

                // Prune (ignore other branches) if our best score so far is less than alpha (lower-bound)
                if (bestScore <= alpha) {
                    break;
                }
            }
        }

        //Return Score
        return bestScore;
    }

    //Scoring Final State of Board
    private double scoreFutureState(GameState startState, GameState futureState) {
        //zero checking
        PlayerID us = startState.getCurPlayer();
        PlayerID them = us == PlayerID.TOP ? PlayerID.BOT : PlayerID.TOP;
        double opponentDifference = futureState.getHome(them) - startState.getHome(them);
        double ourDifference = futureState.getHome(us) - startState.getHome(us);

        if (ourDifference == 0) {
            return -opponentDifference;
        }
        if (opponentDifference == 0) {
            return Double.POSITIVE_INFINITY; // Returns same for all oppoenentDifference = 0
        }
        return ourDifference * ourDifference / opponentDifference;
    }
}
