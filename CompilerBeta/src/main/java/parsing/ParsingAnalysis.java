package parsing;

import lexical.Token;
import lexical.Token.WordType;
import parsing.Type.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ParsingAnalysis {

	private final int lineNo = 0; 	// �Ѵ���ָ��
	private int tab = 0; 			// ��ӡ�ո������������λ��﷨��
	private boolean Error;			// �﷨�����Ƿ��д�������message���
	private Token token;			// ��ǰָ���token
	private int line0; 				// ��ǰ������ָ��match
	private int next;				// �߼�ָ�룬ָ��token�б�
	private String temp_name;		// ��ʱ���� PDF 93ҳ����stm���ݹ鴦�����
	private List<Token> tokenList;	// Token�б�
	private List<String> parsingList;//�﷨�����ַ����б�
	private String tempStr;			// ��ʱ�ַ��������ڴ�ӡ�﷨��
	private String errorStr;		// ��ʱ�ַ��������ڴ�ӡ�﷨����
	private TreeNode root;			// �﷨�����ڵ�

	public ParsingAnalysis() {
		next = 0;
		Error = false;
		tokenList = null;
		parsingList = null;
		tempStr = "";
		root = null;
		token = null;
		errorStr="";
	}

	public String getErrorStr() {
		return errorStr;
	}

	public boolean getError() {
		return Error;
	}

	public void setError(boolean error) {
		Error = error;
	}

	public List<String> getParsingList() {
		return parsingList;
	}

	public List<Token> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<Token> tokenList) {
		this.tokenList = tokenList;
	}

	public void getNextToken() {

		if (next < tokenList.size()) {
			token = tokenList.get(next);
			next++;
		} else {
			Error = true;
		}
	}

	//��ӡ����
	public void printTab() {
		for (int i = 0; i < tab-1; i++) {
			tempStr += "    ";
		}
	}


	public void printTree(TreeNode tree) {
		tab++;
		while (tree != null) {
			//System.out.println(tree);
			tempStr = "";
			//����
			printTab();
			switch (tree.getNodeKind()) {
			case ProK:
				tempStr += "ProK" + "    ";
				break;
			case PheadK: {
				tempStr += "PheadK" + "    ";
				tempStr += tree.name[0] + "    ";
			}
				break;
			case DecK: {
				tempStr += "DecK" + "    ";
				if (tree.attr.procattr.paramt == ParamType.varparamType)
					tempStr += "var param:" + "    ";
				if (tree.attr.procattr.paramt == ParamType.valparamType)
					tempStr += "value param:" + "    ";
				switch (tree.kind.dec) {
				case ArrayK: {
					tempStr += "ArrayK" + "    ";
					tempStr += tree.attr.arrayattr.up + "    ";
					tempStr += tree.attr.arrayattr.low + "    ";

					if (tree.attr.arrayattr.childtype == DecKind.CharK)
						tempStr += "Chark" + "    ";
					else if (tree.attr.arrayattr.childtype == DecKind.IntegerK)
						tempStr += "IntegerK" + "    ";
				}
					break;
				case CharK:
					tempStr += "CharK" + "    ";
					break;
				case IntegerK:
					tempStr += "IntegerK" + "    ";
					break;
				case RecordK:
					tempStr += "RecordK" + "    ";
					break;
				case IdK:
					tempStr += "IdK" + "    ";
					tempStr += tree.attr.type_name + "    ";

					break;
				default:
					tempStr += "error1!";
					errorStr +="error1!";
					Error = true;
				}
				if (tree.getIdNum() != 0)
					for (int i = 0; i <= (tree.getIdNum()); i++) {
						tempStr += tree.name[i] + "    ";
					}
				else {
					tempStr += "wrong!no var!\n";
					errorStr += "wrong!no var!\n";
					Error = true;
				}
			}
				break;
			case TypeK:
				tempStr += "TypeK" + "    ";
				break;

			case VarK:
				tempStr += "VarK" + "    ";
				if (tree.table[0] != null)
					tempStr += "VarK" + tree.table[0].attrIR.more.varattr.off
							+ "   " + tree.table[0].attrIR.more.varattr.level;

				break;

			case ProcDecK:
				tempStr += "ProcDecK" + "    ";
				tempStr += tree.name[0] + "    ";
				if (tree.table[0] != null)
					tempStr += tree.name[0]
							+ tree.table[0].attrIR.more.procattr.size + "    "
							+ tree.table[0].attrIR.more.procattr.code + "    "
							+ tree.table[0].attrIR.more.procattr.level;
				break;
			case StmLK:
				tempStr += "StmLk" + "    ";
				break;

			case StmtK: {
				tempStr += "StmtK" + "    ";
				switch (tree.kind.stmt) {
				case IfK:
					tempStr += "If" + "    ";
					break;
				case WhileK:
					tempStr += "While" + "    ";
					break;

				case AssignK:
					tempStr += "Assign" + "    ";
					break;

				case ReadK:
					tempStr += "Read" + "    ";
					tempStr += tree.name[0] + "    ";
					if (tree.table[0] != null)

						tempStr += tree.table[0].attrIR.more.varattr.off
								+ "     "
								+ tree.table[0].attrIR.more.varattr.level;
					break;
				case WriteK:
					tempStr += "Write" + "    ";
					break;

				case CallK:
					tempStr += "Call" + "    ";
					tempStr += tree.name[0] + "    ";
					break;

				case ReturnK:
					tempStr += "Return" + "    ";
					break;

				default:
					tempStr += "error2!" + "    ";
					errorStr += "error2!" + "    ";
					Error = true;
				}
			}
				break;
			case ExpK: {
				tempStr += "ExpK" + "    ";

				switch (tree.kind.exp) {
				case OpK: {
					tempStr += "Op" + "    ";

					switch (tree.attr.expattr.op) {
					case EQ:
						tempStr += "=" + "    ";
						break;
					case LT:
						tempStr += "<" + "    ";
						break;
					case PLUS:
						tempStr += "+" + "    ";
						break;
					case MINUS:
						tempStr += "-" + "    ";
						break;
					case TIMES:
						tempStr += "*" + "    ";
						break;
					case OVER:
						tempStr += "/" + "    ";
						break;
					default:
						tempStr += "error3!";
						errorStr += "error3!";
						Error = true;

					}

					if (tree.attr.expattr.varkind == VarKind.ArrayMembV) {
						tempStr += "ArrayMember" + "    ";
						tempStr += tree.name[0] + "    ";
					}
				}

					break;
				case ConstK:
					tempStr += "Const" + "    ";
					switch (tree.attr.expattr.varkind) {
					case IdV:
						tempStr += "Id" + "    ";
						tempStr += tree.name[0] + "   ";
						break;
					case FieldMembV:
						tempStr += "FieldMember" + "    ";
						tempStr += tree.name[0] + "   ";
						break;
					case ArrayMembV:
						tempStr += "ArrayMember" + "    ";
						tempStr += tree.name[0] + "   ";
						break;
					default:
						tempStr += "var type error!";
						errorStr += "var type error!";

						Error = true;
					}
					tempStr += tree.attr.expattr.val + "   ";
					break;
				case VariK:
					tempStr += "Vari" + "    ";
					switch (tree.attr.expattr.varkind) {
					case IdV:
						tempStr += "Id" + "    ";
						tempStr += tree.name[0] + "    ";
						break;
					case FieldMembV:
						tempStr += "FieldMember" + "    ";
						tempStr += tree.name[0] + "    ";
						break;
					case ArrayMembV:
						tempStr += "ArrayMember" + "    ";
						tempStr += tree.name[0] + "    ";
						break;
					default:
						tempStr += "var type error!";
						errorStr += "var type error!";
						Error = true;
					}
					if (tree.table[0] != null)
						tempStr += tree.table[0].attrIR.more.varattr.off
								+ "   "
								+ tree.table[0].attrIR.more.varattr.level;
					break;
				default:
					tempStr += "error4!";
					errorStr += "error4!";
					Error = true;
				}
			}
				break;
			default:
				tempStr += "error5!";
				errorStr += "error5!";
				Error = true;
			}

			parsingList.add(tempStr);

			// ���﷨�����tree�ĸ��ӽ��ݹ����printTree����
			// ����д���б��ļ�listing
			for (int i = 0; i < 3; i++)
				printTree(tree.child[i]);
			tree = tree.getSibling();
		}
		tab--;
	}

	// �� �� �﷨��������
	// ˵ �� ����������messageָ���Ĵ�����Ϣ��ʽ��д��errorStr
	public void wrongMessage(String message,int line) {
		errorStr += "\n" + line + " :" + message;
		//System.out.println(errorStr);
		Error = true;
	}

	// �ռ���ƥ�䴦����
	// ��������expected�����������ʷ����뵱ǰ���ʷ���token��ƥ��
	// �����ƥ��,�򱨷����������﷨����
	public void match(WordType expected) {
		if (token.type == expected) {
			getNextToken();
			line0 = token.line;
		} else {
			wrongMessage("not match error ",token.line);
			getNextToken();
		}
	}

	// ������ program
	// �� �� �ܳ���Ĵ�����
	// ����ʽ < program > ::= programHead declarePart programBody .
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	// �﷨���ĸ��ڵ�ĵ�һ���ӽڵ�ָ�����ͷ����programHead,
	// DeclaraPartΪprogramHead���ֵܽڵ�,�����岿��programBody
	// ΪdeclarePart���ֵܽڵ�.
	public TreeNode program() {
		TreeNode t = programHead();
		TreeNode q = declarePart();
		TreeNode s = programBody();

		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.ProK);

		t1.setLineno(lineNo);// ######
		root = t1;
		if (root != null) {
			root.setLineno(0);
			if (t != null)
				root.child[0] = t;
			else
				wrongMessage("a program head is expected!", root.getLineno());
			if (q != null)
				root.child[1] = q;
			if (s != null)
				root.child[2] = s;
			else
				wrongMessage("a program body is expected!",root.getLineno());
		}
		match(WordType.DOT);

		return root;
	}

	// �� �� ����ͷ�Ĵ�����
	// ����ʽ < programHead > ::= PROGRAM ProgramName
	public TreeNode programHead() {
		// �� �� ��������ͷ�����﷨���ڵ㺯��
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.PheadK);
		// ��ͬ���͵ı�������
		t1.setIdNum(0);
		t1.setLineno(lineNo);// ######
		TreeNode t = t1;
		match(WordType.PROGRAM);

		if ((t != null) && (token.type == WordType.ID)) {
			t.setLineno(0);
			t.name[0] = token.context;
		}
		match(WordType.ID);
		return t;
	}

	// �� �� �������ֵĴ�����
	// ����ʽ < declarePart > ::= typeDec varDec procDec
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode declarePart() {
		// ����
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.TypeK);
		t1.setLineno(lineNo);
		TreeNode typeP = t1;
		TreeNode pp = typeP;

		if (typeP != null) {
			typeP.setLineno(0);
			TreeNode tp1 = null;
			tp1 = typeDec();
			if (tp1 == null)
				typeP = null;
			else
				typeP.child[0] = tp1;
		}

		// ����
		TreeNode t = new TreeNode();
		t.setNodeKind(NodeKind.VarK);
		t.setLineno(lineNo);
		TreeNode varP = t;

		if (varP != null) {
			varP.setLineno(0);
			TreeNode tp2 = varDec();
			if (tp2 != null)
				varP.child[0] = tp2;
			else
				varP = null;
		}
		// ����
		TreeNode s = procDec();
		if (s == null) {
		}
		if (varP == null) {
			varP = s;
		}
		if (typeP == null) {
			pp = typeP = varP;
		}
		if (typeP != varP) {
			typeP.setSibling(varP);
			typeP = varP;
		}
		if (varP != s) {
			varP.setSibling(s);
			varP = s;
		}
		return pp;
	}
	// �� �� �����������ֵĴ�����
	// ����ʽ < typeDec > ::= �� | TypeDeclaration
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode typeDec() {
		TreeNode t = null;
		switch (token.type) {
		case TYPE:
			t = typeDeclaration();
			break;
		case VAR:
		case PROCEDURE:
		case BEGIN:
			break;
		default:
			getNextToken();
			System.out.println("ffffffffffff");

			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}


	// ������ TypeDeclaration
	// �� �� �����������ֵĴ�����
	// ����ʽ < TypeDeclaration > ::= TYPE TypeDecList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode typeDeclaration() {
		match(WordType.TYPE);
		TreeNode t = typeDecList();
		if (t == null) {
			wrongMessage("a type declaration is expected!",token.line);
		}
		return t;
	}


	// ������ TypeDecList
	// �� �� �����������ֵĴ�����
	// ����ʽ < TypeDecList > ::= typeId = typeName ; typeDecMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode typeDecList() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.DecK);
		t1.setLineno(lineNo);
		TreeNode t = t1;
		if (t != null) {
			t.setLineno(line0);
			typeId(t);
			match(WordType.EQ);
			typeName(t);
			match(WordType.SEMI);
			TreeNode p = typeDecMore();
			if (p != null)
				t.setSibling(p);
		}
		return t;
	}



	// ������ typeDecMore
	// �� �� �����������ֵĴ�����
	// ����ʽ < typeDecMore > ::= �� | TypeDecList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode typeDecMore() {
		TreeNode t = null;
		switch (token.type) {
		case VAR:
		case PROCEDURE:
		case BEGIN:
			break;
		case ID:
			t = typeDecList();
			break;
		default:
			getNextToken();
			System.out.println("ffffffffffff");
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ typeId
	// �� �� �����������ֵĴ�����
	// ����ʽ < typeId > ::= id
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void typeId(TreeNode t) {
		int tnum = (t.getIdNum());
		if ((token.type == WordType.ID) && (t != null)) {
			t.name[tnum] = token.context;
			tnum = tnum + 1;
		}
		t.setIdNum(tnum);
		match(WordType.ID);
	}



	// ������ typeName
	// �� �� �����������ֵĴ�����
	// ����ʽ < typeName > ::= baseType | structureType | id
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void typeName(TreeNode t) {
		if (t != null)
			switch (token.type) {
			case INTEGER:
			case CHAR:
				baseType(t);
				break;
			case ARRAY:
			case RECORD:
				structureType(t);
				break;
			case ID:
				t.kind.dec = DecKind.IdK;
				t.attr.type_name = token.context;
				match(WordType.ID);
				break;
			default:
				getNextToken();// //!!!!
				wrongMessage("unexpected token is here!",token.line);
				break;
			}
	}



	// ������ baseType
	// �� �� �����������ֵĴ�����
	// ����ʽ < baseType > ::= INTEGER | CHAR
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void baseType(TreeNode t) {
		switch (token.type) {
		case INTEGER:
			match(WordType.INTEGER);
			t.kind.dec = DecKind.IntegerK;
			break;

		case CHAR:
			match(WordType.CHAR);
			t.kind.dec = DecKind.CharK;
			break;

		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}



	// ������ structureType
	// �� �� �����������ֵĴ�����
	// ����ʽ < structureType > ::= arrayType | recType
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void structureType(TreeNode t) {
		switch (token.type) {
		case ARRAY:
			arrayType(t);
			break;
		case RECORD:
			t.kind.dec = DecKind.RecordK;
			recType(t);
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}



	// ������ arrayType
	// �� �� �����������ֵĴ�����
	// ����ʽ < arrayType > ::= ARRAY [low..top] OF baseType
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void arrayType(TreeNode t) {
		match(WordType.ARRAY);
		match(WordType.LMIDPAREN);
		if (token.type == WordType.INTC)
			t.attr.arrayattr.low = Integer.parseInt(token.context);

		match(WordType.INTC);
		match(WordType.UNDERANGE);
		if (token.type == WordType.INTC) {
			t.attr.arrayattr.up = Integer.parseInt(token.context);
		}
		match(WordType.INTC);
		match(WordType.RMIDPAREN);
		match(WordType.OF);
		baseType(t);
		t.attr.arrayattr.childtype = t.kind.dec;
		t.kind.dec = DecKind.ArrayK;
	}



	// ������ recType
	// �� �� �����������ֵĴ�����
	// ����ʽ < recType > ::= RECORD fieldDecList END
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void recType(TreeNode t) {
		TreeNode p = null;
		match(WordType.RECORD);
		p = fieldDecList();
		if (p != null)
			t.child[0] = p;
		else
			wrongMessage("a record body is requested!",token.line);
		match(WordType.END);
	}



	// ������ fieldDecList
	// �� �� �����������ֵĴ�����
	// ����ʽ < fieldDecList > ::= baseType idList ; fieldDecMore
	// | arrayType idList; fieldDecMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode fieldDecList() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.DecK);
		t1.setLineno(lineNo);
		TreeNode t = t1;
		TreeNode p = null;
		if (t != null) {
			t.setLineno(line0);
			switch (token.type) {
			case INTEGER:
			case CHAR:
				baseType(t);
				idList(t);
				match(WordType.SEMI);
				p = fieldDecMore();
				break;
			case ARRAY:
				arrayType(t);
				idList(t);
				match(WordType.SEMI);
				p = fieldDecMore();
				break;
			default:
				getNextToken();
				wrongMessage("unexpected token is here!",token.line);
				break;
			}
			t.setSibling(p);
		}
		return t;
	}



	// ������ fieldDecMore
	// �� �� �����������ֵĴ�����
	// ����ʽ < fieldDecMore > ::= �� | fieldDecList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode fieldDecMore() {
		TreeNode t = null;
		switch (token.type) {
		case END:
			break;
		case INTEGER:
		case CHAR:
		case ARRAY:
			t = fieldDecList();
			break;
		default:
			getNextToken();// /!!!!!!!!!
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ idList
	// �� �� �����������ֵĴ�����
	// ����ʽ < idList > ::= id idMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public void idList(TreeNode t) {
		if (token.type == WordType.ID) {
			t.name[(t.getIdNum())] = token.context;
			match(WordType.ID);
			t.setIdNum(t.getIdNum() + 1);
		}
		idMore(t);
	}



	// ������ idMore
	// �� �� �����������ֵĴ�����
	// ����ʽ < idMore > ::= �� | , idList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public void idMore(TreeNode t) {
		switch (token.type) {
		case SEMI:
			break;
		case COMMA:
			match(WordType.COMMA);
			idList(t);
			break;
		default:
			getNextToken();// //!!!!!!!!!1
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}

	//************************* ������������ ****************************



	// ������ varDec
	// �� �� �����������ֵĴ�����
	// ����ʽ < varDec > ::= �� | varDeclaration
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public TreeNode varDec() {
		TreeNode t = null;
		switch (token.type) {
		case PROCEDURE:
		case BEGIN:
			break;
		case VAR:
			t = varDeclaration();
			break;
		default:
			getNextToken();// !!!!!!!1
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ varDeclaration
	// �� �� �����������ֵĴ�����
	// ����ʽ < varDeclaration > ::= VAR varDecList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public TreeNode varDeclaration() {
		match(WordType.VAR);
		TreeNode t = varDecList();
		if (t == null)
			wrongMessage("a var declaration is expected!",token.line);
		return t;
	}



	// ������ varDecList
	// �� �� �����������ֵĴ�����
	// ����ʽ < varDecList > ::= typeName varIdList; varDecMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public TreeNode varDecList() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.DecK);
		t1.setLineno(lineNo);
		TreeNode t = t1;
		TreeNode p = null;

		if (t != null) {
			t.setLineno(line0);
			typeName(t);
			varIdList(t);
			match(WordType.SEMI);
			p = varDecMore();
			t.setSibling(p);
		}
		return t;
	}



	// ������ varDecMore
	// �� �� �����������ֵĴ�����
	// ����ʽ < varDecMore > ::= �� | varDecList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public TreeNode varDecMore() {
		TreeNode t = null;
		switch (token.type) {
		case PROCEDURE:
		case BEGIN:
			break;
		case INTEGER:
		case CHAR:
		case ARRAY:
		case RECORD:
		case ID:
			t = varDecList();
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ varIdList
	// �� �� �����������ֵĴ�����
	// ����ʽ < varIdList > ::= id varIdMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public void varIdList(TreeNode t) {
		if (token.type == WordType.ID) {
			t.name[(t.getIdNum())] = token.context;
			match(WordType.ID);
			t.setIdNum((t.getIdNum()) + 1);
		} else {
			wrongMessage("a varid is expected here!",token.line);
			getNextToken();
		}
		varIdMore(t);
	}



	// ������ varIdMore
	// �� �� �����������ֵĴ�����
	// ����ʽ < varIdMore > ::= �� | , varIdList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public void varIdMore(TreeNode t) {
		switch (token.type) {
		case SEMI:
			break;
		case COMMA:
			match(WordType.COMMA);
			varIdList(t);
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}

	//*************************** ������������ **************************



	// ������ procDec
	// �� �� �����������ֵĴ�����
	// ����ʽ < procDec > ::= �� | procDeclaration
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public TreeNode procDec() {
		TreeNode t = null;
		switch (token.type) {
		case BEGIN:
			break;
		case PROCEDURE:
			t = procDeclaration();
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ procDeclaration
	// �� �� �����������ֵĴ�����
	// ����ʽ < procDeclaration > ::= PROCEDURE
	// ProcName(paramList);
	// procDecPart
	// procBody
	// procDec
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	// �����ĸ��ڵ����ڼ�¼�ú��������֣���һ���ӽڵ�ָ�������
	// �㣬�ڶ����ڵ�ָ�����е��������ֽڵ㣻�������ڵ�ָ������

	public TreeNode procDeclaration() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.ProcDecK);
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.PROCEDURE);
		if (t != null) {
			t.setLineno(line0);
			if (token.type == WordType.ID) {
				t.name[0] = token.context;
				t.setIdNum(t.getIdNum() + 1);
				match(WordType.ID);
			}
			match(WordType.LPAREN);
			paramList(t);
			match(WordType.RPAREN);
			match(WordType.SEMI);
			t.child[1] = procDecPart();
			t.child[2] = procBody();
			t.setSibling(procDec());
		}
		return t;
	}



	// ������ paramList
	// �� �� ���������в����������ֵĴ�����
	// ����ʽ < paramList > ::= �� | paramDecList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public void paramList(TreeNode t) {
		TreeNode p = null;
		switch (token.type) {
		case RPAREN:
			break;
		case INTEGER:
		case CHAR:
		case ARRAY:
		case RECORD:
		case ID:
		case VAR:
			p = paramDecList();
			t.child[0] = p;
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}



	// ������ paramDecList
	// �� �� ���������в����������ֵĴ�����
	// ����ʽ < paramDecList > ::= param paramMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�


	public TreeNode paramDecList() {
		TreeNode t = param();
		TreeNode p = paramMore();
		if (p != null) {
			t.setSibling(p);
		}
		return t;
	}



	// ������ paramMore
	// �� �� ���������в����������ֵĴ�����
	// ����ʽ < paramMore > ::= �� | ; paramDecList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode paramMore() {
		TreeNode t = null;
		switch (token.type) {
		case RPAREN:
			break;
		case SEMI:
			match(WordType.SEMI);
			t = paramDecList();
			if (t == null)
				wrongMessage("a param declaration is request!",token.line);
			break;
		default:
			getNextToken();// //////////!!!!!!!!!!!1
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ param
	// �� �� ���������в����������ֵĴ�����
	// ����ʽ < param > ::= typeName formList | VAR typeName formList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode param() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.DecK);
		t1.setLineno(lineNo);
		TreeNode t = t1;
		if (t != null) {
			t.setLineno(line0);
			switch (token.type) {
			case INTEGER:
			case CHAR:
			case ARRAY:
			case RECORD:
			case ID:
				t.attr.procattr.paramt = ParamType.valparamType;
				typeName(t);
				formList(t);
				break;
			case VAR:
				match(WordType.VAR);
				t.attr.procattr.paramt = ParamType.varparamType;
				typeName(t);
				formList(t);
				break;
			default:
				getNextToken();// ///////!!!!!!!!!
				wrongMessage("unexpected token is here!",token.line);
				break;
			}
		}
		return t;
	}



	// ������ formList
	// �� �� ���������в����������ֵĴ�����
	// ����ʽ < formList > ::= id fidMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void formList(TreeNode t) {
		if (token.type == WordType.ID) {
			t.name[(t.getIdNum())] = token.context;
			t.setIdNum(t.getIdNum() + 1);
			match(WordType.ID);
		}
		fidMore(t);
	}



	// ������ fidMore
	// �� �� ���������в����������ֵĴ�����
	// ����ʽ < fidMore > ::= �� | , formList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public void fidMore(TreeNode t) {
		switch (token.type) {
		case SEMI:
		case RPAREN:
			break;
		case COMMA:
			match(WordType.COMMA);
			formList(t);
			break;
		default:
			getNextToken();// !!!!!!!!!!
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}



	// ������ procDecPart
	// �� �� �����е��������ֵĴ�����
	// ����ʽ < procDecPart > ::= declarePart
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode procDecPart() {
		TreeNode t = declarePart();
		return t;
	}



	// ������ procBody
	// �� �� �����岿�ֵĴ�����
	// ����ʽ < procBody > ::= programBody
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode procBody() {
		TreeNode t = programBody();
		if (t == null)
			wrongMessage("a program body is requested!",token.line);
		return t;
	}



	// ������ programBody
	// �� �� �����岿�ֵĴ�����
	// ����ʽ < programBody > ::= BEGIN stmList END
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode programBody() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.BEGIN);
		if (t != null) {
			t.setLineno(0);
			// t.child[0] = new TreeNode();
			t.child[0] = stmList();
		}
		match(WordType.END);
		return t;
	}



	// ������ stmList
	// �� �� ��䲿�ֵĴ�����
	// ����ʽ < stmList > ::= stm stmMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode stmList() {
		TreeNode t = stm();
		if (t != null)
			t.setSibling(stmMore());
		return t;
	}

	//****************************************************************
	// ������ stmMore
	// �� �� ��䲿�ֵĴ�����
	// ����ʽ < stmMore > ::= �� | ; stmList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode stmMore() {
		TreeNode t = null;
		switch (token.type) {
		case ELSE:
		case FI:
		case END:
		case ENDWH:
			break;
		case SEMI:
			match(WordType.SEMI);
			t = stmList();
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ stm
	// �� �� ��䲿�ֵĴ�����
	// ����ʽ < stm > ::= conditionalStm {IF}
	// | loopStm {WHILE}
	// | inputStm {READ}
	// | outputStm {WRITE}
	// | returnStm {RETURN}
	// | id assCall {id}
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode stm() {
		TreeNode t = null;
		switch (token.type) {
		case IF:
			t = conditionalStm();
			break;
		case WHILE:
			t = loopStm();
			break;
		case READ:
			t = inputStm();
			break;
		case WRITE:
			t = outputStm();
			break;
		case RETURN:
			t = returnStm();
			break;
		case ID:
			temp_name = token.context;
			match(WordType.ID);
			t = assCall();
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ assCall
	// �� �� ��䲿�ֵĴ�����
	// ����ʽ < assCall > ::= assignmentRest {:=,LMIDPAREN,DOT}
	// | callStmRest {(}
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode assCall() {
		TreeNode t = null;
		switch (token.type) {
		case ASSIGN:
		case LMIDPAREN:
		case DOT:
			t = assignmentRest();
			break;
		case LPAREN:
			t = callStmRest();
			break;
		default:
			getNextToken();// /////////!!!!!!!!!!!!
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ assignmentRest
	// �� �� ��ֵ��䲿�ֵĴ�����
	// ����ʽ < assignmentRest > ::= variMore : = exp
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode assignmentRest() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.stmt = StmtKind.AssignK;
		t1.setLineno(lineNo);
		TreeNode t = t1;

		if (t != null) {
			t.setLineno(line0);
			// �����һ�����ӽ�㣬Ϊ�������ʽ���ͽڵ�
			TreeNode t2 = new TreeNode();
			t2.setNodeKind(NodeKind.ExpK);
			// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
			t2.kind.exp = ExpKind.VariK;
			t2.setLineno(lineNo);
			t2.attr.expattr.varkind = VarKind.IdV;
			// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
			t2.attr.expattr.type = ExpType.Void;
			TreeNode child1 = t2;
			if (child1 != null) {
				child1.setLineno(line0);
				child1.name[0] = temp_name;
				child1.setIdNum(child1.getIdNum() + 1);
				variMore(child1);
				t.child[0] = child1;
			}

			// ��ֵ��ƥ��
			match(WordType.ASSIGN);

			// ����ڶ������ӽڵ�
			t.child[1] = exp();
		}
		return t;
	}



	// ������ conditionalStm
	// �� �� ������䲿�ֵĴ�����
	// ����ʽ < conditionalStm > ::= IF exp THEN stmList ELSE stmList FI
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode conditionalStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);
		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.stmt = StmtKind.IfK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.IF);
		if (t != null) {
			t.setLineno(line0);
			t.child[0] = exp();
		}
		match(WordType.THEN);
		if (t != null)
			t.child[1] = stmList();
		if (token.type == WordType.ELSE) {
			match(WordType.ELSE);
			if (t != null)
				t.child[2] = stmList();
		}
		match(WordType.FI);
		return t;
	}



	// ������ loopStm
	// �� �� ѭ����䲿�ֵĴ�����
	// ����ʽ < loopStm > ::= WHILE exp DO stmList ENDWH
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode loopStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.stmt = StmtKind.WhileK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.WHILE);
		if (t != null) {
			t.setLineno(line0);
			t.child[0] = exp();
			match(WordType.DO);
			t.child[1] = stmList();
			match(WordType.ENDWH);
		}
		return t;
	}
	// ������ inputStm
	// �� �� ������䲿�ֵĴ�����
	// ����ʽ < inputStm > ::= READ(id)
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode inputStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.stmt = StmtKind.ReadK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.READ);
		match(WordType.LPAREN);
		if ((t != null) && (token.type == WordType.ID)) {
			t.setLineno(line0);
			t.name[0] = token.context;
			t.setIdNum(t.getIdNum() + 1);
		}
		match(WordType.ID);
		match(WordType.RPAREN);
		return t;
	}



	// ������ outputStm
	// �� �� �����䲿�ֵĴ�����
	// ����ʽ < outputStm > ::= WRITE(exp)
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode outputStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.stmt = StmtKind.WriteK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.WRITE);
		match(WordType.LPAREN);
		if (t != null) {
			t.setLineno(line0);
			t.child[0] = exp();
		}
		match(WordType.RPAREN);
		return t;
	}



	// ������ returnStm
	// �� �� ������䲿�ֵĴ�����
	// ����ʽ < returnStm > ::= RETURN
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode returnStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.stmt = StmtKind.ReturnK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.RETURN);
		if (t != null)
			t.setLineno(line0);
		return t;
	}



	// ������ callStmRest
	// �� �� ����������䲿�ֵĴ�����
	// ����ʽ < callStmRest > ::= (actParamList)
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode callStmRest() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.stmt = StmtKind.CallK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.LPAREN);
		// ��������ʱ�����ӽڵ�ָ��ʵ��
		if (t != null) {
			t.setLineno(line0);

			// �������Ľ��Ҳ�ñ��ʽ���ͽ��
			TreeNode t2 = new TreeNode();
			t2.setNodeKind(NodeKind.ExpK);
			// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
			t2.kind.exp = ExpKind.VariK;
			t2.setLineno(lineNo);
			t2.attr.expattr.varkind = VarKind.IdV;
			// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
			t2.attr.expattr.type = ExpType.Void;
			TreeNode child0 = t2;
			if (child0 != null) {
				child0.setLineno(line0);
				child0.name[0] = temp_name;
				child0.setIdNum(child0.getIdNum() + 1);
				t.child[0] = child0;
			}
			t.child[1] = actParamList();
		}
		match(WordType.RPAREN);
		return t;
	}



	// ������ actParamList
	// �� �� ��������ʵ�β��ֵĴ�����
	// ����ʽ < actParamList > ::= �� | exp actParamMore
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode actParamList() {
		TreeNode t = null;
		switch (token.type) {
		case RPAREN:
			break;
		case ID:
		case INTC:
			t = exp();
			if (t != null)
				t.setSibling(actParamMore());
			break;
		default:
			getNextToken();// ///!!!!!!!!!!!111
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}



	// ������ actParamMore
	// �� �� ��������ʵ�β��ֵĴ�����
	// ����ʽ < actParamMore > ::= �� | , actParamList
	// ˵ �� ���������ķ�����ʽ,������Ӧ�ĵݹ鴦����,�����﷨���ڵ�
	public TreeNode actParamMore() {
		TreeNode t = null;
		switch (token.type) {
		case RPAREN:
			break;
		case COMMA:
			match(WordType.COMMA);
			t = actParamList();
			break;
		default:
			getNextToken();// /!!!!!!!!!!!!
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		return t;
	}

	// ������ exp
	// �� �� ���ʽ������
	// ����ʽ < ���ʽ > ::= < �򵥱��ʽ > [< ��ϵ����� > < �򵥱��ʽ > ]
	// ˵ �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�
	public TreeNode exp() {
		// ���ü򵥱��ʽ������simple_exp(),�����﷨���ڵ�ָ���t
		TreeNode t = simpleExp();

		// ��ǰ����tokenΪ�߼����㵥��LT����EQ
		if ((token.type == WordType.LT) || (token.type == WordType.EQ)) {
			// �����µ�OpK�����﷨���ڵ㣬���﷨���ڵ�ָ�븳��p
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
			t1.kind.exp = ExpKind.OpK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
			t1.attr.expattr.type = ExpType.Void;
			TreeNode p = t1;


			 //���﷨���ڵ�p�����ɹ�,��ʼ��p��һ���ӽڵ��Աchild[0]
			 //������ǰ����token(ΪEQ����LT)�����﷨���ڵ�p���������Աattr.op
			if (p != null) {
				p.setLineno(line0);
				p.child[0] = t;
				p.attr.expattr.op = token.type;

				// ���µı��ʽ�����﷨���ڵ�p��Ϊ��������ֵt
				t = p;
			}

			// ��ǰ����token��ָ���߼����������(ΪEQ����LT)ƥ��
			match(token.type);

			//�﷨���ڵ�t�ǿ�,���ü򵥱��ʽ������simpleExp() * ���������﷨���ڵ�ָ���t�ĵڶ��ӽڵ��Աchild[1]

			if (t != null)
				t.child[1] = simpleExp();
		}

		// �������ر��ʽ�����﷨���ڵ�t
		return t;
	}

	// ������ simple_exp
	// �� �� �򵥱��ʽ������
	// ����ʽ < �򵥱��ʽ >::= < �� > { < �ӷ������ > < �� > }
	// ˵ �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�
	public TreeNode simpleExp() {
		// ����Ԫ������term(),���������﷨���ڵ�ָ���t
		TreeNode t = term();
		// ��ǰ����tokenΪ�ӷ����������PLUS��MINUS
		while ((token.type == WordType.PLUS) || (token.type == WordType.MINUS)) {
			// ������OpK���ʽ�����﷨���ڵ㣬���﷨���ڵ�ָ�븳��p
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
			t1.kind.exp = ExpKind.OpK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
			t1.attr.expattr.type = ExpType.Void;
			TreeNode p = t1;


			 //�﷨���ڵ�p�����ɹ�,��ʼ��p��һ�ӽڵ��Աchild[0] * �����﷨���ڵ�ָ���p���������Աattr.op

			if (p != null) {
				p.setLineno(line0);
				p.child[0] = t;
				p.attr.expattr.op = token.type;

				// ����������ֵt�����﷨���ڵ�p
				t = p;
				// ��ǰ����token��ָ���ӷ����㵥��(ΪPLUS��MINUS)ƥ��
				match(token.type);

				// ����Ԫ������term(),���������﷨���ڵ�ָ���t�ĵڶ��ӽڵ��Աchild[1]
				t.child[1] = term();
			}
		}
		// �������ر��ʽ�����﷨���ڵ�t
		return t;
	}

	// ������ term
	// �� �� �����
	// ����ʽ < �� > ::= < ���� > { < �˷������ > < ���� > }
	// ˵ �� �ú������ݲ���ʽ������Ӧ�ݹ鴦����,���ɱ��ʽ�����﷨���ڵ�
	public TreeNode term() {
		// �������Ӵ�����factor(),���������﷨���ڵ�ָ���t
		TreeNode t = factor();

		// ��ǰ����tokenΪ�˷����������TIMES��OVER
		while ((token.type == WordType.TIMES) || (token.type == WordType.OVER)) {
			// �����µ�OpK���ʽ�����﷨���ڵ�,�½ڵ�ָ�븳��p
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
			t1.kind.exp = ExpKind.OpK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
			t1.attr.expattr.type = ExpType.Void;
			TreeNode p = t1;


			 //���﷨���ڵ�p�����ɹ�,��ʼ����һ���ӽڵ��Աchild[0]Ϊt *
			 //����ǰ����token��ֵ���﷨���ڵ�p���������Աattr.op

			if (p != null) {
				p.setLineno(line0);
				p.child[0] = t;
				p.attr.expattr.op = token.type;
				t = p;
			}

			// ��ǰ����token��ָ���˷����������(ΪTIMES��OVER)ƥ��
			match(token.type);

			// �������Ӵ�����factor(),���������﷨���ڵ�ָ�븳��p�ڶ����ӽڵ��Աchild[1]
			p.child[1] = factor();

		}
		// �������ر��ʽ�����﷨���ڵ�t
		return t;
	}

	// ������ factor
	// �� �� ���Ӵ�����
	// ����ʽ factor ::= ( exp ) | INTC | variable
	// ˵ �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦����,���ɱ��ʽ�����﷨���ڵ�
	public TreeNode factor() {
		// ���������﷨���ڵ�ָ��t��ʼΪΪnull
		TreeNode t = null;

		switch (token.type) {
		case INTC:

			// �����µ�ConstK���ʽ�����﷨���ڵ�,��ֵ��t
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
			t1.kind.exp = ExpKind.ConstK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
			t1.attr.expattr.type = ExpType.Void;
			t = t1;


			 //���﷨���ڵ�t�����ɹ�,��ǰ����tokenΪ���ֵ���NUM *
			 //����ǰ������tokenStringת��Ϊ�����������﷨���ڵ�t����ֵ��Աattr.val

			if ((t != null) && (token.type == WordType.INTC)) {
				t.setLineno(line0);
				t.attr.expattr.val = Integer.parseInt(token.context);
			}

			// ��ǰ����token�����ֵ���NUMƥ��
			match(WordType.INTC);
			break;

		// ��ǰ����tokenΪ��ʶ������ID
		case ID:

			// �����µ�IdK���ʽ�����﷨���ڵ�t
			t = variable();
			break;

		// ��ǰ����tokenΪ�����ŵ���LPAREN
		case LPAREN:

			// ��ǰ����token�������ŵ���LPARENƥ��
			match(WordType.LPAREN);

			// ���ñ��ʽ������exp(),���������﷨���ڵ�ָ���t
			t = exp();

			// ��ǰ����token�������ŵ���RPARENƥ��
			match(WordType.RPAREN);

			break;

		// ��ǰ����tokenΪ��������
		default:
			getNextToken();// ////////////111111111111111111
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		// �������ر��ʽ�����﷨���ڵ�t
		return t;
	}



	// ������ variable
	// �� �� ����������
	// ����ʽ variable ::= id variMore
	// ˵ �� �ú������ݲ���ʽ, ����������������﷨���ڵ�
	public TreeNode variable() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.ExpK);
		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.exp = ExpKind.VariK;
		t1.setLineno(lineNo);
		t1.attr.expattr.varkind = VarKind.IdV;
		// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
		t1.attr.expattr.type = ExpType.Void;
		TreeNode t = t1;

		if ((t != null) && (token.type == WordType.ID)) {
			t.setLineno(line0);
			t.name[0] = token.context;
			t.setIdNum(t.getIdNum() + 1);
		}

		match(WordType.ID);
		variMore(t);
		return t;
	}



	// ������ variMore
	// �� �� ����������
	// ����ʽ variMore ::= ��
	// | [exp] {[}
	// | . fieldvar {DOT}
	// ˵ �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦������еļ��ֲ�ͬ����
	public void variMore(TreeNode t) {
		switch (token.type) {
		case ASSIGN:
		case TIMES:
		case EQ:
		case LT:
		case PLUS:
		case MINUS:
		case OVER:
		case RPAREN:
		case RMIDPAREN:
		case SEMI:
		case COMMA:
		case THEN:
		case ELSE:
		case FI:
		case DO:
		case ENDWH:
		case END:
			break;
		case LMIDPAREN:
			match(WordType.LMIDPAREN);

			// �����Ժ��������ʽ��ֵ���������������±����
			t.child[0] = exp();

			t.attr.expattr.varkind = VarKind.ArrayMembV;

			// �˱��ʽΪ�����Ա��������
			t.child[0].attr.expattr.varkind = VarKind.IdV;
			match(WordType.RMIDPAREN);
			break;
		case DOT:
			match(WordType.DOT);
			// ��һ������ָ�����Ա�������
			t.child[0] = fieldvar();

			t.attr.expattr.varkind = VarKind.FieldMembV;

			t.child[0].attr.expattr.varkind = VarKind.IdV;
			break;
		default:
			getNextToken();
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}



	// ������ fieldvar
	// �� �� ����������
	// ����ʽ fieldvar ::= id fieldvarMore
	// ˵ �� �ú������ݲ���ʽ����������������������﷨���ڵ�
	public TreeNode fieldvar() {
		// ע�⣬�ɷ񽫴˴���IdEK��Ϊһ���µı�ʶ��������¼��¼���͵���
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.ExpK);
		// ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind
		t1.kind.exp = ExpKind.VariK;
		t1.setLineno(lineNo);
		t1.attr.expattr.varkind = VarKind.IdV;
		// ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid
		t1.attr.expattr.type = ExpType.Void;
		TreeNode t = t1;

		if ((t != null) && (token.type == WordType.ID)) {
			t.setLineno(line0);
			t.name[0] = token.context;
			t.setIdNum(t.getIdNum() + 1);
		}
		match(WordType.ID);
		fieldvarMore(t);
		return t;
	}



	// ������ fieldvarMore
	// �� �� ����������
	// ����ʽ fieldvarMore ::= ��
	// | [exp] {[}
	// ˵ �� �ú������ݲ���ʽ������Ӧ�ĵݹ鴦�������Ϊ�������͵����
	public void fieldvarMore(TreeNode t) {
		switch (token.type) {
		case ASSIGN:
		case TIMES:
		case EQ:
		case LT:
		case PLUS:
		case MINUS:
		case OVER:
		case RPAREN:
		case SEMI:
		case COMMA:
		case THEN:
		case ELSE:
		case FI:
		case DO:
		case ENDWH:
		case END:
			break;
		case LMIDPAREN:
			match(WordType.LMIDPAREN);
			// �����Ժ��������ʽ��ֵ���������������±����
			t.child[0] = exp();
			t.child[0].attr.expattr.varkind = VarKind.ArrayMembV;
			match(WordType.RMIDPAREN);
			break;
		default:
			getNextToken();// //!!!!!!!!!!!!!!!!!!!!1
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
	}


	// ������ parse
	// �� �� �﷨��������
	// ˵ �� �ú����Ѵʷ�����������Ϊ�ӳ������,���õݹ��½���
	// ���ݲ���ʽ���õݹ鴦����,����ΪԴ���򴴽��﷨������
	public TreeNode parse() {
		next = 0; // ���ʱ��¼token�����ı���
		Error = false;
		parsingList = new ArrayList<String>();
		// ���ļ�Tokenlist��ȡ�õ�һ������,���ʷ���Ϣ�͸�token
		getNextToken();
		program();
		// ��ǰ����token����ENDFILE,���������ļ�����ǰ��ǰ��������
		if (token.type != WordType.ENDFILE)
			wrongMessage("Code ends before file\n",token.line);
		return root;
	}

	public void writeToFile(String tofile) {
		try {
			File file = new File(tofile);
			FileOutputStream fous = new FileOutputStream(file);
			OutputStreamWriter fos = new OutputStreamWriter(fous);
			PrintWriter writer = new PrintWriter(fos);
			if (Error)
				writer.println("*******************�﷨�д���*******************");
			else {
				writer.println("*******************�﷨��ȷ,�﷨������:*******************");
			}
			for (String str : parsingList) {
				writer.println(str);
				writer.flush(); // ///////////
			}
			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("���ļ�" + tofile + "��д���﷨�� ʧ��");
		}
	}

	public TreeNode doParsing(List<Token> list) {
		tokenList = list;
		root = this.parse();
		this.printTree(root);
		return root;
	}

}
