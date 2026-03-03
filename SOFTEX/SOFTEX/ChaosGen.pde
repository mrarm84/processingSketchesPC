class ChaosGen extends Source {
  float t = 0;
  float p1 = 1.0; // Parameter 1 (Scale)
  float p2 = 1.0; // Parameter 2 (Complexity)
  float p3 = 0.5; // Parameter 3 (Color Shift)
  
  ChaosGen() {
    super();
    name = "Chaos Field";
    blendMode = 1; // Default to Add for glowing effects
  }
  
  void update() {
    t += 0.01;
    // Animate parameters slightly or link to audio later
    
    canvas.beginDraw();
    canvas.background(0, 10); // Trails
    canvas.strokeWeight(2);
    
    for (int i = 0; i < 200; i++) {
       float x = random(width);
       float y = random(height);
       
       // Algorithm: Trigonometric Interference
       // angle = sin(x * p1) + cos(y * p2)
       float angle = sin(x * 0.01 * p1 + t) + cos(y * 0.01 * p2);
       
       float len = 20;
       float x2 = x + cos(angle) * len;
       float y2 = y + sin(angle) * len;
       
       canvas.stroke(
         (sin(t + x * 0.001) + 1) * 127 * p3, 
         (cos(t + y * 0.001) + 1) * 127, 
         200, 
         150
       );
       canvas.line(x, y, x2, y2);
    }
    
    canvas.endDraw();
  }
  
  // Export logic to PHP (GD Library)
  void exportPHP(String filename) {
    String php = "<?php\n";
    php += "$width = 1280; $height = 720;\n";
    php += "$img = imagecreatetruecolor($width, $height);\n";
    php += "$bg = imagecolorallocate($img, 0, 0, 0);\n";
    php += "imagefill($img, 0, 0, $bg);\n";
    php += "// Parameters\n";
    php += "$p1 = " + p1 + ";\n";
    php += "$p2 = " + p2 + ";\n";
    php += "$p3 = " + p3 + ";\n";
    php += "$t = " + t + ";\n"; // Capture current time state
    
    php += "for ($i = 0; $i < 5000; $i++) {\n";
    php += "  $x = rand(0, $width);\n";
    php += "  $y = rand(0, $height);\n";
    php += "  $angle = sin($x * 0.01 * $p1 + $t) + cos($y * 0.01 * $p2);\n";
    php += "  $len = 20;\n";
    php += "  $x2 = $x + cos($angle) * $len;\n";
    php += "  $y2 = $y + sin($angle) * $len;\n";
    php += "  $r = (sin($t + $x * 0.001) + 1) * 127 * $p3;\n";
    php += "  $g = (cos($t + $y * 0.001) + 1) * 127;\n";
    php += "  $b = 200;\n";
    php += "  $col = imagecolorallocatealpha($img, $r, $g, $b, 50);\n"; // Alpha 0-127 in GD? No, 0-127 where 0 is opaque. 
    // Java alpha 150/255 -> ~60%. 
    // GD Alpha: 0 (opaque) to 127 (transparent). 
    // 150/255 is approx 0.6 opacity. 
    // We want 0.6 opacity -> 0.4 transparency -> ~50.
    php += "  imageline($img, $x, $y, $x2, $y2, $col);\n";
    php += "}\n";
    php += "header('Content-Type: image/png');\n";
    php += "imagepng($img);\n";
    php += "imagedestroy($img);\n";
    php += "?>";
    
    saveStrings(filename, new String[] { php });
    println("Exported PHP algorithm to " + filename);
  }
}
