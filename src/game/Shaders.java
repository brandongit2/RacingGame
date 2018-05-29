package game;

import engine.ShaderProgram;

public class Shaders {
    static final ShaderProgram PLAIN_SHADER;
    static final ShaderProgram TEXTURED_SHADER;
    
    static {
        PLAIN_SHADER    = new ShaderProgram("plainShader", "/game/vertex.glsl", "/game/fragment.glsl",
                                            new String[] {"projectionMatrix", "viewMatrix", "modelMatrix", "color"});
    
        TEXTURED_SHADER = new ShaderProgram("texturedShader", "/game/textured_vertex.glsl", "/game/textured_fragment.glsl",
                                            new String[] {"projectionMatrix", "viewMatrix", "modelMatrix", "textureSampler"});
    }
}
