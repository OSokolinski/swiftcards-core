package swiftcards.core.game;

import swiftcards.core.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerIterator {

    private final List<Player> players;
    private int iterator;
    private int iterationStep;
    private final List<Integer> finishedPlayers;
    private int stoppedPlayer;

    /**
     * Initiating PlayerIterator. All players provided in argument are going to participate in game.
     *
     * @param playerList List of players
     */
    public PlayerIterator(ArrayList<Player> playerList) {
        players = playerList;
        iterator = 0;
        iterationStep = 1;
        finishedPlayers = new ArrayList<>();
        stoppedPlayer = -1;
    }

    /**
     * Getting a player, that is expected to make a turn.
     *
     * @return Player from the queue
     */
    public Player getPlayer() {

        Player candidate = players.get(iterator);

        if (finishedPlayers.contains(iterator) || candidate.hasFinishedPlay()) {

            if (!finishedPlayers.contains(iterator)) {
                finishedPlayers.add(iterator);
            }

            if (finishedPlayers.size() >= players.size()) {
                return null;
            }

            doIteration();
            return getPlayer();
        }

        if (stoppedPlayer == iterator) {

            stoppedPlayer = -1;
            doIteration();
            return getPlayer();
        }

        doIteration();

        return candidate;
    }

    /**
     * Doing player iteration. It takes into account player stops and sequence reversals.
     */
    private void doIteration() {
        if (iterationStep > 0) {

            if (iterator >= (players.size() - 1)) {
                iterator = 0;
            }
            else {
                iterator += iterationStep;
            }
        }
        else if (iterationStep < 0) {

            if (iterator <= 0) {
                iterator = players.size() - 1;
            }
            else {
                iterator += iterationStep;
            }
        }
    }

    /**
     * Getting active players count.
     *
     * @return Number of active players
     */
    public int getPlayersInGameCount() {
        return players.size() - finishedPlayers.size();
    }

    /**
     * Stopping next player
     */
    public void stopNextPlayer() {

        if (getPlayersInGameCount() == 2) {
            stoppedPlayer = iterator;
            return;
        }

        if (iterationStep > 0) {

            if (iterator >= (players.size() - 1)) {
                stoppedPlayer = 0;
            }
            else {
                stoppedPlayer = (iterator + 1);
            }
        }
        else if (iterationStep < 0) {

            if (iterator <= 0) {
                stoppedPlayer = players.size() - 1;
            }
            else {
                stoppedPlayer = (iterator - 1);
            }
        }
    }

    /**
     * Reverting queue sequence
     */
    public void turnBackSequence() {

        if (getPlayersInGameCount() == 2) {
            stopNextPlayer();
        }
        else {
            iterationStep = (-1) * iterationStep;
        }
    }

    /**
     * @return Stopped player ID
     */
    public int getStoppedPlayer() {
        return stoppedPlayer;
    }
}
