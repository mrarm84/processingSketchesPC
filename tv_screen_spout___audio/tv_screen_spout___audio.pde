 
import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;

import processing.sound.*;
PGraphics canvas;
import spout.*;

PShape sh;

PostFX fx;

 

int currentColor = 0;
int midiDevice  = 3;

AudioIn input;
Amplitude loudness;

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

PeasyCam cam;

import java.lang.reflect.Method;


int maxHeight = 40;
int minHeight = 20;
int letterHeight = maxHeight; // Height of the letters
int letterWidth = 20;          // Width of the letter

int x = -letterWidth;          // X position of the letters
int y = 0;                      // Y position of the letters

boolean newletter;

int numChars = 26;      // There are 26 characters in the alphabet
color[] colors = new color[numChars];



PImage img;  // Declare variable "a" of type PImage



void setup() {
  size(1024, 768, P3D);
  canvas = createGraphics(1024, 768, P3D);
  float a = (mouseX / (float) width) * 90f;
  

  fx = new PostFX(this);
  canvas = createGraphics(width, height, P3D);
  // compile shaders in setup
  fx.preload(BloomPass.class);
  fx.preload(RGBSplitPass.class);

  spout = new Spout(this);

  // Create a Sound object and select the second sound device (device ids start at 0) for input
  Sound s = new Sound(this);
  s.list();
  s.inputDevice(7); // mode 16 jako karta
  // kanal 14
  frameRate(120);
  img = loadImage("sample.png");  // Load the image into the program

  // Create an Audio input and grab the 1st channel
  input = new AudioIn(this, 0);

  // Begin capturing the audio input
  input.start();

  // Create a new Amplitude analyzer
  loudness = new Amplitude(this);

  // Patch the input to the volume analyzer
  loudness.input(input);
  //noStroke();
  noStroke();
  colorMode(RGB, 1);
  smooth(4);
  background(numChars/2);
  // Set a hue value for each key
  for (int i = 0; i < numChars; i++) {
    colors[i] = color(i, numChars, numChars);
  }

  cam = new PeasyCam(this, width/2, height/2, depth/2, depth*1.25); //generate the camera

 cam.rotateY(radians(90));
  cam.rotateZ(radians(-90));
}


void draw() {
  // Adjust the volume of the audio input based on mouse position
  float inputLevel = map(mouseY, 0, height, 1.0, 0.0);
  input.amp(inputLevel);

  // loudness.analyze() return a value between 0 and 1. To adjust
  // the scaling and mapping odfdfdsfdfdfdgsgsgsgsgsgsgsgsggsgsgsggsgsgsggsgf an ellipse we scale from 0 to 0.5
  float volume = loudness.analyze();
  int size = int(map((volume), 0, 0.5, 1, 255));
  //  int pitch = 64;
  //int velocity = 127;
  //Note note = new Note(0, pitch, velocity);
  //int  number = (int) random(1,100);
  //  ControlChange change = new

  //print(size);
  float cameraY = height/2.0;
  float cameraX = width/2.0;
  float fov = mouseX/float(width) * PI/2;
  float cameraZ = cameraY / tan(fov / 2.0);
  float aspect = float(width)/float(height);
  background(255);
  pushMatrix();
  translate(width/2, height/2, -30);

  newXmag = mouseX/float(width) * TWO_PI;
  newYmag = mouseY/float(height) * TWO_PI;
  // add bloom filter


  float diff = xmag-newXmag;
  if (abs(diff) >  0.01) {
    xmag -= diff/4.0;
  }
 
  diff = ymag-newYmag;
  if (abs(diff) >  0.01) {
    ymag -= diff/4.0;
  }

  rotateX(-ymag);
  rotateY(-xmag);
  for (int z = 0; z < n; z++) {
    rad += 0.01/size;


    for ( int i = -cubeSize; i <= cubeSize; i += 50) {
      for (int  j = -cubeSize; j <= cubeSize; j += 50) {
        for ( int k = -cubeSize; k <= cubeSize; k += 50) {
          float r = map(sin(frameCount * 0.01 + i + j + k), -1, 1, 0, 255);
          float g = map(sin(frameCount * 0.02 + i + j + k), -1, 1, 0, 255);
          float b = map(sin(frameCount * 0.03 + i + j + k), -1, 1, 0, 255);

          push();
          translate(i, j, k);
          stroke(r, g, b);
          box(15);
          pop();
        }
      }
    }
  }


  for (int i = 0; i < num; i++) {
    float gray = map(i, 0, num-1, 0, 255);
    pushMatrix();
    rotateY(a + offset*i);
    rotateX(a/2 + offset*i);
    //box(200);
    if (newletter == true) {
      // Draw the "letter"
      int y_pos;
      if (letterHeight == maxHeight) {
        y_pos = y;
        rect( x, y_pos, letterWidth, letterHeight );
      } else {
        y_pos = y + minHeight;
        rect( x, y_pos, letterWidth, letterHeight );
        //fill(numChars/2);
        rect( x, y_pos-minHeight, letterWidth, letterHeight );
      }
      newletter = false;
    }

    popMatrix();
  }





  a += 0.01;
  scale(120+size);

  beginShape(QUADS);

  fill(0, 1, 1);
  vertex(-1, 1, 1);
  fill(1, 1, 1);
  vertex( 1, 1, 1);
  fill(1, 0, 1);
  vertex( 1, -1, 1);
  fill(0, 0, 1);
  vertex(-1, -1, 1);

  fill(1, 1, 1);
  vertex( 1, 1, 1);
  fill(1, 1, 0);
  vertex( 1, 1, -1);
  fill(1, 0, 0);
  vertex( 1, -1, -1);
  fill(1, 0, 1);
  vertex( 1, -1, 1);

  fill(1, 1, 0);
  vertex( 1, 1, -1);
  fill(0, 1, 0);
  vertex(-1, 1, -1);
  fill(0, 0, 0);
  vertex(-1, -1, -1);
  fill(1, 0, 0);
  vertex( 1, -1, -1);

  fill(0, 1, 0);
  vertex(-1, 1, -1);
  fill(0, 1, 1);
  vertex(-1, 1, 1);
  fill(0, 0, 1);
  vertex(-1, -1, 1);
  fill(0, 0, 0);
  vertex(-1, -1, -1);

  fill(0, 1, 0);
  vertex(-1, 1, -1);
  fill(1, 1, 0);
  vertex( 1, 1, -1);
  fill(1, 1, 1);
  vertex( 1, 1, 1);
  fill(0, 1, 1);
  vertex(-1, 1, 1);

  fill(0, 0, 0);
  vertex(-1, -1, -1);
  fill(1, 0, 0);
  vertex( 1, -1, -1);
  fill(1, 0, 1);
  vertex( 1, -1, 1);
  fill(1, 0, 1);
  vertex(-1, -1, 1);

  endShape();
  translate(400, 0);

  fx.render()
    .bloom(1.5*size, 50, 40)
    .chromaticAberration()
    .rgbSplit(500)
    .compose();

  popMatrix();

  //translate(width/2, height/2);


  perspective(fov, aspect, cameraZ/10.0, cameraZ*10.0);
  rotateY(frameCount * 0.0001);

  translate(width/2+30, height/2, 0);
  rotateX(-PI/6);
  rotateY(PI/3 + mouseY/float(height) * PI);
  //fill(random(-113), random(255), random(255));
  fill(random(1, 55), random(1, 255), random(1, 255), 11);
  if (mouseY < 1000) {
    n = mouseY/10;
  } else {
    n = 1;
  }

  name ="X";


  //  rotateX(frameCount * 0.0003);
  //  box(200);

  //  //sh.strokeWeight(2);
  //  //sh.setStroke(color(255));
  //  //shape(sh, 140, 140, 640, 640);


  for (int z = 0; z < 122; z++) {
    float gray = map(z, 0, num-1, 0, 255);
    fill(random(1, 55), random(1, 255), random(1, 255), 11);
    rotateY(a + offset*z/size + frameCount/10);
    strokeWeight(1);
    textSize(456);
    pushMatrix();
    //text(name, 1, 2);
    //image(img, 0, 0);
    scale(0.1);
    popMatrix();
  }



  canvas.beginDraw();
  // draw something onto the canvas
  canvas.endDraw();

  blendMode(BLEND);
  image(canvas, 0, 0);

  // add bloom filter
  blendMode(SCREEN);
  fx.render(canvas)
    .brightPass(0.5)
    .chromaticAberration()
    .rgbSplit(500)
    .compose();




  blendMode(selMode);

  perspective(fov, aspect, cameraZ/10.0, cameraZ*10.0);
  //colorMode(RGB, width, height, 10);
  //image(canvas, 0, 0);
  spout.sendTexture();
}


void update()
{
}

void keyPressed()
{
  // If the key is between 'A'(65) to 'Z' and 'a' to 'z'(122)
  if ((key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z')) {
    int keyIndex;
    if (key <= 'Z') {
      keyIndex = key-'A';
      letterHeight = maxHeight;
      fill(colors[keyIndex]);
    } else {
      keyIndex = key-'a';
      letterHeight = minHeight;
      fill(colors[keyIndex]);
    }
  } else {
    fill(0);
    letterHeight = 10;
  }

  newletter = true;

  // Update the "letter" position
  x = ( x + letterWidth );

  // Wrap horizontally
  if (x > width - letterWidth) {
    x = 0;
    y+= maxHeight;
  }

  // Wrap vertically
  if ( y > height - letterHeight) {
    y = 0;      // reset y to 0
  }
}

void mousePressed() {

  if (selMode == REPLACE) {
    img = loadImage("sample.png");  // Load the image into the program
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




void defineLights() {
  // Orange point light on the right
  pointLight(150, 100, 0, // Color
    200, -150, 0); // Position

  // Blue directional light from the left
  directionalLight(0, 102, 255, // Color
    1, 0, 0);    // The x-, y-, z-axis direction

  // Yellow spotlight from the front
  spotLight(255, 255, 109, // Color
    0, 40, 200, // Position
    0, -0.5, -0.5, // Direction
    PI / 2, 2);     // Angle, concentration
}
