package engine;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
    private float fov;
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR  = 1000.0f;
    private Matrix4f projectionMatrix;
    int screenWidth;
    int screenHeight;
    private HashMap<String, Integer> uniforms = new HashMap<>();
    private static ArrayList<Integer> textureIds = new ArrayList<>();
    
    /**
     * Sets up variables for a renderer.
     * @param fov The FOV (in degrees).
     */
    public Renderer(float fov) {
        this.fov = (float) Math.toRadians(fov);
        changeProjectionMatrix(screenWidth, screenHeight);
    }
    
    public void changeProjectionMatrix(int width, int height) {
        float aspectRatio = (float) width / height;
        projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, Z_NEAR, Z_FAR);
    }
    
    public void loadTexture(String filePath) {
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(filePath));
    
            ByteBuffer textureBuffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(textureBuffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            textureBuffer.flip();
            
            int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            textureIds.add(textureId);
            
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, textureBuffer);
            glGenerateMipmap(GL_TEXTURE_2D);
            
            setUniform("textureSampler", 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void render(ArrayList<GameObject> objects, int width, int height, boolean hasBeenResized) {
        if (hasBeenResized) {
            glViewport(0, 0, width, height);
            
            setUniform("projectionMatrix", projectionMatrix);
        }
    
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        for (GameObject object : objects) {
            glBindVertexArray(object.getVertexVaoId());
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureIds.get(0));
            glDrawElements(GL_TRIANGLES, object.getVertexCount(), GL_UNSIGNED_INT, 0);
            
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
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
    
    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer uniformBuffer = stack.mallocFloat(16);
            value.get(uniformBuffer);
            glUniformMatrix4fv(uniforms.get(name), false, uniformBuffer);
        }
    }
    
    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }
    
    public static void cleanUp() {
        for (int texture : textureIds) {
            glDeleteTextures(texture);
        }
    }
}
