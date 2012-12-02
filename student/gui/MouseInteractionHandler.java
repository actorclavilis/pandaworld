/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package student.gui;

import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import student.grid.Critter;
import student.grid.HexGrid.Reference;
import student.grid.Tile;
import student.parse.Constant;
import student.parse.Program;
import student.parse.Tag;

/**
 *
 * @author Panda^H^H^H^H^Hhwh48
 */
public class MouseInteractionHandler extends MouseAdapter implements java.awt.event.KeyListener {

    //private final WorldFrame view;
    private InteractionHandler masterController;
    private Reference<Tile> rclxtar = null;
    private JPopupMenu men;
    private String msg;
    private Action rock, unrock,
            plant, unplant;
    private int i;
    private Action crit, critMenIts[] = new Action[12];
    private boolean EXIT = false;

    public MouseInteractionHandler(final InteractionHandler _parent){//final World _model, final WorldFrame _view) {
        masterController = _parent;
        //view = masterController.getView();
        masterController.getView().worldDisplay.gridpane.addMouseListener(this);
        masterController.getView().worldDisplay.gridpane.addKeyListener(this);
        
        masterController.getView().worldDisplay.scrollpane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                masterController.getView().repaint();
            }
        });
        masterController.getView().worldDisplay.scrollpane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                masterController.getView().repaint();
            }
        });       
        masterController.getView().addWindowListener(new ExitHandler());
        
        rock = new LocAxn("put rock") {
            @Override
            public void act() {
                try {
                    rclxtar.setContents(new Tile(true));
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(masterController.getView(), "Could not instantiate rock", "Export error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        unrock = new LocAxn("derock") {
            @Override
            public void act() {
                try {
                    rclxtar.setContents(new Tile(false, 0));
                } catch (RemoteException ex) {
                   JOptionPane.showMessageDialog(masterController.getView(), "Could not instantiate tile", "Export error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        plant = new LocAxn("plant") {
            @Override
            protected void act() {
                rclxtar.mutableContents().putPlant();
            }
        };
        unplant = new LocAxn("weed-x") {
            @Override
            protected void act() {
                rclxtar.mutableContents().removePlant();
            }
        };
        crit = new LocAxn("add critter") {
            @Override
            protected void act() {
                rclxtar.mutableContents().putCritter(new Critter(masterController.getModel(), rclxtar, new Program()));
            }
        };
        critMenIts[0] = new CrLocAxn("forward") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    Critter rclxtarcri = rclxtar.mutableContents().getCritter();
                    rclxtarcri.forward();
                    masterController.getView().display().setCurrentLocation(rclxtarcri.loc());
                }
            }
        };
        critMenIts[1] = new CrLocAxn("backward") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    Critter rclxtarcri = rclxtar.mutableContents().getCritter();
                    rclxtarcri.backward();
                    masterController.getView().display().setCurrentLocation(rclxtarcri.loc());
                }
            }
        };
        critMenIts[2] = new CrLocAxn("left") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().getCritter().left();
                }
            }
        };
        critMenIts[3] = new CrLocAxn("right") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().getCritter().right();
                }
            }
        };
        critMenIts[4] = new CrLocAxn("eat") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().getCritter().eat();
                }
            }
        };
        critMenIts[5] = new CrLocAxn("attack") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().getCritter().attack();
                }
            }
        };
        critMenIts[6] = new CrLocAxn("grow") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().getCritter().grow();
                }
            }
        };
        critMenIts[7] = new CrLocAxn("remove") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().removeCritter();
                }
            }
        };
        critMenIts[8] = new CrLocAxn("bud") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().getCritter().bud();
                }
            }
        };
        critMenIts[9] = new CrLocAxn("mate") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    rclxtar.mutableContents().getCritter().mate();
                }
            }
        };
        critMenIts[10] = new LocAxn("tag") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    men.setVisible(false);
                    do try {
                            rclxtar.mutableContents().getCritter()._tag(i=Integer.parseInt((msg=JOptionPane.showInputDialog(masterController.getView(), "New tag value:", "Tagging ahead critter", JOptionPane.QUESTION_MESSAGE))));
                            rclxtar.mutableContents().getCritter().recentAction = new Tag(new Constant(i));
                            return;
                        } catch (NumberFormatException nfe) { if(!"".equals(msg)) continue; }
                    while(false); //just give up...
                }
            }
        };
        critMenIts[11] = new LocAxn("view program") {
            @Override
            public void act() {
                if (rclxtar.mutableContents().critter()) {
                    men.setVisible(false);
                    JOptionPane.showMessageDialog(masterController.getView(),
                        rclxtar.mutableContents().getCritter().getProgram().toString(),"Critter Program",
                        JOptionPane.PLAIN_MESSAGE);
               }
            }
        };
     }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case 1:
                leftClick(e);
                break;
            case 3:
                rightClick(e);
                break;
        }
    }

    private void leftClick(MouseEvent e) {
        Reference<Tile> at = lookup(e);
        masterController.getView().display().setCurrentLocation(at);
        masterController.getView().repaint();
    }

    private void rightClick(MouseEvent e) {
        if (men != null) {
            men.setVisible(false);
        }
        rclxtar = lookup(e);
        masterController.getView().display().setCurrentLocation(rclxtar);
        if (rclxtar.mutableContents() == null) {
            try {
                rclxtar.setContents(new Tile(false, 0));
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(masterController.getView(), "Could not instantiate tile", "Export error", JOptionPane.ERROR_MESSAGE);
            }
        }
        men = new JPopupMenu();
        men.addKeyListener(this);
        if (rclxtar.mutableContents().rock()) {
            men.add(unrock);
        } else if (rclxtar.mutableContents().critter()) {
            for (Action a : critMenIts) {
                men.add(a);
            }
        } else {
            if (rclxtar.mutableContents().plant()) {
                men.add(unplant);
            } else {
                men.add(plant);
            }
            men.add(rock);
            men.add(crit);
        }
        men.setLocation(e.getLocationOnScreen());
        men.setVisible(true);
        masterController.getView().repaint();//display().update();//repaint();
    }

    private Reference<Tile> lookup(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        x -= masterController.getView().worldDisplay.scrollpane.getHorizontalScrollBar().getValue();
        y -= masterController.getView().worldDisplay.scrollpane.getVerticalScrollBar().getValue();
        int ret[] = masterController.getView().display().grid().hexAt(x, y);
        if(ret == null)
            return null;
        int r = ret[0];
        int c = ret[1];
        return masterController.getModel().at(r, c);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Kev");
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && men != null) {
            System.out.println("ESC");
            men.setVisible(false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { keyPressed(e); }

    @Override
    public void keyReleased(KeyEvent e) { keyPressed(e); }
    private abstract class LocAxn extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            act();
            men.setVisible(false);
            masterController.getView().repaint();
        }

        protected abstract void act();

        public LocAxn(String s) {
            super(s);
        }
    }
    
    private abstract class CrLocAxn extends LocAxn {
        private final student.parse.Action a;
        public CrLocAxn(String s) {
            super(s);
            a = new student.parse.Action(s);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Critter c = rclxtar.mutableContents().getCritter();
            c.recentAction = a;
            super.actionPerformed(e);
            if(c!=null)c.checkDeath();
        }
    }

    private class ExitHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            EXIT = true;
        }
    }
}
