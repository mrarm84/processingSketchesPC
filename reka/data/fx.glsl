#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
    vec2 uv = vertTexCoord.st;
    
    // 1. Chromatic Aberration
    float amount = 0.01 + 0.005 * sin(time * 2.0);
    vec4 col;
    col.r = texture2D(texture, vec2(uv.x + amount, uv.y)).r;
    col.g = texture2D(texture, uv).g;
    col.b = texture2D(texture, vec2(uv.x - amount, uv.y)).b;
    col.a = 1.0;
    
    // 2. Simple Blur (Multi-tap)
    vec4 blur = vec4(0.0);
    float blurSize = 0.002;
    blur += texture2D(texture, uv + vec2(-blurSize, -blurSize)) * 0.1;
    blur += texture2D(texture, uv + vec2(0.0, -blurSize)) * 0.15;
    blur += texture2D(texture, uv + vec2(blurSize, -blurSize)) * 0.1;
    blur += texture2D(texture, uv + vec2(-blurSize, 0.0)) * 0.15;
    blur += texture2D(texture, uv) * 0.1;
    blur += texture2D(texture, uv + vec2(blurSize, 0.0)) * 0.15;
    blur += texture2D(texture, uv + vec2(-blurSize, blurSize)) * 0.1;
    blur += texture2D(texture, uv + vec2(0.0, blurSize)) * 0.15;
    blur += texture2D(texture, uv + vec2(blurSize, blurSize)) * 0.1;
    
    // Combine
    gl_FragColor = mix(col, blur, 0.4);
    
    // 3. Vignette
    float dist = distance(uv, vec2(0.5));
    gl_FragColor.rgb *= smoothstep(0.8, 0.4, dist);
    
    // 4. Grain
    float noise = (fract(sin(dot(uv.xy, vec2(12.9898, 78.233))) * 43758.5453) - 0.5) * 0.1;
    gl_FragColor.rgb += noise;
    
    // 5. Flicker
    float flicker = 1.0 + 0.05 * sin(time * 50.0);
    gl_FragColor.rgb *= flicker;
}
