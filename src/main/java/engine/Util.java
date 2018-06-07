package engine;

import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.*;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.glfwInit;

public class Util {
    /**
     * Initializes GLFW and OpenGL.
     */
    public static void init() {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        // Set up a GLFW error callback. Prints to System.err.
        GLFWErrorCallback.createPrint(System.err).set();
    }
    
    static String loadResource(String fileName) {
        String result = "";
        try (InputStream in = Util.class.getResourceAsStream(fileName);) {
            Scanner sc = new Scanner(in, "UTF-8");
            result = sc.useDelimiter("\\A").next();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        return result;
    }
}
