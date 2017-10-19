package Main;

import Graphics.GUI;
import org.lwjgl.opengl.Display;

public class Boot {


    private Boot() {
        GUI.init();
        // Main game loop
        while (!Display.isCloseRequested()) {
            Game.INSTANCE.update();
            Clock.INSTANCE.update();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        System.exit(0);
    }

    public static void main(String[] args) {
        new Boot();
    }
}