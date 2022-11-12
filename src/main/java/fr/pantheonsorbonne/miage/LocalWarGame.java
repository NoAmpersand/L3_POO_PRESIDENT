package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;

import java.util.*;
import java.util.stream.Collectors;

/**
 * this class implements the war game locally
 */
public class LocalWarGame extends WarGameEngine {

    private final Set<String> initialPlayers;
    private final Map<String, Queue<Card>> playerCards = new HashMap<>();

    public LocalWarGame(Set<String> initialPlayers) {
        this.initialPlayers = initialPlayers;
        for (String player : initialPlayers) {
            playerCards.put(player, new LinkedList<>());
        }
    }

    public static void main(String... args) {
        LocalWarGame localWarGame = new LocalWarGame(Set.of("Joueur1", "Joueur2", "Joueur3"));
        localWarGame.play();



    }

    @Override
    protected Set<String> getInitialPlayers() {
        return this.initialPlayers;
    }

    @Override
    protected void giveCardsToPlayer(String playerName, String hand) {
        List<Card> cards = Arrays.asList(Card.stringToCards(hand));
        this.giveCardsToPlayer(cards, playerName);
    }

    @Override
    protected boolean playRound(Queue<String> players, String playerA, String playerB, Queue<Card> roundDeck) {
        System.out.println("New round:");
        System.out.println(this.playerCards.keySet().stream().filter(p -> !this.playerCards.get(p).isEmpty()).map(p -> p + " has " + this.playerCards.get(p).stream().map(c -> c.toFancyString()).collect(Collectors.joining(" "))).collect(Collectors.joining("\n")));
        System.out.println();
        return super.playRound(players, playerA, playerB, roundDeck);

    }

    @Override
    protected void declareWinner(String winner) {
        System.out.println(winner + " has won!");
    }

    @Override
    protected Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer, String cardProviderPlayerOpponent) {

        if (!this.playerCards.containsKey(cardProviderPlayer) || this.playerCards.get(cardProviderPlayer).isEmpty()) {
            this.playerCards.get(cardProviderPlayerOpponent).addAll(leftOverCard);
            this.playerCards.remove(cardProviderPlayer);
            return null;
        } else {
            return this.playerCards.get(cardProviderPlayer).poll();
        }
    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String winner) {
        List<Card> cards = new ArrayList<>();
        cards.addAll(roundStack);
        Collections.shuffle(cards);
        this.playerCards.get(winner).addAll(cards);
    }

    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException {
        if (this.playerCards.get(player).isEmpty()) {
            throw new NoMoreCardException();
        } else {
            return this.playerCards.get(player).poll();
        }
    }
}
