package tp;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.custom.ArmatureDebugAppState;
import tp.entities.Paddle;
import tp.entities.Puck;
import tp.game.State;
import tp.ui.HUD;
import tp.ui.Menu;
import tp.entities.Paddle;
import tp.entities.Puck;

public class AirHockeyApp extends SimpleApplication {

    private Menu menu;

    private BulletAppState bulletAppState;
    private Paddle paddle1, paddle2;
    private Puck puck;
    private State state;
    private HUD hud;
    private int servingPlayer = (Math.random() < 0.5) ? 1 : 2;
    private com.jme3.scene.Node sceneSauvegardee = new com.jme3.scene.Node("SceneSauvegardee");
    private boolean isMenuOpen = true;

    //Touches de déplacement pour les 2 joueurs
    private boolean up1, down1, left1, right1; //joueur1
    private boolean up2, down2, left2, right2;//joueur2
    private boolean isMulti;

    public float paddleSpeed = 6f; // vitesse de déplacement des paddles

    // remetre le puck en fonction du dernier qui a marque un but
    private void resetPositions() {
        paddle1.getRigidBodyPaddle().setPhysicsLocation(new Vector3f(-2f, 2f, 0f));
        paddle2.getRigidBodyPaddle().setPhysicsLocation(new Vector3f(2f, 2f, 0f));

        if (servingPlayer == 1) {
            puck.getBody().setPhysicsLocation(new Vector3f(-1f, 0.77f, 0f));
        } else {
            puck.getBody().setPhysicsLocation(new Vector3f(1f, 0.77f, 0f));
        }

        puck.getBody().setLinearVelocity(Vector3f.ZERO);
        puck.getBody().setAngularVelocity(Vector3f.ZERO);
    }

    private void showEndMessage() {
        hud.showMessage(state.getWinnerText() + " - Appuyer sur R pour relancer la une partie");
    }

    private void updateAfterGoal() {
        hud.updateScore(state.getScorePlayer1(), state.getScorePlayer2());

        if (state.isGameOver()) {
            showEndMessage();
        } else {
            resetPositions();
        }
    }

    private void restartGame() {
        state.reset();
        servingPlayer = (Math.random() < 0.5) ? 1 : 2;
        hud.updateScore(state.getScorePlayer1(), state.getScorePlayer2());
        hud.clearMessage();
        resetPositions();
    }


    // but normal joueur 1
    public void goalForPlayer1() {
        if (state.isGameOver()) {
            return;
        }

        state.player1Scores();
        servingPlayer = 2;
        updateAfterGoal();
    }

    // but normal joueur 2
    public void goalForPlayer2() {
        if (state.isGameOver()) {
            return;
        }

        state.player2Scores();
        servingPlayer = 1;
        updateAfterGoal();
    }

    // but  joueur 1 contre son camp
    public void ownGoalPlayer1() {
        if (state.isGameOver()) {
            return;
        }

        state.player1OwnGoal();
        servingPlayer = 1;
        updateAfterGoal();

    }

    // but joueur 2 contre son camp
    public void ownGoalPlayer2() {
        if (state.isGameOver()) {
            return;
        }

        state.player2OwnGoal(); // Vérifie que cette méthode existe dans State.java
        servingPlayer = 2;
        updateAfterGoal();
    }

    public void demarrerLeJeu(boolean multiplayer) {
        this.isMenuOpen = false;
        this.isMulti = multiplayer;

        // 1. On remet le plateau de jeu à l'écran
        rootNode.attachChild(sceneSauvegardee);

        // 2. On affiche le score
        hud.afficher(guiNode);

        // 3. On lance la physique
        bulletAppState.setEnabled(true);
        bulletAppState.setDebugEnabled(false);

        // 4. Réglage de la caméra selon le mode choisi
        if (multiplayer) {
            cam.setLocation(new Vector3f(0f, 10f, 0.1f));
            cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        } else {
            cam.setLocation(new Vector3f(0f, 5f, 7f));
            cam.lookAt(new Vector3f(0f, 0.7f, 0f), Vector3f.UNIT_Y);
        }

        // 5. Souris et Caméra
        flyCam.setEnabled(true);
        inputManager.setCursorVisible(false);
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("/", ClasspathLocator.class);

        // Set up de la Physique : les raquettes et palets doivent être sur la surface de la table et ne pas la traverser
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));
        state = new State();
        hud = new HUD(assetManager, guiNode, cam);
        hud.updateScore(state.getScorePlayer1(), state.getScorePlayer2());
        //Hitbox des objets
        bulletAppState.setDebugEnabled(true);

        // Chargement de la Table
        Table table = new Table(assetManager, rootNode, bulletAppState);
        table.load();

        // Chargement des raquettes et du palet (paddle1, paddle2, puck)
        // Sur des positions par defaut
        // Pour le Joueur 1 (côté gauche de la table)
        paddle1 = new Paddle(assetManager, rootNode, bulletAppState, new Vector3f(-2f, 2f, 0f), -1.15f, -0.05f);

        // Pour le Joueur 2 (côté droit de la table)
        paddle2 = new Paddle(assetManager, rootNode, bulletAppState, new Vector3f(2f, 2f, 0f), 0.05f, 1.15f);
        puck = new Puck(assetManager, rootNode, bulletAppState, new Vector3f(0f, 0.77f, 0f));
        // Ajout de la lumière pour voir les textures
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Vue en plongé pour le mode multi

        cam.setLocation(new Vector3f(0f, 8f, 8f));
        Vector3f position = new Vector3f(0f, 0f, 0f);
        cam.lookAt(position, Vector3f.UNIT_Y);

        // ajouter pour le menu
        menu = new Menu(this);
        menu.initialiserMenu();

        // Et pour nettoyer l'écran comme on a dit :
        setDisplayStatView(false);
        setDisplayFps(false);

        // Vue pour le mode solo (ou on regarde son vis à vis)
        // à Adapter pour l'ergonomie
        /*cam.setLocation(new Vector3f(-5f, 7f, 0f));
        Vector3f position = new Vector3f(3f,0f,0f);
        cam.lookAt(position, Vector3f.UNIT_Y);*/
        flyCam.setMoveSpeed(10f);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(1f);
        flyCam.setRotationSpeed(0.1f);

        // Controles des déplacements des paddles
        //Joueur 1
        inputManager.addMapping("p1_Up", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("p1_Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("p1_Left", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("p1_Right", new KeyTrigger(KeyInput.KEY_D));

        //Joueur 2
        inputManager.addMapping("p2_Up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("p2_Down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("p2_Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("p2_Right", new KeyTrigger(KeyInput.KEY_RIGHT));


        // Relancer le jeu
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));


        //test
        inputManager.addMapping("GoalP1", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("GoalP2", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("OwnGoalP1", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("OwnGoalP2", new KeyTrigger(KeyInput.KEY_J));

        com.jme3.input.controls.ActionListener actionListener = new com.jme3.input.controls.ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                switch (name) {
                    //Joueur 1
                    case "p1_Up":
                        up1 = isPressed;
                        break;
                    case "p1_Down":
                        down1 = isPressed;
                        break;
                    case "p1_Left":
                        left1 = isPressed;
                        break;
                    case "p1_Right":
                        right1 = isPressed;
                        break;

                    //Joueur 2
                    case "p2_Up":
                        up2 = isPressed;
                        break;
                    case "p2_Down":
                        down2 = isPressed;
                        break;
                    case "p2_Left":
                        left2 = isPressed;
                        break;
                    case "p2_Right":
                        right2 = isPressed;
                        break;

                    case "Restart":
                        if (isPressed && state.isGameOver()) {
                            restartGame();
                        }
                        break;

                    // test
                    case "GoalP1":
                        if (isPressed) {
                            goalForPlayer1();
                        }
                        break;

                    case "GoalP2":
                        if (isPressed) {
                            goalForPlayer2();
                        }
                        break;

                    case "OwnGoalP1":
                        if (isPressed) {
                            ownGoalPlayer1();
                        }
                        break;

                    case "OwnGoalP2":
                        if (isPressed) {
                            ownGoalPlayer2();
                        }
                        break;
                }
            }
        };

        inputManager.addListener(actionListener, "p1_Up", "p1_Down", "p1_Left", "p1_Right", "p2_Up", "p2_Down", "p2_Left", "p2_Right", "Restart", "GoalP1", "GoalP2", "OwnGoalP1", "OwnGoalP2");

        // --- LOGIQUE DE DÉPART DU MENU ---
        // On déplace tout ce que les collègues ont chargé dans un noeud caché
        for (com.jme3.scene.Spatial child : rootNode.getChildren()) {
            sceneSauvegardee.attachChild(child);
        }
        rootNode.detachAllChildren();

        hud.masquer(); // Cache le HUD (J1:0 | J2:0)
        bulletAppState.setEnabled(false); // S// toppe la physique au début
    }


    @Override
    public void simpleUpdate(float tpf) {

        if (isMenuOpen) return;

        //Mouvement du paddle1
        Vector3f direction1 = new Vector3f(0, 0, 0);
        if (up1) direction1.z -= paddleSpeed;
        if (down1) direction1.z += paddleSpeed;
        if (left1) direction1.x -= paddleSpeed;
        if (right1) direction1.x += paddleSpeed;
        paddle1.move(direction1);

        //Mouvement du paddle2
        // --- MOUVEMENT PADDLE 2 (Manuel OU IA) ---
        Vector3f direction2 = new Vector3f(0, 0, 0);

        if (isMulti) {
            // Mode DUO : On utilise les touches clavier
            if (up2) direction2.z -= paddleSpeed;
            if (down2) direction2.z += paddleSpeed;
            if (left2) direction2.x -= paddleSpeed;
            if (right2) direction2.x += paddleSpeed;
        } else {
            // Mode SOLO : L'IA suit le palet (Puck)
            Vector3f puckPos = puck.getBody().getPhysicsLocation();
            Vector3f paddle2Pos = paddle2.getRigidBodyPaddle().getPhysicsLocation();

            // Logique simple : si le palet est plus "haut" que la raquette, l'IA monte
            if (puckPos.z < paddle2Pos.z) direction2.z -= paddleSpeed * 0.8f;
            if (puckPos.z > paddle2Pos.z) direction2.z += paddleSpeed * 0.8f;

            // L'IA essaie de se mettre face au palet sur l'axe X
            if (puckPos.x > paddle2Pos.x) direction2.x += paddleSpeed * 0.8f;
            // On peut limiter l'IA pour qu'elle ne soit pas trop forte
        }
        paddle2.move(direction2);
    }
}