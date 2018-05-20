package game;

import engine.GameObject;
import engine.Renderer;
import engine.Util;
import engine.Window;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class RacingGame {
    static  Window                window;
    private ArrayList<GameObject> gameObjects = new ArrayList<>();
    
    private void run() {
        Util.init();
        Renderer mainRenderer = new Renderer(80.0f);
        window = new Window(1600, 900, "Racing Game", mainRenderer);
        
        glfwSetKeyCallback(window.handle, (long window, int key, int scanCode, int action, int mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });
        
        window.setVertexShader("/game/vertex.glsl");
        window.setFragmentShader("/game/fragment.glsl");
        window.linkShaderProgram();
        
        mainRenderer.loadTexture("src/res/emoji.png");
    
        GameObject rectangle = new GameObject(new float[] {
          -0.5f,  0.5f, -1.0f,
          -0.5f, -0.5f, -1.0f,
           0.5f, -0.5f, -1.5f,
           0.5f,  0.5f, -1.5f
        }, new float[] {
          0.0f, 0.0f,
          0.0f, 1.0f,
          1.0f, 1.0f,
          1.0f, 0.0f
        }, new int[] {
          0, 1, 3,
          3, 1, 2
        });
        window.addObject(rectangle);
    
        Thread gameLoop = new Thread(new GameLoop());
        gameLoop.start();
        
        renderLoop();
        
        GameObject.delete();
        window.cleanUp();
    }
    
    private void renderLoop() {
        while (!glfwWindowShouldClose(window.handle)) {
            window.render();
            
            glfwPollEvents();
        }
    }
    
    public static void main(String[] args) {
        new RacingGame().run();
    }
}
