package tp.entities;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

public class PowerUp {

    public enum Type {
        SPEED_PLUS, SIZE_PLUS, SIZE_MINUS, SHOT_ON_GOAL, PADDLE_PLUS, PADDLE_MINUS
    }

    private final Geometry geometry;
    private final Node rootNode;
    private final Type type;
    private final Vector3f position;
    private boolean active = true;
    public float tempsRestant = 0f; // durée restante de l'effet

    public PowerUp(AssetManager assetManager, Node rootNode, Type type, Vector3f position) {
        this.rootNode = rootNode;
        this.type = type;
        this.position = position;

        Cylinder cyl = new Cylinder(8, 16, 0.12f, 0.02f, true);
        geometry = new Geometry("PowerUp_" + type, cyl);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", getCouleur());
        geometry.setMaterial(mat);
        geometry.setLocalTranslation(position);
        geometry.rotate(com.jme3.math.FastMath.HALF_PI, 0, 0);
        rootNode.attachChild(geometry);
    }

    private ColorRGBA getCouleur() {
        switch (type) {
            case SPEED_PLUS:
                return ColorRGBA.Yellow;
            case SIZE_PLUS:
                return ColorRGBA.Orange;
            case SIZE_MINUS:
                return ColorRGBA.Blue;
            case SHOT_ON_GOAL:
                return ColorRGBA.Red;
            case PADDLE_PLUS:
                return ColorRGBA.Green;
            case PADDLE_MINUS:
                return ColorRGBA.Magenta;
            default:
                return ColorRGBA.White;
        }
    }

    // Applique l'effet au moment de la collecte
    public void appliquerEffet(Puck puck, Paddle paddle1, Paddle paddle2, int dernierJoueur) {
        switch (type) {
            case SPEED_PLUS:
                // +50% vitesse du puck
                Vector3f velocite = puck.getBody().getLinearVelocity();
                puck.getBody().setLinearVelocity(velocite.mult(1.5f));
                tempsRestant = 10f;
                break;

            case SIZE_PLUS:
                // +20% taille du puck (visuel uniquement via scale)
                puck.getBody().getSpatial().scale(1.2f);
                tempsRestant = 10f;
                break;

            case SIZE_MINUS:
                // -20% taille du puck
                puck.getBody().getSpatial().scale(0.8f);
                tempsRestant = 10f;
                break;

            case SHOT_ON_GOAL:
                // Le puck va vers le centre du but adverse
                float dirX = (dernierJoueur == 1) ? 1f : -1f;
                puck.getBody().setLinearVelocity(new Vector3f(dirX * 3f, 0f, 0f));
                tempsRestant = 0f; // effet immédiat, pas de durée
                break;

            case PADDLE_PLUS:
                // +10% taille de la raquette du joueur qui a collecté
                if (dernierJoueur == 1){
                    paddle1.getRigidBodyPaddle().getSpatial().scale(1.1f);
                }
                else{
                    paddle2.getRigidBodyPaddle().getSpatial().scale(1.1f);
                }
                tempsRestant = 20f;
                break;

            case PADDLE_MINUS:
                // -10% taille de la raquette adverse
                if (dernierJoueur == 1){
                    paddle2.getRigidBodyPaddle().getSpatial().scale(0.9f);
                }
                else{
                    paddle1.getRigidBodyPaddle().getSpatial().scale(0.9f);
                }
                tempsRestant = 20f;
                break;
        }
    }

    // Annule l'effet quand le timer expire
    public void mettreAJourTimer(float tpf, Puck puck, Paddle paddle1, Paddle paddle2) {
        tempsRestant -= tpf;
        if (tempsRestant <= 0) {
            annulerEffet(puck, paddle1, paddle2);
        }
    }

    // Annule l'effet (remet à l'échelle d'origine)
    private void annulerEffet(Puck puck, Paddle paddle1, Paddle paddle2) {
        switch (type) {
            case SIZE_PLUS:
                puck.getBody().getSpatial().scale(1f / 1.2f);
                break;
            case SIZE_MINUS:
                puck.getBody().getSpatial().scale(1f / 0.8f);
                break;
            case PADDLE_PLUS:
                paddle1.getRigidBodyPaddle().getSpatial().scale(1f / 1.1f);
                paddle2.getRigidBodyPaddle().getSpatial().scale(1f / 1.1f);
                break;
            case PADDLE_MINUS:
                paddle1.getRigidBodyPaddle().getSpatial().scale(1f / 0.9f);
                paddle2.getRigidBodyPaddle().getSpatial().scale(1f / 0.9f);
                break;
            default:
                break;
        }
    }

    // Prolonge la durée si le même type est déjà actif
    public void prolongerDuree(float dureeSupp) {
        tempsRestant += dureeSupp;
    }

    public void collecter() {
        active = false;
        rootNode.detachChild(geometry);
    }

    public boolean isActive() {
        return active;
    }

    public Type getType(){
        return type;
    }

    public Vector3f getPosition() {
        return position;
    }
}
