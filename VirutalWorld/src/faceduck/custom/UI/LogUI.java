package faceduck.custom.UI;

import faceduck.custom.util.Actors;
import faceduck.skeleton.util.Util;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.max;

/**
 * Visual Log for dynamic logging of VirtualWorld
 */
public class LogUI extends JPanel {
    private final int W = 300;
    private final int H = 500;
    private final int BORDER = 16;
    private final int MAX_VALUE = 196;
    private final int X_SIZE = 64;

    private static final Stroke GRAPH_STROKE = new BasicStroke(.2f);

    private final HashMap<Actors, List<Integer>> points;
    private final HashMap<Actors, Color> colors;
    private final HashSet<Actors> loggable;
    private int generation = 0;

    public LogUI(Actors... actors) {
        loggable = new HashSet<>();
        colors = new HashMap<>();
        points = new HashMap<>();

        for (Actors actor : actors) {
            loggable.add(actor);
            colors.put(actor,
                    new Color(Util.getRandom().nextInt(256),
                            Util.getRandom().nextInt(256),
                            Util.getRandom().nextInt(256)));
            points.put(actor, new ArrayList<>());
        }
    }

    public void forEachActors(Consumer<Actors> consumer) {
        loggable.forEach(consumer);
    }

    private void append(Actors actor, int value) {
        List<Integer> list = points.get(actor);
        list.add(value);
    }

    public void log(HashMap<Actors, Integer> counts) {
        forEachActors(actor -> append(actor, counts.get(actor)));
        generation += 1;
    }

    @Override
    public void paintComponent(Graphics g) {
        final int HATCH_COUNT = 16;
        final int HATCH_WIDTH = 8;

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // set scale
        double xScale = ((double) getWidth() - 2 * BORDER) / X_SIZE;
        double yScale = ((double) getHeight() - 2 * BORDER) / (MAX_VALUE);

        // create x and y axes
        g2.drawLine(BORDER, getHeight() - BORDER, BORDER, BORDER);
        g2.drawLine(BORDER, getHeight() - BORDER, getWidth() - BORDER, getHeight() - BORDER);

        // create hatch marks for y axis.
        for (int i = 0; i < HATCH_COUNT; i++) {
            int y = getHeight() - ((getHeight() - BORDER * 2) * (i + 1) / HATCH_COUNT + BORDER);
            g2.drawLine(BORDER, y, HATCH_WIDTH + BORDER, y);
            g2.drawString(Integer.toString((int) (MAX_VALUE - (y - BORDER) / yScale)), 0, y + 5);
        }

        // create hatch marks for x axis
        for (int i = 0; i < X_SIZE; i++) {
            int x = (i + 1) * (getWidth() - BORDER * 2) / X_SIZE + BORDER;
            g2.drawLine(x, getHeight() - BORDER, x, getHeight() - BORDER - HATCH_WIDTH - ((i % 5 == 0) ? (BORDER) : 0));
            if (i % 5 == 0)
                g2.drawString(Integer.toString((int) ((x - BORDER) / xScale) + max(0, generation - X_SIZE)), x - (int) Math.log(x), getHeight() - BORDER + 10);
        }

        Stroke oldStroke = g2.getStroke();
        g2.setStroke(GRAPH_STROKE);

        forEachActors(actor -> {
            List<Integer> list = points.get(actor);
            if (list.isEmpty()) return;

            g2.setColor(colors.get(actor));

            int px = (int)((-max(0, generation - X_SIZE)) * xScale + BORDER);
            int py = list.get(0);
            for (int i = max(0, generation - X_SIZE); i < generation; ++i) {
                int x = (int)((i - max(0, generation - X_SIZE)) * xScale + BORDER);
                int y = (int)((MAX_VALUE - list.get(i)) * yScale + BORDER);
                g2.drawLine(px, py, x, y);
                g2.fillOval(x, y, 3 ,3);
                px = x;
                py = y;
            }

        });
        g2.setStroke(oldStroke);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(W, H);
    }
}
