package engine;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    private static HashMap<String, Window>   windows        = new HashMap<>();
    private static HashMap<String, Renderer> renderers      = new HashMap<>();
    private static HashMap<String, Camera>   cameras        = new HashMap<>();
    private static ArrayList<ShaderProgram>  shaderPrograms = new ArrayList<>();
    private static String                    currentWindow;
    private static String                    currentRenderer;
    private static String                    currentCamera;
    
    static void createWindow (String name, Window window) {
        windows.put(name, window);
    }
    
    public static Window getWindow(String id) {
        return windows.get(id);
    }
    
    public static Window getCurrentWindow() {
        return windows.get(currentWindow);
    }
    
    public static void setCurrentWindow(String newWindow) {
        currentWindow = newWindow;
    }
    
    static void createRenderer(String name, Renderer renderer) {
        renderers.put(name, renderer);
    }
    
    public static Renderer getRenderer(String id) {
        return renderers.get(id);
    }
    
    public static Renderer getCurrentRenderer() {
        return renderers.get(currentRenderer);
    }
    
    public static void setCurrentRenderer(String newRenderer) {
        currentRenderer = newRenderer;
    }
    
    static void createCamera(String name, Camera camera) {
        cameras.put(name, camera);
    }
    
    public static Camera getCamera(String id) {
        return cameras.get(id);
    }
    
    public static Camera getCurrentCamera() {
        return cameras.get(currentCamera);
    }
    
    public static void setCurrentCamera(String newCamera) {
        currentCamera = newCamera;
    }
    
    public static void addShader(ShaderProgram program) {
        shaderPrograms.add(program);
    }
    
    public static ArrayList<ShaderProgram> getShaderPrograms() {
        return shaderPrograms;
    }
}
