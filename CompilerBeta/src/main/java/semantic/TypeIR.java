package semantic;

import semantic.Type.TypeKind;

//TODO PDF 158
public class TypeIR {

	/* ���͵��ڲ��ṹ���� */

	public int size; /* ������ռ�ռ��С */
	public TypeKind kind;

	public More more = new More();

	public static class More {
		public Fieldchain body; /* ��¼�����е����� */

		public ArrayAttr arrayattr = new ArrayAttr();

		public static class ArrayAttr {
			public TypeIR indexTy;
			public TypeIR elemTy;
			public int low; /* ��¼�������͵��½� */
			public int up; /* ��¼�������͵��Ͻ� */
		}
	}
}
