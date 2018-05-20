#version 400

in vec2 outTextureCoords;

out vec4 colorOut;

uniform sampler2D textureSampler;

void main() {
    colorOut = texture(textureSampler, outTextureCoords);
}
