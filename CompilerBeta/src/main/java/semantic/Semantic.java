package semantic;

import parsing.TreeNode;
import parsing.Type.*;
import semantic.Type.AccessKind;
import semantic.Type.IdKind;
import semantic.Type.TypeKind;

import java.util.ArrayList;
import java.util.List;

public class Semantic {

	/* scopeջ�Ĳ��� */
	private int Level = 0;
	/* ��ͬ��ı���ƫ�� */
	private int Off;
	/* ������display���ƫ�� */
	private int StoreNoff;
	/* �����displayOff */
	private int savedOff;
	/* SCOPESIZEΪ���ű�scopeջ�Ĵ�С */
	private static final int SCOPESIZE = 1000;
	/* ��ʼ�����ű��б�����ƫ�� */
	private static final int INITOFF = 5;
	/* scopeջ */
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


	/* �� �� ��ӡ���ű��һ�� */
	/* ˵ �� �з��ű��ӡ����PrintSymbTable���� */
	public String PrintOneLayer(int level) {
		tempStr = "-----------------------SymbTable level :" + level
				+ "  -----------------------\n";
		tempStr += "   ";
		SymbTable t = scope[level];
		while (t != null) { /* �����ʶ������ */
			tempStr += t.idName + ":   ";
			/* �����ʶ����������Ϣ�����̱�ʶ������ */
			AttributeIR Attrib = t.attrIR;

			if (Attrib.type != null) { /* ���̱�ʶ�� */
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
			/* �����ʶ������𣬲����ݲ�ͬ���������ͬ�������� */
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



	/* ������ NewTable */
	/* �� �� ������ǰ�շ��ű� */
	/* ˵ �� �����µ��������ı�ʶ��ʱ�����µĿշ��ű������� */
	/* ָ������ָ�� */
	public SymbTable NewTable() {
		SymbTable table = new SymbTable();
		table.next = null;
		table.attrIR.kind = IdKind.typeKind;
		table.attrIR.type = null;
		table.next = null;
		table.attrIR.more.varattr.isParam = false;
		return table;
	}
	//���ű��ʵ�� ����һ
	//PDF161 �����շ��ű�:������һ���µľֲ�����λʱ�����ñ��ӳ���
	// ����һ���շ��ű�table��������1��ƫ�Ƴ�ʼ��Ϊ0��
	public void CreatTable() {

		Level = Level + 1; /* ������һ */
		scope[Level] = null; /* �������µ�һ��scopeջ�Ŀռ� */
		Off = INITOFF; /* ƫ�Ƴ�ʼ�� */
	}

	//2��������ǰ���ű�:�˳�һ���ֲ�����ʱ�����ñ��ӳ��򡣹����ǲ�����1
	public void DestroyTable() {
		Level = Level - 1;
	}


	//3���ǼǱ�ʶ�������ԣ�����SymbTable P162���ģ�
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
			/* �ڸò���ű��ڼ���Ƿ����ظ�������� */
			// ��û�ж���
			if (present == false) {
				curentry = NewTable();
				prentry.next = curentry;
			}
		}

		/* ����ʶ���������ԵǼǵ����� */
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

	//4��Ѱ�ұ����ַ:�Ը����ı�ʶ��id (idΪ�ַ�������) ���������ַ
	public boolean FindEntry(String id, SymbTable entry) {
		boolean present = false; /* ����ֵ */
		int lev = Level; /* ��ʱ��¼�����ı��� */

		SymbTable findentry = scope[lev];
		/* ����entry��ʵ�ε�Ԫ�з��ر����ַ��������ű���û */
		/* �����ҵ�id��,�򷵻�presentΪ0,�����еĲ���entry */
		/* ��ֵΪָ��ñ����ַ��ָ��;����,present��ֵΪ1�� */
		while (lev != 0 && present == false) {
			while ((findentry != null)) {
				if (findentry.idName.trim().equals(id)) {
					present = true;
					entry.attrIR = findentry.attrIR;
					break;
				}
				/* �����ʶ��������ͬ���򷵻�true */
				else
					findentry = findentry.next;
				/* ���û�ҵ�������������еĲ��� */
			}
			if (present != true) {
				lev = lev - 1;
				findentry = scope[lev];
			}
		}/* ����ڱ�����û�в鵽����ת����һ���ֲ��������м������� */
		if (present != true) {
			entry = null;
		} else
			entry = findentry;

		return present;
	}

	//5�����Բ�ѯ���Ը��������ַ�����������ֵ�������䷵�ظ�Atrrib��ʵ�ε�Ԫ��
	public AttributeIR FindAttr(SymbTable entry) {
		AttributeIR attrIr = entry.attrIR;
		return attrIr;
	}

	//6����ӡ���ɵķ��ű�
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
	/* ������ NewTy */
	/* �� �� ������ǰ�������ڲ���ʾ */
	/* ˵ �� ����Ϊ���ͣ��������ظ����͵��ڲ���ʾ�ĵ�ַ */
	public TypeIR NewTy(TypeKind kind) {
		/* ����ָ��õ�Ԫ�������ڲ���ʾ����ָ��t */
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

	/* ������ NewBody */
	/* �� �� ������ǰ�ռ�¼������������� */
	/* ˵ �� �������ظ����͵��µ�����ĵ�Ԫ��ַ */
	public Fieldchain NewBody() {
		Fieldchain Ptr = new Fieldchain();

		Ptr.Next = null;
		Ptr.off = 0;
		Ptr.UnitType = null;
		return Ptr;
	}

	/* ������ NewParam */
	/* �� �� ������ǰ���β����� */
	/* ˵ �� ��������������ĵ�Ԫ��ַ */
	public ParamTable NewParam() {
		ParamTable Ptr = new ParamTable();
		Ptr.entry = null;
		Ptr.next = null;

		return Ptr;
	}

	/* ������ ErrorPrompt */
	/* �� �� ������ʾ */
	/* ˵ �� ������ļ�����ʾ������ʾ������ȫ����Error��ֵΪ1 */
	public void ErrorPrompt(int line, String name, String message) {
		tempStr += ">>>Line:" + line + "    " + name + "    " + message + "  ";
		System.out.println(" ErrorPrompt  " + tempStr);
		Error = true;
	}

	/* ������ printTab */
	/* �� �� ��ӡ�ո� */
	/* ˵ �� ������ļ��д�ӡ����Ϊ����tabnum�Ŀո� */
	public void printTab(int tabnum) {
		for (int i = 0; i < tabnum; i++) {
			tempStr += " ";
		}
	}

	/* ������ FindField */
	/* �� �� ���Ҽ�¼������ */
	/* ˵ �� ����ֵΪ�Ƿ��ҵ���־������Entry���ش������� */
	/* ��¼������е�λ��. */
	public boolean FindField(String Id, Fieldchain head, Fieldchain Entry) {
		boolean present = false;
		/* ��¼��ǰ�ڵ� */
		Fieldchain currentItem = head;
		/* �ӱ�ͷ��ʼ���������ʶ����ֱ���ҵ��򵽴��β */
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

	/*    	 �����������ʵ��  		*/


	//1�����﷨�����з��� PDF 164
	public int analyze(TreeNode t) {
		Error = false;
		TreeNode p = null;
		CreatTable(); /* �������ű� */
		initialize(); /* ���������ڲ���ʾ��ʼ������ */
		/* �﷨���������ڵ� */
		p = t.child[1];
		while (p != null) {
			switch (p.getNodeKind()) {
				case TypeK:// ���������Ľ��
					TypeDecPart(p.child[0]);
					break;
				case VarK:// ���������Ľ��
					VarDecPart(p.child[0]);
					break;
				case ProcDecK: // �����������
					procDecPart(p);
					break;
				default:
					ErrorPrompt(p.getLineno(), "",
							"no this node kind in syntax tree!");
					break;
			}
			p = p.getSibling();/* ѭ������ */
		}

		/* ������ */
		t = t.child[2];
		if (t.getNodeKind() == NodeKind.StmLK)
			Body(t);

		/* �������ű� */
		if (Level != -1)
			DestroyTable();
		return StoreNoff;

	}


	//2����ʼ���������ͣ��ַ����ͣ��������͵��ڲ���ʾ PDF 164
	public void initialize() {
		/* scopeջ�ĸ���ָ����Ϊ�� */
		for (int i = 0; i < SCOPESIZE; i++)
			scope[i] = null;
	}

	//3�����ͷ����������﷨���ĵ�ǰ������͡��������ǰ���͵��ڲ���ʾ
	// �������ַ���ظ�Ptr�����ڲ���ʾ�ĵ�ַ
	public TypeIR TypeProcess(TreeNode t, DecKind deckind) {
		TypeIR Ptr = null;
		switch (deckind) {
		case IdK:
			Ptr = nameType(t);
			break; /* ����Ϊ�Զ����ʶ�� */
		case ArrayK:
			Ptr = arrayType(t);
			break; /* ����Ϊ�������� */
		case RecordK:
			Ptr = recordType(t);
			break; /* ����Ϊ��¼���� */
		case IntegerK:
			//Ptr = intPtr;
			Ptr = NewTy(TypeKind.intTy);;
			break; /* ����Ϊ�������� */
		case CharK:
			Ptr = NewTy(TypeKind.charTy);
			break; /* ����Ϊ�ַ����� */
		}
		return Ptr;
	}


	//4�����ڷ��ű���Ѱ���Ѷ������������	PDF 165
	//��Ѱ�ұ����ַ����FindEntry�������ҵ��ı����ַ
	public TypeIR nameType(TreeNode t) {
		TypeIR Ptr = null;
		SymbTable entry = new SymbTable();
		boolean present = false;

		/* ���ͱ�ʶ��Ҳ��Ҫ��ǰ����� */
		present = FindEntry(t.attr.type_name, entry);
		if (present == true) {
			/* ���ñ�ʶ���Ƿ�Ϊ���ͱ�ʶ�� */
			if (IdKind.typeKind != entry.attrIR.kind)
				ErrorPrompt(t.getLineno(), t.attr.type_name,
						"used before typed!\n");
			else {
				Ptr = entry.attrIR.type;
			}
		} else/* û���ҵ��ñ�ʶ�� */
		{
			ErrorPrompt(t.getLineno(), t.attr.type_name,
					"type name is not declared!\n");
		}

		return Ptr;
	}

	//5�������������͵��ڲ���ʾ:����Ϊ��������ʱ����Ҫ����±��Ƿ�Ϸ�
	public TypeIR arrayType(TreeNode t) {
		TypeIR Ptr0 = null;
		TypeIR Ptr1 = null;
		TypeIR Ptr = null;

		/* ��������Ͻ��Ƿ�С���½� */
		if (t.attr.arrayattr.low > t.attr.arrayattr.up) {
			ErrorPrompt(t.getLineno(), "", "array subscript error!\n");
			Error = true;
		} else {
			Ptr0 = TypeProcess(t, DecKind.IntegerK);
			/* �������ͷ��������������±����� */
			Ptr1 = TypeProcess(t, t.attr.arrayattr.childtype);
			/* �������ͷ�������������Ԫ������ */
			Ptr = NewTy(TypeKind.arrayTy);
			/* ָ��һ�´�����������Ϣ�� */
			Ptr.size = ((t.attr.arrayattr.up) - (t.attr.arrayattr.low) + 1)
					* (Ptr1.size);
			/* ���㱾���ͳ��� */

			/* ��д������Ϣ */
			Ptr.more.arrayattr.indexTy = Ptr0;
			Ptr.more.arrayattr.elemTy = Ptr1;
			Ptr.more.arrayattr.low = t.attr.arrayattr.low;
			Ptr.more.arrayattr.up = t.attr.arrayattr.up;
		}
		return Ptr;
	}

	//6�������¼���͵��ڲ���ʾ��size��recordTy��body
	public TypeIR recordType(TreeNode t) {
		TypeIR Ptr = NewTy(TypeKind.recordTy); /* �½���¼���͵Ľڵ� */

		t = t.child[0]; /* ���﷨���Ķ��ӽڵ��ȡ����Ϣ */

		Fieldchain Ptr2 = null;
		Fieldchain Ptr1 = null;

		Fieldchain body = null;

		while (t != null)
		{

			for (int i = 0; i < t.getIdNum(); i++) {
				/* �����µ������͵�Ԫ�ṹPtr2 */
				Ptr2 = NewBody();
				if (body == null)
					body = Ptr1 = Ptr2;

				/* ��дPtr2�ĸ�����Ա���� */
				Ptr2.id = t.name[i];
				Ptr2.UnitType = TypeProcess(t, t.kind.dec);

				Ptr2.Next = null;

				/* ���Ptr1!=Ptr2����ô��ָ����� */
				if (Ptr2 != Ptr1) {
					/* ����������ĵ�Ԫoff */
					Ptr2.off = (Ptr1.off) + (Ptr1.UnitType.size);
					Ptr1.Next = Ptr2;
					Ptr1 = Ptr2;
				}
			}
			/* ������ͬ���͵ı�����ȡ�﷨�����ֵܽڵ� */
			t = t.getSibling();
		}

		/* �����¼�����ڲ��ṹ */

		/* ȡPtr2��offΪ���������¼��size */
		Ptr.size = Ptr2.off + (Ptr2.UnitType.size);
		/* �����������¼���͵�body���� */
		Ptr.more.body = body;

		return Ptr;
	}


	//7���������������������������鱾�������������Ƿ����ظ��������
	public void TypeDecPart(TreeNode t) {
		boolean present = false;

		AttributeIR attrIr = new AttributeIR();
		SymbTable entry = new SymbTable();
		/* ��������Ϊ���� */
		attrIr.kind = IdKind.typeKind;
		/* �����﷨�����ֵܽڵ� */
		while (t != null) {
			/* ���ü�¼���Ժ����������Ƿ��ظ����������ڵ�ַ */
			// present = Enter(t.name[0], attrIr, entry);
			present = FindEntry(t.name[0], entry);
			if (present) {// �Ѿ�ע��
				ErrorPrompt(t.getLineno(), t.name[0],
						"is repetation declared!\n");
				// System.out.println("TypeDecPart----->�Ѿ�ע�� " + t.name[0]);
				entry = null;
			} else if (!present) {// ��δע��

				entry.attrIR.type = TypeProcess(t, t.kind.dec);
				entry.attrIR.kind = IdKind.typeKind;
				entry = Enter(t.name[0], entry.attrIR, present);

			}
			t = t.getSibling();
		}
	}


	//7����������������������
	public void VarDecPart(TreeNode t) {
		varDecList(t);
	}

	//8��������������������������������������ʶ��idʱ����id�Ǽǵ����ű��У�
	// ����ظ��Զ��壻��������ʱ���������ڲ���ʾ��
	public void varDecList(TreeNode t) {
		AttributeIR attrIr = new AttributeIR();
		boolean present = false;
		SymbTable entry = new SymbTable();

		while (t != null) {
			attrIr.kind = IdKind.varKind;

			for (int i = 0; i < t.getIdNum(); i++) {
				attrIr.type = TypeProcess(t, t.kind.dec);
				/* �ж�ʶֵ�λ��Ǳ��acess(dir,indir) */
				if (t.attr.procattr.paramt == ParamType.varparamType) {
					attrIr.more.varattr.access = AccessKind.indir;
					attrIr.more.varattr.level = Level;
					/* �����βε�ƫ�� */
					attrIr.more.varattr.off = Off;
					Off = Off + 1;
				}/* ����Ǳ�Σ���ƫ�Ƽ�1 */
				else {
					attrIr.more.varattr.access = AccessKind.dir;
					attrIr.more.varattr.level = Level;
					/* ����ֵ�ε�ƫ�� */
					if (attrIr.type != null) {
						attrIr.more.varattr.off = Off;
						Off = Off + (attrIr.type.size);
					}
				}/* ���������Ϊֵ�Σ�ƫ�Ƽӱ������͵�size */

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


		/* �洢������AR��display���ƫ�Ƶ�ȫ�ֱ��� */
		if (Level == 0)
			StoreNoff = Off;
		/* ����������������¼��ʱƫ�ƣ�����������д������Ϣ���noff��Ϣ */
		else
			savedOff = Off;
	}

	//9�������������������������ڵ�ǰ����ű�����д���̱�ʶ�������ԣ����²���ű�����д�βα�ʶ�������ԡ�
	public void procDecPart(TreeNode t) {
		TreeNode p = t;
		SymbTable entry = null;
		entry = HeadProcess(t); /* �������ͷ */

		t = t.child[1];
		/* ��������ڲ������������֣������������� */
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
		Body(t.child[2]);/* ����Block */

		/* �������ֽ�����ɾ�������β�ʱ���½����ķ��ű� */
		if (Level != -1)
			DestroyTable();/* ������ǰscope */
	}

	//10��������ͷ��������� ���ڵ�ǰ����ű�����д������ʶ�������ԣ�
	// ���²���ű�����д�βα�ʶ�������ԡ����й��̵Ĵ�С�ʹ��붼���Ժ���
	public SymbTable HeadProcess(TreeNode t) {
		AttributeIR attrIr = new AttributeIR();
		SymbTable entry = new SymbTable();
		/* ������ */
		attrIr.kind = IdKind.procKind;
		attrIr.type = null;
		attrIr.more.procattr.level = Level + 1;
		if (t != null) {
			/* �ǼǺ����ķ��ű��� */
			boolean present = false;
			entry = Enter(t.name[0], attrIr, present);
			t.table[0] = entry;
			/* �����β������� */
		}
		entry.attrIR.more.procattr.param = ParaDecList(t);
		return entry;
	}


	//11��������ͷ�еĲ���������������� PDF 169
	public ParamTable ParaDecList(TreeNode t) {
		TreeNode p = null;
		ParamTable Ptr1 = null;
		ParamTable Ptr2 = null;
		ParamTable head = null;

		if (t != null) {
			if (t.child[0] != null)
				p = t.child[0]; /* ���������ڵ�ĵ�һ�����ӽڵ� */

			CreatTable(); /* �����µľֲ����� */

			Off = 7; /* �ӳ����еı�����ʼƫ����Ϊ8 */

			VarDecPart(p); /* ������������ */

			SymbTable Ptr0 = scope[Level];

			while (Ptr0 != null) /* ֻҪ��Ϊ�գ��ͷ������ֵܽڵ� */
			{
				/* �����βη��ű���ʹ�����������ű��param�� */
				Ptr2 = NewParam();
				if (head == null)
					head = Ptr1 = Ptr2;
				// //Դ�ļ�ע�͹���// Ptr0.attrIR.More.VarAttr.isParam = true;
				Ptr2.entry = Ptr0;
				Ptr2.next = null;

				if (Ptr2 != Ptr1) {
					Ptr1.next = Ptr2;
					Ptr1 = Ptr2;
				}
				Ptr0 = Ptr0.next;
			}
		}
		return head; /* �����βη��ű��ͷָ�� */
	}

	//12��ִ���岿�ֵ��������
	public void Body(TreeNode t) {
		if (t.getNodeKind() == NodeKind.StmLK) {
			TreeNode p = t.child[0];
			while (p != null) {
				statement(p); /* �������״̬������ */
				p = p.getSibling(); /* ���ζ����﷨��������е��ֵܽڵ� */
			}
		}
	}

	//13�������������:�����﷨���ڵ��е�kind���ж�Ӧ��ת�����ĸ����
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

	//14�����ʽ�ķ��������ʽ����������ص��Ǽ���������������������
	//����ʽ�����͡����в���Ekind������ʾʵ���Ǳ�λ���ֵ�Ρ�
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
				/* Var = id������ */
				if (t.child[0] == null) {
					/* �ڷ��ű��в��Ҵ˱�ʶ�� */
					present = FindEntry(t.name[0], entry);
					t.table[0] = entry;

					if (present == true) { /* id���Ǳ��� */
						if (FindAttr(entry).kind != IdKind.varKind) {
							ErrorPrompt(t.getLineno(), t.name[0],
									"is not variable error!\n");
							Eptr = null;
						} else {
							Eptr = entry.attrIR.type;
							if (Ekind != null) {/* ��ӱ��� */
								Ekind = AccessKind.indir;
							}
						}
					} else /* ��ʶ�������� */
					{
						ErrorPrompt(t.getLineno(), t.name[0],
								"is not declarations!\n");
					}

				} else/* Var = Var0[E]������ */
				{
					if (t.attr.expattr.varkind == VarKind.ArrayMembV)
						Eptr = arrayVar(t);
					else /* Var = Var0.id������ */
					if (t.attr.expattr.varkind == VarKind.FieldMembV)
						Eptr = recordVar(t);
				}
				break;
			case OpK:
				/* �ݹ���ö��ӽڵ� */
				Eptr0 = Expr(t.child[0], null);
				if (Eptr0 == null)
					return null;
				Eptr1 = Expr(t.child[1], null);
				if (Eptr1 == null)
					return null;

				/* �����б� */
				if (Eptr0 == Eptr1) {
					switch (t.attr.expattr.op) {
					case LT:
					case EQ:
						Eptr = NewTy(TypeKind.boolTy);
						break; /* �������ʽ */
					case PLUS:
					case MINUS:
					case TIMES:
					case OVER:
						//Eptr = intPtr;
						Eptr = NewTy(TypeKind.intTy);;
						break; /* �������ʽ */
					}
					if (Ekind != null) { /* ֱ�ӱ��� */
						Ekind = AccessKind.dir;
					}
				} else
					ErrorPrompt(t.getLineno(), "", "operator is not compat!\n");
				break;
			}
		// System.out.println("Eptr.kind= " + Eptr.kind);
		return Eptr;
	}

	//15����������������±���������var := var0
	public TypeIR arrayVar(TreeNode t) {
		boolean present = false;
		SymbTable entry = new SymbTable();

		TypeIR Eptr0 = null;
		TypeIR Eptr1 = null;
		TypeIR Eptr = null;

		/* �ڷ��ű��в��Ҵ˱�ʶ�� */

		present = FindEntry(t.name[0], entry);
		t.table[0] = entry;
		/* �ҵ� */
		if (present != false) {
			/* Var0���Ǳ��� */
			if (FindAttr(entry).kind != IdKind.varKind) {
				ErrorPrompt(t.getLineno(), t.name[0],
						"is not variable error!\n");
				Eptr = null;
			} else /* Var0�����������ͱ��� */
			if (FindAttr(entry).type != null)
				if (FindAttr(entry).type.kind != TypeKind.arrayTy) {
					ErrorPrompt(t.getLineno(), t.name[0],
							"is not array variable error !\n");
					Eptr = null;
				} else {
					/* ���E�������Ƿ����±�������� */
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
			/* ��ʶ�������� */
			ErrorPrompt(t.getLineno(), t.name[0], "is not declarations!\n");
		return Eptr;
	}

	//16����¼��������ķ��������var:=var0.id�е�var0�ǲ��Ǽ�¼���ͱ�����id�ǲ��Ǹü�¼�����е����Ա��
	public TypeIR recordVar(TreeNode t) {
		boolean present = false;
		boolean result = true;
		SymbTable entry = new SymbTable();

		TypeIR Eptr0 = null;
		TypeIR Eptr = null;
		Fieldchain currentP = null;

		/* �ڷ��ű��в��Ҵ˱�ʶ�� */
		present = FindEntry(t.name[0], entry);
		t.table[0] = entry;
		/* �ҵ� */
		if (present != false) {
			/* Var0���Ǳ��� */
			if (FindAttr(entry).kind != IdKind.varKind) {
				ErrorPrompt(t.getLineno(), t.name[0],
						"is not variable error!\n");
				Eptr = null;
			} else /* Var0���Ǽ�¼���ͱ��� */
			if (FindAttr(entry).type.kind != TypeKind.recordTy) {
				ErrorPrompt(t.getLineno(), t.name[0],
						"is not record variable error !\n");
				Eptr = null;
			} else/* ���id�Ƿ��ǺϷ����� */
			{
				Eptr0 = entry.attrIR.type;
				currentP = Eptr0.more.body;
				while (currentP != null && (result == false)) {
					result = t.child[0].name[0].equals(currentP.id);
					/* ������ */
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
					} else /* ���id��������� */
					if (t.child[0].child[0] != null)
						Eptr = arrayVar(t.child[0]);
			}
		} else
			/* ��ʶ�������� */
			ErrorPrompt(t.getLineno(), t.name[0], "is not declarations!\n");
		return Eptr;
	}

	//17������ֵ����������ֵ��������������ص��Ǽ�鸳ֵ�����˷��������������ԡ�
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
			/* �ڷ��ű��в��Ҵ˱�ʶ�� */
			present = FindEntry(child1.name[0], entry);

			if (present != false) { /* id���Ǳ��� */
				if (FindAttr(entry).kind != IdKind.varKind) {
					ErrorPrompt(child1.getLineno(), child1.name[0],
							"is not variable error!\n");
					Eptr = null;
				} else {
					Eptr = entry.attrIR.type;
					child1.table[0] = entry;
				}
			} else
				/* ��ʶ�������� */
				ErrorPrompt(child1.getLineno(), child1.name[0],
						"is not declarations!\n");
		} else/* Var0[E]������ */
		{
			if (child1.attr.expattr.varkind == VarKind.ArrayMembV)
				Eptr = arrayVar(child1);
			else /* Var0.id������ */
			if (child1.attr.expattr.varkind == VarKind.FieldMembV)
				Eptr = recordVar(child1);
		}
		if (Eptr != null) {
			if ((t.getNodeKind() == NodeKind.StmtK)
					&& (t.kind.stmt == StmtKind.AssignK)) {
				/* ����ǲ��Ǹ�ֵ������ ���͵ȼ� */
				ptr = Expr(child2, null);
				if (ptr != Eptr)
					ErrorPrompt(t.getLineno(), "", "ass_expression error!\n");
			}
			/* ��ֵ����в��ܳ��ֺ������� */
		}
	}
	//18������������������ P174
	public void callstatement(TreeNode t) {
		AccessKind Ekind = null;
		boolean present = false;
		SymbTable entry = new SymbTable();

		TreeNode p = null;

		/* ��id����������ű� */
		present = FindEntry(t.child[0].name[0], entry);
		t.child[0].table[0] = entry;

		/* δ�鵽��ʾ���������� */
		if (present == false) {
			ErrorPrompt(t.getLineno(), t.child[0].name[0],
					"function is not declarationed!\n");
			Error = false;
		} else
		/* id���Ǻ����� */
		if (FindAttr(entry).kind != IdKind.procKind) {
			ErrorPrompt(t.getLineno(), t.name[0], "is not function name!\n");
		} else/* ��ʵ��ƥ�� */
		{
			p = t.child[1];
			/* paramPָ���βη��ű�ı�ͷ */
			ParamTable paramP = FindAttr(entry).more.procattr.param;
			while ((p != null) && (paramP != null)) {
				SymbTable paraEntry = paramP.entry;
				TypeIR Etp = Expr(p, Ekind);/* ʵ�� */

				/* �������ƥ�� */
				FindAttr(paraEntry);
				if ((FindAttr(paraEntry).type) != Etp) {
					ErrorPrompt(p.getLineno(), "", "param type is not match!\n");
					Error = false;
				}
				p = p.getSibling();
				paramP = paramP.next;
			}
			System.out.println("callstatement   end while");
			/* ����������ƥ�� */
			if ((p != null) || (paramP != null))
				ErrorPrompt(t.child[1].getLineno(), "",
						"param num is not match!\n");
		}
	}

	//19������������������������ʽ�Ƿ�Ϊbool���ͣ����� then������в��ֺ� else������в��֡�
	public void ifstatment(TreeNode t) {
		AccessKind Ekind = null;
		TypeIR Etp = Expr(t.child[0], Ekind);
		if (Etp != null)
			/* �����������ʽ */
			if (Etp.kind != TypeKind.boolTy)
				ErrorPrompt(t.getLineno(), "", "condition expressrion error!\n"); /* �߼����ʽ���� */
			else {
				TreeNode p = t.child[1];
				/* ����then������в��� */
				while (p != null) {
					statement(p);
					p = p.getSibling();
				}
				t = t.child[2]; /* ���������� */
				/* ����else��䲻�� */
				while (t != null) {
					statement(t);
					t = t.getSibling();
				}
			}
	}

	//20��ѭ��������:����������ʽ�Ƿ�Ϊbool���ͣ�����������в��֡�
	public void whilestatement(TreeNode t) {
		TypeIR Etp = Expr(t.child[0], null);
		if (Etp != null)
			/* �����������ʽ���� */
			if (Etp.kind != TypeKind.boolTy)
				ErrorPrompt(t.getLineno(), "", "condition expression error!\n"); /* �߼����ʽ���� */
			else {
				t = t.child[1];
				/* ����ѭ������ */
				while (t != null) {
					statement(t);
					t = t.getSibling();
				}
			}
	}

	//21�������������������������������Ƿ�Ϊ�������� PDF 175
	public void readstatement(TreeNode t) {
		SymbTable entry = new SymbTable();
		boolean present = false;

		/* ��id����������ű� */
		present = FindEntry(t.name[0], entry);
		t.table[0] = entry;

		/* δ�鵽��ʾ���������� */
		if (!present)
			ErrorPrompt(t.getLineno(), t.name[0], " is not declarationed!\n");
		else
		/* ���Ǳ�����ʶ������ */
		if (entry.attrIR.kind != IdKind.varKind)
			ErrorPrompt(t.getLineno(), t.name[0], "is not var name!\n ");
	}

	//23����PDF��û��22�����������:�����������еı��ʽ�Ƿ�Ϸ�
	public void writestatement(TreeNode t) {
		TypeIR Etp = Expr(t.child[0], null);
		if (Etp != null)
			/* ������ʽ����Ϊbool���ͣ����� */
			if (Etp.kind == TypeKind.boolTy)
				ErrorPrompt(t.getLineno(), "", "exprssion type error!");
	}

	public void returnstatement(TreeNode t) {
		if (Level == 0)
			ErrorPrompt(t.getLineno(), "", "return statement error!");
	}

}