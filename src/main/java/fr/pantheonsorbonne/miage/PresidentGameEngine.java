package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;

import java.util.*;

/**
 * this class is a abstract version of the engine, to be used locally on through
 * the network
 */
public abstract class PresidentGameEngine {

    public static final int CARDS_IN_HAND_INITIAL_COUNT = 13;

    /**
     * play a war game wit the provided players
     */
    public void play() {
        // On initialise la main pour chaque joueur
        for (String playerName : getInitialPlayers()) {
            // On prend des cartes aléatoires
            Card[] cards = Deck.getRandomCards(CARDS_IN_HAND_INITIAL_COUNT);
            // On les change en String
            String hand = Card.cardsToString(cards);
            // On les donne au joueur
            giveCardsToPlayer(playerName, hand);
        }
        // Initialiser une queue avec tous les joueurs
        final Queue<String> players = new LinkedList<>(this.getInitialPlayers());
        // L'ordre des gagnants
        Queue<String> ordrePlayersWin = new LinkedList<>();


        // On ajoute le i-ième player dans la queue
        // On le met directement dans la fin de la queue
        String firstPlayerInRound = players.poll();
        players.offer(firstPlayerInRound);

        String secondPlayerInRound = players.poll();
        players.offer(secondPlayerInRound);

        String thirdPlayerInRound = players.poll();
        players.offer(thirdPlayerInRound);

        String fourthPlayerInRound = players.poll();
        players.offer(fourthPlayerInRound);

        // Boucle while il reste + d'un joueur
        while (players.size() > 1) {
            playRound(players, ordrePlayersWin);
        }

        String president = ordrePlayersWin.poll();
        String vicePresident = ordrePlayersWin.poll();
        String viceTrouDuCul = ordrePlayersWin.poll();
        String trouDuCul = players.poll();

        // send him the gameover and leave
        declareWinner(president);
        System.out.println(president + " won! bye");
        System.out.println("vice Président : " + vicePresident);
        System.out.println("vice Trou du Cul : " + viceTrouDuCul);
        System.out.println("Trou du Cul : " + trouDuCul);
        System.exit(0);
    }


    protected abstract Set<String> getInitialPlayers();

    /**
     * give some card to a player
     *
     * @param playerName the player that will receive the cards
     * @param hand       the cards as a string (to be converted later)
     */
    protected abstract void giveCardsToPlayer(String playerName, String hand);

    protected boolean allPlayerPass(HashMap<String, Boolean> endTurn) {
        int endTurnCounter = 0;
        int remainingPlayersInTurn = 0;
        for (Map.Entry<String, Boolean> playerEndTurn : endTurn.entrySet()) {
            if (Boolean.TRUE.equals(playerEndTurn.getValue())) { // si boolean == false à tester proposition
                                                                   // sonarlink
                endTurnCounter += 1;
            }
            remainingPlayersInTurn += 1;
        }
        return endTurnCounter + 1 == remainingPlayersInTurn;
    }

    private void endTurnFiller(HashMap<String, Boolean> endTurn, Queue<String> players){
        for (String player : players) {
            endTurn.put(player, true);
        }
    }

    private void playerHandFiller(Queue<String> players, ArrayList<Card> winnerHand, HashMap<Integer, Integer> playerHand, String namePlayer, HashMap<String, Boolean> endTurn){
        for (Map.Entry<Integer, Integer> cardValue : playerHand.entrySet()) {
            if (cardValue.getKey() >= winnerHand.get(0).valueToInt()
                    && cardValue.getValue() >= winnerHand.size() && !winnerHand.isEmpty()) {
                players.remove();
                players.add(namePlayer);
            } else {
                endTurn.put(namePlayer, false);
                players.remove();
            }
        }
    }


    protected Queue<String> playRound(Queue<String> players, Queue<String> ordrePlayersWin) {
        HashMap<String, Boolean> endTurn = new HashMap<>();
        endTurnFiller(endTurn, players);
        boolean allEndTurn = false;
        int turnPassCount = 0;
        ArrayList<Card> winnerHand = new ArrayList<>();
        String winnerTemp = "";
        String namePlayer = players.peek();
        while (!allEndTurn || Objects.equals(winnerTemp, namePlayer)) {
            System.out.println(players);
            namePlayer = players.poll();
            ArrayList<Card> playerCards = getCardOrGameOver(winnerHand, namePlayer);
            System.out.println(playerCards);
            if (playerCards.isEmpty()) {
                HashMap<Integer, Integer> playerHand = getPlayerMapCard(namePlayer);
                if (playerHand.isEmpty()) {
                    ordrePlayersWin.add(namePlayer);
                    return players;
                }
                playerHandFiller(players, winnerHand, playerHand,namePlayer, endTurn);
                turnPassCount += 1;
            } else {
                winnerHand = playerCards;
                winnerTemp = namePlayer;
                players.offer(namePlayer);
            }
            if (allPlayerPass(endTurn) || turnPassCount == players.size() - 1) {
                allEndTurn = true;
            }
        }
        return players;
    }

    /**
     * this method must be called when a winner is identified
     *
     * @param winner the final winner of the same
     */
    protected abstract void declareWinner(String winner);


    protected abstract ArrayList<Card> getCardOrGameOver(ArrayList<Card> winnerHand, String namePlayer);



    /**
     * give some card to a player
     *
     * @param playerName the player that will receive the cards
     * @param cards      the cards as a collection of cards
     */
    protected abstract void giveCardsToPlayer(Collection<Card> cards, String playerName);

    /**
     * get a card from a player
     *
     * @param player the player to give card
     * @return the card from the player
     * @throws NoMoreCardException if the player does not have a remaining card
     */
    protected abstract Card getCardFromPlayer(String player) throws NoMoreCardException;

    protected abstract HashMap<Integer, Integer> getPlayerMapCard(String playerName);
}
