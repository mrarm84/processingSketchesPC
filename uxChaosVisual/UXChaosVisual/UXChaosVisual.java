import processing.core.*;
import processing.video.*;
import spout.*;
import codeanticode.syphon.*;
import g4p_controls.*;
import controlP5.*;
import java.io.*;
import javax.script.*;
import java.util.Map;
import java.util.HashMap;
import java.net.*;
import java.util.Scanner;
import java.util.prefs.Preferences;

/**
 * UX Chaos Visual - Meta Mixer v7.0
 * Refactored for Standard Java IDEs (IntelliJ, Eclipse, VS Code)
 */
public class UXChaosVisual extends PApplet {

    // --- SUB-MODULE: SETTINGS MANAGER (OOP & SECURE) ---
    // Uses Java Preferences API instead of a local JSON file
    public class SettingsManager {
        private Preferences prefs = Preferences.userNodeForPackage(UXChaosVisual.class);
        
        public String getOpenRouterKey() { return prefs.get("OR_KEY", ""); }
        public void setOpenRouterKey(String key) { prefs.put("OR_KEY", key); }
        
        public boolean isLicensed() { return prefs.getBoolean("LICENSED", false); }
        public void setLicensed(boolean state) { prefs.putBoolean("LICENSED", state); }
        
        public float getMixRatio() { return prefs.getFloat("MIX_RATIO", 0.5f); }
        public void setMixRatio(float val) { prefs.putFloat("MIX_RATIO", val); }
    }

    // --- SUB-MODULE: SOURCE OBJECT ---
    public class Source {
        public String name;
        public PGraphics buffer;
        public String code;

        public Source(String n, int w, int h) {
            this.name = n;
            this.buffer = createGraphics(w, h, P3D);
            this.code = "// Source " + n + "
var v = globals.get('v');
g.beginDraw();
g.background(0, 10);
g.fill(0, 255, 200);
g.rect(100, 100, 200*v, 200*v);
g.endDraw();";
        }

        public void render(ScriptEngine engine, Map<String, Object> globals) {
            try {
                engine.put("globals", globals);
                engine.put("g", buffer);
                engine.eval(code);
            } catch (Exception e) {
                scriptError = "ERR in " + name + ": " + e.getMessage();
            }
        }
    }

    // --- GLOBAL STATE ---
    SettingsManager settings = new SettingsManager();
    Source sourceA, sourceB;
    Source activeEditSource;
    
    Spout spout;
    SyphonServer syphon;
    PGraphics finalCanvas;
    PShader fxShader;
    
    ControlP5 cp5;
    GWindow codeWindow, settingsWindow;
    GTextArea codeArea;
    GTextField promptField, apiField;
    
    ScriptEngine engine;
    Map<String, Object> globals = new HashMap<>();
    
    import processing.sound.*;
    AudioIn audioInput;
    Amplitude amplitude;
    
    float volume = 0;
    float mixRatio = 0.5f;
    String scriptError = "NONE";
    String aiStatus = "IDLE";
    String validKey = "CHAOS_PRO_2026";
    String inputKey = "";

    public static void main(String[] args) {
        PApplet.main("UXChaosVisual");
    }

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        surface.setTitle("UX Chaos Meta Mixer - Java Edition");
        
        sourceA = new Source("A", width, height);
        sourceB = new Source("B", width, height);
        activeEditSource = sourceA;
        mixRatio = settings.getMixRatio();
        
        finalCanvas = createGraphics(width, height, P3D);
        fxShader = loadShader("chaos_fx.glsl");
        
        initOutput();
        initAudio();
        
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        
        cp5 = new ControlP5(this);
        setupMainUI();
        setupCodeWindow();
        setupSettingsWindow();
    }

    public void draw() {
        background(0);
        updateAudio();
        
        if (!settings.isLicensed()) {
            drawLicenseScreen();
            return;
        }

        globals.put("v", volume);
        globals.put("f", frameCount);
        
        sourceA.render(engine, globals);
        sourceB.render(engine, globals);

        finalCanvas.beginDraw();
        finalCanvas.background(0);
        finalCanvas.tint(255, (1.0f - mixRatio) * 255);
        finalCanvas.image(sourceA.buffer, 0, 0);
        finalCanvas.tint(255, mixRatio * 255);
        finalCanvas.image(sourceB.buffer, 0, 0);
        finalCanvas.noTint();
        
        fxShader.set("amount", 0.01f);
        fxShader.set("time", frameCount * 0.1f);
        finalCanvas.filter(fxShader);
        finalCanvas.endDraw();
        
        image(finalCanvas, 0, 0);
        sendOutput();
        drawOverlay();
    }

    // --- OUTPUT LOGIC ---
    void initOutput() {
        if (platform == WINDOWS) {
            try { spout = new Spout(this); spout.createSender("UXChaos_Output"); } catch (Exception e) {}
        } else if (platform == MACOS) {
            try { syphon = new SyphonServer(this, "UXChaos_Output"); } catch (Exception e) {}
        }
    }

    void sendOutput() {
        if (spout != null) spout.sendTexture(finalCanvas);
        if (syphon != null) syphon.sendImage(finalCanvas);
    }

    // --- AUDIO LOGIC ---
    void initAudio() {
        audioInput = new AudioIn(this, 0);
        audioInput.start();
        amplitude = new Amplitude(this);
        amplitude.input(audioInput);
    }

    void updateAudio() {
        volume = lerp(volume, amplitude.analyze(), 0.15f);
    }

    // --- UI MODULES (G4P & CP5) ---
    void setupMainUI() {
        cp5.addSlider("mixRatio").setPosition(20, height - 80).setSize(200, 20).setRange(0, 1).setCaptionLabel("CROSSFADER");
        cp5.addButton("openSettings").setPosition(width - 120, height - 40).setSize(100, 30).setCaptionLabel("SETTINGS");
    }

    public void openSettings() { settingsWindow.setVisible(true); }

    void setupSettingsWindow() {
        settingsWindow = GWindow.getWindow(this, "Global Settings", 750, 100, 300, 300, P2D);
        settingsWindow.addDrawHandler(this, "drawSettingsWindow");
        apiField = new GTextField(settingsWindow, 10, 40, 280, 30);
        apiField.setText(settings.getOpenRouterKey());
        GButton saveBtn = new GButton(settingsWindow, 10, 240, 280, 40, "SAVE_AND_CLOSE");
        saveBtn.addEventHandler(this, "handleSaveSettings");
    }

    public void handleSaveSettings(GButton button, GEvent event) {
        settings.setOpenRouterKey(apiField.getText());
        settings.setMixRatio(mixRatio);
        settingsWindow.setVisible(false);
    }

    public void drawSettingsWindow(PApplet app, GWinData data) {
        app.background(20, 25, 30);
        app.fill(255); app.text("SYSTEM_CONFIGURATION", 10, 25);
    }

    void setupCodeWindow() {
        codeWindow = GWindow.getWindow(this, "UX Chaos - AI IDE", 100, 100, 600, 750, P2D);
        codeWindow.addDrawHandler(this, "drawCodeWindow");
        codeArea = new GTextArea(codeWindow, 10, 80, 580, 450);
        codeArea.setText(activeEditSource.code);
        codeArea.addEventHandler(this, "handleCodeChange");
        promptField = new GTextField(codeWindow, 10, 550, 460, 40);
        GButton genBtn = new GButton(codeWindow, 480, 550, 110, 40, "GENERATE");
        genBtn.addEventHandler(this, "handleGenerate");
    }

    public void handleGenerate(GButton button, GEvent event) { askAI(promptField.getText()); }
    
    public void drawCodeWindow(PApplet app, GWinData data) {
        app.background(10, 15, 25);
        app.fill(0, 255, 255); app.text("AI_STATUS: " + aiStatus, 10, 700);
    }

    public void handleCodeChange(GTextArea textArea, GEvent event) {
        if (event == GEvent.CHANGED) activeEditSource.code = textArea.getText();
    }

    // --- AI ENGINE ---
    void askAI(String prompt) {
        if (settings.getOpenRouterKey().isEmpty()) { aiStatus = "ERR: NO KEY"; return; }
        aiStatus = "THINKING...";
        thread("callOpenRouter");
    }

    public void callOpenRouter() {
        try {
            URL url = new URL("https://openrouter.ai/api/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + settings.getOpenRouterKey());
            conn.setDoOutput(true);

            String json = "{"model": "openai/gpt-3.5-turbo", "messages": [" +
                          "{"role": "system", "content": "Processing JS for 'g' buffer, volume 'v'."}," +
                          "{"role": "user", "content": "" + promptField.getText() + ""}]}";

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            if (conn.getResponseCode() == 200) {
                Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\A");
                String response = s.hasNext() ? s.next() : "";
                JSONObject res = parseJSONObject(response);
                String content = res.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                activeEditSource.code = content;
                codeArea.setText(content);
                aiStatus = "SUCCESS";
            } else { aiStatus = "ERR: " + conn.getResponseCode(); }
        } catch (Exception e) { aiStatus = "ERR: " + e.getMessage(); }
    }

    // --- UTILS ---
    void drawLicenseScreen() {
        background(5); textAlign(CENTER, CENTER);
        fill(0, 255, 255); text("UX_CHAOS_AI_MIXER_PRO", width/2, height/2 - 20);
        fill(255, 50); text("INPUT KEY: " + inputKey, width/2, height/2 + 20);
    }

    void drawOverlay() {
        fill(0, 255, 255); textSize(11);
        text("FPS: " + floor(frameRate), 15, 25);
    }

    public void keyPressed() {
        if (!settings.isLicensed()) {
            if (key == ENTER || key == RETURN) {
                if (inputKey.equals(validKey)) { settings.setLicensed(true); }
                else inputKey = "";
            } else if (key == BACKSPACE && inputKey.length() > 0) inputKey = inputKey.substring(0, inputKey.length()-1);
            else if (key != CODED && key != ESC) inputKey += key;
        }
    }
}
