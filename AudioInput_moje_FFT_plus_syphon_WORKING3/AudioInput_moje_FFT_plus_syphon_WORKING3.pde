/** //<>// //<>// //<>//
 * Grab audio from the microphone input and draw a circle whose size
 * is determined by how loud the audio input is.
 */

PGraphics canvas;



int currentColor = 0;
int midiDevice  = 3;


float theta;

float xmag, ymag = 0;
float newXmag, newYmag = 0;

float a;                 // Angle of rotation
float offset = PI/24.0;  // Angle offset between boxes
int num = 236;


int p1; //serves as temp pitch
int v1; //serves as temp velocity

boolean showPerspective = false;


float boxSize = 20;
float margin = boxSize*2;
float depth = 400;
color boxFill;

int fcount, lastm;
float frate;
int fint = 23;


int ccChannel;
int ccNumber;
int ccValue;
import java.lang.reflect.Method;

PShader blur;


void setup() {
  size(800, 600, P3D);
  canvas = createGraphics(800, 600, P3D);
}


void draw() {
  // Adjust the volume of the audio input based on mouse position

  //Note note = new Note(0, pitch, velocity);
  //int  number = (int) random(1, 100);
  //ControlChange change = new ControlChange(0, number, velocity);

  //print(size + " " );
  canvas.beginDraw();
  canvas.lights();

  float far = map(mouseX, 0, width, 120, 400);
  if (showPerspective == true) {
    canvas.perspective(PI/3.0, float(width)/float(height), 10, far);
  } else {
    canvas.ortho(-width/2.0, width/2.0, -height/2.0, height/2.0, 10, far);
  }
  canvas.background(0, 0, 26);
  canvas.translate(width/2, height/2);

  for (int i = 0; i < num; i++) {
    float gray = map(i, 0, num-1, 0, 255);
    canvas.pushMatrix();
    canvas.fill(gray);
    canvas.rotateY(a + offset*i);
    canvas.rotateX(a/222 + offset*i);
    canvas.box(200);
    canvas.popMatrix();
  }

  a += 0.01;


  canvas.endDraw();
  image(canvas, 0, 0);
}



void mousePressed() {
  println("test");
  showPerspective = !showPerspective;


  strokeWeight(random(1, 10));
}



//void controllerChange(int channel, int number, int value) {
//  // store the midi data for using it inside draw()
//  ccChannel = channel;
//  ccNumber = number;
//  ccValue = value;
//  line(channel, number, value, 22);
//  println(channel, number, value);
//  println("testCC");
//}


// Notice all bytes below are converted to integeres using the following system:
// int i = (int)(byte & 0xFF)
// This properly convertes an unsigned byte (MIDI uses unsigned bytes) to a signed int
// Because java only supports signed bytes, you will get incorrect values if you don't do so

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

void variableEllipse(int x, int y, int px, int py) {
  float speed = abs(x-px) + abs(y-py);
  stroke(speed);
  ellipse(x, y, speed, speed);
}
