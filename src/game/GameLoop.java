package game;

import engine.Game;
import engine.GameObject;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class GameLoop implements Runnable {
    public void run() {
        loop();
    }
    
    private void loop() {
        long startTime   = System.nanoTime();
        long timeElapsed;
        
        while (RacingGame.gameIsRunning) {
            RacingGame.cube.rotate(0.0f, 0.3f, 0.0f);
            //Game.getCurrentCamera().rotate();
            
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
        if (isKeyPressed(GLFW_KEY_W)) {
            Game.getCurrentCamera().translate(0.0f, 0.0f, 0.01f);
        }
        if (isKeyPressed(GLFW_KEY_A)) {
            Game.getCurrentCamera().translate(0.01f, 0.0f, 0.0f);
        }
        if (isKeyPressed(GLFW_KEY_S)) {
            Game.getCurrentCamera().translate(0.00f, 0.0f, -0.01f);
        }
        if (isKeyPressed(GLFW_KEY_D)) {
            Game.getCurrentCamera().translate(-0.01f, 0.0f, 0.0f);
        }
    }
    
    static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(Game.getCurrentWindow().getHandle(), keyCode) == GLFW_PRESS;
    }
}
