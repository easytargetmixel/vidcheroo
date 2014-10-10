/*
 * Copyright (C) 2014 Easy Target
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eztarget.vidcheroo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ControlFrame extends JFrame {
	
	private static final long serialVersionUID = 201408251912L;
	
	/**
	 * Initial x-value of top-left coordinate
	 */
	private static final int FRAME_INITIAL_X = 40;
	
	/**
	 * Initial y-value of top-left coordinate
	 */
	private static final int FRAME_INITIAL_Y = 40;
	
	/**
	 * Fixed width value
	 */
	private static final int FRAME_WIDTH = 240;
	
	/**
	 * Fixed height value, relative to width
	 */
	private static final int FRAME_HEIGHT = (int) (FRAME_WIDTH * 2.2f);
	
	/**
	 * Margin spacing between GUI elements
	 */
	private static final int MARGIN = 8;
	
	/**
	 * Width of large GUI elements, relative to frame width
	 */
	private static final int ELEMENT_WIDTH = FRAME_WIDTH - (2 * MARGIN);
	
	/**
	 * Width of smaller GUI elements, half the width of large elements
	 */
	private static final int ELEMENT_WIDTH_S = (int) (ELEMENT_WIDTH * 0.49);
	
	/**
	 * Fixed height of all GUI elements
	 */
	private static final int ELEMENT_HEIGHT = 26;
	
	/**
	 * Fixed x-value of top-left coordinate of second-row, small GUI elements 
	 */
	private static final int ELEMENT_S_COL2_X = FRAME_WIDTH - MARGIN - ELEMENT_WIDTH_S;
	
	/**
	 * Apply custom, non-OS-specific colours
	 */
	private static final boolean APPLY_DESIGN = false;
	
	/**
	 * Custom colour for non-OS-specific design
	 */
	private static final Color COLOR_1 = new Color(246, 127, 1);
	
	/**
	 * Custom colour for non-OS-specific design
	 */
	private static final Color COLOR_2 = Color.WHITE;
	
	/**
	 * Custom colour for non-OS-specific design
	 */
	private static final Color COLOR_3 = new Color(255, 147, 21);

	/**
	 * Colour of the font of the highlighted note length button
	 */
	private static final Color LENGTH_COLOR_HIGHLT = Color.black;
	
	/**
	 * Colour of the font of all non-highlighted note length button
	 */
	private static final Color LENGTH_COLOR_NORMAL = Color.orange;
		
	/**
	 * Status label at the bottom of the control frame
	 */
	private JLabel statusLabel = new JLabel("Waiting for engine...");
	
	/**
	 * Text field that displays and changes the current tempo/BPM
	 */
	private JTextField tempoTextField;
	
	/**
	 * All other buttons
	 */
	private JButton playButton, pauseButton, fullscreenButton, mediaPathButton, vlcPathButton;
	
	/**
	 * Array of note length buttons in the tempo section
	 */
	private JButton[] lengthButtons;
	
	/**
	 *	Constructor containing entire GUI setup.
	 */
	public ControlFrame() {
		System.out.println("Initialising Control Frame.");
		System.out.println("Applying design: " + APPLY_DESIGN);
		
		//setBounds(FRAME_INITIAL_X, FRAME_INITIAL_Y, FRAME_WIDTH + 5, FRAME_HEIGHT + 20);
		setLocation(FRAME_INITIAL_X, FRAME_INITIAL_Y);
		//setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                new ExitDialog(null);
            }
        });
		setResizable(false);
		setLayout(null);
		setTitle("Vidcheroo Controller");
		
		/*
		 * APPLICATION ICON
		 */
		
		java.net.URL url = ClassLoader.getSystemResource(Launcher.ICON_PATH);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image image = kit.createImage(url);
		setIconImage(image);

		if (Engine.getOs() == SupportedOperatingSystems.OSX) {
			try {
				Class<?> application = Class.forName("com.apple.eawt.Application");
				Method getApplication;
				getApplication = application.getMethod("getApplication", new Class[0]);
				Object app = getApplication.invoke(null);
				Method setDockImage = application.getMethod("setDockIconImage", Image.class);
				setDockImage.invoke(app, image);
				//Application application = Application.getApplication();
				//application.setDockIconImage(image);
			} catch (Exception e) {
				System.err.println("ERROR: Cannot load application icon.");
				e.printStackTrace();
			}
		}
		
		/*
		 * CONTENT PANE
		 */
		
		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setBackground(COLOR_2);
		contentPane.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		pack();
		
		/*
		 * TOP PANEL
		 */
		
		JPanel topPanel = new JPanel();
		final int fTopPanelHeight = MARGIN + (2 * ELEMENT_HEIGHT) + MARGIN + ELEMENT_HEIGHT + MARGIN;
		topPanel.setBounds(0, 0, FRAME_WIDTH, fTopPanelHeight);
		topPanel.setLayout(null);
		if (APPLY_DESIGN) topPanel.setBackground(COLOR_1);
		getContentPane().add(topPanel);

		// PLAY Button:
		playButton = new JButton("Play");
		playButton.setBounds(MARGIN, MARGIN, ELEMENT_WIDTH, ELEMENT_HEIGHT * 2);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Engine.play();
			}
		});
		if (APPLY_DESIGN) {
			playButton.setForeground(COLOR_1);
			playButton.setBackground(COLOR_2);
			playButton.setBorderPainted(false);
		}
		topPanel.add(playButton);
		
		final int fTopPanelRow2Y = MARGIN + (ELEMENT_HEIGHT * 2) + MARGIN;
		final boolean fShowFullscrnBtn = Engine.getOs() != SupportedOperatingSystems.OSX;
		//final boolean fShowFullscrnBtn = true;
		
		// PAUSE Button:
		pauseButton = new JButton("Pause");
		
		// If a full-screen button is displayed, shorten the pause button.
		int pauseButtonWidth;
		if (fShowFullscrnBtn) pauseButtonWidth = ELEMENT_WIDTH_S;
		else pauseButtonWidth = ELEMENT_WIDTH;
		
		pauseButton.setBounds(MARGIN, fTopPanelRow2Y, pauseButtonWidth, ELEMENT_HEIGHT);
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Engine.pause();
			}
		});
		if (APPLY_DESIGN) {
			pauseButton.setForeground(COLOR_2);
			pauseButton.setBackground(COLOR_1);
			pauseButton.setBorderPainted(false);
		}
		topPanel.add(pauseButton);
		
		if (fShowFullscrnBtn) {
			// FULLSCREEN Button
			fullscreenButton = new JButton("Fullscreen");
			fullscreenButton.setBounds(
					ELEMENT_S_COL2_X,
					fTopPanelRow2Y,
					ELEMENT_WIDTH_S, 
					ELEMENT_HEIGHT);
			fullscreenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Engine.toggleFullscreen();
				}
			});
			if (APPLY_DESIGN) {
				fullscreenButton.setForeground(COLOR_2);
				fullscreenButton.setBackground(COLOR_1);
				fullscreenButton.setBorderPainted(false);
			}
			
			fullscreenButton.setEnabled(false);
			topPanel.add(fullscreenButton);
		} else {
			fullscreenButton = null;
		}
		
		/*
		 * TEMPO SECTION
		 */
		
		// TEMPO Label:
		JLabel tempoLabel = new JLabel("Tempo");
		//TODO: Calculate width of tempo label.
		final int fTempoLabelWidth = ELEMENT_WIDTH_S / 2;
		final int fTempoSectionRow1Y = fTopPanelHeight + MARGIN;
		tempoLabel.setBounds(MARGIN, fTempoSectionRow1Y, fTempoLabelWidth, ELEMENT_HEIGHT);
		if (APPLY_DESIGN) tempoLabel.setForeground(COLOR_1);
		contentPane.add(tempoLabel);
		
		// TEMPO Text Field:
		tempoTextField = new JTextField();
		tempoTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurationHandler.setTempo(tempoTextField.getText());				
			}
		});
		tempoTextField.setBounds(
				MARGIN + fTempoLabelWidth,
				fTempoSectionRow1Y,
				FRAME_WIDTH - MARGIN - fTempoLabelWidth - MARGIN,
				ELEMENT_HEIGHT);
		tempoTextField.setColumns(5);
		if (APPLY_DESIGN) {
			tempoTextField.setForeground(COLOR_1);
			tempoTextField.setBackground(COLOR_2);
			tempoTextField.setBorder(null);
		}
		contentPane.add(tempoTextField);

		/*
		 * NOTE LENGTH BUTTONS
		 */
		
		int lengthButtonY = fTempoSectionRow1Y + ELEMENT_HEIGHT + MARGIN;
		int numOfBeatButtons = NoteLength.readableNoteLengths.length;
		lengthButtons = new JButton[numOfBeatButtons];
		for (int i = 0; i < numOfBeatButtons; i++) {
			JButton lengthButton = new JButton(NoteLength.readableNoteLengths[i]);
			
			// Even buttons are left, uneven buttons are right.
			int beatButtonX;
			if (i % 2 == 0) beatButtonX = MARGIN;
			else beatButtonX = ELEMENT_S_COL2_X;
						
			lengthButton.addActionListener(beatFracChanged);
			lengthButton.setBounds(beatButtonX, lengthButtonY, ELEMENT_WIDTH_S, ELEMENT_HEIGHT * 2);
			lengthButton.setForeground(LENGTH_COLOR_NORMAL);
			if (APPLY_DESIGN) {
				lengthButton.setForeground(COLOR_2);
				lengthButton.setBackground(COLOR_3);
				lengthButton.setBorderPainted(false);
			}
			contentPane.add(lengthButton);
			
			// Move Y down if this is an uneven button.
			if (i % 2 != 0) lengthButtonY += (ELEMENT_HEIGHT * 2) + MARGIN;
			
			lengthButtons[i] = lengthButton;
		}
		
		/*
		 * BOTTOM PANEL
		 */
		
		JPanel bottomPanel = new JPanel();
		int bottomPanelHeight = MARGIN + ELEMENT_HEIGHT + MARGIN + ELEMENT_HEIGHT + ELEMENT_HEIGHT;
		bottomPanel.setBounds(
				0,
				FRAME_HEIGHT - bottomPanelHeight,
				FRAME_WIDTH,
				bottomPanelHeight
				);
		bottomPanel.setLayout(null);
		if (APPLY_DESIGN) bottomPanel.setBackground(COLOR_1);
		contentPane.add(bottomPanel);
		
		// FIND MEDIA FILES Button:
		mediaPathButton = new JButton("Select Media Path");
		mediaPathButton.addActionListener(openPathListener);
		mediaPathButton.setBounds(MARGIN, MARGIN, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		if (APPLY_DESIGN) {
			mediaPathButton.setForeground(COLOR_2);
			mediaPathButton.setBackground(COLOR_1);
			mediaPathButton.setBorderPainted(false);
		}
		bottomPanel.add(mediaPathButton);
		
		final int fBottomRow2Y = MARGIN + ELEMENT_HEIGHT + MARGIN;
		
		// FIND VLC Button:
		vlcPathButton = new JButton("Select VLC Path");
		vlcPathButton.addActionListener(openPathListener);
		vlcPathButton.setBounds(MARGIN, fBottomRow2Y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		if (APPLY_DESIGN) {
			vlcPathButton.setForeground(COLOR_2);
			vlcPathButton.setBackground(COLOR_1);
			vlcPathButton.setBorderPainted(false);
		}
		bottomPanel.add(vlcPathButton);
				
		// STATUS Label:
		statusLabel.setBounds(
				MARGIN,
				fBottomRow2Y + ELEMENT_HEIGHT,
				ELEMENT_WIDTH,
				ELEMENT_HEIGHT
				);
		if (APPLY_DESIGN) statusLabel.setForeground(COLOR_2);
		bottomPanel.add(statusLabel);
		
		// Disable play/pause control for now.
		setPlayControlEnabled(false);
		
		setVisible(true);
	}
	
	/*
	 * ACTION LISTENER
	 */
	
	ActionListener openPathListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			if (Engine.getStatus() == Status.PLAYING) return;
			
			// Initialise a JFileChooser acting as "directories only".
			String chosenDir = chooseDir();
			if (chosenDir == null) return;
			System.out.println("Chosen Directory: " + chosenDir);
			
			if (e.getSource().equals(mediaPathButton)) {
				ConfigurationHandler.setMediaPath(chosenDir);
			} else if (e.getSource().equals(vlcPathButton)){
				ConfigurationHandler.setVlcPath(chosenDir);
			} else {
				System.err.println("Unknown action event source: " + e.getSource());
			}
		}
		
		private String chooseDir() {
			JFileChooser dirChooser = new JFileChooser();
			dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dirChooser.showSaveDialog(null);
			
			if (dirChooser.getSelectedFile() == null) return null;
			else return dirChooser.getSelectedFile().getAbsolutePath();
		}
	};
	
	ActionListener beatFracChanged = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			System.out.println("Changing beat length: " + actionCommand);
			
			boolean selectionIsValid = false;
			
			for (int index = 0; index < NoteLength.readableNoteLengths.length; index++) {
				if (actionCommand == NoteLength.readableNoteLengths[index]) {
					Engine.setTempoMultiplier(index);
					selectionIsValid = true;
				}
			}
			
			if (!selectionIsValid) {
				// Default is 1/4.
				Engine.setTempoMultiplier(2);
			}
		}
	};
	
	/*
	 * GUI ENABLING & DISABLING
	 */
	
	/**
	 * Sets the enable flag of the top button elements.
	 * 
	 * @param enabled Boolean that will be passed on to setEnabled() of each button
	 */
	public void setPlayControlEnabled(boolean enabled) {
		playButton.setEnabled(enabled);
		pauseButton.setEnabled(enabled);
		if (fullscreenButton != null) fullscreenButton.setEnabled(enabled);
	}
	
	/**
	 * Sets the enable flag of the bottom button elements.
	 * 
	 * @param enabled Boolean that will be passed on to setEnabled() of each button
	 */
	public void setPathControlEnabled(boolean enabled) {
		mediaPathButton.setEnabled(enabled);
		vlcPathButton.setEnabled(enabled);
	}
	
	public void setLengthButtonHighlighted(int lengthIndex) {
		if (lengthButtons == null) {
			System.err.println("ERROR: Note length buttons have not been initialised yet.");
			return;
		}
		
		for (int i = 0; i < lengthButtons.length; i++) {
			if (i == lengthIndex) lengthButtons[i].setForeground(LENGTH_COLOR_HIGHLT);
			else lengthButtons[i].setForeground(LENGTH_COLOR_NORMAL);
		}
	}
	
	/*
	 * GETTER / SETTER
	 */
	
	/**
	 * @return The text that is currently in the bottom status label.
	 */
	public String getStatusText() {
		if (statusLabel == null) return "";
		else return statusLabel.getText();
	}

	/**
	 * @param status The text that should be displayed in the bottom status label.
	 */
	public void setStatusText(String status) {
		statusLabel.setText(status);
	}
	
	/**
	 * @param tempo The float value that will be displayed in the tempo text field.
	 */
	public void setTempoText(float tempo) {
		tempoTextField.setText(String.format("%.1f", tempo));
	}
}
