attribute vec4 position;
attribute vec4 color;
uniform mat4 mvp;
varying vec4 outColor;

void main() {
    gl_Position = mvp * position;
    outColor = color;
}