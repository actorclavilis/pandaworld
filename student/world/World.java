/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package student.world;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import student.grid.ArrayHexGrid;
import student.config.Constants;
import student.grid.Critter;
import student.grid.HexGrid;
import student.grid.HexGrid.HexDir;
import student.grid.HexGrid.Reference;
import student.grid.Species;
import student.grid.Tile;
import student.remote.world.RWorld;
import student.world.util.HashCodeAccessSet;

/**
 *
 * @author Panda
 */
public class World extends UnicastRemoteObject implements RWorld {

    private HexGrid<Tile> grid;
    private int timesteps = 0;
    private boolean WAIT = true; //if false, random action
    private HashCodeAccessSet<Critter> critters = new HashCodeAccessSet<Critter>();
    
    public World() throws RemoteException {
        this(Constants.MAX_ROW, Constants.MAX_COLUMN);
    }

    public World(int _r, int _c) throws RemoteException {
        grid = new ArrayHexGrid<Tile>(_r, _c);
    }

    @Override
    public String getStatus() {
        return "Timesteps: " + timesteps + "\n" + population();
    }
    
    public void toggleWait() {
        WAIT =! WAIT;
    }

    public void step() {
        for (int i = 0; i < Constants.PLANTS_CREATED_PER_TURN; i++) {
            int r = (int) (grid.nRows() * Math.random());
            int c = (int) (grid.nCols() * Math.random());
            if (!grid.get(c, r).plant() && !grid.get(c, r).rock()) {
                grid.get(c, r).putPlant();
            } else {
                i--;
            }
        }
        int cr = 0;
        for (Reference<Tile> e : grid) {
            if (e.mutableContents() != null && e.mutableContents().critter()) {
                cr++;
            }
        }
        double prob = Constants.PLANT_GROW_PROB / (cr == 0 ? 1 : cr);
        for (Reference<Tile> e : grid) {
            Tile t = e.mutableContents();
            if (t.plant()) {
                for (HexDir d : HexDir.values()) {
                    if (e.adj(d) != null
                            && !e.adj(d).mutableContents().plant()
                            && !e.adj(d).mutableContents().rock()
                            && Math.random() < prob) {
                        e.adj(d).mutableContents().putPlant();
                    }
                }
            }
            if (t.critter()) {
                if (!WAIT) {
                    t.getCritter().randomAct();
                }
                if (t.critter()) {
                    t.getCritter().timeStep();
                }
            }
        }
        timesteps++;
        System.out.println("-----------------" + timesteps);
    }

    @Override
    public int getTimesteps() {
        return timesteps;
    }

    @Override
    public int height() {
        return grid.nRows();
    }

    @Override
    public int width() {
        return grid.nCols();
    }
    
    @Override
    public Reference<Tile> at(int r, int c) {
        return grid.ref(c, r);
    }
    /**
     * Adds the given object to the location with the given row and column
     * Throws InvalidWorldAdditionException if the location is invalid
     * @param what the object to add
     * @param row the given row
     * @param col the given col
     * @throws student.world.World.InvalidWorldAdditionException 
     */
    public void add(Object what, int row, int col) throws InvalidWorldAdditionException, RemoteException {
        HexGrid.Reference<Tile> loc = grid.ref(col, row);
        if (loc != null) { //out of bounds
            add(what, loc);
        } else {
            throw new InvalidWorldAdditionException();
        }
    }
    /**
     * Adds the given object to the given location
     * Throws InvalidWorldAdditionException if the object is invalid
     * Precondition: loc is a valid location, or null
     * @param what the object to add
     * @param loc the location to add it, if null, picks a random location
     * @throws student.world.World.InvalidWorldAdditionException 
     */
    public HexGrid.Reference<Tile> add(Object what, HexGrid.Reference<Tile> loc) throws InvalidWorldAdditionException, RemoteException{
            if (loc==null){
                loc = this.randomLoc();
            }
            if (loc.mutableContents() == null) {
                loc.setContents(new Tile(false, 0));
            }
            if (what instanceof Critter) {
                loc.mutableContents().putCritter((Critter)what);
                critters.add((Critter)what);
            }
            else if (what instanceof String && what.equals("plant")) {
                loc.mutableContents().putPlant();
            }
            else if (what instanceof String && what.equals("rock")){
                loc.setContents(new Tile(true));
            }
            else { //not a valid option to add
                throw new InvalidWorldAdditionException();
            }
            return loc;
    }
    
    @Override
    public Critter critterForID(int id) {
        return critters.forHashCode(id);
    }
    
    /**
     * Retrieves the default reference at 0, 0
     *
     * @return the default reference
     */
    public HexGrid.Reference<Tile> defaultLoc() {
        return grid.ref(0, 0);
    }
    @Override
    public HexGrid.Reference<Tile> randomLoc() {
        return grid.ref((int)(Math.random()*height()), (int)(Math.random()*height()));
    }
    /** 
     * MUST BE USED FOR CHANGES
     * This ensures critters get kept track of properly
     */
    public void gridSet(int c, int r, Tile t) {
        grid.set(c, r, t);
        if(t.critter())
            critters.add(t.getCritter());
    }
    
    public static final int CRIT = 0, PLANT = 1, FOOD = 2, ROCK = 3;
    @Override
    public int[] population() {
        int[] population = new int[4]; //[critters, plants, food, rocks]
        Iterator<Reference<Tile>> it = grid.iterator();
        while (it.hasNext()) {
            Tile t = it.next().mutableContents();
            if (t == null) {
                continue;
            }
            population[0] = population[0] + (t.critter() ? 1 : 0);
            population[1] = population[1] + (t.plant() ? 1 : 0);
            population[2] = population[2] + (t.food() ? 1 : 0);
            population[3] = population[3] + (t.rock() ? 1 : 0);
        }
        return population;
    }
    
    public int smell(Reference<Tile> pos, TilePredicate pred, int maxLev) {
        SortedSet<PQEntry> gray = new TreeSet<PQEntry>();
        Set<PQEntry> black = new HashSet<PQEntry>();
        gray.add(new PQEntry(pos, 0, null, null));
        PQEntry res = null;
     out:while(!gray.isEmpty()) {
            PQEntry v = gray.first();
            gray.remove(v);
            black.add(v);
            System.out.printf("(%d,%d):\n",v.curr.col(),v.curr.row());
    working:for(HexDir d : HexDir.VALUES) {
                Reference<Tile> adj = v.curr.adj(d);
                if(adj == null || adj.mutableContents() == null || adj.mutableContents().rock())
                    continue working;
                PQEntry w = null;
                for(PQEntry p : gray) //find in gray set
                    if(p.curr.equals(adj)) {
                        w = p;
                        break;
                    }
                if(w == null)
                    for(PQEntry p : black) //find in black set
                        if(p.curr.equals(adj)) {
                            w = p;
                            break;
                        }
                System.out.printf("\t(%d,%d): ",adj.col(),adj.row());
                if(w == null) {
                    gray.add(w = new PQEntry(adj, v.distance + 1, v, v.direction == null ? d : v.direction));
                    System.out.println("null");
                } /*else if(w.distance > 10) {
                    System.out.println("done");
                    return 1000000;
                } */else if(w.distance > v.distance + 1) {
                    gray.remove(w);
                    w.distance = v.distance + 1;
                    w.direction = v.direction == null ? d : v.direction;
                    w.previous = v;
                    gray.add(w);
                    System.out.printf("relax: %d(%d,%d)\n", w.distance, v.curr.col(), v.curr.row());
                } else
                    System.out.println("check: "+w.distance);
                if(pred.test(adj)) {
                    res = w;
                    break out;
                }
            }
        }
        if(res == null)
            return 1000000;
        if(res.curr == pos)
            return 0;
        int d;
        System.out.println(res.distance);
        for(d = 1; res.previous.direction != null; d++)
            res = res.previous;
        return 1000 * d + res.direction.ordinal();
    }

    public Tile gridGet(int c, int r) {
        return grid.get(c, r);
    }

    public void reset() {
        for(Critter c : critters)
            c.loc().mutableContents().removeCritter();
        critters.clear();
        //species.clear();
    }
    
    //We don't need to clutter up port space
    @Override
    protected void finalize() throws Throwable {
        try {
            unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            System.err.println("Trouble unexporting World");
        }
        super.finalize();
    }
    
    public static class PQEntry implements Comparable<PQEntry> {
        public int distance;
        public Reference<Tile> curr;
        public PQEntry previous;
        public HexDir direction;
        public PQEntry(Reference<Tile> c, int d, PQEntry p, HexDir dd) { 
            curr = c; distance = d; previous = p; direction = dd;
        }
        @Override
        public int compareTo(PQEntry o) {
            return distance - o.distance;
        }
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 19 * hash + this.distance;
            hash = 19 * hash + (this.curr != null ? this.curr.hashCode() : 0);
            hash = 19 * hash + (this.previous != null ? this.previous.hashCode() : 0);
            return hash;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass()) 
                return false;
            final PQEntry other = (PQEntry) obj;
            if (this.distance != other.distance) 
                return false;
            if (this.curr != other.curr && (this.curr == null || !this.curr.equals(other.curr))) 
                return false;
            if (this.previous != other.previous && (this.previous == null || !this.previous.equals(other.previous)))
                return false;
            return true;
        }
        
    }
    
    public static interface TilePredicate {
        public boolean test(Reference<Tile> t);
        public static final TilePredicate isFood =
                new TilePredicate() {
                    @Override
                    public boolean test(Reference<Tile> t) {
                        return t!=null && t.mutableContents() != null && (t.mutableContents().food() || t.mutableContents().plant());
                    }
                };
    }

    public static class InvalidWorldAdditionException extends Exception {
        public InvalidWorldAdditionException() {
        }
    }
}
