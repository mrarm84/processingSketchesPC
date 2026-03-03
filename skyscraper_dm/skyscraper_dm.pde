import processing.video.*;
import peasy.*;

Movie movie;
PeasyCam cam;
int cols = 80, rows = 40;
float w = 100, h = 300;

void setup() {
  size(800, 600, P3D);
  movie = new Movie(this, "background.mp4");
  movie.loop();
  
  cam = new PeasyCam(this, 500);
}

void draw() {
  background(0);
  
  if (movie.available()) {
    movie.read();
  }
  
  pushMatrix();
  translate(-w / 2, height / 2, -300);
  drawSkyscraper();
  popMatrix();
}

void drawSkyscraper() {
  float cellW = w / cols;
  float cellH = h / rows;
  
  for (int i = 0; i < cols; i++) {
    for (int j = 0; j < rows; j++) {
      float x = i * cellW - w / 2;
      float y = -j * cellH;
      
      beginShape();
      texture(movie);
      vertex(x, y, -50, i / (float)cols, j / (float)rows);
      vertex(x + cellW, y, -50, (i + 1) / (float)cols, j / (float)rows);
      vertex(x + cellW, y - cellH, -50, (i + 1) / (float)cols, (j + 1) / (float)rows);
      vertex(x, y - cellH, -50, i / (float)cols, (j + 1) / (float)rows);
      endShape(CLOSE);
    }
  }
}
