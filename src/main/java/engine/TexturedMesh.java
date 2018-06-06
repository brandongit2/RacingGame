package engine;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TexturedMesh extends Mesh {
    private int textureId;
    
    public TexturedMesh(float[] vertices, String textureLocation, float[] textureCoords, int[] indices, GameObject parentObject) {
        super(vertices, indices, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), parentObject);
    
        FloatBuffer textureCoordsBuffer;
        int         textureCoordsVboId;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create vertex buffer.
            textureCoordsBuffer = stack.callocFloat(textureCoords.length);
            textureCoordsBuffer.put(textureCoords).flip();
        
            // Create vertex VBO.
            textureCoordsVboId = glGenBuffers();
            vboIds.add(textureCoordsVboId);
        }
    
        glBindBuffer(GL_ARRAY_BUFFER, textureCoordsVboId);
        glBufferData(GL_ARRAY_BUFFER, textureCoordsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
    
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    
        textureId = loadTexture(textureLocation);
    }
    
    public int loadTexture(String filePath) {
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(filePath));
            
            ByteBuffer textureBuffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(textureBuffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            textureBuffer.flip();
            
            int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, textureBuffer);
            glGenerateMipmap(GL_TEXTURE_2D);
            
            parentObject.getShaderProgram().setUniform("textureSampler", 0);
            
            return textureId;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return -1;
        }
    }
    
    @Override
    protected void beginRender() {
        super.beginRender();
    
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }
    
    @Override
    protected void endRender() {
        super.endRender();
    }
    
    @Override
    void render() {
        parentObject.getShaderProgram().setUniform("isTextured", GL_TRUE);
        
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
    }
}
