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
        long timeElapsed = 0;
        
        while (!glfwWindowShouldClose(Game.getCurrentWindow().getHandle())) {
            ArrayList<GameObject> objects = Game.getCurrentWindow().getObjects();
            for (GameObject object : objects) {
                object.rotate(0.0f, 1.0f, 0.0f);
            }
            
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
            
            if (RacingGame.isKeyPressed(GLFW_KEY_LEFT)) {
                RacingGame.rectangle.translate(0.1f, 0.0f, 0.0f);
            }
            
            startTime = System.nanoTime();
        }
    }
    
    private void input() {
    
    }
}
