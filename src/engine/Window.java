package engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Contains a window object.
 */
public class Window {
    private int                   width;
    private int                   height;
    private long                  handle;
    private int                   programId;
    private ArrayList<GameObject> objects        = new ArrayList<>();
    private boolean               hasBeenResized = true;
    
    /**
     * Create a window.
     *
     * @param width  The width of the window.
     * @param height The height of the window.
     * @param title  The title of the window.
     * @param name   The name of the window object.
     */
    public Window(int width, int height, String title, String name) {
        Game.windows.put(name, this);
        Game.setCurrentWindow(name);
        
        // Create GLFW window.
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL) { // Verify that the window was created.
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window.");
        }
        
        // Update window dimensions
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(handle, w, h);
        this.width = w.get(0);
        this.height = h.get(0);
        
        // Set up resize callback to change fields 'width' and 'height'.
        glfwSetFramebufferSizeCallback(handle, (long handle, int newWidth, int newHeight) -> {
            Window.this.width = newWidth;
            Window.this.height = newHeight;
            
            Game.getCurrentCamera().changeProjectionMatrix(newWidth, newHeight);
            
            hasBeenResized = true;
        });
        
        // Makes the current GLFW context 'handle'. All OpenGL commands will be sent here.
        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1); // Enable g-sync
        
        // Enable OpenGL.
        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback(System.err); // Makes all OpenGL errors print to System.err.
        
        // Enable depth testing.
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        // Create shader program.
        programId = glCreateProgram();
        if (programId == NULL) { // Verify that shader program was created.
            throw new RuntimeException("Could not create shader program.");
        }
        
        System.out.println("OpenGL version " + glGetString(GL_VERSION));
    }
    
    /**
     * Creates a shader.
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
     * @param location The file location for the vertex shader.
     */
    public void setVertexShader(String location) {
        createShader(Util.loadResource(location), GL_VERTEX_SHADER);
    }
    
    /**
     * Sets the fragment shader.
     * @param location The file location for the fragment shader.
     */
    public void setFragmentShader(String location) {
        createShader(Util.loadResource(location), GL_FRAGMENT_SHADER);
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
        
        try {
            renderer.createUniform("projectionMatrix", programId);
            renderer.createUniform("viewMatrix", programId);
            renderer.createUniform("modelMatrix", programId);
            //renderer.createUniform("textureSampler", programId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        glUseProgram(programId);
    }
    
    /**
     * Adds a GameObject to the game.
     * @param object The object to be added.
     */
    public void addObject(GameObject object) {
        Game.getCurrentRenderer().setUniform("modelMatrix", object.getModelMatrix());
        
        objects.add(object);
    }
    
    public void render() {
        Game.getCurrentRenderer().render(objects);
        if (hasBeenResized) {
            hasBeenResized = false;
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public long getHandle() {
        return handle;
    }
    
    public ArrayList<GameObject> getObjects() {
        return objects;
    }
    
    boolean hasBeenResized() {
        return hasBeenResized;
    }
    
    public void cleanUp() {
        glfwTerminate();
    }
}
