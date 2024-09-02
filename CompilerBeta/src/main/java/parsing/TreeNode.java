package parsing;

import lexical.Token.WordType;
import parsing.Type.*;
import semantic.SymbTable;

import java.util.Arrays;

public class TreeNode {

	//pdf59页-60页
	private final int MAX_CHILDREN = 3;
	private TreeNode sibling = null; // 兄弟节点指针
	private int lineno = 0; // 源代码行号
	private NodeKind nodekind; // 节点类型

	private int idNum = 0; // 相同类型的变量个数

	public String[] name = new String[10];// 标识符的名称

	public SymbTable[] table = new SymbTable[10];; // 与标志符对应的符号表地址，在语义分析阶段填入
	public Attr attr = new Attr(); // 属性
	public Kind kind = new Kind(); // 具体类型
	public TreeNode[] child = new TreeNode[MAX_CHILDREN]; // 子节点指针

	public static class Kind // 具体类型
	{
		public DecKind dec;// 声明类型
		public StmtKind stmt;// 语句类型
		public ExpKind exp;// 表达式类型
	}

	// 属性
	public static class Attr { // 数组属性
		public ArrayAttr arrayattr = new ArrayAttr();
		public ProcAttr procattr = new ProcAttr();
		public ExpAttr expattr = new ExpAttr();
		public String type_name; // 类型名是标识符

		@Override
		public String toString() {
			return "Attr{" +
					"arrayattr=" + arrayattr +
					", procattr=" + procattr +
					", expattr=" + expattr +
					", type_name='" + type_name + '\'' +
					'}';
		}

		public static class ArrayAttr {
			public int low; // 数组下界
			public int up; // 数组上界
			public DecKind childtype; // 数组的子类型
		};

		// 过程属性
		public static class ProcAttr {
			public ParamType paramt; // 过程的参数类型
		};

		// 表达式属性
		public static class ExpAttr {
			public WordType op; // 表达式的操作符
			public int val; // 表达式的值
			public VarKind varkind; // 变量的类别
			public ExpType type; // 用于类型检查
		}

	}

	public TreeNode getSibling() {
		return sibling;
	}

	public void setSibling(TreeNode sibling) {
		this.sibling = sibling;
	}

	public TreeNode() {
		for (int i = 0; i < MAX_CHILDREN; i++)
			child[i] = null;
		setIdNum(0);
		for (int i = 0; i < 10; i++) {
			name[i] = "";
		}

	}

	public int getLineno() {
		return lineno;
	}

	public void setLineno(int lineno) {
		this.lineno = lineno;
	}

	public int getIdNum() {
		return idNum;
	}

	public void setIdNum(int idnum) {
		this.idNum = idnum;
	}

	public NodeKind getNodeKind() {
		return nodekind;
	}

	public void setNodeKind(NodeKind nodekind) {
		this.nodekind = nodekind;
	}

	public String printTree2(TreeNode tree,int space){
		StringBuilder output = new StringBuilder();
		if(tree == null){
			return "";
		}
		output.append("    ".repeat(Math.max(0, space)));
		output.append(tree.nodekind);
		for(int i=0;i<3;i++)
			if(tree.child[i]!=null)
				output.append(tree.child[i].printTree2(tree.child[i],space+1));
		if(tree.sibling !=null)
			output.append(tree.sibling.printTree2(tree.sibling, space));
		return output.toString();
	}

	@Override
	public String toString() {
		return "TreeNode{" +
				"MAXCHILDREN=" + MAX_CHILDREN +
				", sibling=" + sibling +
				", lineno=" + lineno +
				", nodekind=" + nodekind +
				", idnum=" + idNum +
				", name=" + Arrays.toString(name) +
				", table=" + Arrays.toString(table) +
				", attr=" + attr +
				", kind=" + kind +
				", child=" + Arrays.toString(child) +
				'}';
	}
}
