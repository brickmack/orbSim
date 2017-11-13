package rocketSim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import org.jzy3d.colors.Color;

public class StartScreen {
	public JButton startButton = new JButton("Start");
	private SpaceObject[] objects;
	private Maneuver[] maneuvers;
	
	public StartScreen() {
		//testing values. Will implement a cleaner solution later
		double earthRadius = 6371000;
		
		Planet earth = new Planet("Earth", 5.972*Math.pow(10, 24), earthRadius, new double[] {0, 0, 0}, new double[] {0, 0, 0}, Color.GREEN, true, true);
		Planet moon = new Planet("Moon", 7.34*Math.pow(10, 22), 1737000, new double[] {0, 1022, 0}, new double[] {384748000, 0, 0}, Color.BLUE, false, false);
		ArrayList<Engine> satEngines = new ArrayList<Engine>();
		satEngines.add(new Engine("AJ-10", 1, 319, new double[] {43700, 15000}, 1, new double[] {20000}, new double[] {200}, true, new String[] {"MMH", "N2O4"}, new double[] {0.5, 0.5}));
		
		ArrayList<PropellantTank> satPropellants = new ArrayList<PropellantTank>();
		satPropellants.add(new PropellantTank("MMH", 3002, 0));
		satPropellants.add(new PropellantTank("N2O4", 3002, 0));
		Spacecraft deltaKStage = new Spacecraft("Delta-K", 6954, 5, new double[] {0, 7950, 0}, new double[] {50000+earthRadius, 0, 0}, Color.RED, true, satEngines, satPropellants, 1000);
		
		objects = new SpaceObject[] {earth, moon, deltaKStage, new SpaceObject("testSat", 100, 2, new double[] {0, 10400, 0}, new double[] {0, 0, 100000+earthRadius}, Color.BLUE, true)};
		
		maneuvers = new Maneuver[] {new Maneuver(deltaKStage, deltaKStage.getVelocity(), 2700, 400, 0.001)};
		
		JFrame window = new JFrame();
		window.setSize(500, 500);
		window.setTitle("GUI");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel top = new JPanel();
		top.add(new JLabel("Header"));
		window.add(top, BorderLayout.PAGE_START);
		
		JPanel middle = new JPanel();
		middle.setLayout(new GridLayout(2,2));
		
		JPanel spacecraftSidePanel = new JPanel();
		spacecraftSidePanel.add(new JLabel("Spacecraft"));
		JButton addSpacecraftButton = new JButton("Add spacecraft");
		addSpacecraftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("adding spacecraft");
				
				JTextField nameField = new JTextField(32);
				JTextField posField = new JTextField(32);
				
				JPanel maneuverDialogPanel = new JPanel();
				maneuverDialogPanel.setLayout(new GridLayout(0,2));
				
				maneuverDialogPanel.add(new JLabel("Name:"));
				maneuverDialogPanel.add(nameField);
				maneuverDialogPanel.add(new JLabel("Position (x,y,z)"));
				maneuverDialogPanel.add(posField);
				
				int result = JOptionPane.showConfirmDialog(null, maneuverDialogPanel, "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
			    if (result == JOptionPane.OK_OPTION) {
			    	
			    }
			}
		});
		spacecraftSidePanel.add(addSpacecraftButton);
		
		middle.add(spacecraftSidePanel);
		
		DefaultListModel objectsListModel = new DefaultListModel();
		for (SpaceObject object : objects) {
			objectsListModel.addElement(object.getName());
		}
		JList objectsList = new JList(objectsListModel);
		
		objectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		objectsList.setLayoutOrientation(JList.VERTICAL);
		objectsList.setVisibleRowCount(-1);
		
		JScrollPane objectsListScroller = new JScrollPane(objectsList);
		objectsListScroller.setPreferredSize(new Dimension(250, 80));
		
		middle.add(objectsListScroller);
		
		JPanel maneuversSidePanel = new JPanel();
		maneuversSidePanel.add(new JLabel("Maneuvers"));
		JButton addManeuverButton = new JButton("Add maneuver");
		/*
		addManeuverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("adding maneuver");
				
				JTextField durationField = new JTextField(5);
				JTextField startTimeField = new JTextField(5);
				
				JPanel maneuverDialogPanel = new JPanel();
				
				maneuverDialogPanel.add(new JLabel("Duration (seconds):"));
				maneuverDialogPanel.add(durationField);
				maneuverDialogPanel.add(new JLabel("Start time (seconds):"));
				maneuverDialogPanel.add(startTimeField);
				
				int result = JOptionPane.showConfirmDialog(null, maneuverDialogPanel, "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
				
			    if (result == JOptionPane.OK_OPTION) {
			    	System.out.println(durationField.getText());
			    }
			}
		});
		*/
		maneuversSidePanel.add(addManeuverButton);
		middle.add(maneuversSidePanel);
		
		DefaultListModel maneuversListModel = new DefaultListModel();
		for (int i=0; i<maneuvers.length; i++) {
			maneuversListModel.addElement("Maneuver " + (i+1));
		}
		JList maneuversList = new JList(maneuversListModel);
		
		maneuversList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		maneuversList.setLayoutOrientation(JList.VERTICAL);
		maneuversList.setVisibleRowCount(-1);
		
		JScrollPane maneuversListScroller = new JScrollPane(maneuversList);
		maneuversListScroller.setPreferredSize(new Dimension(250, 80));
		
		middle.add(maneuversListScroller);
		
		window.add(middle, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel();
		bottom.add(startButton);
		window.add(bottom, BorderLayout.PAGE_END);
		
		window.setVisible(true);
	}
	
	public SpaceObject[] getObjects() {
		return objects;
	}
	
	public Maneuver[] getManeuvers() {
		return maneuvers;
	}
}