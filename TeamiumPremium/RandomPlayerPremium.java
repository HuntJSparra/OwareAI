package TeamiumPremium;

import ProjectOneEngine.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class RandomPlayerPremium implements Player{

    public Move getMove(GameState state){

	Random rand = new Random();
	PlayerID cur_player = state.getCurPlayer();

	int maxSeeds = -1;
	int bestBinToMove = -1;
	ArrayList<Integer> bins = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5));
	Collections.shuffle(bins, rand);
	for (Integer i: bins) {
		GameState testState = GameRules.makeMove(state, i);

	    if (testState != null) {
			int newSeeds = testState.getHome(cur_player);
			if (newSeeds > maxSeeds) {
				maxSeeds = newSeeds;
				bestBinToMove = i;
			}
	    }
	}
	return new Move(bestBinToMove, cur_player);
    }

    public String getPlayName(){
	return "Premium Random Player";
    }
}
	
