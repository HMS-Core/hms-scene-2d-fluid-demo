attribute vec4 position;
attribute vec2 uv;
uniform mat4 mvp;

varying vec2 uv1;

void main() {
    gl_Position = mvp * position;
    uv1 = uv;
}