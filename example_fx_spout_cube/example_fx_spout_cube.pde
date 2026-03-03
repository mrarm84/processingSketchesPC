import peasy.PeasyCam;

PeasyCam cam;
PGraphics canvas;

import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;

import processing.sound.*;
import spout.*;

PShape sh;

PostFX fx;

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

boolean newletter;

int numChars = 26;      // There are 26 characters in the alphabet
color[] colors = new color[numChars];



PImage img;  // Declare variable "a" of type PImage


void setup()
{
  
   size(1024, 768, P2D);
  canvas = createGraphics(1024, 768, P3D);
  //colorMode(RGB, 400, 200, 200);
  // Let's pick an angle 0 to 90 degrees based on the mouse position
  float a = (mouseX / (float) width) * 90f;
  // Convert it to radians

  // On mac you will need to use MMJ since Apple's MIDI subsystem doesn't properly support SysEx.
  // However MMJ doesn't support sending timestamps so you have to turn off timestamps.
  //MidiBus.list();
  //myBus = new MidiBus(this, 4, 4);

  fx = new PostFX(this);  
  canvas = createGraphics(width, height, P3D);
 // compile shaders in setup
  fx.preload(BloomPass.class);
  fx.preload(RGBSplitPass.class);

  // Create syhpon server to send frames out.
  //syphon = new Syphon(this, "Syphon src");
  spout = new Spout(this);

  // Create a Sound object and select the second sound device (device ids start at 0) for input
  Sound s = new Sound(this);
    s.list();
s.inputDevice(11); // mode 16 jako karta
  // kanal 14
  frameRate(120);
  img = loadImage("sample.png");  // Load the image into the program

  // Create an Audio input and grab the 1st channel
  input = new AudioIn(this, 0);

  // Begin capturing the audio input
  input.start();
  // start() activates audio capture so that you can use it as
  // the input to live sound analysis, but it does NOT cause the
  // captured audio to be played back to you. if you also want the
  // microphone input to be played back to you, call
  //  input.play();
  // instead (be careful with your speaker volume, you might produce
  // painful audio feedback. best to first try it out wearing headphones!)
  //sh = loadShape("skull.svg");

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
  
  cam = new PeasyCam(this, 400);

  canvas = createGraphics(width, height, P3D);
}

void draw()
{
  
  
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

  float cameraY = height/2.0;
  float cameraX = width/2.0;
  float fov = mouseX/float(width) * PI/2;
  float cameraZ = cameraY / tan(fov / 2.0);
  float aspect = float(width)/float(height);
  background(0);

  // draw a simple rotating cube around a sphere onto an offscreen canvas
  canvas.beginDraw();
  canvas.background(55);

  canvas.pushMatrix();

  canvas.rotateX(radians(frameCount % 360));
  canvas.rotateZ(radians(frameCount % 360));

  canvas.noStroke();
  canvas.fill(20, 20, 20);
  canvas.box(100 );

  canvas.fill(150, 255, 255);
  canvas.sphere(60);

 

    for (int i = 0; i < num; i++) {
      float gray = map(i, 0, num-1, 0, 255);
      canvas.pushMatrix();
      canvas.rotateY(a + offset*i);
      canvas.rotateX(a/2 + offset*i);
      //box(200);
      if (newletter == true) {
        // Draw the "letter"
        int y_pos;
        if (letterHeight == maxHeight) {
          y_pos = y;
          canvas.rect( x, y_pos, letterWidth, letterHeight );
        } else {
          y_pos = y + minHeight;
          canvas.rect( x, y_pos, letterWidth, letterHeight );
          //fill(numChars/2);
          canvas.rect( x, y_pos-minHeight, letterWidth, letterHeight );
        }
        newletter = false;
      }

      canvas.popMatrix();
    }


  canvas.popMatrix();
  canvas.endDraw();

  // apply view matrix of peasy to canvas
  cam.getState().apply(canvas);

  // draw canvas onto onscreen
  image(canvas, 0, 0);
  
  blendMode(BLEND);

  // add bloom filter
  blendMode(SCREEN);
  fx.render(canvas)
    .brightPass(0.5)
    .chromaticAberration()
    .rgbSplit(500)
    .blur(5, 10)
    .compose();
    

      image(canvas, 0, 0);

    
  blendMode(selMode);

  //perspective(fov, aspect, cameraZ/10.0, cameraZ*10.0);
  
  
  spout.sendTexture(canvas);

}
