import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
import ch.bildspur.postfx.*;
import processing.video.*;

PostFX fx;
Capture cam;

void setup()
{
  size(500, 500, P3D);
  
  fx = new PostFX(this);  
  
   cam = new Capture(this, 320, 240, 30);
  cam.start();
  
  
}
void draw()
{
  // draw something onto the screen
  background(22);
  box(100);

  // add bloom filter
  fx.render()
    .bloom(0.5, 20, 40)
    .compose();
}
