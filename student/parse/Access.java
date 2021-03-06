package student.parse;

import java.util.LinkedList;
import student.grid.CritterState;
import student.parse.ParserImpl.HistObj;
import student.parse.util.Functions;
import static student.parse.util.Functions.forName;

/**
 * Represents an access of some external quantity, such as memory or a sensor.
 */
public class Access extends Expression<Expression<?>> {
    Sen sen;
    
    public static Access parse(LinkedList<HistObj> hist) throws SyntaxError {
        HistObj self = hist.pop();
        Sen type = forName(Sen.class,self.token);
        hist.pop().expect("[");
        Expression ind = Expression.parse(hist);
        hist.pop().expect("]");
        return new Access(type, ind);
    }

    private Access(Sen sen, Expression ind) {
        super(ind);
        this.sen = sen;
    }
    
    /**
     * Randomly changes the type of access being made.
     * @return true
     */
    @Override
    public boolean randomize()
    {
    	sen = Functions.randEnum(Sen.class);
    	return true;
    }

    /**
     * Returns the value of the specified memory/sensor.
     */
    @Override
    public int eval(CritterState s) {
        return sen.val(ind().eval(s),s);
    }
    
    /**
     * Returns the Expression index into the sensor array.
     */
    public Expression ind() {
        return children.get(0);
    }

    @Override
    public StringBuffer toString(StringBuffer sb) {
        sb.append(Functions.en2s(sen));
        sb.append('[');
        ind().toString(sb);
        sb.append(']');
        return sb;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Access a = (Access) super.clone();
        a.children.set(0,(Expression) ind().clone());
        return a;
    }
    
    
    
    public static enum Sen {
        MEM {
            @Override public int val(int par, CritterState s) {
                return s.getMem(par);
            }
        },
        RANDOM {
            @Override public int val(int par, CritterState s) {
                if(par < 2) return 0;
                else return (int)(par*Math.random());
            }            
        },
        AHEAD {
            @Override public int val(int par, CritterState s) {
                return s.ahead(par);
            }            
        },
        NEARBY {
            @Override public int val(int par, CritterState s) {
                return s.nearby(par);
            }            
        };
        
        public abstract int val(int par, CritterState s);
    }
}
