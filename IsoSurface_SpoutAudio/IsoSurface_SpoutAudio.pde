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



VideoExport videoExport;

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
float smoothedSize = 0.0;
float smoothingFactor = 0.05;
Spout spout;

import peasy.*;


import ComputationalGeometry.*;
import peasy.*;
import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;

IsoSurface iso;

void setup() {
  size(1024, 768, P3D);
  cam = new PeasyCam(this, width / 2, height / 2, 400, 500);
  fx = new PostFX(this);
  //  fx.preload(BloomPass.class);
  //fx.preload(RGBSplitPass.class);


  videoExport = new VideoExport(this, "interactive.mp4");
  videoExport.setFrameRate(60);
  frameRate(60);
  videoExport.setQuality(100, 0);

  // // Everything as by default except -vf (video filter)
  //videoExport.setFfmpegVideoSettings(
  //  new String[]{
  //  "[ffmpeg]",                       // ffmpeg executable
  //  "-y",                             // overwrite old file
  //  "-f",        "rawvideo",          // format rgb raw
  //  "-vcodec",   "rawvideo",          // in codec rgb raw
  //  "-s",        "[width]x[height]",  // size
  //  "-pix_fmt",  "rgb24",             // pix format rgb24
  //  "-r",        "[fps]",             // frame rate
  //  "-i",        "-",                 // pipe input

  //                                    // video filter with vignette, blur,
  //                                    // noise and text. font commented out
  //  "-vf", "vignette,gblur=sigma=1,noise=alls=10:allf=t+u," +
  //  "drawtext=text='Made with Processing':x=50:y=(h-text_h-50):fontsize=24:fontcolor=white@0.8",
  //  // drawtext=fontfile=/path/to/a/font/myfont.ttf:text='Made...

  //  "-an",                            // no audio
  //  "-vcodec",   "h264",              // out codec h264
  //  "-pix_fmt",  "yuv420p",           // color space yuv420p
  //  "-crf",      "[crf]",             // quality
  //  "-metadata", "comment=[comment]", // comment
  //  "[output]"                        // output file
  //  });

  //// Everything as by default. Unused: no audio in this example.
  //videoExport.setFfmpegAudioSettings(new String[]{
  //  "[ffmpeg]",                       // ffmpeg executable
  //  "-y",                             // overwrite old file
  //  "-i",        "[inputvideo]",      // video file path
  //  "-i",        "[inputaudio]",      // audio file path
  //  "-filter_complex", "[1:0]apad",   // pad with silence
  //  "-shortest",                      // match shortest file
  //  "-vcodec",   "copy",              // don't reencode vid
  //  "-acodec",   "aac",               // aac audio encoding
  //  "-b:a",      "[bitrate]k",        // bit rate (quality)
  //  "-metadata", "comment=[comment]", // comment
  //  // https://stackoverflow.com/questions/28586397/ffmpeg-error-while-re-encoding-video#28587897
  //  "-strict",   "-2",                // enable aac
  //  "[output]"                        // output file
  //  });




  //videoExport.startMovie();

  // Creating the Isosurface
  iso = new IsoSurface(this, new PVector(0, 0, 0), new PVector(210, 100, 200), 12);
  canvas = createGraphics(width, height, P3D);

  // Adding Data to the Isosurface
  for (int i = 0; i < 10; i++) {
    PVector pt = new PVector(random(100), random(100), random(100));
    iso.addPoint(pt);
  }

  //syphon = new Syphon(this, "Syphon src");
  spout = new Spout(this);

  // Create a Sound object and select the second sound device (device ids start at 0) for input
  Sound s = new Sound(this);
  s.list();
  s.inputDevice(11); // mode 16 jako karta
  // kanal 14
  input = new AudioIn(this, 0);
  input.start();
  loudness = new Amplitude(this);
  loudness.input(input);
}


void draw() {

  float inputLevel = map(mouseY, 0, height, 1.0, 0.0);
  input.amp(inputLevel);

  // loudness.analyze() return a value between 0 and 1. To adjust
  // the scaling and mapping odfdfdsfdfdfdgsgsgsgsgsgsgsgsggsgsgsggsgsgsggsgf an ellipse we scale from 0 to 0.5
  float volume = loudness.analyze();
  int rawSize = int(map(volume, 0, 0.7, 1, 255));

  // Apply smoothing
  smoothedSize += (rawSize - smoothedSize) * smoothingFactor;
  background(220);
  lights();
  scale(1.2);
  // Plot Voxel Space
  noFill();
  stroke(0, 10);
  iso.plotVoxels();

  rotateX(radians(smoothedSize/90));
  //cam.rotateY(radians(size/90));
  // Plot Surface at a Threshold
  fill(255, 255, 0);
  iso.plot(mouseX / 10000.0*smoothedSize);

  // Apply FX post-processing effects (example: blur)
  fx.render().brightPass(0.1).chromaticAberration().rgbSplit(500).compose();

  //videoExport.saveFrame();
}

void keyPressed() {
  if (key == 'q') {
    //videoExport.endMovie();
    //exit();
  }
}
