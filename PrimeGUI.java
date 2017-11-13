package rocketSim;

import java.awt.GridLayout;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrimeGUI {
	private DecimalFormat formatter = new DecimalFormat("#0.00");
	private int[] timeSizes = {60, 60, 24, 365};
	
	public JButton startSimButton = new JButton("Start Simulation");
	private JLabel timeLabel = new JLabel("T=0");
	
	private JFrame window = new JFrame();
	private JPanel orbParams = new JPanel();
	private JPanel middle = new JPanel();
	private int objNum = 3;
	private JLabel[][] orbParamLabels = new JLabel[8][4];
	
	public PrimeGUI() {
		//create the window, set its size and title
		window.setSize(500, 500);
		window.setTitle("GUI");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new GridLayout(1,1));
		//JPanel top = new JPanel();
		
		//top.add(startSimButton);
		//window.add(top);
		
		middle.setLayout(new GridLayout(2,1));
		middle.add(timeLabel);
		
		orbParams.setLayout(new GridLayout(8,4));
		
		orbParamLabels[0][0] = new JLabel("Name");
		orbParamLabels[1][0] = new JLabel("Parent body");
		orbParamLabels[2][0] = new JLabel("Eccentricity");
		orbParamLabels[3][0] = new JLabel("Semi-Major Axis (m)");
		orbParamLabels[4][0] = new JLabel("Periapse (km)");
		orbParamLabels[5][0] = new JLabel("Apoapse (km)");
		orbParamLabels[6][0] = new JLabel("Inclination (degrees)");
		orbParamLabels[7][0] = new JLabel("Orbital Period");
		for (int i=0; i<8; i++) {
			for (int j=1; j<objNum+1; j++) {
				orbParamLabels[i][j] = new JLabel("0");
			}
		}
		
		for (int i=0; i<8; i++) {
			for (int j=0; j<objNum+1; j++) {
				orbParams.add(orbParamLabels[i][j]);
			}
		}
		
		middle.add(orbParams);
		window.add(middle);
		
		window.setVisible(true);
	}
	
	public void set(double time, SpaceObject[] objects) {
		//format and display time
		timeLabel.setText("T="  + formatTime(time));
		
		//set listed orbital parameters in human-readable form
		for (int i=0; i<objects.length; i++) {
			double[] keplerian = OrbitSimImproved.cartesianToKeplerian(objects[i], objects[i].getParent());
			
			orbParamLabels[0][i+1].setText(objects[i].getName());
			orbParamLabels[1][i+1].setText(objects[i].getParent().getName());
			
			if (keplerian[0] < 1) { //check that orbit is elliptical, not an escape trajectory, because some of these values have no meaning in that case
				for (int j=0; j<5; j++) {
					orbParamLabels[j+2][i+1].setText(formatter.format(keplerian[j]));
				}
				orbParamLabels[7][i+1].setText(formatTime(keplerian[5]));
			}
			else {
				orbParamLabels[2][i+1].setText(formatter.format(keplerian[0]));
				orbParamLabels[3][i+1].setText("n/a");
				orbParamLabels[4][i+1].setText(formatter.format(keplerian[2]));
				orbParamLabels[5][i+1].setText("n/a");
				orbParamLabels[6][i+1].setText(formatter.format(keplerian[4]));
				orbParamLabels[7][i+1].setText("n/a");
			}
		}
		
		//update display
		timeLabel.paintImmediately(timeLabel.getVisibleRect());
		orbParams.paintImmediately(orbParams.getVisibleRect());
	}
	
	public String formatTime(double time) { //converts time in seconds to human-readable form in years:days:hours:minutes:seconds
		int[] timeUnits = {(int) time, 0, 0, 0, 0};

		while (timeUnits[0] >= 60) {
			timeUnits[0] = timeUnits[0] - 60;
			timeUnits[1]++;
			for (int i=1; i<4; i++) {
				if (timeUnits[i] == timeSizes[i]) {
					timeUnits[i+1]++;
					timeUnits[i] = 0;
				}
			}
		}
		
		String out = Integer.toString(timeUnits[0]);
		for (int i=1; i<5; i++) {
			if (timeUnits[i] > 9) {
				out = timeUnits[i] + ":" + out;
			}
			else if (timeUnits[i] > 0 || i==0) {
				out = "0" + timeUnits[i] + ":" + out;
			}
		}
		return out;
	}
}