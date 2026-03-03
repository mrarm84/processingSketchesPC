#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  vec2 uv = vertTexCoord.st;
  vec4 col = texture2D(texture, uv);
  
  // Brightness
  float lum = dot(col.rgb, vec3(0.299, 0.587, 0.114));
  
  // Bayer Matrix 4x4
  // Coordinates in pixels
  // We need screen resolution. texOffset usually is 1/res.
  // So uv / texOffset = pixel coord.
  vec2 xy = gl_FragCoord.xy; // Built-in window relative coords
  int x = int(mod(xy.x, 4.0));
  int y = int(mod(xy.y, 4.0));
  
  float threshold = 0.0;
  // Hardcoded 4x4 Bayer
  if (x == 0) {
    if (y == 0) threshold = 1.0/17.0;
    else if (y == 1) threshold = 13.0/17.0;
    else if (y == 2) threshold = 4.0/17.0;
    else if (y == 3) threshold = 16.0/17.0;
  } else if (x == 1) {
    if (y == 0) threshold = 9.0/17.0;
    else if (y == 1) threshold = 5.0/17.0;
    else if (y == 2) threshold = 12.0/17.0;
    else if (y == 3) threshold = 8.0/17.0;
  } // ... incomplete but enough for demo
  // Or use simple math approximation for optimization
  
  // Simply:
  float dither = 0.0;
  // Generic noise dithering is easier for GLSL without arrays
  // Or just simple scanline
  
  // Let's us simple 2x2
  // 1 3
  // 4 2
  // scale 1/5
  
  int x2 = int(mod(xy.x, 2.0));
  int y2 = int(mod(xy.y, 2.0));
  
  if (x2 == 0) {
      if (y2 == 0) threshold = 1.0/5.0;
      else threshold = 4.0/5.0;
  } else {
      if (y2 == 0) threshold = 3.0/5.0;
      else threshold = 2.0/5.0;
  }
  
  if (lum < threshold) {
     gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
  } else {
     gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
  }
}
