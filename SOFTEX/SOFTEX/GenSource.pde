// A source that runs a generative algorithm
class GenSource extends Source {
  float time = 0;
  
  GenSource(String algoName) {
    super();
    name = algoName;
    active = true;
  }
  
  void update() {
    time += 0.01;
    
    canvas.beginDraw();
    // Example: Moving perlin noise background
    canvas.loadPixels();
    float yoff = 0;
    for (int y = 0; y < canvas.height; y++) {
      float xoff = 0;
      for (int x = 0; x < canvas.width; x++) {
        // Simple noise pattern
        float r = noise(xoff, yoff, time) * 255;
        float g = noise(xoff + 10, yoff + 10, time) * 255;
        float b = noise(xoff + 20, yoff + 20, time) * 255;
        
        canvas.pixels[x + y * canvas.width] = color(r, g, b);
        xoff += 0.01;
      }
      yoff += 0.01;
    }
    canvas.updatePixels();
    
    // Draw some shapes on top
    canvas.stroke(255);
    canvas.noFill();
    canvas.ellipse(canvas.width/2, canvas.height/2, 200 + sin(time*2)*50, 200 + sin(time*2)*50);
    
    canvas.endDraw();
  }
}
