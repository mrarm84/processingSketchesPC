import processing.video.*;
import gab.opencv.*;
import java.awt.Rectangle;

Capture video;
OpenCV opencv;
CyberHand cyberHand;
PShader postFX;
PGraphics mainCanvas;

int threshold = 80;
boolean debug = false;

void setup() {
  size(1280, 720, P3D);
  mainCanvas = createGraphics(width, height, P3D);
  
  // Initialize webcam
  String[] cameras = Capture.list();
  if (cameras == null || cameras.length == 0) {
    println("No cameras available.");
    exit();
  } else {
    // Try to find a good resolution
    video = new Capture(this, 640, 480);
    video.start();
  }
  
  opencv = new OpenCV(this, 640, 480);
  cyberHand = new CyberHand(this);
  
  // Load post-processing shader
  postFX = loadShader("fx.glsl");
  postFX.set("resolution", (float)width, (float)height);
}

void captureEvent(Capture c) {
  c.read();
}

void draw() {
  // 1. Process Hand
  opencv.loadImage(video);
  opencv.gray();
  opencv.threshold(threshold);
  opencv.erode();
  opencv.dilate();
  
  // 2. Render to mainCanvas
  mainCanvas.beginDraw();
  mainCanvas.background(0);
  
  if (debug) {
    mainCanvas.image(opencv.getOutput(), 0, 0, mainCanvas.width, mainCanvas.height);
  }
  
  // Find contours and render cyber hand
  ArrayList<Contour> contours = opencv.findContours();
  if (contours.size() > 0) {
    Contour largest = null;
    float maxArea = 0;
    for (Contour c : contours) {
      if (c.area() > maxArea) {
        maxArea = c.area();
        largest = c;
      }
    }
    
    if (largest != null && maxArea > 2000) {
      cyberHand.update(largest, opencv);
      cyberHand.display(mainCanvas);
    }
  }
  mainCanvas.endDraw();
  
  // 3. Apply Post-FX
  postFX.set("time", millis() / 1000.0);
  shader(postFX);
  image(mainCanvas, 0, 0, width, height);
  resetShader();
  
  // HUD
  if (debug) {
    fill(255);
    text("Threshold: " + threshold + " (UP/DOWN to change, D to toggle debug)", 20, 30);
  }
}

void keyPressed() {
  if (keyCode == UP) threshold++;
  if (keyCode == DOWN) threshold--;
  if (key == 'd' || key == 'D') debug = !debug;
}
