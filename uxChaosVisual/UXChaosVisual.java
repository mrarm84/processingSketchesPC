import processing.core.*;
import processing.video.*;
import spout.*;
import codeanticode.syphon.*;
import g4p_controls.*;
import controlP5.*;
import java.io.*;
import javax.script.*;
import java.util.*;
import java.net.*;

/**
 * UX Chaos Visual - META ENGINE v8.0 (MODULAR IDE)
 * Professional Visual Mixer & Live-Coding Environment
 */
public class UXChaosVisual extends PApplet {

    // --- TYPES & ENUMS ---
    public enum SourceType { VIDEO, YOUTUBE, SHADER, PROCESSING, P5JS, HYDRA, SPOUT }

    // --- INTERFACE: MODULAR SOURCE ---
    public abstract class ChannelSource {
        public String name;
        public PGraphics buffer;
        public String code = "";
        public String path = "";
        
        public ChannelSource(String n) {
            this.name = n;
            this.buffer = createGraphics(width, height, P3D);
        }
        
        public abstract void update();
        public abstract void draw();
        public void setCode(String c) { this.code = c; }
    }

    // --- CONCRETE SOURCE: SCRIPT (JS/PROCESSING) ---
    public class ScriptSource extends ChannelSource {
        public ScriptSource(String n) { super(n); }
        public void update() {}
        public void draw() {
            try {
                engine.put("g", buffer);
                engine.put("v", volume);
                engine.eval(code);
            } catch (Exception e) { scriptError = e.getMessage(); }
        }
    }

    // --- CONCRETE SOURCE: VIDEO ---
    public class VideoSource extends ChannelSource {
        Movie movie;
        public VideoSource(String n) { super(n); }
        public void load(String p) {
            if (movie != null) movie.stop();
            movie = new Movie(UXChaosVisual.this, p);
            movie.loop();
        }
        public void update() { if (movie != null && movie.available()) movie.read(); }
        public void draw() {
            buffer.beginDraw();
            if (movie != null) buffer.image(movie, 0, 0, buffer.width, buffer.height);
            buffer.endDraw();
        }
    }

    // --- CONCRETE SOURCE: SHADER ---
    public class ShaderSource extends ChannelSource {
        PShader shader;
        public ShaderSource(String n) { super(n); }
        public void update() {}
        public void draw() {
            buffer.beginDraw();
            buffer.background(0);
            // Dynamic shader logic here
            buffer.endDraw();
        }
    }

    // --- GLOBAL MANAGERS ---
    ChannelSource sourceA, sourceB;
    ChannelSource activeSource;
    SourceType typeA = SourceType.PROCESSING;
    SourceType typeB = SourceType.VIDEO;
    
    float mixRatio = 0.5f;
    PGraphics finalCanvas;
    PShader postFXShader;
    ControlP5 cp5;
    GWindow ideWindow, fxWindow;
    GTextArea editor;
    
    ScriptEngine engine;
    float volume = 0;
    String scriptError = "READY";

    public static void main(String[] args) {
        PApplet.main("UXChaosVisual");
    }

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        surface.setTitle("UX Chaos Meta Engine - IDE");
        
        // 1. Init Sources
        sourceA = new ScriptSource("A");
        sourceB = new VideoSource("B");
        activeSource = sourceA;
        
        finalCanvas = createGraphics(width, height, P3D);
        
        // 2. Scripting
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        
        // 3. UI
        cp5 = new ControlP5(this);
        setupMainUI();
        setupIDE();
        setupFXWindow();
    }

    public void draw() {
        background(0);
        
        sourceA.update();
        sourceB.update();
        sourceA.draw();
        sourceB.draw();

        finalCanvas.beginDraw();
        finalCanvas.background(0);
        finalCanvas.tint(255, (1.0f - mixRatio) * 255);
        finalCanvas.image(sourceA.buffer, 0, 0);
        finalCanvas.tint(255, mixRatio * 255);
        finalCanvas.image(sourceB.buffer, 0, 0);
        finalCanvas.noTint();
        finalCanvas.endDraw();

        image(finalCanvas, 0, 0);
        drawOverlay();
    }

    // --- MODULE: IDE & FILE OPERATIONS ---
    void setupIDE() {
        ideWindow = GWindow.getWindow(this, "UX Chaos - IDE", 100, 100, 600, 800, P2D);
        ideWindow.addDrawHandler(this, "drawIDE");
        
        editor = new GTextArea(ideWindow, 10, 100, 580, 500);
        editor.addEventHandler(this, "handleCodeUpdate");
        
        GButton saveBtn = new GButton(ideWindow, 10, 40, 80, 30, "SAVE");
        saveBtn.addEventHandler(this, "handleSave");
        
        GButton loadBtn = new GButton(ideWindow, 100, 40, 80, 30, "OPEN");
        loadBtn.addEventHandler(this, "handleLoad");
        
        GButton sourceABtn = new GButton(ideWindow, 400, 40, 90, 30, "SOURCE_A");
        sourceABtn.addEventHandler(this, "switchToA");
        
        GButton sourceBBtn = new GButton(ideWindow, 500, 40, 90, 30, "SOURCE_B");
        sourceBBtn.addEventHandler(this, "switchToB");
    }

    public void drawIDE(PApplet app, GWinData data) {
        app.background(15, 20, 30);
        app.fill(0, 255, 200);
        app.text("IDE // EDITING: " + activeSource.name + " [" + activeSource.getClass().getSimpleName() + "]", 10, 25);
        app.fill(255, 50);
        app.text("STATUS: " + scriptError, 10, 750);
    }

    public void handleCodeUpdate(GTextArea area, GEvent event) {
        if (event == GEvent.CHANGED) activeSource.code = area.getText();
    }

    public void handleSave(GButton btn, GEvent event) {
        selectOutput("Save Visual Script:", "fileSaved");
    }

    public void fileSaved(File f) {
        if (f != null) saveStrings(f.getAbsolutePath(), new String[]{activeSource.code});
    }

    public void handleLoad(GButton btn, GEvent event) {
        selectInput("Open Visual Script:", "fileLoaded");
    }

    public void fileLoaded(File f) {
        if (f != null) {
            String[] lines = loadStrings(f.getAbsolutePath());
            activeSource.code = join(lines, "
");
            editor.setText(activeSource.code);
        }
    }

    public void switchToA(GButton b, GEvent e) { activeSource = sourceA; editor.setText(sourceA.code); }
    public void switchToB(GButton b, GEvent e) { activeSource = sourceB; editor.setText(sourceB.code); }

    // --- MODULE: FX & MIXER ---
    void setupFXWindow() {
        fxWindow = GWindow.getWindow(this, "Post-Processing & Master", 800, 100, 300, 400, P2D);
        fxWindow.addDrawHandler(this, "drawFXWindow");
        
        // This would contain checkboxes for Dither, Chromatic Aberration, etc.
    }

    public void drawFXWindow(PApplet app, GWinData data) {
        app.background(25, 10, 20);
        app.fill(255); app.text("MASTER_FX_STACK", 10, 25);
    }

    void setupMainUI() {
        cp5.addSlider("mixRatio").setPosition(20, height - 60).setSize(300, 25).setRange(0, 1).setCaptionLabel("CROSSFADER (A <-> B)");
        
        cp5.addScrollableList("typeA").setPosition(20, 20).setSize(150, 200)
           .setBarHeight(20).setItemHeight(20).addItems(Arrays.asList("VIDEO", "SHADER", "PROCESSING", "SPOUT"))
           .setCaptionLabel("SOURCE_A_TYPE");
           
        cp5.addScrollableList("typeB").setPosition(180, 20).setSize(150, 200)
           .setBarHeight(20).setItemHeight(20).addItems(Arrays.asList("VIDEO", "SHADER", "PROCESSING", "SPOUT"))
           .setCaptionLabel("SOURCE_B_TYPE");
    }

    void drawOverlay() {
        noStroke(); fill(0, 150); rect(0, 0, width, 40);
        fill(0, 255, 200); textSize(11);
        text("UX_CHAOS_META_ENGINE // MASTER_IDE // FPS: " + floor(frameRate), 15, 25);
    }

    public void movieEvent(Movie m) { m.read(); }
}
