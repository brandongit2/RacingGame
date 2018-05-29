package engine;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    static         HashMap<String, Window>   windows        = new HashMap<>();
    static         HashMap<String, Renderer> renderers      = new HashMap<>();
    static         HashMap<String, Camera>   cameras        = new HashMap<>();
    static         ArrayList<ShaderProgram>  shaderPrograms = new ArrayList<>();
    private static String                    currentWindow;
    private static String                    currentRenderer;
    private static String                    currentCamera;
    
    public static void setCurrentWindow(String newWindow) {
        currentWindow = newWindow;
    }
    
    public static void setCurrentRenderer(String newRenderer) {
        currentRenderer = newRenderer;
    }
    
    public static void setCurrentCamera(String newCamera) {
        currentCamera = newCamera;
    }
    
    public static Window getWindow(String id) {
        return windows.get(id);
    }
    
    public static Window getCurrentWindow() {
        return windows.get(currentWindow);
    }
    
    public static String getCurrentWindowId() {
        return currentWindow;
    }
    
    public static Renderer getRenderer(String id) {
        return renderers.get(id);
    }
    
    public static Renderer getCurrentRenderer() {
        return renderers.get(currentRenderer);
    }
    
    public static String getCurrentRendererId() {
        return currentRenderer;
    }
    
    public static Camera getCamera(String id) {
        return cameras.get(id);
    }
    
    public static Camera getCurrentCamera() {
        return cameras.get(currentCamera);
    }
    
    public static String getCurrentCameraId() {
        return currentCamera;
    }
    
    public static void addShader(ShaderProgram program) {
        shaderPrograms.add(program);
    }
    
    public static ArrayList<ShaderProgram> getShaderPrograms() {
        return shaderPrograms;
    }
}
