import processing.core.*;
import gab.opencv.*;
import java.util.ArrayList;

class CyberHand {
  PApplet parent;
  PVector centroid;
  ArrayList<PVector> skeletonPoints;
  ArrayList<PVector> techLines;
  float timeOffset = 0;

  CyberHand(PApplet p) {
    this.parent = p;
    centroid = new PVector();
    skeletonPoints = new ArrayList<PVector>();
    techLines = new ArrayList<PVector>();
  }

  void update(Contour contour, OpenCV opencv) {
    // 1. Find Centroid
    Rectangle bounds = contour.getBoundingBox();
    centroid.set(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
    
    // 2. Find Convex Hull for fingertips
    ArrayList<PVector> hull = contour.getConvexHull();
    skeletonPoints.clear();
    
    // Simplify hull for skeletal fingers
    for (int i = 0; i < hull.size(); i++) {
      PVector p = hull.get(i);
      // Heuristic: Check distance from centroid to identify fingers
      if (p.dist(centroid) > bounds.width * 0.4) {
        skeletonPoints.add(p);
      }
    }
    
    // 3. Generate tech pieces (lines/rects)
    techLines.clear();
    ArrayList<PVector> pts = contour.getPoints();
    for (int i = 0; i < pts.size(); i += 10) {
      if (Math.random() > 0.8) {
        techLines.add(pts.get(i));
      }
    }
  }

  void display(PGraphics g) {
    g.pushMatrix();
    g.translate(g.width, 0);
    g.scale(-g.width/640.0, g.height/480.0); // Mirror and Scale
    
    // Draw Hand Contour as Wireframe
    g.stroke(0, 255, 255, 60);
    g.strokeWeight(1);
    g.noFill();
    g.beginShape();
    ArrayList<PVector> contourPoints = contour.getPoints();
    for (PVector p : contourPoints) {
      g.vertex(p.x, p.y);
    }
    g.endShape(CLOSE);
    
    // Draw "Skeleton"
    g.noFill();
    g.stroke(0, 255, 255, 200);
    g.strokeWeight(3);
    for (int i = 0; i < skeletonPoints.size(); i++) {
      PVector p = skeletonPoints.get(i);
      // Draw bone from centroid to fingertip
      g.line(centroid.x, centroid.y, p.x, p.y);
      
      // Electricity jump between joints
      if (i > 0 && Math.random() > 0.8) {
        PVector prev = skeletonPoints.get(i-1);
        g.stroke(255, 255, 255, 150);
        g.strokeWeight(1);
        float midX = (p.x + prev.x)/2 + random(-10, 10);
        float midY = (p.y + prev.y)/2 + random(-10, 10);
        g.line(p.x, p.y, midX, midY);
        g.line(midX, midY, prev.x, prev.y);
        g.stroke(0, 255, 255, 200);
        g.strokeWeight(3);
      }
      
      // Draw Joint
      g.fill(255, 0, 150, 200);
      g.noStroke();
      g.ellipse(p.x, p.y, 8, 8);
      g.noFill();
    }
    
    // Centroid Hub
    g.stroke(255, 0, 150, 150);
    g.strokeWeight(2);
    g.ellipse(centroid.x, centroid.y, 20, 20);
    g.stroke(0, 255, 255, 150);
    g.ellipse(centroid.x, centroid.y, 30, 30);

    // Draw Rectangular tech bits floating around
    g.rectMode(CENTER);
    for (int i = 0; i < 8; i++) {
      float angle = parent.millis()*0.001 + i * TWO_PI / 8;
      float dist = 50 + sin(parent.millis()*0.003 + i)*20;
      float rx = centroid.x + cos(angle) * dist;
      float ry = centroid.y + sin(angle) * dist;
      
      g.pushMatrix();
      g.translate(rx, ry);
      g.rotate(angle + parent.millis()*0.002);
      g.noFill();
      g.stroke(0, 255, 255, 120);
      g.rect(0, 0, 15, 15);
      g.fill(0, 255, 255, 40);
      g.rect(0, 0, 5, 5);
      g.popMatrix();
    }
    
    g.popMatrix();
  }
}
