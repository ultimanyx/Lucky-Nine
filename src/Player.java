import java.util.ArrayList;

public class Player {

    private String name;
    private ArrayList<Card> cards;

    public Player(String name) {
        this.name = name;
        cards = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int getScore() {

        int total = 0;

        for (Card card : cards) {
            total += card.getValue();
        }

        return total % 10;
    }

    public void clearCards() {
        cards.clear();
    }
}