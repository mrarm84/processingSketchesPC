import processing.video.*;
import controlP5.*;
import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;
Capture cam;
PGraphics asciiLayer;
ControlP5 cp5;

String chars = "@#&$%*o!;:,. ";
float threshold = 0.5;
int fontSize = 10;
String fontName = "Courier";
int blurSize = 0;
int blendMode = BLEND;
color fgColor = color(255);
color bgColor = color(0);

void setup() {
  size(640, 480);
  cam = new Capture(this, 160, 120);
  cam.start();
  asciiLayer = createGraphics(width, height);
  createControls();
}

void draw() {
  background(bgColor);
  if (cam.available()) {
    cam.read();
  }
  asciiLayer.beginDraw();
  asciiLayer.background(bgColor);
  asciiLayer.fill(fgColor);
  asciiLayer.textFont(createFont(fontName, fontSize));
  asciiLayer.textAlign(CENTER, CENTER);

  for (int y = 0; y < cam.height; y++) {
    for (int x = 0; x < cam.width; x++) {
      color c = cam.get(x, y); 
      float b = brightness(c) / 255.0;
      if (b > threshold) {
        int index = constrain(int(map(b, 0, 1, 0, chars.length() - 1)), 0, chars.length() - 1);

        asciiLayer.text(chars.charAt(index), x * fontSize, y * fontSize);
      }
    }
  }

  if (blurSize > 0) {
    asciiLayer.filter(BLUR, blurSize);
  }

  asciiLayer.endDraw();
  blendMode(blendMode);
  image(asciiLayer, 0, 0);
}

void createControls() {
  cp5 = new ControlP5(this);
  cp5.addSlider("threshold").setPosition(10, 10).setRange(0, 1).setValue(threshold);
  cp5.addTextfield("chars").setPosition(10, 40).setSize(200, 30).setText(chars);
  cp5.addSlider("fontSize").setPosition(10, 80).setRange(5, 30).setValue(fontSize);
  cp5.addTextfield("fontName").setPosition(10, 120).setSize(200, 30).setText(fontName);
  cp5.addSlider("blurSize").setPosition(10, 160).setRange(0, 10).setValue(blurSize);
  cp5.addColorWheel("fgColor").setPosition(10, 200).setRGB(fgColor);
  cp5.addColorWheel("bgColor").setPosition(220, 200).setRGB(bgColor);
  cp5.addDropdownList("blendMode").setPosition(10, 400).setSize(200, 100)
    .addItem("BLEND", BLEND)
    .addItem("ADD", ADD)
    .addItem("SUBTRACT", SUBTRACT)
    .addItem("DARKEST", DARKEST)
    .addItem("LIGHTEST", LIGHTEST);
}
