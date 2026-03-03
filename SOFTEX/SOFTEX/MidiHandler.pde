import themidibus.*;

class MidiHandler {
  MidiBus myBus;
  
  MidiHandler(PApplet parent) {
    MidiBus.list(); // List all available Midi devices on STDOUT. This will show each device's index and name.
    
    // Auto-connect to first available input/output or provide a way to select
    // For now, try default 0, 0
    // In a real app, we'd need a GUI to select device.
    try {
       myBus = new MidiBus(parent, 0, 1);
       println("MIDI Initialized on default devices.");
    } catch (Exception e) {
       println("MIDI Init Failed: " + e.getMessage());
    }
  }
  
  // Note On
  void noteOn(int channel, int pitch, int velocity) {
    println("Note On: " + channel + ", " + pitch + ", " + velocity);
    // Map notes to triggers?
    // Ex: Notes 0-3 trigger Channel A-D active toggle
    if (pitch >= 48 && pitch < 52) {
       int ch = pitch - 48;
       // Toggle active?
       // sourceManager.channels[ch].active = !sourceManager.channels[ch].active;
    }
  }
  
  // Note Off
  void noteOff(int channel, int pitch, int velocity) {
     // ...
  }
  
  // Controller Change
  void controllerChange(int channel, int number, int value) {
    println("CC: " + channel + ", " + number + ", " + value);
    // Map CC to Opacity
    // Ex: CC 1, 2, 3, 4 -> Opacity A, B, C, D
    if (number >= 1 && number <= 4) {
       int ch = number - 1;
       if (ch < 4) {
         sourceManager.channels[ch].opacity = map(value, 0, 127, 0, 1);
       }
    }
  }
}
