package parsing;

public class Type {
	// 教材第59页-60页 语法树结点的说明
	// 语法树标志结点：根节点ProK,程序头结点PheadK，声明类型节点DecK,
	public enum NodeKind {
		ProK, PheadK, DecK, TypeK, VarK, ProcDecK, StmLK, StmtK, ExpK
	}
	// 语法树的具体节点：声明结点、语句节点、表达式类型
	// 声明类型： 数组类型ArrayK,字符类型CharK, 整数类型IntegerK,记录类型RecordK,
	public enum DecKind {
		ArrayK, CharK, IntegerK, RecordK, IdK
	}
	 // 语句类型StmtKind类型的枚举定义: * 判断类型IfK,循环类型WhileK * 赋值类型AssignK,读类型ReadK *
	 // 写类型WriteK，函数调用类型CallK
	public enum StmtKind {
		IfK, WhileK, AssignK, ReadK, WriteK, CallK, ReturnK
	}


	 // 表达式类型ExpKind类型的枚举定义: * 操作类型OpK,常数类型ConstK,变量类型VarK
	public enum ExpKind {
		OpK, ConstK, VariK
	}


	// 变量类型VarKind类型的枚举定义: * 标识符IdV,数组成员ArrayMembV,域成员FieldMembV
	public enum VarKind {
		IdV, ArrayMembV, FieldMembV
	}


	// 类型检查ExpType类型的枚举定义: * 空Void,整数类型Integer,字符类型Char
	public enum ExpType {
		Void, Integer, Boolean
	}

	// 参数类型ParamType类型的枚举定义： * 值参valparamType,变参varparamType
	public enum ParamType {
		valparamType, varparamType
	}

}
