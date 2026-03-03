int scrollValue = 0;
int numCubes = 10;

import com.hamoid.*;
import peasy.PeasyCam;
//zz
PeasyCam cam;
PGraphics canvas;
import ComputationalGeometry.*;
IsoSkeleton skeleton;
float angle = 0;
float sphereRadius = 200;
float angle1 = 0;
float angle2 = 0;
float radius1 = 100;
float radius2 = 150;
import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;

import processing.sound.*;
import spout.*;

PShape sh;

PostFX fx;
boolean recording = false;
int recordStartTime;
//import themidibus.*; //Import the library
//import javax.sound.midi.MidiMessage; //Import the MidiMessage classes http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/midi/MidiMessage.html
//import javax.sound.midi.SysexMessage;
//import javax.sound.midi.ShortMessage;
//MidiBus myBus;


int currentColor = 0;
int midiDevice  = 3;

AudioIn input;
Amplitude loudness;
//MidiMessage message;

float theta;

float xmag, ymag = 0;
float newXmag, newYmag = 0;

float a;                 // Angle of rotation
float offset = PI/24.0;  // Angle offset between boxes
int num = 26;
int rad = 0 ;
int n;
int selMode = REPLACE;
String name = "REPLACE";
int p1; //serves as temp pitch
int v1; //serves as temp velocity
int redColor = int(random(10, 255));
int greenColor= int(random(50, 255));
int blueColor = int(random(200, 255));


float boxSize = 20;
float margin = boxSize*2;
float depth = 400;
color boxFill;

int fcount, lastm;
float frate;
int fint = 3;

int ccChannel;
int ccNumber;
int ccValue;

int angleX = 0;
int angleY = 0;
int angleZ = 0;
int cubeSize = 122;

Spout spout;

import peasy.*;


import java.lang.reflect.Method;


int maxHeight = 40;
int minHeight = 20;
int letterHeight = maxHeight; // Height of the letters
int letterWidth = 20;          // Width of the letter

int x = -letterWidth;          // X position of the letters
int y = 0;                      // Y position of the letters
VideoExport videoExport;

boolean newletter;

int numChars = 26;      // There are 26 characters in the alphabet
color[] colors = new color[numChars];

PVector[] orbitPoints;
PVector[] helixPoints;

PImage img;  // Declare variable "a" of type PImage

// New graphical elements
ArrayList<Particle> particles;
ArrayList<Wave> waves;
ArrayList<OrganicShape> organicShapes;
float time = 0;
float noiseOffset = 0;
int numParticles = 200;
int numWaves = 8;
int numShapes = 15;

// Particle class for flowing elements
class Particle {
  PVector pos, vel, acc;
  float life, maxLife;
  color col;
  float size;
  
  Particle(float x, float y, float z) {
    pos = new PVector(x, y, z);
    vel = PVector.random3D();
    vel.mult(2);
    acc = new PVector(0, 0, 0);
    life = maxLife = random(100, 200);
    col = color(random(100, 255), random(100, 255), random(150, 255), 150);
    size = random(2, 8);
  }
  
  void update() {
    vel.add(acc);
    pos.add(vel);
    acc.mult(0);
    life--;
    
    // Wrap around edges
    if (pos.x < -width/2) pos.x = width/2;
    if (pos.x > width/2) pos.x = -width/2;
    if (pos.y < -height/2) pos.y = height/2;
    if (pos.y > height/2) pos.y = -height/2;
    if (pos.z < -400) pos.z = 400;
    if (pos.z > 400) pos.z = -400;
  }
  
  void applyForce(PVector force) {
    acc.add(force);
  }
  
  void display(PGraphics pg) {
    pg.pushMatrix();
    pg.translate(pos.x, pos.y, pos.z);
    pg.noStroke();
    pg.fill(col);
    pg.sphere(size * (life / maxLife));
    pg.popMatrix();
  }
  
  boolean isDead() {
    return life <= 0;
  }
}

// Wave class for flowing wave effects
class Wave {
  float amplitude, frequency, phase, speed;
  float yOffset;
  color waveColor;
  
  Wave(float amp, float freq, float ph, float spd, float yOff) {
    amplitude = amp;
    frequency = freq;
    phase = ph;
    speed = spd;
    yOffset = yOff;
    waveColor = color(random(50, 200), random(100, 255), random(100, 255), 100);
  }
  
  void update() {
    phase += speed;
  }
  
  void display(PGraphics pg) {
    pg.stroke(waveColor);
    pg.strokeWeight(3);
    pg.noFill();
    pg.beginShape();
    for (float x = -width/2; x < width/2; x += 10) {
      float y = sin(x * frequency + phase) * amplitude + yOffset;
      pg.vertex(x, y, 0);
    }
    pg.endShape();
  }
}

// Organic shape class for flowing organic forms
class OrganicShape {
  PVector[] points;
  float angle, speed, radius;
  color shapeColor;
  int numPoints;
  
  OrganicShape(int numPts, float rad, float spd) {
    numPoints = numPts;
    radius = rad;
    speed = spd;
    angle = random(TWO_PI);
    points = new PVector[numPoints];
    shapeColor = color(random(100, 255), random(100, 255), random(100, 255), 80);
    
    for (int i = 0; i < numPoints; i++) {
      float a = TWO_PI * i / numPoints;
      float r = radius + random(-20, 20);
      points[i] = new PVector(cos(a) * r, sin(a) * r, 0);
    }
  }
  
  void update() {
    angle += speed;
  }
  
  void display(PGraphics pg) {
    pg.pushMatrix();
    pg.rotateZ(angle);
    pg.translate(0, 0, sin(angle * 2) * 50);
    
    pg.fill(shapeColor);
    pg.stroke(shapeColor);
    pg.strokeWeight(1);
    
    pg.beginShape();
    for (int i = 0; i < numPoints; i++) {
      pg.vertex(points[i].x, points[i].y, points[i].z);
    }
    pg.endShape(CLOSE);
    
    pg.popMatrix();
  }
}

void setup()
{

  size(1024, 768, P3D);
  canvas = createGraphics(1024, 768, P3D);
  //colorMode(RGB, 400, 200, 200);
  // Let's pick an angle 0 to 90 degrees based on the mouse position
  float a = (mouseX / (float) width) * 90f;
  // Convert it to radians

  // On mac you will need to use MMJ since Apple's MIDI subsystem doesn't properly support SysEx.
  // However MMJ doesn't support sending timestamps so you have to turn off timestamps.
  //MidiBus.list();
  //myBus = new MidiBus(this, 4, 4);
  videoExport = new VideoExport(this, "output.mp4");
    videoExport.setFrameRate(30);
  fx = new PostFX(this);
  //canvas = createGraphics(width, height, P3D);
  // compile shaders in setup
  fx.preload(BloomPass.class);
  fx.preload(RGBSplitPass.class);

  // Create syhpon server to send frames out.
  //syphon = new Syphon(this, "Syphon src");
  spout = new Spout(this);

  // Create a Sound object and select the second sound device (device ids start at 0) for input
  Sound s = new Sound(this);
  s.list();
  s.inputDevice(6); // mode 16 jako karta
  // kanal 14
  frameRate(120);
  //img = loadImage("sample.png");  // Load the image into the program

  // Create an Audio input and grab the 1st channel
  input = new AudioIn(this, 0);

  // Begin capturing the audio input
  input.start();
    
   loudness = new Amplitude(this);

  // Patch the input to the volume analyzer
  loudness.input(input);
  //noStroke();
  colorMode(RGB, 1);
  //smooth(2);
  background(numChars/2);
  // Set a hue value for each key
  for (int i = 0; i < numChars; i++) {
    colors[i] = color(i, numChars, numChars);
  }

  initializeOrbits();

  canvas = createGraphics(width, height, P3D);

  // Initialize Spout
  spout = new Spout(this);

  // Initialize PeasyCam
  cam = new PeasyCam(this, width / 2, height / 2, 400, 500);
  //cam.rotateY(radians(90));
  //cam.rotateZ(radians(-90));

  // Initialize new graphical elements
  particles = new ArrayList<Particle>();
  waves = new ArrayList<Wave>();
  organicShapes = new ArrayList<OrganicShape>();
  
  // Create particles
  for (int i = 0; i < numParticles; i++) {
    particles.add(new Particle(random(-width/2, width/2), random(-height/2, height/2), random(-400, 400)));
  }
  
  // Create waves
  for (int i = 0; i < numWaves; i++) {
    float yOff = map(i, 0, numWaves-1, -height/3, height/3);
    waves.add(new Wave(random(20, 60), random(0.005, 0.02), random(TWO_PI), random(0.02, 0.08), yOff));
  }
  
  // Create organic shapes
  for (int i = 0; i < numShapes; i++) {
    organicShapes.add(new OrganicShape(int(random(6, 12)), random(30, 80), random(0.01, 0.03)));
  }
}

void draw()
{
 if (recording) {
        videoExport.saveFrame();
        if (millis() - recordStartTime > 10000) {
            videoExport.endMovie(); // Stop recording properly
            recording = false;
        }
    }

  float inputLevel = map(mouseY, 0, height, 1.0, 0.0);
  input.amp(inputLevel);
  text("Mouse Wheel Value: " + scrollValue, 50, height / 2);
  // loudness.analyze() return a value between 0 and 1. To adjust
  // the scaling and mapping odfdfdsfdfdfdgsgsgsgsgsgsgsgsggsgsgsggsgsgsggsgf an ellipse we scale from 0 to 0.5
  float volume = loudness.analyze();
  int size = int(map((volume), 0, 0.5, 1, 255));
  //  int pitch = 64;
  //int velocity = 127;
  //Note note = new Note(0, pitch, velocity);
  //int  number = (int) random(1,100);
  //  ControlChange change = new

 
  
 canvas.beginDraw();  // Start drawing on canvas
  canvas.background(0);
  canvas.lights();
  canvas.translate(width / 2, height / 2, -200);
  
  // Update time and noise
  time += 0.01;
  noiseOffset += 0.005;
  
  // Create flowing force field based on audio
  float audioForce = map(volume, 0, 0.5, 0.1, 2.0);
  
  // Update and display particles
  for (int i = particles.size() - 1; i >= 0; i--) {
    Particle p = particles.get(i);
    
    // Apply flowing force field
    float noiseVal = noise(p.pos.x * 0.01 + noiseOffset, p.pos.y * 0.01 + noiseOffset, p.pos.z * 0.01 + noiseOffset);
    PVector flowForce = new PVector(
      sin(noiseVal * TWO_PI) * audioForce,
      cos(noiseVal * TWO_PI) * audioForce,
      sin(noiseVal * PI) * audioForce * 0.5
    );
    p.applyForce(flowForce);
    
    p.update();
    p.display(canvas);
    
    if (p.isDead()) {
      particles.remove(i);
      particles.add(new Particle(random(-width/2, width/2), random(-height/2, height/2), random(-400, 400)));
    }
  }
  
  // Update and display waves
  for (Wave w : waves) {
    w.update();
    w.display(canvas);
  }
  
  // Update and display organic shapes
  for (OrganicShape os : organicShapes) {
    os.update();
    os.display(canvas);
  }
  
  // Add some flowing geometric elements based on scroll value
  if (scrollValue > 0) {
    canvas.pushMatrix();
    canvas.rotateX(angle * 0.5);
    canvas.rotateY(angle * 0.3);
    canvas.rotateZ(angle * 0.7);
    
    // Flowing helix
    canvas.stroke(255, 100, 150, 120);
    canvas.strokeWeight(2);
    canvas.noFill();
    canvas.beginShape();
    for (int i = 0; i < 200; i++) {
      float t = i * 0.1;
      float x = cos(t) * (100 + scrollValue * 5);
      float y = sin(t) * (100 + scrollValue * 5);
      float z = t * 2 - 200;
      canvas.vertex(x, y, z);
    }
    canvas.endShape();
    
    // Flowing torus
    canvas.pushMatrix();
    canvas.rotateX(angle * 0.8);
    canvas.noFill();
    canvas.stroke(100, 255, 200, 100);
    canvas.strokeWeight(1);
    for (int i = 0; i < 20; i++) {
      float a = i * TWO_PI / 20;
      canvas.beginShape();
      for (int j = 0; j < 20; j++) {
        float b = j * TWO_PI / 20;
        float r = 80 + scrollValue * 3;
        float x = (r + 30 * cos(b)) * cos(a);
        float y = (r + 30 * cos(b)) * sin(a);
        float z = 30 * sin(b);
        canvas.vertex(x, y, z);
      }
      canvas.endShape();
    }
    canvas.popMatrix();
    
    canvas.popMatrix();
  }

  canvas.endDraw();  // End drawing on canvas
  angle += 0.01;
  
  fx.render(canvas).brightPass(0.1).chromaticAberration().rgbSplit(500).compose(canvas);
  spout.sendTexture(canvas);
  image(canvas, 0, 0);  // Display the canvas onto the main window
}


void initializeOrbits() {
  int numPoints = 100;
  orbitPoints = new PVector[numPoints];
  for (int i = 0; i < numPoints; i++) {
    float theta = TWO_PI * i / numPoints;
    orbitPoints[i] = new PVector(cos(theta), sin(theta));
  }
}
void drawOrbits() {
  canvas.stroke(0);
  canvas.strokeWeight(8);
  for (int i = 0; i < orbitPoints.length; i++) {
    PVector p = orbitPoints[i];
    float x = width / 2 + cos(angle + p.x * TWO_PI) * 100;
    float y = height / 2 + sin(angle + p.y * TWO_PI) * 100;
    canvas.line(width / 2, height / 2, x, y);
  }
}
void drawSphere() {
  canvas.pushMatrix();
  canvas.translate(width / 2, height / 2);
  canvas.noFill();
  canvas.stroke(0);
  canvas.strokeWeight(2);
  canvas.sphereDetail(30);
  canvas.sphere(sphereRadius);
  canvas.popMatrix();
}
void drawDashedOrbit(float radius, float angle) {
  int numDashes = 40;  // Number of dashes per orbit
  float dashLength = TWO_PI * radius / (numDashes * 2); // Length of each dash

  canvas.stroke(0);
  canvas.strokeWeight(8);

  for (int i = 0; i < numDashes; i++) {
    float startAngle = angle + i * TWO_PI / numDashes;
    float endAngle = startAngle + dashLength / radius;

    float x1 = radius * cos(startAngle);
    float y1 = radius * sin(startAngle);
    float x2 = radius * cos(endAngle);
    float y2 = radius * sin(endAngle);

    canvas.line(x1, y1, x2, y2);
  }
}

void drawCube(float size) {
  canvas.fill(255, 50); // Translucent effect
  canvas.stroke(255);
  canvas.box(size);
}
// Detect mouse wheel movement
void mouseWheel(MouseEvent event) {
  scrollValue += event.getCount(); // Positive for scrolling down, negative for up
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
