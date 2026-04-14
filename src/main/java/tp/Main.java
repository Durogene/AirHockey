package tp;

import com.jme3.system.AppSettings;

public class Main {
    public static void main(String[] args) {
        AirHockeyApp app = new AirHockeyApp();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Air Hockey 3D");
        settings.setResolution(1280, 720);
        settings.setAudioRenderer(null);
        app.setSettings(settings);
        app.start();
    }
}