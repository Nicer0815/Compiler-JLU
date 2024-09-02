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

	private final int lineNo = 0; 	// 已处理指针
	private int tab = 0; 			// 打印空格数量，输出层次化语法树
	private boolean Error;			// 语法分析是否有错误，用于message输出
	private Token token;			// 当前指向的token
	private int line0; 				// 当前正处理指针match
	private int next;				// 逻辑指针，指向token列表
	private String temp_name;		// 临时变量 PDF 93页用于stm语句递归处理分析
	private List<Token> tokenList;	// Token列表
	private List<String> parsingList;//语法树的字符串列表
	private String tempStr;			// 临时字符串，用于打印语法树
	private String errorStr;		// 临时字符串，用于打印语法错误
	private TreeNode root;			// 语法树根节点

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

	//打印缩进
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
			//缩进
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

			// 对语法树结点tree的各子结点递归调用printTree过程
			// 缩进写入列表文件listing
			for (int i = 0; i < 3; i++)
				printTree(tree.child[i]);
			tree = tree.getSibling();
		}
		tab--;
	}

	// 功 能 语法错误处理函数
	// 说 明 将函数参数message指定的错误信息格式化写入errorStr
	public void wrongMessage(String message,int line) {
		errorStr += "\n" + line + " :" + message;
		//System.out.println(errorStr);
		Error = true;
	}

	// 终极符匹配处理函数
	// 函数参数expected给定期望单词符号与当前单词符号token相匹配
	// 如果不匹配,则报非期望单词语法错误
	public void match(WordType expected) {
		if (token.type == expected) {
			getNextToken();
			line0 = token.line;
		} else {
			wrongMessage("not match error ",token.line);
			getNextToken();
		}
	}

	// 函数名 program
	// 功 能 总程序的处理函数
	// 产生式 < program > ::= programHead declarePart programBody .
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	// 语法树的根节点的第一个子节点指向程序头部分programHead,
	// DeclaraPart为programHead的兄弟节点,程序体部分programBody
	// 为declarePart的兄弟节点.
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

	// 功 能 程序头的处理函数
	// 产生式 < programHead > ::= PROGRAM ProgramName
	public TreeNode programHead() {
		// 功 能 创建程序头类型语法树节点函数
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.PheadK);
		// 相同类型的变量个数
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

	// 功 能 声明部分的处理函数
	// 产生式 < declarePart > ::= typeDec varDec procDec
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode declarePart() {
		// 类型
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

		// 变量
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
		// 函数
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
	// 功 能 类型声明部分的处理函数
	// 产生式 < typeDec > ::= ε | TypeDeclaration
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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


	// 函数名 TypeDeclaration
	// 功 能 类型声明部分的处理函数
	// 产生式 < TypeDeclaration > ::= TYPE TypeDecList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode typeDeclaration() {
		match(WordType.TYPE);
		TreeNode t = typeDecList();
		if (t == null) {
			wrongMessage("a type declaration is expected!",token.line);
		}
		return t;
	}


	// 函数名 TypeDecList
	// 功 能 类型声明部分的处理函数
	// 产生式 < TypeDecList > ::= typeId = typeName ; typeDecMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 typeDecMore
	// 功 能 类型声明部分的处理函数
	// 产生式 < typeDecMore > ::= ε | TypeDecList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 typeId
	// 功 能 类型声明部分的处理函数
	// 产生式 < typeId > ::= id
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public void typeId(TreeNode t) {
		int tnum = (t.getIdNum());
		if ((token.type == WordType.ID) && (t != null)) {
			t.name[tnum] = token.context;
			tnum = tnum + 1;
		}
		t.setIdNum(tnum);
		match(WordType.ID);
	}



	// 函数名 typeName
	// 功 能 类型声明部分的处理函数
	// 产生式 < typeName > ::= baseType | structureType | id
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 baseType
	// 功 能 类型声明部分的处理函数
	// 产生式 < baseType > ::= INTEGER | CHAR
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 structureType
	// 功 能 类型声明部分的处理函数
	// 产生式 < structureType > ::= arrayType | recType
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 arrayType
	// 功 能 类型声明部分的处理函数
	// 产生式 < arrayType > ::= ARRAY [low..top] OF baseType
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 recType
	// 功 能 类型声明部分的处理函数
	// 产生式 < recType > ::= RECORD fieldDecList END
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 fieldDecList
	// 功 能 类型声明部分的处理函数
	// 产生式 < fieldDecList > ::= baseType idList ; fieldDecMore
	// | arrayType idList; fieldDecMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 fieldDecMore
	// 功 能 类型声明部分的处理函数
	// 产生式 < fieldDecMore > ::= ε | fieldDecList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 idList
	// 功 能 类型声明部分的处理函数
	// 产生式 < idList > ::= id idMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


	public void idList(TreeNode t) {
		if (token.type == WordType.ID) {
			t.name[(t.getIdNum())] = token.context;
			match(WordType.ID);
			t.setIdNum(t.getIdNum() + 1);
		}
		idMore(t);
	}



	// 函数名 idMore
	// 功 能 类型声明部分的处理函数
	// 产生式 < idMore > ::= ε | , idList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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

	//************************* 变量声明部分 ****************************



	// 函数名 varDec
	// 功 能 变量声明部分的处理函数
	// 产生式 < varDec > ::= ε | varDeclaration
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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



	// 函数名 varDeclaration
	// 功 能 变量声明部分的处理函数
	// 产生式 < varDeclaration > ::= VAR varDecList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


	public TreeNode varDeclaration() {
		match(WordType.VAR);
		TreeNode t = varDecList();
		if (t == null)
			wrongMessage("a var declaration is expected!",token.line);
		return t;
	}



	// 函数名 varDecList
	// 功 能 变量声明部分的处理函数
	// 产生式 < varDecList > ::= typeName varIdList; varDecMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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



	// 函数名 varDecMore
	// 功 能 变量声明部分的处理函数
	// 产生式 < varDecMore > ::= ε | varDecList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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



	// 函数名 varIdList
	// 功 能 变量声明部分的处理函数
	// 产生式 < varIdList > ::= id varIdMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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



	// 函数名 varIdMore
	// 功 能 变量声明部分的处理函数
	// 产生式 < varIdMore > ::= ε | , varIdList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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

	//*************************** 过程声明部分 **************************



	// 函数名 procDec
	// 功 能 函数声明部分的处理函数
	// 产生式 < procDec > ::= ε | procDeclaration
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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



	// 函数名 procDeclaration
	// 功 能 函数声明部分的处理函数
	// 产生式 < procDeclaration > ::= PROCEDURE
	// ProcName(paramList);
	// procDecPart
	// procBody
	// procDec
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	// 函数的根节点用于记录该函数的名字；第一个子节点指向参数节
	// 点，第二个节点指向函数中的声明部分节点；第三个节点指向函数体

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



	// 函数名 paramList
	// 功 能 函数声明中参数声明部分的处理函数
	// 产生式 < paramList > ::= ε | paramDecList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


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



	// 函数名 paramDecList
	// 功 能 函数声明中参数声明部分的处理函数
	// 产生式 < paramDecList > ::= param paramMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点


	public TreeNode paramDecList() {
		TreeNode t = param();
		TreeNode p = paramMore();
		if (p != null) {
			t.setSibling(p);
		}
		return t;
	}



	// 函数名 paramMore
	// 功 能 函数声明中参数声明部分的处理函数
	// 产生式 < paramMore > ::= ε | ; paramDecList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 param
	// 功 能 函数声明中参数声明部分的处理函数
	// 产生式 < param > ::= typeName formList | VAR typeName formList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 formList
	// 功 能 函数声明中参数声明部分的处理函数
	// 产生式 < formList > ::= id fidMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public void formList(TreeNode t) {
		if (token.type == WordType.ID) {
			t.name[(t.getIdNum())] = token.context;
			t.setIdNum(t.getIdNum() + 1);
			match(WordType.ID);
		}
		fidMore(t);
	}



	// 函数名 fidMore
	// 功 能 函数声明中参数声明部分的处理函数
	// 产生式 < fidMore > ::= ε | , formList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 procDecPart
	// 功 能 函数中的声明部分的处理函数
	// 产生式 < procDecPart > ::= declarePart
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode procDecPart() {
		TreeNode t = declarePart();
		return t;
	}



	// 函数名 procBody
	// 功 能 函数体部分的处理函数
	// 产生式 < procBody > ::= programBody
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode procBody() {
		TreeNode t = programBody();
		if (t == null)
			wrongMessage("a program body is requested!",token.line);
		return t;
	}



	// 函数名 programBody
	// 功 能 程序体部分的处理函数
	// 产生式 < programBody > ::= BEGIN stmList END
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 stmList
	// 功 能 语句部分的处理函数
	// 产生式 < stmList > ::= stm stmMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode stmList() {
		TreeNode t = stm();
		if (t != null)
			t.setSibling(stmMore());
		return t;
	}

	//****************************************************************
	// 函数名 stmMore
	// 功 能 语句部分的处理函数
	// 产生式 < stmMore > ::= ε | ; stmList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 stm
	// 功 能 语句部分的处理函数
	// 产生式 < stm > ::= conditionalStm {IF}
	// | loopStm {WHILE}
	// | inputStm {READ}
	// | outputStm {WRITE}
	// | returnStm {RETURN}
	// | id assCall {id}
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 assCall
	// 功 能 语句部分的处理函数
	// 产生式 < assCall > ::= assignmentRest {:=,LMIDPAREN,DOT}
	// | callStmRest {(}
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 assignmentRest
	// 功 能 赋值语句部分的处理函数
	// 产生式 < assignmentRest > ::= variMore : = exp
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode assignmentRest() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
		t1.kind.stmt = StmtKind.AssignK;
		t1.setLineno(lineNo);
		TreeNode t = t1;

		if (t != null) {
			t.setLineno(line0);
			// 处理第一个儿子结点，为变量表达式类型节点
			TreeNode t2 = new TreeNode();
			t2.setNodeKind(NodeKind.ExpK);
			// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
			t2.kind.exp = ExpKind.VariK;
			t2.setLineno(lineNo);
			t2.attr.expattr.varkind = VarKind.IdV;
			// 指定新语法树节点t成员: 类型检查类型type为Void
			t2.attr.expattr.type = ExpType.Void;
			TreeNode child1 = t2;
			if (child1 != null) {
				child1.setLineno(line0);
				child1.name[0] = temp_name;
				child1.setIdNum(child1.getIdNum() + 1);
				variMore(child1);
				t.child[0] = child1;
			}

			// 赋值号匹配
			match(WordType.ASSIGN);

			// 处理第二个儿子节点
			t.child[1] = exp();
		}
		return t;
	}



	// 函数名 conditionalStm
	// 功 能 条件语句部分的处理函数
	// 产生式 < conditionalStm > ::= IF exp THEN stmList ELSE stmList FI
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode conditionalStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);
		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
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



	// 函数名 loopStm
	// 功 能 循环语句部分的处理函数
	// 产生式 < loopStm > ::= WHILE exp DO stmList ENDWH
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode loopStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
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
	// 函数名 inputStm
	// 功 能 输入语句部分的处理函数
	// 产生式 < inputStm > ::= READ(id)
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode inputStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
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



	// 函数名 outputStm
	// 功 能 输出语句部分的处理函数
	// 产生式 < outputStm > ::= WRITE(exp)
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode outputStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
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



	// 函数名 returnStm
	// 功 能 返回语句部分的处理函数
	// 产生式 < returnStm > ::= RETURN
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode returnStm() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
		t1.kind.stmt = StmtKind.ReturnK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.RETURN);
		if (t != null)
			t.setLineno(line0);
		return t;
	}



	// 函数名 callStmRest
	// 功 能 函数调用语句部分的处理函数
	// 产生式 < callStmRest > ::= (actParamList)
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
	public TreeNode callStmRest() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.StmLK);

		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
		t1.kind.stmt = StmtKind.CallK;
		t1.setLineno(lineNo);
		TreeNode t = t1;
		match(WordType.LPAREN);
		// 函数调用时，其子节点指向实参
		if (t != null) {
			t.setLineno(line0);

			// 函数名的结点也用表达式类型结点
			TreeNode t2 = new TreeNode();
			t2.setNodeKind(NodeKind.ExpK);
			// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
			t2.kind.exp = ExpKind.VariK;
			t2.setLineno(lineNo);
			t2.attr.expattr.varkind = VarKind.IdV;
			// 指定新语法树节点t成员: 类型检查类型type为Void
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



	// 函数名 actParamList
	// 功 能 函数调用实参部分的处理函数
	// 产生式 < actParamList > ::= ε | exp actParamMore
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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



	// 函数名 actParamMore
	// 功 能 函数调用实参部分的处理函数
	// 产生式 < actParamMore > ::= ε | , actParamList
	// 说 明 函数根据文法产生式,调用相应的递归处理函数,生成语法树节点
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

	// 函数名 exp
	// 功 能 表达式处理函数
	// 产生式 < 表达式 > ::= < 简单表达式 > [< 关系运算符 > < 简单表达式 > ]
	// 说 明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点
	public TreeNode exp() {
		// 调用简单表达式处理函数simple_exp(),返回语法树节点指针给t
		TreeNode t = simpleExp();

		// 当前单词token为逻辑运算单词LT或者EQ
		if ((token.type == WordType.LT) || (token.type == WordType.EQ)) {
			// 创建新的OpK类型语法树节点，新语法树节点指针赋给p
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
			t1.kind.exp = ExpKind.OpK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// 指定新语法树节点t成员: 类型检查类型type为Void
			t1.attr.expattr.type = ExpType.Void;
			TreeNode p = t1;


			 //新语法树节点p创建成功,初始化p第一个子节点成员child[0]
			 //并将当前单词token(为EQ或者LT)赋给语法树节点p的运算符成员attr.op
			if (p != null) {
				p.setLineno(line0);
				p.child[0] = t;
				p.attr.expattr.op = token.type;

				// 将新的表达式类型语法树节点p作为函数返回值t
				t = p;
			}

			// 当前单词token与指定逻辑运算符单词(为EQ或者LT)匹配
			match(token.type);

			//语法树节点t非空,调用简单表达式处理函数simpleExp() * 函数返回语法树节点指针给t的第二子节点成员child[1]

			if (t != null)
				t.child[1] = simpleExp();
		}

		// 函数返回表达式类型语法树节点t
		return t;
	}

	// 函数名 simple_exp
	// 功 能 简单表达式处理函数
	// 产生式 < 简单表达式 >::= < 项 > { < 加法运算符 > < 项 > }
	// 说 明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点
	public TreeNode simpleExp() {
		// 调用元处理函数term(),函数返回语法树节点指针给t
		TreeNode t = term();
		// 当前单词token为加法运算符单词PLUS或MINUS
		while ((token.type == WordType.PLUS) || (token.type == WordType.MINUS)) {
			// 创建新OpK表达式类型语法树节点，新语法树节点指针赋给p
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
			t1.kind.exp = ExpKind.OpK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// 指定新语法树节点t成员: 类型检查类型type为Void
			t1.attr.expattr.type = ExpType.Void;
			TreeNode p = t1;


			 //语法树节点p创建成功,初始化p第一子节点成员child[0] * 返回语法树节点指针给p的运算符成员attr.op

			if (p != null) {
				p.setLineno(line0);
				p.child[0] = t;
				p.attr.expattr.op = token.type;

				// 将函数返回值t赋成语法树节点p
				t = p;
				// 当前单词token与指定加法运算单词(为PLUS或MINUS)匹配
				match(token.type);

				// 调用元处理函数term(),函数返回语法树节点指针给t的第二子节点成员child[1]
				t.child[1] = term();
			}
		}
		// 函数返回表达式类型语法树节点t
		return t;
	}

	// 函数名 term
	// 功 能 项处理函数
	// 产生式 < 项 > ::= < 因子 > { < 乘法运算符 > < 因子 > }
	// 说 明 该函数根据产生式调用相应递归处理函数,生成表达式类型语法树节点
	public TreeNode term() {
		// 调用因子处理函数factor(),函数返回语法树节点指针给t
		TreeNode t = factor();

		// 当前单词token为乘法运算符单词TIMES或OVER
		while ((token.type == WordType.TIMES) || (token.type == WordType.OVER)) {
			// 创建新的OpK表达式类型语法树节点,新节点指针赋给p
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
			t1.kind.exp = ExpKind.OpK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// 指定新语法树节点t成员: 类型检查类型type为Void
			t1.attr.expattr.type = ExpType.Void;
			TreeNode p = t1;


			 //新语法树节点p创建成功,初始化第一个子节点成员child[0]为t *
			 //将当前单词token赋值给语法树节点p的运算符成员attr.op

			if (p != null) {
				p.setLineno(line0);
				p.child[0] = t;
				p.attr.expattr.op = token.type;
				t = p;
			}

			// 当前单词token与指定乘法运算符单词(为TIMES或OVER)匹配
			match(token.type);

			// 调用因子处理函数factor(),函数返回语法树节点指针赋给p第二个子节点成员child[1]
			p.child[1] = factor();

		}
		// 函数返回表达式类型语法树节点t
		return t;
	}

	// 函数名 factor
	// 功 能 因子处理函数
	// 产生式 factor ::= ( exp ) | INTC | variable
	// 说 明 该函数根据产生式调用相应的递归处理函数,生成表达式类型语法树节点
	public TreeNode factor() {
		// 函数返回语法树节点指针t初始为为null
		TreeNode t = null;

		switch (token.type) {
		case INTC:

			// 创建新的ConstK表达式类型语法树节点,赋值给t
			TreeNode t1 = new TreeNode();
			t1.setNodeKind(NodeKind.ExpK);
			// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
			t1.kind.exp = ExpKind.ConstK;
			t1.setLineno(lineNo);
			t1.attr.expattr.varkind = VarKind.IdV;
			// 指定新语法树节点t成员: 类型检查类型type为Void
			t1.attr.expattr.type = ExpType.Void;
			t = t1;


			 //新语法树节点t创建成功,当前单词token为数字单词NUM *
			 //将当前单词名tokenString转换为整数并赋给语法树节点t的数值成员attr.val

			if ((t != null) && (token.type == WordType.INTC)) {
				t.setLineno(line0);
				t.attr.expattr.val = Integer.parseInt(token.context);
			}

			// 当前单词token与数字单词NUM匹配
			match(WordType.INTC);
			break;

		// 当前单词token为标识符单词ID
		case ID:

			// 创建新的IdK表达式类型语法树节点t
			t = variable();
			break;

		// 当前单词token为左括号单词LPAREN
		case LPAREN:

			// 当前单词token与左括号单词LPAREN匹配
			match(WordType.LPAREN);

			// 调用表达式处理函数exp(),函数返回语法树节点指针给t
			t = exp();

			// 当前单词token与右括号单词RPAREN匹配
			match(WordType.RPAREN);

			break;

		// 当前单词token为其它单词
		default:
			getNextToken();// ////////////111111111111111111
			wrongMessage("unexpected token is here!",token.line);
			break;
		}
		// 函数返回表达式类型语法树节点t
		return t;
	}



	// 函数名 variable
	// 功 能 变量处理函数
	// 产生式 variable ::= id variMore
	// 说 明 该函数根据产生式, 处理变量，生成其语法树节点
	public TreeNode variable() {
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.ExpK);
		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
		t1.kind.exp = ExpKind.VariK;
		t1.setLineno(lineNo);
		t1.attr.expattr.varkind = VarKind.IdV;
		// 指定新语法树节点t成员: 类型检查类型type为Void
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



	// 函数名 variMore
	// 功 能 变量处理函数
	// 产生式 variMore ::= ε
	// | [exp] {[}
	// | . fieldvar {DOT}
	// 说 明 该函数根据产生式调用相应的递归处理变量中的几种不同类型
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

			// 用来以后求出其表达式的值，送入用于数组下标计算
			t.child[0] = exp();

			t.attr.expattr.varkind = VarKind.ArrayMembV;

			// 此表达式为数组成员变量类型
			t.child[0].attr.expattr.varkind = VarKind.IdV;
			match(WordType.RMIDPAREN);
			break;
		case DOT:
			match(WordType.DOT);
			// 第一个儿子指向域成员变量结点
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



	// 函数名 fieldvar
	// 功 能 变量处理函数
	// 产生式 fieldvar ::= id fieldvarMore
	// 说 明 该函数根据产生式，处理域变量，并生成其语法树节点
	public TreeNode fieldvar() {
		// 注意，可否将此处的IdEK改为一个新的标识，用来记录记录类型的域
		TreeNode t1 = new TreeNode();
		t1.setNodeKind(NodeKind.ExpK);
		// 指定新语法树节点t成员:语句类型kind.stmt为函数给定参数kind
		t1.kind.exp = ExpKind.VariK;
		t1.setLineno(lineNo);
		t1.attr.expattr.varkind = VarKind.IdV;
		// 指定新语法树节点t成员: 类型检查类型type为Void
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



	// 函数名 fieldvarMore
	// 功 能 变量处理函数
	// 产生式 fieldvarMore ::= ε
	// | [exp] {[}
	// 说 明 该函数根据产生式调用相应的递归处理域变量为数组类型的情况
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
			// 用来以后求出其表达式的值，送入用于数组下标计算
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


	// 函数名 parse
	// 功 能 语法分析函数
	// 说 明 该函数把词法分析程序作为子程序调用,采用递归下降法
	// 根据产生式调用递归处理函数,函数为源程序创建语法分析树
	public TreeNode parse() {
		next = 0; // 输出时记录token个数的变量
		Error = false;
		parsingList = new ArrayList<String>();
		// 从文件Tokenlist中取得第一个单词,将词法信息送给token
		getNextToken();
		program();
		// 当前单词token不是ENDFILE,报代码在文件结束前提前结束错误
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
				writer.println("*******************语法有错误*******************");
			else {
				writer.println("*******************语法正确,语法树如下:*******************");
			}
			for (String str : parsingList) {
				writer.println(str);
				writer.flush(); // ///////////
			}
			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("向文件" + tofile + "中写入语法树 失败");
		}
	}

	public TreeNode doParsing(List<Token> list) {
		tokenList = list;
		root = this.parse();
		this.printTree(root);
		return root;
	}

}
