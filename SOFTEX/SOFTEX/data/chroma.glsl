#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float amount; // 0.0 to 1.0

void main() {
  vec2 uv = vertTexCoord.st;
  
  // Calculate offset based on distance from center for radial aberration
  vec2 dist = uv - 0.5;
  vec2 offset = dist * amount * 0.05; // Scale efffect
  
  float r = texture2D(texture, uv - offset).r;
  float g = texture2D(texture, uv).g;
  float b = texture2D(texture, uv + offset).b;
  
  // Combine
  gl_FragColor = vec4(r, g, b, 1.0);
}
