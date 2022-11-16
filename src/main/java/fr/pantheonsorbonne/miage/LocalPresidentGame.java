package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;

import java.util.*;
import java.util.stream.Collectors;

/**
 * this class implements the war game locally
 */
public class LocalPresidentGame extends PresidentGameEngine {

    private final Set<String> initialPlayers;
    private final Map<String, ArrayList<Card>> playerCards = new HashMap<>();

    public LocalPresidentGame(Set<String> initialPlayers) {
        this.initialPlayers = initialPlayers;
        for (String player : initialPlayers) {
            playerCards.put(player, new ArrayList<>());
        }
    }

    public static void main(String... args) {
        LocalPresidentGame localWarGame = new LocalPresidentGame(Set.of("Joueur1", "Joueur2", "Joueur3", "Joueur4"));
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
        System.out
                .println(
                        this.playerCards
                                .keySet().stream().filter(p -> !this.playerCards.get(p).isEmpty()).map(
                                        p -> p + " has "
                                                + this.playerCards.get(p).stream().map(c -> c.toFancyString())
                                                        .collect(Collectors.joining(" ")))
                                .collect(Collectors.joining("\n")));
        System.out.println();
        return super.playRound(players, playerA, playerB, roundDeck);

    }

    @Override
    protected void declareWinner(String winner) {
        System.out.println(winner + " has won!");
    }

    @Override
    // Cette méthode prends les cartes du dernier gagnant et les cartes du joueur et
    // le joueur renvoie une ou plusieurs cartes adéquates
    protected ArrayList<Card> getCardOrGameOver(TreeMap<String, ArrayList<Card>> winnerTemp, String namePlayer) {
        /*
         * Méthode à changer :
         * À partir de la main, on doit poser aucune ou plusieurs cartes de même valeur
         * Elle prend comme paramètre la main
         * Elle return la main + cartes à jouer + variable passerLeTour ou passerLePli
         */
        ArrayList<Card> hand = this.playerCards.get(namePlayer);
        ArrayList<Card> winnerHand = winnerTemp.firstEntry().getValue();
        Map<Integer, Integer> mapHand = new HashMap<>();
        for (Card card : hand) {
            if (mapHand.containsKey(card.valueToInt())) {
                int valueToIncrement = mapHand.get(card.valueToInt());
                valueToIncrement++;
                mapHand.put(mapHand.get(card.valueToInt()), valueToIncrement);
            } else {
                mapHand.put(mapHand.get(card.valueToInt()), 1);
            }
        }
        Map<Integer, Integer> playableCards = new HashMap<>();
        for (int cardValue : mapHand.keySet()) {
            if (winnerHand.get(0).valueToInt() <= cardValue) {
                if (winnerHand.size() <= mapHand.get(cardValue)) {
                    playableCards.put(cardValue, mapHand.get(cardValue));
                }
            }
        }
        // appelle methode systemeExpert qui renvoie les cartes à jouer
        // map<valeur carte qui sera jouer, nbcarte de cette valeur qui sera jouer
        TreeMap<Integer, Integer> mapPlay = new TreeMap<>();
        // valeur par default
        mapPlay.put(2, 2);

        ArrayList<Card> cardPlay = new ArrayList<>();
        int nbDeleteCard = 0;
        int index = 0;
        for (Card card : hand) {
            if (nbDeleteCard >= mapPlay.firstEntry().getValue()) {
                break;
            }
            if (mapHand.get(card.valueToInt()) == mapPlay.firstEntry().getValue()) {
                Card oneCard = hand.get(index);
                cardPlay.add(oneCard);
                nbDeleteCard += 1;
            }
            index += 1;
        }
        for (Card card : hand) {
            if (nbDeleteCard >= mapPlay.firstEntry().getValue()) {
                break;
            }
            if (mapHand.get(card.valueToInt()) == mapPlay.firstEntry().getValue()) {
                hand.remove(card);
            }
        }

        return cardPlay;

        /*
         * if (!this.playerCards.containsKey(cardProviderPlayer) ||
         * this.playerCards.get(cardProviderPlayer).isEmpty()) {
         * this.playerCards.get(cardProviderPlayerOpponent).addAll(leftOverCard);
         * this.playerCards.remove(cardProviderPlayer);
         * return null;
         * } else {
         * return this.playerCards.get(cardProviderPlayer).remove(0);
         * }
         */
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
            return this.playerCards.get(player).remove(0);
        }
    }

    // @Override
    protected Queue<String> passTurn(Queue<String> players) {

        String playerPass = players.poll();
        players.offer(playerPass);

        return players;
    }

    protected fetchQofH
}
