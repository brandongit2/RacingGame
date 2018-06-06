package game;

import engine.*;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import static game.Shaders.PLAIN_SHADER;
import static org.lwjgl.glfw.GLFW.*;

class RacingGame {
    private boolean firstCall = true;
    
    static volatile boolean gameIsRunning = true;
    
    static GameObject cube;
    
    private void run() {
        Util.init();
        
        new Window(1000, 900, "Racing Game", "mainWindow");
        new Shaders();
        new Camera(
          new Vector3f(0, 0, 0),
          new Vector3f(0, 0, 0),
          60.0f,
          "primaryCamera"
        );
        new Renderer("mainRenderer");
        
        glfwSetInputMode(Game.getWindow("mainWindow").getHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED); // Disable the cursor.
        glfwSetCursorPosCallback(Game.getWindow("mainWindow").getHandle(), new GLFWCursorPosCallbackI() {
            double prevMouseX = 0;
            double prevMouseY = 0;
            
            @Override
            public void invoke(long evWindow, double mouseX, double mouseY) {
                if (firstCall) {
                    firstCall = false;
                } else {
                    Game.getCamera("primaryCamera").rotate((float) (mouseX - prevMouseX) / 10, (float) (mouseY - prevMouseY) / 10, 0.0f);
                }
                
                prevMouseX = mouseX;
                prevMouseY = mouseY;
            }
        });
        
        glfwSetKeyCallback(Game.getWindow("mainWindow").getHandle(), (long receiver, int key, int scanCode, int action, int mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(receiver, true);
            }
        });
        
        // Creates a cube.
        // @formatter:off
        Game.getWindow("mainWindow").addObject(new GameObject(
          new Vector3f(0f, 0f, -3f),
          new Vector3f(1f, 3f,  1f),
          new Vector3f(0f, 0f,  0f),
          new float[] {
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,
             0.5f, -0.5f,  0.5f,
             0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f, -0.5f,
             0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
             0.5f, -0.5f, -0.5f
          }, "src/main/resources/emoji.png",
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
          }, PLAIN_SHADER),
        "cube");
        // @formatter:on
        
        // @formatter:off
        Game.getWindow("mainWindow").addObject(
          new GameObject(
            new Vector3f(0f, -1f, 0f),
            new Vector3f(1f,  1f, 1f),
            new Vector3f(0f,  0f, 0f),
            new float[] {
              -5f, 0f, -5f,
              -5f, 0f,  5f,
               5f, 0f, -5f,
               5f, 0f,  5f
            }, "src/main/resources/floor.png",
            new float[] {
              0.0f, 0.0f,
              0.0f,  20f,
               20f, 0.0f,
               20f,  20f
            }, new int[] {
              0, 1, 2,
              2, 1, 3
            }, PLAIN_SHADER
          ), "floor"
        );
        // @formatter:on
        
        // @formatter:off
        Game.getWindow("mainWindow").addObject(
          new GameObject(
            new Vector3f(-4f, -1f, 0f),
            new Vector3f( 1f,  1f, 1f),
            new Vector3f(90f,  0f, 0f),
            new float[] {
              -5f, 0f, -5f,
              -5f, 0f,  5f,
               5f, 0f, -5f,
               5f, 0f,  5f
            }, "src/main/resrouces/floor.png",
            new float[] {
              0.0f, 0.0f,
              0.0f,  20f,
               20f, 0.0f,
               20f,  20f
            }, new int[] {
              0, 1, 2,
              2, 1, 3
            }, PLAIN_SHADER
          ), "floor2"
        );
        // @formatter:on
        
        // @formatter:off
        Game.getWindow("mainWindow").addObject(
          new GameObject(
            new Vector3f(-4f,  1f, 0f),
            new Vector3f( 1f,  1f, 1f),
            new Vector3f( 0f, 90f, 0f),
            new float[] {
              -1f,  1f, 0f,
              -1f, -1f, 0f,
               1f, -1f, 0f,
               1f,  1f, 0f
            }, new int[] {
              0, 1, 3,
              3, 1, 2
            }, new Vector4f(1.0f, 1.0f, 0.0f, 1.0f),
            PLAIN_SHADER
          ),"wall"
        );
        // @formatter:on
        
        // @formatter:off
        Game.getWindow("mainWindow").addObject(
          new GameObject(
            new Vector3f(0, 0, 0),
            new Vector3f(1, 1, 1),
            new Vector3f(0, 0, 0),
            "src/main/resources/car.obj",
            PLAIN_SHADER
          ), "car"
        );
        // @formatter:on
        
        Thread gameLoop = new Thread(new GameLoop(), "gameLoopThread");
        gameLoop.start();
        
        renderLoop();
        
        gameIsRunning = false;
        Game.getWindow("mainWindow").cleanUp();
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
