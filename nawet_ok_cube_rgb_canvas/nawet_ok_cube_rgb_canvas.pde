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
  
  for (int i = 0; i < numCubes*scrollValue; i++) {
    canvas.pushMatrix();
    canvas.rotateX(angle * (i + 1) * 0.02);
    canvas.rotateY(angle * (i + 1) * 0.02);
    drawCube(i * 10 + 50);
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
