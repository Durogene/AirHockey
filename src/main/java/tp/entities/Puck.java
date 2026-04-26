package tp.entities;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Puck {

    private final RigidBodyControl rigidBody;
    private final float posy;

    public Puck(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, Vector3f position) {
        Spatial spatial = assetManager.loadModel("Models/puck/scene.gltf");
        spatial.scale(0.02f);
        rootNode.attachChild(spatial);
        this.posy=position.y;
        // Dans Puck.java
        Material mat = assetManager.loadMaterial("Puck.j3m");
        spatial.setMaterial(mat);

        // Cylindre plat = forme réelle d'un palet + la masse
        CylinderCollisionShape shape = new CylinderCollisionShape(new Vector3f(0.1f, 0.07f, 0.1f), 1);
        rigidBody = new RigidBodyControl(shape, 100f);
        spatial.addControl(rigidBody);
        bulletAppState.getPhysicsSpace().add(rigidBody);

        rigidBody.setPhysicsLocation(position);

        // Simule le coussin d'air : presque pas de friction, rebond élastique
        rigidBody.setFriction(0.01f);
        //rigidBody.setRestitution(0.95f);
        rigidBody.setRestitution(1.0f); // Lors d'une collision on conserve l'energie pour que le puck ne soit pas immobile

        rigidBody.setAngularFactor(0f);
        rigidBody.setDamping(0f, 1.0f);

    }

    public void update(){
        Vector3f velocite= rigidBody.getLinearVelocity();
        rigidBody.setLinearVelocity(new Vector3f(velocite.x, 0f, velocite.z));
        rigidBody.setAngularVelocity(Vector3f.ZERO);

        float speed = new Vector3f(velocite.x, 0f, velocite.z).length();
        if (speed > 0.001f && speed < 0.3f) {
            rigidBody.setLinearVelocity(new Vector3f(velocite.x, 0f, velocite.z).normalize().mult(0.5f));
        }
    }

    public RigidBodyControl getBody() {
        return rigidBody;
    }

    private float scale = 1.0f;

    public void multiplierEchelle(float facteur) {
        scale *= facteur;
        rigidBody.getSpatial().setLocalScale(0.02f * scale);
    }

    public void multiplierVitesse(float facteur) {
        rigidBody.setLinearVelocity(rigidBody.getLinearVelocity().mult(facteur));
    }

}