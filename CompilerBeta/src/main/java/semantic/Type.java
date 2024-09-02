package semantic;

public class Type {
	/* 标识符的类型 */
	public enum IdKind {
		typeKind, varKind, procKind
	}

	/* 变量的类别。dir表直接变量(值参)，indir表示间接变量(变参) */
	public enum AccessKind {
		dir, indir
	}

	/* 类型内部表示 */
	// PDF 157  (165)
	public enum TypeKind {
		intTy, charTy, arrayTy, recordTy, boolTy
	}

}
