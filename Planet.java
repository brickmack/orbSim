package rocketSim;

import org.jzy3d.colors.Color;

public class Planet extends SpaceObject{
	boolean hasAtmosphere;
	
	public Planet(String name, double mass, double radius, double[] velocity, double[] position, Color color, boolean focused, boolean hasAtmosphere) {
		super(name, mass, radius, velocity, position, color, focused);
		this.hasAtmosphere = hasAtmosphere;
	}
}