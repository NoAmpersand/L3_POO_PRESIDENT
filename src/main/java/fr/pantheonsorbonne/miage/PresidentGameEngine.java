package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;

import java.util.*;

import org.apache.camel.component.properties.SysPropertiesFunction;


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

    void endTurnFiller(HashMap<String, Boolean> endTurn, Queue<String> players) {
        for (String player : players) {
            endTurn.put(player, false);
        }
    }

    private void organiserOrdrePlayerBaseEnFonctionNextPlay(String namePlayer, Queue<String> ordrePlayerBase) {
        String playerEtudier;
        do {
            playerEtudier = ordrePlayerBase.poll();
            ordrePlayerBase.offer(playerEtudier);
        } while (!Objects.equals(playerEtudier, namePlayer));
    }

    protected Queue<String> falseCasPlayCardValTwo(Queue<String> ordrePlayerBase, Queue<String> players,
            Queue<String> ordrePlayersWin, Queue<String> newPlayers) {
        for (String playerAjout : ordrePlayerBase) {
            if (Objects.equals(playerAjout, players.peek())) {
                break;
            }
            if (!ordrePlayersWin.contains(playerAjout) && !Objects.equals(newPlayers.peek(), playerAjout)) {
                newPlayers.add(playerAjout);
            }
        }
        return newPlayers;
    }

    protected Queue<String> updateNewPlayer(Queue<String> ordrePlayerBase, boolean casPlayCardValTwo,
            Queue<String> ordrePlayersWin, Queue<String> newPlayers, Queue<String> players) {
        if (!casPlayCardValTwo) {
            falseCasPlayCardValTwo(ordrePlayerBase, players, ordrePlayersWin, newPlayers);
        } else {
            for (String playerAjout : ordrePlayerBase) {
                if (!ordrePlayersWin.contains(playerAjout) && !Objects.equals(newPlayers.peek(), playerAjout)) {
                    newPlayers.add(playerAjout);
                }
            }
        }
        return newPlayers;
    }

    private Queue<String> updateQueueForNextRound(Queue<String> ordrePlayerBase, Queue<String> players,
            Queue<String> ordrePlayersWin, String winnerTemp, boolean casPlayCardValTwo) {
        HashMap<Integer, Integer> winnerHandTestEmpty = getPlayerMapCard(winnerTemp);
        Queue<String> newPlayers = new LinkedList<>();
        if (!winnerHandTestEmpty.isEmpty() || casPlayCardValTwo) {
            organiserOrdrePlayerBaseEnFonctionNextPlay(winnerTemp, ordrePlayerBase);
            if (players.peek() != null) {
                newPlayers.add(players.peek());
            }
            System.out.println(ordrePlayerBase);
        } else {
            String playerPlayNextWinner = players.poll();
            organiserOrdrePlayerBaseEnFonctionNextPlay(playerPlayNextWinner, ordrePlayerBase);
        }
        updateNewPlayer(ordrePlayerBase, casPlayCardValTwo, ordrePlayersWin, newPlayers, players);
        if (!newPlayers.isEmpty()) {
            HashMap<Integer, Integer> playerHandFirstNewPlayers = getPlayerMapCard(newPlayers.peek());
            if (playerHandFirstNewPlayers.isEmpty()) {
                newPlayers.remove();
            }
        }
        return newPlayers;
    }

    void addOrdrePlayerWin(String namePlayer, Queue<String> players, Queue<String> ordrePlayersWin) {
        HashMap<Integer, Integer> playerHandTestEmpty = getPlayerMapCard(namePlayer);
        if (!playerHandTestEmpty.isEmpty()) {
            players.offer(namePlayer);
        } else {
            if (!ordrePlayersWin.contains(namePlayer)) {
                ordrePlayersWin.add(namePlayer);
            }
        }
    }

    protected boolean addOrdrePlayerWinIfNotAdd(String winnerTemp,Queue<String> ordrePlayersWin, boolean allEndTurn) {
        HashMap<Integer, Integer> playerHandWinner = getPlayerMapCard(winnerTemp);
        if (playerHandWinner.isEmpty() && !ordrePlayersWin.contains(winnerTemp)) {
            ordrePlayersWin.add(winnerTemp);
            allEndTurn = true;
        }
        return allEndTurn;
    }

    protected Queue<String> playRound(Queue<String> players, Queue<String> ordrePlayersWin,
            Queue<String> ordrePlayerBase) {
        HashMap<String, Boolean> endTurn = new HashMap<>();
        endTurnFiller(endTurn, players);
        boolean allEndTurn = false;
        boolean casPlayCardValTwo = false;
        ArrayList<Card> winnerHand = new ArrayList<>();
        String winnerTemp = "";
        String namePlayer;
        while (!allEndTurn) {
            namePlayer = players.poll();
            if (winnerTemp.equals(namePlayer)) {
                break;
            }
            ArrayList<Card> playerCards = getCardOrGameOver(winnerHand, namePlayer);
            if (playerCards.isEmpty()) {
                endTurn.put(namePlayer, true);
            } else if (playerCards.get(0).valueToInt() == 14) {
                winnerHand = playerCards;
                winnerTemp = namePlayer;
                allEndTurn = true;
                casPlayCardValTwo = true;
            } else {
                winnerHand = playerCards;
                winnerTemp = namePlayer;
                addOrdrePlayerWin(namePlayer, players, ordrePlayersWin);
            }
            if (0 == players.size() - 1) {
                allEndTurn = true;
            }
            if (!"".equals(winnerTemp)) {
                allEndTurn = addOrdrePlayerWinIfNotAdd(winnerTemp, ordrePlayersWin, allEndTurn);
            }

        }
        if (players.size() == 1 && ordrePlayersWin.size() == 3) {
            ordrePlayersWin.add(players.poll());
        }
        if (ordrePlayersWin.size() == 4) {
            return new LinkedList<>();
        }
        return updateQueueForNextRound(ordrePlayerBase, players, ordrePlayersWin, winnerTemp, casPlayCardValTwo);
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

    protected abstract HashMap<Integer, Integer> getPlayerMapCard(String playerName);
}
