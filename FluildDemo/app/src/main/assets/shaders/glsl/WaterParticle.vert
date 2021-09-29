attribute vec4 position;
attribute vec4 color;
uniform mat4 mvp;
uniform float pointSize;
varying vec4 outColor;

void main() {
    gl_Position = mvp * position;
    gl_PointSize = pointSize;
    outColor = color;
}