package semantic;

import semantic.Type.TypeKind;

//TODO PDF 158
public class TypeIR {

	/* 类型的内部结构定义 */

	public int size; /* 类型所占空间大小 */
	public TypeKind kind;

	public More more = new More();

	public static class More {
		public Fieldchain body; /* 记录类型中的域链 */

		public ArrayAttr arrayattr = new ArrayAttr();

		public static class ArrayAttr {
			public TypeIR indexTy;
			public TypeIR elemTy;
			public int low; /* 记录数组类型的下界 */
			public int up; /* 记录数组类型的上界 */
		}
	}
}
