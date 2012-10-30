/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package student.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author Eileen Liu <el544@cornell.edu>
 */
public class ControlPanel extends JPanel{
    public JButton stepButton, runButton, stopButton;
    public JSlider speedSlider;
    public static final int INDEFINITE = 0, FIXED_STEPS = 1, PROMPT_STEPS = 2;

    private static final int MIN_DELAY_MSECS = 10, MAX_DELAY_MSECS = 1000;
    private static final int INITIAL_DELAY = MIN_DELAY_MSECS
            + (MAX_DELAY_MSECS - MIN_DELAY_MSECS) / 2;

    public ControlPanel(){
        //setLayout(new BorderLayout());
        setLayout(new GridLayout(2,3));
        JLabel slow = new JLabel("Slow", JLabel.RIGHT);
        speedSlider = generateSlider();
        JLabel fast = new JLabel("Fast", JLabel.LEFT);
        add(slow);
        add(speedSlider);
        add(fast);
        addButtons();
        //JPanel buttonPane = generateButtonPanel();
        //add(buttonPane, BorderLayout.CENTER);
        //add(speedSlider, BorderLayout.SOUTH);
    }
    
    private JSlider generateSlider()
    {
       JSlider slide = new JSlider(MIN_DELAY_MSECS, MAX_DELAY_MSECS,
                INITIAL_DELAY);
        slide.setInverted(true);
        slide.setPreferredSize(new Dimension(100, slide.getPreferredSize().height));
        slide.setMaximumSize(slide.getPreferredSize()); 
        return slide;
    }
        private void addButtons()
    {
        stepButton = new JButton("Step");
        runButton = new JButton("Run");
        stopButton = new JButton("Stop");
        add(stepButton);
        add(runButton);
        add(stopButton);
    }
    private JPanel generateButtonPanel()
    {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,3));
        stepButton = new JButton("Step");
        runButton = new JButton("Run");
        stopButton = new JButton("Stop");
        buttons.add(stepButton);
        buttons.add(runButton);
        buttons.add(stopButton);
        return buttons;
    }
    
}
