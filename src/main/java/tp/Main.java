/*package tp;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


import com.jme3.system.AppSettings;

// collisions imports
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Air Hockey 3D");
        settings.setResolution(1280, 720);
        settings.setAudioRenderer(null); // Pas d'audio (notamment pour WSL)
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("/", ClasspathLocator.class);

        Spatial model = assetManager.loadModel("Models/air_hockey_table/scene.gltf");

        model.scale(1f);
        model.setLocalTranslation(0f,   0f, 0f);

        rootNode.attachChild(model);

        // Set up de la Physique : les raquettes et palets doivent être sur la surface de la table et ne pas la traverser
        BulletAppState bulletAppState =new BulletAppState();
        stateManager.attach(bulletAppState);

        // Ajout des collisions pour la scene :
        // ici la scene des objets sera la tableau de jeu
        CollisionShape sceneShape= CollisionShapeFactory.createMeshShape(model);
        RigidBodyControl landscape=new RigidBodyControl(sceneShape,10);
        model.addControl(landscape);

        // Ajout de la detection des collisions pour les raquettes et le palet
        CapsuleCollisionShape capsuleShape=new CapsuleCollisionShape(1.5f,6f,1);

        // coordonnées :
        // x droite à gauche (quand le plateau est en mode multijoueur)
        // y en avant - en arrière
        // z en haut en bas (par raport au plateau de jeu)

        // Spatial : structure de donnée abstraite qui stocke les transformations (translation,rotation,scale)
        Spatial paddle1 = assetManager.loadModel("Models/paddle/Paddle.gltf");
        paddle1.scale(0.004f);
        paddle1.setLocalTranslation(-2f,0f,0f);
        rootNode.attachChild(paddle1);
        //paddle1.getOverlappingObjects();
        //RigidBodyControl paddle1_mass = new RigidBodyControl(paddle1 , 123.0f );

        //Ne fonctionne pas encore
        //bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));

        bulletAppState.getPhysicsSpace().add(paddle1);


        Spatial paddle2 = assetManager.loadModel("Models/paddle/Paddle.gltf");
        paddle2.scale(0.004f);
        paddle2.setLocalTranslation(3,0f,0f);
        rootNode.attachChild(paddle2);
        bulletAppState.getPhysicsSpace().add(paddle2);

        Spatial puck = assetManager.loadModel("Models/puck/scene.gltf");
        puck.scale(0.03f);
        puck.setLocalTranslation(0,0.7f,0f);
        rootNode.attachChild(puck);
        bulletAppState.getPhysicsSpace().add(puck);

        CollisionShape itemShape= CollisionShapeFactory.createMeshShape(puck);
        RigidBodyControl landscape1=new RigidBodyControl(itemShape,10);
        puck.addControl(landscape1);
        // Vue en plongé pour le mode multi

        cam.setLocation(new Vector3f(0f, 8f, 8f));
        Vector3f position = new Vector3f(0f,0f,0f);
        cam.lookAt(position, Vector3f.UNIT_Y);

        // Vue pour le mode solo (ou on regarde son vis à vis)
        // à Adapter pour l'ergonomie
        //cam.setLocation(new Vector3f(-5f, 7f, 0f));
        //Vector3f position = new Vector3f(3f,0f,0f);
        //cam.lookAt(position, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10f);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(1f);
        flyCam.setRotationSpeed(0.1f);


    }
}*/


package tp;

import com.jme3.system.AppSettings;

public class Main {
    public static void main(String[] args) {
        AirHockeyApp app = new AirHockeyApp();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Air Hockey 3D");
        settings.setResolution(1280, 720);
        settings.setAudioRenderer(null);
        app.setSettings(settings);
        app.start();
    }
}