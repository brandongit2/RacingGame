package engine;

import java.util.HashMap;

public class Game {
    protected static HashMap<String, Window>   windows         = new HashMap<>();
    protected static HashMap<String, Renderer> renderers       = new HashMap<>();
    protected static HashMap<String, Camera>   cameras         = new HashMap<>();
    protected static String                       currentWindow;
    protected static String                       currentRenderer;
    protected static String                       currentCamera;
    
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
}
