package rocketSim;

import java.util.ArrayList;
import org.jzy3d.colors.Color;

public class SpaceObject {
	private String name;
	private SpaceObject parent = null;
	protected double mass;
	private double radius;
	private double[] velocity;
	private double[] position;
	private Color color;
	private ArrayList<double[]> forcesList = new ArrayList<double[]>();
	private boolean draw;
	
	public SpaceObject(String name, double mass, double radius, double[] velocity, double[] position, Color color, boolean draw) {
		this.name = name;
		this.mass = mass;
		this.radius = radius;
		this.velocity = velocity;
		this.position = position;
		this.color = color;
		this.draw = draw;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public SpaceObject getParent() {
		return parent;
	}
	
	public void setParent(SpaceObject parent) {
		this.parent = parent;
	}
	
	public double getMass() {
		return mass;
	}
	
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setVelocity(double[] velocity) {
		this.velocity = velocity;
	}
	
	public double[] getVelocity() {
		return velocity;
	}
	
	public void setPosition(double[] position) {
		this.position = position;
	}
	
	public double[] getPosition() {
		return position;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setDraw(boolean draw) {
		this.draw = draw;
	}
	
	public boolean getDraw() {
		return draw;
	}
	
	public void addForce(double[] force) {
		forcesList.add(force);
	}
	
	public double[] getForceVector() {
		double[] netForce = {0, 0, 0};
		for (int i=0; i<forcesList.size(); i++) {
			for (int j=0; j<3; j++) {
				netForce[j] = netForce[j] + forcesList.get(i)[j];
			}
		}
		return netForce;
	}
	
	public void applyForce(double timestep) {
		double[] netForce = {0, 0, 0};
		for (int i=0; i<forcesList.size(); i++) {
			for (int j=0; j<3; j++) {
				netForce[j] = netForce[j] + forcesList.get(i)[j];
			}
		}
		
		for (int i=0; i<3; i++) {
			velocity[i] = velocity[i] + (netForce[i]*timestep);
			position[i] = position[i] + (velocity[i]*timestep);
		}
		
		forcesList = new ArrayList<double[]>(); //reset list
	}
}