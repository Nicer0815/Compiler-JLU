package semantic;
//TODO PDF 158
public class Fieldchain {

	/* 域类型单元结构定义 */
	public String id; /* 变量名 */
	public int off; /* 所在记录中的偏移 */
	public TypeIR UnitType; /* 域中成员的类型 */
	public Fieldchain Next;
}
