/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.util;
import java.awt.FileDialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

// TODO: Auto-generated Javadoc
/**
 * The Class SparqlScope.
 */
public final class SparqlScope implements ActionListener, ItemListener, SparqlScopeListener {
	private static final Logger log = LoggerFactory.getLogger(SparqlScope.class);
	
	/** The t jena interpreter window. */
	static SparqlScopeWindow tSparqlScopeWindow;
	
	/** The interp. */
	static SparqlScope interp;
	private SparqlScopeImplementor jil;
    
    /**
     * Save.
     *
     * @param name the name
     * @param text the text
     */
    static void Save(String name,String text) {
    	try {
			FileOutputStream fos = new FileOutputStream(name);
			fos.write(text.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			log.error(name,e);
		} catch (IOException e) {
			log.error(name,e);
		}
    }

    /**
     * Load data.
     *
     * @param name the name
     * @return the string
     */
    static String LoadData(String name) {
    	try {
			File f = new File(name);
			long size = f.length();
			FileInputStream fis = new FileInputStream(f);
			byte[] ba = new byte[(int)size];
			fis.read(ba);
			String text = new String(ba);
			return text;
		} catch (FileNotFoundException e) {
			log.warn(name);
		} catch (IOException e) {
			log.error(name,e);
		}
		return "";
    }

    /**
     * Save.
     *
     * @param name the name
     */
    static void Save(String name) {
			String text = tSparqlScopeWindow.textAreaCmds.getText();
	    	Save(name,text);
    }

    /**
     * Load.
     *
     * @param name the name
     */
    static void Load(String name) {
    	String text = LoadData(name);
		tSparqlScopeWindow.textAreaCmds.setText(text);
    }

    private static String statefile =
    	System.getProperty("java.io.tmpdir", ".\\")+"SparqlScopeState.txt";
    
    /**
     * Save state.
     */
    static void SaveState() {
    	Save(statefile);
    }

    /**
     * Load state.
     */
    static void LoadState() {
		Load(statefile);
    }

    /**
     * Scope.
     */
    public static void scope(){
        new SparqlScope(new SparqlScopeImpl()); // SPARQL Scope
    }
    
    /**
     * Scope.
     *
     * @param model the model
     */
    public static void scope(Model model){
        new SparqlScope(new SparqlScopeImpl(model)); // SPARQL Scope
    }
    
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String args[]) throws Exception  {
    	
//		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
//		"c.../RuleConfigContext.xml");

		new SparqlScope(new SparqlScopeImpl());
    }
    
    /**
     * Instantiates a new jena interpreter.
     *
     * @param nil the nil
     */
    public SparqlScope(SparqlScopeImplementor nil){
    	this(nil,false);
    }
    
    /** The last load dir. */
    String lastLoadDir;
    
    /** The last save dir. */
    String lastSaveDir;
    
    /**
     * Gets the filename.
     *
     * @param title the title
     * @param dirName the dir name
     * @param filter the filter
     * @param loadSave the load save
     * @return the filename
     */
    public String getFilename(String title, String dirName, String filter, int loadSave) {
        FileDialog d = new FileDialog(tSparqlScopeWindow, title, loadSave);
        if (loadSave == FileDialog.LOAD) {
            if (lastLoadDir == null)
                 d.setDirectory(dirName);
            else d.setDirectory(lastLoadDir);
        }
        else {
            if (lastSaveDir == null)
                 d.setDirectory(dirName);
            else d.setDirectory(lastSaveDir);
        }
        d.setFile(filter);
        Point loc = tSparqlScopeWindow.getLocation();
        d.setLocation(loc.x + 50, loc.y + 50);
        d.setVisible(true);

        String fileName = d.getFile();
        if (fileName == null)
            return null;
        String dir = d.getDirectory();
        if (loadSave == FileDialog.LOAD)
             lastLoadDir = dir;
        else lastSaveDir = dir;

        return dir + fileName;
    }
    
    /**
     * Instantiates a new jena interpreter.
     *
     * @param nil the nil
     * @param exitOnClose the exit on close
     */
    public SparqlScope(SparqlScopeImplementor nil,boolean exitOnClose){
    	
    	this.jil = nil;
    	nil.registerListener(this);
    	tSparqlScopeWindow = new SparqlScopeWindow();
    	tSparqlScopeWindow.buttonExec.addActionListener(this);
    	tSparqlScopeWindow.menuItemOpen.addActionListener(this);
    	tSparqlScopeWindow.menuItemSave.addActionListener(this);
    	tSparqlScopeWindow.menuItemShortNames.addItemListener(this);
    	tSparqlScopeWindow.exitOnClose=exitOnClose;
    	tSparqlScopeWindow.pack();
    	//	you can tSparqlScopeWindow.setSize(width,height); here
    	tSparqlScopeWindow.setLocation(200,200); 
    	LoadState();
    	tSparqlScopeWindow.show();    	
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == tSparqlScopeWindow.buttonExec) {
        	tSparqlScopeWindow.textAreaResults.setText("");
        	String sin = tSparqlScopeWindow.textAreaCmds.getSelectedText();
        	if (sin.equals("")){
        		sin = tSparqlScopeWindow.textAreaCmds.getText();
        	}
        	String s = jil.submitCommands(sin);
        	tSparqlScopeWindow.textAreaResults.append(s);
        }
        else if (e.getSource() == tSparqlScopeWindow.menuItemOpen) {
            String fileName = getFilename("Select Cmd File to Open", "\\Jena\\Models\\",
                    "*.cmds", FileDialog.LOAD);
            if (fileName != null) {
            	tSparqlScopeWindow.textAreaCmds.setText("");
            	Load(fileName);
            }
        }
        else if (e.getSource() == tSparqlScopeWindow.menuItemSave) {
            String fileName = getFilename("Select Cmd File to Save", "\\Jena\\Models\\",
                    "*.cmds", FileDialog.SAVE);
            if (fileName != null)
            	Save(fileName);
        }
    }
    
    /* (non-Javadoc)
     * @see org.pipseq.common.SparqlScopeListener#resultEvent(java.lang.String)
     */
    public void resultEvent(String s){
		tSparqlScopeWindow.textAreaResults.append(
				s
				);
    }
    
    /**
     * Close.
     */
    public void close(){
    	this.tSparqlScopeWindow.close();
    }
    
    /**
     * Checks if is showing.
     *
     * @return true, if is showing
     */
    public boolean isShowing(){
    	return tSparqlScopeWindow.isShowing();
    }

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == tSparqlScopeWindow.menuItemShortNames) {
        	jil.setUseShortNames(tSparqlScopeWindow.menuItemShortNames.getState());
        }
		
	}
}


