import peasy.*;

PeasyCam cam;
int numSquares = 17;
int baseSize = 80;
int squareSize = 400;
int amp = 200;
float t;

void settings() {
  size(displayWidth, displayHeight, P3D);
}

void setup() {
  cam = new PeasyCam(this, 500); // Initialize PeasyCam with distance
  colorMode(HSB);
  noFill();
  strokeWeight(5);
}

void draw() {
  background(10);
  t = millis() * 0.0006;

  for (int i = 0; i < numSquares; i++) {
    float angle = TWO_PI * i / numSquares;
    float y = amp * sin(t + angle);
    float s = abs(baseSize + squareSize * cos(t + angle));

    float h = map(angle, 0, TWO_PI, 0, 360);
    stroke(h, 400, 400);

    pushMatrix();
    translate(0, y, 0);
    rotateX(HALF_PI);
    rectMode(CENTER);
    rect(0, 0, s, s);
    popMatrix();
  }
}
