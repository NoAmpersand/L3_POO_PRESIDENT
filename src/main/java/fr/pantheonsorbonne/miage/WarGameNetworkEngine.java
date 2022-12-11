package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

import java.util.*;

/*public class WarGameNetworkEngine extends PresidentGameEngine {
    private static final int PLAYER_COUNT = 4;

    private final HostFacade hostFacade;
    private final Set<String> players;
    private final Game president;

    public WarGameNetworkEngine(HostFacade hostFacade, Set<String> players, fr.pantheonsorbonne.miage.model.Game president) {
        this.hostFacade = hostFacade;
        this.players = players;
        this.president = president;
    }

    public static void main(String[] args) {
        //create the host facade
        HostFacade hostFacade = Facade.getFacade();
        hostFacade.waitReady();

        //set the name of the player
        hostFacade.createNewPlayer("Host");

        //create a new game of war
        fr.pantheonsorbonne.miage.model.Game war = hostFacade.createNewGame("WAR");

        //wait for enough players to join
        hostFacade.waitForExtraPlayerCount(PLAYER_COUNT);

        PresidentGameEngine host = new WarGameNetworkEngine(hostFacade, war.getPlayers(), war);
        host.play();


    }


    @Override
    protected Set<String> getInitialPlayers() {
        return this.president.getPlayers();
    }


    @Override
    protected void giveCardsToPlayer(String playerName, String hand) {
        hostFacade.sendGameCommandToPlayer(president, playerName, new GameCommand("cardsForYou", hand));
    }


    @Override
    protected void declareWinner(String winner) {
        hostFacade.sendGameCommandToPlayer(president, winner, new GameCommand("gameOver", "win"));
    }

    @Override
    protected ArrayList<Card> getCardOrGameOver(ArrayList<Card> winnerHand, String namePlayer) {
        return null;
    }

    @Override
    protected Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer, String cardProviderPlayerOpponent) {

        try {
            return getCardFromPlayer(cardProviderPlayer);
        } catch (NoMoreCardException nmc) {
            //contestant A is out of cards
            //we send him a gameover
            hostFacade.sendGameCommandToPlayer(president, cardProviderPlayer, new GameCommand("gameOver"));
            //remove him from the queue so he won't play again
            players.remove(cardProviderPlayer);
            //give back all the cards for this round to the second players
            hostFacade.sendGameCommandToPlayer(president, cardProviderPlayerOpponent, new GameCommand("cardsForYou", Card.cardsToString(leftOverCard.toArray(new Card[leftOverCard.size()]))));
            return null;
        }

    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String winner) {
        List<Card> cards = new ArrayList<>();
        cards.addAll(roundStack);
        //shuffle the round deck so we are not stuck
        Collections.shuffle(cards);
        hostFacade.sendGameCommandToPlayer(president, winner, new GameCommand("cardsForYou", Card.cardsToString(cards.toArray(new Card[cards.size()]))));
    }

    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException {
        hostFacade.sendGameCommandToPlayer(president, player, new GameCommand("playACard"));
        GameCommand expectedCard = hostFacade.receiveGameCommand(president);
        if (expectedCard.name().equals("card")) {
            return Card.valueOf(expectedCard.body());
        }
        if (expectedCard.name().equals("outOfCard")) {
            throw new NoMoreCardException();
        }
        //should not happen!
        throw new RuntimeException("invalid state");

    }

    @Override
    protected HashMap<Integer, Integer> getPlayerMapCard(String playerName) {
        return null;
    }

}*/
