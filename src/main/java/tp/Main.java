package tp;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import tp.ui.Menu;


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

        Menu monMenu = new Menu(this);
        monMenu.initialiserMenu();
        setDisplayFps(false);      // Cache les FPS (60fps)
        setDisplayStatView(false); // Cache les stats (Triangles, Vertices)


        // coordonnées :
        // x droite à gauche (quand le plateau est en mode multijoueur)
        // y en avant - en arrière
        // z en haut en bas (par raport au plateau de jeu)


        Spatial paddle1 = assetManager.loadModel("Models/paddle/Paddle.gltf");
        paddle1.scale(0.004f);
        paddle1.setLocalTranslation(-2f,0f,0f);
        rootNode.attachChild(paddle1);


        Spatial paddle2 = assetManager.loadModel("Models/paddle/Paddle.gltf");
        paddle2.scale(0.004f);
        paddle2.setLocalTranslation(3,0f,0f);
        rootNode.attachChild(paddle2);

        Spatial puck = assetManager.loadModel("Models/puck/scene.gltf");
        puck.scale(0.03f);
        puck.setLocalTranslation(0,0.7f,0f);
        rootNode.attachChild(puck);


        // Vue en plongé pour le mode multi

        cam.setLocation(new Vector3f(0f, 8f, 8f));
        Vector3f position = new Vector3f(0f,0f,0f);
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



            // 1. Désactive le mode "vol libre" (la caméra ne bougera plus)
            flyCam.setEnabled(false);

            // 2. Si vous voulez quand même voir le curseur de la souris
            inputManager.setCursorVisible(true);

            // 3. Positionnez la caméra une fois pour toutes
            cam.setLocation(new Vector3f(0, 15, 20)); // Reculez et montez la caméra
            // Orientez-la vers le centre de la table

            // ... le reste de votre code (chargement de la table, etc.)






        /*AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(3f));
        rootNode.addLight(ambient);*/


        // Intensité de la lumière qui influe également sur la couleur du plateau
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3));
        sun.setColor(ColorRGBA.White.mult(10f));
        rootNode.addLight(sun);
    }
}
