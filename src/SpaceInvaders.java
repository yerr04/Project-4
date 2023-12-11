import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.Timer;

public class SpaceInvaders {

    // game states
    enum GameState {
        START, PLAY, END
    }
    // start menu
    static GameObject startMenu;
    // game over menu
    static GameObject gameOverMenu;
    // background
    static GameObject background;
    // End menu
    static GameObject endMenu;
    // player ship and enemeies
    static GameObject player;
    static ArrayList<GameObject> livesIcons = new ArrayList<>();
    static boolean playerIsInvincible = false;
    static ArrayList<GameObject> enemies = new ArrayList<>();
    // bullets
    static GameObject bullet;
    static GameObject enemyBullet;
    static ArrayList<GameObject> enemyBullets = new ArrayList<>();
    static ArrayList<GameObject> bullets = new ArrayList<>();

    // game state
    static GameState gameState = GameState.START;

    // score
    static int score = 0;

    // current level
    static int currentLevel = 1;

    // lives
    static int lives = 3;

    // game over
    static boolean gameOver = false;

    // power up chance
    static double POWER_UP_CHANCE = 1;

    // start game
    static void StartGame() {
        //set game state to start
        gameState = GameState.START;
        startMenu = new GameObject();
        startMenu.shape = new Rectangle2D.Double(0, 0, GatorEngine.WIDTH, GatorEngine.HEIGHT);
        startMenu.material = new Material("./assets/Title.png");
        startMenu.scripts.add(new MouseHover(startMenu));
        // add start menu to object list
        GatorEngine.Create(startMenu);
    }

    static void LevelSetup(){
        // create background
        background = new GameObject();
        background.shape = new Rectangle2D.Double(0, 0, GatorEngine.WIDTH, GatorEngine.HEIGHT);
        background.material = new Material("./assets/Background.png");
        // add background to object list
        GatorEngine.Create(background);

        // create player
        player = new GameObject();
        player.shape = new Rectangle2D.Double(0, 0, 40, 40);
        player.material = new Material("./assets/Ship.png");
        player.scripts.add(new Mover(player));
        player.scripts.add(new PlayerCollision(player));
        GatorEngine.Create(player);

        // create lives
        createLifeIcons();
    }

    static void LevelOne(){
        //set game state to start
        gameState = GameState.PLAY;
        LevelSetup();
        // create enemies
        CreateEnemies();
        spawnPowerUp();
    }

    static void LevelTwo(){
        LevelSetup();
        // create new enemies
        CreateEnemies();
        spawnPowerUp();

        // change 5 random enemies to a different color
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            int randomIndex = rand.nextInt(enemies.size());
            enemies.get(randomIndex).material = new Material("./assets/Enemy2.png");
            // remove the bullet collision script from the enemy and add a new script that handles the new enemy behavior
            enemies.get(randomIndex).scripts.remove(1);
            enemies.get(randomIndex).scripts.add(new LevelTwoEnemy(enemies.get(randomIndex)));
        }

    }

    static void LevelThree(){
        LevelSetup();
        // create new enemies
        CreateEnemies();
        spawnPowerUp();
        // change 5 random enemies to a different color
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            int randomIndex = rand.nextInt(enemies.size());
            enemies.get(randomIndex).material = new Material("./assets/Enemy3.png");
            // remove the bullet collision script from the enemy and add a new script that handles the new enemy behavior
            enemies.get(randomIndex).scripts.remove(1);
            enemies.get(randomIndex).scripts.add(new LevelThreeEnemy(enemies.get(randomIndex)));
        }
    }

    static void updateGame() {
        // Check if the game is in play state
        if (gameState == GameState.PLAY) {
            // Check if all enemies are cleared
            if (enemies.isEmpty()) {
                currentLevel++; // Increment the level
                clearLevel(); // Clear the current level setup
                // Progress to the next level or loop back to level one
                switch (currentLevel) {
                    case 2:
                        LevelTwo();
                        break;
                    case 3:
                        LevelThree();
                        break;
                    default:
                        currentLevel = 1; // Reset to level one
                        LevelOne(); // Start level one again
                        break;
                }
            }
        }
    }    

    static void clearLevel() {
        // clear
        GatorEngine.Delete(enemyBullet);
        GatorEngine.Delete(bullet);
        GatorEngine.Delete(background);
        GatorEngine.Delete(player);
        // remove all enemies
        for (GameObject enemy : enemies) {
            GatorEngine.Delete(enemy);
        }
        // reset lives to 3
        lives = 3;
        createLifeIcons();
    }

    static void resetGame() {
        // Clear bullets, enemies, and other game elements
        bullets.clear();
        enemyBullets.clear();
        GatorEngine.Delete(enemyBullet);
        GatorEngine.Delete(bullet);
        GatorEngine.Delete(background);
        GatorEngine.Delete(player);
        for (GameObject enemy : enemies) {
            GatorEngine.Delete(enemy);
        }
        enemies.clear();
    
        // Reset lives and recreate life icons
        lives = 3;
        createLifeIcons();
    }    

    static void CreateEnemies() {
        final int rows = 3;
        final int cols = 6;
        final int enemyWidth = 40;
        final int enemyHeight = 40;
        final int spacing = 10;
        final int startingX = 100; // Define starting X position for the grid
        final int startingY = 50;  // Define starting Y position for the grid
    
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                    // Create a default game object
                    GameObject enemy = new GameObject();
                    enemy.shape = new Rectangle2D.Double(0, 0, enemyWidth, enemyHeight); // Enemy size
                    enemy.material = new Material("./assets/Enemy1.png"); // Enemy color
                    enemy.scripts.add(new EnemyMovement(enemy)); // Enemy movement script
                    enemy.scripts.add(new BulletCollision(enemy)); // Enemy collision script
                    enemy.Translate(startingX + (col * (enemyWidth + spacing)), startingY + (row * (enemyHeight + spacing))); // Enemy position
                    GatorEngine.Create(enemy);
                    enemies.add(enemy); // Add enemy to the enemy list
            }
        }
    }

    static void createLifeIcons() {
        livesIcons.clear(); // Clear existing icons if any
        for (int i = 0; i < lives; i++) {
            GameObject life = new GameObject();
            life.shape = new Rectangle2D.Double(0, 0, 25, 25);
            life.material = new Material("./assets/Ship.png");
            life.Translate(25 + (i * 25), 450);
            GatorEngine.Create(life);
            livesIcons.add(life);
        }
    }

    static void spawnPowerUp() {
        // Power-up spawning logic, only spawn one per level
        if (Math.random() < POWER_UP_CHANCE && !playerIsInvincible) {
        GameObject powerUp = new GameObject();
        powerUp.shape = new Rectangle2D.Double(0, 0, 25, 25);
        powerUp.material = new Material("./assets/invincible_item.png");
        // random x,y position located above the player
        Random rand = new Random();
        int randomX = rand.nextInt(400);
        int randomY = rand.nextInt(200);
        powerUp.Translate(randomX, randomY);
        powerUp.scripts.add(new PowerUp(powerUp));
        powerUp.scripts.add(new BulletCollision(powerUp));
        GatorEngine.Create(powerUp);
        }
    }

    static public class LevelTwoEnemy extends ScriptableBehavior{
        int hitCount = 0;
        LevelTwoEnemy(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {
        }
    
        @Override
        public void Update() {
            // level two enemies need to be hit twice to be destroyed
            for (GameObject bullet : bullets) {
                if (gameObject.CollidesWith(bullet)) {
                    hitCount++;
                    GatorEngine.Delete(bullet); // Delete bullet
                    bullets.remove(bullet); // Remove bullet from the list
                    // change enemy color to indicate hit
                    if (hitCount == 1) {
                        gameObject.material = new Material("./assets/Enemy1.png");
                    } 
                    else if (hitCount == 2) {
                        GatorEngine.Delete(gameObject); // Delete enemy
                        enemies.remove(gameObject); // Remove enemy from the list
                    }
                    break; // Exit loop after handling collision
                }
            }

            // increase shooting probability
            EnemyMovement.setShootingProbability(0.05);

        }
    }

    static public class LevelThreeEnemy extends ScriptableBehavior{
        int hitCount = 0;
        LevelThreeEnemy(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {
        }
    
        @Override
        public void Update() {
            // level three enemies need to be hit three times to be destroyed
            for (GameObject bullet : bullets) {
                if (gameObject.CollidesWith(bullet)) {
                    hitCount++;
                    GatorEngine.Delete(bullet); // Delete bullet
                    bullets.remove(bullet); // Remove bullet from the list
                    // change enemy color to indicate hit
                    if (hitCount == 1) {
                        // turn into level two enemy
                        gameObject.material = new Material("./assets/Enemy2.png");
                    } 
                    else if (hitCount == 2) {
                        gameObject.material = new Material("./assets/Enemy1.png");
                    }
                    else if (hitCount == 3) {
                        GatorEngine.Delete(gameObject); // Delete enemy
                        enemies.remove(gameObject); // Remove enemy from the list
                    }
                    break; // Exit loop after handling collision
                }
            }
            // increase shooting probability
            EnemyMovement.setShootingProbability(0.1);
        }
    }
    
    static public class EnemyMovement extends ScriptableBehavior{
        private int moveDistance = 50;
        private int moveStep = 0;
        private Direction currentDirection = Direction.RIGHT;
        private Direction lastDirection = Direction.RIGHT;
        private static double shootingProbability = 0.02;
    
        EnemyMovement(GameObject g) {
            super(g);
            gameObject.Translate(0, 0);
        }
    
        @Override
        public void Start() {      
        }
    
        @Override
        public void Update() {
            if (currentDirection == Direction.RIGHT) {
                gameObject.Translate(0.5f, 0);
            } else if (currentDirection == Direction.LEFT) {
                gameObject.Translate(-0.5f, 0);
            } else if (currentDirection == Direction.DOWN) {
                gameObject.Translate(0, 0.5f);
            }
    
            moveStep++;
            if (moveStep >= moveDistance) {
                moveStep = 0;
                switchDirection();
            }
        }
    
        private void switchDirection() {
            if (currentDirection == Direction.RIGHT) {
                currentDirection = Direction.DOWN;
                lastDirection = Direction.RIGHT;
            } else if (currentDirection == Direction.DOWN && lastDirection == Direction.RIGHT) {
                currentDirection = Direction.LEFT;
                lastDirection = Direction.DOWN;
            } else if (currentDirection == Direction.LEFT) {
                currentDirection = Direction.DOWN;
                lastDirection = Direction.LEFT;
            } else if (currentDirection == Direction.DOWN && lastDirection == Direction.LEFT) {
                currentDirection = Direction.RIGHT;
                lastDirection = Direction.DOWN;
            }

            if (Math.random() < getShootingProbability()) {
                fireEnemyBullet();
            }
        }        
    
        private enum Direction {
            RIGHT, DOWN, LEFT
        }

        private double getShootingProbability() {
            return shootingProbability;
        }

        private void fireEnemyBullet() {
            // same logic in the player bullet, but the bullet is red and moves down
            enemyBullet = new GameObject();
            enemyBullet.shape = new Rectangle2D.Double(gameObject.transform.getTranslateX(), gameObject.transform.getTranslateY(), 5, 10); // Bullet size and initial position
            enemyBullet.material = new Material(Color.RED, Color.RED, 0); // Bullet color
            enemyBullet.scripts.add(new EnemyBulletMover(enemyBullet)); // Bullet movement script
            GatorEngine.Create(enemyBullet);
            enemyBullets.add(enemyBullet); // Add bullet to the bullet list
        }

        static void setShootingProbability(double newProbability) {
            shootingProbability = newProbability;
        }
    }
    
    static public class MouseHover extends ScriptableBehavior{
        String startHover;
        String startNormal;
        String endHover;
        String endNormal;

        MouseHover(GameObject g) {
            super(g);
            startHover = "./assets/Title_hover.png";
            startNormal = "./assets/Title.png";
            endHover = "./assets/DeathScreen_hover.png";
            endNormal = "./assets/DeathScreen.png";
        }

        @Override
        public void Start() {
        }

        @Override
        public void Update() {
            // if the mouse is hovering over the button, change the image to the highlighted version
            if (gameState == GameState.START){
                if (Input.MouseX > 201 && Input.MouseX < 300 && Input.MouseY > 313 && Input.MouseY < 363) {
                    gameObject.material.setImg(startHover);
                    // if the mouse is clicked, start the game
                    if(Input.MousePressed){
                        gameState = GameState.PLAY;
                        GatorEngine.Delete(startMenu);
                        LevelOne();
                    }
                }
                else {
                    gameObject.material.setImg(startNormal);
                }
            }
            else if (gameState == GameState.END){
                if (Input.MouseX > 144 && Input.MouseX < 367 && Input.MouseY > 367 && Input.MouseY < 433){
                    gameObject.material.setImg(endHover);
                    // if the mouse is clicked, start the game
                    if(Input.MousePressed){
                        resetGame();
                        GatorEngine.Delete(gameOverMenu);
                        StartGame();
                    }
                }
                else {
                    gameObject.material.setImg(endNormal);
                }
            }
        }
    }

    static public class Mover extends ScriptableBehavior{
        private float lastShotTime = 0;
        private float movementSpeed = 4;
        private float shotDelay = 500;
        Mover(GameObject g) {
            super(g);
            gameObject.Translate(225, 380);
        }

        @Override
        public void Start() {
            
        }

        @Override
        public void Update(){
            lastShotTime += GatorEngine.FRAMEDELAY;
            if(Input.GetKeyDown('a')){
                gameObject.Translate(-movementSpeed,0);
            }
            if(Input.GetKeyDown('d')){
                gameObject.Translate(movementSpeed,0);
            }
            // shoot when space is pressed
            if(Input.GetKeyDown(' ') && lastShotTime >= 500) {
                fireBullet();
                lastShotTime = 0;
            }
        }

        public void fireBullet(){
            if (lastShotTime >= shotDelay) {
                bullet = new GameObject();
                bullet.shape = new Rectangle2D.Double(gameObject.transform.getTranslateX(), gameObject.transform.getTranslateY(), 5, 10);
                bullet.material = new Material(Color.WHITE, Color.WHITE, 0);
                bullet.scripts.add(new BulletMover(bullet));
                GatorEngine.Create(bullet);
                bullets.add(bullet);
                lastShotTime = 0;
            }
        }
        

        public void setPowerUp(boolean active){
            if (active) {
                movementSpeed = 10;
                shotDelay = 50;
            } else {
                movementSpeed = 4;
                shotDelay = 500;
            }
        }
    }

    static public class PowerUp extends ScriptableBehavior {
        PowerUp(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {
        }
    
        @Override
        public void Update() {
            for (GameObject bullet : bullets) {
                if (gameObject.CollidesWith(bullet)) {
                    // Activate power-up effects
                    playerIsInvincible = true;
                    player.material = new Material("./assets/ShipUpgrade.png");
                    ((Mover)player.scripts.get(0)).setPowerUp(true);
        
                    // Schedule to deactivate effects after 10 seconds
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            playerIsInvincible = false;
                            ((Mover)player.scripts.get(0)).setPowerUp(false);
                            player.material = new Material("./assets/Ship.png");
                        }
                    }, 10000);
        
                    GatorEngine.Delete(gameObject); // Remove power-up item
                    GatorEngine.Delete(bullet); // Remove the bullet
                    bullets.remove(bullet); // Remove bullet from list
                    break;
                }
            }
        }
    }

    static public class BulletMover extends ScriptableBehavior{
        BulletMover(GameObject g) {
            super(g);
        }
    
        @Override
        public void Start() {
        }
    
        @Override
        public void Update(){
            gameObject.Translate(0, -5); // Move the bullet upwards
        }
    }

    // enemy bullet
    static public class EnemyBulletMover extends ScriptableBehavior{
        EnemyBulletMover(GameObject g) {
            super(g);
        }
    
        @Override
        public void Start() {
        }
    
        @Override
        public void Update(){
            gameObject.Translate(0, 5); // Move the bullet downwards
        }
    }
    static public class BulletCollision extends ScriptableBehavior {
    
        BulletCollision(GameObject g) {
            super(g);
        }
    
        @Override
        public void Start() {
        }
    
        @Override
        public void Update() {
            // Assuming 'gameObject' is an enemy and checking collision with all bullets
            for (GameObject bullet : bullets) {
                if (gameObject.CollidesWith(bullet)) {
                    GatorEngine.Delete(gameObject); // Delete enemy
                    GatorEngine.Delete(bullet); // Delete bullet
                    bullets.remove(bullet); // Remove bullet from the list
                    enemies.remove(gameObject); // Remove enemy from the list
                    break; // Exit loop after handling collision
                }
            }
        }
    }

    // user ship collision
    static public class PlayerCollision extends ScriptableBehavior {

        PlayerCollision(GameObject g) {
            super(g);
        }
    
        @Override
        public void Start() {
        }
    
        @Override
        public void Update() {
            // Check collision with all enemy bullets
            for (GameObject enemyBullet : enemyBullets) {
                if (gameObject.CollidesWith(enemyBullet)) {
                    if (!playerIsInvincible) {
                        playerIsInvincible = true;
                        GatorEngine.Delete(enemyBullet); // Delete enemy bullet
                        enemyBullets.remove(enemyBullet); // Remove bullet from the list
                        lives -= 1; // Decrease life
                        updateLivesDisplay(); // Update lives display
                        changePlayerIcon(); // Blink player icon
        
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                playerIsInvincible = false;
                            }
                        }, 3000); // Set invincibility duration to 3 seconds
                    }
                    if (lives == 0) {
                        gameOver = true; // Set game over flag
                        // clear everything
                        GatorEngine.Delete(background);
                        GatorEngine.Delete(player);
                        GatorEngine.Delete(enemyBullet);
                        GatorEngine.Delete(bullet);
                        GatorEngine.Delete(startMenu);
                        // remove all enemies
                        for (GameObject enemy : enemies) {
                            GatorEngine.Delete(enemy);
                        }
                        // create game over menu
                        gameOverMenu = new GameObject();
                        gameState = GameState.END;
                        gameOverMenu.shape = new Rectangle2D.Double(0, 0, GatorEngine.WIDTH, GatorEngine.HEIGHT);
                        gameOverMenu.material = new Material("./assets/DeathScreen.png");
                        gameOverMenu.scripts.add(new MouseHover(gameOverMenu));
                        // add menu to object list
                        GatorEngine.Create(gameOverMenu);
                    }
                    break; // Exit loop after handling collision
                }
            }

            // if the enemies reach the player ship, game over
            for (GameObject enemy : enemies) {
                if (enemy.CollidesWith(gameObject) || enemy.transform.getTranslateY() > 400) {
                    gameOver = true; // Set game over flag
                    // clear everything
                    GatorEngine.Delete(background);
                    GatorEngine.Delete(player);
                    GatorEngine.Delete(enemyBullet);
                    GatorEngine.Delete(bullet);
                    GatorEngine.Delete(startMenu);
                    // remove all enemies
                    for (GameObject enemy1 : enemies) {
                        GatorEngine.Delete(enemy1);
                    }
                    // create game over menu
                    gameOverMenu = new GameObject();
                    gameState = GameState.END;
                    gameOverMenu.shape = new Rectangle2D.Double(0, 0, GatorEngine.WIDTH, GatorEngine.HEIGHT);
                    gameOverMenu.material = new Material("./assets/DeathScreen.png");
                    gameOverMenu.scripts.add(new MouseHover(gameOverMenu));
                    // add menu to object list
                    GatorEngine.Create(gameOverMenu);
                    break;
                }
            }
        }
    
        private void updateLivesDisplay() {
            // Remove one life from the screen
            GatorEngine.Delete(livesIcons.get(livesIcons.size() - 1));
            livesIcons.remove(livesIcons.size() - 1);
        }
    
        private void changePlayerIcon() {
            long invincibilityDuration = 3000; // 3 seconds of invincibility
            long flickerInterval = 50; // 200ms flicker interval
            long startTime = System.currentTimeMillis();
        
            new Thread(() -> {
                boolean iconVisible = true;
                while (System.currentTimeMillis() - startTime < invincibilityDuration) {
                    if (iconVisible) {
                        player.material = new Material("./assets/ShipHit.png"); // Example invisible icon
                    } else {
                        player.material = new Material("./assets/Ship.png"); // Regular icon
                    }
                    iconVisible = !iconVisible;
                    try {
                        Thread.sleep(flickerInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                player.material = new Material("./assets/Ship.png"); // Reset to regular icon after invincibility
            }).start();
        
            playerIsInvincible = true; // Set a flag to indicate the player is invincible
            // remove the collision script
            player.scripts.remove(1);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    playerIsInvincible = false; // Reset invincibility flag after duration
                }
            }, invincibilityDuration);
            player.scripts.add(new PlayerCollision(player)); // Add collision script back after duration
        }
    }

}