package game;

import engine.Game;
import org.joml.Vector3f;

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
            Game.getCurrentWindow().getObjects().get("cube").rotate(0.0f, 0.3f, 0.0f);
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
        float orientationX = Game.getCurrentCamera().getOrientation().x;
        
        if (isKeyPressed(GLFW_KEY_W)) {
            Game.getCurrentCamera().translate((float) Math.sin(Math.toRadians(orientationX)) / -40, 0.0f, (float) Math.cos(Math.toRadians(orientationX)) / 40);
        }
        if (isKeyPressed(GLFW_KEY_A)) {
            Game.getCurrentCamera().translate((float) Math.cos(Math.toRadians(orientationX)) / 40, 0.0f, (float) Math.sin(Math.toRadians(orientationX)) / 40);
        }
        if (isKeyPressed(GLFW_KEY_S)) {
            Game.getCurrentCamera().translate((float) Math.sin(Math.toRadians(orientationX)) / 40, 0.0f, (float) Math.cos(Math.toRadians(orientationX)) / -40);
        }
        if (isKeyPressed(GLFW_KEY_D)) {
            Game.getCurrentCamera().translate((float) Math.cos(Math.toRadians(orientationX)) / -40, 0.0f, (float) Math.sin(Math.toRadians(orientationX)) / -40);
        }
    }
    
    static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(Game.getCurrentWindow().getHandle(), keyCode) == GLFW_PRESS;
    }
}
