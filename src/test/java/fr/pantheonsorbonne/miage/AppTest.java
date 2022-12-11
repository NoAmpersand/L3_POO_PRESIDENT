package fr.pantheonsorbonne.miage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;
import fr.pantheonsorbonne.miage.game.Card;
import org.junit.jupiter.api.Test;

import java.util.*;

import javax.naming.LinkException;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    void getInitialPlayers() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        assertEquals(players, test1.getInitialPlayers());
    }

    @Test
    void giveCardsToPlayer() {
        HashSet<String> players = new HashSet<>();
        players.add("J1");
        var test1 = new LocalPresidentGame(players);
        ArrayList<Card> cardJ1 = new ArrayList<>();
        cardJ1.add(Card.valueOf("QH"));
        test1.playerCards.put("J1", cardJ1);
        test1.giveCardsToPlayer("J1", "QH");
        boolean result = !cardJ1.isEmpty();
        assertTrue(result);
    }

    @Test
    void fetchQofH() {
        HashSet<String> players = new HashSet<>();
        players.add("P1");
        players.add("P2");

        var test1 = new LocalPresidentGame(players);
        ArrayList<Card> cardP1 = new ArrayList<>();
        ArrayList<Card> cardP2 = new ArrayList<>();
        cardP1.add(new Card(CardColor.HEART, CardValue.QUEEN));
        cardP2.add(new Card(CardColor.SPADE, CardValue.ACE));
        test1.playerCards.put("J1", cardP1);
        test1.playerCards.put("J2", cardP2);

        assertEquals("J1", test1.fetchQofH());
    }

    @Test
    void handShouldBeFilled() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        Map<Integer, Integer> handToFill = new HashMap<>();
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(CardColor.HEART, CardValue.QUEEN));
        hand.add(new Card(CardColor.HEART, CardValue.QUEEN));
        test1.fillHand(handToFill, hand);

        assertEquals(2, handToFill.get(11));

    }

    @Test
    void allShouldBeFalse() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        Queue<String> playersTest = new LinkedList<>();
        playersTest.add("P1");
        playersTest.add("P2");
        HashMap<String, Boolean> endTurn = new HashMap<>();
        test1.endTurnFiller(endTurn, playersTest);
        Map<String, Boolean> shouldMatch = new HashMap<>();
        shouldMatch.put("P1", false);
        shouldMatch.put("P2", false);
        boolean result = shouldMatch.equals(endTurn);

        assertTrue(result);

    }

    @Test
    void mapShouldBeFilled() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        HashMap<Integer, Integer> playableCards = new HashMap<>();
        playableCards.put(12, 1);
        Map<Integer, Integer> mapHand = new HashMap<>();
        mapHand.put(12, 1);
        mapHand.put(2, 1);
        ArrayList<Card> winnerHand = new ArrayList<>();
        winnerHand.add(new Card(CardColor.SPADE, CardValue.THREE));
        boolean firstTurn = true;
        Map<Integer, Integer> shouldMatch = new HashMap<>();
        test1.fillPlayableCards(playableCards, mapHand, winnerHand, firstTurn);
        shouldMatch.put(12, 2);
        shouldMatch.put(2, 1);
        boolean result = shouldMatch.equals(playableCards);
        assertTrue(result);

        HashMap<Integer, Integer> secondPlayableCards = new HashMap<>();
        firstTurn = false;
        test1.fillPlayableCards(secondPlayableCards, mapHand, winnerHand, firstTurn);
        boolean result2 = mapHand.equals(secondPlayableCards);
        assertTrue(result2);
    }

    @Test
    void shouldReturnPlayerCard() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        HashMap<String, ArrayList<Card>> toTest = new HashMap<>();
        ArrayList<Card> handToTest = new ArrayList<>();
        handToTest.add(new Card(CardColor.SPADE, CardValue.FIVE));
        toTest.put("P1", handToTest);
        test1.playerCards = toTest;

        assertEquals(handToTest, test1.getPlayerCards("P1"));
    }

    @Test
    void expertSystemShouldReturnFilledTreeMap() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        HashMap<Integer, Integer> toTest = new HashMap<>();
        toTest.put(2, 2);
        toTest.put(3, 2);
        boolean result = !test1.firstTurnExpertSystem(toTest).isEmpty();
        assertTrue(result);

    }


    @Test
    void expertSystemShouldFillTreeMap(){
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        Map<Integer, Integer> playableCards = new HashMap<>();
        playableCards.put(12, 1);
        playableCards.put(2, 1);
        ArrayList<Card> winnerHand = new ArrayList<>();
        winnerHand.add(new Card(CardColor.SPADE, CardValue.FIVE));
        winnerHand.add(new Card(CardColor.HEART, CardValue.THREE));
        winnerHand.add(new Card(CardColor.SPADE, CardValue.JACK));
        boolean premierTour = false;
        boolean result = !test1.expertSystem(playableCards, winnerHand, premierTour).isEmpty();
        assertTrue(result);
    }

    @Test
    void falseCasPlayCardValTwo() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        Queue<String> ordrePlayerBase = new LinkedList<>();
        ordrePlayerBase.add("Joueur3");
        ordrePlayerBase.add("Joueur2");
        ordrePlayerBase.add("Joueur1");
        ordrePlayerBase.add("Joueur4");
        Queue<String> player = new LinkedList<>();
        player.add("Joueur4");
        Queue<String> ordrePlayerWin = new LinkedList<>();
        ordrePlayerWin.add("Joueur3");
        ordrePlayerWin.add("Joueur2");
        Queue<String> newPlayer = new LinkedList<>();
        newPlayer.add("Joueur4");
        Queue<String> toTest = new LinkedList<>();
        toTest.add("Joueur4");
        toTest.add("Joueur1");
        Queue<String> teste = new LinkedList<>();
        teste = test1.falseCasPlayCardValTwo(ordrePlayerBase, player, ordrePlayerWin, newPlayer);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (elem1 == elem2) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);
    }

}
