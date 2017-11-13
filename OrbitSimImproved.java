package rocketSim;

import java.util.ArrayList;

public class OrbitSimImproved {
	private static double gravConstant = 6.674*Math.pow(10, -11);
	private double time = 0;
	private int count = 0;
	private int drawEveryNTicks;
	private double simulationTimestep;
	private boolean collisionsEnabled;
	private SpaceObject[] objects;
	private ArrayList<SpaceObject[]> objectCombinations = new ArrayList<SpaceObject[]>();
	private ArrayList<Maneuver> maneuvers = new ArrayList<Maneuver>();
	
	public OrbitSimImproved(int drawEveryNTicks, double simulationTimestep, boolean collisionsEnabled, SpaceObject[] objects) {
		this.drawEveryNTicks = drawEveryNTicks;
		this.simulationTimestep = simulationTimestep;
		this.collisionsEnabled = collisionsEnabled;
		this.objects = objects;
		
		//generate list of all valid 2-object combinations. Enables us to quickly apply forces across all potential interactions. There are only n!/(2(n-2)!) valid interactions, but n^2 possibilities must be tested first, so this results in between a 1/4 and 1/2 time savings per interaction cycle. Since we're doing millions of cycles, the overhead is negligible.
		for (SpaceObject objA : objects) {
			for (SpaceObject objB : objects) {
				boolean valid = true;
				if (objectCombinations.size() >= 0) {
					if (objA == objB) {
						valid = false;
					}
					for (int i=0; i<objectCombinations.size(); i++) {
						if (((objectCombinations.get(i)[0] == objA) && (objectCombinations.get(i)[1] == objB)) || ((objectCombinations.get(i)[0] == objB) && (objectCombinations.get(i)[1] == objA))) {
							valid = false;
						}
					}
				}
				if (valid == true) {
					objectCombinations.add(new SpaceObject[] {objA, objB});
				}
			}
		}	
	}
	
	public void stepForward() {
		//maneuvers
		maneuverTiming();
		
		for (int i=0; i<objectCombinations.size(); i++) {
			double[] objAGravVector = new double[3];
			double[] objBGravVector = new double[3];
				
			double distanceBetweenObjects = distance(objectCombinations.get(i)[0].getPosition(), objectCombinations.get(i)[1].getPosition());
			double fGrav = gravConstant * objectCombinations.get(i)[0].getMass()*objectCombinations.get(i)[1].getMass()/Math.pow(distanceBetweenObjects, 2);
			double objAAcceleration = fGrav/objectCombinations.get(i)[0].getMass();
			double objBAcceleration = fGrav/objectCombinations.get(i)[1].getMass();
				
			for (int j=0; j<3; j++) {
				objAGravVector[j] = -objAAcceleration*objectCombinations.get(i)[0].getPosition()[j]/distanceBetweenObjects;
				objBGravVector[j] = -objBAcceleration*objectCombinations.get(i)[1].getPosition()[j]/distanceBetweenObjects;
			}
			objectCombinations.get(i)[0].addForce(objAGravVector);
			objectCombinations.get(i)[1].addForce(objBGravVector);
		}
		
		//apply forces and output
		for (SpaceObject x : objects) {
			//System.out.println("Applied force on " + x.getName() + ". Net force was " + x.getForce()[0] + ", " + x.getForce()[1] + ", "  + x.getForce()[2]);
			x.applyForce(simulationTimestep);
		}
		time = time + simulationTimestep;
		count++;
		
		if ((collisionsEnabled == true)) {
			for (int i=0; i<objectCombinations.size(); i++) {
				if (distance(objectCombinations.get(i)[0].getPosition(), objectCombinations.get(i)[1].getPosition()) < (objectCombinations.get(i)[0].getRadius() + objectCombinations.get(i)[1].getRadius())) {
					System.out.println("Collision between " + objectCombinations.get(i)[0].getName() + " and " + objectCombinations.get(i)[1].getName());
				}
			}
		}
	}
	
	public boolean isDrawTime() {
		if (count == drawEveryNTicks) {
			count = 0;
			return true;
		}
		
		return false;
	}
	
	public static double distance(double[] a, double[] b) {
		return Math.sqrt( Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) +  Math.pow(a[2] - b[2], 2));
	}
	
	public double getTime() {
		return time;
	}
	
	public static double[] cartesianToKeplerian(SpaceObject satellite, SpaceObject parent) {
		double radius = distance(satellite.getPosition(), parent.getPosition());
		double relVel = distance(satellite.getVelocity(), parent.getVelocity());
		double u = (gravConstant*(satellite.getMass()+parent.getMass()));
		double specificEnergy = (Math.pow(relVel, 2)/2)-(u/radius);
		double SMA = -u/(2*specificEnergy);
		double[] specificAngularMomentum = crossProduct(new double[] {satellite.getPosition()[0]-parent.getPosition()[0], satellite.getPosition()[1]-parent.getPosition()[1], satellite.getPosition()[2]-parent.getPosition()[2]}, new double[] {satellite.getVelocity()[0]-parent.getVelocity()[0], satellite.getVelocity()[1]-parent.getVelocity()[1], satellite.getVelocity()[2]-parent.getVelocity()[2]});
		double h = Math.sqrt(Math.pow(specificAngularMomentum[0], 2) + Math.pow(specificAngularMomentum[1], 2) + Math.pow(specificAngularMomentum[2], 2));
		double eccentricity = Math.sqrt(1 - (Math.pow(h, 2)/(SMA*u)));
		double inclination = Math.acos(specificAngularMomentum[2]/h) * (180/Math.PI);
		double periapse = ((SMA*(1-eccentricity))-parent.getRadius())/1000;
		double apoapse = ((SMA*(1+eccentricity))-parent.getRadius())/1000;
		double period = 2*Math.PI*Math.sqrt((Math.pow(SMA, 3))/u);

		return new double[] {eccentricity, SMA, periapse, apoapse, inclination, period};
	}
	
	public static double twoDeterminant(double[][] matrix) {
		return (matrix[0][0]*matrix[1][1])-(matrix[1][0]*matrix[0][1]);
	}
	
	public static double[] crossProduct(double[] a, double[] b) {
		double[][] matrixA = {new double[] {a[1], b[1]}, new double[] {a[2], b[2]}};
		double[][] matrixB = {new double[] {a[0], b[0]}, new double[] {a[2], b[2]}};
		double[][] matrixC = {new double[] {a[0], b[0]}, new double[] {a[1], b[1]}};
		
		return new double[] {twoDeterminant(matrixA), -twoDeterminant(matrixB), twoDeterminant(matrixC)};
	}
	
	public void addManeuver(Maneuver added) {
		maneuvers.add(added);
	}
	
	public ArrayList<Maneuver> getManeuvers() {
		return maneuvers;
	}
	
	public void maneuverTiming() {
		for (Maneuver m : maneuvers) {
			if (time >= m.getTimeRange()[0] && time <= m.getTimeRange()[1]) {
				m.execute(time, m.getCraft().getVelocity());
			}
			
			if (time == m.getTimeRange()[0]) {
				System.out.println("Burning");
			}
			else if (time == m.getTimeRange()[1]) {
				System.out.println("Burn ended");
				
				//maneuver has passed, remove it from the arraylist
				maneuvers.remove(m);
			}
		}
	}
	
	public double[][] getObjectPositions() {
		double[][] objectPositions = new double[objects.length][3];
		
		for (int i=0; i<objects.length; i++) {
			for (int j=0; j<3; j++) {
				objectPositions[i][j] = objects[i].getPosition()[j];
			}
		}
		
		return objectPositions;
	}
}