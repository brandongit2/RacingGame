#version 400

//in vec2 outTextureCoords;

out vec4 colorOut;

//uniform sampler2D textureSampler;

void main() {
    //colorOut = texture(textureSampler, outTextureCoords);
    colorOut = vec4(1, 0.3, 0, 1);
}
