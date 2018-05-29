package engine;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class GameObject extends Entity {
    private static ArrayList<Integer>    vbos        = new ArrayList<>();
    private static ArrayList<Integer>    vaos        = new ArrayList<>();
    private        ArrayList<GameObject> gameObjects = new ArrayList<>();
    private        Vector3f              scale       = new Vector3f(1f, 1f, 1f);
    private        int                   textureId;
    private        int                   vertexVaoId;
    private        int                   vertexCount;
    private        Matrix4f              modelMatrix = new Matrix4f();
    private        ShaderProgram         shaderProgram;
    private boolean isTextured;
    
    /**
     * Creates a plain, untextured GameObject.
     *
     * @param pos           The position of the GameObject.
     * @param scale         The scale of the GameObject.
     * @param rot           The rotation of the GameObject.
     * @param vertices      The vertices of the GameObject.
     * @param indices       The indices for the GameObject data.
     * @param color         The color of the GameObject.
     * @param shaderProgram The ShaderProgram used to render the GameObject.
     */
    public GameObject(Vector3f pos, Vector3f scale, Vector3f rot, float[] vertices, int[] indices, Vector4f color, ShaderProgram shaderProgram) {
        modelMatrix.translate(pos.x, pos.y, pos.z)
                   .scale(scale.x, scale.y, scale.z);
        modelMatrix.rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        modelMatrix.rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0));
        modelMatrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0));
        
        position = pos;
        rotation = rot;
        this.scale = scale;
        this.shaderProgram = shaderProgram;
        
        isTextured = false;
        
        glUseProgram(this.shaderProgram.getProgramId());
        this.shaderProgram.setUniform("isTextured", GL_FALSE);
        this.shaderProgram.setUniform("color", color);
        
        vertexCount = indices.length;
        
        IntBuffer   indexBuffer;
        int         indicesVboId;
        FloatBuffer vertexBuffer;
        int         vertexVboId;
        FloatBuffer textureCoordBuffer;
        int         textureVboId;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create index buffer.
            indexBuffer = stack.callocInt(indices.length + 1);
            indexBuffer.put(indices).flip();
            
            // Create index VBO.
            indicesVboId = glGenBuffers();
            vbos.add(indicesVboId);
            
            // Create vertex buffer.
            vertexBuffer = stack.callocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();
            
            // Create vertex VBO.
            vertexVboId = glGenBuffers();
            vbos.add(vertexVboId);
    
            // Create color buffer.
            textureCoordBuffer = stack.callocFloat(2);
            textureCoordBuffer.put(new float[] {1.0f, 1.0f}).flip();
    
            // Create texture VBO.
            textureVboId = glGenBuffers();
            vbos.add(textureVboId);
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
    
    /**
     * Creates a textured GameObject.
     *
     * @param pos             The position of the GameObject.
     * @param scale           The scale of the GameObject.
     * @param rot             The rotation of the GameObject.
     * @param vertices        The vertices of the GameObject.
     * @param textureLocation The file path for the texture of the GameObject.
     * @param textureCoords   The texture coordinates for the GameObject.
     * @param indices         The indices for the GameObject data.
     * @param shaderProgram   The ShaderProgram used to render the GameObject.
     */
    public GameObject(Vector3f pos, Vector3f scale, Vector3f rot, float[] vertices, String textureLocation, float[] textureCoords, int[] indices, ShaderProgram shaderProgram) {
        modelMatrix.translate(pos.x, pos.y, pos.z)
                   .scale(scale.x, scale.y, scale.z);
        modelMatrix.rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        modelMatrix.rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0));
        modelMatrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0));
        
        position = pos;
        rotation = rot;
        this.scale = scale;
        this.shaderProgram = shaderProgram;
    
        isTextured = true;
        
        glUseProgram(this.shaderProgram.getProgramId());
        this.shaderProgram.setUniform("isTextured", GL_TRUE);
        this.shaderProgram.setUniform("textureSampler", 0);
        
        vertexCount = indices.length;
        
        textureId = loadTexture(textureLocation);
        
        IntBuffer   indexBuffer;
        int         indicesVboId;
        FloatBuffer vertexBuffer;
        int         vertexVboId;
        FloatBuffer textureCoordBuffer;
        int         textureVboId;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create index buffer.
            indexBuffer = stack.callocInt(indices.length + 1);
            indexBuffer.put(indices).flip();
            
            // Create index VBO.
            indicesVboId = glGenBuffers();
            vbos.add(indicesVboId);
            
            // Create vertex buffer.
            vertexBuffer = stack.callocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();
            
            // Create vertex VBO.
            vertexVboId = glGenBuffers();
            vbos.add(vertexVboId);
            
            // Create color buffer.
            textureCoordBuffer = stack.callocFloat(textureCoords.length);
            textureCoordBuffer.put(textureCoords).flip();
            
            // Create texture VBO.
            textureVboId = glGenBuffers();
            vbos.add(textureVboId);
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
    
    /**
     * Creates a GameObject from an .obj file.
     *
     * @param pos           The position of the GameObject.
     * @param scale         The scale of the GameObject.
     * @param rot           The rotation of the GameObject.
     * @param location      The location of the .obj file.
     * @param shaderProgram The ShaderProgram used to render the GameObject.
     */
    public GameObject(Vector3f pos, Vector3f scale, Vector3f rot, String location, ShaderProgram shaderProgram) {
        ArrayList<String> lines = Util.readAllLines(location);
    
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector2f> textures = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Face> faces = new ArrayList<>();
        
        modelMatrix.translate(pos.x, pos.y, pos.z)
                   .scale(scale.x, scale.y, scale.z);
        modelMatrix.rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        modelMatrix.rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0));
        modelMatrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0));
    
        position = pos;
        rotation = rot;
        this.scale = scale;
        this.shaderProgram = shaderProgram;
    
        isTextured = true;
    
        glUseProgram(this.shaderProgram.getProgramId());
        this.shaderProgram.setUniform("isTextured", GL_TRUE);
        this.shaderProgram.setUniform("textureSampler", 0);
    
        //vertexCount = indices.length;
    
        //textureId = loadTexture(textureLocation);
    
        IntBuffer   indexBuffer;
        int         indicesVboId;
        FloatBuffer vertexBuffer;
        int         vertexVboId;
        FloatBuffer textureCoordBuffer;
        int         textureVboId;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create index buffer.
            //indexBuffer = stack.callocInt(indices.length + 1);
            //indexBuffer.put(indices).flip();
        
            // Create index VBO.
            indicesVboId = glGenBuffers();
            vbos.add(indicesVboId);
        
            // Create vertex buffer.
            //vertexBuffer = stack.callocFloat(vertices.length);
            //vertexBuffer.put(vertices).flip();
        
            // Create vertex VBO.
            vertexVboId = glGenBuffers();
            vbos.add(vertexVboId);
        
            // Create color buffer.
            //textureCoordBuffer = stack.callocFloat(textureCoords.length);
            //textureCoordBuffer.put(textureCoords).flip();
        
            // Create texture VBO.
            textureVboId = glGenBuffers();
            vbos.add(textureVboId);
        }
    
        // Create vertex VAO.
        vertexVaoId = glGenVertexArrays();
        glBindVertexArray(vertexVaoId);
        vaos.add(vertexVaoId);
    
        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        //glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    
        glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
        //glBufferData(GL_ARRAY_BUFFER, textureCoordBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
    
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
        //glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    
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
        
        modelMatrix.rotate((float) Math.toRadians(dz), new Vector3f(0, 0, 1));
        modelMatrix.rotate((float) Math.toRadians(dy), new Vector3f(0, 1, 0));
        modelMatrix.rotate((float) Math.toRadians(dx), new Vector3f(1, 0, 0));
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
            
            shaderProgram.setUniform("textureSampler", 0);
            
            return textureId;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return -1;
        }
    }
    
    int getTextureId() {
        return textureId;
    }
    
    Matrix4f getModelMatrix() {
        return modelMatrix;
    }
    
    int getVertexVaoId() {
        return vertexVaoId;
    }
    
    int getVertexCount() {
        return vertexCount;
    }
    
    boolean isTextured() {
        return isTextured;
    }
    
    ShaderProgram getShaderProgram() {
        return shaderProgram;
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
