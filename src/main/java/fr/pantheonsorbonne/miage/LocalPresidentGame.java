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
    Map<String, ArrayList<Card>> playerCards = new HashMap<>();

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
    protected Queue<String> playRound(Queue<String> players, Queue<String> ordrePlayersWin, Queue<String> ordrePlayerBase) {
        System.out.println("New round:");
        System.out
                .println(
                        this.playerCards
                                .keySet().stream().filter(p -> !this.playerCards.get(p).isEmpty()).map(
                                        p -> p + " has "
                                                + this.playerCards.get(p).stream().map(Card::toFancyString)
                                                        .collect(Collectors.joining(" ")))
                                .collect(Collectors.joining("\n")));
        System.out.println();
        return super.playRound(players, ordrePlayersWin, ordrePlayerBase);

    }

    @Override
    protected void declareWinner(String winner) {
        System.out.println(winner + " has won!");
    }

    protected void fillHand(Map<Integer, Integer> handToFill, ArrayList<Card> handToVerify) {
        for (Card card : handToVerify) {
            if (handToFill.containsKey(card.valueToInt())) {
                handToFill.put(card.valueToInt(), handToFill.get(card.valueToInt()) + 1);
            } else {
                handToFill.put(card.valueToInt(), 1);
            }
        }
    }

    protected void fillPlayableCards(HashMap<Integer, Integer> playableCards, Map<Integer, Integer> mapHand,
            ArrayList<Card> winnerHand, boolean firstTurn) {
        if (firstTurn) {
            for (Map.Entry<Integer, Integer> cardValue : mapHand.entrySet()) {
                if (playableCards.containsKey(cardValue.getKey())) {
                    playableCards.put(cardValue.getKey(), cardValue.getValue() + 1);
                } else {
                    playableCards.put(cardValue.getKey(), cardValue.getValue());
                }
            }
        } else {
            for (Map.Entry<Integer, Integer> cardValue : mapHand.entrySet()) {
                if (winnerHand.get(0).valueToInt() <= cardValue.getKey() && winnerHand.size() <= cardValue.getValue()) {
                    playableCards.put(cardValue.getKey(), cardValue.getValue());
                }
            }
        }
    }

    protected void deleteCardInHand(int nbDeleteCard, ArrayList<Card> hand, ArrayList<Card> cardPlay,
            TreeMap<Integer, Integer> mapPlay, String namePlayer) {
        if (nbDeleteCard == 0) {
            return;
        }
        for (int i = 0; i <= nbDeleteCard; i++) {
            for (Card card : hand) {
                if (cardPlay.contains(card) || mapPlay.firstEntry() == null) {
                    hand.remove(card);
                    break;
                }
            }
        }
        Collections.copy(this.playerCards.get(namePlayer), hand);
    }

    @Override
    // Cette méthode prends les cartes du dernier gagnant et les cartes du joueur et
    // le joueur renvoie une ou plusieurs cartes adéquates
    protected ArrayList<Card> getCardOrGameOver(ArrayList<Card> winnerHand, String namePlayer) {
        System.out.println("--------------------------------");
        System.out.println("joueur play " + namePlayer);
        boolean premierTour = winnerHand.isEmpty();
        ArrayList<Card> hand = this.playerCards.get(namePlayer);
        System.out.println("main joueur : " + hand);
        Map<Integer, Integer> mapHand = new HashMap<>();
        fillHand(mapHand, hand);
        HashMap<Integer, Integer> playableCards = new HashMap<>();
        fillPlayableCards(playableCards, mapHand, winnerHand, premierTour);
        System.out.println("carte jouable : " + playableCards);
        TreeMap<Integer, Integer> mapPlay = expertSystem(playableCards, winnerHand, premierTour);
        ArrayList<Card> cardPlay = new ArrayList<>();
        int nbDeleteCard = 0;
        int index = 0;
        System.out.println(mapPlay.firstEntry());
        if (mapPlay.firstEntry() != null) {
            for (Card card : hand) {
                if (nbDeleteCard >= mapPlay.firstEntry().getValue()) {
                    break;
                }
                if (card.valueToInt() == mapPlay.firstEntry().getKey()) {
                    Card oneCard = hand.get(index);
                    cardPlay.add(oneCard);
                    nbDeleteCard += 1;
                }
                index += 1;
            }
        }
        System.out.println(cardPlay);
        deleteCardInHand(nbDeleteCard, hand, cardPlay, mapPlay, namePlayer);

        System.out.println("hand retirer " + hand);
        return cardPlay;
    }

    protected TreeMap<Integer, Integer> expertSystem(Map<Integer, Integer> playableCards,
            ArrayList<Card> winnerHand, boolean premierTour) {

        TreeMap<Integer, Integer> playCard = new TreeMap<>();
        if (premierTour) {
            playCard = firstTurnExpertSystem(playableCards);
        } else {
            for (Map.Entry<Integer, Integer> card : playableCards.entrySet()) {
                if (card.getValue() <= winnerHand.size()) {
                    playCard.put(card.getKey(), card.getValue());
                    break;
                }
            }
        }
        System.out.println("playCard : " + playCard);
        return playCard;
    }

    protected TreeMap<Integer, Integer> firstTurnExpertSystem(Map<Integer, Integer> playableCards) {

        int maxCardDouble = 0;
        int minValueOfMaxCardDouble = 100;
        for (Map.Entry<Integer, Integer> card : playableCards.entrySet()) {
            if (maxCardDouble <= card.getValue() && minValueOfMaxCardDouble > card.getKey()) {
                maxCardDouble = card.getValue();
                minValueOfMaxCardDouble = card.getKey();
            }
        }
        TreeMap<Integer, Integer> playCard = new TreeMap<>();
        playCard.put(minValueOfMaxCardDouble, maxCardDouble);
        return playCard;
    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String winner) {
        List<Card> cards = new ArrayList<>(roundStack);
        Collections.shuffle(cards);
        this.playerCards.get(winner).addAll(cards);
    }

    protected String fetchQofH() {
        String specialPlayer = "";
        for (String player : playerCards.keySet()) {
            for (Card card : playerCards.get(player)) {
                if (card.verifQofH()) {
                    specialPlayer = player;
                }
            }
        }
        return specialPlayer;
    }

    protected ArrayList<Card> getPlayerCards(String playerName) {

        return playerCards.get(playerName);
    }

    protected HashMap<Integer, Integer> getPlayerMapCard(String playerName) {
        ArrayList<Card> playerHand = getPlayerCards(playerName);
        HashMap<Integer, Integer> mapHand = new HashMap<>();
        for (Card card : playerHand) {
            if (mapHand.containsKey(card.valueToInt())) {
                int valueToIncrement = mapHand.get(card.valueToInt());
                valueToIncrement++;
                mapHand.put(card.valueToInt(), valueToIncrement);
            } else {
                mapHand.put(card.valueToInt(), 1);
            }
        }
        return mapHand;
    }
}
