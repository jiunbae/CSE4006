package faceduck.skeleton.world;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.*;

import faceduck.custom.util.Actors;
import faceduck.custom.util.Pair;
import faceduck.skeleton.interfaces.World;
import faceduck.custom.UI.LogUI;

/**
 * This class represents the GUI for the virtual world simulation.
 */
@SuppressWarnings("serial")
public class WorldUI extends JPanel {

//	private final int X_DIM = 40;
//	private final int Y_DIM = 20;
	private final int X_DIM = 40;
	private final int Y_DIM = 20;

	private final World w;

	private final WorldPanel worldPanel;
	private final JButton step;
	private final JButton run;

	// @Custom Improve
    protected final LogUI vLog;
    private final HashMap<Actors, Pair<JLabel, JLabel>> labelList;

    private final JLabel genLabel;
    private final JLabel genValue;
    // END

	public WorldUI() {
		// set the layout of the UI
		setLayout(new BorderLayout());

		// setup/add the world
		w = new WorldImpl(X_DIM, Y_DIM);
		worldPanel = new WorldPanel(w);

		// worldPanel.setBorder( BorderFactory.createLineBorder(
		// Color.LIGHT_GRAY ) );

		add(worldPanel, BorderLayout.CENTER);

		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout());

		step = new JButton("Step");
		bottom.add(step, BorderLayout.EAST);

		run = new JButton("Start");
		bottom.add(run, BorderLayout.WEST);

        // @Custom Improve
        vLog = new LogUI(Actors.FOX, Actors.RABBIT, Actors.GRASS, Actors.GNAT);

        labelList = new HashMap<>();
        vLog.forEachActors((Actors actor) -> {
            labelList.put(actor, new Pair<>(new JLabel(", " + actor.toString() + ": "), new JLabel("0")));
        });


        JPanel information = new JPanel();
        information.setLayout(new FlowLayout(FlowLayout.CENTER));

        genLabel = new JLabel("Gen: ");
        genValue = new JLabel("0");

        labelList.forEach((actor, labels) -> {
            information.add(labels.getFirst());
            information.add(labels.getSecond());
        });

        information.add(genValue, FlowLayout.LEFT);
        information.add(genLabel, FlowLayout.LEFT);

        worldPanel.setStepConsumer((World world) -> {
            genValue.setText(Integer.toString(world.getGeneration()));
            labelList.forEach((actor, labels) ->
                labels.getSecond().setText(Integer.toString(world.getCount(actor)))
            );

            vLog.log(world.getCount());
            vLog.invalidate();
            vLog.validate();
            vLog.repaint();
        });

        bottom.add(information, FlowLayout.LEFT);
        // END

        add(bottom, BorderLayout.SOUTH);

        // add functionality to the step button.
		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				worldPanel.step();
			}
		});

		// add functionality to the run button
		run.addActionListener(new ActionListener() {
			boolean toggle = true;

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (toggle) {
					worldPanel.stepForever();
					step.setEnabled(false);
					run.setText("Stop");
				} else {
					worldPanel.stop();
					step.setEnabled(true);
					run.setText("Start");
				}
				toggle = !toggle;
			}
		});

		// world iniitalization
		WorldLoader wl = new WorldLoader(w);

		wl.initializeWorld();

		// ((WorldImpl)w).setMap(aiToName);

		// draw the world
		worldPanel.setVisible(true);
	}

	/**
	 * The simulator main entry point.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("Virtual World For FaceDuck");
				WorldUI gui = new WorldUI();
				f.add(gui);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.pack();
				f.setResizable(false);
				f.setVisible(true);

				// @Custom Improve
                JFrame logFrame = new JFrame("Virtual World Log");
                logFrame.add(gui.vLog);
                logFrame.pack();
                logFrame.setResizable(false);
                logFrame.setVisible(true);
                // END
			}
		});
	}
}
