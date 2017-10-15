package faceduck.skeleton.world;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.*;

import faceduck.actors.Gardener;
import faceduck.actors.Gnat;
import faceduck.actors.Grass;
import faceduck.custom.Actionable;
import faceduck.custom.UI.SightMode;
import faceduck.custom.interfaces.*;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Utility;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Location;

/**
 * This class represents an element in the GUI for rabbit world. This panel
 * draws the actual world, and also controls running the rabbit world
 * appropriately.
 */
@SuppressWarnings("serial")
public class WorldPanel extends JPanel implements Runnable {

    // @Custom Improve
    private Consumer<World> stepConsumer;
    private Consumer<Object> trackConsumer;
    private Location prevLoc;
    private Object target;
    private Object track;
    private SightMode mode;
    // END

	private final int IMAGE_SIZE = 40;
	private final World world;

	// Load all the icons
	private final ImageIcon gardenerImage = new ImageIcon(getClass()
			.getResource("icons/gardener.jpg"));
	private final ImageIcon gnatImage = new ImageIcon(getClass().getResource(
			"icons/gnat.jpg"));
	private final ImageIcon grassImage = new ImageIcon(getClass().getResource(
			"icons/grass.png"));
	private final ImageIcon rabbitImage = new ImageIcon(getClass().getResource(
			"icons/rabbit.gif"));
	private final ImageIcon foxImage = new ImageIcon(getClass().getResource(
			"icons/fox.jpg"));
	private final ImageIcon unknownImage = new ImageIcon(getClass()
			.getResource("icons/unknown.png"));
	// @Custom Improve : load image
    private final ImageIcon bearImage = new ImageIcon(getClass().getResource(
            "icons/bear.jpg"));
    private final ImageIcon hunterImage = new ImageIcon(getClass().getResource(
            "icons/hunter.jpg"));
    // END

	private int numSteps;

	public WorldPanel(World w) {
		this.world = w;
		// calculate the preferred width and height of the panel
		int panelWidth = w.getWidth() * IMAGE_SIZE;
		int panelHeight = w.getHeight() * IMAGE_SIZE;
		Dimension preferredSize = new Dimension(panelWidth, panelHeight);
		this.setPreferredSize(preferredSize);
		this.setBackground(Color.WHITE);

		// @Custom Improve : provide {@link Animal}'s sight
        prevLoc = new Location(-1, -1);
		addMouseListener(new MouseAdapter() {
		    @Override
            public void mousePressed(MouseEvent e) {
		        super.mousePressed(e);

                Location loc = new Location(e.getX() / IMAGE_SIZE, e.getY() / IMAGE_SIZE);
                if (!world.isValidLocation(loc)) return;

                target = world.getThing(loc);
                consumeTrack(target);
                mode = (prevLoc.equals(loc)) ? mode.next() : SightMode.VIEWRANGE;

                prevLoc = loc;


                invalidate();
                validate();
                repaint();
            }
        });
		// END
	}

    /** @Custom Improve
     * set stepConsumer for apply function every step.
     *
     * @param consumer
     */
	public void setStepConsumer(Consumer<World> consumer) {
	    stepConsumer = consumer;
    }

    /** @Custom Improve
     * set trackConsumer for apply function every track objects.
     *
     * @param consumer
     */
    public void setTrackConsumer(Consumer<Object> consumer) {
	    trackConsumer = consumer;
    }

    /** @Custom Improve
     * do step consumer to update information to UI panel
     */
    protected void consumeStep() {
        stepConsumer.accept(world);
        consumeTrack(target);
    }

    /** @Custom Improve
     * do track consumer to update information to UI panel
     */
    protected void consumeTrack(Object obj) {
        trackConsumer.accept(obj);
    }

	/**
	 * This method paints the graphics of each object in the world.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// iterate over all objects in the world and redraw each object
		synchronized (WorldImpl.class) {
			for (Object thing : world.getAllObjects()) {
				Location loc = world.getLocation(thing);
				int x = loc.getX() * IMAGE_SIZE;
				int y = loc.getY() * IMAGE_SIZE;

				// determine which icon to draw
				if (thing instanceof Gardener) {
					gardenerImage.paintIcon(this, g, x, y);
				} else if (thing instanceof Grass) {
					grassImage.paintIcon(this, g, x, y);
				} else if (thing instanceof Rabbit) {
					rabbitImage.paintIcon(this, g, x, y);
				} else if (thing instanceof Fox) {
					foxImage.paintIcon(this, g, x, y);
				} else if (thing instanceof Gnat) {
					gnatImage.paintIcon(this, g, x, y);
				} // @Custom Improve
                else if (thing instanceof Bear) {
				    bearImage.paintIcon(this, g, x, y);
                } else if (thing instanceof Hunter) {
				    hunterImage.paintIcon(this, g, x, y);
                } // END
                else {
					unknownImage.paintIcon(this, g, x, y);
				}
			}

            // @Custom Improve : draw sight view
            // Support each VIEW MODE
            // - VIEWRANGE: only show selected animal's sight
            // - WEIGHTS: show memory value of animal
            // - EVALUATED: show evaluated position value of animal (It's make decision)
            switch (Actors.recognize(target)) {
                // support sight animals
                case GNAT:
                case RABBIT:
                case HUNTER:
                case BEAR:
                case FOX: {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    switch (mode) {
                        case VIEWRANGE:
                            g2.setColor(Color.BLACK);

                            for (int i = 0; i < world.getWidth(); ++i) {
                                for (int j = 0; j < world.getHeight(); ++j) {
                                    if (!Utility.isInsideViewRange(((Animal) target).getViewRange(),
											world.getLocation(target),
											new Location(i, j))) {
                                        g2.fillRect(i * IMAGE_SIZE, j * IMAGE_SIZE,
                                                IMAGE_SIZE, IMAGE_SIZE);
                                    }
                                }
                            }
                            break;
                        case WEIGHTS: {
                            if (!(target instanceof  Actionable)) break;
                            Actionable actor = (Actionable) target;
                            actor.forEachMemory((v, loc) -> {
                                if (world.getThing(loc) == target) return;
                                g2.setColor(Utility.gradientColor(v.intValue()));
                                g2.fillRect(loc.getX() * IMAGE_SIZE, loc.getY() * IMAGE_SIZE,
                                        IMAGE_SIZE, IMAGE_SIZE);

                                g2.setColor(Color.WHITE);
                                g2.drawString(Integer.toString(v.intValue()),
                                        loc.getX() * IMAGE_SIZE + IMAGE_SIZE / 4,
                                        loc.getY() * IMAGE_SIZE + IMAGE_SIZE / 2);
                            });
                            break;
                        }
                        case EVALUATED: {
                            if (!(target instanceof  Actionable)) break;
                            Actionable actor = (Actionable) target;
                            actor.forEachMemory((v, loc) -> {
                                if (world.getThing(loc) == target) return;
                                g2.setColor(Utility.gradientColor((int) actor.evaluate(prevLoc, loc)));
                                g2.fillRect(loc.getX() * IMAGE_SIZE, loc.getY() * IMAGE_SIZE,
                                        IMAGE_SIZE, IMAGE_SIZE);

                                g2.setColor(Color.WHITE);
                                g2.drawString(Integer.toString((int) actor.evaluate(prevLoc, loc)),
                                        loc.getX() * IMAGE_SIZE + IMAGE_SIZE / 4,
                                        loc.getY() * IMAGE_SIZE + IMAGE_SIZE / 2);
                            });
                            break;
                        }
                    }
                }
            }
		}
	}

	/**
	 * This method steps the world once and then updates the UI. This method can
	 * be safely called from a separate thread.
	 */
	public boolean step() {
		boolean ret = world.step();
		// @Custom Improve
        consumeStep();

		repaint();
		sleep();
		return ret;
	}

	/**
	 * This method steps the world n times and updates the UI.
	 *
	 * @param num
	 *            The number of steps to run
	 */
	public void step(int num) {
		numSteps = num;
		new Thread(this).start();
	}

	/**
	 * This method will run the world forever.
	 */
	public void stepForever() {
		numSteps = -1;
		new Thread(this).start();
	}

	/**
	 * This method tells the thread how to run.
	 */
	@Override
	public void run() {
		if (numSteps == -1) {
			while (numSteps == -1) {
				if (!step()) {
					break;
				}
			}
		} else {
			for (int i = 0; i < numSteps; i++) {
				step();
			}
		}
	}

	public void stop() {
		// value breaks concurrent loop in run()
		numSteps = 0;
	}

	/**
	 * this method will cause the current thread to sleep for 100 milliseconds
	 */
	private static void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
