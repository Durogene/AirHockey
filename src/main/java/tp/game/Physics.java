package tp.game;

import com.jme3.math.Vector3f;
import tp.entities.Puck;

public class Physics {

    // Smash : boost 5-15% si la raquette frappe fort
    public static void appliquerSmash(Puck puck, float vitessePaddle) {
        if (vitessePaddle < 1.5f) return; // frappe trop faible
        float boost = 1.05f + Math.min(vitessePaddle / 20f, 1f) * 0.10f;
        puck.multiplierVitesse(boost);
    }

    // Lift : légère déviation si frappe en angle
    public static void appliquerLift(Puck puck, Vector3f directionPaddle) {
        Vector3f vitesse = puck.getBody().getLinearVelocity();
        if (vitesse.length() < 0.1f) return;
        float deviation = directionPaddle.z * 0.15f;
        puck.getBody().setLinearVelocity(new Vector3f(vitesse.x, 0, vitesse.z + deviation));
    }

    // Flip : réduit la vitesse de 20%
    public static void appliquerFlip(Puck puck) {
        puck.multiplierVitesse(0.8f);
    }
}
