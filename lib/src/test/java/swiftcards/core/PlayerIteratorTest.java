package swiftcards.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import swiftcards.core.game.PlayerIterator;
import swiftcards.core.player.AIPlayer;
import swiftcards.core.player.Player;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerIteratorTest {

    private static final ArrayList<Player> players = new ArrayList<>();

    @BeforeAll
    public static void before() {
        AIPlayer p0 = new AIPlayer();
        p0.setId(0);
        p0.setHasFinishedPlay(false);
        AIPlayer p1 = new AIPlayer();
        p1.setId(1);
        p1.setHasFinishedPlay(false);
        AIPlayer p2 = new AIPlayer();
        p2.setId(2);
        p2.setHasFinishedPlay(false);
        AIPlayer p3 = new AIPlayer();
        p3.setId(3);
        p3.setHasFinishedPlay(false);
        AIPlayer p4 = new AIPlayer();
        p4.setId(4);
        p4.setHasFinishedPlay(false);

        players.add(p0);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
    }

    @Test
    public void PlayerIterator_ReturnsProperSequence_InDefaultMode() {

        PlayerIterator playerIterator = new PlayerIterator(players);

        int[] expectedSequence = new int[] {0, 1, 2, 3, 4, 0, 1, 2};

        for (int j : expectedSequence) {
            assertEquals(j, playerIterator.getPlayer().getId());
        }
    }

    @Test
    public void PlayerIterator_ReturnsProperSequence_InRevertMode() {

        PlayerIterator playerIterator = new PlayerIterator(players);

        int[] expectedSequence = new int[] {0, 1, 2, 3, 4, 0, 4, 3, 2, 1, 0, 1, 2, 3};

        for (int i = 0; i < expectedSequence.length; i++) {

            if (i == 6 || i == 11) {
                playerIterator.turnBackSequence();
            }

            assertEquals(expectedSequence[i], playerIterator.getPlayer().getId());
        }

        AIPlayer p0 = new AIPlayer();
        p0.setId(0);
        p0.setHasFinishedPlay(false);
        AIPlayer p1 = new AIPlayer();
        p1.setId(1);
        p1.setHasFinishedPlay(false);

        ArrayList<Player> localPlayers = new ArrayList<>();
        localPlayers.add(p0);
        localPlayers.add(p1);

        playerIterator = new PlayerIterator(localPlayers);
        expectedSequence = new int[] {0, 0, 0};

        for (int j : expectedSequence) {

            assertEquals(j, playerIterator.getPlayer().getId());
            playerIterator.turnBackSequence();
        }
    }

    @Test
    public void PlayerIterator_SkipsPlayer_WhenIsStopped() {

        PlayerIterator playerIterator = new PlayerIterator(players);

        int[] expectedSequence = new int[] {0, 1, 3, 4, 3, 2, 1, 4, 3};

        for (int i = 0; i < expectedSequence.length; i++) {

            if (i == 4) {
                playerIterator.turnBackSequence();
            }

            if (i == 2 || i == 7) {
                playerIterator.stopNextPlayer();
            }

            assertEquals(expectedSequence[i], playerIterator.getPlayer().getId());
        }

        AIPlayer p0 = new AIPlayer();
        p0.setId(0);
        p0.setHasFinishedPlay(false);
        AIPlayer p1 = new AIPlayer();
        p1.setId(1);
        p1.setHasFinishedPlay(false);

        ArrayList<Player> localPlayers = new ArrayList<>();
        localPlayers.add(p0);
        localPlayers.add(p1);

        playerIterator = new PlayerIterator(localPlayers);
        expectedSequence = new int[] {0, 0, 0};

        for (int j : expectedSequence) {

            assertEquals(j, playerIterator.getPlayer().getId());
            playerIterator.stopNextPlayer();
        }
    }

   @Test
   public void PlayerIteratorSkipsPlayers_ThatHaveAlreadyFinished() {

       ArrayList<Player> players = new ArrayList<>();

        AIPlayer p0 = new AIPlayer();
        p0.setId(0);
        p0.setHasFinishedPlay(false);
        players.add(p0);

       AIPlayer p1 = new AIPlayer();
       p1.setId(1);
       p1.setHasFinishedPlay(false);
       players.add(p1);

       AIPlayer p2 = new AIPlayer();
       p2.setId(2);
       p2.setHasFinishedPlay(false);
       players.add(p2);

       AIPlayer p3 = new AIPlayer();
       p3.setId(3);
       p3.setHasFinishedPlay(false);
       players.add(p3);

       PlayerIterator pi = new PlayerIterator(players);

       int[] expectedPlayerSequence = new int[] {0, 1, 2, 3};

       for (int sequenceVal : expectedPlayerSequence) {
           assertEquals(sequenceVal, pi.getPlayer().getId());
       }

       p1.setHasFinishedPlay(true);

       expectedPlayerSequence = new int[] {0, 2, 3};

       for (int sequenceVal : expectedPlayerSequence) {
           assertEquals(sequenceVal, pi.getPlayer().getId());
       }

       p3.setHasFinishedPlay(true);

       expectedPlayerSequence = new int[] {0, 2};

       for (int sequenceVal : expectedPlayerSequence) {
           assertEquals(sequenceVal, pi.getPlayer().getId());
       }

       p0.setHasFinishedPlay(true);
       p2.setHasFinishedPlay(true);

       assertNull(pi.getPlayer());
   }
}
