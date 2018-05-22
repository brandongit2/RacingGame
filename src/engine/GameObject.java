package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class GameObject extends Entity {
    private static List<Integer>         vbos        = new ArrayList<>();
    private static List<Integer>         vaos        = new ArrayList<>();
    private        ArrayList<GameObject> gameObjects = new ArrayList<>();
    private Vector3f scale = new Vector3f(1f, 1f, 1f);
    
    private        int      vertexVaoId;
    private        int      vertexCount;
    private static Matrix4f modelMatrix = new Matrix4f();
    
    public GameObject(Vector3f pos, Vector3f scale, Vector3f rot, float[] vertices, float[] textureCoords, int[] indices) {
        modelMatrix.translate(pos.x, pos.y, pos.z)
                   .scale(scale.x, scale.y, scale.z)
                   .rotateX(rot.x)
                   .rotateY(rot.y)
                   .rotateZ(rot.z);
        
        vertexCount = indices.length;
        
        IntBuffer   indexBuffer;
        int         indicesVboId;
        FloatBuffer textureCoordBuffer;
        int         textureVboId;
        FloatBuffer vertexBuffer;
        int         vertexVboId;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create index buffer.
            indexBuffer = stack.callocInt(indices.length + 1);
            indexBuffer.put(indices).flip();
            
            // Create index VBO.
            indicesVboId = glGenBuffers();
            vbos.add(indicesVboId);
            
            // Create color buffer.
            textureCoordBuffer = stack.callocFloat(textureCoords.length);
            textureCoordBuffer.put(textureCoords).flip();
            
            // Create texture VBO.
            textureVboId = glGenBuffers();
            vbos.add(textureVboId);
            
            // Create vertex buffer.
            vertexBuffer = stack.callocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();
            
            // Create vertex VBO.
            vertexVboId = glGenBuffers();
            vbos.add(vertexVboId);
        }
        
        // Create vertex VAO.
        vertexVaoId = glGenVertexArrays();
        glBindVertexArray(vertexVaoId);
        vaos.add(vertexVaoId);
        
        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
        glBufferData(GL_ARRAY_BUFFER, textureCoordBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    
    public void translate(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
        
        modelMatrix.translate(dx, dy, dz);
    }
    
    public void rotate(float dx, float dy, float dz) {
        rotation.x += dx;
        rotation.y += dy;
        rotation.z += dz;
        rotation.x %= 360;
        rotation.y %= 360;
        rotation.z %= 360;
        System.out.println(rotation);
    
        modelMatrix.rotate((float) Math.toRadians(dz), new Vector3f(0, 0, 1));
        modelMatrix.rotate((float) Math.toRadians(dy), new Vector3f(0, 1, 0));
        modelMatrix.rotate((float) Math.toRadians(dx), new Vector3f(1, 0, 0));
    }
    
    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
    
    public int getVertexVaoId() {
        return vertexVaoId;
    }
    
    public int getVertexCount() {
        return vertexCount;
    }
    
    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }
    
    public static void delete() {
        for (int vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        
        for (int vbo : vbos) {
            glDeleteBuffers(vbo);
        }
    }
}
