package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.game.Card;


import java.util.*;
import java.util.stream.Collectors;

/**
 * cette classe implémente le jeu en local
 */
public class LocalPresidentGame extends PresidentGameEngine {

    private final Set<String> initialPlayers;
    Map<String, ArrayList<Card>> playerCards = new HashMap<>();

    /**
     * Cette methode initialise un player avec son arrayList
     * 
     * @param initialPlayers Set<String> qui represente tout les joueurs qui doit
     *                       etre initialiser
     */
    public LocalPresidentGame(Set<String> initialPlayers) {
        this.initialPlayers = initialPlayers;
        for (String player : initialPlayers) {
            playerCards.put(player, new ArrayList<>());
        }
    }

    /**
     * Cette methode est la méthode qui lance le jeu
     * 
     * @param args
     */
    public static void main(String... args) {
        LocalPresidentGame localWarGame = new LocalPresidentGame(Set.of("Joueur1", "Joueur2", "Joueur3", "Joueur4"));
        localWarGame.play();

    }

    @Override
    /**
     * Getter d'initialPlayer
     */
    protected Set<String> getInitialPlayers() {
        return this.initialPlayers;
    }

    @Override
    /**
     * Cette méthode donne des cartes à un joueur
     * 
     * @param playerName String nom du joueur
     * @param hand       String deck
     */
    protected void giveCardsToPlayer(String playerName, String hand) {
        List<Card> cards = Arrays.asList(Card.stringToCards(hand));
        this.giveCardsToPlayer(cards, playerName);
    }

    @Override
    /**
     * Cette methode permet d'effecter un round(un pli)
     * 
     * @param players Queue<String> ordre des joueurs
     * @param ordrePlayersWin Queue<String> ordre des joueurs qui ont gagnés
     * @param ordrePlayerBase Queue<String> ordre des joueurs de base
     */
    protected Queue<String> playRound(Queue<String> players, Queue<String> ordrePlayersWin,
            Queue<String> ordrePlayerBase) {
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
    /**
     * Cette méthode declare le gagnant de la game
     * 
     * @param winner String nom du joueur gagnant
     */
    protected void declareWinner(String winner) {
        System.out.println(winner + " has won!");
    }

    /**
     * Cette methode remplie une map handToFill a partir de la main d'un joueur
     * 
     * @param handToFill   map de la main du joueur
     * @param handToVerify ArrayList<Card> main du joueur
     */
    protected void fillHand(Map<Integer, Integer> handToFill, ArrayList<Card> handToVerify) {
        for (Card card : handToVerify) {
            if (handToFill.containsKey(card.valueToInt())) {
                handToFill.put(card.valueToInt(), handToFill.get(card.valueToInt()) + 1);
            } else {
                handToFill.put(card.valueToInt(), 1);
            }
        }
    }

    /**
     * Cette methode permet de remplir playableCards afin de savoir qu'elle carte
     * peut jouer le joueur
     * 
     * @param playableCards HashMap<Integer, Integer> le dictionnaire de cartes que
     *                      peut jouer le joueur
     * @param mapHand       Map<Integer, Integer> la map de cartes que le joueur
     *                      possède
     * @param winnerHand    ArrayList<Card> les cartes de la personne qui est en
     *                      train de gagner sur la round
     * @param firstTurn     boolean qui indique si c'est la premiere personne à
     *                      jouer dans la round
     */
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

    /**
     * Cette methode supprime de la main du joueur les cartes qui ont été joué
     * 
     * @param nbDeleteCard int le nombre de carte qui doit etre suprimmer de la main
     *                     du joueur
     * @param hand         ArrayList<Card> la main du joueur
     * @param cardPlay     ArrayList<Card> les cartes qui ont été joué par le joueur
     * @return ArrayList<Card> la main du joueur apres suppression des cartes qui
     *         ont été joué
     */
    protected ArrayList<Card> deleteCardInHand(int nbDeleteCard, ArrayList<Card> hand, ArrayList<Card> cardPlay) {
        if (nbDeleteCard == 0) {
            return hand;
        }
        for (int i = 0; i < nbDeleteCard; i++) {
            for (Card card : hand) {
                if (cardPlay.contains(card)) {
                    hand.remove(card);
                    break;
                }
            }
        }
        return hand;
    }

    @Override
    /**
     * Cette méthode prends les cartes du dernier gagnant et les cartes du joueur et
     * le joueur renvoie une ou plusieurs cartes adéquates
     * 
     * @param winnerHand ArrayList<Card> les cartes du dernier joueur qui est
     *                   entrain de gangner la round
     * @param namePlayer Sting nom du joueur qui doit jouer une carte
     * @return return la, les ou aucune cartes qui seront jouer dans la round
     */
    protected ArrayList<Card> getCardOrGameOver(ArrayList<Card> winnerHand, String namePlayer) {
        boolean premierTour = winnerHand.isEmpty();
        ArrayList<Card> hand = this.playerCards.get(namePlayer);
        Map<Integer, Integer> mapHand = new HashMap<>();
        fillHand(mapHand, hand);
        HashMap<Integer, Integer> playableCards = new HashMap<>();
        fillPlayableCards(playableCards, mapHand, winnerHand, premierTour);
        TreeMap<Integer, Integer> mapPlay = expertSystem(playableCards, winnerHand, premierTour);
        ArrayList<Card> cardPlay = new ArrayList<>();
        int nbDeleteCard = 0;
        int index = 0;
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
        deleteCardInHand(nbDeleteCard, hand, cardPlay);
        return cardPlay;
    }

    /**
     * Cette methode permet de choisir la ou les meilleures cartes possiblent à
     * jouer et les return sous forme d'une map
     * 
     * @param playableCards Map<Integer, Integer> map des cartes qui sont possibles
     *                      à jouer
     * @param winnerHand    ArrayList<Card> les cartes du dernier joueur qui est
     *                      entrain de gangner la round
     * @param premierTour   boolean qui indique si il est le premier à jouer
     * @return TreeMap<Integer, Integer> les cartes qui vont etre jouer sous forme
     *         d'une map
     */
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
        return playCard;
    }

    /**
     * Cette methode permet au joueur quand il est le premier à jouer dans la round
     * de choisir la ou les meilleures cartes possiblent à
     * jouer et les return sous forme d'une map
     * 
     * @param playableCards Map<Integer, Integer> map des cartes qui sont possibles
     *                      à jouer
     * @return TreeMap<Integer, Integer> les cartes qui vont etre jouer sous forme
     *         d'une map
     */
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
    /**
     * Cette methode donne les cartes à un joueurs
     * 
     * @param card   Collection<Card> les cartes qui vont etre donné au joueur
     * @param player String le joueur qui va recevoir les cartes
     */
    protected void giveCardsToPlayer(Collection<Card> card, String player) {
        List<Card> cards = new ArrayList<>(card);
        Collections.shuffle(cards);
        this.playerCards.get(player).addAll(cards);
    }

    /**
     * Cette methode renvoie le nom du joueur qui a la dame de coeur
     * 
     * @return String nom du joueur qui à la dame de coeur
     */
    protected String fetchQofH() {
        String specialPlayer = "";
        for(Map.Entry<String, ArrayList<Card>> playersCards : playerCards.entrySet()){
            for(Card card : playersCards.getValue()){
                if(card.verifQofH()){
                    specialPlayer = playersCards.getKey();
                }
            }
        }
        return specialPlayer;
    }

    /**
     * Cette methode renvoie les cartes d'un joueur
     * 
     * @param playerName String nom du joueur
     * @return ArrayList<Card> les cartes du joueur
     */
    protected ArrayList<Card> getPlayerCards(String playerName) {
        return playerCards.get(playerName);
    }

    /**
     * Cette methode renvoie les cartes d'un joueur sous forme d'un dictionnaire
     * 
     * @param playerName String nom du joueur
     * @return HashMap<Integer, Integer> dictionnaire des cartes du joueur
     */
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
