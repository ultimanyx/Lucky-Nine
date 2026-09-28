import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> cards;

    private String[] suits = {
        "♥", "♦", "♣", "♠"
    };

    private String[] ranks = {
        "A", "2", "3", "4", "5", "6", "7",
        "8", "9", "10", "J", "Q", "K"
    };

    public Deck() {
        createDeck();
    }

    private void createDeck() {

        cards = new ArrayList<>();

        for (String suit : suits) {

            for (String rank : ranks) {

                cards.add(new Card(suit, rank));
            }
        }

        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {

        if (cards.isEmpty()) {
            createDeck();
        }

        return cards.remove(cards.size() - 1);
    }

    public int size() {
        return cards.size();
    }
}