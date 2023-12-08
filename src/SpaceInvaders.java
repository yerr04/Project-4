import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SpaceInvaders {

    // game states
    enum GameState {
        START, PLAY, PAUSE, END
    }
    // start menu
    static GameObject startMenu;
    // background
    static GameObject background;
    // End menu
    static GameObject endMenu;
    // player ship and enemeies
    static GameObject player;
    static ArrayList<GameObject> enemies = new ArrayList<>();

    // bullets
    static ArrayList<GameObject> bullets = new ArrayList<>();

    // game state
    static GameState gameState = GameState.START;

    // score
    static int score = 0;

    // lives
    static int lives = 3;

    // game over
    static boolean gameOver = false;

    // start game
    static void StartGame() {
        //set game state to start
        gameState = GameState.START;
        startMenu = new GameObject();
        startMenu.shape = new Rectangle2D.Double(0, 0, GatorEngine.WIDTH, GatorEngine.HEIGHT);
        startMenu.material = new Material("./assets/Title.png");
        startMenu.scripts.add(new MouseHover(startMenu));
        // add start menu to object list
        //GatorEngine.OBJECTLIST.add(startMenu);
        GatorEngine.Create(startMenu);
    }

    static void InGame(){
        //set game state to start
        gameState = GameState.PLAY;
        // create background
        background = new GameObject();
        background.shape = new Rectangle2D.Double(0, 0, GatorEngine.WIDTH, GatorEngine.HEIGHT);
        background.material = new Material("./assets/Background.png");
        // add background to object list
        GatorEngine.Create(background);
        System.out.println("InGame");

        // create player
        player = new GameObject();
        player.shape = new Rectangle2D.Double(0, 0, 40, 40);
        player.material = new Material("./assets/Ship.png");
        player.scripts.add(new Mover(player));
        GatorEngine.Create(player);
    }
    

    static public class MouseHover extends ScriptableBehavior{
        String hover;
        String normal;

        MouseHover(GameObject g) {
            super(g);
            hover = "./assets/Title_hover.png";
            normal = "./assets/Title.png";
        }

        @Override
        public void Start() {
        }

        @Override
        public void Update() {
            // if the mouse is hovering over the button, change the image to the highlighted version
            if (gameState == GameState.START){
                if (Input.MouseX > 201 && Input.MouseX < 300 && Input.MouseY > 313 && Input.MouseY < 363) {
                    gameObject.material.setImg(hover);
                    System.out.println("Done");
                    // if the mouse is clicked, start the game
                    if(Input.MousePressed){
                        gameState = GameState.PLAY;
                        GatorEngine.Delete(startMenu);;
                        System.out.println("shit");
                        InGame();
                    }
                }
                else {
                    gameObject.material.setImg(normal);
                }
            }
        }
    }

    static public class Mover extends ScriptableBehavior{
        Mover(GameObject g) {
            super(g);
            gameObject.Translate(225, 380);
        }

        @Override
        public void Start() {
            
        }

        @Override
        public void Update(){
            if(Input.GetKeyDown('a')){
                gameObject.Translate(-4,0);
                System.out.println("fuck");
            }
            if(Input.GetKeyDown('d')){
                gameObject.Translate(4,0);
                System.out.println("fuck");
            }
        }
    }
}
