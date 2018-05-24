package game;

import engine.Game;
import engine.GameObject;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class GameLoop implements Runnable {
    public void run() {
        loop();
    }
    
    private void loop() {
        long startTime   = System.nanoTime();
        long timeElapsed;
        
        while (RacingGame.gameIsRunning) {
            ArrayList<GameObject> objects = Game.getCurrentWindow().getObjects();
//            for (GameObject object : objects) {
//                object.rotate(0.0f, 0.3f, 0.0f);
//            }
            
            timeElapsed = startTime - System.nanoTime();
            if (timeElapsed < 1e7) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            
            input();
            
            startTime = System.nanoTime();
        }
    }
    
    private void input() {
    
    }
}
