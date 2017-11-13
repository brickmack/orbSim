package rocketSim;

import java.util.ArrayList;
import java.util.Random;
import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class Orbit3dDisplay extends AbstractAnalysis{
	private ArrayList<Point> allPoints = new ArrayList<Point>();
	private double size = 6700000;
	private Point[] scalePoints = new Point[2];
	
	public Orbit3dDisplay() {
	}
		
    public void init(){
        int size = 5000000;
        float x;
        float y;
        float z;
        float a;

        Coord3d[] points = new Coord3d[size];
        Color[] colors = new Color[size];
        
        Random r = new Random();
        r.setSeed(0);
        
        for(int i=0; i<size; i++){
            x = r.nextFloat() - 0.5f;
            y = r.nextFloat() - 0.5f;
            z = r.nextFloat() - 0.5f;
            points[i] = new Coord3d(x, y, z);
            a = 0.25f;
            colors[i] = new Color(x, y, z, a);
        }
        
        Scatter scatter = new Scatter(points, colors);
        chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
        chart.getScene().add(scatter);
        ensureScale();
	}
    
    public double getSize() {
    	return size;
    }
    
    public void ensureScale() {
    	size = size*1.5;
    	//we add fake points to the chart to control the scaling. Its hacky, but it works...
    	//remove the old points first
    	
    	if (scalePoints[0] != null) {
    		chart.getScene().remove(scalePoints[0]);
    		chart.getScene().remove(scalePoints[1]);
    	}
    	
    	//add new
    	scalePoints[0] = new Point(new Coord3d(size, size, size),  Color.WHITE, 1);
    	scalePoints[1] = new Point(new Coord3d(-size, -size, -size),  Color.WHITE, 1);
    	chart.getScene().getGraph().add(scalePoints[0]);
    	chart.getScene().getGraph().add(scalePoints[1]);
    }
    
    public void addPoint(double[] coord, Color color) {
        Point p = new Point(new Coord3d(coord[0], coord[1], coord[2]), color, 2);
        allPoints.add(p);
        for (int i=0; i<3; i++) {
        	if (Math.abs(coord[i]) > size) {
        		System.out.println("resized");
        		ensureScale();
        		break;
        	}
        }
        
        chart.getScene().getGraph().add(p);
    }
}