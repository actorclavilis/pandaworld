package student;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A node in the abstract syntax tree of a program.
 */
public abstract class Node implements Cloneable {

	protected Node parent;
	/**
	 * The number of nodes in this AST, including the current node. This can be
	 * helpful for implementing mutate() correctly.
	 */
	public abstract int size();

	/**
	 * Return a version of the same AST with one random mutation in it. May have
	 * side effects on the original AST.
	 */
	public abstract Node mutate();

	/**
	 * Appends the program represented by this node prettily to the given
	 * StringBuffer.
	 *
	 * @param sb The StringBuffer to be appended
	 */
	public abstract void prettyPrint(StringBuffer sb);

	/**
	 * Checks if the node has a parent
	 *
	 * @return whether the node has a parent
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Sets the node's parent to the given node
	 *
	 * @param n the given node
	 */
	public void setParent(Node n) {
		parent = n;
	}
	/**
	 * Retrieves the node's parent
	 * @return the node's parent
	 */
	public Node getParent(){
		return parent;
	}
	/**
	 * Creates a copy of the node and its subtree
	 *
	 * @return
	 */
	public Node copy() {
		try {
			return (Node)this.clone();
		} catch (CloneNotSupportedException ex) {
			throw new Error("Yes, we *do* support .clone()");
		}
	}
	public Node[] toArray()
	{
		Node[] arr = (Node[]) buildArray(new LinkedList<Node>()).toArray();
		return arr;
	}
	protected LinkedList<Node> buildArray(LinkedList<Node> list)
	{
		list.add(this);
		return list;
	}
	/*{
		if(numChildren()>0)
		{
			
		}
	}*/
	/**
	 * Sets the current node to the given node(including its subtree)
	 *
	 * @param n
	 */
	public void set(Node n) {
		throw new Error("Unimplemented");
	}
	/**
	 * Retrieves the number of children the node has
	 * @return number of children the node has
	 */
	public abstract int numChildren();
	/**
	 * Swaps two children of the Node, if there are more than two, the two children are randomly selected
	 * Precondition: Node has at least 2 children
	 */
	public abstract void swapChildren();
	/**
	 * Removes the node
	 */
	public abstract boolean remove();
	/**
	 * Delete the given child node
	 * Precondition: n must be a child of the node
	 * @param n the given child node
	 */
	public abstract boolean deleteChild(Node n);
}
