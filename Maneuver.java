package rocketSim;

public class Maneuver {
	private Spacecraft craft;
	private double[] vector;
	private double startTime;
	private double burnTime;
	private double elapsedTime;
	private double timeStep;
	
	public Maneuver(Spacecraft craft, double[] vector, double startTime, double burnTime, double timeStep) {
		this.craft = craft;
		this.vector = vector;
		this.startTime = startTime;
		this.burnTime = burnTime;
		this.timeStep = timeStep;
	}
	
	public double[] getTimeRange() {
		return new double[] {startTime, startTime+burnTime};
	}
	
	public Spacecraft getCraft() {
		return craft;
	}
	
	public void execute(double time) { //for fixed-vector maneuvers
		elapsedTime = time-startTime;
		craft.executeManeueverFromTime(0, vector, elapsedTime, timeStep);
	}
	
	public void execute(double time, double[] curVector) {
		elapsedTime = time-startTime;
		craft.executeManeueverFromTime(0, curVector, elapsedTime, timeStep);
	}
}