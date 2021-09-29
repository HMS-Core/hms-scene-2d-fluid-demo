attribute vec4 position;
attribute vec4 color;
attribute float pointSize;
uniform mat4 mvp;
varying vec4 outColor;

void main() {
    gl_Position = mvp * position;
    gl_PointSize = pointSize;
    outColor = color;
}