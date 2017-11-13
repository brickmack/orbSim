/*
 * Mackenzie Crawford/Brickmack
 * Orbital dynamics simulator
 * 
 * Supports n-body Newtonian physics, non-impulsive orbital maneuvering, 3d spacial view of orbits
 * 
 * To-Do:
 * 1. user-defined spacecraft, maneuvers (including read-in from config files. This is partially implemented but mostly boilerplate)
 * 2. maneuver duration sanity-check
 * 3. additional planetary objects
 * 4. lumpy gravitational fields (mascons)
 * 5. performance/accuracy settings
 * 	a. variable number of largest-influencers to integrate during physics simulation
 * 		I. planetary objects only, or all SpaceObjects
 * 		II. for situations resolving to two-body simulations, revert to patched-conics approximation
 * 	b. simulation timestep and output rate (output takes *much* longer than the actual physics simulation)
 * 	c. planetary objects on/off rails
 * 6. Save-states
 * 7. trajectory optimization (given spacecraft with some performance parameters, a current orbital state vector, and a target orbit, find the most efficient way to reach that target)
 * 8. Alternate visualization modes
 * 9. start/pause
 * 10. multi-part spacecraft and staging
 */

package rocketSim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jzy3d.analysis.AnalysisLauncher;

public class Driver {
	public static void main(String[] args) {
		StartScreen startScreen = new StartScreen();
		startScreen.startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PrimeGUI gui = new PrimeGUI();
					double timeStep = 0.001;
					
					Orbit3dDisplay test = new Orbit3dDisplay();
					AnalysisLauncher.open(test);
					
					SpaceObject[] objects = startScreen.getObjects();
					
					OrbitSimImproved sim = new OrbitSimImproved(100000, timeStep, false, objects);
					
					for (int i=0; i<startScreen.getManeuvers().length; i++) {
						sim.addManeuver(startScreen.getManeuvers()[i]);
					}
					
					while (true) {
						sim.stepForward();
						
						if (sim.isDrawTime() == true) {
							//calculate each object's largest influence and set it in the object
							for (int i=0; i<objects.length; i++) {
								SpaceObject largestInfluence = null;
								double largestInfluenceFGrav = 0;
								for (int j=0; j<objects.length; j++) {
									if (i != j) {
										double distanceBetweenObjects = distance(objects[i].getPosition(), objects[j].getPosition());
										double fGrav = 6.674*Math.pow(10, -11) * objects[i].getMass()*objects[j].getMass()/Math.pow(distanceBetweenObjects, 2);
										if (fGrav > largestInfluenceFGrav) {
											largestInfluenceFGrav = fGrav;
											largestInfluence = objects[j];
										}
									}
								}
								objects[i].setParent(largestInfluence);
							}
							
							//update the text display with the current orbital parameters of each object and time
							gui.set(sim.getTime(), new SpaceObject[] {objects[1], objects[2], objects[3]});
							
							//draw each drawable object in the scatterplot
							double[][] positions = sim.getObjectPositions();
							for (int i=0; i<objects.length; i++) {
								if (objects[i].getDraw() || (Math.abs(objects[i].getPosition()[0])<test.getSize() && Math.abs(objects[i].getPosition()[1])<test.getSize() && Math.abs(objects[i].getPosition()[2])<test.getSize())) {
									test.addPoint(positions[i], objects[i].getColor());
								}
							}
						}
					}
				}
				catch (Exception f) {
					System.out.println("f error " + f.getMessage().toString());
				}
			}
		});
	}
	
	public static double distance(double[] a, double[] b) {
		return Math.sqrt( Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) +  Math.pow(a[2] - b[2], 2));
	}
}	