package engine;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {
    public Renderer(String name) {
        Game.renderers.put(name, this);
        Game.setCurrentRenderer(name);
    }
    
    public void render(ArrayList<GameObject> objects) {
        Window window = Game.getCurrentWindow();
        Camera camera = Game.getCurrentCamera();
        
        if (window.hasBeenResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            
            setUniform("projectionMatrix", camera.getProjectionMatrix());
        }
        
        glClearColor(0.0f, 0.6f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        setUniform("viewMatrix", camera.getViewMatrix());
        
        for (GameObject object : objects) {
            glBindVertexArray(object.getVertexVaoId());
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            setUniform("modelMatrix", object.getModelMatrix());

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, object.getTextureId());
            glDrawElements(GL_TRIANGLES, object.getVertexCount(), GL_UNSIGNED_INT, 0);

            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
        }
    
        glfwSwapBuffers(glfwGetCurrentContext());
    }
}
