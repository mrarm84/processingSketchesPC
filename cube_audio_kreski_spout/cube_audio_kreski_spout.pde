import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;
import processing.sound.*;
import spout.*;
import peasy.*;

PGraphics canvas;
PShape sh;
PostFX fx;
Spout spout;
PeasyCam cam;
AudioIn input;
Amplitude loudness;

String[] words = {"electronic", "memory", "knowledge", "generating", "data", "example"};
float rotationX = 0;
float rotationY = 0;
float rotationZ = 0;
float boxSize = 100;
float rotationSpeed = 0.01;
float direction = 1;
int selMode = REPLACE;
String name = "REPLACE";
void setup() {
  size(1024, 768, P3D);
  frameRate(120);

  // Initialize PostFX
  fx = new PostFX(this);
  canvas = createGraphics(width, height, P3D);

  // Initialize Spout
  spout = new Spout(this);

  // Initialize PeasyCam
  cam = new PeasyCam(this, width / 2, height / 2, 400, 500);
  cam.rotateY(radians(90));
  cam.rotateZ(radians(-90));

  // Initialize Minim and audio input
  Sound s = new Sound(this);
  s.list();
  s.inputDevice(7); // Select input device
  input = new AudioIn(this, 0);
  input.start();

  // Initialize Amplitude analyzer
  loudness = new Amplitude(this);
  loudness.input(input);

  // Load an image
  //img = loadImage("sample.png");
}

void draw() {
  background(255);
  lights();

  // Get the current volume
  float volume = loudness.analyze();
  boxSize = map(volume, 0, 0.5, 1, 255);

  // Adjust rotation speed and direction based on FFT peak
  if (volume > 0.1) {
    rotationSpeed = 0.1;
    direction *= -1;
  } else {
    rotationSpeed = 0.001;
  }

  // Rotate the whole system
  rotationX += rotationSpeed * direction;
  rotationY += rotationSpeed * direction;
  rotationZ += rotationSpeed * direction;

  pushMatrix();
  translate(width / 2, height / 2);
  rotateX(rotationX);
  rotateY(rotationY);
  rotateZ(rotationZ);
  box(boxSize);
  popMatrix();
blendMode(selMode);
  for (int j = 0; j < 10; j++) {
    drawRings(volume);
   rotateX(rotationX);
 }

  // Generate rings based on volume


  // Apply PostFX effects
  fx.render()
    .chromaticAberration()
    .rgbSplit(500)
    .compose();

  // Send the current frame through Spout
  spout.sendTexture(canvas);
}

void drawRings(float volume) {
  stroke(0);
  noFill();

  // Create multiple sets of orbits in different dimensions
  for (int j = 0; j < 10; j++) {
    float baseRadius = map(volume, 0, 0.5, 50 + j * 20, 200 + j * 20);

    // XY plane
    for (int i = 0; i < 360; i += 20) {
      float x1 = width / 2 + cos(radians(i)) * baseRadius * 1.1;
      float y1 = height / 2 + sin(radians(i)) * baseRadius * 1.1;
      float x2 = width / 2 + cos(radians(i + 10)) * baseRadius * 1.1;
      float y2 = height / 2 + sin(radians(i + 10)) * baseRadius * 1.1;
      line(x1, y1, x2, y2);
    }

    // YZ plane
    for (int i = 0; i < 360; i += 20) {
      float y1 = height / 2 + cos(radians(i)) * baseRadius * 1.1;
      float z1 = baseRadius * sin(radians(i)) * 1.1;
      float y2 = height / 2 + cos(radians(i + 10)) * baseRadius * 1.1;
      float z2 = baseRadius * sin(radians(i + 10)) * 1.1;
      line(0, y1, z1, 0, y2, z2);
    }

    // XZ plane
    for (int i = 0; i < 360; i += 20) {
      float x1 = width / 2 + cos(radians(i)) * baseRadius * 1.1;
      float z1 = baseRadius * sin(radians(i)) * 1.1;
      float x2 = width / 2 + cos(radians(i + 10)) * baseRadius * 1.1;
      float z2 = baseRadius * sin(radians(i + 10)) * 1.1;
      line(x1, 0, z1, x2, 0, z2);
    }

    // Dotted rings and text in XY plane
    for (int i = 0; i < 360; i += 20) {
      float x = width / 2 + cos(radians(i)) * baseRadius * 1.3;
      float y = height / 2 + sin(radians(i)) * baseRadius * 1.3;
      point(x, y);
    }

    // Text ring in XY plane
    textSize(12);
    textAlign(CENTER, CENTER);
    for (int i = 0; i < 360; i += 60) {
      float x = width / 2 + cos(radians(i)) * baseRadius * 1.5;
      float y = height / 2 + sin(radians(i)) * baseRadius * 1.5;
      text(words[(i / 60) % words.length], x, y);
    }
  }
}

void mousePressed() {

  if (selMode == REPLACE) {
    selMode = BLEND;
    name = "SLIGHT";
  } else if (selMode == BLEND) {
    selMode = ADD;
    name = "ADD";
  } else if (selMode == ADD) {
    selMode = LIGHTEST;
    name = "MOVE";
  } else if (selMode == LIGHTEST) {
    selMode = DIFFERENCE;
    name = "DIFFERENCE";
  } else if (selMode == DIFFERENCE) {
    
    selMode = SCREEN;
    name = "EXPLODE";
  } else if (selMode == SCREEN) {
    selMode = REPLACE;
    name = "REPLACE";
  }


  print(name);
}

void stop() {
  //input.close();
  //minim.stop();
  super.stop();
}
