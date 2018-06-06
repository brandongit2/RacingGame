package engine;

import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    static ArrayList<Integer> vboIds = new ArrayList<>();
    static ArrayList<Integer> vaoIds = new ArrayList<>();
    
    private int vertexVaoId;
    
    protected GameObject parentObject;
    protected int        vertexCount;
    
    public Mesh(float[] vertices, int[] indices, Vector4f color, GameObject parentObject) {
        this(vertices, indices, colorToArray(color, indices), parentObject);
    }
    
    private static float[] colorToArray(Vector4f color, int[] indices) {
        float[] arr = new float[indices.length * 4];
        for (int i = 0; i < arr.length; i += 4) {
            arr[i] = color.x;
            arr[i + 1] = color.y;
            arr[i + 2] = color.z;
            arr[i + 3] = color.w;
        }
        return arr;
    }
    
    public Mesh(float[] vertices, int[] indices, float[] colors, GameObject parentObject) {
        this.parentObject = parentObject;
        vertexCount = indices.length;
        
        FloatBuffer vertexBuffer;
        int         verticesVboId;
        IntBuffer   indexBuffer;
        int         indicesVboId;
        FloatBuffer colorBuffer;
        int         colorVboId;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create vertex buffer.
            vertexBuffer = stack.callocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();
            
            // Create vertex VBO.
            verticesVboId = glGenBuffers();
            vboIds.add(verticesVboId);
            
            // Create index buffer.
            indexBuffer = stack.callocInt(indices.length);
            indexBuffer.put(indices).flip();
            
            // Create index VBO.
            indicesVboId = glGenBuffers();
            vboIds.add(indicesVboId);
            
            // Create color buffer.
            colorBuffer = stack.callocFloat(colors.length);
            colorBuffer.put(colors).flip();
            
            // Create color VBO
            colorVboId = glGenBuffers();
            vboIds.add(colorVboId);
        }
        
        // Create vertex VAO.
        vertexVaoId = glGenVertexArrays();
        glBindVertexArray(vertexVaoId);
        
        glBindBuffer(GL_ARRAY_BUFFER, verticesVboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    
    protected void beginRender() {
        glBindVertexArray(vertexVaoId);
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }
    
    protected void endRender() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        
        glBindVertexArray(0);
    }
    
    void render() {
        parentObject.getShaderProgram().setUniform("isTextured", GL_FALSE);
        
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
    }
}
