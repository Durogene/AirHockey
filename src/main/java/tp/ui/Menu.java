package tp.ui;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import tp.AirHockeyApp;

public class Menu {

    private SimpleApplication app;
    private boolean gameStarted = false;
    private boolean isMultiplayer = false;
    private BitmapText instructions;

    public Menu(SimpleApplication app) {
        this.app = app;
    }

    public void initialiserMenu() {
        // 1. Bloquer la caméra pour qu'elle ne bouge plus pendant le menu
        app.getFlyByCamera().setEnabled(false);
        app.getInputManager().setCursorVisible(true);

        // 2. Charger la police
        BitmapFont guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");

        // 3. Créer le texte
        instructions = new BitmapText(guiFont, false);
        instructions.setSize(30);
        instructions.setText("CHOISISSEZ LE MODE :\n1 - SOLO\n2 - DUO");
        instructions.setColor(ColorRGBA.White);

        // 4. Centrage dynamique (Parfait peu importe la résolution)

        float screenWidth = app.getContext().getSettings().getWidth();
        float screenHeight = app.getContext().getSettings().getHeight();

        // Largeur du texte pour le centrer horizontalement
        float textWidth = instructions.getLineWidth();
        // Hauteur du texte pour le centrer verticalement
        float textHeight = instructions.getLineHeight();

        instructions.setLocalTranslation(
                (screenWidth / 2) - (textWidth / 2),
                (screenHeight / 2) + (textHeight),
                0
        );

        // 5. Attacher au guiNode (Interface 2D fixe)
        app.getGuiNode().attachChild(instructions);

        // 6. Configurer les touches
        initKeys();
    }

    private void initKeys() {
        // On nettoie les anciens mappings pour éviter les conflits
        if (app.getInputManager().hasMapping("ChoixSolo")) {
            app.getInputManager().deleteMapping("ChoixSolo");
            app.getInputManager().deleteMapping("ChoixDuo");
        }

        app.getInputManager().addMapping("ChoixSolo", new KeyTrigger(KeyInput.KEY_1));
        app.getInputManager().addMapping("ChoixDuo", new KeyTrigger(KeyInput.KEY_2));
        app.getInputManager().addListener(actionListener, "ChoixSolo", "ChoixDuo");
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            // On déclenche l'action quand on RELÂCHE la touche (keyPressed == false)
            if (!gameStarted && !keyPressed) {
                if (name.equals("ChoixSolo")) {
                    isMultiplayer = false;
                    lancerLeJeu();
                } else if (name.equals("ChoixDuo")) {
                    isMultiplayer = true;
                    lancerLeJeu();
                }
            }
        }
    };

    private void lancerLeJeu() {
        gameStarted = true;

        // Nettoyer l'écran
        app.getGuiNode().detachAllChildren();

        // Retirer les écouteurs du menu pour ne pas interférer avec le jeu
        app.getInputManager().removeListener(actionListener);

        ((AirHockeyApp)app).demarrerLeJeu(isMultiplayer);



        System.out.println("Jeu lancé ! Mode Multiplayer : " + isMultiplayer);


    }

}