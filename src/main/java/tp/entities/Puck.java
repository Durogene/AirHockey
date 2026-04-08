package tp.entities;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Puck {

    private final RigidBodyControl rigidBody;
    private final float posy;

    public Puck(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, Vector3f position) {
        Spatial spatial = assetManager.loadModel("Models/puck/scene.gltf");
        spatial.scale(0.03f);
        rootNode.attachChild(spatial);
        this.posy=position.y;

        // Cylindre plat = forme réelle d'un palet
        CylinderCollisionShape shape = new CylinderCollisionShape(new Vector3f(0.15f, 0.03f, 0.15f), 1);
        rigidBody = new RigidBodyControl(shape, 0.1f);
        spatial.addControl(rigidBody);
        bulletAppState.getPhysicsSpace().add(rigidBody);

        rigidBody.setPhysicsLocation(position);

        // Simule le coussin d'air : presque pas de friction, rebond élastique
        rigidBody.setFriction(0.01f);
        rigidBody.setRestitution(0.95f);
    }

    public void update(){
        Vector3f pos= rigidBody.getPhysicsLocation();
        Vector3f velocite= rigidBody.getLinearVelocity();

        // Bloque Y en position et en vitesse
        if (pos.y != posy) {
            rigidBody.setPhysicsLocation(new Vector3f(pos.x, posy, pos.z));
        }
        rigidBody.setLinearVelocity(new Vector3f(velocite.x, 0f, velocite.z));
        rigidBody.setAngularVelocity(Vector3f.ZERO);
    }

    public RigidBodyControl getBody() {
        return rigidBody;
    }
}