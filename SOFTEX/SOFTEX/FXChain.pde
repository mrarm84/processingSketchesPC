class FXChain {
  ArrayList<Effect> potentialEffects;
  
  class Effect {
    String name;
    PShader shader;
    boolean active = false;
    // Parameters map? For now, hardcoded refs
    
    Effect(String n, PShader s) { name = n; shader = s; }
  }
  
  FXChain(PApplet p) {
    potentialEffects = new ArrayList<Effect>();
    // Load default FX
    try {
      potentialEffects.add(new Effect("Chroma", p.loadShader("chroma.glsl")));
      potentialEffects.add(new Effect("Kaleido", p.loadShader("kaleido.glsl")));
      potentialEffects.add(new Effect("Dither", p.loadShader("dither.glsl")));
    } catch (Exception e) {
      println("Error loading shaders: " + e);
    }
  }
  
  void toggle(String name) {
    for (Effect e : potentialEffects) {
      if (e.name.equals(name)) e.active = !e.active;
    }
  }
  
  void apply(PGraphics pg) {
    pg.beginDraw();
    for (Effect e : potentialEffects) {
       if (e.active && e.shader != null) {
          // Update uniform params if needed
          if (e.name.equals("Chroma")) e.shader.set("amount", 1.0); // Max aberration for demo
          if (e.name.equals("Kaleido")) e.shader.set("segments", 6.0);
          
          pg.filter(e.shader);
       }
    }
    pg.endDraw();
  }
}
