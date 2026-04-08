package tp;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Table {

    private AssetManager assetManager;
    private Node rootNode;
    private BulletAppState bulletAppState;

    public Table(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.bulletAppState = bulletAppState;
    }

    public void load() {
        // Charger le modèle de la table
        Spatial model = assetManager.loadModel("Models/air_hockey_table/scene.gltf");
        model.scale(1f);
        model.setLocalTranslation(0f, 0f, 0f);
        rootNode.attachChild(model);

        // Ajouter les collisions pour la table
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(model);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0); // masse 0 pour un objet statique
        model.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);

        // Intensité de la lumière qui influe également sur la couleur du plateau
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3));
        sun.setColor(ColorRGBA.White.mult(10f));
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(3f));
        rootNode.addLight(ambient);




    }
}
