package com.pf.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pf.manager.AgentManager;
import com.pf.manager.EnvironmentManager;
import com.pf.math.Vector2;
import com.pf.model.Agent;
import com.pf.model.Exit;
import com.pf.model.Obstacle;
import com.pf.model.SpawnPoint;
import com.pf.simulator.Simulator;
import com.pf.simulator.replay.Replay;
import com.pf.simulator.replay.ReplayShape;

public class SimPanel extends JPanel implements MouseListener,
		MouseWheelListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Orientation {
		X, Y
	};

	private Orientation orient;
	private boolean fillObstacles = true;
	private Point2D.Float min;
	private Point2D.Float max;
	private float width;
	private float height;
	private float excess;
	private float scale;
	private double translateX;
	private double translateY;
	private int lastOffsetX;
	private int lastOffsetY;
	private float pixelsPerUnit_X;
	private float pixelsPerUnit_Y;
	private static final Color socialDebug = Color.GREEN;
	private static final Color wallDebug = Color.magenta;
	private static final Color desireDebug = Color.blue;
	private boolean limitsSet;

	BufferedImage recimage = null;

	public boolean drawingAgents = false;

	public boolean isFillObstacles() {
		return fillObstacles;
	}

	public void setFillObstacles(boolean fillObstacles) {
		this.fillObstacles = fillObstacles;
	}

	public SimPanel() {
		super();
		setOpaque(true);
		setDoubleBuffered(true);
		reset();
		setFont(new Font("Monospaced", Font.PLAIN, 14));
		try {
			recimage = ImageIO.read(ClassLoader.getSystemResource("player_rec.png"));
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void reset() {
		limitsSet = false;
		pixelsPerUnit_X = 0;
		pixelsPerUnit_Y = 0;
		scale = 1;

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (!isShowing())
			return;

		Graphics2D g2D = (Graphics2D) g;

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		GuiStates state = Simulator.getGuiFrame().state.getCurrentState();
		if (state == GuiStates.preloaded)
			return;

		if (Simulator.replay != null)
			PlayReplay(g2D);
		else
			PlaySimulation(g2D);
		// if(Simulator.replay.renderReplayFrame)
		// PlayReplay(g2D);

		return;

	}



	private void PlayReplay(Graphics2D g2D) {

		Replay replay;
		try {
			replay = Simulator.getReplay();
		} catch (Exception e) {
			return;
		}

		EnvironmentManager env = Simulator.replay.getEnvManager();
		DrawEnvironment(g2D, env);

		AffineTransform saveAT = g2D.getTransform();
		AffineTransform t = getTransform();
		g2D.transform(t);

		Color defaultColor = Simulator.defaultColor;
		g2D.setColor(defaultColor);
	//	System.err.println("#shapes:"+replay.getAgentShapes().size());
		for (ReplayShape e : replay.getAgentShapes())
		{
			if(e.color.equals(defaultColor))
			{
				g2D.fill(e.shape);
			}else
			{
				g2D.setColor(e.color);
				g2D.fill(e.shape);
				g2D.setColor(defaultColor);
			}
		}
			

		g2D.setTransform(saveAT);

		PaintStripes(g2D);
		DrawAxis(g2D);
//		System.err.println("done rendering");
		Simulator.replayRenderDone();

	}

	private void PlaySimulation(Graphics2D g2D) {
		EnvironmentManager env;
		try {
			env = Simulator.getSimEnvironment().getEnvironment();
		} catch (Exception e) {

			System.err.println("Skipping env drawing, no env Manager");
			return;
		}
		DrawEnvironment(g2D, env);
		if (Simulator.guiFrame.state.getCurrentState() != GuiStates.recording)
			DrawAgents(g2D);

		PaintStripes(g2D);
		DrawAxis(g2D);

	}

	private void DrawAgents(Graphics2D g2D) {

		AgentManager agentMan;
		try {
			agentMan = Simulator.getSimEnvironment().getAgentManager();
		} catch (Exception e2) {
	//		e2.printStackTrace();
			System.err.println("Skipping Drawing of Agents, no Agent Manager yet");

			return;
		}


		AffineTransform saveAT = g2D.getTransform();
		AffineTransform t = getTransform();
		g2D.transform(t);
		
		drawingAgents = true;
		for (Agent a : agentMan.getAgentsForDrawing()) {
			if(a.movementType.equals("social"))
				g2D.setColor(Simulator.socialColor);
			else if(a.movementType.equals("q-learning"))
				g2D.setColor(Simulator.qlearningColor);
			else if(a.movementType.equals("q-interpreter"))
			{
				//HACK!!
				if(a.logger == null)
					g2D.setColor(Simulator.qinterpreterColor);
				else
					g2D.setColor(Simulator.qinterpreterLoggedColor);
			}
			else
				g2D.setColor(Simulator.defaultColor);

			g2D.fill(a.getShape());

			if (agentMan.isAgentDebugged(a.id)) {
				float dash1[] = { 10.0f };
				BasicStroke dashed = new BasicStroke(0.1f,
						BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.2f,
						dash1, 0.0f);
				g2D.setStroke(dashed);

				if (a.movementType.equals("social")) {
					g2D.setColor(desireDebug);
					g2D.draw(getLineToDraw(a.desireForce, a));
					g2D.setColor(wallDebug);
					g2D.draw(getLineToDraw(a.contactForce, a));
					g2D.setColor(socialDebug);
					g2D.draw(getLineToDraw(a.granularForce, a));
				} else if (a.movementType.equals("q-learning")) {
					g2D.setColor(desireDebug);
					g2D.draw(getLineToDraw(a.granularForce, a));
					// g2D.setColor(desireDebug);
					// g2D.draw(getLineToDraw(a.desireForce, a));
					// g2D.setColor(wallDebug);
					// g2D.draw(getLineToDraw(a.contactForce, a));
					//
					float angleOffset = (float) Math.acos(a.desireForce
							.normalize().dot(new Vector2(0, 1)));

					Vector2 firstPoint;
					Vector2 secondPoint = null;

					for (int i = 0; i < 6; i++) {
						firstPoint = new Vector2(
								(float) (a.position.getX() + 10 * Math.cos(30
										* Math.PI / 180 * i - angleOffset)),
								(float) (a.position.getY() + 10 * Math.sin(30
										* Math.PI / 180 * i - angleOffset)));
						secondPoint = new Vector2(
								(float) (a.position.getX() + 10 * Math
										.cos(30 * Math.PI / 180 * (i + 1)
												- angleOffset)),
								(float) (a.position.getY() + 10 * Math
										.sin(30 * Math.PI / 180 * (i + 1)
												- angleOffset)));
						g2D.setColor(socialDebug);
						g2D.draw(new Line2D.Float(new Point2D.Float(a.position
								.getX(), a.position.getY()), new Point2D.Float(
								firstPoint.getX(), firstPoint.getY())));
						g2D.setColor(wallDebug);
						g2D.draw(new Line2D.Float(new Point2D.Float(a.position
								.getX(), a.position.getY()), new Point2D.Float(
								secondPoint.getX(), secondPoint.getY())));
					}

				}

			}

			

		}
		drawingAgents = false;
		g2D.setTransform(saveAT);

	}

	private void InitParams() {
		if (width == 0)
			width = getWidth();
		if (height == 0)
			height = getHeight();

		if (pixelsPerUnit_X == 0)
			pixelsPerUnit_X = (float) (width / (max.getX() - min.getX()));

		if (pixelsPerUnit_Y == 0)
			pixelsPerUnit_Y = (float) (height / max.getY() - min.getY());

		if (pixelsPerUnit_X < pixelsPerUnit_Y)
			orient = Orientation.X;
		else
			orient = Orientation.Y;

		if (orient == Orientation.X) {
			excess = (float) (height - max.getY() * pixelsPerUnit_X);
		} else {
			excess = (float) (width - max.getX() * pixelsPerUnit_Y);
		}

	}

	private void PaintStripes(Graphics2D g2D) {

		g2D.setColor(Color.lightGray);

		if (orient == Orientation.X) {

			Rectangle2D.Float r = new Rectangle2D.Float(0, 0, width, excess / 2);
			g2D.fill(r);
			r = new Rectangle2D.Float(0, height - excess / 2, width, excess / 2);
			g2D.fill(r);

		} else {

			Rectangle2D.Float r = new Rectangle2D.Float(0, 0, excess / 2,
					height);
			g2D.fill(r);
			r = new Rectangle2D.Float(width - excess / 2, 0, excess / 2, height);
			g2D.fill(r);

		}

	}

	private void DrawAxis(Graphics2D g2D) {

		// String[] names =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		// for(String a : names)
		// System.out.println(a);

		g2D.setColor(Color.black);
		AffineTransform t = getTransform();
		try {
			t.invert();
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Point2D.Float ptDst = new Point2D.Float();

		if (orient == Orientation.X) {

			t.transform(new Point2D.Float(0, excess / 2), ptDst);

			int x, y;

			x = (int) ptDst.getX();
			y = (int) ptDst.getY();

			g2D.drawString(Integer.toString(x) + "," + Integer.toString(y), 0,
					excess / 2);

			t.transform(new Point2D.Float(width, excess / 2), ptDst);

			x = (int) ptDst.getX();
			y = (int) ptDst.getY();

			g2D.drawString(Integer.toString(x) + "," + Integer.toString(y),
					width - 50, excess / 2);

			t.transform(new Point2D.Float(0, height - excess / 2), ptDst);

			x = (int) ptDst.getX();
			y = (int) ptDst.getY();
			g2D.drawString(Integer.toString(x) + "," + Integer.toString(y), 0,
					height - excess / 2 + 14);

		} else {
			t.transform(new Point2D.Float(excess / 2, 0), ptDst);

			int x, y;

			x = (int) ptDst.getX();
			y = (int) ptDst.getY();

			g2D.drawString(Integer.toString(x) + "," + Integer.toString(y),
					excess / 2 - 60, 14);

			t.transform(new Point2D.Float(width - excess / 2, 0), ptDst);

			x = (int) ptDst.getX();
			y = (int) ptDst.getY();

			g2D.drawString(Integer.toString(x) + "," + Integer.toString(y),
					width - excess / 2, 14);

			t.transform(new Point2D.Float(excess / 2, height), ptDst);

			x = (int) ptDst.getX();
			y = (int) ptDst.getY();
			g2D.drawString(Integer.toString(x) + "," + Integer.toString(y),
					excess / 2 - 60, height - 10);
		}
	}

	private void DrawEnvironment(Graphics2D g2D, EnvironmentManager env) {

		if (!limitsSet) {
			min = new Point2D.Float(0, 0);
			max = new Point2D.Float(env.getWidth(), env.getHeight());
			limitsSet = true;
		}

		GuiStates state = Simulator.getGuiFrame().state.getCurrentState();
		InitParams();
		if (state == GuiStates.recording) {

			int x = 0;
			int y = 0;

			x = (int) ((width - recimage.getWidth()) / 2.0f);
			y = (int) ((height - recimage.getHeight()) / 2.0f);
			if (state != GuiStates.playing) {
				g2D.drawImage(recimage, x, y, this);
				return;
			}

		}

		AffineTransform saveAT = g2D.getTransform();
		AffineTransform t = getTransform();

		g2D.transform(t);

		int type = AlphaComposite.SRC_OVER;
		float trans = 0.1f;
		AlphaComposite alpha = AlphaComposite.getInstance(type, trans);
		g2D.setComposite(alpha);

		for (Exit e : env.getExits()) {
			g2D.setColor(Color.red);
			if (fillObstacles)
			{
				g2D.fill(e.getShape());
				g2D.setColor(Color.BLACK);

				float x,y;
				x = (float) e.getShape().getBounds().getMinX()+0.5f;
				y = (float) e.getShape().getBounds().getCenterY();		
				alpha = AlphaComposite.getInstance(type, 0.5f);
				g2D.setComposite(alpha);
				Font f = getFont();
				f = f.deriveFont((float) 2.0);
				Font newf = f.deriveFont((float) 2.0);
				g2D.setFont(newf);
				
				g2D.drawString(e.getId(),x,y);
				g2D.setFont(f);
				
				alpha = AlphaComposite.getInstance(type, trans);
				g2D.setComposite(alpha);
			}
			else
				g2D.draw(e.getShape());

		}

		for (SpawnPoint s : env.getSpawnPoints()) {
			g2D.setColor(Color.blue);
			if (fillObstacles)
			{	g2D.fill(s.getShape());
				g2D.setColor(Color.BLACK);

				float x,y;
				x = (float) s.getShape().getBounds().getMinX()+0.5f;
				y = (float) s.getShape().getBounds().getCenterY();		
				alpha = AlphaComposite.getInstance(type, 0.5f);
				g2D.setComposite(alpha);
				Font f = getFont();
				
				Font newf = f.deriveFont((float) 2.0);
				g2D.setFont(newf);
				g2D.drawString(s.getId(),x,y);
				g2D.setFont(f);
				alpha = AlphaComposite.getInstance(type, trans);
				g2D.setComposite(alpha);
				
			}
			else
				g2D.draw(s.getShape());
		}

		alpha = AlphaComposite.getInstance(type, 1f);
		g2D.setComposite(alpha);
		for (Obstacle o : env.getObstacles()) {
			g2D.setColor(Color.black);

			if (fillObstacles)
				g2D.fill(o.getShape());
			else
				g2D.draw(o.getShape());
		}

		g2D.setTransform(saveAT);

	}

	private AffineTransform getTransform() {
		AffineTransform t = new AffineTransform();
		t.scale(scale, scale);
		t.translate(translateX, translateY);
		if (orient == Orientation.X) {
			t.translate(0, excess / 2);
			t.scale(pixelsPerUnit_X, pixelsPerUnit_X);
		} else {
			t.translate(excess / 2, 0);
			t.scale((width - excess) / max.x, pixelsPerUnit_Y);
		}
		return t;
	}

	private Line2D getLineToDraw(Vector2 force, Agent a) {
		Vector2 forceauxi = force.normalize();
		Vector2 df = forceauxi;
		df = df.scale(a.radius);
		Point2D.Double base = new Point2D.Double(a.getShape().getCenterX()
				+ df.getX(), a.getShape().getCenterY() + df.getY());
		df = df.scale(force.mod());
		Line2D.Double l = new Line2D.Double(base, new Point2D.Double(
				base.getX() + df.getX() * 2, base.getY() + df.getY() * 2));
		return l;
	}

	public void mouseClicked(MouseEvent e) {

		AgentManager agentMan;
		try {
			agentMan = Simulator.getSimEnvironment().getAgentManager();
		} catch (Exception e2) {
			return;
		}

		Point2D pt = e.getPoint();

		Point2D p = new Point2D.Float();

		AffineTransform t = getTransform();
		try {
			t.invert();
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		t.transform(pt, p);

		drawingAgents = true;
		for (Agent a : agentMan.getAgentsForDrawing()) {

			if (a.containsPoint(p)) {
				if (e.getButton() == MouseEvent.BUTTON1)
					agentMan.setAgentDebugMode(a.id);

				if (e.getButton() == MouseEvent.BUTTON3) {

					Runnable CreateDebugPanel = new DebugPanelRunnable(a);
					SwingUtilities.invokeLater(CreateDebugPanel);
				}

			}
		}
		drawingAgents = false;

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		// capture starting point
		lastOffsetX = e.getX();
		lastOffsetY = e.getY();

	}

	public void mouseReleased(MouseEvent e) {

	}

	public class DebugPanelRunnable implements Runnable {

		Agent a;

		public DebugPanelRunnable(Agent a) {
			this.a = a;
		}

		@Override
		public void run() {
			Simulator.getGuiFrame().createDebugFrame(a);

		}

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

			// make it a reasonable amount of zoom
			// .1 gives a nice slow transition
			scale += (.1 * -e.getWheelRotation());
			// don't cross negative threshold.
			// also, setting scale to 0 has bad effects
			scale = (float) Math.max(0.00001, scale);
			Simulator.getGuiFrame().UpdateSimPanel();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// new x and y are defined by current mouse location subtracted
		// by previously processed mouse location
		int newX = e.getX() - lastOffsetX;
		int newY = e.getY() - lastOffsetY;

		// increment last offset to last processed by drag event.
		lastOffsetX += newX;
		lastOffsetY += newY;

		// update the canvas locations
		translateX += newX;
		translateY += newY;

		// schedule a repaint.
		Simulator.getGuiFrame().UpdateSimPanel();

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
