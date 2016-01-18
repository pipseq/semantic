/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.util;
import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
// TODO: Auto-generated Javadoc
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.TextArea;

/**
 * The Class SparqlScopeWindow.
 */
class SparqlScopeWindow extends Frame {

	/** The exit on close. */
	boolean exitOnClose=false;
	
	/** The button exec. */
	Button buttonExec=new Button();	//	Execute
	
	/** The label2. */
	Label label2=new Label();	//	
	
	/** The text area cmds. */
	TextArea textAreaCmds=new TextArea("",0,0,TextArea.SCROLLBARS_BOTH);
	
	/** The label result. */
	Label labelResult=new Label();	//	
	
	/** The text area results. */
	TextArea textAreaResults=new TextArea("",0,0,TextArea.SCROLLBARS_BOTH);
	
	/** The menu main. */
	//TextArea textAreaResults=new TextArea("",0,0,TextArea.SCROLLBARS_BOTH);
	Menu menuMain=new Menu();	//	
	
	/** The menu item open. */
	MenuItem menuItemOpen=new MenuItem();	//	Open
	
	/** The menu item save. */
	MenuItem menuItemSave=new MenuItem();	//	Save
	
	/** The menu item4. */
	MenuItem menuItem4=new MenuItem();	//	-
	
	/** The menu item short names. */
	CheckboxMenuItem menuItemShortNames=new CheckboxMenuItem();	//	ShortNames
	
	/** The menu item3. */
	MenuItem menuItem3=new MenuItem();	//	-
	
	/** The menu item exit. */
	MenuItem menuItemExit=new MenuItem();	//	Exit
	
	/** The menu help. */
	Menu menuHelp=new Menu();	//	
	
	/** The menu item about. */
	MenuItem menuItemAbout=new MenuItem();	//	About

/**
 * The main method.
 *
 * @param args the arguments
 */

public static void main(String[] args) {
	SparqlScopeWindow tJenaInterpreterWindow = new SparqlScopeWindow();
	tJenaInterpreterWindow.exitOnClose=true;
	tJenaInterpreterWindow.pack();
	//	you can tJenaInterpreterWindow.setSize(width,height); here
	//	you can tJenaInterpreterWindow.setLocation(x,y); here
	tJenaInterpreterWindow.show();
}

/* (non-Javadoc)
 * @see java.awt.Component#handleEvent(java.awt.Event)
 */

public boolean handleEvent(Event event) {
	if (event.id == Event.WINDOW_DESTROY && exitOnClose) {
		close();
		System.exit(0);
	}
	else if (event.id == Event.WINDOW_DESTROY) {
		close();
	}
	return super.handleEvent(event);
}

/**
 * Close.
 */
protected void close(){
	SparqlScope.SaveState();
	dispose();
}

/**
 * Instantiates a new jena interpreter window.
 */
SparqlScopeWindow() {
	setTitle("SPARQL Scope");
	setForeground(Color.black);
	setBackground(Color.white);
	setFont(new Font("Dialog",0,12));
	GridBagConstraints c = new GridBagConstraints();
	GridBagLayout gridbaglayout=new GridBagLayout();
	setLayout(gridbaglayout);
	buttonExec.setLabel("Execute");
	c.fill=0;
	c.weightx=0.0;
	c.weighty=0.0;
	c.gridx=-1;
	c.gridy=-1;
	c.gridwidth=0;
	c.gridheight=1;
	c.ipadx=0;
	c.ipady=0;
	c.anchor=17;
	gridbaglayout.setConstraints(buttonExec,c);
	add(buttonExec);
	label2.setText("Exec selected commands");
	c.fill=0;
	c.weightx=0.0;
	c.weighty=0.0;
	c.gridx=-1;
	c.gridy=-1;
	c.gridwidth=0;
	c.gridheight=1;
	c.ipadx=0;
	c.ipady=0;
	c.anchor=17;
	gridbaglayout.setConstraints(label2,c);
	add(label2);
	textAreaCmds.setRows(0);
	textAreaCmds.setColumns(0);
	textAreaCmds.setEditable(true);
	textAreaCmds.setText("");
	textAreaCmds.setFont(new Font("Monospaced",Font.PLAIN,14));
	c.fill=1;
	c.weightx=10.0;
	c.weighty=10.0;
	c.gridx=-1;
	c.gridy=-1;
	c.gridwidth=0;
	c.gridheight=1;
	c.ipadx=10;
	c.ipady=10;
	c.anchor=10;
	gridbaglayout.setConstraints(textAreaCmds,c);
	add(textAreaCmds);
	labelResult.setText("Results");
	c.fill=0;
	c.weightx=0.0;
	c.weighty=0.0;
	c.gridx=-1;
	c.gridy=-1;
	c.gridwidth=0;
	c.gridheight=1;
	c.ipadx=0;
	c.ipady=0;
	c.anchor=17;
	gridbaglayout.setConstraints(labelResult,c);
	add(labelResult);
	textAreaResults.setRows(0);
	textAreaResults.setColumns(0);
	textAreaResults.setEditable(true);
	textAreaResults.setText("");
	textAreaResults.setFont(new Font("Monospaced",Font.PLAIN,13));
	textAreaResults.setEditable(false);
	textAreaResults.setBackground(Color.WHITE);
	c.fill=1;
	c.weightx=10.0;
	c.weighty=10.0;
	c.gridx=-1;
	c.gridy=-1;
	c.gridwidth=0;
	c.gridheight=1;
	c.ipadx=10;
	c.ipady=10;
	c.anchor=10;
	gridbaglayout.setConstraints(textAreaResults,c);
	add(textAreaResults);
	MenuBar mb = new MenuBar();
	setMenuBar(mb);
	menuMain.setLabel("File");
	menuItemOpen.setLabel("Open");
	menuMain.add(menuItemOpen);
	menuItemSave.setShortcut(new MenuShortcut(83,false));
	menuItemSave.setLabel("Save");
	menuMain.add(menuItemSave);
	menuItem4.setLabel("-");
	menuMain.add(menuItem4);
	menuItemShortNames.setLabel("Use short names");
	menuItemShortNames.setState(true);
	menuMain.add(menuItemShortNames);
	menuItem3.setLabel("-");
	menuMain.add(menuItem3);
	menuItemExit.setLabel("Exit");
	menuMain.add(menuItemExit);
	menuHelp.setLabel("Help");
	menuItemAbout.setLabel("About");
	menuHelp.add(menuItemAbout);
	mb.add(menuMain);
	mb.add(menuHelp);
	}
}
