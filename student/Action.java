package student;

import java.util.Collections;
import java.util.LinkedList;
import student.ParserImpl.HistObj;
import student.util.Functions;
import static student.util.Functions.forName;

/**
 * Represents an action a critter can take.
 * @author haro
 */
public class Action extends Node<Expression<?>> {
    Act act;
    
    public static Action parse(LinkedList<HistObj> hist) throws SyntaxError {
        HistObj self = hist.pop();
        String type = hist.pop().token;
        if("tag".equals(type)) {
            hist.pop().expect("[");
            Expression ind = Expression.parse(hist);
            hist.pop().expect("]");
            return new Tag(ind);
        } else
            return new Action(type);
    }

    protected Action(String type) {
        super(Collections.EMPTY_LIST);
        this.act = forName(Act.class,type);
    }
    
    protected Action() {
        super(Collections.EMPTY_LIST);
    }
    
    protected Action(Expression...e) {
        super(e);
    }
    
    /**
     * Executes the action, by which I mean it throws an error.
     * This method's signature is expected to change.
     */
    public void execute() {
        act.exec();
    }

    @Override
    public StringBuffer toString(StringBuffer sb) {
        sb.append(Functions.en2s(act));
        return sb;
    }
    
    @Override
    public boolean randomize() {
        act = Functions.randEnum(Act.class);
        return true;
    }

    public static enum Act {
        WAIT {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, FORWARD {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, BACKWARD {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, LEFT {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, RIGHT {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, EAT {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, ATTACK {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, GROW {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, BUD {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        }, MATE {
            @Override public void exec() {
                throw new Error("Can't do that yet!");
            }
        };
        
        public abstract void exec();
    }
}