package game;

import engine.ShaderProgram;

class Shaders {
    static final ShaderProgram PLAIN_SHADER;
    
    static {
        PLAIN_SHADER = new ShaderProgram(
          "plainShader",
          "/vertex.glsl",
          "/fragment.glsl",
          new String[] {
            "projectionMatrix",
            "viewMatrix",
            "modelMatrix",
            "textureSampler",
            "isTextured"},
          2
        );
    }
}
