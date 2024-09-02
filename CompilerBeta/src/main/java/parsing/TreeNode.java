package parsing;

import lexical.Token.WordType;
import parsing.Type.*;
import semantic.SymbTable;

import java.util.Arrays;

public class TreeNode {

	//pdf59ҳ-60ҳ
	private final int MAX_CHILDREN = 3;
	private TreeNode sibling = null; // �ֵܽڵ�ָ��
	private int lineno = 0; // Դ�����к�
	private NodeKind nodekind; // �ڵ�����

	private int idNum = 0; // ��ͬ���͵ı�������

	public String[] name = new String[10];// ��ʶ��������

	public SymbTable[] table = new SymbTable[10];; // ���־����Ӧ�ķ��ű��ַ������������׶�����
	public Attr attr = new Attr(); // ����
	public Kind kind = new Kind(); // ��������
	public TreeNode[] child = new TreeNode[MAX_CHILDREN]; // �ӽڵ�ָ��

	public static class Kind // ��������
	{
		public DecKind dec;// ��������
		public StmtKind stmt;// �������
		public ExpKind exp;// ���ʽ����
	}

	// ����
	public static class Attr { // ��������
		public ArrayAttr arrayattr = new ArrayAttr();
		public ProcAttr procattr = new ProcAttr();
		public ExpAttr expattr = new ExpAttr();
		public String type_name; // �������Ǳ�ʶ��

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
			public int low; // �����½�
			public int up; // �����Ͻ�
			public DecKind childtype; // �����������
		};

		// ��������
		public static class ProcAttr {
			public ParamType paramt; // ���̵Ĳ�������
		};

		// ���ʽ����
		public static class ExpAttr {
			public WordType op; // ���ʽ�Ĳ�����
			public int val; // ���ʽ��ֵ
			public VarKind varkind; // ���������
			public ExpType type; // �������ͼ��
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
