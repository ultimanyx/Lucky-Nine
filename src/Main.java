import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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

    public List<Card> deck;

    // Multiplayer game state
    public int numPlayers = 2; // 2 or 3 players
    public int activePlayerIndex = 0; // 0 = P1, 1 = P2, 2 = P3
    public final List<List<Card>> playerHands = new ArrayList<>();
    public final List<HBox> playerCardsBoxes = new ArrayList<>();
    public final List<Label> playerScoreLabels = new ArrayList<>();
    public final List<Boolean> playerHasDrawn = new ArrayList<>();

    public final List<Card> bankerHand = new ArrayList<>();
    public HBox bankerCardsBox;
    public Label bankerScoreLabel;

    public Label resultLabel;
    public Label roundLabel;
    public Label turnIndicatorLabel;

    public Button hitButton;
    public Button standButton;
    public Button newRoundButton;
    public Button menuButton;

    public int roundNumber = 0;
    public boolean roundFinished = false;

    // View management
    public StackPane rootContainer;
    public VBox mainMenuPane;
    public VBox modeSelectPane;
    public VBox settingsPane;
    public BorderPane gamePane;

    // Background music player
    public MediaPlayer backgroundMusic;
    public boolean musicOn = false;

    // Card Back Color Customization Settings (Default: Gold)
    public String cardBgColor = "#0b2545";
    public String cardBorderColor = "#ffd85c";
    public String cardTextColor = "#ffd85c";

    // UI elements inside Settings Pane to show current states
    private Button musicToggleBtn;
    private Label currentCardColorLabel;

    @Override
    public void start(Stage stage) {
        rootContainer = new StackPane();
        rootContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #071b33, #0d3152);");

        initMusic();
        buildMainMenu();
        buildModeSelectMenu();
        buildSettingsMenu();

        // Show Main Menu on start
        rootContainer.getChildren().add(mainMenuPane);

        Scene scene = new Scene(rootContainer, 1000, 700);

        stage.setTitle("Lucky 9 - Multiplayer JavaFX");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }

    //____________________________________________________________ INITIALIZE BACKGROUND MUSIC _________________________________________________
    public void initMusic() {
        try {
            // Loads the music file from your project resources relative to the Main class
            String musicFile = getClass().getResource("/assets/Music/background.mp3").toExternalForm();
            Media sound = new Media(musicFile);
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.setVolume(0.3);
        } catch (Throwable e) {
            // This will now catch missing files or unsupported formats gracefully
            backgroundMusic = null;
            System.out.println("Could not load music file: " + e.getMessage());
        }
    }
    //____________________________________________________________ CLOSED _________________________________________________

    public void buildMainMenu() {
        mainMenuPane = new VBox(20);
        mainMenuPane.setAlignment(Pos.CENTER);
        mainMenuPane.setPadding(new Insets(40));
        mainMenuPane.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #071b33, #0d3152);");

        Label title = new Label("LUCKY 9");
        title.setStyle(
                "-fx-text-fill: #ffd85c;" +
                        "-fx-font-size: 48px;" +
                        "-fx-font-weight: bold;");

        Label subtitle = new Label("Multiplayer JavaFX Card Game");
        subtitle.setStyle(
                "-fx-text-fill: #d9e7f5;" +
                        "-fx-font-size: 16px;");

        Button playBtn = createMenuButton("Play");
        Button howToPlayBtn = createMenuButton("How to Play");
        Button settingBtn = createMenuButton("Setting");
        Button quitBtn = createMenuButton("Quit");

        playBtn.setOnAction(e -> rootContainer.getChildren().setAll(modeSelectPane));
        howToPlayBtn.setOnAction(e -> showHowToPlayDialog());
        settingBtn.setOnAction(e -> rootContainer.getChildren().setAll(settingsPane));
        quitBtn.setOnAction(e -> Platform.exit());

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playBtn, howToPlayBtn, settingBtn, quitBtn);

        Label creditsLabel = new Label(
                "Credited by:\n" +
                        "• GEMILLA\n" +
                        "• ELLEVERA\n" +
                        "• MACAHILOS\n" +
                        "• DOMO. J\n" +
                        "• CAMOCAMO\n" +
                        "• CAGAMPANG\n" +
                        "\n\nBSIT-2A\n" +
                        "Multiplayer Lucky 9 JavaFX Project");
        creditsLabel.setStyle(
                "-fx-text-fill: #8faec6;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-style: italic;");

        BorderPane menuLayout = new BorderPane();
        menuLayout.setCenter(new VBox(30, title, subtitle, buttonBox));
        ((VBox) menuLayout.getCenter()).setAlignment(Pos.CENTER);

        HBox bottomBox = new HBox(creditsLabel);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        menuLayout.setBottom(bottomBox);

        mainMenuPane.getChildren().add(menuLayout);
    }

    public void buildSettingsMenu() {
        settingsPane = new VBox(20);
        settingsPane.setAlignment(Pos.CENTER);
        settingsPane.setPadding(new Insets(40));
        settingsPane.setStyle("-fx-background-color: linear-gradient(to bottom, #071b33, #0d3152);");

        Label title = new Label("SETTINGS");
        title.setStyle("-fx-text-fill: #ffd85c; -fx-font-size: 36px; -fx-font-weight: bold;");

        // --- Audio Settings ---
        Label musicLabel = new Label("Background Music:");
        musicLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        musicToggleBtn = createMenuButton("Music: OFF");
        musicToggleBtn.setOnAction(e -> toggleMusic());

        // --- Card Color Settings ---
        Label cardColorLabel = new Label("Folded Card Color:");
        cardColorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        currentCardColorLabel = new Label("Selected: Gold (Default)");
        currentCardColorLabel.setStyle("-fx-text-fill: #ffd85c; -fx-font-size: 14px; -fx-font-style: italic;");

        HBox colorButtonsBox = new HBox(12);
        colorButtonsBox.setAlignment(Pos.CENTER);

        Button yellowBtn = createColorOptionButton("Gold / Yellow", "#ffd85c", "#0b2545");
        Button blueBtn = createColorOptionButton("Blue", "#3391ff", "#102a43");
        Button redBtn = createColorOptionButton("Red", "#ff4d4d", "#4a0e17");
        Button violetBtn = createColorOptionButton("Violet", "#b142f5", "#2c103b");

        yellowBtn.setOnAction(e -> setCardTheme("Gold (Default)", "#0b2545", "#ffd85c", "#ffd85c"));
        blueBtn.setOnAction(e -> setCardTheme("Blue", "#102a43", "#3391ff", "#3391ff"));
        redBtn.setOnAction(e -> setCardTheme("Red", "#4a0e17", "#ff4d4d", "#ff4d4d"));
        violetBtn.setOnAction(e -> setCardTheme("Violet", "#2c103b", "#b142f5", "#b142f5"));

        colorButtonsBox.getChildren().addAll(yellowBtn, blueBtn, redBtn, violetBtn);

        // --- Back Button ---
        Button backBtn = createMenuButton("Back to Menu");
        backBtn.setOnAction(e -> rootContainer.getChildren().setAll(mainMenuPane));

        VBox settingsContainer = new VBox(15);
        settingsContainer.setAlignment(Pos.CENTER);
        settingsContainer.getChildren().addAll(
                musicLabel, musicToggleBtn,
                cardColorLabel, currentCardColorLabel, colorButtonsBox,
                new Label(""), // spacing
                backBtn
        );

        settingsPane.getChildren().addAll(title, settingsContainer);
    }

    private Button createColorOptionButton(String text, String accentColor, String bgColor) {
        Button button = new Button(text);
        button.setPrefWidth(120);
        button.setPrefHeight(40);
        button.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-border-color: " + accentColor + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: " + accentColor + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private void toggleMusic() {
        if (backgroundMusic == null) {
            musicToggleBtn.setText("Music: Unavailable");
            return;
        }
        musicOn = !musicOn;
        if (musicOn) {
            backgroundMusic.play();
            musicToggleBtn.setText("Music: ON");
        } else {
            backgroundMusic.pause();
            musicToggleBtn.setText("Music: OFF");
        }
    }

    private void setCardTheme(String name, String bgColor, String borderColor, String textColor) {
        this.cardBgColor = bgColor;
        this.cardBorderColor = borderColor;
        this.cardTextColor = textColor;
        currentCardColorLabel.setText("Selected: " + name);
        currentCardColorLabel.setStyle("-fx-text-fill: " + borderColor + "; -fx-font-size: 14px; -fx-font-style: italic;");
        
        // Update game board view if game screen is loaded
        if (gamePane != null && rootContainer.getChildren().contains(gamePane)) {
            updateUI();
        }
    }

    public void buildModeSelectMenu() {
        modeSelectPane = new VBox(20);
        modeSelectPane.setAlignment(Pos.CENTER);
        modeSelectPane.setPadding(new Insets(40));
        modeSelectPane.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #071b33, #0d3152);");

        Label title = new Label("SELECT GAME MODE");
        title.setStyle(
                "-fx-text-fill: #ffd85c;" +
                        "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;");

        Button twoPlayerBtn = createMenuButton("2 Players vs Banker");
        Button threePlayerBtn = createMenuButton("3 Players vs Banker");
        Button backBtn = createMenuButton("Back to Menu");

        twoPlayerBtn.setOnAction(e -> {
            numPlayers = 2;
            startGame();
        });

        threePlayerBtn.setOnAction(e -> {
            numPlayers = 3;
            startGame();
        });

        backBtn.setOnAction(e -> rootContainer.getChildren().setAll(mainMenuPane));

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(twoPlayerBtn, threePlayerBtn, backBtn);

        modeSelectPane.getChildren().addAll(title, buttonBox);
    }

    public void startGame() {
        buildGameView();
        rootContainer.getChildren().setAll(gamePane);
        startNewRound();
    }

    public Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setPrefHeight(50);
        button.setStyle(
                "-fx-background-color: #1769aa;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-cursor: hand;");
        return button;
    }

    public void showHowToPlayDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How to Play - Lucky 9");
        alert.setHeaderText("Multiplayer Lucky 9 Rules");
        alert.setContentText(
                "1. Each player and the banker receive 2 cards to start.\n" +
                        "2. Card Values: Ace = 1, 2-9 = Face Value, 10/J/Q/K = 0.\n" +
                        "3. Score is the rightmost digit of the sum (modulo 10).\n" +
                        "4. Players take turns sequentially to HIT or STAND.\n" +
                        "5. All cards and scores remain hidden until every player finishes.\n" +
                        "6. After all players move, the Banker plays, and results are revealed!");
        alert.showAndWait();
    }

    public void buildGameView() {
        gamePane = new BorderPane();
        gamePane.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #071b33, #0d3152);");

        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));

        Label title = new Label("LUCKY 9 MULTIPLAYER");
        title.setStyle(
                "-fx-text-fill: #ffd85c;" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;");

        roundLabel = new Label("Round 0");
        roundLabel.setStyle(
                "-fx-text-fill: #9fc7e8;" +
                        "-fx-font-size: 13px;");

        turnIndicatorLabel = new Label("Turn Indicator");
        turnIndicatorLabel.setStyle(
                "-fx-text-fill: #ffb703;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;");

        header.getChildren().addAll(title, roundLabel, turnIndicatorLabel);
        gamePane.setTop(header);

        BorderPane tableLayout = new BorderPane();
        tableLayout.setPadding(new Insets(10));

        // Banker Section (Top)
        VBox bankerSection = createHandSection("BANKER");
        bankerCardsBox = new HBox(10);
        bankerCardsBox.setAlignment(Pos.CENTER);
        bankerScoreLabel = new Label("Score: ?");
        bankerScoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        bankerSection.getChildren().addAll(bankerCardsBox, bankerScoreLabel);
        tableLayout.setTop(bankerSection);
        BorderPane.setAlignment(bankerSection, Pos.CENTER);

        // Player Sections Initialization
        playerHands.clear();
        playerCardsBoxes.clear();
        playerScoreLabels.clear();
        playerHasDrawn.clear();

        for (int i = 0; i < numPlayers; i++) {
            List<Card> hand = new ArrayList<>();
            playerHands.add(hand);
            playerHasDrawn.add(false);
        }

        // Player 1 (Left Side)
        if (numPlayers >= 1) {
            VBox p1Section = createHandSection("PLAYER 1");
            HBox p1CardsBox = new HBox(10);
            p1CardsBox.setAlignment(Pos.CENTER);
            playerCardsBoxes.add(p1CardsBox);

            Label p1ScoreLabel = new Label("Score: ? (Hidden)");
            p1ScoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            playerScoreLabels.add(p1ScoreLabel);

            p1Section.getChildren().addAll(p1CardsBox, p1ScoreLabel);
            tableLayout.setLeft(p1Section);
            BorderPane.setAlignment(p1Section, Pos.CENTER);
            BorderPane.setMargin(p1Section, new Insets(0, 30, 0, 450));
        }

        // Player 2 (Right Side)
        if (numPlayers >= 2) {
            VBox p2Section = createHandSection("PLAYER 2");
            HBox p2CardsBox = new HBox(10);
            p2CardsBox.setAlignment(Pos.CENTER);
            playerCardsBoxes.add(p2CardsBox);

            Label p2ScoreLabel = new Label("Score: ? (Hidden)");
            p2ScoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            playerScoreLabels.add(p2ScoreLabel);

            p2Section.getChildren().addAll(p2CardsBox, p2ScoreLabel);
            tableLayout.setRight(p2Section);
            BorderPane.setAlignment(p2Section, Pos.CENTER);
            BorderPane.setMargin(p2Section, new Insets(0, 450, 0, 30));
        }

        // Player 3 (Bottom)
        if (numPlayers == 3) {
            VBox p3Section = createHandSection("PLAYER 3");
            HBox p3CardsBox = new HBox(10);
            p3CardsBox.setAlignment(Pos.CENTER);
            playerCardsBoxes.add(p3CardsBox);

            Label p3ScoreLabel = new Label("Score: ? (Hidden)");
            p3ScoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            playerScoreLabels.add(p3ScoreLabel);

            p3Section.getChildren().addAll(p3CardsBox, p3ScoreLabel);
            tableLayout.setBottom(p3Section);
            BorderPane.setAlignment(p3Section, Pos.CENTER);
            BorderPane.setMargin(p3Section, new Insets(10, 0, 0, 0));
        }

        gamePane.setCenter(tableLayout);

        // Bottom Controls
        resultLabel = new Label("Press NEW ROUND to start");
        resultLabel.setWrapText(true);
        resultLabel.setAlignment(Pos.CENTER);
        resultLabel.setStyle(
                "-fx-text-fill: #ffd85c;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;");

        hitButton = createButton("HIT");
        standButton = createButton("STAND");
        newRoundButton = createButton("NEW ROUND");
        menuButton = createButton("MENU");

        hitButton.setOnAction(event -> playerHit());
        standButton.setOnAction(event -> playerStand());
        newRoundButton.setOnAction(event -> startNewRound());
        menuButton.setOnAction(event -> rootContainer.getChildren().setAll(mainMenuPane));

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(
                hitButton,
                standButton,
                newRoundButton,
                menuButton);

        VBox bottom = new VBox(10);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(15, 20, 20, 20));
        bottom.getChildren().addAll(resultLabel, buttonBox);

        gamePane.setBottom(bottom);

        hitButton.setDisable(true);
        standButton.setDisable(true);
    }

    public VBox createHandSection(String titleText) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;");

        box.getChildren().add(title);
        return box;
    }

    public Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(120);
        button.setPrefHeight(40);
        button.setStyle(
                "-fx-background-color: #1769aa;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;");
        return button;
    }

    public void createDeck() {
        deck = new ArrayList<>();
        String[] suits = { "♠", "♥", "♦", "♣" };
        String[] ranks = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck);
    }

    public Card drawCard() {
        if (deck.isEmpty()) {
            createDeck();
        }
        return deck.remove(deck.size() - 1);
    }

    public void startNewRound() {
        roundNumber++;
        roundLabel.setText("Round " + roundNumber);

        createDeck();
        bankerHand.clear();
        bankerHand.add(drawCard());
        bankerHand.add(drawCard());

        for (int i = 0; i < numPlayers; i++) {
            playerHands.get(i).clear();
            playerHands.get(i).add(drawCard());
            playerHands.get(i).add(drawCard());
            playerHasDrawn.set(i, false);
        }

        activePlayerIndex = 0;
        roundFinished = false;

        hitButton.setDisable(false);
        standButton.setDisable(false);

        resultLabel.setText("Player 1's Turn: Choose Hit or Stand");
        updateUI();
    }

    public void playerHit() {
        if (roundFinished)
            return;

        if (playerHasDrawn.get(activePlayerIndex)) {
            resultLabel.setText("Player " + (activePlayerIndex + 1) + " can only draw one additional card.");
            return;
        }

        playerHands.get(activePlayerIndex).add(drawCard());
        playerHasDrawn.set(activePlayerIndex, true);

        updateUI();

        int score = getScore(playerHands.get(activePlayerIndex));
        if (score == 9) {
            resultLabel.setText("Natural 9 for Player " + (activePlayerIndex + 1) + "!");
            playerStand();
        }
    }

    public void playerStand() {
        if (roundFinished)
            return;

        activePlayerIndex++;

        if (activePlayerIndex < numPlayers) {
            resultLabel.setText("Player " + (activePlayerIndex + 1) + "'s Turn");
            updateUI();
        } else {
            bankerPlay();
            roundFinished = true;

            hitButton.setDisable(true);
            standButton.setDisable(true);

            updateUI();
            determineWinners();
        }
    }

    public void bankerPlay() {
        int bankerScore = getScore(bankerHand);
        if (bankerScore <= 5 && bankerHand.size() < 3) {
            bankerHand.add(drawCard());
        }
    }

    public int getScore(List<Card> hand) {
        int total = 0;
        for (Card card : hand) {
            total += card.getValue();
        }
        return total % 10;
    }

    public void updateUI() {
        if (!roundFinished) {
            turnIndicatorLabel.setText("▶ TURN: PLAYER " + (activePlayerIndex + 1));
        } else {
            turnIndicatorLabel.setText("✔ ROUND OVER - SHOWDOWN");
        }

        bankerCardsBox.getChildren().clear();
        for (Card card : bankerHand) {
            bankerCardsBox.getChildren().add(createCardView(card, !roundFinished));
        }
        if (roundFinished) {
            bankerScoreLabel.setText("Score: " + getScore(bankerHand));
        } else {
            bankerScoreLabel.setText("Score: ?");
        }

        for (int i = 0; i < numPlayers; i++) {
            playerCardsBoxes.get(i).getChildren().clear();
            List<Card> hand = playerHands.get(i);

            boolean showCards = roundFinished;

            for (Card card : hand) {
                playerCardsBoxes.get(i).getChildren().add(createCardView(card, !showCards));
            }

            if (showCards) {
                playerScoreLabels.get(i).setText("Score: " + getScore(hand));
            } else {
                playerScoreLabels.get(i).setText("Score: ? (Hidden)");
            }
        }
    }

    public Label createCardView(Card card, boolean hidden) {
        Label cardLabel = new Label();
        cardLabel.setAlignment(Pos.CENTER);
        cardLabel.setPrefWidth(75);
        cardLabel.setPrefHeight(105);

        if (hidden) {
            cardLabel.setText("LUCKY\n9");
            cardLabel.setStyle(
                    "-fx-background-color: " + cardBgColor + ";" +
                            "-fx-border-color: " + cardBorderColor + ";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 8px;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-text-fill: " + cardTextColor + ";" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;");
        } else {
            cardLabel.setText(card.rank + "\n" + card.suit);
            String textColor = card.isRed() ? "#d62828" : "#111111";

            cardLabel.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #d6d6d6;" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 8px;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-text-fill: " + textColor + ";" +
                            "-fx-font-size: 22px;" +
                            "-fx-font-weight: bold;");
        }

        return cardLabel;
    }

    public void determineWinners() {
        int bankerScore = getScore(bankerHand);
        StringBuilder sb = new StringBuilder("RESULTS:\n");

        for (int i = 0; i < numPlayers; i++) {
            int pScore = getScore(playerHands.get(i));
            sb.append("Player ").append(i + 1).append(" (").append(pScore).append(") vs Banker (").append(bankerScore)
                    .append("): ");

            if (pScore > bankerScore) {
                sb.append("WIN! ");
            } else if (bankerScore > pScore) {
                sb.append("LOSE! ");
            } else {
                sb.append("TIE! ");
            }
            if (i < numPlayers - 1)
                sb.append(" | ");
        }

        resultLabel.setText(sb.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}