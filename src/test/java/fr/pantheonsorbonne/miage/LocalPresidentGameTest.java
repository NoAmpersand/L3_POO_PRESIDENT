package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.game.Card;
import org.junit.jupiter.api.Test;
import java.util.*;

public class LocalPresidentGameTest {

    TreeMap<String, ArrayList<Card>> winnerTemp;
    String namePlayer;

    @Test
    public static void depotCarteValide(){
        TreeMap<String, ArrayList<Card>> winnerTemp = new TreeMap<>();
        winnerTemp.put("Joueur 2", null);
        String namePlayer = "Joueur 1";
        System.out.println(namePlayer);
        //LocalPresidentGame.getCardOrGameOver(winnerTemp, namePlayer);
    }

    public static void main(String... args) {
        depotCarteValide();
    }

}
