#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture; // The base (bottom) layer
uniform sampler2D layer;   // The top layer to blend
uniform int mode;          // Blend mode
uniform float opacity;
uniform vec2 texOffset;    // Resolution

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  vec4 base = texture2D(texture, vertTexCoord.st);
  vec4 blend = texture2D(layer, vertTexCoord.st);
  
  vec3 res = base.rgb;
  vec3 b = blend.rgb;
  vec3 a = base.rgb;
  
  // 0: Normal (Alpha only)
  if (mode == 0) {
    res = mix(a, b, blend.a * opacity);
  }
  // 1: Add
  else if (mode == 1) {
    res = min(a + b * opacity, vec3(1.0));
  }
  // 2: Multiply
  else if (mode == 2) {
    res = a * b; // mix handled later? No, mult is dark. 
    // Standard Mult: result = a * b. Lerp with opacity.
    res = mix(a, a * b, opacity);
  }
  // 3: Screen
  else if (mode == 3) {
    res = vec3(1.0) - ((vec3(1.0) - a) * (vec3(1.0) - b));
    res = mix(a, res, opacity);
  }
  // 4: Difference
  else if (mode == 4) {
    res = abs(a - b);
    res = mix(a, res, opacity);
  }
  // 5: Exclusion (XOR-like)
  else if (mode == 5) {
    res = a + b - 2.0 * a * b;
    res = mix(a, res, opacity);
  }
  // 6: Subtract
  else if (mode == 6) {
    res = max(a - b, vec3(0.0));
    res = mix(a, res, opacity);
  }
  
  gl_FragColor = vec4(res, 1.0); // Simple alpha implementation
}
