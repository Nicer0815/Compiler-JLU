package parsing;

import lexical.Token;
import lexical.WordType;

import java.util.Vector;

public class ParsingAnalysis {
    String message = "";				//报错信息
    Vector<Token> tokenVector;	        //tokenList
    int next = 0;						//逻辑指针
    public ParsingAnalysis(Vector<Token> tokenVector) {
        this.tokenVector = tokenVector;
    }
    public ParsingAnalysis(){}

    public void setTokenVector(Vector<Token> tokenVector) {
        this.tokenVector = tokenVector;
    }

    public String getMessage() {
        return message;
    }
    private void wrongMessage(WordType word) {
        message += "line[" + tokenVector.get(next-1).line + "]:After \"" + tokenVector.get(next-1).context+"\" Should be \"" + word.toString() + "\"\n";
    }
    private void wrongString(String string) {
        message += "line[" + tokenVector.get(next-1).line + "]" +string+"\n";
    }
    public TreeNode parse() {
        //TODO 67页 不太一致
        TreeNode root = program();
        if (this.tokenVector.lastElement().type != WordType.EOF) {
            this.wrongMessage(WordType.EOF);
        }
        return root;
    }

    TreeNode program() {
        //对应68页的 t、q、s
        TreeNode head,part,body;
        TreeNode root = new TreeNode();
        root.nodeKind = TreeNode.NodeKind.PROC_K;

        //程序头
        head = programHead();
        if(head!=null) {
            root.child[0] = head;
        }
        else {
            this.wrongString("Program Head Wrong");
        }

        part = declarePart();
        if(part!=null) {
            root.child[1] = part;
        }
        else {
            this.wrongString("Program Declare Wrong");
        }

        body = programBody();
        if(body!=null) {
            root.child[2] = body;
        }
        else {
            message += "" + this.tokenVector.get(next - 1).line + ":program body wrong \n";
        }

        //匹配dot
        if(tokenVector.get(tokenVector.size()-2).type!= WordType.DOT) {
            message += "" + this.tokenVector.get(next - 1).line + ":expect a dot as the end of file \n";
        }
        return root;
    }
    TreeNode programHead() {
        Token currentToken = tokenVector.get(next++);
        //匹配 program 关键字
        if(currentToken.type == WordType.PROGRAM) {
            TreeNode tempRoot = new TreeNode();
            tempRoot.nodeKind = TreeNode.NodeKind.PHEAD_K;
            currentToken = tokenVector.get(next);
            //匹配 程序名称标识符
            if(currentToken.type == WordType.IDENTIFIERS) {
                tempRoot.idString.add(currentToken.context);
                next++;
            }
            else {
                message+=""+currentToken.line+"expect ID\n";
            }
            return tempRoot;
        }
        message += ""+this.tokenVector.get(next-1).line+":expect \"program\"\n";
        next--;
        return null;
    }
    TreeNode declarePart() {
        TreeNode typeP = new TreeNode();
        typeP.nodeKind = TreeNode.NodeKind.TYPE_K;
		/*
		书上第8页产生式4
		DeclarePart ::= TypeDecPart VarDecPart ProcDecPart
		第55页产生式4对应的Predict集合
		{ TYPE, VAR, PROCEDURE, BEGIN }
		*/
        while(tokenVector.get(next).type != WordType.TYPE &&
                tokenVector.get(next).type != WordType.VAR &&
                tokenVector.get(next).type != WordType.PROCEDURE&&
                tokenVector.get(next).type != WordType.BEGIN)
        {
            if(next<tokenVector.size()-1)
                next++;
        }

        TreeNode tp1=typeDec();
        typeP.child[0]=tp1;


        TreeNode varP = new TreeNode();
        varP.nodeKind = TreeNode.NodeKind.VAR_K;
        //varDec只前保证独到var
        while(tokenVector.get(next).type != WordType.VAR &&
                tokenVector.get(next).type != WordType.PROCEDURE&&
                tokenVector.get(next).type != WordType.BEGIN)
        {
            if(next<tokenVector.size()-1) {
                next++;
            }
        }
        TreeNode tp2 = varDec();
        varP.child[0]=tp2;
        typeP.sibling = varP;


        while(tokenVector.get(next).type != WordType.PROCEDURE&&
                tokenVector.get(next).type != WordType.BEGIN)
        {
            if(next<tokenVector.size()-1)
                next++;
        }
        TreeNode s = procDec();
        varP.sibling = s;

        return typeP;
    }
    TreeNode typeDec() {
        //71页图5.9
        TreeNode typeRoot = new TreeNode();
        typeRoot.nodeKind = TreeNode.NodeKind.DEC_K;

        Token typeToken = tokenVector.get(next);
        //第7个产生式，TypeDec 的Predict集合{ TYPE }
        if(typeToken.type== WordType.TYPE) {
            return typeDeclaration();
        }
        //第5个产生式，TypeDecPart 的 Predict集合{VAR，PROCEDURE，BEGIN}
        else if(typeToken.type== WordType.VAR) {
            return null;
        }
        else if(typeToken.type== WordType.PROCEDURE)
            return null;
        else if(typeToken.type== WordType.BEGIN) {
            return null;
        }
        else {
            next++;
            return null;
        }
    }
    TreeNode typeDeclaration() {
        //图5.10
        Token token = tokenVector.get(next++);
        if(token.type == WordType.TYPE) {
            TreeNode t = typeDecList();
            if(t == null) {
                message+=""+this.tokenVector.get(next-1).line+":declaration wrong \n";
            }
            return t;
        }
        //不会执行此分支
        //typeDec() 调用前提保证了token.type == Words.TYPE
        next--;
        return null;
    }
    TreeNode typeDecList() {
        //图5.11 这里的逻辑结构可能有点问题
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.DEC_K;
        if(tokenVector.get(next).type == WordType.IDENTIFIERS) {
            typeId(t);
        }
        else {
            message += ""+tokenVector.get(next).line+":expect ID\n";
        }
        Token token = tokenVector.get(next++);
        if(token.type != WordType.EQUAL) {
            next--;
            message += ""+this.tokenVector.get(next-1).line+":expect \"=\"\n";
        }
        //TypeName()函数
        typeDef(t);

        token = tokenVector.get(next++);
        if(token.type != WordType.SEMICOLON) {
            message += ""+this.tokenVector.get(next-1).line+":expect \";\"\n";
            next--;
        }
        TreeNode p = typeDecMore();
        if(p != null) {
            t.sibling = p;
        }
        return t;
    }
    void typeId(TreeNode t) {
        //typeDecList() 调用保证了 tokenVector.get(next).type == Words.IDENTIFIERS
        Token token = tokenVector.get(next++);
        t.idString.add(token.context);
    }
    void typeDef(TreeNode t) {
        //图5.14
        if(t == null) {
            return;
        }
        Token token = tokenVector.get(next);
        if(token.type== WordType.INTEGER||token.type== WordType.CHAR) {
            baseType(t);
        }
        else if(token.type== WordType.RECORD||token.type== WordType.ARRAY) {
            structureType(t);
        }
        else if(token.type== WordType.IDENTIFIERS) {
            next++;
            //TODO 有点问题
            t.idString.add(token.context);
        }else{
            next++;
        }
    }
    void baseType(TreeNode t) {
        //图5.15
        Token token = tokenVector.get(next++);
        if(token.type== WordType.INTEGER) {
            t.kind = TreeNode.Kind.INTEGER_DEC;
            t.idString.add("Integer");
        }
        else if(token.type== WordType.CHAR) {
            t.kind = TreeNode.Kind.CHAR_DEC;
            t.idString.add("Char");
        }
    }
    void structureType(TreeNode t) {
        Token token = tokenVector.get(next);
        if(token.type == WordType.ARRAY) {
            arrayType(t);
        }else if(token.type == WordType.RECORD) {
            t.kind = TreeNode.Kind.RECORD_DEC;
            recType(t);
        }else{
            next++;
        }
    }
    void arrayType(TreeNode t) {
        //图5.17
        if(tokenVector.get(next++).type== WordType.ARRAY) {
            t.idString.add("Array");
        }
        else {
            next--;
            message += ""+this.tokenVector.get(next-1).line+":expect array\n";
        }
        //array[
        if(tokenVector.get(next++).type!= WordType.LEFT_BRACKET)
        {
            next--;
            message += ""+this.tokenVector.get(next-1).line+":expect [\n";
        }
        if(tokenVector.get(next++).type== WordType.UNSIGNEDNUMBER) {
            t.idString.add(tokenVector.get(next-1).context);
        }
        else {
            next--;
            message += ""+this.tokenVector.get(next-1).line+":expect number\n";
        }
        //TODO 5.17 有问题
        if(tokenVector.get(next++).type== WordType.TWO_DOT) {
            t.idString.add(tokenVector.get(next-1).context);
        }
        else {
            message += ""+this.tokenVector.get(next-1).line+":expect ..\n";
        }
        if(tokenVector.get(next++).type== WordType.UNSIGNEDNUMBER) {
            t.idString.add(tokenVector.get(next-1).context);
        }
        else {
            next--;
            message += ""+this.tokenVector.get(next-1).line+":expect number\n";
        }
        //]
        if(tokenVector.get(next++).type!= WordType.RIGHT_BRACKET) {
            message += ""+this.tokenVector.get(next-1).line+":expect ]\n";
        }
        if(tokenVector.get(next++).type!= WordType.OF) {
            message += ""+this.tokenVector.get(next-1).line+":expect of\n";
        }
        baseType(t);
        t.kind = TreeNode.Kind.ARRAY_DEC;
    }
    void recType(TreeNode t) {
        //图5.18
        if(tokenVector.get(next).type== WordType.RECORD) {
            next++;
        }
        TreeNode p = fieldDecList();
        if(p!=null) {
            t.child[0] = p;
        }
        else {
            message += "" + this.tokenVector.get(next - 1).line + ":record type wrong\n";
        }
        if(tokenVector.get(next++).type!= WordType.END) {
            message += "" + this.tokenVector.get(next - 1).line + ":expect end\n";
            //TODO new
            next--;
        }
    }
    TreeNode fieldDecList() {
        //图5.19
        TreeNode tn = new TreeNode();
        TreeNode p = null;
        tn.nodeKind = TreeNode.NodeKind.DEC_K;
        Token token = tokenVector.get(next);
        if(token.type== WordType.INTEGER||token.type== WordType.CHAR) {
            baseType(tn);
            idList(tn);
            if(tokenVector.get(next++).type!= WordType.SEMICOLON)
            {
                message += ""+this.tokenVector.get(next-1).line+":expect ;\n";
            }
            p = fieldDecMore();
        }
        else if(token.type== WordType.ARRAY) {
            arrayType(tn);
            idList(tn);
            if(tokenVector.get(next++).type!= WordType.SEMICOLON) {
                message += ""+this.tokenVector.get(next-1).line+":expect ;\n";
            }
            p = fieldDecMore();
        }
        if(p!=null) {
            tn.sibling = p;
        }
        return tn;
    }
    TreeNode fieldDecMore()
    {
        Token token = tokenVector.get(next);
        if(token.type== WordType.END) {
            return null;
        }
        else if(token.type == WordType.INTEGER || token.type == WordType.CHAR || token.type == WordType.ARRAY) {
            return fieldDecList();
        }
        return null;
    }
    void idList(TreeNode t) {
        Token token = tokenVector.get(next++);
        if(token.type== WordType.IDENTIFIERS) {
            t.idString.add(token.context);
        }
        idMore(t);
    }
    void idMore(TreeNode t) {
        Token token = tokenVector.get(next);
        if(token.type == WordType.SEMICOLON) {
            return;
        }
        if(token.type == WordType.COMMA) {
            next++;
            idList(t);
        }
    }
    TreeNode typeDecMore() {
        //图5.12
        //处理连续声明 eg: integer v1,v2,v3;
        Token token = tokenVector.get(next);
        if(token.type== WordType.VAR || token.type== WordType.PROCEDURE || token.type== WordType.BEGIN) {
            return null;
        }
        else {
            if(token.type== WordType.IDENTIFIERS)
                return typeDecList();
            else {
                message +=""+this.tokenVector.get(next-1).line+":expect \"ID\"\n";
                next++;
                return typeDecMore();
            }
        }

    }

    /*---declarePart2---*/
    TreeNode varDec() {
        //图5.23
        TreeNode t = null;
        Token token = tokenVector.get(next);
        if(token.type == WordType.PROCEDURE || token.type == WordType.BEGIN) {
            return null;
        }
        else if(token.type == WordType.VAR) {
            t = varDeclaration();
            return t;
        }
        else {
            next++;
            //TODO 与流程图不一致
            return varDeclaration();
        }
    }
    TreeNode varDeclaration() {
        //TODO 如果失败 next--？
        Token token = tokenVector.get(next++);
        if(token.type == WordType.VAR) {
            return varDecList();
        }
        else {
            message += ""+this.tokenVector.get(next-1).line+":expect var\n";
            return null;
        }
    }

    TreeNode varDecList() {
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.VAR_K;
        TreeNode p = null;
        typeDef(t);
        varIdList(t);
        if(tokenVector.get(next++).type!= WordType.SEMICOLON) {
            next--;
            message +=""+this.tokenVector.get(next-1).line+":expect ;\n";
        }
        p = varDecMore();
        if(p!=null) {
            t.sibling = p;
        }
        return t;
    }
    TreeNode varDecMore() {
        //图5.26
        Token token = tokenVector.get(next);
        TreeNode t = null;
        if(token.type == WordType.PROCEDURE || token.type== WordType.BEGIN) {
            return null;
        }
        else if(token.type == WordType.INTEGER ||
                token.type == WordType.CHAR ||
                token.type == WordType.ARRAY ||
                token.type == WordType.RECORD ||
                token.type == WordType.IDENTIFIERS)
        {
            t = varDecList();
        }
        return t;
    }
    void varIdList(TreeNode t) {
        //图5.27
        Token token = tokenVector.get(next++);
        if(token.type == WordType.IDENTIFIERS) {
            t.idString.add(token.context);
        }
        else {
            next--;
            message += ""+this.tokenVector.get(next-1).line+":expect ID\n";
        }
        varIdMore(t);
    }
    void varIdMore(TreeNode t) {
        Token token = tokenVector.get(next);
        if(token.type == WordType.SEMICOLON) {
            return;
        }
        //TODO else 读入下一个单词？
        if(token.type == WordType.COMMA) {
            next++;
            varIdList(t);
        }
    }

    /*---declarePart2---*/

    TreeNode procDec() {
        TreeNode t = null;
        Token token = tokenVector.get(next);
        if(token.type == WordType.BEGIN) {
            return null;
        }
        else if(token.type == WordType.PROCEDURE) {
            t = procDeclaration();
            if(t != null) {
                t.sibling = procDec();
            }
            return  t;
        }
        //TODO else 读入下一个单词 正确
        next++;
        return null;
    }
    TreeNode procDeclaration() {
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.PROC_DEC_K;
        Token token = tokenVector.get(next++);
        if(token.type == WordType.PROCEDURE) {
            if(tokenVector.get(next++).type == WordType.IDENTIFIERS) {
                t.idString.add(tokenVector.get(next - 1).context);
            }
            else {
                message +=""+this.tokenVector.get(next-1).line+":expect id\n";
                next--;
            }

            if(tokenVector.get(next).type != WordType.LEFT_PARENT) {
                message += ""+this.tokenVector.get(next-1).line+":expect (\n";
            }
            paramList(t);
            if(tokenVector.get(next++).type != WordType.RIGHT_PARENT) {
                next--;
                message += ""+this.tokenVector.get(next-1).line+":expect )\n";
            }
            if(tokenVector.get(next++).type != WordType.SEMICOLON) {
                next--;
                message += ""+this.tokenVector.get(next-1).line+":expect ;\n";
            }
            t.child[1] = procDecPart();
            t.child[2] = procBody();
            t.child[2].nodeKind = TreeNode.NodeKind.STM_L_K;
        }
        return t;

    }
    void paramList(TreeNode t) {
        //5.31
        if(tokenVector.get(next++).type != WordType.LEFT_PARENT) {
            next--;
            message += ""+this.tokenVector.get(next-1).line+":expect (\n";
        }
        Token token = tokenVector.get(next);
        if(token.type == WordType.INTEGER ||
                token.type == WordType.CHAR ||
                token.type == WordType.ARRAY ||
                token.type == WordType.RECORD ||
                token.type == WordType.VAR ||
                token.type == WordType.IDENTIFIERS )
        {
            t.child[0] = paramDecList();
        }
        //TODO else 读入下一个单词？
    }

    TreeNode paramDecList() {
        TreeNode t = param();
        if(t == null) {
            return null;
        }
        t.sibling = paramMore();
        return t;

    }
    TreeNode paramMore() {
        TreeNode t = null;
        if(tokenVector.get(next).type == WordType.RIGHT_PARENT) {
            return null;
        }
        if(tokenVector.get(next++).type == WordType.SEMICOLON) {
            t = paramDecList();
            if(t==null){
                message += ""+this.tokenVector.get(next-1).line+":paramDecList Error \n";
            }
            return t;
        }
        else {
            message += ""+this.tokenVector.get(next-1).line+":ecpect )\n";
            return null;
        }
    }

    TreeNode param() {
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.VAR_K;
        Token token = tokenVector.get(next);
        if(token.type == WordType.INTEGER ||
                token.type == WordType.CHAR ||
                token.type == WordType.ARRAY ||
                token.type == WordType.RECORD ||
                token.type == WordType.IDENTIFIERS )
        {
            //TODO t的成员attr.param赋值为valparamType？

            t.idString.add("Value");
            typeDef(t);
            formList(t);
            t.nodeKind = TreeNode.NodeKind.DEC_K;
        }
        else if(token.type == WordType.VAR) {
            t.idString.add("Var");
            next++;
            typeDef(t);
            formList(t);
            t.nodeKind = TreeNode.NodeKind.DEC_K;
        }
        //TODO 读入下一个单词？
        return t;

    }
    void formList(TreeNode t) {
        //5.35
        if(tokenVector.get(next++).type == WordType.IDENTIFIERS) {
            //TODO 将当前单词的sem复制到t的成员attr.name[]中，并将t的成员attr.idnum加一,匹配ID
            t.idString.add(tokenVector.get(next-1).context);
        }
        else {
            message += ""+this.tokenVector.get(next-1).line+":expect id\n";
        }
        fidMore(t);
    }
    void fidMore(TreeNode t) {
        //5.36
        Token token = tokenVector.get(next);
        if(token.type == WordType.SEMICOLON || token.type == WordType.RIGHT_PARENT) {
            return;
        }
        //TODO 有点问题 else
        else if(token.type == WordType.COMMA) {
            next++;
            formList(t);
        }
    }
    TreeNode procDecPart() {
        //5.37
        return declarePart();
    }
    TreeNode procBody() {
        TreeNode t = programBody();
        if(t == null) {
            message += "" + this.tokenVector.get(next - 1).line + ":program body wrong\n";
        }
        return t;
    }

    TreeNode programBody() {
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = TreeNode.NodeKind.STM_L_K;
        int temp = next;
        while(tokenVector.get(temp).type!= WordType.BEGIN) {
            if(temp>=tokenVector.size()-1) {
                message += ""+tokenVector.get(next).line+":expect begin\n";
                break;
            }
            temp++;
        }
        //找到了合法的BEGIN保留字，那就移动next指针吧，指向BEGIN后一个Token
        //如果没找到，说明缺少BEGIN，忽略，按照原来的next指针继续分析
        if(temp<tokenVector.size()-1){
            next = temp + 1;
        }
        treeNode.child[0] = stmList();
        if(tokenVector.get(next++).type != WordType.END) {
            message += ""+this.tokenVector.get(next-1).line+":expect end\n";
            next--;
        }
        return treeNode;
    }
    TreeNode stmList() {
        //5.40
        TreeNode t = stm();
        if(t!=null){
            t.sibling = stmMore();
        }
        return t;
    }
    TreeNode stmMore() {
        TreeNode t = null;
        //TODO 这里逻辑顺序很不一样
        Token token = tokenVector.get(next);

        if(token.type != WordType.SEMICOLON) {
            message += ""+this.tokenVector.get(next-1).line+":expect ;\n";
        }
        else {
            next++;
            token = tokenVector.get(next);
        }

        if(token.type == WordType.END) {
            return null;
        }
        else if(token.type == WordType.ENDWH) {
            next++;
            return null;
        }
        else{
            t = stmList();
            return t;
        }
    }

    TreeNode stm() {
        //5.42
        Token token = tokenVector.get(next);
        switch(token.type) {
            case IF:
                return conditionalStm();
            case WHILE:
                return loopStm();
            case RETURN:
                return returnStm();
            case READ:
                return inputStm();
            case WRITE:
                return outputStm();
            case IDENTIFIERS:
                return assCall();
        }
        return null;
    }
    TreeNode assCall() {
        //5.43
        //TODO 疑问？
        TreeNode first = exp(),tmp=null;
        if(next>=tokenVector.size()-1) {
            return first;
        }
        Token token = tokenVector.get(next++);
        if(token.type == WordType.COLON_EQUAL) {
            tmp =  assignmentRest();
            if(tmp!=null) {
                tmp.child[0] = first;
            }
            return tmp;
        }
        else if(token.type == WordType.LEFT_PARENT) {
            tmp = callStmRest();
            if(tmp!=null) {
                tmp.idString = first.idString;
            }
            return tmp;
        }
        next--;
        return null;
    }
    TreeNode assignmentRest() {
        //5.44
        //TODO 疑问？
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.STMT_K;
        t.idString.add("Assign");
        t.child[0] = null;
        t.child[1] = exp();
        return t;

    }
    TreeNode conditionalStm() {
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.STMT_K;
        t.idString.add("Conditional");
        Token token = tokenVector.get(next++);
        if(token.type == WordType.IF) {
            t.child[0] = exp();
        }else {
            message += ""+this.tokenVector.get(next-1).line+":expect if\n";
        }
        token = tokenVector.get(next++);
        if(token.type == WordType.THEN) {
            t.child[1] = stm();
        }else {

            message += ""+this.tokenVector.get(next-1).line+":expect then\n";
            next--;
            t.child[1] = stm();
        }
        token = tokenVector.get(next++);
        if(token.type == WordType.ELSE) {
            t.child[2] = stm();
        }else {
            message += ""+this.tokenVector.get(next-1).line+":expect else\n";
            next--;
            t.child[2] = stm();
        }
        token = tokenVector.get(next++);
        if(token.type != WordType.FI) {
            message += ""+this.tokenVector.get(next-1).line+":expect fi\n";
            next--;
        }
        return t;

    }
    TreeNode loopStm() {
        //5.46
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.STMT_K;
        //TODO ?
        t.idString.add("While");
        Token token = tokenVector.get(next++);
        if(token.type == WordType.WHILE) {
            t.child[0] = exp();
        }else {
            message += ""+this.tokenVector.get(next-1).line+":expect while\n";
            //TODO ? next--?
        }

        token = tokenVector.get(next++);
        if(token.type == WordType.DO) {
            t.child[1] = stmList();
        }else {
            message += ""+this.tokenVector.get(next-1).line+":expect do\n";
            next--;
            t.child[1] = stmList();
        }
        return t;
    }
    TreeNode inputStm() {
        //5.47
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.STMT_K;
        t.idString.add("Read");
        Token token = tokenVector.get(next++);
        if(token.type != WordType.READ) {
            message += ""+this.tokenVector.get(next-1).line+":expect read\n";
        }
        token = tokenVector.get(next++);
        if(token.type != WordType.LEFT_PARENT) {
            message += ""+this.tokenVector.get(next-1).line+":expect (\n";
            next--;
        }

        token = tokenVector.get(next++);
        if(token.type != WordType.IDENTIFIERS) {
            message += ""+this.tokenVector.get(next-1).line+":expect id\n";
        }
        else {
            t.idString.add(token.context);
        }

        token = tokenVector.get(next++);
        if(token.type != WordType.RIGHT_PARENT) {
            message += ""+this.tokenVector.get(next-1).line+":expect )\n";
            next--;
        }
        return t;
    }
    TreeNode outputStm() {
        //5.48
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.STMT_K;
        t.idString.add("Write");
        Token token = tokenVector.get(next++);
        if(token.type != WordType.WRITE) {
            message += ""+this.tokenVector.get(next-1).line+":expect write\n";
        }
        token = tokenVector.get(next++);
        if(token.type != WordType.LEFT_PARENT) {
            message += ""+this.tokenVector.get(next-1).line+":expect (\n";
            next--;
        }

        t.child[0] = exp();
        token = tokenVector.get(next++);
        if(token.type != WordType.RIGHT_PARENT) {
            message +=""+this.tokenVector.get(next-1).line+":expect )\n";
            next--;
        }
        return t;
    }
    TreeNode returnStm() {
        //5.49
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.STMT_K;
        t.idString.add("Return");
        Token token = tokenVector.get(next++);
        if(token.type != WordType.RETURN) {
            message += ""+this.tokenVector.get(next-1).line+":expect return\n";
        }
        return t;
    }
    TreeNode callStmRest() {
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.STMT_K;
        Token token = tokenVector.get(next-1);

        if(token.type != WordType.LEFT_PARENT) {
            message +=""+this.tokenVector.get(next-1).line+":expect (\n";
        }

        t.child[0] = actParamList();
        if(t.child[0]!=null) {
            t.child[0].nodeKind = TreeNode.NodeKind.DEC_K;
        }
        token = tokenVector.get(next++);
        if(token.type != WordType.RIGHT_PARENT) {
            message += ""+this.tokenVector.get(next-1).line+":expect )\n";
            next--;
        }
        return t;
    }
    TreeNode actParamList() {
        //5.51
        TreeNode t = null;
        Token token = tokenVector.get(next);
        if(token.type == WordType.RIGHT_PARENT) {
            return null;
        }
        else if(token.type == WordType.IDENTIFIERS ||
                token.type == WordType.UNSIGNEDNUMBER) {
            //next--;
            t = exp();
            if(t != null) {
                t.sibling = actParamMore();
                return t;
            }
        }
        return t;
    }
    TreeNode actParamMore() {
        //5.52
        Token token = tokenVector.get(next++);
        if(token.type == WordType.RIGHT_PARENT) {
            next--;
            return null;
        }
        else if(token.type == WordType.COMMA) {
            return actParamList();
        }
        return null;
    }
    TreeNode exp() {
        //5.53
        TreeNode t = simpleExp();
        if(next>=tokenVector.size()-1) {
            return t;
        }
        Token token = tokenVector.get(next++);
        //TODO if结构有问题
        if(token.type == WordType.EQUAL || token.type == WordType.LESS) {
            TreeNode p = new TreeNode();
            p.nodeKind = TreeNode.NodeKind.STMT_K;
            if(token.type == WordType.COLON_EQUAL) {
                p.idString.add("Assign");
            }
            else {
                p.idString.add("Less");
            }
            p.child[0] = t;
            t = p;
        }
        else if(token.type== WordType.RIGHT_BRACKET) {
            return t;
        }
        else {
            next--;
            return t;
        }
        if(t!=null) {
            t.child[1] = simpleExp();
        }
        return t;
    }
    TreeNode simpleExp() {
        //5.54
        TreeNode t = term();
        if(next>=tokenVector.size()-1) {
            return t;
        }
        Token token = tokenVector.get(next++);
        //TODO 改名为PLUS & MINUS？
        if(token.type == WordType.ADD || token.type == WordType.SUB) {
            TreeNode p = new TreeNode();
            p.nodeKind = TreeNode.NodeKind.EXP_K;
            if(token.type == WordType.ADD) {
                p.idString.add("ADD");
            }
            else {
                p.idString.add("SUB");
            }
            p.child[0] = t;
            //TODO 项处理函数 term()？
            p.child[1] = simpleExp();
            return p;
        }
        next--;
        return t;

    }
    TreeNode term() {
        //5.55
        TreeNode t = factor();
        if(next>=tokenVector.size()-1) {
            return t;
        }
        Token token = tokenVector.get(next++);
        if(token.type == WordType.MUL || token.type == WordType.DIV) {
            TreeNode p = new TreeNode();
            p.nodeKind = TreeNode.NodeKind.EXP_K;
            if(token.type == WordType.MUL) {
                p.idString.add("MUL");
            }
            else {
                p.idString.add("DIV");
            }
            p.child[0]=t;
            //TODO 因子处理函数 factor()？
            p.child[1] = exp();
            t = p;
            return t;
        }
        next--;
        return t;

    }

    TreeNode factor() {
        //5.56
        TreeNode tn = null;
        switch(tokenVector.get(next).type) {
            case UNSIGNEDNUMBER: {
                TreeNode t = new TreeNode();
                t.nodeKind = TreeNode.NodeKind.EXP_K;
                t.idString.add(tokenVector.get(next++).context);
                return t;
            }
            case IDENTIFIERS:
                return variable();
            case LEFT_PARENT: {
        		next++;
                tn = exp();
                break;
            }
            case RIGHT_BRACKET: {
                next++;
                return tn;
            }
            default: {
                message += ""+this.tokenVector.get(next-1).line+tokenVector.get(next).type.name()+"   wrong \n";
            }
        }
        return tn;

    }
    TreeNode variable() {
        //电子版PDF 111页
        TreeNode tn = new TreeNode();
        tn.nodeKind = TreeNode.NodeKind.EXP_K;
        if(tokenVector.get(next++).type == WordType.IDENTIFIERS) {
            tn.idString.add(tokenVector.get(next-1).context);
            variableMore(tn);
            return tn;
        }
        else {
            next--;
            return null;
        }

    }
    void variableMore(TreeNode t) {
        //TODO 电子版PDF 112 很不一致
        Token token = tokenVector.get(next);
        if(token.type == WordType.LEFT_BRACKET) {
            next++;
            t.child[0]=variable();
            if(tokenVector.get(next).type== WordType.RIGHT_BRACKET) {
                next++;
            }
            else{
                message += ""+tokenVector.get(next).line + "expect ]\n";
            }
        }
        else if(token.type == WordType.DOT) {
            if(next>=tokenVector.size()-1) {
                return;
            }
            next++;
            t.child[0] = fieldVar();
        }
        else if(token.type == WordType.COLON_EQUAL) {
            return;
        }
    }
    TreeNode fieldVar() {
        TreeNode t = new TreeNode();
        t.nodeKind = TreeNode.NodeKind.EXP_K;
        if(tokenVector.get(next++).type == WordType.IDENTIFIERS) {
            t.idString.add(tokenVector.get(next-1).context);
            fielderMore(t);
        }
        else {
            message += ""+this.tokenVector.get(next-1).line+":expect Id\n";
        }
        return t;
    }
    void fielderMore(TreeNode tn) {
        //TODO 不知道怎么写 114PDF
        Token token = tokenVector.get(next);
        if(token.type == WordType.COLON_EQUAL) {
            //TODO ?

        }
        else if(token.type == WordType.LEFT_BRACES) {

            tn.sibling = exp();

        }
    }
}
