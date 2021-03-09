
# OwareAI
A collection of A.I. implementations for Oware, created by TeamiumPremium.
For the course: Artificial Intelligence Through Machine Game Playing, Spring 2021.

Yes, we know the name "TeamiumPremium" is ~~terrible~~ great. It keeps things light.

## Structure
**TeamiumPremium**: Directory containing the AI implementations and associated code (e.g. test Oware environment runner).

**AI-Game-Playing**: Directory containing the Oware environment, provided by https://github.com/mlepinski/AI-Game-Playing (*main* branch).

## Our Bots
In order from weakest to strongest.

All bots except RandomPlayerPremium take a depth argument. Depending on the strength of the computer, we use a depth of 5-7 for WiserOlderBaabPremium and 10-13 for all others (with alpha-beta pruning).

### RandomPlayerPremium
Choose the best move for the immediate turn. If all moves yield the same number of seeds, pick a random move.

This can beat the RandomPlayer but will lose to any more advanced AI.

### WiserOlderBaabPremium
Run the minimax algorithm using the ratio of our gains over our oppoonents to evaluate the game state.

This performs slightly worse than CleverBaab, CarefulBaab, and Baab.

### PruniumWiserOlderBaabPremium
This is the same as WiserOlderBaabPremium but with alpha-beta pruning.

Alpha-beta pruning enabled a greater search depth, which enabled us to somewhat reliably beat CleverBaab, CarefulBaab, and Baab.

### GreediumPruniumWiserOlderBaabPremium
This is the same as PruniumWiserOlderBaabPremium but with our difference squared divided by our opponent's difference.

Despite being more complex, this is as strong as PruniumWiserOlderBaabPremium.

### NotiumAiumRatioiumPruniumWiserOlderBaabPremium
This is the same as PruniumWiserOlderBaabPremium but uses our difference minus our opponent's difference to evaluate the game state.

This is slightly worse than WiseOldBaab.

### SafiumBaabPremium
This is the same as NotiumAiumRatioiumPruniumWiserOlderBaabPremium but additionally subtracts the number of vulnerable seeds (the sum of seeds in bins with 1 or 2 seeds).

This is stronger than NotiumAiumRatioiumPruniumWiserOlderBaabPremium, but this improvement does not translate to better performance against WiseOldBaab.

## AI-Game-Playing Subtree
The Oware environment is handled using *git subtree* (as opposed to *submodule*). *Subtree* was chosen over *submodule* for usability when the Oware environment is updated. *Subtree* simply requires the repository to be updated. *Submodule* requires all contributors to update, making user-error likely.

*Subtree* also allows the repository to be cloned without extra commands.

**The subtree can be manually updated with**:
`git subtree pull --prefix AI-Game-Playing https://github.com/mlepinski/AI-Game-Playing.git main --squash`

*Time permitting, I hope to replace this manual update with a GitHub Action.*
