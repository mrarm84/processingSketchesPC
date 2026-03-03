/**
 * Grab audio from the microphone input and draw a circle whose size
 * is determined by how loud the audio input is.
 */

import processing.sound.*;
import codeanticode.syphon.*;
PGraphics canvas;
SyphonServer server;



AudioIn input;
Amplitude loudness;

float theta;   

float xmag, ymag = 0;
float newXmag, newYmag = 0; 

float a;                 // Angle of rotation
float offset = PI/24.0;  // Angle offset between boxes
int num = 11;    




float boxSize = 20;
float margin = boxSize*2;
float depth = 400;
color boxFill;

int fcount, lastm;
float frate;
int fint = 3;


void setup() {
  size(800, 600, P3D);
  background(242);
  colorMode(RGB, 2); 
  canvas = createGraphics(800, 600, P3D);

// Let's pick an angle 0 to 90 degrees based on the mouse position
  float a = (mouseX / (float) width) * 90f;
  // Convert it to radians

  
  
  
  // Create syhpon server to send frames out.
  server = new SyphonServer(this, "Processing Syphon");
  
  // Create an Audio input and grab the 1st channel
  input = new AudioIn(this, 0);
  Sound.list();
  rectMode(CENTER);

  // Begin capturing the audio input
  input.start();
  // start() activates audio capture so that you can use it as
  // the input to live sound analysis, but it does NOT cause the
  // captured audio to be played back to you. if you also want the
  // microphone input to be played back to you, call
   //  input.play();
  // instead (be careful with your speaker volume, you might produce
  // painful audio feedback. best to first try it out wearing headphones!)

  // Create a new Amplitude analyzer
  loudness = new Amplitude(this);

  // Patch the input to the volume analyzer
  loudness.input(input);
}


void draw() {
  // Adjust the volume of the audio input based on mouse position
  float inputLevel = map(mouseY, 0, height, 1.0, 0.0);
  input.amp(inputLevel);

  // loudness.analyze() return a value between 0 and 1. To adjust
  // the scaling and mapping of an ellipse we scale from 0 to 0.5
  float volume = loudness.analyze();
  int size = int(map((volume), 0, 0.5, 1, 350));


  canvas.beginDraw();
  //canvas.lights();
  //canvas.rotateX(frameCount * 0.01);
  //canvas.rotateY(frameCount * 0.01);  
  //canvas.box(150, 150, 150+size*10);
  
   canvas.translate(width/2, height/2); 
    //defineLights();
 
 
   
  
  //canvas.popMatrix();

  
  
  
  
  canvas.endDraw();
  image(canvas, 0, 0);
    server.sendImage(canvas);
 
  
}



void mousePressed() {
  redraw();
}

void defineLights() {
  // Orange point light on the right
  pointLight(150, 100, 0,   // Color
             200, -150, 0); // Position

  // Blue directional light from the left
  directionalLight(0, 102, 255, // Color
                   1, 0, 0);    // The x-, y-, z-axis direction

  // Yellow spotlight from the front
  spotLight(255, 255, 109,  // Color
            0, 40, 200,     // Position
            0, -0.5, -0.5,  // Direction
            PI / 2, 2);     // Angle, concentration
}
