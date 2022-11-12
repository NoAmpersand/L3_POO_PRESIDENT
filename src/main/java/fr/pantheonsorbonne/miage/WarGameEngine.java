package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * this class is a abstract version of the engine, to be used locally on through the network
 */
public abstract class WarGameEngine {

    public static final int CARDS_IN_HAND_INITIAL_COUNT = 3;

    /**
     * play a war game wit the provided players
     */
    public void play() {
        //send the initial hand to every players
        for (String playerName : getInitialPlayers()) {
            //get random cards
            Card[] cards = Deck.getRandomCards(CARDS_IN_HAND_INITIAL_COUNT);
            // transform them to String
            String hand = Card.cardsToString(cards);
            //send them to this players
            giveCardsToPlayer(playerName, hand);
        }
        // make a queue with all the players
        final Queue<String> players = new LinkedList<>();
        players.addAll(this.getInitialPlayers());
        //repeat until only 1 player is left
        while (players.size() > 1) {
            //these are the cards played by the players on this round
            Queue<Card> roundDeck = new LinkedList<>();

            //Si playerIsFinished() == true remove from Queue

            //take the first player form the queue
            String firstPlayerInRound = players.poll();
            //and put it immediately at the end
            players.offer(firstPlayerInRound);

            //take the second player from the queue
            String secondPlayerInRound = players.poll();
            //and put it back immediately also
            players.offer(secondPlayerInRound);


            String thirdPlayerInRound = players.poll();
            players.offer(thirdPlayerInRound);

            //loop until there is a winner for this round
            while (true) {


                if (playRound(players, firstPlayerInRound, secondPlayerInRound, roundDeck)) break;
            }


        }
        //since we've left the loop, we have only 1 player left: the winner
        String winner = players.poll();
        //send him the gameover and leave
        declareWinner(winner);
        System.out.println(winner + " won! bye");
        System.exit(0);
    }

    /**
     * provide the list of the initial players to play the game
     *
     * @return
     */
    protected abstract Set<String> getInitialPlayers();

    /**
     * give some card to a player
     *
     * @param playerName the player that will receive the cards
     * @param hand       the cards as a string (to be converted later)
     */
    protected abstract void giveCardsToPlayer(String playerName, String hand);

    /**
     * Play a single round
     *
     * @param players             the queue containing the remaining players
     * @param firstPlayerInRound  the first contestant in this round
     * @param secondPlayerInRound the second contestant in this roun
     * @param roundDeck           possible cards left over from previous rounds
     * @return true if we have a winner for this round, false otherwise
     */
    protected boolean playRound(Queue<String> players, String firstPlayerInRound, String secondPlayerInRound, Queue<Card> roundDeck) {

        /*Mettre while si fin de tour ou deux consécutifs ne peuvent pas jouer
        dans la boucle : si Queue.length = 3 ET Joueur.passeSonTour() => joueur passe en fin de Queue
        dans la boucle : si Queue.length = 2 ET Joueur.passeSonTour() => joueur remove from Queue*/
        //here, we try to get the first player card
        Card firstPlayerCard = getCardOrGameOver(roundDeck, firstPlayerInRound, secondPlayerInRound);
        //getCardOrGameOver nécessite implémentation de "passer le tour"
        if (firstPlayerCard == null) {
            players.remove(firstPlayerInRound);
            return true;
        }
        //here we also get the second player card
        Card secondPlayerCard = getCardOrGameOver(roundDeck, secondPlayerInRound, firstPlayerInRound);
        if (secondPlayerCard == null) {
            players.remove(secondPlayerInRound);
            return true;
        }


        //put the two cards on the roundDeck
        //On offer des tableaux de Card
        //Si queue vide on offer tableaux de taille n (n = pairs|tri|carré)
        //Else : prend 1er element, calcule arrayList[0].length = m
        //On fait tableau de taille m
        roundDeck.offer(firstPlayerCard);
        roundDeck.offer(secondPlayerCard);

        //compute who is the winner
        String winner = getWinner(firstPlayerInRound, secondPlayerInRound, firstPlayerCard, secondPlayerCard);
        //if there's a winner, we distribute the card to him
        if (winner != null) {
            giveCardsToPlayer(roundDeck, winner);
            return true;
        }
        //otherwise we do another round.
        return false;
    }

    /**
     * this method must be called when a winner is identified
     *
     * @param winner the final winner of the same
     */
    protected abstract void declareWinner(String winner);

    /**
     * get a card from a player. If the player doesn't have a card, it will be declared loser and all the left over cards will be given to his opponent
     *
     * @param leftOverCard               card left over from another round
     * @param cardProviderPlayer         the player that should give a card
     * @param cardProviderPlayerOpponent the Opponent of this player
     * @return a card of null if player cardProviderPlayer is gameover
     */
    protected abstract Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer, String cardProviderPlayerOpponent);

    /**
     * give the winner of a round
     *
     * @param contestantA     a contestant
     * @param contestantB     another contestand
     * @param contestantACard its card
     * @param contestantBCard its card
     * @return the name of the winner or null if it's a tie
     */
    protected static String getWinner(String contestantA, String contestantB, Card contestantACard, Card contestantBCard) {
        if (contestantACard.getValue().getRank() > contestantBCard.getValue().getRank()) {
            return contestantA;
        } else if (contestantACard.getValue().getRank() < contestantBCard.getValue().getRank()) {
            return contestantB;
        }
        return null;
    }

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
}
