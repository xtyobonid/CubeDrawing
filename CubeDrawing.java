//https://math.stackexchange.com/questions/1741282/3d-calculate-new-location-of-point-after-rotation-around-origin
//https://math.stackexchange.com/questions/3071711/how-would-one-find-the-equation-for-the-normal-line-to-a-3-dimensional-equation
//https://math.stackexchange.com/questions/549421/tangent-plane-and-normal-line
//https://keisan.casio.com/exec/system/1359533867
//https://math.stackexchange.com/questions/231221/great-arc-distance-between-two-points-on-a-unit-sphere
//https://www.cmu.edu/biolphys/deserno/pdf/sphere_equi.pdf
//https://www.desmos.com/calculator/9noidbmszl
//https://www.siggraph.org/education/materials/HyperGraph/modeling/mod_tran/3drota.htm
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;
import javax.swing.*;

public class CubeDrawing extends JPanel implements MouseMotionListener, MouseListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Point[] corners;
	private static final int ADJ = 320;
	private static final double LIGHT_SPHERE_RADIUS = 5;
	private static final double LIGHT_X = 5;
	private static final double LIGHT_Y = 0;
	private static final double LIGHT_Z = 0;
	private static final int[][] faces = {{0,1,3,2},{0,1,5,4},{0,2,6,4},{4,5,7,6},{2,3,7,6},{1,3,7,5}};
	private static final double SCALE = 100;
	private static final double MOUSE_DRAG_CONSTANT = 1000;
	
	private static int lastX;
	private static int lastY;
	private static int xAdj;
	private static int yAdj;
	
	public CubeDrawing() {
		setPreferredSize(new Dimension(640, 640));
        setBackground(Color.white);
		corners = new Point[14];
		
		//Create the corners
		int b = 0;
		for (double y = -1; y <= 1; y += 2) {              //--- --+ -+- -++ +-- +-+ ++- +++
			for (double z = -1; z <= 1; z += 2) {		   // 0   1   2   3   4   5   6   7
				for (double x = -1; x <= 1; x += 2) {
					corners[b++] = new Point(y, z, x);     //-00 0-0 00- +00 0+0 00+
				}										   // 8   9   10  11  12  13
			}
		}
		
		corners[8] =  new Point(-1 * LIGHT_SPHERE_RADIUS, 0, 0);
		corners[9] =  new Point( 0,-1 * LIGHT_SPHERE_RADIUS, 0);
		corners[10] = new Point( 0, 0,-1 * LIGHT_SPHERE_RADIUS);
		corners[11] = new Point( LIGHT_SPHERE_RADIUS, 0, 0);
		corners[12] = new Point( 0, LIGHT_SPHERE_RADIUS, 0);
		corners[13] = new Point( 0, 0, LIGHT_SPHERE_RADIUS);
		
		//for (Point c: corners) {
		//	System.out.println(c);
		//}
		
		for (int i = 0; i < corners.length; i++) {
			corners[i].scale();
		}
		
		//Rotate the cube to the correct position
		for (int i = 0; i < corners.length; i++) {
			//corners[i].rotate(Math.PI / 4, Math.atan(Math.sqrt(2)));
		}
		
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addKeyListener(this);
		
		this.setFocusable(true);
		
		lastX = 0;
		lastY = 0;
		xAdj = 0;
		yAdj = 0;
		
		new Timer(17, (ActionEvent e) -> {
            for (int i = 0; i < corners.length; i++) {
            	//corners[i].rotate(Math.PI / 2000, Math.PI/ 2000);
            }
            repaint();
        }).start();
	}
	
	public void drawWireCube(Graphics2D g) {
		g.translate(ADJ, ADJ);
   		g.drawLine((int)corners[0].x, (int)corners[0].y, (int)corners[1].x, (int)corners[1].y);
   		g.drawLine((int)corners[0].x, (int)corners[0].y, (int)corners[2].x, (int)corners[2].y);
   		g.drawLine((int)corners[1].x, (int)corners[1].y, (int)corners[3].x, (int)corners[3].y);
   		g.drawLine((int)corners[2].x, (int)corners[2].y, (int)corners[3].x, (int)corners[3].y);
   		g.drawLine((int)corners[0].x, (int)corners[0].y, (int)corners[4].x, (int)corners[4].y);
   		g.drawLine((int)corners[1].x, (int)corners[1].y, (int)corners[5].x, (int)corners[5].y);
   		g.drawLine((int)corners[2].x, (int)corners[2].y, (int)corners[6].x, (int)corners[6].y);
   		g.drawLine((int)corners[3].x, (int)corners[3].y, (int)corners[7].x, (int)corners[7].y);
   		g.drawLine((int)corners[4].x, (int)corners[4].y, (int)corners[5].x, (int)corners[5].y);
   		g.drawLine((int)corners[4].x, (int)corners[4].y, (int)corners[6].x, (int)corners[6].y);
   		g.drawLine((int)corners[5].x, (int)corners[5].y, (int)corners[7].x, (int)corners[7].y);
   		g.drawLine((int)corners[6].x, (int)corners[6].y, (int)corners[7].x, (int)corners[7].y);
    }
	
	public void drawSolidCube(Graphics2D g) {
		g.translate(ADJ, ADJ);
		
		double iHat = -1 * LIGHT_X/LIGHT_SPHERE_RADIUS;
		double jHat = -1 * LIGHT_Y/LIGHT_SPHERE_RADIUS; //Light vector
		double kHat = -1 * LIGHT_Z/LIGHT_SPHERE_RADIUS;
		
		//int max = (Math.max(Math.abs(iHat), Math.abs(jHat)) == Math.abs(iHat)) ? 0 : 1;
		//double actMax = (Math.max(Math.abs(iHat), Math.abs(jHat)) == Math.abs(iHat)) ? iHat : jHat;  //Largest light vector component
		//max = (Math.max(Math.abs(actMax), Math.abs(kHat)) == Math.abs(actMax)) ? max : 2;
		//actMax = (Math.max(Math.abs(actMax), Math.abs(kHat)) == Math.abs(actMax)) ? actMax : kHat;
		
		double angL1 = 0;
		if (Math.abs(iHat) != 0.0)
			angL1 = Math.atan(jHat / iHat);
		else
			angL1 = Math.PI/2;
		double angL2 = Math.atan(Math.sqrt(Math.pow(iHat, 2) + Math.pow(jHat, 2))/ kHat);
		
		double maxArcLength = Math.PI;
		
		for (int r = 0; r < faces.length; r++) {	
			double siHat = corners[8 + r].x/SCALE/LIGHT_SPHERE_RADIUS;
			double sjHat = corners[8 + r].y/SCALE/LIGHT_SPHERE_RADIUS; //Sphere Face Vector
			double skHat = corners[8 + r].z/SCALE/LIGHT_SPHERE_RADIUS;
		
			//double max2 = 0;
			//if (Math.abs(siHat) > Math.abs(sjHat))
			//	max2 = siHat;
			//else
			//	max2 = sjHat;
			//if (Math.abs(max2) < Math.abs(skHat))
			//	max2 = skHat;
			
			//double multiplier = actMax / max2;
			
			//if (r == 3)
			//	System.out.println(siHat + " " + sjHat + " " + skHat);
			
			//siHat *= multiplier;
			//sjHat *= multiplier;  //normalizing the two vectors
			//skHat *= multiplier;
			
			double angSF1 = -1 * Math.abs(Math.atan(sjHat / siHat));
			double angSF2 = Math.atan(Math.sqrt(Math.pow(siHat, 2) + Math.pow(sjHat, 2))/ skHat);
			
			//double actArcLength = Math.acos(Math.cos(angL1) * Math.cos(angSF1) + Math.sin(angL1) * Math.sin(angSF1) * Math.cos(angL2 - angSF2));
			double actArcLength = Math.acos(siHat * iHat + sjHat * jHat + skHat * kHat);
			
			//if (r == 3)
				//System.out.println(actArcLength + " " + angL1 + " " + angL2 + " " + angSF2/Math.PI + " " + siHat + " " + iHat + " " + sjHat * jHat + " " + skHat * kHat);
			
			double comp = actArcLength / maxArcLength;
			//System.out.println(angL1 + " " + angL2 + " " + angSF1 + " " + angSF2 + " " + (Math.abs(iHat) != 0.0));
			int col = (int)(comp * 205) + 50;
			col = Math.abs(col);
			
			//if (r == 0)
				//System.out.println(actArcLength + " " + angSF1 + " " + sjHat + " " + siHat);
			
			//if (r == 0)
			//	g.setColor(new Color(255, 0, 0));
			//else
			g.setColor(new Color(col, col, col));
			if (checkFace(r)) {
				int[] xPoints = {(int)corners[faces[r][0]].x + xAdj, (int)corners[faces[r][1]].x + xAdj, (int)corners[faces[r][2]].x + xAdj, (int)corners[faces[r][3]].x + xAdj};
				int[] yPoints = {(int)corners[faces[r][0]].y + yAdj, (int)corners[faces[r][1]].y + yAdj, (int)corners[faces[r][2]].y + yAdj, (int)corners[faces[r][3]].y + yAdj};
				g.fillPolygon(xPoints, yPoints, 4);
			}
		}
	}
 
    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawSolidCube(g);
    }
    
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Cube");
            f.setResizable(false);
            f.add(new CubeDrawing(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
    
    public static boolean checkFace(int face) {
    	double zSum = 0;
    	for (int i = 0; i < faces[face].length; i++) {
    		zSum += corners[faces[face][i]].z;
    	}
    	return (zSum < 0);
    }
	
	private class Point {
		public double x;
		public double y;
		public double z;
		
		public Point(double x2, double y2, double z2) {
			x = x2;
			y = y2;
			z = z2;
		}
		
		public void scale() {
			x *= SCALE;
			y *= SCALE;
			z *= SCALE;
		}
		
		public void rotate(double ang1, double ang2) {
			double sinX = Math.sin(ang1);
	        double cosX = Math.cos(ang1);
	 
	        double sinY = Math.sin(ang2);
	        double cosY = Math.cos(ang2);
	 
	        for (Point c : corners) {
	            double cx = c.x;
	            double cy = c.y;
	            double cz = c.z;
	 
	            c.x = cx * cosX - cz * sinX;
	            c.z = cz * cosX + cx * sinX;
	 
	            cz = c.z;
	 
	            c.y = cy * cosY - cz * sinY;
	            c.z = cz * cosY + cy * sinY;
	        }
	        //for (Point c: corners) {
	        	//System.out.println(c);
	        //}
	        //System.out.println(ang1 + " " + ang2);
		}
		
		public String toString() {
			return x + " " + y + " " + z;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			//MOVE
			int dx = lastX - e.getX();
			int dy = lastY - e.getY();
			
			xAdj -= dx;
			yAdj -= dy;
		} else {
			//ROTATE
			int dx = lastX - e.getX();
			int dy = lastY - e.getY();
			
			//yaxis rotation
			for (int i = 0; i < corners.length; i ++) {
				corners[i].rotate(dx/MOUSE_DRAG_CONSTANT,0);
			}
			
			//xaxis rotation
			for (int i = 0; i < corners.length; i ++) {
				corners[i].rotate(0,dy/MOUSE_DRAG_CONSTANT);
			}
		}
		lastX = e.getX();
		lastY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastX = e.getX();
		lastY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("r");
		if (e.getKeyCode() == KeyEvent.VK_R) {
			xAdj = 0;
			yAdj = 0;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}