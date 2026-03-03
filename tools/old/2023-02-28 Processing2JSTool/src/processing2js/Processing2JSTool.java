package processing2js;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import processing.app.Base;
import processing.app.ui.Editor;
import processing.app.Base;
import processing.app.tools.Tool;
import processing.app.ui.Editor;

/**
 *
 * @author dahjon
 */
public class Processing2JSTool implements Tool {

    Base base;

    @Override
    public void init(Base base) {
        Debug.trace("RunAsProcessingJS init...");
        this.base = base;

    }

    @Override
    public void run() {
        Editor editor = base.getActiveEditor();
//        File fil = editor.getSketch().getMainFile();
//        //String txt = editor.getTextArea().getText();
//        String absolutePath = fil.getAbsolutePath();
        new Processing2JS(base);
    }

    @Override
    public String getMenuTitle() {
        Debug.trace("RProcessing2JS getMenuTitle...");

        return "Processing2JS";
    }

}
