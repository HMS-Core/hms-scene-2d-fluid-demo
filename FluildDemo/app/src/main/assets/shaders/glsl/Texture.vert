attribute vec4 position;
attribute vec2 uv;
uniform mat4 mvp;
uniform mat4 uvTransform;

varying vec2 uv1;

void main() {
    gl_Position = mvp * position;
    uv1 = (uvTransform * vec4(uv, 0, 1)).xy;
    uv1.y = 1.0 - uv1.y;
}