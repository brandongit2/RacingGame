#version 400

in vec4 colorIn;
in vec2 outTextureCoords;

out vec4 colorOut;

uniform bool isTextured;
uniform sampler2D textureSampler;

void main() {
    if (isTextured) {
        colorOut = texture(textureSampler, outTextureCoords);
    } else {
        colorOut = colorIn;
    }
}
