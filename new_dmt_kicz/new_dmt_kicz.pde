import com.hamoid.*;
import peasy.PeasyCam;
import processing.video.*;
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
FFT fft;
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

// YOLO MODE - EXTREME CHAOS!
boolean yoloMode = true;
float yoloIntensity = 3.0; // Multiplier for all effects
float yoloSpeed = 2.0; // Speed multiplier
float yoloSize = 1.5; // Size multiplier

// Solar System Variables
int numPlanets = 200; // YOLO MODE: Even more planets!
Planet[] planets;
Dodecahedron dodecahedron;
float time = 0;

// Audio Reactive Variables
float bassLevel = 0;
float midLevel = 0;
float highLevel = 0;
float[] fftBands;
int numBands = 64;

// Audio peak detection and smoothing
float smoothedBass = 0;
float smoothedMid = 0;
float smoothedHigh = 0;
float peakThreshold = 0.3;
float smoothingFactor = 0.1;
boolean isPeak = false;

// Color filtering and strobe effects
color[] strobeColors = {
  color(255, 20, 147),   // Deep Pink
  color(255, 69, 0),     // Red Orange
  color(255, 140, 0),    // Dark Orange
  color(255, 215, 0),    // Gold
  color(50, 205, 50),    // Lime Green
  color(0, 255, 127),    // Spring Green
  color(0, 191, 255),    // Deep Sky Blue
  color(138, 43, 226)    // Blue Violet
};
int currentStrobeColor = 0;
float strobeSpeed = 0.02; // Slower color movement
float colorIntensity = 0.6; // Reduce overall intensity

// Mouse-controlled parameters for center object
float mouseInfluence = 0;
float mouseRotationBoost = 0;
float mouseSizeBoost = 0;

// Sine wave distortion parameters
float sineTime = 0;
float orbitDistortion = 0.1;
float pulsationIntensity = 0.05;

// Automatic rotation parameters - YOLO MODE
float autoRotationSpeed = 0.01; // Much faster rotation
float autoRotationX = 0;
float autoRotationY = 0;

// Blend mode and feedback
int currentBlendMode = BLEND;
int[] blendModes = {BLEND, ADD};
String[] blendModeNames = {"BLEND", "ADD"};
color[] blendModeColors = {
  color(255, 255, 255), // BLEND - White
  color(255, 100, 100)  // ADD - Red tint
};
int currentBlendIndex = 0;
float blendFadeTime = 0;
float blendFadeDuration = 3000; // 3 seconds fade (slower)
int previousBlendIndex = 0;

// Palette shifting
int currentPaletteShift = 0;

// Edge count changes
int baseEdgeCount = 12;
int currentEdgeCount = 12;

// Zoom-based particle count
int baseParticleCount = 50;
int currentParticleCount = 50;
float zoomParticleTime = 0;

// Background animation
float backgroundTime = 0;
int backgroundDetail = 20;

// Camera video capture
Capture videoCam;
PImage cameraFrame;
float ditherIntensity = 0.5;
int ditherPattern = 0;

// Sinusoidal blur and chromatic aberration effects
float blurTime = 0;
float chromaticTime = 0;

// Mouse click feedback effects
float clickFeedbackTime = 0;
float clickFeedbackDuration = 3000; // 3 seconds decay
float clickChromaticPeak = 0;
float clickBlurPeak = 0;
float clickBrightnessPeak = 0;
boolean clickActive = false;

// Music peak feedback effects
float peakFeedbackTime = 0;
float peakFeedbackDuration = 2000; // 2 beats decay (assuming 120 BPM)
float peakChromaticPeak = 0;
float peakBlurPeak = 0;
float peakBrightnessPeak = 0;
boolean peakActive = false;
int peakCounter = 0;
int peakTriggerInterval = 4; // Trigger every 4 peaks
int beatCounter = 0;
int beatTriggerInterval = 8; // Chaotic rotation every 8 beats

// Chaotic rotation variables
float chaoticRotationX = 0;
float chaoticRotationY = 0;
float chaoticRotationZ = 0;
boolean chaoticRotationActive = false;
float chaoticRotationTime = 0;
float chaoticRotationDuration = 1000; // 1 second chaotic rotation


void setup()
{
  // Set window size first - YOLO MODE!
  size(1024, 768, P3D);
  
  canvas = createGraphics(1024, 768, P3D);
  
  // Initialize video export

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
  s.inputDevice(12); // mode 16 jako karta
  // kanal 14
  frameRate(120);
  //img = loadImage("sample.png");  // Load the image into the program

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

  // Create FFT analyzer
  fft = new FFT(this, numBands);
  
  // Initialize FFT bands array
  fftBands = new float[numBands];

  // Patch the input to the volume analyzer
  loudness.input(input);
  
  // Patch the input to the FFT analyzer
  fft.input(input);
  //noStroke();
  colorMode(RGB, 1);
  //smooth(2);
  background(numChars/2);
  // Set a hue value for each key
  for (int i = 0; i < numChars; i++) {
    colors[i] = color(i, numChars, numChars);
  }

  initializeOrbits();
  initializeSolarSystem();
  
  // Initialize camera for video capture
  String[] cameras = Capture.list();
  if (cameras.length > 0) {
    println("Available cameras:");
    for (int i = 0; i < cameras.length; i++) {
      println(i + ": " + cameras[i]);
    }
    // Try to use the first available camera
    videoCam = new Capture(this, cameras[0]);
    videoCam.start();
    println("Camera initialized with: " + cameras[0]);
  } else {
    println("No cameras found, trying default");
    videoCam = new Capture(this, width, height);
    videoCam.start();
  }

  // Setup PeasyCam for interactive camera control
  cam = new PeasyCam(this, 0, 0, 0, 1000);
  cam.setMinimumDistance(200);
  cam.setMaximumDistance(2000);
  cam.setSuppressRollRotationMode();
}

void draw()
{
  // Simple camera test - draw small preview
  if (videoCam != null && videoCam.width > 0) {
    pushMatrix();
    camera();
    hint(DISABLE_DEPTH_TEST);
    image(videoCam, 10, 10, 200, 150); // Small camera preview
    hint(ENABLE_DEPTH_TEST);
    popMatrix();
  }
  
  // Animated labyrinth background
  backgroundTime += 0.01;
  drawAnimatedBackground();
  
  // Update blur and chromatic aberration timing
  blurTime += 0.02;
  chromaticTime += 0.015;
  
  // Update zoom-based particle count with slow sine wave
  zoomParticleTime += 0.01;
  float zoomSine = sin(zoomParticleTime * 0.3) * 0.3 + 0.7; // Varies between 0.4 and 1.0
  currentParticleCount = int(baseParticleCount * zoomSine);
  currentParticleCount = constrain(currentParticleCount, 20, 80);
  
  // Update click feedback decay
  if (clickActive) {
    clickFeedbackTime -= 16; // Assuming ~60fps
    if (clickFeedbackTime <= 0) {
      clickActive = false;
      clickFeedbackTime = 0;
    }
  }
  
  // Update peak feedback decay
  if (peakActive) {
    peakFeedbackTime -= 16; // Assuming ~60fps
    if (peakFeedbackTime <= 0) {
      peakActive = false;
      peakFeedbackTime = 0;
    }
  }
  
  // Update chaotic rotation decay
  if (chaoticRotationActive) {
    chaoticRotationTime -= 16; // Assuming ~60fps
    if (chaoticRotationTime <= 0) {
      chaoticRotationActive = false;
      chaoticRotationTime = 0;
    }
  }
  
 if (recording) {
        videoExport.saveFrame();
        if (millis() - recordStartTime > 10000) {
            videoExport.endMovie(); // Stop recording properly
            recording = false;
        }
    }

  float inputLevel = map(mouseY, 0, height, 1.0, 0.0);
  input.amp(inputLevel);

  float volume = loudness.analyze();
  int volumeSize = int(map((volume), 0, 0.5, 1, 255));

  // Analyze FFT
  // Analyze FFT into the buffer
  fft.analyze(fftBands);

  // Normalize FFT bands so the values are in a stable 0..1 range regardless of input gain
  float maxVal = 0.0001; // avoid division by zero
  for (int i = 0; i < numBands; i++) {
    if (fftBands[i] > maxVal) maxVal = fftBands[i];
  }
  // If we have a measurable loudness, bias normalization by loudness so quiet input doesn't blow up
  float loud = loudness.analyze();
  float loudBias = 1.0 + constrain(loud * 4.0, 0, 8); // modest amplifier when there's real input
  maxVal *= loudBias;

  for (int i = 0; i < numBands; i++) {
    fftBands[i] = fftBands[i] / maxVal; // now roughly 0..1
  }

  // Calculate frequency band levels (averages) - bass/mid/high
  float rawBass = 0;
  float rawMid = 0;
  float rawHigh = 0;
  for (int i = 0; i < numBands; i++) {
    if (i < numBands/3) {
      rawBass += fftBands[i];
    } else if (i < 2*numBands/3) {
      rawMid += fftBands[i];
    } else {
      rawHigh += fftBands[i];
    }
  }

  // Average the sums and apply YOLO intensity multiplier
  rawBass = (rawBass / (numBands/3)) * yoloIntensity;
  rawMid = (rawMid / (numBands/3)) * yoloIntensity;
  rawHigh = (rawHigh / (numBands/3)) * yoloIntensity;

  // Apply exponential smoothing to reduce flickering
  smoothedBass = smoothedBass * (1 - smoothingFactor) + rawBass * smoothingFactor;
  smoothedMid = smoothedMid * (1 - smoothingFactor) + rawMid * smoothingFactor;
  smoothedHigh = smoothedHigh * (1 - smoothingFactor) + rawHigh * smoothingFactor;

  // Final band levels
  bassLevel = smoothedBass;
  midLevel = smoothedMid;
  highLevel = smoothedHigh;
  
  // Detect audio peaks
  float totalLevel = (bassLevel + midLevel + highLevel) / 3;
  isPeak = totalLevel > peakThreshold;
  
  // Handle peak-based effects
  if (isPeak) {
    peakCounter++;
    beatCounter++;
    
    // Trigger peak feedback every 4 peaks
    if (peakCounter >= peakTriggerInterval) {
      peakActive = true;
      peakFeedbackTime = peakFeedbackDuration;
      peakChromaticPeak = 1.5;
      peakBlurPeak = 1.0;
      peakBrightnessPeak = 0.6;
      peakCounter = 0;
      println("Peak feedback triggered! (Peak #" + peakCounter + ")");
    }
    
    // Trigger chaotic rotation every 8 beats
    if (beatCounter >= beatTriggerInterval) {
      chaoticRotationActive = true;
      chaoticRotationTime = chaoticRotationDuration;
      chaoticRotationX = random(-PI, PI);
      chaoticRotationY = random(-PI, PI);
      chaoticRotationZ = random(-PI, PI);
      beatCounter = 0;
      println("Chaotic rotation triggered! (Beat #" + beatCounter + ")");
    }
  }
  
  // Update strobe colors
  currentStrobeColor = int((time * strobeSpeed) % strobeColors.length);
  
  // Time-based palette shifting and edge count changes
  if (int(time * 0.1) % 10 == 0 && frameCount % 60 == 0) {
    currentPaletteShift = (currentPaletteShift + 1) % strobeColors.length;
    currentEdgeCount = (currentEdgeCount % 18) + 6;
    println("Time-based change - Palette shift: " + currentPaletteShift + ", Edge count: " + currentEdgeCount);
  }
  
  // Update blend mode fade
  if (blendFadeTime > 0) {
    blendFadeTime -= 16; // Assuming ~60fps
    if (blendFadeTime <= 0) {
      blendFadeTime = 0;
    }
  }
  
  // Update mouse influence for center object
  mouseInfluence = map(mouseX, 0, width, 0, 1);
  mouseRotationBoost = map(mouseY, 0, height, 0, 2);
  mouseSizeBoost = map(dist(mouseX, mouseY, width/2, height/2), 0, width/2, 0, 1);
  
  // Update sine wave time
  sineTime += 0.02;
  
  // Update automatic rotation
  autoRotationX += autoRotationSpeed;
  autoRotationY += autoRotationSpeed * 0.7;

  background(0);
  lights();
  
  // Apply blend mode with fade
  if (blendFadeTime > 0) {
    float fadeAlpha = blendFadeTime / blendFadeDuration;
    blendMode(currentBlendMode);
  }

  // Draw the mirrored dodecahedron solar system
  drawSolarSystem();
  
  // Update time for animations
  time += 0.01;

  // Apply post-processing effects
  // Apply PostFX effects with sinusoidal variations and click/peak feedback
  float blurAmount = 0.1 + sin(blurTime) * 0.05; // Sinusoidal blur
  float chromaticAmount = 0.2 + sin(chromaticTime) * 0.1; // Sinusoidal chromatic aberration
  
  // Add click feedback effects
  if (clickActive) {
    float feedbackIntensity = clickFeedbackTime / clickFeedbackDuration;
    blurAmount += clickBlurPeak * feedbackIntensity;
    chromaticAmount += clickChromaticPeak * feedbackIntensity;
  }
  
  // Add peak feedback effects
  if (peakActive) {
    float peakFeedbackIntensity = peakFeedbackTime / peakFeedbackDuration;
    blurAmount += peakBlurPeak * peakFeedbackIntensity;
    chromaticAmount += peakChromaticPeak * peakFeedbackIntensity;
  }
  
  fx.render()
    .brightPass(0.1 + 
      (clickActive ? clickBrightnessPeak * (clickFeedbackTime / clickFeedbackDuration) : 0) +
      (peakActive ? peakBrightnessPeak * (peakFeedbackTime / peakFeedbackDuration) : 0))
    .chromaticAberration()
    .rgbSplit(chromaticAmount)
    .compose();
    
  // Send to Spout
  spout.sendTexture();
}

void mousePressed() {
  // Store previous blend mode
  previousBlendIndex = currentBlendIndex;
  
  // Change blend mode on click with fade
  currentBlendIndex = (currentBlendIndex + 1) % blendModes.length;
  currentBlendMode = blendModes[currentBlendIndex];
  blendFadeTime = blendFadeDuration;
  
  // Trigger massive click feedback effects
  clickActive = true;
  clickFeedbackTime = clickFeedbackDuration;
  clickChromaticPeak = 2.0; // Massive chromatic aberration peak
  clickBlurPeak = 1.5; // Massive blur peak
  clickBrightnessPeak = 0.8; // Brightness boost
  
  // Console logging
  println("Mouse clicked! Blend mode transition:");
  println("- From: " + blendModeNames[previousBlendIndex] + " (Index: " + previousBlendIndex + ")");
  println("- To: " + blendModeNames[currentBlendIndex] + " (Index: " + currentBlendIndex + ")");
  println("- Fade duration: " + blendFadeDuration + "ms");
  println("- Chromatic peak: " + clickChromaticPeak);
  println("- Blur peak: " + clickBlurPeak);
  println("- Brightness peak: " + clickBrightnessPeak);
}

void initializeOrbits() {
  int numPoints = 100;
  orbitPoints = new PVector[numPoints];
  for (int i = 0; i < numPoints; i++) {
    float theta = TWO_PI * i / numPoints;
    orbitPoints[i] = new PVector(cos(theta), sin(theta));
  }
}

// Color filtering functions
color filterColor(color originalColor, float intensity) {
  // Reduce intensity and mix with strobe color
  color strobeColor = strobeColors[currentStrobeColor];
  
  float r = red(originalColor) * intensity + red(strobeColor) * (1 - intensity);
  float g = green(originalColor) * intensity + green(strobeColor) * (1 - intensity);
  float b = blue(originalColor) * intensity + blue(strobeColor) * (1 - intensity);
  
  // Apply additional filtering to reduce neon effect
  r = constrain(r * colorIntensity, 0, 255);
  g = constrain(g * colorIntensity, 0, 255);
  b = constrain(b * colorIntensity, 0, 255);
  
  return color(r, g, b);
}

color getStrobeColor() {
  return strobeColors[currentStrobeColor];
}

// Planet class for orbiting cubes
class Planet {
  float orbitRadius;
  float orbitSpeed;
  float angle;
  float cubeSize;
  color planetColor;
  ArrayList<PVector> trail;
  ArrayList<Float> trailAlpha; // Store alpha values for each trail point
  int maxTrailLength = 200; // Increased for longer trails
  boolean useDottedTrail;
  
  Planet(float radius, float speed, float planetSize, color col) {
    orbitRadius = radius;
    orbitSpeed = speed;
    angle = random(TWO_PI);
    cubeSize = planetSize;
    planetColor = col;
    trail = new ArrayList<PVector>();
    trailAlpha = new ArrayList<Float>(); // Initialize alpha array
    useDottedTrail = random(1) < 0.5; // Randomly choose dotted or solid trail
  }
  
  void update() {
    // Audio-reactive orbit speed
    float audioSpeed = orbitSpeed * (1 + midLevel * 2);
    
    // Slow orbit speed on audio peaks
    if (isPeak) {
      audioSpeed *= 0.1; // Slow down orbit during peaks
    }
    
    angle += audioSpeed;
    
    PVector pos = getPosition();
    trail.add(pos.copy());
    trailAlpha.add(255.0); // Start with full alpha
    
    // Fade trail alpha values over time
    int trailAlphaLength = 11;
    for (int i = 0; i < trailAlphaLength; i++) {
      float currentAlpha = trailAlpha.get(i);
      float fadeRate = 2.0; // Fade rate per frame
      trailAlpha.set(i, max(0, currentAlpha - fadeRate));
    }
    
    // Audio-reactive trail length
    int audioTrailLength = maxTrailLength + int(highLevel * 50);
    int trailLength = trail.size();
    if (trailLength > audioTrailLength) {
      trail.remove(0);
      trailAlpha.remove(0);
    }
    
    // Remove completely faded points
    int currentTrailAlphaLength = trailAlpha.size();
    for (int i = currentTrailAlphaLength - 1; i >= 0; i--) {
      if (trailAlpha.get(i) <= 0) {
        trail.remove(i);
        trailAlpha.remove(i);
      }
    }
  }
  
  PVector getPosition() {
    // Add sine wave distortion to orbits
    float distortionX = sin(sineTime + angle * 0.5) * orbitDistortion;
    float distortionY = cos(sineTime + angle * 0.3) * orbitDistortion;
    float distortionZ = sin(sineTime + angle * 0.7) * orbitDistortion * 2;
    
    float x = orbitRadius * cos(angle) + distortionX * orbitRadius;
    float y = orbitRadius * sin(angle) + distortionY * orbitRadius;
    float z = orbitRadius * 0.3 * sin(angle * 0.7) + distortionZ; // Add some vertical movement
    return new PVector(x, y, z);
  }
  
  void draw() {
    PVector pos = getPosition();
    
    // Audio-reactive trail intensity
    float trailAlphaValue = 150 + highLevel * 100;
    trailAlphaValue = constrain(trailAlphaValue, 50, 255);
    
    // Draw trail with fading alpha
    int trailLength = trail.size();
    if (trailLength > 1) {
      // Apply color filtering to trail with palette shifting
      color baseTrailColor = filterColor(planetColor, 0.6);
      color shiftedColor = strobeColors[(currentStrobeColor + currentPaletteShift) % strobeColors.length];
      color trailColor = lerpColor(baseTrailColor, shiftedColor, 0.3);
      
      // Get current alpha array length
      int trailAlphaLength = trailAlpha.size();
      
      if (useDottedTrail) {
        // Draw dotted trail with individual alpha values - step by 3 for dotted effect
        for (int i = 0; i < trailLength - 1 && i < trailAlphaLength; i += 3) {
          float alpha = trailAlpha.get(i);
          stroke(trailColor, alpha);
          strokeWeight(2 + highLevel * 3);
          
          // Only draw line if we have both current and next point
          if (i + 1 < trailLength) {
            PVector p1 = trail.get(i);
            PVector p2 = trail.get(i + 1);
            line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
          }
        }
      } else {
        // Draw solid trail with individual alpha values
        for (int i = 0; i < trailLength - 1 && i < trailAlphaLength; i++) {
          float alpha = trailAlpha.get(i);
          stroke(trailColor, alpha);
          strokeWeight(2 + highLevel * 3);
          
          // Only draw line if we have both current and next point
          if (i + 1 < trailLength) {
            PVector p1 = trail.get(i);
            PVector p2 = trail.get(i + 1);
            line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
          }
        }
      }
    }
    
    // Draw cube
    pushMatrix();
    translate(pos.x, pos.y, pos.z);
    
    // Audio-reactive cube size with sine wave pulsation
    float sinePulsation = sin(sineTime * 2 + angle) * pulsationIntensity;
    float audioCubeSize = cubeSize * (1 + midLevel * 0.5 + sinePulsation);
    
    // Audio-reactive colors with filtering
    color baseColor = color(
      red(planetColor) + midLevel * 50,
      green(planetColor) + midLevel * 50,
      blue(planetColor) + midLevel * 50
    );
    
    // Apply color filtering to reduce neon intensity and add transparency
    color audioColor = filterColor(baseColor, 0.7);
    // Add transparency (alpha = 160 for semi-transparent)
    audioColor = color(red(audioColor), green(audioColor), blue(audioColor), 160);
    
    // Apply mirror material
    fill(audioColor);
    specular(255, 255, 255);
    shininess(100 + midLevel * 50);
    
    // Audio-reactive rotation speed
    float audioRotationSpeed = 1 + midLevel;
    
    // Slow rotation on audio peaks
    if (isPeak) {
      audioRotationSpeed *= 0.2; // Slow down rotation during peaks
    }
    
    rotateX(time * 2 * audioRotationSpeed);
    rotateY(time * 1.5 * audioRotationSpeed);
    
    box(audioCubeSize);
    popMatrix();
  }
}

// Dodecahedron class
class Dodecahedron {
  float radius; // renamed from dodecahedronSize to be clearer
  color dodecaColor;
  
  Dodecahedron(float radius, color c) {
    this.radius = radius;
    dodecaColor = c;
  }
  
  void draw() {
    pushMatrix();
    
    // Audio-reactive rotation speed with mouse control
    float audioRotationSpeed = 1 + bassLevel * 2 + mouseRotationBoost * mouseInfluence;
    
    // Slow rotation on audio peaks
    if (isPeak) {
      audioRotationSpeed *= 0.3; // Slow down rotation during peaks
    }
    
    rotateX(time * 0.5 * audioRotationSpeed);
    rotateY(time * 0.3 * audioRotationSpeed);
    rotateZ(time * 0.2 * audioRotationSpeed);
    
    // Audio-reactive size pulsing with sine waves and mouse control
    float sinePulsation = sin(sineTime * 1.5) * pulsationIntensity * 2;
    float audioSize = radius * (1 + bassLevel * 0.3 + sinePulsation + mouseSizeBoost * mouseInfluence);
    
    // Add click feedback size boost
    if (clickActive) {
      float clickSizeBoost = 0.5 * (clickFeedbackTime / clickFeedbackDuration);
      audioSize *= (1 + clickSizeBoost);
    }
    
    // Audio-reactive colors with filtering
    color baseColor = color(
      red(dodecaColor) + bassLevel * 75,
      green(dodecaColor) + bassLevel * 50,
      blue(dodecaColor) + bassLevel * 100
    );
    
    // Apply color filtering to reduce neon intensity and add transparency
    color audioColor = filterColor(baseColor, 0.8);
    // Add transparency (alpha = 180 for semi-transparent)
    audioColor = color(red(audioColor), green(audioColor), blue(audioColor), 180);
    
    // Apply mirror material
    fill(audioColor);
    specular(255, 255, 255);
    shininess(100 + bassLevel * 100);
    
    // Draw both dodecahedrons blending together
    pushMatrix();
    
    // Sinusoidal detail changes for dodecahedron complexity
    float detailVariation = sin(sineTime * 0.5) * 0.3 + 0.7; // Varies between 0.4 and 1.0
    int dodecahedronDetail = int(12 * detailVariation);
    dodecahedronDetail = constrain(dodecahedronDetail, 6, 20);
    
  // Blend mode: softly add layers to make geometry merge visually
  hint(ENABLE_POINT_SPRITES);
  pushStyle();
  // Use additive tint for stronger merge
  blendMode(ADD);
  // Primary outer dodecahedron
  pushMatrix();
  drawDodecahedron(audioSize * 0.9);
  popMatrix();

  // Secondary inner dodecahedron, rotated and slightly offset for interference
  pushMatrix();
  translate(0, 0, audioSize * 0.02);
  rotateX(0.2);
  rotateY(0.3);
  drawDodecahedron(audioSize * 0.7);
  popMatrix();

  // Semi-transparent sphere the same overall size to visually merge with dodecahedron
  pushMatrix();
  noStroke();
  float sphereAlpha = 120 + bassLevel * 120; // more alpha on bass
  color sCol = color(red(dodecaColor), green(dodecaColor), blue(dodecaColor), constrain(sphereAlpha, 60, 255));
  fill(sCol);
  sphere(audioSize * 0.85);
  popMatrix();

  popStyle();
    
    popMatrix();
    
    popMatrix();
  }
}

void drawDodecahedron(float dodecahedronSize) {
  // Dynamic dodecahedron with variable edge count based on zoom
  float phi = (1 + sqrt(5)) / 2; // Golden ratio
  float a = dodecahedronSize / phi;
  float b = dodecahedronSize / (phi * phi);
  
  // Create vertices for dodecahedron with current edge count
  PVector[] vertices = new PVector[20];
  
  // Standard dodecahedron vertices
  vertices[0] = new PVector(0, b, a);
  vertices[1] = new PVector(0, -b, a);
  vertices[2] = new PVector(0, b, -a);
  vertices[3] = new PVector(0, -b, -a);
  vertices[4] = new PVector(b, a, 0);
  vertices[5] = new PVector(-b, a, 0);
  vertices[6] = new PVector(b, -a, 0);
  vertices[7] = new PVector(-b, -a, 0);
  vertices[8] = new PVector(a, 0, b);
  vertices[9] = new PVector(-a, 0, b);
  vertices[10] = new PVector(a, 0, -b);
  vertices[11] = new PVector(-a, 0, -b);
  vertices[12] = new PVector(0, 0, dodecahedronSize);
  vertices[13] = new PVector(0, 0, -dodecahedronSize);
  vertices[14] = new PVector(dodecahedronSize, 0, 0);
  vertices[15] = new PVector(-dodecahedronSize, 0, 0);
  vertices[16] = new PVector(0, dodecahedronSize, 0);
  vertices[17] = new PVector(0, -dodecahedronSize, 0);
  vertices[18] = new PVector(b, b, b);
  vertices[19] = new PVector(-b, -b, -b);
  
  // Draw faces with smoother translucency and normals
  int[][] faceIndices = {
    {0,8,12,9,1}, {0,1,4,16,5}, {0,5,14,8,12}, {1,9,6,7,2}, {2,3,11,10,4},
    {3,2,7,15,11}, {4,10,14,5,16}, {6,9,8,14,10}, {7,6,10,11,15}, {12,8,14,10,13},
    {13,11,15,3,2}, {13,12,9,1,3}
  };

  // Use translucent material
  noStroke();
  float baseAlpha = 160 + midLevel * 80; // audio reactive alpha
  color faceColor = color(red(strobeColors[currentStrobeColor]), green(strobeColors[currentStrobeColor]), blue(strobeColors[currentStrobeColor]), constrain(baseAlpha, 80, 255));
  fill(faceColor);

  for (int f = 0; f < 12; f++) {
    beginShape(TRIANGLE_FAN);
    // compute face center for normal
    PVector center = new PVector(0,0,0);
    for (int k = 0; k < 5; k++) {
      center.add(vertices[faceIndices[f][k]]);
    }
    center.div(5);
    for (int k = 0; k < 5; k++) {
      PVector v = vertices[faceIndices[f][k]];
      // Slightly push vertices toward the center for soft blending
      PVector vOut = PVector.lerp(v, center, 0.08);
      vertex(vOut.x, vOut.y, vOut.z);
    }
    endShape(CLOSE);
  }
}

void drawAnimatedBackground() {
  // Capture camera frame if available
  if (videoCam != null && videoCam.available()) {
    videoCam.read();
    println("Camera frame read - available: " + videoCam.available());
  }
  
  // Create animated labyrinth pattern with sinusoidal fluctuations
  pushMatrix();
  
  // Set up 2D drawing for background
  camera();
  hint(DISABLE_DEPTH_TEST);
  
  // Draw camera background with dithering if available
  if (videoCam != null && videoCam.width > 0) {
    println("Drawing camera with dithering");
    // Calculate dither pattern based on mouse movement
    float mouseSpeed = dist(mouseX, mouseY, pmouseX, pmouseY);
    ditherIntensity = map(mouseSpeed, 0, 50, 0.1, 1.0);
    ditherPattern = int(mouseX * 0.01) % 4; // 4 different dither patterns
    
    // Apply dithering effect
    applyDithering(videoCam, ditherIntensity, ditherPattern);
  } else {
    println("Using fallback labyrinth pattern");
    // Fallback to original labyrinth pattern
    drawLabyrinthPattern();
  }
  
  hint(ENABLE_DEPTH_TEST);
  popMatrix();
}

void applyDithering(PImage img, float intensity, int pattern) {
  // Apply macOS-style 12-bit color dithering
  img.loadPixels();
  
  for (int i = 0; i < img.pixels.length; i++) {
    color originalColor = img.pixels[i];
    
    // Convert to 12-bit color space (4 bits per channel)
    int r = int(red(originalColor) / 16) * 16;
    int g = int(green(originalColor) / 16) * 16;
    int b = int(blue(originalColor) / 16) * 16;
    
    // Apply dithering pattern based on mouse movement
    float ditherNoise = 0;
    int x = i % img.width;
    int y = i / img.width;
    
    switch(pattern) {
      case 0: // Bayer matrix dithering
        ditherNoise = sin(x * 0.1) * sin(y * 0.1) * intensity * 8;
        break;
      case 1: // Ordered dithering
        ditherNoise = ((x + y) % 2 == 0) ? intensity * 8 : -intensity * 8;
        break;
      case 2: // Random dithering
        ditherNoise = random(-intensity * 16, intensity * 16);
        break;
      case 3: // Wave dithering
        ditherNoise = sin(x * 0.05 + backgroundTime) * intensity * 12;
        break;
    }
    
    // Apply dithering to each channel
    r = constrain(r + int(ditherNoise), 0, 255);
    g = constrain(g + int(ditherNoise), 0, 255);
    b = constrain(b + int(ditherNoise), 0, 255);
    
    img.pixels[i] = color(r, g, b);
  }
  
  img.updatePixels();
  
  // Draw the dithered image
  image(img, 0, 0, width, height);
}

void drawLabyrinthPattern() {
  // Original labyrinth pattern as fallback
  for (int i = 0; i < backgroundDetail; i++) {
    for (int j = 0; j < backgroundDetail; j++) {
      // Calculate sinusoidal patterns
      float x = map(i, 0, backgroundDetail, 0, width);
      float y = map(j, 0, backgroundDetail, 0, height);
      
      // Multiple sine waves for labyrinth effect
      float wave1 = sin(x * 0.01 + backgroundTime) * 0.5 + 0.5;
      float wave2 = sin(y * 0.008 + backgroundTime * 1.3) * 0.5 + 0.5;
      float wave3 = sin((x + y) * 0.005 + backgroundTime * 0.7) * 0.5 + 0.5;
      
      // Combine waves for complex pattern
      float intensity = (wave1 + wave2 + wave3) / 3.0;
      
      // Create grey-black color with slight variations
      float greyValue = map(intensity, 0, 1, 15, 45);
      color bgColor = color(greyValue, greyValue * 0.9, greyValue * 0.8);
      
      // Draw small rectangles for pattern
      fill(bgColor);
      noStroke();
      rect(x, y, width/backgroundDetail, height/backgroundDetail);
    }
  }
}

void initializeSolarSystem() {
  planets = new Planet[numPlanets];
  dodecahedron = new Dodecahedron(80, color(200, 200, 255));
  
  // Create planets with varying orbits
  for (int i = 0; i < numPlanets; i++) {
    float radius = 150 + i * 15 + random(50); // Varying orbit radii
    float speed = 0.005 + random(0.01); // Varying speeds
    float cubeSize = 5 + random(15); // Varying cube sizes
    color col = color(100 + random(155), 100 + random(155), 200 + random(55));
    
    planets[i] = new Planet(radius, speed, cubeSize, col);
  }
}

void drawSolarSystem() {
  // Apply automatic rotation to entire solar system
  pushMatrix();
  rotateX(autoRotationX);
  rotateY(autoRotationY);
  
  // Apply chaotic rotation on music peaks
  if (chaoticRotationActive) {
    float chaoticIntensity = chaoticRotationTime / chaoticRotationDuration;
    rotateX(chaoticRotationX * chaoticIntensity);
    rotateY(chaoticRotationY * chaoticIntensity);
    rotateZ(chaoticRotationZ * chaoticIntensity);
  }
  
  // Draw coordinate axes for debugging
  stroke(255, 0, 0);
  line(0, 0, 0, 100, 0, 0); // X axis - red
  stroke(0, 255, 0);
  line(0, 0, 0, 0, 100, 0); // Y axis - green
  stroke(0, 0, 255);
  line(0, 0, 0, 0, 0, 100); // Z axis - blue
  
  // Draw dodecahedron at center
  dodecahedron.draw();
  
  // Apply blend mode color effect
  color blendColor = blendModeColors[currentBlendIndex];
  float blendIntensity = 0.3;
  tint(red(blendColor), green(blendColor), blue(blendColor), 255 * blendIntensity);
  
  // Draw planets with dynamic count
  for (int i = 0; i < min(currentParticleCount, planets.length); i++) {
    planets[i].update();
    planets[i].draw();
  }
  
  // Reset tint
  tint(255, 255, 255, 255);
  
  // End automatic rotation
  popMatrix();
}
