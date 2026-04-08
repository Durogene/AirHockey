package tp.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class HUD {

    private final BitmapText scoreText;
    private final BitmapText messageText;

    public HUD(AssetManager assetManager, Node guiNode, Camera cam) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        scoreText = new BitmapText(font);
        scoreText.setSize(30);
        scoreText.setLocalTranslation(20, cam.getHeight() - 20, 0);
        scoreText.setText("J1 : 0 | J2 : 0");
        guiNode.attachChild(scoreText);

        messageText = new BitmapText(font);
        messageText.setSize(25);
        messageText.setLocalTranslation(20, cam.getHeight() - 60, 0);
        messageText.setText("");
        guiNode.attachChild(messageText);
    }

    public void updateScore(int score1, int score2) {
        scoreText.setText("J1 : " + score1 + " | J2 : " + score2);
    }

    public void showMessage(String message) {
        messageText.setText(message);
    }

    public void clearMessage() {
        messageText.setText("");
    }
}