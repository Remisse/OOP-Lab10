package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stop);
        panel.add(up);
        panel.add(down);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        stop.addActionListener(l -> {
            agent.stopCounting();
            agent.disableButtons();
        });
        up.addActionListener(l -> agent.setCounterUpwards());
        down.addActionListener(l -> agent.setCounterDownwards());
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean downwards;
        private volatile int counter;

        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display
                                                            .setText(Integer.toString(this.counter)));
                    this.advanceCounter();
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        private void disableButtons() {
            SwingUtilities.invokeLater(() -> {
                ConcurrentGUI.this.stop.setEnabled(false);
                ConcurrentGUI.this.up.setEnabled(false);
                ConcurrentGUI.this.down.setEnabled(false);
            });
        }

        private void advanceCounter() {
            this.counter += this.downwards ? -1 : 1;
        }

        private void setCounterUpwards() {
            this.downwards = false;
        }

        private void setCounterDownwards() {
            this.downwards = true;
        }
    }
}
