package semantic;

import semantic.Type.AccessKind;
import semantic.Type.IdKind;

//TODO ���Ӱ�pdf  P156��157
public class AttributeIR {
	/* ��ʶ�������Խṹ���� */
	public TypeIR type; /* ָ���ʶ���������ڲ���ʾ */
	public IdKind kind; /* ��ʶ�������� */

	public More more = new More();

	public static class More { // /*��ʶ���Ĳ�ͬ�����в�ͬ������*/
		public VarAttr varattr = new VarAttr();
		public ProcAttr procattr = new ProcAttr();

		public static class VarAttr/* ������ʶ�������� */
		{
			public AccessKind access; /* �ж��Ǳ�λ���ֵ�� */
			public int level;
			public int off;
			public boolean isParam; /* �ж��ǲ���������ͨ���� */

		}

		public static class ProcAttr// /*��������ʶ��������*/
		{
			public int level; /* �ù��̵Ĳ��� */

			public ParamTable param; /* ������ */

			public int size; /* ���̻��¼�Ĵ�С */

			public int code; /* sp��display���ƫ���� */

		}

	}
}
