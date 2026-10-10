import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    static class Card {
        String rank;
        String suit;

        Card(String rank, String suit) {
            this.rank = rank;
            this.suit = suit;
        }

        int getValue() {
            if (rank.equals("A")) {
                return 1;
            }

            if (rank.equals("10") ||
                rank.equals("J") ||
                rank.equals("Q") ||
                rank.equals("K")) {
                return 0;
            }

            return Integer.parseInt(rank);
        }

        boolean isRed() {
            return suit.equals("♥") || suit.equals("♦");
        }

        @Override
        public String toString() {
            return rank + suit;
        }
    }

    private List<Card> deck;

    private final List<Card> playerHand = new ArrayList<>();
    private final List<Card> bankerHand = new ArrayList<>();

    private HBox playerCardsBox;
    private HBox bankerCardsBox;

    private Label playerScoreLabel;
    private Label bankerScoreLabel;
    private Label resultLabel;
    private Label roundLabel;

    private Button hitButton;
    private Button standButton;
    private Button newRoundButton;

    private int roundNumber = 0;
    private boolean roundFinished = false;
    private boolean playerHasDrawn = false;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #071b33, #0d3152);"
        );

        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));

        Label title = new Label("LUCKY 9");
        title.setStyle(
                "-fx-text-fill: #ffd85c;" +
                "-fx-font-size: 34px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("JavaFX Card Game");
        subtitle.setStyle(
                "-fx-text-fill: #d9e7f5;" +
                "-fx-font-size: 14px;"
        );

        roundLabel = new Label("Round 0");
        roundLabel.setStyle(
                "-fx-text-fill: #9fc7e8;" +
                "-fx-font-size: 13px;"
        );

        header.getChildren().addAll(title, subtitle, roundLabel);

        root.setTop(header);

        VBox bankerSection = createHandSection("BANKER");

        bankerCardsBox = new HBox(12);
        bankerCardsBox.setAlignment(Pos.CENTER);

        bankerScoreLabel = new Label("Score: 0");
        bankerScoreLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        bankerSection.getChildren().addAll(
                bankerCardsBox,
                bankerScoreLabel
        );

        VBox playerSection = createHandSection("PLAYER");

        playerCardsBox = new HBox(12);
        playerCardsBox.setAlignment(Pos.CENTER);

        playerScoreLabel = new Label("Score: 0");
        playerScoreLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        playerSection.getChildren().addAll(
                playerCardsBox,
                playerScoreLabel
        );

        VBox gameArea = new VBox(25);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(30));

        gameArea.getChildren().addAll(
                bankerSection,
                playerSection
        );

        root.setCenter(gameArea);

        resultLabel = new Label("Press NEW ROUND to start");
        resultLabel.setWrapText(true);
        resultLabel.setAlignment(Pos.CENTER);
        resultLabel.setStyle(
                "-fx-text-fill: #ffd85c;" +
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;"
        );

        hitButton = createButton("HIT");
        standButton = createButton("STAND");
        newRoundButton = createButton("NEW ROUND");

        hitButton.setOnAction(event -> playerHit());
        standButton.setOnAction(event -> playerStand());
        newRoundButton.setOnAction(event -> startNewRound());

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(
                hitButton,
                standButton,
                newRoundButton
        );

        VBox bottom = new VBox(15);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(20, 20, 30, 20));

        bottom.getChildren().addAll(
                resultLabel,
                buttonBox
        );

        root.setBottom(bottom);

        hitButton.setDisable(true);
        standButton.setDisable(true);

        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("Lucky 9 - JavaFX");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private VBox createHandSection(String titleText) {

        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);

        Label title = new Label(titleText);

        title.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );

        box.getChildren().add(title);

        return box;
    }

    private Button createButton(String text) {

        Button button = new Button(text);

        button.setPrefWidth(135);
        button.setPrefHeight(45);

        button.setStyle(
                "-fx-background-color: #1769aa;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    private void createDeck() {

        deck = new ArrayList<>();

        String[] suits = {
                "♠", "♥", "♦", "♣"
        };

        String[] ranks = {
                "A", "2", "3", "4", "5", "6", "7",
                "8", "9", "10", "J", "Q", "K"
        };

        for (String suit : suits) {

            for (String rank : ranks) {

                deck.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(deck);
    }

    private Card drawCard() {

        if (deck.isEmpty()) {
            createDeck();
        }

        return deck.remove(deck.size() - 1);
    }

    private void startNewRound() {

        roundNumber++;

        roundLabel.setText("Round " + roundNumber);

        playerHand.clear();
        bankerHand.clear();

        createDeck();

        // Deal two cards each
        playerHand.add(drawCard());
        playerHand.add(drawCard());

        bankerHand.add(drawCard());
        bankerHand.add(drawCard());

        playerHasDrawn = false;
        roundFinished = false;

        hitButton.setDisable(false);
        standButton.setDisable(false);

        resultLabel.setText("Your move");

        updateCards();
        updateScores();
    }

    private void playerHit() {

        if (roundFinished) {
            return;
        }

        if (playerHasDrawn) {

            resultLabel.setText(
                    "You can only draw one additional card."
            );

            return;
        }

        playerHand.add(drawCard());

        playerHasDrawn = true;

        updateCards();
        updateScores();

        int playerScore = getScore(playerHand);

        if (playerScore == 9) {

            resultLabel.setText(
                    "Lucky 9! You have a natural 9."
            );

            playerStand();
        }
    }

    private void playerStand() {

        if (roundFinished) {
            return;
        }

        hitButton.setDisable(true);
        standButton.setDisable(true);

        bankerPlay();

        updateCards();
        updateScores();

        determineWinner();

        roundFinished = true;
    }

    private void bankerPlay() {

        int bankerScore = getScore(bankerHand);

        if (bankerScore <= 5 && bankerHand.size() < 3) {

            bankerHand.add(drawCard());
        }
    }

    private int getScore(List<Card> hand) {

        int total = 0;

        for (Card card : hand) {
            total += card.getValue();
        }

        return total % 10;
    }

    private void updateScores() {

        int playerScore = getScore(playerHand);
        int bankerScore = getScore(bankerHand);

        playerScoreLabel.setText(
                "Score: " + playerScore
        );

        bankerScoreLabel.setText(
                "Score: " + bankerScore
        );
    }

    private void updateCards() {

        playerCardsBox.getChildren().clear();
        bankerCardsBox.getChildren().clear();

        for (Card card : playerHand) {

            playerCardsBox.getChildren().add(
                    createCardView(card)
            );
        }

        for (Card card : bankerHand) {

            bankerCardsBox.getChildren().add(
                    createCardView(card)
            );
        }
    }

    private Label createCardView(Card card) {

        Label cardLabel = new Label(
                card.rank + "\n" + card.suit
        );

        cardLabel.setAlignment(Pos.CENTER);

        cardLabel.setPrefWidth(85);
        cardLabel.setPrefHeight(115);

        String textColor;

        if (card.isRed()) {
            textColor = "#d62828";
        } else {
            textColor = "#111111";
        }

        cardLabel.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #d6d6d6;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 10px;" +
                "-fx-background-radius: 10px;" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;"
        );

        return cardLabel;
    }

    private void determineWinner() {

        int playerScore = getScore(playerHand);
        int bankerScore = getScore(bankerHand);

        if (playerScore > bankerScore) {

            resultLabel.setText(
                    "PLAYER WINS!  " +
                    playerScore +
                    " vs " +
                    bankerScore
            );

        } else if (bankerScore > playerScore) {

            resultLabel.setText(
                    "BANKER WINS!  " +
                    bankerScore +
                    " vs " +
                    playerScore
            );

        } else {

            resultLabel.setText(
                    "TIE!  " +
                    playerScore +
                    " vs " +
                    bankerScore
            );
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}