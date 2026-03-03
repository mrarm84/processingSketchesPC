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

// --- UX Chaos Visual - META MIXER v6.0 (SETTINGS ENABLED) ---

// --- MODULE: SETTINGS (PERSISTENCE) ---
class Settings {
  String openRouterKey = "";
  String licenseKey = "";
  boolean isLicensed = false;
  float defaultMixRatio = 0.5;
  String outputName = "UXChaos_Output";
  
  void save() {
    JSONObject json = new JSONObject();
    json.setString("openRouterKey", openRouterKey);
    json.setString("licenseKey", licenseKey);
    json.setBoolean("isLicensed", isLicensed);
    json.setFloat("mixRatio", defaultMixRatio);
    saveJSONObject(json, "data/settings.json");
  }
  
  void load() {
    File f = new File(dataPath("settings.json"));
    if (f.exists()) {
      JSONObject json = loadJSONObject("data/settings.json");
      openRouterKey = json.getString("openRouterKey", "");
      licenseKey = json.getString("licenseKey", "");
      isLicensed = json.getBoolean("isLicensed", false);
      defaultMixRatio = json.getFloat("mixRatio", 0.5);
    }
  }
}

Settings config = new Settings();
String validKey = "CHAOS_PRO_2026";
String inputKey = "";

// Dual Source Architecture
class Source {
  String name;
  PGraphics buffer;
  String code;
  Source(String n, PApplet p) {
    name = n;
    buffer = p.createGraphics(p.width, p.height, P3D);
    code = "// Source " + n + "\n// Use 'g' to draw\nvar v = globals.get('v');\ng.beginDraw();\ng.background(0, 15);\ng.fill(0, 255, 200, 100);\ng.rect(100, 100, 200 * v, 200 * v);\ng.endDraw();";
  }
}

Source sourceA, sourceB;
Source activeEditSource;
float mixRatio = 0.5;

// Video & Output
Spout spout;
SyphonServer syphon;
PGraphics finalCanvas; 
PShader fxShader;
boolean isRecording = false;

// UI & Windows
GWindow codeWindow, settingsWindow;
GTextArea codeArea;
GTextField promptField, apiField;
ControlP5 cp5;
String scriptError = "NONE";
String aiStatus = "IDLE";

// Audio
import processing.sound.*;
AudioIn input;
Amplitude amp;
float volume = 0;

// FX Parameters
float aberrationAmount = 10.0;
float glitchIntensity = 0.5;
boolean showOutput = true;

// Scripting Engine
ScriptEngine engine;
Map<String, Object> globals = new HashMap<String, Object>();

void setup() {
  size(1024, 768, P3D);
  surface.setTitle("UX Chaos Visual - Meta Mixer (Settings)");
  
  // 1. Load Settings
  config.load();
  mixRatio = config.defaultMixRatio;
  
  sourceA = new Source("A", this);
  sourceB = new Source("B", this);
  activeEditSource = sourceA;
  
  finalCanvas = createGraphics(width, height, P3D);
  fxShader = loadShader("chaos_fx.glsl");
  
  if (platform == WINDOWS) {
    try { spout = new Spout(this); spout.createSender(config.outputName); } catch (Exception e) {}
  } else if (platform == MACOS) {
    try { syphon = new SyphonServer(this, config.outputName); } catch (Exception e) {}
  }
  
  input = new AudioIn(this, 0);
  input.start();
  amp = new Amplitude(this);
  amp.input(input);
  
  ScriptEngineManager manager = new ScriptEngineManager();
  engine = manager.getEngineByName("JavaScript");
  
  cp5 = new ControlP5(this);
  setupMainUI();
  setupCodeWindow();
  setupSettingsWindow();
}

void draw() {
  background(0);
  volume = lerp(volume, amp.analyze(), 0.15);
  
  if (!config.isLicensed) {
    drawLicenseScreen();
    return;
  }

  renderSource(sourceA);
  renderSource(sourceB);

  finalCanvas.beginDraw();
  finalCanvas.background(0);
  finalCanvas.tint(255, (1.0 - mixRatio) * 255);
  finalCanvas.image(sourceA.buffer, 0, 0);
  finalCanvas.tint(255, mixRatio * 255);
  finalCanvas.image(sourceB.buffer, 0, 0);
  finalCanvas.noTint();
  
  fxShader.set("amount", aberrationAmount / 1000.0);
  fxShader.set("time", frameCount * 0.1);
  fxShader.set("glitch", glitchIntensity);
  finalCanvas.filter(fxShader);
  finalCanvas.endDraw();
  
  image(finalCanvas, 0, 0);
  if (showOutput) {
    if (spout != null) spout.sendTexture(finalCanvas);
    if (syphon != null) syphon.sendImage(finalCanvas);
  }
  
  if (isRecording) finalCanvas.save("recordings/meta_" + nf(frameCount, 6) + ".png");
  drawOverlay();
}

void renderSource(Source s) {
  if (engine != null) {
    try {
      globals.put("v", volume);
      globals.put("f", frameCount);
      engine.put("globals", globals);
      engine.put("g", s.buffer); 
      engine.eval(s.code);
      scriptError = "RUNNING";
    } catch (Exception e) {
      scriptError = "ERR in " + s.name + ": " + e.getMessage();
    }
  }
}

// --- MODULE: AI INTEGRATION ---

void askAI(String prompt) {
  if (config.openRouterKey.equals("")) { aiStatus = "ERR: NO API KEY"; return; }
  aiStatus = "THINKING...";
  thread("callOpenRouter");
}

void callOpenRouter() {
  try {
    URL url = new URL("https://openrouter.ai/api/v1/chat/completions");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("Authorization", "Bearer " + config.openRouterKey);
    conn.setDoOutput(true);

    String json = "{\"model\": \"openai/gpt-3.5-turbo\", \"messages\": [" +
                  "{\"role\": \"system\", \"content\": \"Generate Processing JS for buffer 'g', use globals.get('v') for volume.\"}," +
                  "{\"role\": \"user\", \"content\": \"" + promptField.getText() + "\"}]}";

    OutputStream os = conn.getOutputStream();
    os.write(json.getBytes());
    os.flush();

    if (conn.getResponseCode() == 200) {
      Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
      String response = s.hasNext() ? s.next() : "";
      JSONObject res = parseJSONObject(response);
      String content = res.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
      activeEditSource.code = content;
      codeArea.setText(content);
      aiStatus = "SUCCESS";
    } else { aiStatus = "ERR: " + conn.getResponseCode(); }
  } catch (Exception e) { aiStatus = "ERR: " + e.getMessage(); }
}

// --- MODULE: SETTINGS WINDOW ---

void setupSettingsWindow() {
  settingsWindow = GWindow.getWindow(this, "Global Settings", 750, 100, 300, 300, P2D);
  settingsWindow.addDrawHandler(this, "drawSettingsWindow");
  
  apiField = new GTextField(settingsWindow, 10, 40, 280, 30);
  apiField.setText(config.openRouterKey);
  apiField.setPromptText("PASTE_OPENROUTER_KEY");
  apiField.addEventHandler(this, "handleKeyUpdate");
  
  GButton saveBtn = new GButton(settingsWindow, 10, 240, 280, 40, "SAVE_AND_CLOSE");
  saveBtn.addEventHandler(this, "handleSaveSettings");
}

public void handleKeyUpdate(GTextField field, GEvent event) { config.openRouterKey = field.getText(); }
public void handleSaveSettings(GButton button, GEvent event) { config.save(); settingsWindow.setVisible(false); }

public void drawSettingsWindow(PApplet app, GWinData data) {
  app.background(20, 25, 30);
  app.fill(255);
  app.text("SYSTEM_CONFIGURATION", 10, 25);
  app.text("OPENROUTER_API_KEY:", 10, 38);
}

// --- MODULE: CODE WINDOW ---

void setupCodeWindow() {
  codeWindow = GWindow.getWindow(this, "UX Chaos - AI IDE", 100, 100, 600, 750, P2D);
  codeWindow.addDrawHandler(this, "drawCodeWindow");
  codeArea = new GTextArea(codeWindow, 10, 80, 580, 450);
  codeArea.setText(activeEditSource.code);
  codeArea.addEventHandler(this, "handleCodeChange");
  promptField = new GTextField(codeWindow, 10, 550, 460, 40);
  GButton genBtn = new GButton(codeWindow, 480, 550, 110, 40, "GENERATE");
  genBtn.addEventHandler(this, "handleGenerate");
  GButton btnA = new GButton(codeWindow, 10, 40, 285, 30, "EDIT_SOURCE_A");
  btnA.addEventHandler(this, "handleEditA");
  GButton btnB = new GButton(codeWindow, 305, 40, 285, 30, "EDIT_SOURCE_B");
  btnB.addEventHandler(this, "handleEditB");
}

public void handleGenerate(GButton button, GEvent event) { askAI(promptField.getText()); }
public void handleEditA(GButton button, GEvent event) { activeEditSource = sourceA; codeArea.setText(sourceA.code); }
public void handleEditB(GButton button, GEvent event) { activeEditSource = sourceB; codeArea.setText(sourceB.code); }
public void drawCodeWindow(PApplet app, GWinData data) {
  app.background(10, 15, 25);
  app.fill(0, 255, 255);
  app.text("AI_STATUS: " + aiStatus + " | SCRIPT: " + scriptError, 10, 700);
}
public void handleCodeChange(GTextArea textArea, GEvent event) { if (event == GEvent.CHANGED) activeEditSource.code = textArea.getText(); }

// --- MODULE: UI ---

void setupMainUI() {
  cp5.addSlider("mixRatio").setPosition(20, height - 120).setSize(200, 20).setRange(0, 1).setCaptionLabel("CROSSFADER");
  cp5.addSlider("aberrationAmount").setPosition(20, height - 80).setRange(0, 100);
  cp5.addSlider("glitchIntensity").setPosition(20, height - 60).setRange(0, 1);
  cp5.addButton("openSettings").setPosition(width - 120, height - 40).setSize(100, 30).setCaptionLabel("SETTINGS");
}

public void openSettings() { settingsWindow.setVisible(true); }

void drawLicenseScreen() {
  background(2, 2, 5);
  textAlign(CENTER, CENTER);
  fill(0, 255, 255); textSize(24);
  text("UX_CHAOS_AI_MIXER", width/2, height/2 - 40);
  fill(255); text("INPUT_KEY: " + inputKey, width/2, height/2 + 20);
}

void drawOverlay() {
  noStroke(); fill(0, 200); rect(0, 0, width, 40);
  fill(0, 255, 255); textSize(11);
  text("UX_CHAOS_META_AI // SOURCE_A <-> SOURCE_B // " + (aiStatus.equals("THINKING...") ? "LLM_PROCESSING..." : "READY"), 15, 25);
}

void keyPressed() {
  if (!config.isLicensed) {
    if (key == ENTER || key == RETURN) {
      if (inputKey.equals(validKey)) { config.isLicensed = true; config.licenseKey = inputKey; config.save(); }
      else inputKey = "";
    } else if (key == BACKSPACE && inputKey.length() > 0) inputKey = inputKey.substring(0, inputKey.length()-1);
    else if (key != CODED && key != ESC) inputKey += key;
  }
}


