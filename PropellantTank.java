package rocketSim;

public class PropellantTank {
	private String name;
	private double load;
	private double boiloff;
	
	public PropellantTank(String name, double load, double boiloff) {
		this.name = name;
		this.load = load;
		this.boiloff = boiloff;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLoad(double load) {
		this.load = load;
	}
	
	public void addLoad(double added) {
		load = load + added;
	}
	
	public void reduceLoad(double removed) {
		load = load - removed;
	}
	
	public double getLoad() {
		return load;
	}
	
	public double getBoiloff() {
		return boiloff;
	}
}