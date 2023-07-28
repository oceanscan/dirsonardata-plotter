package pt.omst.dirsonardata;

import pt.lsts.imc.DirSonarData;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.IMCProtocol;

import javax.swing.*;
import java.awt.*;

public class DirSonarDataPlotter {

    private static final String LOCAL_NAME = "ccu-sonar-plotter";
    private static final int LOCAL_PORT = 6006;
    private static final int SOURCE_ID = 0xEEEE;
    private static final String VEHICLE_NAME = "lauv-veganum";
    private IMCProtocol proto;

    private RadialWaterfallPanel waterfallPanel;

    public DirSonarDataPlotter() {
        proto = new IMCProtocol(LOCAL_NAME, LOCAL_PORT, SOURCE_ID);
        waterfallPanel = new RadialWaterfallPanel(200);
        proto.register(this);
        proto.connect(VEHICLE_NAME);
        System.out.println("Waiting for "+VEHICLE_NAME+" to be available...");
        proto.waitFor(VEHICLE_NAME, 60_000);
        System.out.println("Vehicle is available!");
    }



    @Consume
    public void onDirSonarData(DirSonarData msg) {
        System.out.println("On Dir Sonar Data with bearing: " + Math.toDegrees(msg.getPose().getPsi()));
        waterfallPanel.drawLine(msg.getMeasurement().getData(), msg.getPose().getPsi());
    }

    private static void createAndShowGUI() {
        DirSonarDataPlotter app = new DirSonarDataPlotter();
        //Create and set up the window.
        JFrame frame = new JFrame("DirSonarData Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 800));
        frame.add(app.waterfallPanel, SwingConstants.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
