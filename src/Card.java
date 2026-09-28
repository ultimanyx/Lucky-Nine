public class Card {

    private String suit;
    private String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getValue() {

        if (rank.equals("A")) {
            return 1;
        }

        if (rank.equals("J") ||
            rank.equals("Q") ||
            rank.equals("K")) {
            return 0;
        }

        return Integer.parseInt(rank);
    }

    public String getImagePath() {

        return "/assets"
                + suit
                + "_"
                + rank
                + ".png";
    }

    @Override
    public String toString() {
        return rank + suit;
    }
}