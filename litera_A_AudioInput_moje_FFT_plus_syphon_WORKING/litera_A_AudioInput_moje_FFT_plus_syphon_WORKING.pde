/**
 * Grab audio from the microphone input and draw a circle whose size
 * is determined by how loud the audio input is.
 */

import processing.sound.*;
import codeanticode.syphon.*;
PGraphics canvas;
SyphonServer server;


import themidibus.*; //Import the library
import javax.sound.midi.MidiMessage; //Import the MidiMessage classes http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/midi/MidiMessage.html
import javax.sound.midi.SysexMessage;
import javax.sound.midi.ShortMessage;
MidiBus myBus;


int currentColor = 0;
int midiDevice  = 3;

AudioIn input;
Amplitude loudness;
MidiMessage message;

float theta;

float xmag, ymag = 0;
float newXmag, newYmag = 0;

float a;                 // Angle of rotation
float offset = PI/24.0;  // Angle offset between boxes
int num = 33;
int rad = 0 ;

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
import java.lang.reflect.Method;



void setup() {
  size(800, 600, P2D);
  background(242);
  colorMode(RGB, 400);
  canvas = createGraphics(800, 600, P3D);
  canvas.colorMode(RGB, 400, 200, 200);
  canvas.smooth();
  // Let's pick an angle 0 to 90 degrees based on the mouse position
  float a = (mouseX / (float) width) * 90f;
  // Convert it to radians

  // On mac you will need to use MMJ since Apple's MIDI subsystem doesn't properly support SysEx.
  // However MMJ doesn't support sending timestamps so you have to turn off timestamps.
  //MidiBus.list();
  //myBus = new MidiBus(this, 4, 4);



  // Create syhpon server to send frames out.
  server = new SyphonServer(this, "Processing Syphon");

  // Create a Sound object and select the second sound device (device ids start at 0) for input
  Sound s = new Sound(this);
  s.inputDevice(6); // mode 16 jako karta
  // kanal 14

  // Now get the first audio input channel from that sound device (ids again start at 0)

  // Create an Audio input and grab the 1st channel
  input = new AudioIn(this, 1);
  s.list();
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
  int size = int(map((volume), 0, 0.5, 1, 379));
  //  int pitch = 64;
  //int velocity = 127;
  //Note note = new Note(0, pitch, velocity);
  //int  number = (int) random(1,100);
  //  ControlChange change = new ControlChange(0, number, velocity);


  //print(size + " " );
  canvas.beginDraw();
  canvas.background(0);
  canvas.lights();
  canvas.fill(random(-113), random(255), random(255), 0);
  canvas.rotateX(frameCount * 0.0003);
  canvas.rotateY(frameCount * 0.0001);
  canvas.scale(0.8);

  canvas.translate(width/2, height/2);
 float cameraY = height/2.0;
  float fov = mouseX/float(width) * PI/2;
  float cameraZ = cameraY / tan(fov / 2.0);
  float aspect = float(width)/float(height);
  if (mousePressed) {
    aspect = aspect / 2.0;
  }
  canvas.perspective(fov, aspect, cameraZ/10.0, cameraZ*10.0);
  
  canvas.translate(width/2+30, height/2, 0);
  canvas.rotateX(-PI/6);
  canvas.rotateY(PI/3 + mouseY/float(height) * PI);
  //canvas.fill(random(-113), random(255), random(255));
  canvas.fill(random(1,55), random(1,255), random(1,255), 44);
  for (int i = 0; i < num; i++) {
    canvas.textSize(256);
    canvas.text('a', 1, 2);

    for (int j = 0; j < 4; j++) {
      rad += 0.01;
      for (int k = 0; k < 30; k++) {
        float x = width / 2 + 100 * sin(rad + 0.5*k);
        float y = height / 2 + 100 * cos(rad+ 0.5*k);


        if (rad + 0.5*k < 2 * PI) {
          canvas.textSize(256);
          canvas.rotateX(frameCount * 0.0003);
          canvas.text('a', x, y);
        }
      }
    }

    canvas.textSize(356);
    canvas.text('a', 1, 2);
  }

  a += 0.001/size;

  //canvas.colorMode(RGB, width, height, 10);
  canvas.endDraw();
  image(canvas, 0, 0);
  server.sendImage(canvas);
}


void mousePressed() {
  println("test");
  strokeWeight(15);
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
