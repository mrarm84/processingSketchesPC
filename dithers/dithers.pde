var ditherTemplates;
var dithers;
var currentDither;
var scenes;
var currentScene;
var WHITE;

function preload() {
  ditherTemplates = [loadImage('dithers/4x28.png'), 
                     loadImage('dithers/4x36.png'), 
                     loadImage('dithers/4x68.png'), 
                     loadImage('dithers/6x42 LINES.png'), 
                     loadImage('dithers/5x30 CIRCLES.png'),
                     loadImage('dithers/5x30 CIRCUITS.png'),
                     loadImage('dithers/5x45 DiagLines.png')];
}

function setup() {
  createCanvas(500, 500);
  noStroke();
  //noLoop();
  
  //Dither setup
  currentDither = 0;
  currentScene = 0;
  scenes = 3;
  WHITE = color(255, 255, 255, 255);
  dithers = [];
  for(var i = 0; i < ditherTemplates.length; i++) {
    dithers.push(new dither(ditherTemplates[i]));
  }
}

function draw() {
  background(0);  //The darker color
  fill(255);      //The lighter color
  
  var pxSize = 5;
  for(var x = 0; x < width; x+=pxSize) {
    for(var y = 0; y < height; y+=pxSize) {   
      var colorToSend;
      switch(currentScene) {
        case 0:
          //NOISE ANIMATION 
          colorToSend = color(noise(x/150, y/150, cos(frameCount/10))*256);
          break;
        case 1:
          //GRADIENT 
          colorToSend = color(y/height*255);
          break;
        case 2:
          //FOLLOW THE MOUSE
          colorToSend = color(dist(mouseX, mouseY, x, y));
          break;
      }
          
      if(ditherColor(colorToSend, x/pxSize, y/pxSize)) {
        square(x, y, pxSize);
      }
    }
  }
}

function mouseClicked() {
  //Cycles through loaded dither templates
  currentDither = (currentDither + 1) % ditherTemplates.length;
}

function keyPressed() {
  if (keyCode == 87) {  //Press 'W' for next scene
      currentScene = (currentScene + 1) % scenes;
  }
  else if(keyCode == 83) {  //Press 'S' for next scene
      currentScene = (currentScene + scenes - 1) % scenes;
  }
}
