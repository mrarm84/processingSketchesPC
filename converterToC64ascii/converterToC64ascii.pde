PImage img;
PGraphics edgeImg;
String asciiChars = "@%#*+=-:. "; // ASCII characters for representation

void setup() {
  size(800, 800);
  img = loadImage("your_image.jpg"); // Load your image here
  img.resize(width, height);
  edgeImg = createGraphics(width, height);
}

void draw() {
  background(255);
  PImage grayscale = toGrayscale(img);
  PImage edges = sobelEdgeDetection(grayscale);
  image(edges, 0, 0);
  generateAsciiArt(edges);
  noLoop();
}

PImage toGrayscale(PImage img) {
  PImage grayImg = createImage(img.width, img.height, RGB);
  img.loadPixels();
  grayImg.loadPixels();
  for (int i = 0; i < img.pixels.length; i++) {
    int c = img.pixels[i];
    float r = red(c);
    float g = green(c);
    float b = blue(c);
    float brightness = (r + g + b) / 3;
    grayImg.pixels[i] = color(brightness);
  }
  grayImg.updatePixels();
  return grayImg;
}

PImage sobelEdgeDetection(PImage img) {
  PImage edgeImg = createImage(img.width, img.height, RGB);
  img.loadPixels();
  edgeImg.loadPixels();
  
  int[][] sobelX = {
    {-1, 0, 1},
    {-2, 0, 2},
    {-1, 0, 1}
  };
  int[][] sobelY = {
    {-1, -2, -1},
    {0, 0, 0},
    {1, 2, 1}
  };
  
  for (int y = 1; y < img.height - 1; y++) {
    for (int x = 1; x < img.width - 1; x++) {
      float pixelX = (
        (sobelX[0][0] * brightness(img.pixels[x-1 + (y-1)*img.width])) +
        (sobelX[0][1] * brightness(img.pixels[x + (y-1)*img.width])) +
        (sobelX[0][2] * brightness(img.pixels[x+1 + (y-1)*img.width])) +
        (sobelX[1][0] * brightness(img.pixels[x-1 + y*img.width])) +
        (sobelX[1][1] * brightness(img.pixels[x + y*img.width])) +
        (sobelX[1][2] * brightness(img.pixels[x+1 + y*img.width])) +
        (sobelX[2][0] * brightness(img.pixels[x-1 + (y+1)*img.width])) +
        (sobelX[2][1] * brightness(img.pixels[x + (y+1)*img.width])) +
        (sobelX[2][2] * brightness(img.pixels[x+1 + (y+1)*img.width]))
      );
      
      float pixelY = (
        (sobelY[0][0] * brightness
