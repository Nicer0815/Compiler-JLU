package parsing;

public class Type {
	// �̲ĵ�59ҳ-60ҳ �﷨������˵��
	// �﷨����־��㣺���ڵ�ProK,����ͷ���PheadK���������ͽڵ�DecK,
	public enum NodeKind {
		ProK, PheadK, DecK, TypeK, VarK, ProcDecK, StmLK, StmtK, ExpK
	}
	// �﷨���ľ���ڵ㣺������㡢���ڵ㡢���ʽ����
	// �������ͣ� ��������ArrayK,�ַ�����CharK, ��������IntegerK,��¼����RecordK,
	public enum DecKind {
		ArrayK, CharK, IntegerK, RecordK, IdK
	}
	 // �������StmtKind���͵�ö�ٶ���: * �ж�����IfK,ѭ������WhileK * ��ֵ����AssignK,������ReadK *
	 // д����WriteK��������������CallK
	public enum StmtKind {
		IfK, WhileK, AssignK, ReadK, WriteK, CallK, ReturnK
	}


	 // ���ʽ����ExpKind���͵�ö�ٶ���: * ��������OpK,��������ConstK,��������VarK
	public enum ExpKind {
		OpK, ConstK, VariK
	}


	// ��������VarKind���͵�ö�ٶ���: * ��ʶ��IdV,�����ԱArrayMembV,���ԱFieldMembV
	public enum VarKind {
		IdV, ArrayMembV, FieldMembV
	}


	// ���ͼ��ExpType���͵�ö�ٶ���: * ��Void,��������Integer,�ַ�����Char
	public enum ExpType {
		Void, Integer, Boolean
	}

	// ��������ParamType���͵�ö�ٶ��壺 * ֵ��valparamType,���varparamType
	public enum ParamType {
		valparamType, varparamType
	}

}
