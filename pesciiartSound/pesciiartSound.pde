import ddf.minim.*;
import ddf.minim.analysis.*;
import processing.video.*;

Minim minim;
AudioInput input; // Use audio input instead of a file
FFT fft;

Movie movie;
String petsciiChars = " .,-~:;+*=#%@"; // Example PETSCII palette

void setup() {
  size(640, 480); // Set resolution
  minim = new Minim(this);

  // Initialize live audio input
  input = minim.getLineIn(Minim.STEREO, 512); // Stereo input with buffer size of 512
  fft = new FFT(input.bufferSize(), input.sampleRate());

  // Load the video
  movie = new Movie(this, "./AnimateDiff_00148.mp4");
  movie.loop();
}

void draw() {
  if (movie.available()) {
    movie.read();
    image(movie, 0, 0, width, height); // Optional: Display the video

    fft.forward(input.mix); // Perform FFT on live audio input

    loadPixels();
    movie.loadPixels();
    for (int y = 0; y < height; y += 8) {
      for (int x = 0; x < width; x += 8) {
        int loc = x + y * width;
        color c = movie.pixels[loc];
        float brightness = brightness(c);

        // Use FFT values to influence the PETSCII mapping
        int charIndex = int(map(brightness, 0, 255, 0, petsciiChars.length() - 1));
        float soundInfluence = fft.getBand((y / 8) % fft.specSize()) * 20;
        charIndex = constrain(charIndex + int(soundInfluence), 0, petsciiChars.length() - 1);

        fill(0);
        rect(x, y, 8, 8); // Clear block
        fill(255);
        text(petsciiChars.charAt(charIndex), x, y + 8); // Draw PETSCII char
      }
    }
  }
}

void stop() {
  input.close();
  minim.stop();
  super.stop();
}
