import processing.video.*;

class VideoSource extends Source {
  Movie movie;
  
  VideoSource(PApplet parent, String filename) {
    super();
    // In a real scenario, we'd check if file exists
    try {
      movie = new Movie(parent, filename);
      movie.loop();
      movie.volume(0); // Mute mainly for VJ use
      name = filename;
      active = true;
    } catch (Exception e) {
      println("Error loading movie: " + filename);
      name = "Error";
      active = false;
    }
  }
  
  void update() {
    if (movie != null && movie.available()) {
      movie.read();
    }
    
    // Draw movie to internal canvas
    canvas.beginDraw();
    canvas.background(0);
    if (movie != null) {
      // Resize to fit or cover? For now, stretch to fit
      canvas.image(movie, 0, 0, canvas.width, canvas.height);
    }
    canvas.endDraw();
  }
  
  // Need to handle disposal or stopping
  void stop() {
    if (movie != null) movie.stop();
  }
}
