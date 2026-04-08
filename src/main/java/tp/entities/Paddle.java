package tp.entities;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Paddle {

    public RigidBodyControl rigidBodyPaddle;

    public Paddle(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, Vector3f initialPosition) {
        Spatial model = assetManager.loadModel("Models/paddle/Paddle.gltf");
        model.scale(0.003f);
        //model.setLocalTranslation(initialPosition);
        rootNode.attachChild(model);

        // Collisions pour les raquettes
        CylinderCollisionShape paddleShape = new CylinderCollisionShape(new Vector3f(0.19f, 0.05f, 0.f), 1);
        rigidBodyPaddle = new RigidBodyControl(paddleShape, 100f); // masse 1 pour un objet dynamique
        model.addControl(rigidBodyPaddle);
        bulletAppState.getPhysicsSpace().add(rigidBodyPaddle);

        rigidBodyPaddle.setPhysicsLocation(initialPosition);


    }

    public void move(Vector3f velocite) {
        velocite.y = 0; // Y=0 pour ne pas faire bouger la raquette en hauteur
        rigidBodyPaddle.setLinearVelocity(velocite);

        // On supprimer les rotation
        rigidBodyPaddle.setAngularVelocity(Vector3f.ZERO);
    }
    public RigidBodyControl getRigidBodyPaddle() {
        return rigidBodyPaddle;
    }
}
