package engine;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.*;

public class GameObject extends Entity {
    private ArrayList<Mesh> meshes = new ArrayList<>();
    private        Vector3f              scale       = new Vector3f(1f, 1f, 1f);
    private        Matrix4f              modelMatrix = new Matrix4f();
    private        ShaderProgram         shaderProgram;
    
    private GameObject(Vector3f pos, Vector3f scale, Vector3f rot, ShaderProgram shaderProgram) {
        position = pos;
        rotation = rot;
        this.scale = scale;
    
        translate(pos.x, pos.y, pos.z);
        scale(scale.x, scale.y, scale.z);
        rotate(rot.x, rot.y, rot.z);
    
        this.shaderProgram = shaderProgram;
    }
    
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
        this(pos, scale, rot, shaderProgram);
        
        glUseProgram(this.shaderProgram.getProgramId());
        this.shaderProgram.setUniform("isTextured", GL_FALSE);
        
        meshes.add(new Mesh(vertices, indices, color, this));
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
        this(pos, scale, rot, shaderProgram);
        
        glUseProgram(this.shaderProgram.getProgramId());
        this.shaderProgram.setUniform("isTextured", GL_TRUE);
        this.shaderProgram.setUniform("textureSampler", 0);
        
        meshes.add(new TexturedMesh(vertices, textureLocation, textureCoords, indices, this));
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
        this(pos, scale, rot, shaderProgram);
        
        AIScene aiScene = aiImportFile(location, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
        if (aiScene == null) {
            new RuntimeException("Failed to load model.").printStackTrace();
            System.exit(1);
        }
        
        int           numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes  = aiScene.mMeshes();
        AIMesh[] meshes = new AIMesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            meshes[i] = AIMesh.create(aiMeshes.get(i));
        }
        
        this.shaderProgram = shaderProgram;
        
        glUseProgram(this.shaderProgram.getProgramId());
    }
    
    @Override
    public void translate(float dx, float dy, float dz) {
        super.translate(dx, dy, dz);
        
        modelMatrix.translate(dx, dy, dz);
    }
    
    @Override
    public void rotate(float dx, float dy, float dz) {
        super.rotate(dx, dy, dz);
        
        modelMatrix.rotate((float) Math.toRadians(dz), new Vector3f(0, 0, 1));
        modelMatrix.rotate((float) Math.toRadians(dy), new Vector3f(0, 1, 0));
        modelMatrix.rotate((float) Math.toRadians(dx), new Vector3f(1, 0, 0));
    }
    
    public void scale(float x, float y, float z) {
        modelMatrix.scale(x, y, z);
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
    
    void render() {
        for (Mesh mesh : meshes) {
            mesh.beginRender();
            mesh.render();
            mesh.endRender();
        }
    }
    
    Matrix4f getModelMatrix() {
        return modelMatrix;
    }
    
    ShaderProgram getShaderProgram() {
        return shaderProgram;
    }
}
