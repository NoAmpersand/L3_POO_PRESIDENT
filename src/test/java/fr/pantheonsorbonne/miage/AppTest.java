
package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;
import fr.pantheonsorbonne.miage.game.Card;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
        Map<Integer, Integer> shouldMatch = new HashMap<>();
        test1.fillPlayableCards(playableCards, mapHand, winnerHand, true);
        shouldMatch.put(12, 2);
        shouldMatch.put(2, 1);
        boolean result = shouldMatch.equals(playableCards);
        assertTrue(result);

        HashMap<Integer, Integer> secondPlayableCards = new HashMap<>();
        test1.fillPlayableCards(secondPlayableCards, mapHand, winnerHand, false);
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
    void shouldReturnFilledTreeMap() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        HashMap<Integer, Integer> toTest = new HashMap<>();
        toTest.put(2, 2);
        toTest.put(3, 2);
        boolean result = !test1.firstTurnExpertSystem(toTest).isEmpty();
        assertTrue(result);

    }

    @Test
    void expertSystemShouldFillTreeMap() {
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
        Queue<String> teste;
        teste = test1.falseCasPlayCardValTwo(ordrePlayerBase, player, ordrePlayerWin, newPlayer);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (Objects.equals(elem1, elem2)) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);
    }

    @Test
    void updateNewPlayer() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        Queue<String> ordrePlayerBase = new LinkedList<>();
        ordrePlayerBase.add("Joueur3");
        ordrePlayerBase.add("Joueur2");
        ordrePlayerBase.add("Joueur1");
        ordrePlayerBase.add("Joueur4");
        boolean casPlayCardValTwo = false;
        Queue<String> ordrePlayerWin = new LinkedList<>();
        ordrePlayerWin.add("Joueur2");
        ordrePlayerWin.add("joueur1");
        Queue<String> player = new LinkedList<>();
        player.add("Joueur4");
        Queue<String> newPlayer = new LinkedList<>();
        newPlayer.add("Joueur4");
        Queue<String> toTest = new LinkedList<>();
        toTest.add("Joueur4");
        toTest.add("Joueur3");

        Queue<String> teste;
        teste = test1.updateNewPlayer(ordrePlayerBase, casPlayCardValTwo, ordrePlayerWin, newPlayer, player);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (Objects.equals(elem1, elem2)) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);

    }

    @Test
    void updateNewPlayer2() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        Queue<String> ordrePlayerBase = new LinkedList<>();
        ordrePlayerBase.add("Joueur4");
        ordrePlayerBase.add("Joueur3");
        ordrePlayerBase.add("Joueur2");
        ordrePlayerBase.add("Joueur1");
        boolean casPlayCardValTwo = true;
        Queue<String> ordrePlayerWin = new LinkedList<>();
        Queue<String> player = new LinkedList<>();
        player.add("Joueur4");
        Queue<String> newPlayer = new LinkedList<>();
        newPlayer.add("Joueur4");
        Queue<String> toTest = new LinkedList<>();
        toTest.add("Joueur4");
        toTest.add("Joueur3");
        toTest.add("Joueur2");
        toTest.add("Joueur1");

        Queue<String> teste;
        teste = test1.updateNewPlayer(ordrePlayerBase, casPlayCardValTwo, ordrePlayerWin, newPlayer, player);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (Objects.equals(elem1, elem2)) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);
    }

    @Test
    void organiserOrdrePlayerBaseEnFonctionNextPlay() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        String namePlayer = "Joueur4";
        Queue<String> ordrePlayerBase = new LinkedList<>();
        ordrePlayerBase.add("Joueur3");
        ordrePlayerBase.add("Joueur4");
        ordrePlayerBase.add("Joueur2");
        ordrePlayerBase.add("Joueur1");
        Queue<String> toTest = new LinkedList<>();
        toTest.add("Joueur2");
        toTest.add("Joueur1");
        toTest.add("Joueur3");
        toTest.add("Joueur4");

        Queue<String> teste;
        teste = test1.organiserOrdrePlayerBaseEnFonctionNextPlay(namePlayer, ordrePlayerBase);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (Objects.equals(elem1, elem2)) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);
    }

    @Test
    void addOrdrePlayerWinIfNotAdd() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        HashMap<String, ArrayList<Card>> toTest = new HashMap<>();
        ArrayList<Card> handToTest = new ArrayList<>();
        handToTest.add(new Card(CardColor.SPADE, CardValue.FIVE));
        handToTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        handToTest.add(new Card(CardColor.CLUB, CardValue.FIVE));
        handToTest.add(new Card(CardColor.HEART, CardValue.FIVE));
        toTest.put("P1", handToTest);
        test1.playerCards = toTest;

        String winnerTemp = "P1";
        Queue<String> ordrePlayersWin = new LinkedList<>();
        ordrePlayersWin.add("P1");
        ordrePlayersWin.add("P2");
        ordrePlayersWin.add("P3");
        ordrePlayersWin.add("P4");
        boolean allEndTurn = false;

        test1.addOrdrePlayerWinIfNotAdd(winnerTemp, ordrePlayersWin, allEndTurn);

        assertFalse(test1.addOrdrePlayerWinIfNotAdd(winnerTemp, ordrePlayersWin, allEndTurn));
    }

    @Test
    void shouldContainP1() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        HashMap<String, ArrayList<Card>> toTest = new HashMap<>();
        ArrayList<Card> handToTest = new ArrayList<>();
        toTest.put("P1", handToTest);
        test1.playerCards = toTest;

        String winnerTemp = "P1";
        Queue<String> ordrePlayersWin = new LinkedList<>();
        ordrePlayersWin.add("P2");
        ordrePlayersWin.add("P3");
        ordrePlayersWin.add("P4");
        boolean allEndTurn = false;

        test1.addOrdrePlayerWinIfNotAdd(winnerTemp, ordrePlayersWin, allEndTurn);
        assertTrue(ordrePlayersWin.contains("P1"));

    }

    @Test
    void updateQueueForNextRound() {
        HashSet<String> players = new HashSet<>();
        players.add("Joueur3");
        players.add("Joueur2");
        players.add("Joueur1");
        players.add("Joueur4");
        var test1 = new LocalPresidentGame(players);

        Queue<String> ordrePlayerBase = new LinkedList<>();
        ordrePlayerBase.add("Joueur3");
        ordrePlayerBase.add("Joueur2");
        ordrePlayerBase.add("Joueur1");
        ordrePlayerBase.add("Joueur4");
        Queue<String> player = new LinkedList<>();
        player.add("Joueur2");
        Queue<String> ordrePlayerWin = new LinkedList<>();
        ordrePlayerWin.add("Joueur1");
        ordrePlayerWin.add("Joueur3");
        String winnerTemp = "Joueur2";
        boolean casPlayCardValTwo = false;
        Queue<String> toTest = new LinkedList<>();
        toTest.add("Joueur4");
        toTest.add("Joueur2");

        Queue<String> teste;
        teste = test1.updateQueueForNextRound(ordrePlayerBase, player, ordrePlayerWin, winnerTemp, casPlayCardValTwo);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (Objects.equals(elem1, elem2)) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);

    }

    @Test
    void updateQueueForNextRound2() {
        HashSet<String> players = new HashSet<>();
        players.add("Joueur3");
        players.add("Joueur2");
        players.add("Joueur1");
        players.add("Joueur4");
        var test1 = new LocalPresidentGame(players);

        Queue<String> ordrePlayerBase = new LinkedList<>();
        ordrePlayerBase.add("Joueur1");
        ordrePlayerBase.add("Joueur2");
        ordrePlayerBase.add("Joueur3");
        ordrePlayerBase.add("Joueur4");
        Queue<String> player = new LinkedList<>();
        player.add("Joueur3");
        Queue<String> ordrePlayerWin = new LinkedList<>();
        ordrePlayerWin.add("Joueur1");
        String winnerTemp = "Joueur3";
        boolean casPlayCardValTwo = false;
        Queue<String> toTest = new LinkedList<>();
        toTest.add("Joueur4");
        toTest.add("Joueur2");
        toTest.add("Joueur3");

        Queue<String> teste;
        teste = test1.updateQueueForNextRound(ordrePlayerBase, player, ordrePlayerWin, winnerTemp, casPlayCardValTwo);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (Objects.equals(elem1, elem2)) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);

    }

    @Test
    void updateQueueForNextRound3() {
        HashSet<String> players = new HashSet<>();
        players.add("Joueur3");
        players.add("Joueur4");
        players.add("Joueur1");
        players.add("Joueur2");
        var test1 = new LocalPresidentGame(players);

        Queue<String> ordrePlayerBase = new LinkedList<>();
        ordrePlayerBase.add("Joueur3");
        ordrePlayerBase.add("Joueur4");
        ordrePlayerBase.add("Joueur1");
        ordrePlayerBase.add("Joueur2");
        Queue<String> player = new LinkedList<>();
        player.add("Joueur1");
        player.add("Joueur2");
        player.add("Joueur3");
        Queue<String> ordrePlayerWin = new LinkedList<>();
        ordrePlayerWin.add("Joueur4");
        String winnerTemp = "Joueur4";
        boolean casPlayCardValTwo = true;
        Queue<String> toTest = new LinkedList<>();
        toTest.add("Joueur1");
        toTest.add("Joueur2");
        toTest.add("Joueur3");

        Queue<String> teste;
        teste = test1.updateQueueForNextRound(ordrePlayerBase, player, ordrePlayerWin, winnerTemp, casPlayCardValTwo);
        int tailleQueue = toTest.size();
        boolean identique = false;
        for (int i = 0; i < tailleQueue; i++) {
            String elem1 = toTest.poll();
            String elem2 = teste.poll();
            if (Objects.equals(elem1, elem2)) {
                identique = true;
            } else {
                identique = false;
                break;
            }
        }
        assertTrue(identique);
    }

    @Test
    void addOrderPlayerWin() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        HashMap<String, ArrayList<Card>> toTest = new HashMap<>();
        ArrayList<Card> handToTest = new ArrayList<>();
        toTest.put("P1", handToTest);
        test1.playerCards = toTest;

        Queue<String> playersToTest = new LinkedList<>();
        playersToTest.add("P1");
        playersToTest.add("P2");
        playersToTest.add("P3");
        playersToTest.add("P4");
        Queue<String> ordrePlayersWin = new LinkedList<>();
        ordrePlayersWin.add("P2");
        ordrePlayersWin.add("P3");
        ordrePlayersWin.add("P4");
        test1.addOrdrePlayerWin("P1", playersToTest, ordrePlayersWin);
        assertTrue(playersToTest.contains("P1"));
    }

    @Test
    void shouldntContainP1() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);
        HashMap<String, ArrayList<Card>> toTest = new HashMap<>();
        ArrayList<Card> handToTest = new ArrayList<>();
        handToTest.add(new Card(CardColor.SPADE, CardValue.FIVE));
        handToTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        handToTest.add(new Card(CardColor.CLUB, CardValue.FIVE));
        handToTest.add(new Card(CardColor.HEART, CardValue.FIVE));
        toTest.put("P1", handToTest);
        test1.playerCards = toTest;

        Queue<String> playersToTest = new LinkedList<>();
        playersToTest.add("P2");
        playersToTest.add("P3");
        playersToTest.add("P4");
        Queue<String> ordrePlayersWin = new LinkedList<>();
        ordrePlayersWin.add("P2");
        ordrePlayersWin.add("P3");
        ordrePlayersWin.add("P4");
        test1.addOrdrePlayerWin("P1", playersToTest, ordrePlayersWin);
        assertTrue(playersToTest.contains("P1"));
    }

    @Test
    void handShouldBeSame() {
        HashSet<String> players = new HashSet<>();
        var test1 = new LocalPresidentGame(players);

        int nbDeleteCard = 0;
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(CardColor.SPADE, CardValue.FIVE));
        hand.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        ArrayList<Card> cardPlay = new ArrayList<>();
        hand.add(new Card(CardColor.SPADE, CardValue.FIVE));
        hand.add(new Card(CardColor.HEART, CardValue.FIVE));
        ArrayList<Card> toTest = new ArrayList<>(hand);
        test1.deleteCardInHand(nbDeleteCard, hand, cardPlay);
        assertEquals(hand, toTest);
    }

    @Test
    void getCardOrGameOver(){
        HashSet<String> players = new HashSet<>();
        players.add("Joueur3");
        players.add("Joueur4");
        players.add("Joueur1");
        players.add("Joueur2");
        var test1 = new LocalPresidentGame(players);

        String namePlayer = "Joueur2";
        ArrayList<Card> winnerHand = new ArrayList<>();
        ArrayList<Card> toTest= new ArrayList<>();

        ArrayList<Card> teste;
        teste = test1.getCardOrGameOver(winnerHand, namePlayer);
        System.out.println("blabala"+ teste);
        assertEquals(teste, toTest);
    }

    @Test
    void playerRole(){
        HashSet<String> players = new HashSet<>();
        players.add("Joueur3");
        players.add("Joueur4");
        players.add("Joueur1");
        players.add("Joueur2");
        var test1 = new LocalPresidentGame(players);

        ArrayList<String> test;
        test = test1.play();

        boolean playerNotNull = true;

        for(String player : test){
            if (player == null) {
                playerNotNull = false;
                break;
            }
        }
        assertTrue(playerNotNull);
    }
}
