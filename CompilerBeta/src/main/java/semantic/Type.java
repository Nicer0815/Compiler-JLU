package semantic;

public class Type {
	/* ��ʶ�������� */
	public enum IdKind {
		typeKind, varKind, procKind
	}

	/* ���������dir��ֱ�ӱ���(ֵ��)��indir��ʾ��ӱ���(���) */
	public enum AccessKind {
		dir, indir
	}

	/* �����ڲ���ʾ */
	// PDF 157  (165)
	public enum TypeKind {
		intTy, charTy, arrayTy, recordTy, boolTy
	}

}
