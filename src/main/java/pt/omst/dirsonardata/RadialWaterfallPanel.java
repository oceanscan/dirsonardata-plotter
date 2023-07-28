package pt.omst.dirsonardata;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RadialWaterfallPanel extends JPanel {
    private static final int LINE_LENGTH = 299; // Number of bins per sonar line

    private final ConcurrentHashMap<Double, byte[]> lines;

    private final double BEAM_ANGLE = Math.toRadians(3.0);


    public RadialWaterfallPanel(int maxLines) {
        lines = new ConcurrentHashMap<>();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(700, 700);
    }

    // API to draw a line with byte intensities at a specified bearing in the radial waterfall
    public void drawLine(byte[] lineData, double bearing) {
        if (lineData.length != LINE_LENGTH) {
            throw new IllegalArgumentException("The input array must have a length of " + LINE_LENGTH + " bytes.");
        }
        lines.put(bearing, lineData);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Point center = new Point(getWidth() / 2, getHeight() / 2);

        // Translate the coordinate system to the center point
        g2d.translate(center.x, center.y);

        for (Map.Entry<Double, byte[]> entry : lines.entrySet()) {
            drawTrapezoids(g2d, entry.getKey(), entry.getValue());
        }
    }


    private void drawTrapezoids(Graphics2D g2d, double bearing, byte[] lineData) {
        g2d.rotate(bearing); // Rotate to the specified bearing
        g2d.setStroke(new BasicStroke(LINE_LENGTH));

        for (int i = 0; i < LINE_LENGTH; i++) {
            int intensity = lineData[i] & 0xFF; // Convert the byte value to an intensity (0-255)
            Color color = new Color(intensity, intensity, intensity); // Grayscale color
            g2d.setColor(color);
            int curX1 = (int) (Math.tan(BEAM_ANGLE/2) * i);
            int curX2 = (int) (Math.tan(BEAM_ANGLE/2) * (i+1));

            g2d.fillPolygon(new int[]{curX1, curX2, -curX2, -curX1}, new int[]{i, i+1, i+1, i}, 4); // Draw the trapezoid
        }

        g2d.rotate(-bearing); // Reset rotation
    }
}