package finalproject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class FrontEnd extends Application {

    private Stage primaryStage;
    private Arena arena;
    private final double ARENA_WIDTH = 800;
    private final double ARENA_HEIGHT = 800;

    private Set<KeyCode> activeKeys = new HashSet<>();
    private AnimationTimer gameLoop;
    
    private ComboBox<String> p1Selector;
    private ComboBox<String> p2Selector;
    private String lastP1Selection = "Warrior";
    private String lastP2Selection = "Archer";
    
    private Image backgroundImage; 

    private static class FighterConfig {
        final int health;
        final double speed;
        final double length;
        final double width;
        final long cooldown;

        public FighterConfig(int health, double speed, double length, double width, long cooldown) {
            this.health = health;
            this.speed = speed;
            this.length = length;
            this.width = width;
            this.cooldown = cooldown;
        }
    }

    private final Map<String, FighterConfig> fighterPresets = new HashMap<>();

    public FrontEnd() {
        fighterPresets.put("Warrior", new FighterConfig(120, 4.0, 50.0, 50.0, 15000));
        fighterPresets.put("Archer", new FighterConfig(100, 6.0, 50.0, 50.0, 15000));
        fighterPresets.put("Mage", new FighterConfig(120, 4.0, 40.0, 40.0, 18000));
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("2D Fighter Arena - Epic Battle");
        showMainMenu();
    }
    
    private void showMainMenu() {
        if (gameLoop != null) gameLoop.stop();
        
        Pane root = new Pane();
        root.setPrefSize(ARENA_WIDTH, ARENA_HEIGHT);
        
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.DARKSLATEBLUE),
            new Stop(0.5, Color.MIDNIGHTBLUE),
            new Stop(1, Color.BLACK)
        );
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
        
        Label title = new Label("EPIC FIGHTER ARENA");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 40));
        title.setTextFill(Color.GOLD);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        
        Label subtitle = new Label("Select Your Champions");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        subtitle.setTextFill(Color.LIGHTGRAY);
        
        Label p1Label = new Label("PLAYER 1");
        p1Label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        p1Label.setTextFill(Color.CYAN);
        
        p1Selector = new ComboBox<>();
        p1Selector.getItems().addAll(fighterPresets.keySet());
        p1Selector.setValue(lastP1Selection);
        p1Selector.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(0,100,200,0.7); -fx-text-fill: white;");
        p1Selector.setPrefWidth(200);
        
        Label p1Controls = new Label("Controls: WASD Move | SPACE Shoot | E Special | 1/2 Switch Weapon");
        p1Controls.setFont(Font.font("Arial", 12));
        p1Controls.setTextFill(Color.LIGHTCYAN);
        
        Label p2Label = new Label("PLAYER 2");
        p2Label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        p2Label.setTextFill(Color.CORAL);
        
        p2Selector = new ComboBox<>();
        p2Selector.getItems().addAll(fighterPresets.keySet());
        p2Selector.setValue(lastP2Selection);
        p2Selector.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(200,50,50,0.7); -fx-text-fill: white;");
        p2Selector.setPrefWidth(200);
        
        Label p2Controls = new Label("Controls: ARROWS Move | ENTER Shoot | SHIFT Special | 7/8 Switch Weapon");
        p2Controls.setFont(Font.font("Arial", 12));
        p2Controls.setTextFill(Color.LIGHTCORAL);
        
        Button startButton = new Button("START BATTLE!");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        startButton.setStyle("-fx-background-color: linear-gradient(to bottom, #ff3333, #990000); " +
                           "-fx-text-fill: white; -fx-padding: 15px 30px; " +
                           "-fx-background-radius: 10; -fx-border-radius: 10; " +
                           "-fx-border-color: gold; -fx-border-width: 2px;");
        startButton.setOnAction(e -> {
            lastP1Selection = p1Selector.getValue();
            lastP2Selection = p2Selector.getValue();
            startGame();
        });
        
        VBox content = new VBox(20);
        content.getChildren().addAll(title, subtitle, 
            new VBox(10, p1Label, p1Selector, p1Controls),
            new VBox(10, p2Label, p2Selector, p2Controls),
            startButton);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        
        root.getChildren().add(content);
        content.setLayoutX(ARENA_WIDTH/2 - 250);
        content.setLayoutY(50);

        Scene menuScene = new Scene(root, ARENA_WIDTH, ARENA_HEIGHT);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    private void startGame() {
        Fighter p1 = createFighter(lastP1Selection, "Player 1");
        Fighter p2 = createFighter(lastP2Selection, "Player 2");

        arena = new Arena(p1, p2);
        
        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/arena_background.png"));
            if (backgroundImage.isError()) {
                throw new Exception("Image error");
            }
        } catch (Exception e) {
            System.err.println("Could not load background image '/arena_background.png'. Using gradient background.");
            backgroundImage = null;
        }

        Canvas canvas = new Canvas(ARENA_WIDTH, ARENA_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, ARENA_WIDTH, ARENA_HEIGHT);

        setupInputHandling(scene);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                renderGame(gc);
                if (arena.isGameOver()) {
                    stop();
                    displayWinner();
                }
            }
        };

        gameLoop.start();
        primaryStage.setScene(scene);
    }
    
    private Fighter createFighter(String type, String name) {
        FighterConfig config = fighterPresets.get(type);
        if (config == null) config = fighterPresets.get("Warrior");
        long t = System.currentTimeMillis();
        switch (type) {
            case "Warrior": return new Warrior(name, config.health, 0, 0, config.speed, t, config.length, config.width, config.cooldown, t);
            case "Archer": return new Archer(name, config.health, 0, 0, config.speed, t, config.length, config.width, config.cooldown, t);
            case "Mage": return new Mage(name, config.health, 0, 0, config.speed, t, config.length, config.width, config.cooldown, t);
            default: return new Warrior(name, config.health, 0, 0, config.speed, t, config.length, config.width, config.cooldown, t);
        }
    }

    private void setupInputHandling(Scene scene) {
        activeKeys.clear();
        
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            
            if (!activeKeys.contains(code)) {
                activeKeys.add(code);

                
                if (code == KeyCode.SPACE) arena.playerShoot(arena.getPlayer1());
                if (code == KeyCode.E) arena.getPlayer1().useSpecialAbility();
                if (code == KeyCode.DIGIT1) arena.getPlayer1().switchWeapon(0);
                if (code == KeyCode.DIGIT2) arena.getPlayer1().switchWeapon(1);
                
                if (code == KeyCode.ENTER) arena.playerShoot(arena.getPlayer2());
                if (code == KeyCode.SHIFT) arena.getPlayer2().useSpecialAbility();
                if (code == KeyCode.DIGIT7) arena.getPlayer2().switchWeapon(0);
                if (code == KeyCode.DIGIT8) arena.getPlayer2().switchWeapon(1);
            }
        });

        scene.setOnKeyReleased(event -> {
            activeKeys.remove(event.getCode());
        });
    }

    private void updateGame() {
        Fighter p1 = arena.getPlayer1();
        Fighter p2 = arena.getPlayer2();
        
        if (activeKeys.contains(KeyCode.W)) p1.moveUp(0);
        if (activeKeys.contains(KeyCode.S)) p1.moveDown(ARENA_HEIGHT);
        if (activeKeys.contains(KeyCode.A)) p1.moveLeft(0);
        if (activeKeys.contains(KeyCode.D)) p1.moveRight(ARENA_WIDTH);
        
        if (activeKeys.contains(KeyCode.UP)) p2.moveUp(0);
        if (activeKeys.contains(KeyCode.DOWN)) p2.moveDown(ARENA_HEIGHT);
        if (activeKeys.contains(KeyCode.LEFT)) p2.moveLeft(0);
        if (activeKeys.contains(KeyCode.RIGHT)) p2.moveRight(ARENA_WIDTH);

        arena.enforceBounds(p1);
        arena.enforceBounds(p2);
        arena.update();
    }

    private void renderGame(GraphicsContext gc) {
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, ARENA_WIDTH, ARENA_HEIGHT);
        } else {
            RadialGradient gradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.8, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.DARKSLATEGRAY),
                new Stop(0.3, Color.DARKBLUE),
                new Stop(0.6, Color.MIDNIGHTBLUE),
                new Stop(1, Color.BLACK)
            );
            gc.setFill(gradient);
            gc.fillRect(0, 0, ARENA_WIDTH, ARENA_HEIGHT);
            
            gc.setFill(Color.rgb(30, 20, 10, 0.8));
            gc.fillRect(0, ARENA_HEIGHT - 60, ARENA_WIDTH, 60);
            
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            for (int i = 0; i < ARENA_WIDTH; i += 40) {
                gc.strokeLine(i, ARENA_HEIGHT - 60, i + 20, ARENA_HEIGHT - 40);
            }
        }
       
        gc.setStroke(Color.RED);
        gc.setLineWidth(3);
        gc.strokeLine(ARENA_WIDTH / 2.0, 0, ARENA_WIDTH / 2.0, ARENA_HEIGHT);
        
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(1);
        gc.strokeLine(ARENA_WIDTH / 2.0, 0, ARENA_WIDTH / 2.0, ARENA_HEIGHT);

        drawFighter(gc, arena.getPlayer1(), Color.CYAN);
        drawFighter(gc, arena.getPlayer2(), Color.ORANGERED);

        for (Projectile p : arena.getProjectiles()) {
            gc.setFill(Color.YELLOW);
            gc.fillOval(p.getX() - 2, p.getY() - 2, 12, 12);
            
            gc.setFill(Color.WHITE);
            gc.fillOval(p.getX(), p.getY(), 8, 8);
            
            gc.setStroke(Color.YELLOW.brighter());
            gc.setLineWidth(2);
            double trailX = p.getX() - p.getDirectionX() * 10;
            gc.strokeLine(trailX, p.getY() + 4, p.getX(), p.getY() + 4);
        }

        drawHUD(gc, arena.getPlayer1(), 10, 10);
        drawHUD(gc, arena.getPlayer2(), ARENA_WIDTH - 250, 10);
    }
    
    private void drawFighter(GraphicsContext gc, Fighter f, Color color) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(f.getX() + 3, f.getY() + 3, f.getWidth(), f.getLength());
        
        LinearGradient fighterGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, color.brighter()),
            new Stop(0.7, color),
            new Stop(1, color.darker())
        );
        gc.setFill(fighterGradient);
        gc.fillRoundRect(f.getX(), f.getY(), f.getWidth(), f.getLength(), 10, 10);
        
        gc.setStroke(color.brighter().brighter());
        gc.setLineWidth(2);
        gc.strokeRoundRect(f.getX(), f.getY(), f.getWidth(), f.getLength(), 10, 10);
        
        double healthPercent = f.getHealth() / 100.0;
        gc.setFill(healthPercent > 0.5 ? Color.LIMEGREEN : 
                  healthPercent > 0.25 ? Color.YELLOW : Color.RED);
        gc.fillRect(f.getX(), f.getY() - 5, f.getWidth() * healthPercent, 3);
        
        gc.setFill(Color.WHITE);
        double indicatorX = (f.getFacingDirection() > 0) ? 
            f.getX() + f.getWidth() - 8 : f.getX() + 3;
        gc.fillRect(indicatorX, f.getY() + f.getLength() / 2.0 - 5, 5, 10);
    }

    private void drawHUD(GraphicsContext gc, Fighter f, double x, double y) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRoundRect(x - 5, y - 5, 240, 80, 10, 10);
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x - 5, y - 5, 240, 80, 10, 10);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText(f.getName(), x, y + 20);
        
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y + 25, 200, 15);
        
        double healthPercent = Math.max(0, f.getHealth()) / 100.0;
        Color healthColor = healthPercent > 0.5 ? Color.LIMEGREEN : 
                           healthPercent > 0.25 ? Color.ORANGE : Color.RED;
        gc.setFill(healthColor);
        gc.fillRect(x, y + 25, 200 * healthPercent, 15);
        
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y + 25, 200, 15);
        
        gc.setFill(Color.WHITE);
        gc.fillText(f.getHealth() + " HP", x + 205, y + 38);
        
        String weaponInfo = String.format("%s (%d/%d)", 
            f.getWeapon().getName(), f.getCurrentWeaponIndex() + 1, f.getInventory().size());
        gc.fillText("Weapon: " + weaponInfo, x, y + 55);
        
        long timeSinceSpecial = System.currentTimeMillis() - f.getLastSpecialUseTime();
        boolean ready = timeSinceSpecial >= f.getSpecialCooldownTime();
        String specStatus = ready ? "READY" : 
            String.format("%.1fs", (f.getSpecialCooldownTime() - timeSinceSpecial)/1000.0);
        
        gc.setFill(ready ? Color.LIMEGREEN : Color.RED);
        gc.fillText("Special: " + specStatus, x, y + 70);
        
        if (ready) {
            gc.setFill(Color.GOLD);
            gc.fillOval(x + 70, y + 58, 8, 8);
        }
    }

    private void displayWinner() {
        Fighter p1 = arena.getPlayer1();
        String winner = p1.isDead() ? arena.getPlayer2().getName() : p1.getName();
        
        Pane victoryRoot = new Pane();
        victoryRoot.setPrefSize(ARENA_WIDTH, ARENA_HEIGHT);
        
        RadialGradient victoryGradient = new RadialGradient(
            0, 0, 0.5, 0.5, 0.8, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.GOLD),
            new Stop(0.3, Color.ORANGE),
            new Stop(0.6, Color.DARKRED),
            new Stop(1, Color.BLACK)
        );
        victoryRoot.setBackground(new Background(new BackgroundFill(victoryGradient, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
        
        Label winText = new Label(winner + " WINS!");
        winText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 60));
        winText.setTextFill(Color.GOLD);
        winText.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 30, 0, 0, 0);");
        
        Label victoryMessage = new Label("The Arena Has A New Champion!");
        victoryMessage.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        victoryMessage.setTextFill(Color.WHITE);
        
        Button playAgain = new Button("FIGHT AGAIN");
        playAgain.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playAgain.setStyle("-fx-background-color: linear-gradient(to bottom, #00cc00, #006600); " +
                         "-fx-text-fill: white; -fx-padding: 10px 20px; " +
                         "-fx-background-radius: 5;");
        playAgain.setOnAction(e -> startGame());
        
        Button backToMenu = new Button("MAIN MENU");
        backToMenu.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        backToMenu.setStyle("-fx-background-color: linear-gradient(to bottom, #0066cc, #003366); " +
                          "-fx-text-fill: white; -fx-padding: 10px 20px; " +
                          "-fx-background-radius: 5;");
        backToMenu.setOnAction(e -> showMainMenu());
        
        Button close = new Button("EXIT ARENA");
        close.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        close.setStyle("-fx-background-color: linear-gradient(to bottom, #cc0000, #660000); " +
                     "-fx-text-fill: white; -fx-padding: 10px 20px; " +
                     "-fx-background-radius: 5;");
        close.setOnAction(e -> primaryStage.close());
        
        HBox buttons = new HBox(20, playAgain, backToMenu, close);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        
        VBox victoryContent = new VBox(40, winText, victoryMessage, buttons);
        victoryContent.setAlignment(javafx.geometry.Pos.CENTER);
        victoryContent.setPrefSize(ARENA_WIDTH, ARENA_HEIGHT);
        
        victoryRoot.getChildren().add(victoryContent);
        
        Scene finishScene = new Scene(victoryRoot, ARENA_WIDTH, ARENA_HEIGHT);
        primaryStage.setScene(finishScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}