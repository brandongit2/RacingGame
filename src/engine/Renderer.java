package engine;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            object.getShaderProgram().setUniform("isTextured", object.isTextured() ? GL_TRUE : GL_FALSE);
            
            if (window.hasBeenResized()) {
                object.getShaderProgram().setUniform("projectionMatrix", camera.getProjectionMatrix());
            }
            
            object.getShaderProgram().setUniform("viewMatrix", camera.getViewMatrix());
            
            glBindVertexArray(object.getVertexVaoId());
            for (int i = 0; i < object.getShaderProgram().getNumAttribArrays(); i++) {
                glEnableVertexAttribArray(i);
            }

            object.getShaderProgram().setUniform("modelMatrix", object.getModelMatrix());

            if (object.isTextured()) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, object.getTextureId());
            }
            
            glDrawElements(GL_TRIANGLES, object.getVertexCount(), GL_UNSIGNED_INT, 0);
    
            for (int i = 0; i < object.getShaderProgram().getNumAttribArrays(); i++) {
                glDisableVertexAttribArray(i);
            }
            glBindVertexArray(0);
        }
    
        glfwSwapBuffers(glfwGetCurrentContext());
    }
}
