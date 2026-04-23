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

    // Limite de Zone de jeu des joueurs ( chaque joueurs peut se déplacer dans la moitié de son terrain)
    // Il y a un changememnt en X et Z si on change de vue (ça fonctionne que pour la vue multijoueur)
    public float minX, maxX; // (de -1.15 à 1.15, 0 est le centre en X)
    public float minZ=-0.50f,  maxZ=0.50f; // (de -0.50 à 0.50) (Pour les 2 puck)


    public Paddle(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, Vector3f initialPosition,float minX, float maxX) {
        Spatial model = assetManager.loadModel("Models/paddle/Paddle.gltf");
        model.scale(0.0023f);
        //model.setLocalTranslation(initialPosition);
        rootNode.attachChild(model);

        // Collisions pour les raquettes
        CylinderCollisionShape paddleShape = new CylinderCollisionShape(new Vector3f(0.12f, 0.05f, 0.f), 1);
        rigidBodyPaddle = new RigidBodyControl(paddleShape, 100f); // masse 1 pour un objet dynamique
        model.addControl(rigidBodyPaddle);
        bulletAppState.getPhysicsSpace().add(rigidBodyPaddle);

        rigidBodyPaddle.setPhysicsLocation(initialPosition);

        rigidBodyPaddle.setGravity(Vector3f.ZERO);
        rigidBodyPaddle.setAngularFactor(0f);
        rigidBodyPaddle.setDamping(0.9f, 1.0f);

        this.minX=minX;
        this.maxX=maxX;


    }

    public void move(Vector3f velocite) {
        velocite.y = 0; // Y=0 pour ne pas faire bouger la raquette en hauteur
        Vector3f pos = rigidBodyPaddle.getPhysicsLocation();
        // On creer les zones de jeu pour les joueurs
        // joueur 1 est entre -1.15 et -0.575
        // joueur 2 est entre 0.575 et 1.15

        if(pos.x<=minX && velocite.x<0){
            velocite.x=0;
        }
        if(pos.x>=maxX && velocite.x>0){
            velocite.x=0;
        }

        if(pos.z<=minZ && velocite.z<0){
            velocite.z=0;
        }
        if(pos.z>=maxZ && velocite.z>0){
            velocite.z=0;
        }

        rigidBodyPaddle.setLinearVelocity(velocite);

        // On supprimer les rotation
        rigidBodyPaddle.setAngularVelocity(Vector3f.ZERO);
    }
    public RigidBodyControl getRigidBodyPaddle() {
        return rigidBodyPaddle;
    }
}
