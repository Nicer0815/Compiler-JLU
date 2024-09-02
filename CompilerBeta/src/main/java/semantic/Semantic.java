package semantic;

import parsing.TreeNode;
import parsing.Type.*;
import semantic.Type.AccessKind;
import semantic.Type.IdKind;
import semantic.Type.TypeKind;

import java.util.ArrayList;
import java.util.List;

public class Semantic {

	/* scope栈的层数 */
	private int Level = 0;
	/* 在同层的变量偏移 */
	private int Off;
	/* 主程序display表的偏移 */
	private int StoreNoff;
	/* 各层的displayOff */
	private int savedOff;
	/* SCOPESIZE为符号表scope栈的大小 */
	private static final int SCOPESIZE = 1000;
	/* 初始化符号表中变量的偏移 */
	private static final int INITOFF = 5;
	/* scope栈 */
	private final SymbTable[] scope = new SymbTable[SCOPESIZE];

	private String errorMessage="";

	public String getErrorMessage() {
		return errorMessage;
	}

	private boolean Error = false;

	private List<String> list = null;

	public String getTempStr() {
		return tempStr;
	}

	private String tempStr = "";

	public int getLevel() {
		return Level;
	}

	public List<String> getList() {
		return list;
	}

	public boolean getError() {
		return Error;
	}


	/* 功 能 打印符号表的一层 */
	/* 说 明 有符号表打印函数PrintSymbTable调用 */
	public String PrintOneLayer(int level) {
		tempStr = "-----------------------SymbTable level :" + level
				+ "  -----------------------\n";
		tempStr += "   ";
		SymbTable t = scope[level];
		while (t != null) { /* 输出标识符名字 */
			tempStr += t.idName + ":   ";
			/* 输出标识符的类型信息，过程标识符除外 */
			AttributeIR Attrib = t.attrIR;

			if (Attrib.type != null) { /* 过程标识符 */
				switch (Attrib.type.kind) {
				case intTy:
					tempStr += "intTy" + "    ";
					break;
				case charTy:
					tempStr += "charTy" + "    ";
					break;
				case arrayTy:
					tempStr += "arrayTy" + "    ";
					break;
				case recordTy:
					tempStr += "recordTy" + "    ";
					break;
				default:
					tempStr += "error  type!" + ":   ";
					break;
				}
			}
			/* 输出标识符的类别，并根据不同类型输出不同其它属性 */
			switch (Attrib.kind) {
			case typeKind:
				tempStr += "typekind" + "    ";
				break;
			case varKind:
				tempStr += "varkind" + "   ";
				tempStr += "Level =" + Attrib.more.varattr.level + "   ";
				tempStr += "Offset =" + Attrib.more.varattr.off + "   ";
				switch (Attrib.more.varattr.access) {
				case dir:
					tempStr += "dir" + "   ";
					break;
				case indir:
					tempStr += "indir" + "   ";
					break;
				default:
					tempStr += "errorkind" + "   ";
					break;
				}
				break;
			case procKind:
				tempStr += "funckind" + "   ";
				tempStr += "Level=" + Attrib.more.procattr.level + "   ";
				tempStr += "Noff=" + Attrib.more.procattr.code + "   ";
				break;
			default:
				tempStr += "error" + "   ";
			}
			tempStr += "\n   ";
			t = t.next;
		}
		return tempStr;
	}



	/* 函数名 NewTable */
	/* 功 能 创建当前空符号表 */
	/* 说 明 遇到新的无声明的标识符时创建新的空符号表，并返回 */
	/* 指向它的指针 */
	public SymbTable NewTable() {
		SymbTable table = new SymbTable();
		table.next = null;
		table.attrIR.kind = IdKind.typeKind;
		table.attrIR.type = null;
		table.next = null;
		table.attrIR.more.varattr.isParam = false;
		return table;
	}
	//符号表的实现 函数一
	//PDF161 创建空符号表:当进入一个新的局部化单位时，调用本子程序
	// 建立一个空符号表table，层数加1，偏移初始化为0。
	public void CreatTable() {

		Level = Level + 1; /* 层数加一 */
		scope[Level] = null; /* 申请了新的一层scope栈的空间 */
		Off = INITOFF; /* 偏移初始化 */
	}

	//2、撤销当前符号表:退出一个局部化区时，调用本子程序。功能是层数减1
	public void DestroyTable() {
		Level = Level - 1;
	}


	//3、登记标识符和属性，返回SymbTable P162（改）
	public SymbTable Enter(String id, AttributeIR attribP, boolean present) {
		present = false;
		SymbTable curentry = scope[Level];
		SymbTable prentry = scope[Level];
		if (scope[Level] == null) {
			curentry = NewTable();
			scope[Level] = curentry;
		} else {
			while (curentry != null && (present == false)) {
				prentry = curentry;
				if (id.equals(curentry.idName)) {
					tempStr += curentry.idName +"    repetition declaration error !";
					System.out.println(tempStr);

					errorMessage +="\n"+tempStr;
					Error = true;
					present = true;
				} else {
					curentry = prentry.next;
				}
			}
			/* 在该层符号表内检查是否有重复定义错误 */
			// 还没有定义
			if (present == false) {
				curentry = NewTable();
				prentry.next = curentry;
			}
		}

		/* 将标识符名和属性登记到表中 */
		curentry.idName = id;
		curentry.attrIR.type = attribP.type;
		curentry.attrIR.kind = attribP.kind;
		// curentry.attrIR.more.procattr.nOff = savedOff;

		switch (attribP.kind) {
		case typeKind:
			break;
		case varKind:
			curentry.attrIR.more.varattr.level = attribP.more.varattr.level;
			curentry.attrIR.more.varattr.off = attribP.more.varattr.off;
			curentry.attrIR.more.varattr.access = attribP.more.varattr.access;
			break;
		case procKind:
			curentry.attrIR.more.procattr.level = attribP.more.procattr.level;
			curentry.attrIR.more.procattr.param = attribP.more.procattr.param;
			break;
		default:
			break;
		}

		return curentry;

	}

	//4、寻找表项地址:对给定的标识符id (id为字符串类型) 求出其表项地址
	public boolean FindEntry(String id, SymbTable entry) {
		boolean present = false; /* 返回值 */
		int lev = Level; /* 临时记录层数的变量 */

		SymbTable findentry = scope[lev];
		/* 并在entry的实参单元中返回表项地址。如果符号表里没 */
		/* 有所找的id项,则返回present为0,则函数中的参数entry */
		/* 赋值为指向该表项地址的指针;否则,present赋值为1。 */
		while (lev != 0 && present == false) {
			while ((findentry != null)) {
				if (findentry.idName.trim().equals(id)) {
					present = true;
					entry.attrIR = findentry.attrIR;
					break;
				}
				/* 如果标识符名字相同，则返回true */
				else
					findentry = findentry.next;
				/* 如果没找到，则继续链表中的查找 */
			}
			if (present != true) {
				lev = lev - 1;
				findentry = scope[lev];
			}
		}/* 如果在本层中没有查到，则转到上一个局部化区域中继续查找 */
		if (present != true) {
			entry = null;
		} else
			entry = findentry;

		return present;
	}

	//5、属性查询：对给定表项地址，求出其属性值，并将其返回给Atrrib的实参单元中
	public AttributeIR FindAttr(SymbTable entry) {
		AttributeIR attrIr = entry.attrIR;
		return attrIr;
	}

	//6、打印生成的符号表
	public void PrintSymbTable() {
		int level = 0;
		String layerStr = "";
		list = new ArrayList<String>();
		while (scope[level + 1] != null) {
			layerStr = PrintOneLayer(level + 1);
			layerStr += "\n";
			System.out.println(layerStr);
			list.add(layerStr);
			level++;
		}

	}
	/* 函数名 NewTy */
	/* 功 能 创建当前空类型内部表示 */
	/* 说 明 参数为类型，函数返回该类型的内部表示的地址 */
	public TypeIR NewTy(TypeKind kind) {
		/* 返回指向该单元的类型内部表示类型指针t */
		TypeIR table = new TypeIR();

		switch (kind) {
		case intTy:
		case charTy:
		case boolTy:
			table.kind = kind;
			table.size = 1;
			break;
		case arrayTy:
			table.kind = TypeKind.arrayTy;
			table.more.arrayattr.indexTy = null;
			table.more.arrayattr.elemTy = null;
			break;
		case recordTy:
			table.kind = TypeKind.recordTy;
			table.more.body = null;
			break;
		}
		return table;
	}

	/* 函数名 NewBody */
	/* 功 能 创建当前空记录类型中域的链表 */
	/* 说 明 函数返回该类型的新的链表的单元地址 */
	public Fieldchain NewBody() {
		Fieldchain Ptr = new Fieldchain();

		Ptr.Next = null;
		Ptr.off = 0;
		Ptr.UnitType = null;
		return Ptr;
	}

	/* 函数名 NewParam */
	/* 功 能 创建当前空形参链表 */
	/* 说 明 函数返回新申请的单元地址 */
	public ParamTable NewParam() {
		ParamTable Ptr = new ParamTable();
		Ptr.entry = null;
		Ptr.next = null;

		return Ptr;
	}

	/* 函数名 ErrorPrompt */
	/* 功 能 错误提示 */
	/* 说 明 在输出文件中显示错误提示，并给全局量Error赋值为1 */
	public void ErrorPrompt(int line, String name, String message) {
		tempStr += ">>>Line:" + line + "    " + name + "    " + message + "  ";
		System.out.println(" ErrorPrompt  " + tempStr);
		Error = true;
	}

	/* 函数名 printTab */
	/* 功 能 打印空格 */
	/* 说 明 在输出文件中打印个数为参数tabnum的空格 */
	public void printTab(int tabnum) {
		for (int i = 0; i < tabnum; i++) {
			tempStr += " ";
		}
	}

	/* 函数名 FindField */
	/* 功 能 查找纪录的域名 */
	/* 说 明 返回值为是否找到标志，变量Entry返回此域名在 */
	/* 纪录的域表中的位置. */
	public boolean FindField(String Id, Fieldchain head, Fieldchain Entry) {
		boolean present = false;
		/* 记录当前节点 */
		Fieldchain currentItem = head;
		/* 从表头开始查找这个标识符，直到找到或到达表尾 */
		while ((currentItem != null) && (present == false)) {
			if (currentItem.id.compareTo(Id) == 0) {
				present = true;
				if (Entry != null) {
					Entry = currentItem;
				}
			} else
				currentItem = currentItem.Next;
		}

		return (present);
	}

	/*    	 语义分析函数实现  		*/


	//1、对语法树进行分析 PDF 164
	public int analyze(TreeNode t) {
		Error = false;
		TreeNode p = null;
		CreatTable(); /* 创建符号表 */
		initialize(); /* 调用类型内部表示初始化函数 */
		/* 语法树的声明节点 */
		p = t.child[1];
		while (p != null) {
			switch (p.getNodeKind()) {
				case TypeK:// 类型声明的结点
					TypeDecPart(p.child[0]);
					break;
				case VarK:// 变量声明的结点
					VarDecPart(p.child[0]);
					break;
				case ProcDecK: // 函数声明结点
					procDecPart(p);
					break;
				default:
					ErrorPrompt(p.getLineno(), "",
							"no this node kind in syntax tree!");
					break;
			}
			p = p.getSibling();/* 循环处理 */
		}

		/* 程序体 */
		t = t.child[2];
		if (t.getNodeKind() == NodeKind.StmLK)
			Body(t);

		/* 撤销符号表 */
		if (Level != -1)
			DestroyTable();
		return StoreNoff;

	}


	//2、初始化整数类型，字符类型，布尔类型的内部表示 PDF 164
	public void initialize() {
		/* scope栈的各层指针设为空 */
		for (int i = 0; i < SCOPESIZE; i++)
			scope[i] = null;
	}

	//3、类型分析：处理语法树的当前结点类型。构造出当前类型的内部表示
	// 并将其地址返回给Ptr类型内部表示的地址
	public TypeIR TypeProcess(TreeNode t, DecKind deckind) {
		TypeIR Ptr = null;
		switch (deckind) {
		case IdK:
			Ptr = nameType(t);
			break; /* 类型为自定义标识符 */
		case ArrayK:
			Ptr = arrayType(t);
			break; /* 类型为数组类型 */
		case RecordK:
			Ptr = recordType(t);
			break; /* 类型为记录类型 */
		case IntegerK:
			//Ptr = intPtr;
			Ptr = NewTy(TypeKind.intTy);;
			break; /* 类型为整数类型 */
		case CharK:
			Ptr = NewTy(TypeKind.charTy);
			break; /* 类型为字符类型 */
		}
		return Ptr;
	}


	//4、来在符号表中寻找已定义的类型名字	PDF 165
	//用寻找表项地址函数FindEntry，返回找到的表项地址
	public TypeIR nameType(TreeNode t) {
		TypeIR Ptr = null;
		SymbTable entry = new SymbTable();
		boolean present = false;

		/* 类型标识符也需要往前层查找 */
		present = FindEntry(t.attr.type_name, entry);
		if (present == true) {
			/* 检查该标识符是否为类型标识符 */
			if (IdKind.typeKind != entry.attrIR.kind)
				ErrorPrompt(t.getLineno(), t.attr.type_name,
						"used before typed!\n");
			else {
				Ptr = entry.attrIR.type;
			}
		} else/* 没有找到该标识符 */
		{
			ErrorPrompt(t.getLineno(), t.attr.type_name,
					"type name is not declared!\n");
		}

		return Ptr;
	}

	//5、处理数组类型的内部表示:类型为数组类型时，需要检查下标是否合法
	public TypeIR arrayType(TreeNode t) {
		TypeIR Ptr0 = null;
		TypeIR Ptr1 = null;
		TypeIR Ptr = null;

		/* 检查数组上界是否小于下界 */
		if (t.attr.arrayattr.low > t.attr.arrayattr.up) {
			ErrorPrompt(t.getLineno(), "", "array subscript error!\n");
			Error = true;
		} else {
			Ptr0 = TypeProcess(t, DecKind.IntegerK);
			/* 调用类型分析函数，处理下标类型 */
			Ptr1 = TypeProcess(t, t.attr.arrayattr.childtype);
			/* 调用类型分析函数，处理元素类型 */
			Ptr = NewTy(TypeKind.arrayTy);
			/* 指向一新创建的类型信息表 */
			Ptr.size = ((t.attr.arrayattr.up) - (t.attr.arrayattr.low) + 1)
					* (Ptr1.size);
			/* 计算本类型长度 */

			/* 填写其他信息 */
			Ptr.more.arrayattr.indexTy = Ptr0;
			Ptr.more.arrayattr.elemTy = Ptr1;
			Ptr.more.arrayattr.low = t.attr.arrayattr.low;
			Ptr.more.arrayattr.up = t.attr.arrayattr.up;
		}
		return Ptr;
	}

	//6、处理记录类型的内部表示：size、recordTy、body
	public TypeIR recordType(TreeNode t) {
		TypeIR Ptr = NewTy(TypeKind.recordTy); /* 新建记录类型的节点 */

		t = t.child[0]; /* 从语法数的儿子节点读取域信息 */

		Fieldchain Ptr2 = null;
		Fieldchain Ptr1 = null;

		Fieldchain body = null;

		while (t != null)
		{

			for (int i = 0; i < t.getIdNum(); i++) {
				/* 申请新的域类型单元结构Ptr2 */
				Ptr2 = NewBody();
				if (body == null)
					body = Ptr1 = Ptr2;

				/* 填写Ptr2的各个成员内容 */
				Ptr2.id = t.name[i];
				Ptr2.UnitType = TypeProcess(t, t.kind.dec);

				Ptr2.Next = null;

				/* 如果Ptr1!=Ptr2，那么将指针后移 */
				if (Ptr2 != Ptr1) {
					/* 计算新申请的单元off */
					Ptr2.off = (Ptr1.off) + (Ptr1.UnitType.size);
					Ptr1.Next = Ptr2;
					Ptr1 = Ptr2;
				}
			}
			/* 处理完同类型的变量后，取语法树的兄弟节点 */
			t = t.getSibling();
		}

		/* 处理记录类型内部结构 */

		/* 取Ptr2的off为最后整个记录的size */
		Ptr.size = Ptr2.off + (Ptr2.UnitType.size);
		/* 将域链链入记录类型的body部分 */
		Ptr.more.body = body;

		return Ptr;
	}


	//7、处理类型声明的语义分析：检查本层类型声明中是否有重复定义错误
	public void TypeDecPart(TreeNode t) {
		boolean present = false;

		AttributeIR attrIr = new AttributeIR();
		SymbTable entry = new SymbTable();
		/* 添属性作为参数 */
		attrIr.kind = IdKind.typeKind;
		/* 遍历语法树的兄弟节点 */
		while (t != null) {
			/* 调用记录属性函数，返回是否重复声明错和入口地址 */
			// present = Enter(t.name[0], attrIr, entry);
			present = FindEntry(t.name[0], entry);
			if (present) {// 已经注册
				ErrorPrompt(t.getLineno(), t.name[0],
						"is repetation declared!\n");
				// System.out.println("TypeDecPart----->已经注册 " + t.name[0]);
				entry = null;
			} else if (!present) {// 尚未注册

				entry.attrIR.type = TypeProcess(t, t.kind.dec);
				entry.attrIR.kind = IdKind.typeKind;
				entry = Enter(t.name[0], entry.attrIR, present);

			}
			t = t.getSibling();
		}
	}


	//7、处理变量声明的语义分析
	public void VarDecPart(TreeNode t) {
		varDecList(t);
	}

	//8、数处理变量声明的语义分析：当遇到变量表识符id时，把id登记到符号表中；
	// 检查重复性定义；遇到类型时，构造其内部表示。
	public void varDecList(TreeNode t) {
		AttributeIR attrIr = new AttributeIR();
		boolean present = false;
		SymbTable entry = new SymbTable();

		while (t != null) {
			attrIr.kind = IdKind.varKind;

			for (int i = 0; i < t.getIdNum(); i++) {
				attrIr.type = TypeProcess(t, t.kind.dec);
				/* 判断识值参还是变参acess(dir,indir) */
				if (t.attr.procattr.paramt == ParamType.varparamType) {
					attrIr.more.varattr.access = AccessKind.indir;
					attrIr.more.varattr.level = Level;
					/* 计算形参的偏移 */
					attrIr.more.varattr.off = Off;
					Off = Off + 1;
				}/* 如果是变参，则偏移加1 */
				else {
					attrIr.more.varattr.access = AccessKind.dir;
					attrIr.more.varattr.level = Level;
					/* 计算值参的偏移 */
					if (attrIr.type != null) {
						attrIr.more.varattr.off = Off;
						Off = Off + (attrIr.type.size);
					}
				}/* 其他情况均为值参，偏移加变量类型的size */

				Enter(t.name[0], attrIr, present);
				if (present) {
					ErrorPrompt(t.getLineno(), t.name[i],
							" is defined repetation!\n");
				} else
					t.table[i] = entry;

			}
			if (t != null)
				t = t.getSibling();
		}


		/* 存储主程序AR的display表的偏移到全局变量 */
		if (Level == 0)
			StoreNoff = Off;
		/* 如果不是主程序，则记录此时偏移，用于下面填写过程信息表的noff信息 */
		else
			savedOff = Off;
	}

	//9、处理过程声明的语义分析：在当前层符号表中填写过程标识符的属性；在新层符号表中填写形参标识符的属性。
	public void procDecPart(TreeNode t) {
		TreeNode p = t;
		SymbTable entry = null;
		entry = HeadProcess(t); /* 处理过程头 */

		t = t.child[1];
		/* 如果过程内部存在声明部分，则处理声明部分 */
		while (t != null) {
			switch (t.getNodeKind()) {
			case TypeK:
				TypeDecPart(t.child[0]);
				break;
			case VarK:
				VarDecPart(t.child[0]);
				break;

			case ProcDecK:
				break;
			default:
				ErrorPrompt(t.getLineno(), "",
						"no this node kind in syntax tree!");
				break;
			}
			if (t.getNodeKind() == NodeKind.ProcDecK)
				break;
			else
				t = t.getSibling();
		}

		entry.attrIR.more.procattr.code = savedOff;
		entry.attrIR.more.procattr.size = entry.attrIR.more.procattr.code
				+ entry.attrIR.more.procattr.level + 1;

		while (t != null) {
			procDecPart(t);
			t = t.getSibling();
		}
		t = p;
		Body(t.child[2]);/* 处理Block */

		/* 函数部分结束，删除进入形参时，新建立的符号表 */
		if (Level != -1)
			DestroyTable();/* 结束当前scope */
	}

	//10、处理函数头的语义分析 ：在当前层符号表中填写函数标识符的属性；
	// 在新层符号表中填写形参标识符的属性。其中过程的大小和代码都需以后回填。
	public SymbTable HeadProcess(TreeNode t) {
		AttributeIR attrIr = new AttributeIR();
		SymbTable entry = new SymbTable();
		/* 填属性 */
		attrIr.kind = IdKind.procKind;
		attrIr.type = null;
		attrIr.more.procattr.level = Level + 1;
		if (t != null) {
			/* 登记函数的符号表项 */
			boolean present = false;
			entry = Enter(t.name[0], attrIr, present);
			t.table[0] = entry;
			/* 处理形参声明表 */
		}
		entry.attrIR.more.procattr.param = ParaDecList(t);
		return entry;
	}


	//11、处理函数头中的参数声明的语义分析 PDF 169
	public ParamTable ParaDecList(TreeNode t) {
		TreeNode p = null;
		ParamTable Ptr1 = null;
		ParamTable Ptr2 = null;
		ParamTable head = null;

		if (t != null) {
			if (t.child[0] != null)
				p = t.child[0]; /* 程序声明节点的第一个儿子节点 */

			CreatTable(); /* 进入新的局部化区 */

			Off = 7; /* 子程序中的变量初始偏移设为8 */

			VarDecPart(p); /* 变量声明部分 */

			SymbTable Ptr0 = scope[Level];

			while (Ptr0 != null) /* 只要不为空，就访问其兄弟节点 */
			{
				/* 构造形参符号表，并使其连接至符号表的param项 */
				Ptr2 = NewParam();
				if (head == null)
					head = Ptr1 = Ptr2;
				// //源文件注释过的// Ptr0.attrIR.More.VarAttr.isParam = true;
				Ptr2.entry = Ptr0;
				Ptr2.next = null;

				if (Ptr2 != Ptr1) {
					Ptr1.next = Ptr2;
					Ptr1 = Ptr2;
				}
				Ptr0 = Ptr0.next;
			}
		}
		return head; /* 返回形参符号表的头指针 */
	}

	//12、执行体部分的语义分析
	public void Body(TreeNode t) {
		if (t.getNodeKind() == NodeKind.StmLK) {
			TreeNode p = t.child[0];
			while (p != null) {
				statement(p); /* 调用语句状态处理函数 */
				p = p.getSibling(); /* 依次读入语法树语句序列的兄弟节点 */
			}
		}
	}

	//13、处理语句序列:根据语法树节点中的kind项判断应该转向处理哪个语句
	public void statement(TreeNode t) {
		switch (t.kind.stmt) {
		case IfK:
			ifstatment(t);
			break;
		case WhileK:
			whilestatement(t);
			break;
		case AssignK:
			assignstatement(t);
			break;
		case ReadK:
			readstatement(t);
			break;
		case WriteK:
			writestatement(t);
			break;
		case CallK:
			callstatement(t);
			break;
		case ReturnK:
			returnstatement(t);
			break;
		default:
			ErrorPrompt(t.getLineno(), "", "statement type error!\n");
			break;
		}
	}

	//14、表达式的分析：表达式语义分析的重点是检查运算分量的类型相容性
	//求表达式的类型。其中参数Ekind用来表示实参是变参还是值参。
	public TypeIR Expr(TreeNode t, AccessKind Ekind) {
		boolean present = false;
		SymbTable entry = new SymbTable();
		TypeIR Eptr0 = null;
		TypeIR Eptr1 = null;
		TypeIR Eptr = null;
		if (t != null)
			switch (t.kind.exp) {
			case ConstK:
				Eptr = TypeProcess(t, DecKind.IntegerK);
				Eptr.kind = TypeKind.intTy;
				if (Ekind != null) {
					Ekind = AccessKind.dir;
				}
				break;
			case VariK:
				/* Var = id的情形 */
				if (t.child[0] == null) {
					/* 在符号表中查找此标识符 */
					present = FindEntry(t.name[0], entry);
					t.table[0] = entry;

					if (present == true) { /* id不是变量 */
						if (FindAttr(entry).kind != IdKind.varKind) {
							ErrorPrompt(t.getLineno(), t.name[0],
									"is not variable error!\n");
							Eptr = null;
						} else {
							Eptr = entry.attrIR.type;
							if (Ekind != null) {/* 间接变量 */
								Ekind = AccessKind.indir;
							}
						}
					} else /* 标识符无声明 */
					{
						ErrorPrompt(t.getLineno(), t.name[0],
								"is not declarations!\n");
					}

				} else/* Var = Var0[E]的情形 */
				{
					if (t.attr.expattr.varkind == VarKind.ArrayMembV)
						Eptr = arrayVar(t);
					else /* Var = Var0.id的情形 */
					if (t.attr.expattr.varkind == VarKind.FieldMembV)
						Eptr = recordVar(t);
				}
				break;
			case OpK:
				/* 递归调用儿子节点 */
				Eptr0 = Expr(t.child[0], null);
				if (Eptr0 == null)
					return null;
				Eptr1 = Expr(t.child[1], null);
				if (Eptr1 == null)
					return null;

				/* 类型判别 */
				if (Eptr0 == Eptr1) {
					switch (t.attr.expattr.op) {
					case LT:
					case EQ:
						Eptr = NewTy(TypeKind.boolTy);
						break; /* 条件表达式 */
					case PLUS:
					case MINUS:
					case TIMES:
					case OVER:
						//Eptr = intPtr;
						Eptr = NewTy(TypeKind.intTy);;
						break; /* 算数表达式 */
					}
					if (Ekind != null) { /* 直接变量 */
						Ekind = AccessKind.dir;
					}
				} else
					ErrorPrompt(t.getLineno(), "", "operator is not compat!\n");
				break;
			}
		// System.out.println("Eptr.kind= " + Eptr.kind);
		return Eptr;
	}

	//15、处理数组变量的下标分析：检查var := var0
	public TypeIR arrayVar(TreeNode t) {
		boolean present = false;
		SymbTable entry = new SymbTable();

		TypeIR Eptr0 = null;
		TypeIR Eptr1 = null;
		TypeIR Eptr = null;

		/* 在符号表中查找此标识符 */

		present = FindEntry(t.name[0], entry);
		t.table[0] = entry;
		/* 找到 */
		if (present != false) {
			/* Var0不是变量 */
			if (FindAttr(entry).kind != IdKind.varKind) {
				ErrorPrompt(t.getLineno(), t.name[0],
						"is not variable error!\n");
				Eptr = null;
			} else /* Var0不是数组类型变量 */
			if (FindAttr(entry).type != null)
				if (FindAttr(entry).type.kind != TypeKind.arrayTy) {
					ErrorPrompt(t.getLineno(), t.name[0],
							"is not array variable error !\n");
					Eptr = null;
				} else {
					/* 检查E的类型是否与下标类型相符 */
					Eptr0 = entry.attrIR.type.more.arrayattr.indexTy;
					if (Eptr0 == null)
						return null;
					Eptr1 = Expr(t.child[0], null);// intPtr;
					if (Eptr1 == null)
						return null;
					if (Eptr0 != Eptr1) {
						ErrorPrompt(t.getLineno(), "",
								"type is not matched with the array member error !\n");
						Eptr = null;
					} else
						Eptr = entry.attrIR.type.more.arrayattr.elemTy;
				}
		} else
			/* 标识符无声明 */
			ErrorPrompt(t.getLineno(), t.name[0], "is not declarations!\n");
		return Eptr;
	}

	//16、记录变量中域的分析：检查var:=var0.id中的var0是不是记录类型变量，id是不是该记录类型中的域成员。
	public TypeIR recordVar(TreeNode t) {
		boolean present = false;
		boolean result = true;
		SymbTable entry = new SymbTable();

		TypeIR Eptr0 = null;
		TypeIR Eptr = null;
		Fieldchain currentP = null;

		/* 在符号表中查找此标识符 */
		present = FindEntry(t.name[0], entry);
		t.table[0] = entry;
		/* 找到 */
		if (present != false) {
			/* Var0不是变量 */
			if (FindAttr(entry).kind != IdKind.varKind) {
				ErrorPrompt(t.getLineno(), t.name[0],
						"is not variable error!\n");
				Eptr = null;
			} else /* Var0不是记录类型变量 */
			if (FindAttr(entry).type.kind != TypeKind.recordTy) {
				ErrorPrompt(t.getLineno(), t.name[0],
						"is not record variable error !\n");
				Eptr = null;
			} else/* 检查id是否是合法域名 */
			{
				Eptr0 = entry.attrIR.type;
				currentP = Eptr0.more.body;
				while (currentP != null && (result == false)) {
					result = t.child[0].name[0].equals(currentP.id);
					/* 如果相等 */
					if (result)
						Eptr = currentP.UnitType;
					else
						currentP = currentP.Next;

				}
				if (currentP == null)
					if (result != false) {
						ErrorPrompt(t.child[0].getLineno(), t.child[0].name[0],
								"is not field type!\n");
						Eptr = null;
					} else /* 如果id是数组变量 */
					if (t.child[0].child[0] != null)
						Eptr = arrayVar(t.child[0]);
			}
		} else
			/* 标识符无声明 */
			ErrorPrompt(t.getLineno(), t.name[0], "is not declarations!\n");
		return Eptr;
	}

	//17、处理赋值语句分析：赋值语句的语义分析的重点是检查赋值号两端分量的类型相容性。
	public void assignstatement(TreeNode t) {
		SymbTable entry = new SymbTable();

		boolean present = false;
		TypeIR ptr = null;
		TypeIR Eptr = null;

		TreeNode child1 = null;
		TreeNode child2 = null;

		child1 = t.child[0];
		child2 = t.child[1];

		if (child1.child[0] == null) {
			/* 在符号表中查找此标识符 */
			present = FindEntry(child1.name[0], entry);

			if (present != false) { /* id不是变量 */
				if (FindAttr(entry).kind != IdKind.varKind) {
					ErrorPrompt(child1.getLineno(), child1.name[0],
							"is not variable error!\n");
					Eptr = null;
				} else {
					Eptr = entry.attrIR.type;
					child1.table[0] = entry;
				}
			} else
				/* 标识符无声明 */
				ErrorPrompt(child1.getLineno(), child1.name[0],
						"is not declarations!\n");
		} else/* Var0[E]的情形 */
		{
			if (child1.attr.expattr.varkind == VarKind.ArrayMembV)
				Eptr = arrayVar(child1);
			else /* Var0.id的情形 */
			if (child1.attr.expattr.varkind == VarKind.FieldMembV)
				Eptr = recordVar(child1);
		}
		if (Eptr != null) {
			if ((t.getNodeKind() == NodeKind.StmtK)
					&& (t.kind.stmt == StmtKind.AssignK)) {
				/* 检查是不是赋值号两侧 类型等价 */
				ptr = Expr(child2, null);
				if (ptr != Eptr)
					ErrorPrompt(t.getLineno(), "", "ass_expression error!\n");
			}
			/* 赋值语句中不能出现函数调用 */
		}
	}
	//18、处理函数调用语句分析 P174
	public void callstatement(TreeNode t) {
		AccessKind Ekind = null;
		boolean present = false;
		SymbTable entry = new SymbTable();

		TreeNode p = null;

		/* 用id检查整个符号表 */
		present = FindEntry(t.child[0].name[0], entry);
		t.child[0].table[0] = entry;

		/* 未查到表示函数无声明 */
		if (present == false) {
			ErrorPrompt(t.getLineno(), t.child[0].name[0],
					"function is not declarationed!\n");
			Error = false;
		} else
		/* id不是函数名 */
		if (FindAttr(entry).kind != IdKind.procKind) {
			ErrorPrompt(t.getLineno(), t.name[0], "is not function name!\n");
		} else/* 形实参匹配 */
		{
			p = t.child[1];
			/* paramP指向形参符号表的表头 */
			ParamTable paramP = FindAttr(entry).more.procattr.param;
			while ((p != null) && (paramP != null)) {
				SymbTable paraEntry = paramP.entry;
				TypeIR Etp = Expr(p, Ekind);/* 实参 */

				/* 参数类别不匹配 */
				FindAttr(paraEntry);
				if ((FindAttr(paraEntry).type) != Etp) {
					ErrorPrompt(p.getLineno(), "", "param type is not match!\n");
					Error = false;
				}
				p = p.getSibling();
				paramP = paramP.next;
			}
			System.out.println("callstatement   end while");
			/* 参数个数不匹配 */
			if ((p != null) || (paramP != null))
				ErrorPrompt(t.child[1].getLineno(), "",
						"param num is not match!\n");
		}
	}

	//19、条件语句分析：检查条件表达式是否为bool类型，处理 then语句序列部分和 else语句序列部分。
	public void ifstatment(TreeNode t) {
		AccessKind Ekind = null;
		TypeIR Etp = Expr(t.child[0], Ekind);
		if (Etp != null)
			/* 处理条件表达式 */
			if (Etp.kind != TypeKind.boolTy)
				ErrorPrompt(t.getLineno(), "", "condition expressrion error!\n"); /* 逻辑表达式错误 */
			else {
				TreeNode p = t.child[1];
				/* 处理then语句序列部分 */
				while (p != null) {
					statement(p);
					p = p.getSibling();
				}
				t = t.child[2]; /* 必有三儿子 */
				/* 处理else语句不分 */
				while (t != null) {
					statement(t);
					t = t.getSibling();
				}
			}
	}

	//20、循环语句分析:检查条件表达式是否为bool类型，处理语句序列部分。
	public void whilestatement(TreeNode t) {
		TypeIR Etp = Expr(t.child[0], null);
		if (Etp != null)
			/* 处理条件表达式部分 */
			if (Etp.kind != TypeKind.boolTy)
				ErrorPrompt(t.getLineno(), "", "condition expression error!\n"); /* 逻辑表达式错误 */
			else {
				t = t.child[1];
				/* 处理循环部分 */
				while (t != null) {
					statement(t);
					t = t.getSibling();
				}
			}
	}

	//21、输入语句分析：检查变量有无声明和是否为变量错误 PDF 175
	public void readstatement(TreeNode t) {
		SymbTable entry = new SymbTable();
		boolean present = false;

		/* 用id检查整个符号表 */
		present = FindEntry(t.name[0], entry);
		t.table[0] = entry;

		/* 未查到表示变量无声明 */
		if (!present)
			ErrorPrompt(t.getLineno(), t.name[0], " is not declarationed!\n");
		else
		/* 不是变量标识符错误 */
		if (entry.attrIR.kind != IdKind.varKind)
			ErrorPrompt(t.getLineno(), t.name[0], "is not var name!\n ");
	}

	//23、（PDF里没有22）输出语句分析:分析输出语句中的表达式是否合法
	public void writestatement(TreeNode t) {
		TypeIR Etp = Expr(t.child[0], null);
		if (Etp != null)
			/* 如果表达式类型为bool类型，报错 */
			if (Etp.kind == TypeKind.boolTy)
				ErrorPrompt(t.getLineno(), "", "exprssion type error!");
	}

	public void returnstatement(TreeNode t) {
		if (Level == 0)
			ErrorPrompt(t.getLineno(), "", "return statement error!");
	}

}