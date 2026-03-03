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
int num = 22;


int p1; //serves as temp pitch
int v1; //serves as temp velocity



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
  size(800, 600, P3D);
  background(242);
  colorMode(RGB, 1);
  canvas = createGraphics(800, 600, P3D);

  // Let's pick an angle 0 to 90 degrees based on the mouse position
  float a = (mouseX / (float) width) * 90f;
  // Convert it to radians


  // On mac you will need to use MMJ since Apple's MIDI subsystem doesn't properly support SysEx.
  // However MMJ doesn't support sending timestamps so you have to turn off timestamps.
  MidiBus.list();
  myBus = new MidiBus(this, 4, 4);



  // Create syhpon server to send frames out.
  server = new SyphonServer(this, "Processing Syphon");

  // Create a Sound object and select the second sound device (device ids start at 0) for input
  Sound s = new Sound(this);
  s.inputDevice(12); // mode 16 jako karta
  // kanal 14

  // Now get the first audio input channel from that sound device (ids again start at 0)

  // Create an Audio input and grab the 1st channel
  input = new AudioIn(this, 14);
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
  int size = int(map((volume), 0, 0.5, 1, 350));
    int pitch = 64;
  int velocity = 127;
  Note note = new Note(0, pitch, velocity);
  int  number = (int) random(1,100);
    ControlChange change = new ControlChange(0, number, velocity);


//print(velocity);
  canvas.beginDraw();
  canvas.background(0);
  canvas.lights();
  canvas.rotateX(frameCount * 0.00001);
  //canvas.rotateY(frameCount * 0.01);  
  
   canvas.translate(width/2, height/2); 
    defineLights();
  canvas.box(150, 150, 150+size*10);
  canvas.box(150, 150, 150+size*10);
    canvas.fill(random(1,255), random(1,255), random(1,255));
    canvas.smooth();

  for(int i = 0; i < num; i++) {
    float gray = map(random(size), random(size), random(size), random(size), 222);
    float zzz = map(random(size), 22, num-20, size/num, 222);
    
    canvas.pushMatrix();
    canvas.lights();
    canvas.saturation(size*120);
    canvas.box(150, 150, 150+size*10);
  canvas.hue(size*120);
     canvas.stroke(num-44/size,num-44,random(num-44));
     canvas.strokeWeight(random(16));
     float s = cos(a)*2+size;
    canvas.fill(gray, gray, gray, zzz);
    canvas.rotateY(1/a + offset*i);
    canvas.rotateX(a/2 + offset*i);
    canvas.box(290);
    canvas.scale(s); 

    canvas.popMatrix();
  }
  
  a += size*0.0001;    


  canvas.endDraw();
  image(canvas, 0, 0);
  server.sendImage(canvas);
}

void noteOn(int channel, int pitch, int velocity) {
  p1=pitch;
  v1=velocity;
  //println("pitch : "+pitch);
 // println("velocity : "+velocity);
}

 

void mousePressed() {
  println("test");
  strokeWeight(2);
}



void controllerChange(int channel, int number, int value) {
  // store the midi data for using it inside draw()
  ccChannel = channel;
  ccNumber = number;
  ccValue = value;
  line(channel, number, value, 22);
  println(channel, number, value);
  println("testCC");
}


// Notice all bytes below are converted to integeres using the following system:
// int i = (int)(byte & 0xFF)
// This properly convertes an unsigned byte (MIDI uses unsigned bytes) to a signed int
// Because java only supports signed bytes, you will get incorrect values if you don't do so

void rawMidi(byte[] data) { // You can also use rawMidi(byte[] data, String bus_name)
  // Receive some raw data
  // data[0] will be the status byte
  // data[1] and data[2] will contain the parameter of the message (e.g. pitch and volume for noteOn noteOff)
  println();
  println("Raw Midi Data:");
  println("--------");
  println("Status Byte/MIDI Command:"+(int)(data[0] & 0xFF));
  // N.B. In some cases (noteOn, noteOff, controllerChange, etc) the first half of the status byte is the command and the second half if the channel
  // In these cases (data[0] & 0xF0) gives you the command and (data[0] & 0x0F) gives you the channel
  for (int i = 1; i < data.length; i++) {
    println("Param "+(i+1)+": "+(int)(data[i] & 0xFF));
  }
}

void midiMessage(MidiMessage message) { // You can also use midiMessage(MidiMessage message, long timestamp, String bus_name)
  // Receive a MidiMessage
  // MidiMessage is an abstract class, the actual passed object will be either javax.sound.midi.MetaMessage, javax.sound.midi.ShortMessage, javax.sound.midi.SysexMessage.
  // Check it out here http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/midi/package-summary.html
  println();
  println("MidiMessage Data:");
  println("--------");
  println("Status Byte/MIDI Command:"+message.getStatus());
  for (int i = 1; i < message.getMessage().length; i++) {
    println("Param "+(i+1)+": "+(int)(message.getMessage()[i] & 0xFF));
  }
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
