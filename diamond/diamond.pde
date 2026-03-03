import peasy.*;
import processing.video.*;

import ch.bildspur.postfx.*;
import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;

PeasyCam cam;
Capture video;
PostFX fx;

// Audio-reactive variables for post-processing
boolean clickActive = false;
float clickBrightnessPeak = 0.5;
float clickFeedbackTime = 0;
float clickFeedbackDuration = 30;

boolean peakActive = false;
float peakBrightnessPeak = 0.3;
float peakFeedbackTime = 0;
float peakFeedbackDuration = 60;

float chromaticAmount = 12;

void setup() {
  size(1200, 800, P3D);
  cam = new PeasyCam(this, 0, 0, 0, 1000);
  cam.setMinimumDistance(200);
  cam.setMaximumDistance(2000);
  cam.setSuppressRollRotationMode();

  // List available cameras
  String[] cameras = Capture.list();
  if (cameras.length == 0) {
    println("No cameras available.");
    exit();
  } else {
    println("Available cameras:");
    for (String c : cameras) println(c);
    video = new Capture(this, cameras[0]);
    video.start();
  }

  fx = new PostFX(this);
  fx.preload(BloomPass.class);
  fx.preload(RGBSplitPass.class);
}

void draw() {
  if (video.available()) {
    video.read();
  }

  background(20);

  // Render the diamond with PeasyCam
  renderTransparentDiamond();

  // Apply post-processing effects
  fx.render()
    .brightPass(0.1 +
      (clickActive ? clickBrightnessPeak * (clickFeedbackTime / clickFeedbackDuration) : 0) +
      (peakActive ? peakBrightnessPeak * (peakFeedbackTime / peakFeedbackDuration) : 0))
    .chromaticAberration()
    .rgbSplit(chromaticAmount)
    .compose();

  // Update feedback timers
  if (clickActive) {
    clickFeedbackTime++;
    if (clickFeedbackTime >= clickFeedbackDuration) {
      clickActive = false;
      clickFeedbackTime = 0;
    }
  }

  if (peakActive) {
    peakFeedbackTime++;
    if (peakFeedbackTime >= peakFeedbackDuration) {
      peakActive = false;
      peakFeedbackTime = 0;
    }
  }
}


void renderTransparentDiamond() {
  renderTransparentDiamondToContext(g);
}

void renderTransparentDiamondToContext(PGraphics pg) {
  pg.hint(ENABLE_DEPTH_TEST);
  pg.blendMode(BLEND);
  pg.textureMode(NORMAL);
  pg.noStroke();

  // Apply 50% transparency tint to the texture
  pg.tint(255, 128);

  // Render diamond manually with video texture and 50% transparency
  pg.beginShape(TRIANGLES);
  pg.texture(video);

  float h = 100;
  float r = 80;
  int sides = 6;

  PVector top = new PVector(0, -h, 0);
  PVector bottom = new PVector(0, h, 0);

  for (int i = 0; i < sides; i++) {
    float angle1 = TWO_PI * i / sides;
    float angle2 = TWO_PI * (i + 1) / sides;

    PVector p1 = new PVector(r * cos(angle1), 0, r * sin(angle1));
    PVector p2 = new PVector(r * cos(angle2), 0, r * sin(angle2));

    // Top triangle
    pg.vertex(top.x, top.y, top.z, 0.5, 0);
    pg.vertex(p1.x, p1.y, p1.z, 0, 1);
    pg.vertex(p2.x, p2.y, p2.z, 1, 1);

    // Bottom triangle
    pg.vertex(bottom.x, bottom.y, bottom.z, 0.5, 0);
    pg.vertex(p2.x, p2.y, p2.z, 0, 1);
    pg.vertex(p1.x, p1.y, p1.z, 1, 1);
  }

  pg.endShape();
  pg.noTint();
}

