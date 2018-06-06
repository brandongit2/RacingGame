package engine;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ShaderProgram {
    private static HashMap<String, Integer> shaderPrograms = new HashMap<>();
    
    private int                      programId;
    private HashMap<String, Integer> uniforms = new HashMap<>();
    private int                      numAttribArrays;
    
    public ShaderProgram(String name, String vertexShaderLocation, String fragmentShaderLocation, String[] uniforms, int numAttribArrays) {
        programId = glCreateProgram();
        if (programId == NULL) { // Verify that shader program was created.
            throw new RuntimeException("Could not create shader program.");
        }
        
        this.numAttribArrays = numAttribArrays;
        
        shaderPrograms.put(name, programId);
        
        setVertexShader(vertexShaderLocation);
        setFragmentShader(fragmentShaderLocation);
        
        linkShaderProgram();
        
        for (String uniform : uniforms) {
            try {
                createUniform(uniform);
            } catch (RuntimeException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        Game.addShader(this);
    }
    
    /**
     * Finalizes the shaders (links them to the shader program).
     */
    public void linkShaderProgram() {
        Renderer renderer = Game.getCurrentRenderer();
        
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == NULL) {
            throw new RuntimeException("Error linking shader program. Code " + glGetProgramInfoLog(programId, 2048));
        }
        
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == NULL) {
            System.err.println("Warning validating shader. Code " + glGetProgramInfoLog(programId, 2048));
        }
        
        glUseProgram(programId);
    }
    
    private void createUniform(String name) throws RuntimeException {
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
    
    void setUniform(String name, Vector4f value) {
        glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
    }
    
    /**
     * Creates a shader.
     *
     * @param shaderCode The raw shader code.
     * @param shaderType The type of shader to be compiled.
     */
    private void createShader(String shaderCode, int shaderType) {
        // Create shader.
        int shaderId = glCreateShader(shaderType);
        if (shaderId == NULL) { // Verify that shader was created.
            throw new RuntimeException("Error creating shader with type" + shaderType + ".");
        }
        
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == NULL) { // Checks shader compile status.
            throw new RuntimeException("Error compiling shader. Code " + glGetShaderInfoLog(shaderId, 2048));
        }
        
        glAttachShader(programId, shaderId);
    }
    
    /**
     * Sets the vertex shader.
     *
     * @param location The file location for the vertex shader.
     */
    public void setVertexShader(String location) {
        createShader(Util.loadResource(location), GL_VERTEX_SHADER);
    }
    
    /**
     * Sets the fragment shader.
     *
     * @param location The file location for the fragment shader.
     */
    public void setFragmentShader(String location) {
        createShader(Util.loadResource(location), GL_FRAGMENT_SHADER);
    }
    
    public int getNumAttribArrays() {
        return numAttribArrays;
    }
    
    public int getProgramId() {
        return programId;
    }
}
