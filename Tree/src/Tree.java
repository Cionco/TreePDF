import java.util.ArrayList;


/**
 * XML is a language that is used to display hierarchic structured data
 * in form of a textfile. That means using trees to translate the structure
 * into Java Objects is quite convenient.
 * The basic structure of these Trees just consists of {@link #head}.
 * As this class is specifically used for input statement XMLs recieved
 * from the German Stock Exchange these trees consist of a few more members
 * specifying the XML better. 
 * 
 * @author Nicolas Kepper
 *
 */
public class Tree {

	Node head = null;
	
	/**
	 * Tells how many Layers there are from Head to Bottom
	 */
	int depth = 0;
	
	public Tree(String name) {
		head = new Node(name, null);
	}
	
	public Tree(Node node) {
		head = node;
	}
	
	/**
	 * Checks if a direct child node of the head
	 * has the name <code>name</code>
	 * 
	 * @param name Name to check for
	 * @return True if any of the heads child nodes
	 * has the name <code>name</code>. If not, returns
	 * false 
	*/ 
	public boolean contains(String name) {
		for(Node n : head.children) {
			if(n.name.equals(name)) return true;
		}
		return false;
	}
	
	/**
	 * Adds an Node as child to the <code>head</code>
	 * node.
	 * 
	 * @param newChild The Node to add to the tree
	 * @return This Tree
	 * 
	 * @see Node#addChild(Node)
	 */
	public Tree addChild(Node newChild) {
		head.addChild(newChild);
		return this;
	}
	
	/**
	 * Adds the head node of another Tree as
	 * child to this tree's head node
	 * 
	 * @param newChild The tree to add to this tree
	 * @return This Tree
	 * 
	 * @see Node#addChild(Tree)
	 */
	public Tree addChild(Tree newChild) {
		head.addChild(newChild);
		return this;
	}

	public Tree setValue(String value) {
		head.setValue(value);
		return this;
	}
	
	public Tree setValue(String value, String type) {
		head.setValue(value, type);
		return this;
	}
	
	/**
	 * Scans the entire tree for a node with the name <code>name</code>
	 * 
	 * @param name Name to search for
	 * @return the {@link Node} if the name is somewhere in the tree
	 * <code>null</code> if <code>name</code> can't be found
	 * 
	 * @see #get(Node, String)
	 */
	public Node get(String name) {
		for(Node n : this.head.children) {
			if(n.name.equals(name)) return n;
			Node r = get(n, name);
			if(r != null) return r;
		}
		return null;
	}
	
	/**
	 * Recursive Part of {@link #get(String)}
	 * Checks the name of each child node <code>n</code> and their 
	 * child nodes against <code>name</code>
	 * get(String) zapps through the children of the head node
	 * while this method checks every other node
	 * 
	 * @param node Node to search through
	 * @param name Name to search for
	 * @return the {@link Node} if the name is somewhere in the tree
	 * <code>null</code> if <code>name</code> can't be found
	 * 
	 * @see #get(String)
	 */
	private Node get(Node node, String name) {
		for(Node n : node.children) {
			if(n.name.equals(name)) return n;
			Node r = get(n, name);
			if(r != null) return r;
		}
		
		return null;
	}
	
	public int getDepth() {
		return depth;
	}
	
	/**
	 * Every tree has to consist of elements.
	 * Nodes are the element for the Tree.
	 * Each node represents one XML-Tag.
	 * Each node knows its children and its parent
	 * so you could rebuild the whole tree as soon
	 * as you know one Node.
	 * 
	 * @author Nicolas
	 *
	 */
	public static class Node{
		/**
		 * The list of child nodes
		 */
		ArrayList<Node> children = new ArrayList<Node>();
		
		/**
		 * Flag that indicates whether the Node has
		 * no child nodes.<br>
		 * <code>true</code> if it has no child nodes
		 * <code>false</code> if so
		 */
		boolean leaf;
		
		/**
		 * If the node is a leaf it can have 
		 * a value.
		 */
		String value = null;
		
		/**
		 * Each node has a name corresponding to
		 * the XML-Tag it represents
		 */
		String name = null;
		
		/**
		 * Reference on the parent node
		 */
		Node parent;
		
		/**
		 * Creates a new Node.<br>
		 * Defaults: <code>leaf = true</code> and
		 * 			<code>type = "varchar(50)"</code>
		 * 
		 * @param name The name of this Node i.e.
		 * the XML tag this node represents
		 * @param parent The parent Node of the new Node 
		 */
		public Node(String name, Node parent) {
			leaf = true;
			this.name = name;
			this.parent = parent;
		}
		
		/**
		 * Adds a new child to the list. Sets <code>leaf</code>
		 * to <code>false</code> and <code>type</code> to 
		 * <code>null</code>
		 * 
		 * @param newChild The node to be added 
		 * to the list
		 */
		public Node addChild(Node newChild) {
			if(leaf) leaf = false;
			children.add(newChild);
			return this;
		}
		
		/**
		 * Adds the head node of <code>newChild</code> as child
		 * to the list. Sets <code>leaf</code>
		 * to <code>false</code> and <code>type</code> to 
		 * <code>null</code>
		 * 
		 * @param newChild Tree of which the head node should be appended to the current tree
		 */
		public Node addChild(Tree newChild) {
			if(leaf) leaf = false;
			children.add(newChild.head);
			newChild.head.setParent(this);
			return this;
		}
		
		/**
		 * Sets the {@link #value} of this node to <code>value</code>
		 * In that process sets <code>leaf</code> to <code>true</code>
		 * 
		 * @param value The value for this node
		 */
		public Node setValue(String value) {
			leaf = true;
			this.value = value;
			return this;
		}
		
		/**
		 * Sets the {@link #value} and the {@link #type} of this node to
		 * <code>value</code> and <code>type</code>,
		 * respectively.
		 * In that process sets <code>leaf</code> to <code>true</code>
		 * 
		 * @param value The value for this node
		 * @param type The type for this node
		 */
		public Node setValue(String value, String type) {
			this.value = value;
			leaf = true;
			return this;
		}
		
		/**
		 * Sets the {@link #parent} member
		 * 
		 * @param parent Parent of this node
		 */
		public Node setParent(Node parent) {
			this.parent = parent;
			return this;
		}
		
		
		/**
		 * Sets the {@link #name} of this Node to <code>name</code>
		 * 
		 * @param name Name for this node
		 */
		public Node setName(String name) {
			this.name = name;
			return this;
		}
		
		/** 
		 * @return The core information of the node in Form of the Node.Field Struct (class). "XML" as type
		 * when the node is not a leaf
		 */
		public Field getField() {
			if(!leaf) return new Field(this.name, this.parent.name);
			else return new Field(this.name, this.parent.name);
		}
		
		/**
		 * Always using Node objects gets confusing and
		 * memory intense fast so using a smaller struct that
		 * only contains the core data and methods.
		 * 
		 * @author Nicolas Kepper
		 *
		 */
		public static class Field {
			/**
			 * @see Node#name
			 */
			public String name;
			
			/**
			 * The name of the parent node
			 * 
			 * @see Node#parent
			 */
			public String parent;
			
			/**
			 * Creates a Field Object with the core Node members
			 * @param name {@link #name}
			 * @param type {@link #type}
			 * @param parent {@link #parent}
			 * @param NULL {@link #NULL}
			 * 
			 * @see Node#getField()
			 * @see Node
			 */
			public Field(String name, String parent) {
				this.name = name;
				this.parent = parent;
			}
		
			/**
			 * @return true only if name, type and parent are the same
			 */
			@Override
			public boolean equals(Object o) {
				Field f = (Field) o;
				return this.name.equals(f.name) && this.parent.equals(f.parent);
			}
		}

	}

	/**
	 * The String representation of an Tree Object is its
	 * tree structure always indented by the right amount so
	 * any human can see the hierarchic dependencies
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.head.name);
		sb.append("\n");
		
		for(Node node : this.head.children) {
			sb.append(toString(node, 1));
			sb.append("\n");
		}
		depth -= 2; //First and last Step don't count towards the depth
		return sb.toString();
	}
	
	/**
	 * Recursively builds the representation string for {@link #toString()}
	 * 
	 * @param node The node to build the String for
	 * @param level The level on which the node lies
	 * @return The String representation of this nodes XML Structure
	 */
	private String toString(Node node, int level) {
		StringBuilder sb = new StringBuilder();
		
		if(level > depth) depth = level;
		
		sb.append(tab(level, node.name));
		
		for(Node n : node.children) {
			sb.append(toString(n, level + 1));
		}
		
		return sb.toString();
	}
	
	/**
	 * Builds the indention for the {@link #toString()} method
	 *  
	 * @param level Tells the method how far the actual string should be indented
	 * @param line The string supposed to be indented
	 * @return The by <code>level</code> indented string <code>line</code>
	 */
	private String tab(int level, String line) {
		String s = "";
		for(int i = 0; i < level; i++) s += "\t";
		s += line + "\n";
		return s;
	}
}