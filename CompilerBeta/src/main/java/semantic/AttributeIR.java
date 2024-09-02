package semantic;

import semantic.Type.AccessKind;
import semantic.Type.IdKind;

//TODO 电子版pdf  P156、157
public class AttributeIR {
	/* 标识符的属性结构定义 */
	public TypeIR type; /* 指向标识符的类型内部表示 */
	public IdKind kind; /* 标识符的类型 */

	public More more = new More();

	public static class More { // /*标识符的不同类型有不同的属性*/
		public VarAttr varattr = new VarAttr();
		public ProcAttr procattr = new ProcAttr();

		public static class VarAttr/* 变量标识符的属性 */
		{
			public AccessKind access; /* 判断是变参还是值参 */
			public int level;
			public int off;
			public boolean isParam; /* 判断是参数还是普通变量 */

		}

		public static class ProcAttr// /*过程名标识符的属性*/
		{
			public int level; /* 该过程的层数 */

			public ParamTable param; /* 参数表 */

			public int size; /* 过程活动记录的大小 */

			public int code; /* sp到display表的偏移量 */

		}

	}
}
