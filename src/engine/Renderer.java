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
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Renderer {
    private static HashMap<String, Integer> uniforms   = new HashMap<>();
    
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
            if (object.isTextured()) {
                System.out.println("yaaaaah");
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
            } else {
                glBindVertexArray(object.getVertexVaoId());
                glEnableVertexAttribArray(0);

                setUniform("modelMatrix", object.getModelMatrix());

                glDrawElements(GL_TRIANGLES, object.getVertexCount(), GL_UNSIGNED_INT, 0);

                glDisableVertexAttribArray(0);
                glBindVertexArray(0);
            }
        }
        
        glfwSwapBuffers(glfwGetCurrentContext());
    }
    
    public void createUniform(String name, int programId) throws RuntimeException {
        int uniformLocation = glGetUniformLocation(programId, name);
        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform " + name);
        }
        uniforms.put(name, uniformLocation);
    }
    
    void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer uniformBuffer = stack.mallocFloat(16);
            value.get(uniformBuffer);
            glUniformMatrix4fv(uniforms.get(name), false, uniformBuffer);
        }
    }
    
    void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }
}
