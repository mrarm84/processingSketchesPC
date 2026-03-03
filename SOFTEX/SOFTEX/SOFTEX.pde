/**
 * SOFTEX - VJ Software
 * 
 * Paid VJ Software with unique generative mechanisms.
 * Supports 4 channels (A, B, C, D) with mixing and FX.
 */

import processing.video.*;

SourceManager sourceManager;
FXChain fxChain;
OutputWindow outputWin; // Second Window
GUI gui;
MidiHandler midi;
boolean debugMode = true;

void setup() {
  size(1280, 720, P3D); // GUI Window
  frameRate(60);
  surface.setTitle("SOFTEX VJ Engine - CONTROLLER");
  surface.setResizable(true);
  
  // Initialize Output Window
  outputWin = new OutputWindow(1920, 1080); // Full HD Output
  String[] args = {"OutputWindow"};
  PApplet.runSketch(args, outputWin);
  
  // Initialize Managers
  sourceManager = new SourceManager();
  sourceManager.init(this);
  fxChain = new FXChain(this);
  
  // Setup MIDI
  midi = new MidiHandler(this);
  
  // Setup GUI
  gui = new GUI(this);
  setupGUI();
}

void setupGUI() {
  int startX = 20;
  int startY = 40;
  int gapX = 160;
  
  for (int i = 0; i < 4; i++) {
    final int chIndex = i;
    int x = startX + (i * gapX);
    
    // Channel Label
    // (Drawn manually in draw for now, or add Label widget)
    
    // Load Video Button
    gui.addWidget(new Button(x, startY, 140, 30, "Load Video Ch" + (char)('A'+i), new Runnable() {
      public void run() {
        selectInput("Select media file:", "fileSelected", null, chIndex);
      }
    }));
    
    // Load Gen Button
    gui.addWidget(new Button(x, startY + 40, 140, 30, "Load Gen Ch" + (char)('A'+i), new Runnable() {
      public void run() {
         sourceManager.loadSource(chIndex, new ChaosGen());
      }
    }));
    
    // Eject Button
    gui.addWidget(new Button(x, startY + 80, 140, 30, "Eject", new Runnable() {
      public void run() {
         sourceManager.channels[chIndex] = new EmptySource();
      }
    }));
    
    // Export PHP (Only for Gen)
    gui.addWidget(new Button(x, startY + 180, 140, 20, "Export PHP", new Runnable() {
      public void run() {
         if (sourceManager.channels[chIndex] instanceof ChaosGen) {
            ((ChaosGen)sourceManager.channels[chIndex]).exportPHP("pattern_ch" + chIndex + ".php");
         } else {
            println("Channel " + chIndex + " is not a Chaos Generator.");
         }
      }
    }));
    
    // Opacity Slider
    Slider opCheck = new Slider(x, startY + 120, 140, 20, "Opacity", 1.0);
    gui.addWidget(opCheck);
    
    // Blend Mode Dropdown
    String[] modes = {"Normal", "Add", "Multiply", "Screen", "Difference", "Exclusion", "Subtract"};
    Dropdown blendDrop = new Dropdown(x, startY + 150, 140, 20, "Blend", modes);
    gui.addWidget(blendDrop);
  }
  
  // FX Toggles
  int fxY = startY + 220;
  String[] fxNames = {"Chroma", "Kaleido", "Dither"}; 
  for (int i = 0; i < fxNames.length; i++) {
     final String fxName = fxNames[i];
     final int btnIndex = i;
     gui.addWidget(new Button(20 + i * 160, fxY, 140, 30, "Toggle " + fxName, new Runnable() {
       public void run() {
          if (fxChain != null) fxChain.toggle(fxName);
       }
     }));
  }
  
  // Recording Controls (Bottom Right)
  gui.addWidget(new Button(width - 150, height - 50, 140, 40, "REC TOGGLE", new Runnable() {
    public void run() {
      toggleRecording();
    }
  }));
}

// Queue for file loading to avoid threading issues
class LoadRequest {
  int channel;
  String path;
  LoadRequest(int c, String p) { channel = c; path = p; }
}
ArrayList<LoadRequest> loadQueue = new ArrayList<LoadRequest>();

void draw() {
  background(40); // Dark Gray GUI background
  
  // Process File Loads (Main Thread)
  if (loadQueue.size() > 0) {
    for (LoadRequest req : loadQueue) {
       sourceManager.loadSource(req.channel, new VideoSource(this, req.path));
    }
    loadQueue.clear();
  }
  
  // Update Mechanics
  updateControlLinks(); // Sync sliders to logic
  sourceManager.update();
  
  // Render blended result
  PGraphics finalOutput = sourceManager.render();
  
  // Apply Post-Processing FX
  fxChain.apply(finalOutput);
  
  // GUI Preview (Center constrained)
  // Calculate aspect ratio fit
  float margin = 200; // Space for UI
  float availW = width;
  float availH = height - margin; 
  
  float scale = min(availW / finalOutput.width, availH / finalOutput.height) * 0.7;
  float pw = finalOutput.width * scale;
  float ph = finalOutput.height * scale;
  
  image(finalOutput, (width - pw)/2, 250, pw, ph); // Positioned below controls
  
  // Send to Output Window
  if (outputWin != null) {
    outputWin.setFrame(finalOutput);
  }
  
  // Draw GUI
  gui.updateAndDraw();
  
  // OSD
  if (debugMode) {
    drawDebugUI();
  }
}

void updateControlLinks() {
  // Sync sliders to source manager
  // In a robust system, this uses binding. Here we iterate widgets.
  int chCountOp = 0;
  int chCountBlend = 0;
  
  for (Widget w : gui.widgets) {
    if (w instanceof Slider && w.label.equals("Opacity")) {
      Slider s = (Slider)w;
      if (chCountOp < 4) {
         if (s.dragging) {
           // User is dragging, update Logic
           sourceManager.channels[chCountOp].opacity = s.value;
         } else {
           // Logic (MIDI/Auto) drives slider
           s.value = sourceManager.channels[chCountOp].opacity;
         }
         chCountOp++;
      }
    }
    else if (w instanceof Dropdown && w.label.equals("Blend")) {
      Dropdown d = (Dropdown)w;
      if (chCountBlend < 4) {
        sourceManager.channels[chCountBlend].blendMode = d.selectedIndex;
        chCountBlend++;
      }
    }
  }
}

// MIDI Callbacks (TheMidiBus looks for these in the parent sketch)
void noteOn(int channel, int pitch, int velocity) {
  if (midi != null) midi.noteOn(channel, pitch, velocity);
}

void noteOff(int channel, int pitch, int velocity) {
  if (midi != null) midi.noteOff(channel, pitch, velocity);
}

void controllerChange(int channel, int number, int value) {
  if (midi != null) midi.controllerChange(channel, number, value);
}

// Callback for file picker
void fileSelected(File selection, Object chIndexObj) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    int chIndex = (int)chIndexObj;
    println("User selected " + selection.getAbsolutePath());
    // Queue the load request
    loadQueue.add(new LoadRequest(chIndex, selection.getAbsolutePath()));
  }
}

void mousePressed() {
  gui.mousePressed();
}

void mouseDragged() {
  gui.mouseDragged();
}

void mouseReleased() {
  gui.mouseReleased();
}

void drawDebugUI() {
  fill(255);
  noStroke();
  text("FPS: " + frameRate, 10, 20);
  text("Sources Active: " + sourceManager.getActiveCount(), 10, 40);
  if (outputWin.recording) {
    fill(255, 0, 0);
    text("REC", 10, 60);
  }
}

void keyPressed() {
  // Simple test controls
  if (key == 'd') debugMode = !debugMode;
  if (key == 'r') {
    if (outputWin.recording) outputWin.stopRecording();
    else outputWin.startRecording("export-" + timestamp() + ".mp4");
  }
}

String timestamp() {
  return year() + nf(month(), 2) + nf(day(), 2) + "-" + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2);
}
