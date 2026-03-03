#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform float segments; // e.g. 6.0

varying vec4 vertTexCoord;

void main() {
  vec2 uv = vertTexCoord.st - 0.5;
  float r = length(uv);
  float a = atan(uv.y, uv.x);
  
  float segmentAngle = 3.14159 * 2.0 / segments;
  a = mod(a, segmentAngle);
  a = abs(a - segmentAngle/2.0); // Mirror
  
  vec2 newUV = r * vec2(cos(a), sin(a)) + 0.5;
  
  gl_FragColor = texture2D(texture, newUV);
}
