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

public class AirHockeyApp extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Paddle paddle1, paddle2;
    private Puck puck;

    //Touches de déplacement pour les 2 joueurs
    private boolean up1, down1, left1, right1; //joueur1
    private boolean up2, down2, left2, right2; //joueur2

    public float paddleSpeed = 6f; // vitesse de déplacement des paddles

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("/", ClasspathLocator.class);

        // Set up de la Physique : les raquettes et palets doivent être sur la surface de la table et ne pas la traverser
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));
        //Hitbox des objets
        bulletAppState.setDebugEnabled(true);

        // Chargement de la Table
        Table table = new Table(assetManager, rootNode, bulletAppState);
        table.load();

        // Chargement des raquettes et du palet (paddle1, paddle2, puck)
        // Sur des positions par defaut
        paddle1 = new Paddle(assetManager, rootNode, bulletAppState, new Vector3f(-2f, 2f, 0f)); // y=0.76f +-
        paddle2 = new Paddle(assetManager, rootNode, bulletAppState, new Vector3f(2f, 2f, 0f)); // 0.76f +-
        puck = new Puck(assetManager, rootNode, bulletAppState, new Vector3f(0f, 0.77f, 0f));

        // Vue en plongé pour le mode multi

        cam.setLocation(new Vector3f(0f, 8f, 8f));
        Vector3f position = new Vector3f(0f, 0f, 0f);
        cam.lookAt(position, Vector3f.UNIT_Y);

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
                }
            }
        };

        inputManager.addListener(actionListener, "p1_Up", "p1_Down", "p1_Left", "p1_Right", "p2_Up", "p2_Down", "p2_Left", "p2_Right");
    }

        @Override
        public void simpleUpdate ( float tpf){
            //Mouvement du paddle1
            Vector3f direction1 = new Vector3f(0, 0, 0);
            if (up1) direction1.z -= paddleSpeed;
            if (down1) direction1.z += paddleSpeed;
            if (left1) direction1.x -= paddleSpeed;
            if (right1) direction1.x += paddleSpeed;
            paddle1.move(direction1);

            //Mouvement du paddle2
            Vector3f direction2 = new Vector3f(0, 0, 0);
            if (up2) direction2.z -= paddleSpeed;
            if (down2) direction2.z += paddleSpeed;
            if (left2) direction2.x -= paddleSpeed;
            if (right2) direction2.x += paddleSpeed;
            paddle2.move(direction2);

            System.out.println("paddle1 Y : " + paddle1.getRigidBodyPaddle().getPhysicsLocation().y);
            System.out.println("paddle2 Z : " + paddle2.getRigidBodyPaddle().getPhysicsLocation().z);
            System.out.println("puck Y    : " + puck.getBody().getPhysicsLocation().y);

        }
    }