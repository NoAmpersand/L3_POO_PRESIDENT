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
        Queue<String> players = new LinkedList<>(this.getInitialPlayers());
        Queue<String> playersOrdreBase = new LinkedList<>();
        // L'ordre des gagnants
        Queue<String> ordrePlayersWin = new LinkedList<>();

        // On ajoute le i-ième player dans la queue
        // On le met directement dans la fin de la queue
        String firstPlayerInRound = players.poll();
        players.offer(firstPlayerInRound);
        playersOrdreBase.offer(firstPlayerInRound);

        String secondPlayerInRound = players.poll();
        players.offer(secondPlayerInRound);
        playersOrdreBase.offer(secondPlayerInRound);

        String thirdPlayerInRound = players.poll();
        players.offer(thirdPlayerInRound);
        playersOrdreBase.offer(thirdPlayerInRound);

        String fourthPlayerInRound = players.poll();
        players.offer(fourthPlayerInRound);
        playersOrdreBase.offer(fourthPlayerInRound);

        // Boucle while il reste + d'un joueur
        while (players.size() > 1) {
            players = playRound(players, ordrePlayersWin, playersOrdreBase);
        }

        String president = ordrePlayersWin.poll();
        String vicePresident = ordrePlayersWin.poll();
        String viceTrouDuCul = ordrePlayersWin.poll();
        String trouDuCul = ordrePlayersWin.poll();

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

    public void endTurnFiller(Map<String, Boolean> endTurn, Queue<String> players) {
        for (String player : players) {
            endTurn.put(player, false);
        }
    }

    private Queue<String> updateQueueForNextRound(Queue<String> ordrePlayerBase, Queue<String> players,
                                                  Queue<String> ordrePlayersWin, String winnerTemp) {
        String playerEtudier;
        while(true) {
            playerEtudier = ordrePlayerBase.poll();
            ordrePlayerBase.offer(playerEtudier);
            if (Objects.equals(playerEtudier, winnerTemp)) {
                break;
            }
        }
        Queue<String> newPlayers = new LinkedList<>();
        if (players.peek() != null) {
            newPlayers.add(players.peek());
        }
        for (String playerAjout : ordrePlayerBase) {
            if (Objects.equals(playerAjout, players.peek())) {
                break;
            }
            if (!ordrePlayersWin.contains(playerAjout) && !Objects.equals(newPlayers.peek(), playerAjout)) {
                newPlayers.add(playerAjout);
            }
        }
        HashMap<Integer, Integer> playerHandFirstNewPlayers = getPlayerMapCard(newPlayers.peek());
        if(playerHandFirstNewPlayers.isEmpty()){
            newPlayers.remove();
        }
        return newPlayers;
    }

    protected Queue<String> playRound(Queue<String> players, Queue<String> ordrePlayersWin,
            Queue<String> ordrePlayerBase) {
        HashMap<String, Boolean> endTurn = new HashMap<>();
        endTurnFiller(endTurn, players);
        boolean allEndTurn = false;
        ArrayList<Card> winnerHand = new ArrayList<>();
        String winnerTemp = "";
        String namePlayer;
        while (!allEndTurn) {
            namePlayer = players.poll();
            if (!"".equals(winnerTemp)) {
                HashMap<Integer, Integer> playerHandWinner = getPlayerMapCard(winnerTemp);
                if (playerHandWinner.isEmpty() && !ordrePlayersWin.contains(winnerTemp)) {
                    ordrePlayersWin.add(winnerTemp);
                }
            }
            HashMap<Integer, Integer> playerHand = getPlayerMapCard(namePlayer);
            assert winnerTemp != null;
            if (winnerTemp.equals(namePlayer) || playerHand.isEmpty()) {
                break;
            }
            ArrayList<Card> playerCards = getCardOrGameOver(winnerHand, namePlayer);
            if (playerCards.isEmpty()) {
                endTurn.put(namePlayer, true);
            } else {
                winnerHand = playerCards;
                winnerTemp = namePlayer;
                players.offer(namePlayer);
            }
            if (0 == players.size() - 1) {
                allEndTurn = true;
            }
            System.out.println("ordre " + players);
            System.out.println("ordre winner : " + ordrePlayersWin);
            System.out.println("winner Temp " + winnerTemp);
            System.out.println("pass turn : " + endTurn);
        }
        // For Trou du Cul
        System.out.println("player final" + players);
        if (players.size() == 1 && ordrePlayersWin.size() == 3) {
            ordrePlayersWin.add(players.poll());
        }
        return updateQueueForNextRound(ordrePlayerBase, players, ordrePlayersWin, winnerTemp);
    }

    /**
     * this method must be called when a winner is identified
     *
     * @param winner the final winner of the same
     */
    protected abstract void declareWinner(String winner);

    protected abstract ArrayList<Card> getCardOrGameOver(ArrayList<Card> winnerHand, String namePlayer);

    protected abstract Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer, String cardProviderPlayerOpponent);
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
