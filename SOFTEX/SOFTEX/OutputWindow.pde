import spout.*;
import com.hamoid.*;

class OutputWindow extends PApplet {
  PGraphics sharedBuffer;
  Spout spout;
  VideoExport videoExport;
  boolean recording = false;
  
  // Settings
  int outWidth = 1280;
  int outHeight = 720;
  
  OutputWindow(int w, int h) {
    super();
    this.outWidth = w;
    this.outHeight = h;
  }
  
  void settings() {
    size(outWidth, outHeight, P3D);
    // clean way to set title not available in settings(), done in setup()
  }
  
  void setup() {
    surface.setTitle("SOFTEX Output");
    surface.setLocation(100, 100);
    
    // Initialize Spout
    try {
      spout = new Spout(this);
      spout.createSender("SOFTEX Output");
    } catch (Throwable e) {
      println("Spout not available or library missing");
    }
  }
  
  void draw() {
    background(0);
    
    if (sharedBuffer != null) {
      // Draw the shared buffer to the screen
      image(sharedBuffer, 0, 0, width, height);
      
      // Send to Spout
      if (spout != null) {
        spout.sendTexture();
      }
      
      // Handle Recording
      if (recording && videoExport != null) {
        videoExport.saveFrame();
      }
    }
    
    // OSD for recording status
    if (recording) {
      fill(255, 0, 0);
      noStroke();
      ellipse(20, 20, 15, 15);
    }
  }
  
  // Called by main engine to pass the latest frame
  void setFrame(PGraphics pg) {
    this.sharedBuffer = pg;
  }
  
  void startRecording(String filename) {
    videoExport = new VideoExport(this, filename);
    videoExport.setFrameRate(60); // Match or configurable
    videoExport.startMovie();
    recording = true;
    println("Recording started: " + filename);
  }
  
  void stopRecording() {
    if (recording && videoExport != null) {
      videoExport.endMovie();
      recording = false;
      println("Recording stopped.");
    }
  }
}
