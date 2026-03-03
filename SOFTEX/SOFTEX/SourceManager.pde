// Abstract base class for all visual sources
abstract class Source {
  PGraphics canvas;
  boolean active = false;
  String name;
  float opacity = 1.0;
  int blendMode = 0; // 0: Normal, 1: Add, 2: Mult, 3: Screen, 4: Diff, 5: Excl, 6: Sub
  
  Source() {
    canvas = createGraphics(width, height, P3D);
  }
  
  abstract void update();
  
  PGraphics getFrame() {
    return canvas;
  }
}

// Manager for the 4 channels (A, B, C, D)
class SourceManager {
  Source[] channels = new Source[4];
  PGraphics[] mixBuffers;
  int currentBuffer = 0;
  PShader mixShader;
  
  SourceManager() {
    // Initialize with empty/placeholder sources
    for (int i = 0; i < 4; i++) {
      channels[i] = new EmptySource();
    }
  }
  
  void init(PApplet parent) {
    mixShader = parent.loadShader("mix.glsl");
    // Initialize ping-pong buffers
    mixBuffers = new PGraphics[2];
    for(int i=0; i<2; i++) {
      mixBuffers[i] = parent.createGraphics(parent.width, parent.height, P3D);
    }
  }
  
  void loadSource(int channelIndex, Source newSource) {
    if (channelIndex >= 0 && channelIndex < 4) {
      channels[channelIndex] = newSource;
      println("Channel " + channelIndex + " loaded: " + newSource.name);
    }
  }
  
  void update() {
    for (Source s : channels) {
      if (s.active) {
        s.update();
      }
    }
  }
  
  PGraphics render() {
    if (mixBuffers == null) return null;

    // Start with Channel A (or clear)
    currentBuffer = 0;
    PGraphics master = mixBuffers[currentBuffer];
    
    master.beginDraw();
    master.background(0);
    if (channels[0] != null && channels[0].active) {
       master.image(channels[0].getFrame(), 0, 0); 
    }
    master.endDraw();
    
    // Blend subsequent channels
    for (int i = 1; i < 4; i++) {
       if (channels[i] != null && channels[i].active && channels[i].opacity > 0) {
          int nextBuffer = (currentBuffer + 1) % 2;
          PGraphics target = mixBuffers[nextBuffer];
          applyBlend(master, target, channels[i].getFrame(), channels[i].blendMode, channels[i].opacity);
          master = target;
          currentBuffer = nextBuffer;
       }
    }
    
    return master;
  }
  
  void applyBlend(PGraphics base, PGraphics target, PGraphics top, int mode, float opacity) {
    if (mixShader == null) return; 

    target.beginDraw();
    target.background(0); // Ensure alpha is handled or just overwrite?
    // If we rely on valid storage, just draw over. 
    // We are generating a full frame.
    
    mixShader.set("texture", base); 
    mixShader.set("layer", top);
    mixShader.set("mode", mode);
    mixShader.set("opacity", opacity);
    
    target.shader(mixShader);
    target.image(base, 0, 0); 
    target.resetShader();
    target.endDraw();
  }
}

class EmptySource extends Source {
  EmptySource() {
    name = "Empty";
    active = false;
  }
  
  void update() {
    // Do nothing
  }
}
