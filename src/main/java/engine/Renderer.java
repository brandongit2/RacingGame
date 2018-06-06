package engine;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer {
    public Renderer(String name) {
        Game.createRenderer(name, this);
        Game.setCurrentRenderer(name);
    }
    
    public void render(HashMap<String, GameObject> objects) {
        Window window = Game.getCurrentWindow();
        Camera camera = Game.getCurrentCamera();
        
        if (window.hasBeenResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
        }
        
        glClearColor(0.0f, 0.6f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        for (Map.Entry<String, GameObject> entry : objects.entrySet()) {
            GameObject object = entry.getValue();
            
            glUseProgram(object.getShaderProgram().getProgramId());
            
            if (window.hasBeenResized()) {
                object.getShaderProgram().setUniform("projectionMatrix", camera.getProjectionMatrix());
            }
    
            object.getShaderProgram().setUniform("viewMatrix", camera.getViewMatrix());
            
            object.render();
        }
    
        glfwSwapBuffers(glfwGetCurrentContext());
    }
}
