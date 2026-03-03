#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float amount;
uniform float time;
uniform float glitch;
uniform float pixelate;
uniform float scanlines;

void main() {
  vec2 uv = vertTexCoord.st;
  
  // Pixelate logic
  if (pixelate > 0.0) {
    float size = 1.0 / pixelate;
    uv = floor(uv * pixelate) / pixelate;
  }

  // Glitch logic
  float g = step(0.98, fract(sin(dot(uv.yy + time, vec2(12.9898, 78.233))) * 43758.5453));
  uv.x += g * glitch * 0.05;

  // Chromatic Aberration
  float r = texture2D(texture, uv + vec2(amount, 0.0)).r;
  float g_col = texture2D(texture, uv).g;
  float b = texture2D(texture, uv - vec2(amount, 0.0)).b;
  
  vec4 color = vec4(r, g_col, b, 1.0);
  
  // Scanlines
  if (scanlines > 0.0) {
    color.rgb *= 1.0 - (mod(vertTexCoord.t * 500.0, 2.0) * 0.2 * scanlines);
  }

  gl_FragColor = color * vertColor;
}
