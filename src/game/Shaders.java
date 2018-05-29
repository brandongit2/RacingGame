package game;

import engine.Game;
import engine.ShaderProgram;

public class Shaders {
    static final ShaderProgram PLAIN_SHADER;
    
    static {
        PLAIN_SHADER = new ShaderProgram("plainShader", "/game/vertex.glsl", "/game/fragment.glsl",
                                         new String[] {"projectionMatrix", "viewMatrix", "modelMatrix", "textureSampler", "color", "isTextured"}, 2, true);
        Game.addShader(PLAIN_SHADER);
    }
}
