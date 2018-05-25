package game;

import engine.*;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class RacingGame {
    public static volatile boolean gameIsRunning = true;
    
    private void run() {
        Util.init();
        
        Window window = new Window(1600, 900, "Racing Game", "mainWindow");
        Camera camera = new Camera(
          new Vector3f(
            0, 0, 20
          ), new Vector3f(
          0, 0, 0
        ), 60.0f, "primaryCamera"
        );
        Renderer renderer = new Renderer("mainRenderer");
        
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
        }));
        
        //        window.addObject(new GameObject(
        //                new Vector3f(0f, -1f, 0f),
        //                new Vector3f(0f, 0f, 0f),
        //                new Vector3f(0f, 0f, 0f),
        //                new float[]{
        //                        -20f, 0f, 20f,
        //                        -20f, 0f, -20f,
        //                        20f, 0f, -20f,
        //                        20f, 0f, 20f
        //                }, new int[]{
        //                0, 1, 2,
        //                2, 3, 0
        //        }
        //        ));
        
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
    
    private void generateFloor() {
        Game.getCurrentWindow().addObject(new GameObject(
          new Vector3f(0f, -2f, 0f),
          new Vector3f(0f, 0f, 0f),
          new Vector3f(0f, 0f, 0f),
          new float[] {
            -20f, 20f, 0f,
            -20f, -20f, 0f,
            20f, -20f, 0f,
            20f, 20f, 0f
          }, "res/floor.png",
          new float[] {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f
          }, new int[] {
          0, 1, 2,
          2, 3, 0
        }
        ));
    }
    
    static boolean isKeyPressed(int keyCode) {
        return glfwGetKey(Game.getCurrentWindow().getHandle(), keyCode) == GLFW_PRESS;
    }
    
    public static void main(String[] args) {
        new RacingGame().run();
    }
}
