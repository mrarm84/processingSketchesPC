// Custom minimal Immediate Mode-style GUI for VJing

class GUI {
  ArrayList<Widget> widgets;
  PFont uiFont;
  
  GUI(PApplet p) {
    widgets = new ArrayList<Widget>();
    uiFont = createFont("Arial", 12);
  }
  
  void addWidget(Widget w) {
    widgets.add(w);
  }
  
  void updateAndDraw() {
    hint(DISABLE_DEPTH_TEST); // Draw on top of 3D
    camera(); // Reset camera to 2D HUD
    noLights();
    
    for (Widget w : widgets) {
      w.draw();
    }
    
    // overlays
    for (Widget w : widgets) {
      if (w instanceof Dropdown) {
         ((Dropdown)w).drawOverlay();
      }
    }
    
    hint(ENABLE_DEPTH_TEST);
  }
  
  void mousePressed() {
    for (Widget w : widgets) {
      if (w.isHovered()) {
        w.onPress();
      }
    }
  }
  
  void mouseDragged() {
    for (Widget w : widgets) {
      if (w.dragging) {
        w.onDrag();
      }
    }
  }
  
  void mouseReleased() {
    for (Widget w : widgets) {
      w.onRelease();
    }
  }
}

abstract class Widget {
  float x, y, w, h;
  String label;
  boolean dragging = false;
  
  Widget(float x, float y, float w, float h, String label) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.label = label;
  }
  
  abstract void draw();
  void onPress() { dragging = true; }
  void onDrag() {}
  void onRelease() { dragging = false; }
  
  boolean isHovered() {
    return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
  }
}

class Button extends Widget {
  Runnable onClick;
  
  Button(float x, float y, float w, float h, String label, Runnable onClick) {
    super(x, y, w, h, label);
    this.onClick = onClick;
  }
  
  void draw() {
    fill(isHovered() ? 100 : 60);
    stroke(255);
    rect(x, y, w, h);
    fill(255);
    textAlign(CENTER, CENTER);
    text(label, x + w/2, y + h/2);
  }
  
  void onPress() {
    super.onPress();
    if (onClick != null) onClick.run();
  }
}

class Slider extends Widget {
  float value = 0.0;
  String paramName;
  
  Slider(float x, float y, float w, float h, String label, float initialVal) {
    super(x, y, w, h, label);
    this.value = initialVal;
    this.paramName = label;
  }
  
  void draw() {
    fill(40);
    stroke(255);
    rect(x, y, w, h);
    
    fill(100, 200, 255);
    noStroke();
    rect(x, y, w * value, h);
    
    fill(255);
    textAlign(LEFT, CENTER);
    text(paramName, x + 5, y + h/2);
  }
  
  void onDrag() {
    value = constrain((mouseX - x) / w, 0, 1);
  }
  
  void onPress() {
    super.onPress();
    onDrag(); // Update immediately on click
  }
}

class Dropdown extends Widget {
  String[] options;
  int selectedIndex = 0;
  boolean isOpen = false;
  
  Dropdown(float x, float y, float w, float h, String label, String[] options) {
    super(x, y, w, h, label);
    this.options = options;
  }
  
  void draw() {
    fill(40);
    stroke(255);
    rect(x, y, w, h);
    fill(255);
    textAlign(LEFT, CENTER);
    if (options.length > 0) {
      text(options[selectedIndex], x + 5, y + h/2);
    }
    
    // Draw arrow
    triangle(x + w - 15, y + 10, x + w - 5, y + 10, x + w - 10, y + 20);
    
    // Draw list if open
    if (isOpen) {
      // Defer drawing to end of frame? 
      // For immediate mode, we might get blocked by other widgets.
      // But standard painter's algo in updateAndDraw iterates.
      // Ideally, Dropdowns should be drawn last. 
      // We'll hack it: drawn here, but might be covered. 
      // Better: Register to draw overlay.
    }
  }
  
  // Custom draw method for overlay
  void drawOverlay() {
    if (!isOpen) return;
    
    for (int i = 0; i < options.length; i++) {
      float itemY = y + h + (i * h);
      fill(50);
      stroke(200);
      rect(x, itemY, w, h);
      fill(255);
      if (mouseX >= x && mouseX <= x+w && mouseY >= itemY && mouseY <= itemY+h) {
        fill(100, 200, 255); // Highlight
        rect(x, itemY, w, h);
        fill(0);
      }
      text(options[i], x + 5, itemY + h/2);
    }
  }
  
  void onPress() {
    isOpen = !isOpen;
  }
  
  void onRelease() {
    // Check if clicked an item
    if (isOpen) {
       for (int i = 0; i < options.length; i++) {
          float itemY = y + h + (i * h);
          if (mouseX >= x && mouseX <= x+w && mouseY >= itemY && mouseY <= itemY+h) {
            selectedIndex = i;
            isOpen = false;
            break;
          }
       }
    }
    dragging = false; 
  }
}
