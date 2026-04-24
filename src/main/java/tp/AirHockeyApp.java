package tp;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import tp.entities.Paddle;
import tp.entities.Puck;
import tp.game.State;
import tp.ui.HUD;


import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;

import java.util.List;
import java.util.ArrayList;
import tp.entities.PowerUp;
import tp.game.Physics;

import static com.jme3.anim.tween.Tweens.delay;

public class AirHockeyApp extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Paddle paddle1, paddle2;
    private Puck puck;
    private State state;
    private HUD hud;
    private int servingPlayer = (Math.random() < 0.5) ? 1 : 2;
    // dernier joueur qui a touché le puck
    private int lastPlayer = 0;
    // Choix du mode de jeu pour le moment en brut avant l'ajout du menu pour choisir le mode de jeu
    public enum Mode { MULTI, SOLO};
    private Mode mode = Mode.SOLO;

    //Touches de déplacement pour les 2 joueurs
    private boolean up1, down1, left1, right1; //joueur1
    private boolean up2, down2, left2, right2; //joueur2

    public float paddleSpeed = 2f; // vitesse de déplacement des paddles


    private final List<PowerUp> bonusSurTerrain  = new ArrayList<>(); // max 2 sur la table
    private final List<PowerUp> effetsActifs     = new ArrayList<>(); // effets en cours
    private float timerSpawnBonus  =0f;
    private final float INTERVALLE_SPAWN=10f;
    private final int MAX_BONUS=2;


    // remetre le puck en fonction du dernier qui a marque un but
    private void resetPositions() {
        paddle1.getRigidBodyPaddle().setPhysicsLocation(new Vector3f(-1f, 0.78f, 0f));
        paddle2.getRigidBodyPaddle().setPhysicsLocation(new Vector3f(1f, 0.78f, 0f));

        if (servingPlayer == 1) {
            puck.getBody().setPhysicsLocation(new Vector3f(-0.6f, 0.78f, 0f));
        } else {
            puck.getBody().setPhysicsLocation(new Vector3f(0.6f, 0.78f, 0f));
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

    // quand une raquette toche le puck pour savoir qui a joue en denier
    public void setLastPlayer(int player) {
        lastPlayer = player;
    }

    // le puck est entre dans le cote du joeur 1
    public void goalOnPlayer1Side() {
        if (lastPlayer == 2) {
            goalForPlayer2();
        } else if (lastPlayer == 1) {
            ownGoalPlayer1();
        }
    }

    // le puck est entre dans le cote du joeur 2
    public void goalOnPlayer2Side() {
        if (lastPlayer == 1) {
            goalForPlayer1();
        } else if (lastPlayer == 2) {
            ownGoalPlayer2();
        }
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

    // but  joueur 2 contre son camp
    public void ownGoalPlayer2() {
        if (state.isGameOver()) {
            return;
        }

        state.player2OwnGoal();
        servingPlayer = 2;
        updateAfterGoal();
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
        //bulletAppState.setDebugEnabled(true);

        // Chargement de la Table
        Table table = new Table(assetManager, rootNode, bulletAppState);
        table.load();

        // Chargement des raquettes et du palet (paddle1, paddle2, puck)
        // Sur des positions par defaut
        paddle1 = new Paddle(assetManager, rootNode, bulletAppState, new Vector3f(-1f, 0.78f, 0f),-1.10f, -0.50f); // y=0.76f +-
        paddle2 = new Paddle(assetManager, rootNode, bulletAppState, new Vector3f(1f, 0.78f, 0f),0.50f, 1.10f); // 0.76f +-
        puck = new Puck(assetManager, rootNode, bulletAppState, new Vector3f(0.6f, 0.80f, 0f));

        // Visualisation des Zones de but (pour débugger)
        /*Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Red);
        Geometry zoneP1 = new Geometry("zP1", new Box(0.01f, 0.1f, 0.23f));
        zoneP1.setMaterial(m);
        zoneP1.setLocalTranslation(-1.15f, 0.82f, 0f);
        rootNode.attachChild(zoneP1);

        Material m2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m2.setColor("Color", ColorRGBA.Blue);
        Geometry zoneP2 = new Geometry("zP2", new Box(0.01f, 0.1f, 0.23f));
        zoneP2.setMaterial(m2);
        zoneP2.setLocalTranslation(1.15f, 0.82f, 0f);
        rootNode.attachChild(zoneP2);*/

        if(Mode.MULTI == mode) {
            // Vue en plongé pour le mode multi

            cam.setLocation(new Vector3f(0f, 3f, 3f));
            Vector3f position = new Vector3f(0f, 0f, 0f);
            cam.lookAt(position, Vector3f.UNIT_Y);
        }else {

            // Vue pour le mode solo (ou on regarde son vis à vis)
            // à Adapter pour l'ergonomie
            /*cam.setLocation(new Vector3f(-5f, 7f, 0f));
            Vector3f position = new Vector3f(3f, 0f, 0f);
            cam.lookAt(position, Vector3f.UNIT_Y);*/
            cam.setLocation(new Vector3f(3.5f, 2.2f, 0f));
            Vector3f position = new Vector3f(0f, 0.78f, 0f);
            cam.lookAt(position, Vector3f.UNIT_Y);

        }

        if (mode == Mode.MULTI) {
            // Controle de la caméra pour débug mais par defaut on laisse setEnabled(false) pour pas que la caméra bouge lorsque l'on joue
            flyCam.setMoveSpeed(10f);
            flyCam.setEnabled(false);
            flyCam.setMoveSpeed(1f);
            flyCam.setRotationSpeed(0.1f);
        }else{
            flyCam.setEnabled(false); // on fixe la caméra pour pas qu'elle bouge lorsque l'on joue
        }

        // Controles des déplacements des paddles
        //Joueur 1
        inputManager.addMapping("p1_Up", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("p1_Down", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("p1_Left", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("p1_Right", new KeyTrigger(KeyInput.KEY_L));

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
        inputManager.addMapping("OwnGoalP2", new KeyTrigger(KeyInput.KEY_B));

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
                        if (isPressed || state.isGameOver()) {
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
    }

        @Override
        public void simpleUpdate ( float tpf){
            //Mouvement du paddle1
            Vector3f direction1 = new Vector3f(0, 0, 0);
            if(mode==Mode.MULTI) {
                if (up1) direction1.z -= paddleSpeed;
                System.out.println();
                if (down1) direction1.z += paddleSpeed;
                if (left1) direction1.x -= paddleSpeed;
                if (right1) direction1.x += paddleSpeed;
            }else{
                if (up1)    direction1.x -= paddleSpeed;
                if (down1)  direction1.x += paddleSpeed;
                if (left1)  direction1.z += paddleSpeed;
                if (right1) direction1.z -= paddleSpeed;
            }
            paddle1.move(direction1);

            //Mouvement du paddle2
            Vector3f direction2 = new Vector3f(0, 0, 0);
            if(mode==Mode.MULTI) {
                if (up2) direction2.z -= paddleSpeed;
                if (down2) direction2.z += paddleSpeed;
                if (left2) direction2.x -= paddleSpeed;
                if (right2) direction2.x += paddleSpeed;
            }else{
                if (up2)    direction2.x -= paddleSpeed;
                if (down2)  direction2.x += paddleSpeed;
                if (left2)  direction2.z += paddleSpeed;
                if (right2) direction2.z -= paddleSpeed;
            }
            paddle2.move(direction2);


            puck.update();


            // Détecter la dernière personne qui a touché le puck
            float distP1 = puck.getBody().getPhysicsLocation().distance(paddle1.getRigidBodyPaddle().getPhysicsLocation());
            float distP2 = puck.getBody().getPhysicsLocation().distance(paddle2.getRigidBodyPaddle().getPhysicsLocation());
            float seuilContact = 0.3f;

            if (distP1 < seuilContact) {
                lastPlayer = 1;
            } else if (distP2 < seuilContact) {
                lastPlayer = 2;
            }


            //Si le puck est entre (-0.23 et 0.23)z et x = -1.17 ou + : but pour le joueur 1 si x=1.17 but pour joueur 2
            if (puck.getBody().getPhysicsLocation().x<=-1.17f && (puck.getBody().getPhysicsLocation().z>=-0.23f && puck.getBody().getPhysicsLocation().z<=0.23f)){
                if(lastPlayer==1){
                    ownGoalPlayer1();
                } else if (lastPlayer==2){
                    goalForPlayer2();
                }
            }
            else if (puck.getBody().getPhysicsLocation().x>=1.17f && (puck.getBody().getPhysicsLocation().z>=-0.23f && puck.getBody().getPhysicsLocation().z<=0.23f)){
                if(lastPlayer==2){
                    ownGoalPlayer2();
                } else if (lastPlayer==1){
                    goalForPlayer1();
                }
            }

            System.out.println("P1  X : " + paddle1.getRigidBodyPaddle().getPhysicsLocation().x +" Y : " + paddle1.getRigidBodyPaddle().getPhysicsLocation().y + "Z " + paddle1.getRigidBodyPaddle().getPhysicsLocation().z);
            //System.out.println("paddle2 Z : " + paddle2.getRigidBodyPaddle().getPhysicsLocation().z);
            //System.out.println("Puck : X :"+ puck.getBody().getPhysicsLocation().x+" Y    : " + puck.getBody().getPhysicsLocation().y + " Z : " + puck.getBody().getPhysicsLocation().z);
            delay(100);


            // Ajout de la pHysic avec les lift et les smash


            // Physique paddle1
            float vitessePaddle1 = paddle1.getRigidBodyPaddle().getLinearVelocity().length();
            if (distP1 < 0.25f) {
                if (vitessePaddle1 > 1.5f) {
                    Vector3f dirP1 = paddle1.getRigidBodyPaddle().getLinearVelocity().normalize();
                    Physics.appliquerSmash(puck, vitessePaddle1);
                    Physics.appliquerLift(puck, dirP1);
                } else if (vitessePaddle1 < 0.3f) {
                    Physics.appliquerFlip(puck);
                }
            }

            // Physique paddle2
            float vitessePaddle2 = paddle2.getRigidBodyPaddle().getLinearVelocity().length();
            if (distP2 < 0.25f) {
                if (vitessePaddle2 > 1.5f) {
                    Vector3f dirP2 = paddle2.getRigidBodyPaddle().getLinearVelocity().normalize();
                    Physics.appliquerSmash(puck, vitessePaddle2);
                    Physics.appliquerLift(puck, dirP2);
                } else if (vitessePaddle2 < 0.3f) {
                    Physics.appliquerFlip(puck);
                }
            }

            // Spawn : 1 bonus toutes les 10s, max 2 sur le terrain
            timerSpawnBonus += tpf;
            if (timerSpawnBonus >= INTERVALLE_SPAWN && bonusSurTerrain.size() < MAX_BONUS) {
                timerSpawnBonus = 0f;
                PowerUp.Type[] types = PowerUp.Type.values();
                PowerUp.Type typeAleatoire = types[(int)(Math.random() * types.length)];
                float rx = (float)(Math.random() * 1.6f - 0.8f);
                float rz = (float)(Math.random() * 0.8f - 0.4f);
                bonusSurTerrain.add(new PowerUp(assetManager, rootNode, typeAleatoire, new Vector3f(rx, 0.79f, rz)));
            }

            // Collecte
            for (int i = bonusSurTerrain.size() - 1; i >= 0; i--) {
                PowerUp bonus = bonusSurTerrain.get(i);
                if (puck.getBody().getPhysicsLocation().distance(bonus.getPosition()) < 0.15f) {
                    // Vérifier si ce type est déjà actif (pas de cumul d'effet)
                    boolean dejaActif = false;
                    for (PowerUp effetActif : effetsActifs) {
                        if (effetActif.getType() == bonus.getType()) {
                            effetActif.prolongerDuree(10f); // prolonge juste le timer
                            dejaActif = true;
                            break;
                        }
                    }
                    if (!dejaActif) {
                        bonus.appliquerEffet(puck, paddle1, paddle2, lastPlayer);
                        effetsActifs.add(bonus);
                    }
                    bonus.collecter();
                    bonusSurTerrain.remove(i);
                }
            }

            // Timers des effets actifs
            for (int i = effetsActifs.size() - 1; i >= 0; i--) {
                PowerUp effet = effetsActifs.get(i);
                effet.mettreAJourTimer(tpf, puck, paddle1, paddle2);
                if (effet.tempsRestant <= 0) {
                    effetsActifs.remove(i);
                }
            }


        }
}