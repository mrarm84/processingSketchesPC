/**
 * Grab audio from the microphone input and draw a circle whose size
 * is determined by how loud the audio input is.
 */

import processing.sound.*;
PGraphics canvas;
import spout.*;

PShape sh;

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
  size(800, 600, P3D);
  canvas = createGraphics(800, 600, P3D);
  //colorMode(RGB, 400, 200, 200);
  // Let's pick an angle 0 to 90 degrees based on the mouse position
  float a = (mouseX / (float) width) * 90f;
  // Convert it to radians

  // On mac you will need to use MMJ since Apple's MIDI subsystem doesn't properly support SysEx.
  // However MMJ doesn't support sending timestamps so you have to turn off timestamps.
  //MidiBus.list();
  //myBus = new MidiBus(this, 4, 4);



  // Create syhpon server to send frames out.
  //syphon = new Syphon(this, "Syphon src");
  spout = new Spout(this);

  // Create a Sound object and select the second sound device (device ids start at 0) for input
  Sound s = new Sound(this);
  s.inputDevice(8); // mode 16 jako karta
  // kanal 14
  frameRate(120);
  img = loadImage("sample.png");  // Load the image into the program

  // Create an Audio input and grab the 1st channel
  input = new AudioIn(this, 0);
  s.list();

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

  background(numChars/2);
  // Set a hue value for each key
  for (int i = 0; i < numChars; i++) {
    colors[i] = color(i, numChars, numChars);
  }
}


void draw() {
  // Adjust the volume of the audio input based on mouse position
  float inputLevel = map(mouseY, 0, height, 1.0, 0.0);
  input.amp(inputLevel);

  // loudness.analyze() return a value between 0 and 1. To adjust
  // the scaling and mapping odfdfdsfdfdfdgsgsgsgsgsgsgsgsggsgsgsggsgsgsggsgf an ellipse we scale from 0 to 0.5
  float volume = loudness.analyze();
  int size = int(map((volume), 0, 0.5, 1, 379));
  //  int pitch = 64;
  //int velocity = 127;
  //Note note = new Note(0, pitch, velocity);
  //int  number = (int) random(1,100);
  //  ControlChange change = new

  float cameraY = height/2.0;
  float fov = mouseX/float(width) * PI/2;
  float cameraZ = cameraY / tan(fov / 2.0);
  float aspect = float(width)/float(height);
  //background(0);
  stroke(0);
  pushMatrix();
  translate(width/2, height/2, -30);
  strokeWeight(6/size);

  newXmag = size/float(width) * TWO_PI;
  newYmag = size/float(height) * TWO_PI;

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
sphere(size/23+100);
fill(0,0,0,0);
  scale(90);
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
  fill(0, 0, 1);
  vertex(-1, -1, 1);

  endShape();

  popMatrix();
    //translate(width/2, height/2);
    blendMode(selMode);

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

    spout.sendTexture();

  //  rotateX(frameCount * 0.0003);
  //  box(200);

  //  //sh.strokeWeight(2);
  //  //sh.setStroke(color(255));
  //  //shape(sh, 140, 140, 640, 640);


  //  for (int z = 0; z < 44; z++) {
  //    float gray = map(z, 0, num-1, 0, 255);
  //    fill(random(1, 55), random(1, 255), random(1, 255), 11);
  //    rotateY(a + offset*z+size);
  //    strokeWeight(1);
  //    textSize(456);
  //     pushMatrix();
  //   text(name, 1, 2);
  //    image(img, 0, 0);
  //    scale(0.1);
  //    popMatrix();
  //  }

    a += 0.01;
    for (int z = 0; z < n; z++) {
      rad += 0.01;


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


  //  for (int i = 0; i < num; i++) {
  //    float gray = map(i, 0, num-1, 0, 255);
  //    pushMatrix();
  //    rotateY(a + offset*i);
  //    rotateX(a/2 + offset*i);
  //    //box(200);
  //    if (newletter == true) {
  //      // Draw the "letter"
  //      int y_pos;
  //      if (letterHeight == maxHeight) {
  //        y_pos = y;
  //        rect( x, y_pos, letterWidth, letterHeight );
  //      } else {
  //        y_pos = y + minHeight;
  //        rect( x, y_pos, letterWidth, letterHeight );
  //        //fill(numChars/2);
  //        rect( x, y_pos-minHeight, letterWidth, letterHeight );
  //      }
  //      newletter = false;
  //    }

  //    popMatrix();
  //  }

  a += 0.01;

  //for ( int i = -cubeSize; i <= cubeSize; i += 50) {
  //  for (int  j = -cubeSize; j <= cubeSize; j += 50) {
  //    for ( int k = -cubeSize; k <= cubeSize; k += 50) {
  //      float r = map(sin(frameCount * 0.01 + i + j + k), -1, 1, 0, 255);
  //      float g = map(sin(frameCount * 0.02 + i + j + k), -1, 1, 0, 255);
  //      float b = map(sin(frameCount * 0.03 + i + j + k), -1, 1, 0, 255);

  //      push();
  //      translate(i, j, k);
  //      stroke(r, g, b);
  //      box(15);
  //      pop();
  //    }
  //  }
  //}


  perspective(fov, aspect, cameraZ/10.0, cameraZ*10.0);
  //colorMode(RGB, width, height, 10);
  //image(canvas, 0, 0);
  //syphon.send(canvas);
}


void update()
{
}

//void keyPressed()
//{
//  // If the key is between 'A'(65) to 'Z' and 'a' to 'z'(122)
//  if ((key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z')) {
//    int keyIndex;
//    if (key <= 'Z') {
//      keyIndex = key-'A';
//      letterHeight = maxHeight;
//      fill(colors[keyIndex]);
//    } else {
//      keyIndex = key-'a';
//      letterHeight = minHeight;
//      fill(colors[keyIndex]);
//    }
//  } else {
//    fill(0);
//    letterHeight = 10;
//  }

//  newletter = true;

//  // Update the "letter" position
//  x = ( x + letterWidth );

//  // Wrap horizontally
//  if (x > width - letterWidth) {
//    x = 0;
//    y+= maxHeight;
//  }

//  // Wrap vertically
//  if ( y > height - letterHeight) {
//    y = 0;      // reset y to 0
//  }
//}

void mousePressed() {

  if (selMode == REPLACE) {
    img = loadImage("sample.png");  // Load the image into the program
    selMode = BLEND;
    name = "BLEND";
  } else if (selMode == BLEND) {
    selMode = ADD;
    name = "ADD";
  } else if (selMode == ADD) {
    selMode = LIGHTEST;
    name = "LIGHTEST";
  } else if (selMode == LIGHTEST) {
    selMode = DIFFERENCE;
    name = "DIFFERENCE";
  } else if (selMode == DIFFERENCE) {
    selMode = EXCLUSION;
    name = "EXCLUSION";
  } else if (selMode == EXCLUSION) {
    selMode = SCREEN;
    name = "SCREEN";
  } else if (selMode == SCREEN) {
    selMode = REPLACE;
    name = "REPLACE";
  }


  print(name);
}



//void controllerChange(int channel, int number, int value) {
//  // store the midi data for using it inside draw()
//  ccChannel = channel;
//  ccNumber = number;
//  ccValue = value;
//  line(channel, number, value, 22);
//  //println(channel, number, value);
//  //println("testCC");
//}


//// Notice all bytes below are converted to integeres using the following system:
//// int i = (int)(byte & 0xFF)
//// This properly convertes an unsigned byte (MIDI uses unsigned bytes) to a signed int
//// Because java only supports signed bytes, you will get incorrect values if you don't do so

//void rawMidi(byte[] data) { // You can also use rawMidi(byte[] data, String bus_name)
//  // Receive some raw data
//  // data[0] will be the status byte
//  // data[1] and data[2] will contain the parameter of the message (e.g. pitch and volume for noteOn noteOff)
//  //println();
//  //println("Raw Midi Data:");
//  //println("--------");
//  //println("Status Byte/MIDI Command:"+(int)(data[0] & 0xFF));
//  // N.B. In some cases (noteOn, noteOff, controllerChange, etc) the first half of the status byte is the command and the second half if the channel
//  // In these cases (data[0] & 0xF0) gives you the command and (data[0] & 0x0F) gives you the channel
//  for (int i = 1; i < data.length; i++) {
//    //println("Param "+(i+1)+": "+(int)(data[i] & 0xFF));
//  }
//}

//void midiMessage(MidiMessage message) { // You can also use midiMessage(MidiMessage message, long timestamp, String bus_name)
//  // Receive a MidiMessage
//  // MidiMessage is an abstract class, the actual passed object will be either javax.sound.midi.MetaMessage, javax.sound.midi.ShortMessage, javax.sound.midi.SysexMessage.
//  // Check it out here http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/midi/package-summary.html
//  println();
//  println("MidiMessage Data:");
//  println("--------");
//  println("Status Byte/MIDI Command:"+message.getStatus());
//  for (int i = 1; i < message.getMessage().length; i++) {
//    println("Param "+(i+1)+": "+(int)(message.getMessage()[i] & 0xFF));
//  }
//}
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
