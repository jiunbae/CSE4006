package faceduck.skeleton.world;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

import faceduck.custom.Actionable;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Information;
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
    private HashMap<Actors, Pair<JLabel, JLabel>> countList;
    private List<Pair<JLabel, JLabel>> infoList;

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
        vLog = new LogUI(Actors.FOX, Actors.RABBIT, Actors.GRASS, Actors.GNAT, Actors.BEAR, Actors.HUNTER);

        // Create label and panel for show information
        countList = new HashMap<>();
        vLog.forEachActors((Actors actor) ->
            countList.put(actor, new Pair<>(new JLabel(", " + actor.toString() + ": "), new JLabel("0")))
        );

        infoList = new ArrayList<>();
        for (String label : Information.getLabels())
            infoList.add(new Pair<>(new JLabel(label + ": "), new JLabel("0")));

        JPanel counts = new JPanel();
        counts.setLayout(new FlowLayout(FlowLayout.CENTER));

        genLabel = new JLabel("Gen: ");
        genValue = new JLabel("0");

        countList.forEach((actor, labels) -> {
            counts.add(labels.getFirst());
            counts.add(labels.getSecond());
        });

        counts.add(genValue, FlowLayout.LEFT);
        counts.add(genLabel, FlowLayout.LEFT);

        JPanel info = new JPanel();
        info.setLayout(new FlowLayout(FlowLayout.CENTER));

        infoList.forEach(p -> {
            info.add(p.getFirst());
            info.add(p.getSecond());
        });

        // Update visual Log UI
        // send data to ui using {@link LogUI.log}
        worldPanel.setStepConsumer((World world) -> {
            genValue.setText(Integer.toString(world.getGeneration()));
            countList.forEach((actor, labels) ->
                labels.getSecond().setText(Integer.toString(world.getCount(actor)))
            );

            vLog.log(world.getCount());
            vLog.invalidate();
            vLog.validate();
            vLog.repaint();
        });

        // Toggle bottom panel to show counts or information of object
        // If trackable object(Actionable), show object's information and if not, show general counts
        worldPanel.setTrackConsumer((Object object) -> {
            if (object instanceof  Actionable) {
                int[] values = ((Actionable) object).getInformation();
                if (values == null) return;
                for (int i = 0; i < values.length; ++i) {
                    infoList.get(i).getSecond().setText(Integer.toString(values[i]));
                }

                bottom.remove(counts);
                bottom.add(info);
                bottom.invalidate();
                bottom.validate();
                bottom.repaint();
            } else {
                bottom.remove(info);
                bottom.add(counts);
                bottom.invalidate();
                bottom.validate();
                bottom.repaint();
            }
        });

        bottom.add(counts, FlowLayout.LEFT);
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

		// @Custom Improve : load value after init
		worldPanel.consumeStep();
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
