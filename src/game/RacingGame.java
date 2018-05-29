package game;

import engine.*;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static game.Shaders.PLAIN_SHADER;
import static org.lwjgl.glfw.GLFW.*;

public class RacingGame {
    private double  prevMouseX = 0;
    private double  prevMouseY = 0;
    private boolean firstCall  = true;
    
    static volatile boolean gameIsRunning = true;
    
    static GameObject cube;
    
    private void run() {
        Util.init();
        
        Window window = new Window(1000, 900, "Racing Game", "mainWindow");
        new Shaders();
        Camera camera = new Camera(
          new Vector3f(
            0, 0, 0
          ), new Vector3f(
          0, 0, 0
        ), 60.0f, "primaryCamera"
        );
        Renderer renderer = new Renderer("mainRenderer");
        
        glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(window.getHandle(), (long evWindow, double mouseX, double mouseY) -> {
            if (firstCall) {
                firstCall = false;
            } else {
                camera.rotate((float) (mouseX - prevMouseX) / 10, (float) (mouseY - prevMouseY) / 10, 0.0f);
            }
            prevMouseX = mouseX;
            prevMouseY = mouseY;
        });
        
        glfwSetKeyCallback(window.getHandle(), (long receiver, int key, int scanCode, int action, int mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(receiver, true);
            }
        });
        
        // Creates a cube.
        window.addObject(new GameObject(
          new Vector3f(
            0f, 0f, -3f
          ), new Vector3f(
          1f, 1f, 1f
        ), new Vector3f(
          0f, 0f, 0f
        ), new float[] {
          -0.5f, 0.5f, 0.5f,
          -0.5f, -0.5f, 0.5f,
          0.5f, -0.5f, 0.5f,
          0.5f, 0.5f, 0.5f,
          -0.5f, 0.5f, -0.5f,
          0.5f, 0.5f, -0.5f,
          -0.5f, -0.5f, -0.5f,
          0.5f, -0.5f, -0.5f
        }, "res/emoji.png",
          new float[] {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
          }, new int[] {
          0, 1, 3, 3, 1, 2,
          4, 0, 3, 5, 4, 3,
          3, 2, 7, 5, 3, 7,
          6, 1, 0, 6, 0, 4,
          2, 1, 6, 2, 6, 7,
          7, 6, 4, 7, 4, 5
        }, PLAIN_SHADER), "cube");
        
        window.addObject(new GameObject(
          new Vector3f(0f, -1f, -1f),
          new Vector3f(1f, 1f, 1f),
          new Vector3f(0f, 0f, 0f),
          new float[] {
            -5f, 0f, -5f,
            -5f, 0f, 5f,
            5f, 0f, -5f,
            5f, 0f, 5f
          }, "res/floor.png",
          new float[] {
            0.0f, 0.0f,
            0.0f, 20f,
            20f, 0.0f,
            20f, 20f
          }, new int[] {
          0, 1, 2,
          2, 1, 3
        }, PLAIN_SHADER), "floor");
        
        window.addObject(new GameObject(
          new Vector3f(-4f, 1f, 0f),
          new Vector3f(1f, 1f, 1f),
          new Vector3f(0f, 90f, 0f),
          new float[] {
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f
          }, new int[] {
          0, 1, 3,
          3, 1, 2
        },
          new Vector4f(1.0f, 1.0f, 0.0f, 1.0f),
          PLAIN_SHADER), "wall");
        
        Thread gameLoop = new Thread(new GameLoop(), "gameLoopThread");
        gameLoop.start();
        
        renderLoop();
        
        gameIsRunning = false;
        GameObject.delete();
        window.cleanUp();
    }
    
    private void renderLoop() {
        while (!glfwWindowShouldClose(Game.getCurrentWindow().getHandle())) {
            Game.getCurrentWindow().render();
            
            glfwPollEvents();
        }
    }
    
    public static void main(String[] args) {
        new RacingGame().run();
    }
}
