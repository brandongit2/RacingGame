package game;

import engine.*;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class RacingGame {
    static GameObject rectangle;
    
    private void run() {
        Util.init();
        
        Camera camera = new Camera(
          new Vector3f(
            0, 0, 0
          ), new Vector3f(
          0, 0, 0
          ), 60.0f, "primaryCamera"
        );
        Renderer renderer = new Renderer("mainRenderer");
        Window window = new Window(1600, 900, "Racing Game", "mainWindow");
        
        glfwSetKeyCallback(window.getHandle(), (long receiver, int key, int scanCode, int action, int mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(receiver, true);
            } else if (key == GLFW_KEY_W && action == GLFW_REPEAT) {
                camera.translate(0f, 0f, 0.01f);
            }
        });
        
        window.setVertexShader("/game/vertex.glsl");
        Game.getCurrentWindow().setFragmentShader("/game/fragment.glsl");
        Game.getCurrentWindow().linkShaderProgram();
        
        renderer.loadTexture("src/res/emoji.png");
        
        rectangle = new GameObject(
          new Vector3f(
            0f, 0f, -1f
          ), new Vector3f(
          1f, 1f, 1f
        ), new Vector3f(
          0f, 0f, 0f
        ), new float[]{
          -0.5f, 0.5f, -1.0f,
          -0.5f, -0.5f, -1.0f,
          0.5f, -0.5f, -1.0f,
          0.5f, 0.5f, -1.0f
        }, new float[]{
          0.0f, 0.0f,
          0.0f, 1.0f,
          1.0f, 1.0f,
          1.0f, 0.0f
        }, new int[]{
          0, 1, 3,
          3, 1, 2
        });
        window.addObject(rectangle);
    
        Thread gameLoop = new Thread(new GameLoop(), "gameLoopThread");
        gameLoop.start();
        
        renderLoop();
        
        GameObject.delete();
        window.cleanUp();
    }
    
    private void renderLoop() {
        while (!glfwWindowShouldClose(Game.getCurrentWindow().getHandle())) {
            Game.getCurrentWindow().render();
            
            glfwPollEvents();
        }
    }
    
    static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(Game.getCurrentWindow().getHandle(), keyCode) == GLFW_PRESS;
    }
    
    public static void main(String[] args) {
        new RacingGame().run();
    }
}
