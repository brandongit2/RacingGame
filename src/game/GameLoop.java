package game;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class GameLoop implements Runnable {
    public void run() {
        loop();
    }
    
    private void loop() {
        long startTime   = System.nanoTime();
        long timeElapsed = 0;
        
        while (!glfwWindowShouldClose(RacingGame.window.handle)) {
            timeElapsed = startTime - System.nanoTime();
            if (timeElapsed < 1e7) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            startTime = System.nanoTime();
        }
    }
}
