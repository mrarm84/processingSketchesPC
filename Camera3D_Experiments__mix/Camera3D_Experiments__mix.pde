/*
This sketch is a utility to help you explore the Camera3D options.
 
 Not all renderers will work with all object and background color
 combinations. Experimenting with the possibilities can be tedious,
 and this sketch is designed to help make that easier.
 
 Don't look at the actual code if you are a beginner. Just run it
 and play with the sliders.
 
 On the other hand, if you want to see some awesome ControlP5
 coding, read on...
 */

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

Spout spout;



import ComputationalGeometry.*;
import peasy.*;
import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;

IsoSurface iso;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import camera3D.*;
import camera3D.generators.*;
import controlP5.*;
import shapes3d.*;
float smoothedSize = 0.0;
float smoothingFactor = 0.05;
int windowWidth;
int windowHeight;

// internal values
float xRot = 0;
float yRot = 0;
float zRot = 0;

// user settings
float logscale = 0;
int shapeStrokeWeight = 5;
float divergence = 3;
float xRotSpeed = 0f;
float yRotSpeed = 0;
float zRotSpeed = 0f;
int xTrans = 0;
int yTrans = 0;
int zTrans = 0;
boolean rgbFlag = true;
int background_v1 = 64;
int background_v2 = 64;
int background_v3 = 64;
int fill_v1 = 255;
int fill_v2 = 255;
int fill_v3 = 255;
int fill_v4 = 255;
int stroke_v1 = 32;
int stroke_v2 = 32;
int stroke_v3 = 32;
int stroke_v4 = 255;

Camera3D camera3D;
ControlP5 cp5;
DropdownList objectList;
ShapeGroup earthSpaceStationGroup;

Map<Integer, String> rendererMenuItems;
String rendererChoices = "Regular P3D Renderer, Default Anaglyph,"
  + "BitMask Filter Red-Cyan, BitMask Filter Magenta-Green,"
  + "True Anaglyph, Gray Anaglyph, Half Color Anaglyph,"
  + "Dubois Red-Cyan, Dubois Magenta-Green, Dubois Amber-Blue,"
  + "Barrel Distortion, Split Depth Illusion, Interlaced,"
  + "Side by Side, Side by Side Half Width,"
  + "Over Under, Over Under Half Height";
Map<Integer, String> objectMenuItems;
String objectChoices = "Box, Sphere, Ring of Spheres, Earth, Custom";

void setup() {
  size(800, 600, P3D);
  fx = new PostFX(this);
  windowWidth = width;
  windowHeight = height;

  videoExport = new VideoExport(this, "interactive.mp4");
  videoExport.setFrameRate(60);
  frameRate(60);
  videoExport.setQuality(100, 0);
  xTrans = width / 2;
  yTrans = height / 2;
  zTrans = -200;

  videoExport.startMovie();
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


  rendererMenuItems = createDropdownMap(rendererChoices);
  objectMenuItems = createDropdownMap(objectChoices);

  createControls();
  camera3D = new Camera3D(this);
  camera3D.renderDefaultAnaglyph();
  camera3D.reportStats();

  Ellipsoid earth = new Ellipsoid(150, 20, 20);
  earth.texture(this, "land_ocean_ice_2048.png");
  earth.drawMode(Shape3D.TEXTURE);

  Box spaceStation = new Box(20, 10, 10);
  spaceStation.fill(128);
  spaceStation.strokeWeight(2);
  spaceStation.stroke(0);
  spaceStation.moveTo(0, 0, 250);

  earthSpaceStationGroup = new ShapeGroup();
  earthSpaceStationGroup.addChild(earth);
  earthSpaceStationGroup.addChild(spaceStation);
}

void createControls() {
  cp5 = new ControlP5(this);
  cp5.setAutoDraw(false);
  cp5.getFont().setSize(12);

  float yOffset = 0.1;
  int controlSpace = 23;

  DropdownList rendererList = addDropdown("Renderer Choices",
    (controlSpace * yOffset++), rendererMenuItems).addListener(
    new RendererListener());
  objectList = addDropdown("Object Choices", (controlSpace * yOffset++),
    objectMenuItems); // .addListener(new ObjectListener());

  addSlider("divergence", "divergence", (controlSpace * yOffset++), -10,
    10).addListener(new DivergenceListener());

  addSlider("xTrans", "x translate", (controlSpace * yOffset++), 0, width);
  addSlider("yTrans", "y translate", (controlSpace * yOffset++), 0,
    height);
  addSlider("zTrans", "z translate", (controlSpace * yOffset++), -500,
    250);

  addSlider("xRot", "x rotation", (controlSpace * yOffset++), 0, 360);
  addSlider("yRot", "y rotation", (controlSpace * yOffset++), 0, 360);
  addSlider("zRot", "z rotation", (controlSpace * yOffset++), 0, 360);

  addSlider("xRotSpeed", "x rotation speed", (controlSpace * yOffset++),
    -5, 5);
  addSlider("yRotSpeed", "y rotation speed", (controlSpace * yOffset++),
    -5, 5);
  addSlider("zRotSpeed", "z rotation speed", (controlSpace * yOffset++),
    -5, 5);

  addSlider("logscale", "object log-scale", (controlSpace * yOffset++), -2,
    2);

  RadioButton rb = cp5.addRadioButton("colorModel")
    .setPosition(10, (controlSpace * yOffset++)).setSize(25, 18)
    .addItem("RGB", 0).addItem("HSB   Color Model", 1).activate(0)
    .setItemsPerRow(2).setSpacingColumn(25)
    .setNoneSelectedAllowed(false);
  ColorModellListener cml = new ColorModellListener();
  rb.getItem(0).addListener(cml);
  rb.getItem(1).addListener(cml);

  BackgroundColorListener bcl = new BackgroundColorListener();
  addSlider("background_v1", "background red",
    (controlSpace * yOffset++), 0, 255).addListener(bcl);
  addSlider("background_v2", "background green",
    (controlSpace * yOffset++), 0, 255).addListener(bcl);
  Slider b3 = addSlider("background_v3", "background blue",
    (controlSpace * yOffset++), 0, 255).addListener(bcl);
  addSlider("fill_v1", "fill red", (controlSpace * yOffset++), 0, 255);
  addSlider("fill_v2", "fill green", (controlSpace * yOffset++), 0, 255);
  addSlider("fill_v3", "fill blue", (controlSpace * yOffset++), 0, 255);
  addSlider("fill_v4", "fill alpha", (controlSpace * yOffset++), 0, 255);

  addSlider("shapeStrokeWeight", "stroke weight", (controlSpace * yOffset++),
    0, 10);

  addSlider("stroke_v1", "stroke red", (controlSpace * yOffset++), 0, 255);
  addSlider("stroke_v2", "stroke green", (controlSpace * yOffset++), 0,
    255);
  addSlider("stroke_v3", "stroke blue", (controlSpace * yOffset++), 0,
    255);
  addSlider("stroke_v4", "stroke alpha", (controlSpace * yOffset++), 0,
    255);

  objectList.bringToFront().close();
  rendererList.bringToFront().close();

  b3.listen(true); // trigger listener
}

Map<Integer, String> createDropdownMap(String itemList) {
  Map<Integer, String> map = new HashMap<Integer, String>();
  for (String item : itemList.split(",")) {
    map.put(map.size(), item.trim());
  }
  return map;
}

DropdownList addDropdown(String name, float y,
  Map<Integer, String> menuItems) {
  int itemHeight = 18;
  DropdownList dropdownList = cp5.addDropdownList(name)
    .setPosition(10, y).setSize(180, (menuItems.size() + 1) * itemHeight)
    .setItemHeight(itemHeight).setBarHeight(itemHeight);
  for (Entry<Integer, String> entry : menuItems.entrySet()) {
    dropdownList.addItem(entry.getValue(), entry.getKey());
  }
  return dropdownList;
}

Slider addSlider(String variable, String caption, float y, int min,
  int max) {
  Slider slider = cp5.addSlider(variable);
  slider.setPosition(10, y);
  slider.setRange(min, max);
  slider.setSize(80, 18);
  slider.setCaptionLabel(caption);

  return slider;
}

void preDraw() {
  camera3D.setBackgroundColor(color(background_v1, background_v2,
    background_v3));

  xRot = (xRot + xRotSpeed + 360) % 360;
  yRot = (yRot + yRotSpeed + 360) % 360;
  zRot = (zRot + zRotSpeed + 360) % 360;

  cp5.getController("xRot").setValue(xRot);
  cp5.getController("yRot").setValue(yRot);
  cp5.getController("zRot").setValue(zRot);

  earthSpaceStationGroup.rotateTo(radians(xRot), radians(yRot), radians(zRot));
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


  if (rgbFlag) {
    colorMode(RGB, 255, 255, 255);
  } else {
    colorMode(HSB, 255, 100, 100);
  }

  String objectChoice = objectMenuItems.get((int) objectList.getValue());

  strokeWeight(shapeStrokeWeight);

  if (stroke_v4 == 0) {
    noStroke();
  } else {
    stroke(color(stroke_v1, stroke_v2, stroke_v3, stroke_v4));
  }

  if (fill_v4 == 0) {
    noFill();
  } else {
    fill(color(fill_v1, fill_v2, fill_v3, fill_v4));
  }

  pushMatrix();
  translate(xTrans, yTrans, zTrans);
  if (!objectChoice.equals("Earth")) {
    rotateX(radians(xRot));
    rotateY(radians(yRot));
    rotateZ(radians(zRot));
  }

  shapeMode(CENTER);
  scale(pow(10, logscale));

  switch ((int) objectList.getValue()) {
  case 0:
    box(100);
    break;
  case 1:
    sphereDetail(8, 6);
    sphere(100);
    break;
  case 2:
    sphereDetail(10);
    int sphereCount = 6;
    for (int ii = 0; ii < sphereCount; ++ii) {
      pushMatrix();
      rotateY(TWO_PI * ii / sphereCount);
      translate(0, 0, 200);
      sphere(50);
      popMatrix();
    }
    break;
  case 3:
    earthSpaceStationGroup.draw(getGraphics());
    break;
  case 4:

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



    break;
  default:
    println("unknown object " + objectChoice + ". please report bug.");
  }

  popMatrix();

  fx.render().brightPass(0.1).chromaticAberration().rgbSplit(500).compose();
  videoExport.saveFrame();
}

void postDraw() {
  cp5.draw();
}

/*
 * Mouse Events
 */
void mouseWheel(MouseEvent event) {
  logscale += -event.getCount() / 10.;
  cp5.getController("logscale").setValue(logscale);
}

void keyPressed() {
  if (key == 'q') {
    videoExport.endMovie();
    exit();
  }
}
