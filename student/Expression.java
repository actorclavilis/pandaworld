package student;

// A critter program expression that has an integer value.

import java.util.List;

public abstract class Expression<SubNodeType extends Expression<?>> extends Node<SubNodeType> {
	protected int value;
	/**
	 * Creates a new Expression of random value between 0 and 999
	 */
	public Expression() {
		value=(int)(Math.random()*999);
	}
	/**
	 * Creates a new Expression with the given value
	 * @param v the given value
	 */
	public Expression(int v) {
		value = v;
	}
        
        public Expression(List<SubNodeType> subs) {
            super(subs);
        }

	/**
	 * Retrieves the Expression's value
	 * @return the expression's value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * Sets the Expression's value to the given integer
	 * @param v the given integer
	 */
	public void setValue(int v) {
		value = v;
	}	
	@Override
	public int size() {
		return 1;
	}
	@Override
	public String toString(){
		return ""+value;
	}
	@Override
	public boolean remove() {
		throw new Error();
	}
}
