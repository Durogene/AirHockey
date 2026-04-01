package tp;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

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

        // Vue en plongé pour le mode multi

        /*cam.setLocation(new Vector3f(0f, 8f, 8f));
        Vector3f position = new Vector3f(0f,0f,0f);
        cam.lookAt(position, Vector3f.UNIT_Y);*/

        // Vue pour le mode solo (ou on regarde son vis à vis)
        // à Adapter pour l'ergonomie
        cam.setLocation(new Vector3f(-5f, 7f, 0f));
        Vector3f position = new Vector3f(3f,0f,0f);
        cam.lookAt(position, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10f);
        flyCam.setEnabled(false);

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
