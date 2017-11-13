package rocketSim;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.jzy3d.colors.Color;

public class Spacecraft extends SpaceObject{
	private double dryMass;
	private ArrayList<PropellantTank> propellants = new ArrayList<PropellantTank>();
	private ArrayList<Engine> engines = new ArrayList<Engine>();
	
	public Spacecraft(String name, double mass, double radius, double[] velocity, double[] position, Color color, boolean focused, ArrayList<Engine> engines, ArrayList<PropellantTank> propellants, double dryMass) {
		super(name, mass, radius, velocity, position, color, focused);
		this.dryMass = dryMass;
		//this.mass = mass;
		this.propellants = propellants;
		this.engines = engines;
	}
	
	public void readConfig (String configFile) {
		try {
			Scanner in = new Scanner(new FileReader(configFile));
			String[] lineParts;
			
			while (in.hasNextLine()) {
				lineParts = in.nextLine().split(" ");
				if (lineParts[0].equals("SPACECRAFT")) {
					String name = lineParts[1];
					for (int i=2; i<lineParts.length; i++) {
						name = name + " " + lineParts[i];
					}
				}
				else if (lineParts[0].equals("dryMass")) {
					dryMass = Double.parseDouble(lineParts[1]);
				}
				else if (lineParts[0].equals("radius")) {
					//radius = Double.parseDouble(lineParts[1]);
				}
			}
			
			in.close();
		}
		catch (Exception e) {
			System.out.println("Error reading " + configFile + ": " + e.getMessage());
		}
	}
	
	public void executeManeueverFromTime(int engineID, double[] vector, double timeSinceStart, double timeStep) {
		Engine usedEngine = engines.get(engineID);
		
		//find which engine we need
		for (int i=0; i<engines.size(); i++) {
			if (engines.get(i).getID() == engineID) {
				usedEngine = engines.get(i);
				break;
			}
		}
		
		double[] firedEngineResult = usedEngine.fireEngineSimple(usedEngine.getThrustRange()[0], timeStep, timeSinceStart);
		
		//calculate force vector
		double netAcceleration = firedEngineResult[0]/getMass();
		double[] accelerationVector = new double[3];
		for (int i=0; i<3; i++) {
			accelerationVector[i] = netAcceleration*vector[i]/Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
		}
		
		addForce(accelerationVector);
		
		//calculate propellant consumption
		for (int i=0; i<usedEngine.getPropellants().length; i++) {
			for (int j=0; j<propellants.size(); j++) {
				if (usedEngine.getPropellants()[i].equals(propellants.get(j).getName())) {
					propellants.get(j).reduceLoad(firedEngineResult[i+1]);
					mass = mass - firedEngineResult[i+1];
					break;
				}
			}
		}
	}
}