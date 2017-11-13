package rocketSim;

import java.io.FileReader;
import java.util.Scanner;

public class Engine {
	private String name;
	private int ID;
	private boolean continuous; //throttling has continuous or discrete levels
	private double[] thrustSteps; //contains either all throttle levels allowed (for non-throttling or discretely-throttling engines like RS-68), or maximum and minimum thrust (for continously-throttling engines)
	private double transientDuration; //how long it takes to go from 0 to nominal throttle
	private double[] transientThrust; //sets of points, equally spaced across the start/shutdown transient, indicating the thrust and ISP curve
	private double[] transientISP;
	private double ISP; //in seconds
	private String[] propellants;
	private double[] mixRatio;
	
	
	public Engine(String configFile) { //for engines where we read in from a config file
		try {
			Scanner in = new Scanner(new FileReader(configFile));
			String line;
			while (in.hasNextLine()) {
				line = in.nextLine();
				String[] parts = line.split(" ");
				if (parts[0].equals("ENGINE")) {
					name = parts[1];
				}
				else if (parts[0].equals("continuous")) {
					if (parts[1].equals("true")) {
						continuous = true;
					}
					else {
						continuous = false;
					}
				}
				else if (parts[0].equals("thrustSteps")) {
					thrustSteps = new double[parts.length-1];
					for (int i=0; i<parts.length; i++) {
						thrustSteps[i] = Double.parseDouble(parts[i+1]);
					}
				}
				else if (parts[0].equals("ISP")) {
					ISP = Double.parseDouble(parts[1]);
				}
				else if (parts[0].equals("propellant")) {
					propellants = new String[parts.length-1];
					for (int i=0; i<parts.length; i++) {
						propellants[i] = parts[i+1];
					}
				}
				else if (parts[0].equals("mixRatio")) {
					mixRatio = new double[parts.length-1];
					for (int i=0; i<parts.length; i++) {
						mixRatio[i] = Double.parseDouble(parts[i+1]);
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error reading " + configFile + ": " + e.getMessage());
		}
	}
	
	public Engine(String name, int ID, double ISP, double[] thrustSteps, double transientDuration, double[] transientThrust, double[] transientISP, boolean continuous, String[] propellants, double[] mixRatio) {
		this.name = name;
		this.ID = ID;
		this.ISP = ISP;
		this.thrustSteps = thrustSteps;
		this.transientDuration = transientDuration;
		this.transientThrust = transientThrust;
		this.propellants = propellants;
		this.continuous = continuous;
		this.mixRatio = mixRatio;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return ID;
	}
	
	public double getISP() {
		return ISP;
	}
	
	public double getTransientDuration() {
		return transientDuration;
	}
	
	public boolean isContinuous() {
		return continuous;
	}
	
	public String[] getPropellants() {
		return propellants;
	}
	
	public double[] getThrustRange() {
		return thrustSteps;
	}
	
	public double[] fireEngineSimple(double thrustLevel, double timeStep, double startTime) { //simplified version of firing logic, no transients. We'll do that later
		double[] out = new double[propellants.length+1];
		
		out[0] = thrustLevel; //impulse
		double totalMassFlow = (thrustLevel*timeStep)/(9.81*ISP);
		for (int i=0; i<propellants.length; i++) {
			out[i+1] = totalMassFlow*mixRatio[i];
		}
		
		return out;
	}
	
	/*
	public double[] fireEngine(double thrustLevel, double timeStep, double startTime) { //returns impulse in Newton-Seconds, and propellant consumed
		double[] out = new double[propellants.length+1];
		double currentISP = ISP;
		
		if (startTime <= transientDuration) {
			//we're still in the transient.
			for (int i=0; i<transientThrust.length; i++) { //if the current burntime is exactly equal to one of the defined values, just use that
				if ((i*transientDuration/transientThrust.length) == startTime) {
					if (transientThrust[i] < thrustLevel) { //don't want to bother with a higher thrust level than we need
						thrustLevel = transientThrust[i];
					}
					currentISP = transientISP[i];
				}
				else if (transientThrust.length == 1) { //only 1 datapoint given, that simplifies things
					if (transientThrust[0] < thrustLevel) {
						thrustLevel = transientThrust[0];
					}
					currentISP = transientISP[i];
					
				}
				else { //we have to estimate thrust an ISP by linear regression
					double timeSpread = transientDuration/transientThrust.length;
					for (int j=1; j<transientThrust.length; j++) { //find upper and lower points
						if (startTime<(i*timeSpread)) {
							thrustLevel = (transientThrust[j-1] + transientThrust[j])/2;
							break;
						}
					}
				}
			}
		}
		out[0] = thrustLevel*timeStep; //impulse
		double totalMassFlow = (thrustLevel*timeStep)/(9.81*currentISP);
		for (int i=0; i<propellants.length; i++) {
			out[i+1] = totalMassFlow*mixRatio[i];
		}
		
		return out;
	}
	*/
}