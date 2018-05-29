#version 400

in vec2 outTextureCoords;

out vec4 colorOut;

uniform bool isTextured;
uniform vec4 color;
uniform sampler2D textureSampler;

void main() {
    if (isTextured) {
        colorOut = texture(textureSampler, outTextureCoords);
    } else {
        colorOut = color;
    }
}
