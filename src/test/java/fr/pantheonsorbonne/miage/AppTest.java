package fr.pantheonsorbonne.miage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;
import fr.pantheonsorbonne.miage.game.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void fetchQofH(){
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
}
