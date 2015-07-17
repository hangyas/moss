package net.hangyas.moss

/**
 * Created by hangyas on 2015-07-13
 */

abstract class Ast {
  def code(generator: CodeGenerator): Unit
}

class AstProgram(body: List[AstFunc]) extends Ast {
  override def code(generator: CodeGenerator): Unit = {
    val main: AstFunc = body.find(_.name == "main").orNull
    main.code(generator);
    body.filter(_ != main).foreach(_.code(generator));
  }
}

class AstFunc(val name: String, val args: List[String], body: List[AstUnit]) extends Ast {
  override def code(generator: CodeGenerator): Unit ={
    generator.appendFunc(name, args);
    body.foreach(_.code(generator));
  }
}

abstract class AstUnit extends Ast

class AstReturn(expr: List[AstExpr]) extends AstUnit {
  override def code(generator: CodeGenerator): Unit = {
    expr.foreach(_.code(generator));
    generator.append(generator.RET);
  }
}

class AstAssign(left: String, right: List[AstExpr]) extends AstUnit {
  override def code(generator: CodeGenerator): Unit = {
    right.foreach(_.code(generator));
    generator.append(generator.POP);
    generator.append(generator.getLocal(left));
  }
}

class AstIf(condition: List[AstExpr], body: List[AstUnit]) extends AstUnit {
  override def code(generator: CodeGenerator): Unit = {
    val endOfBlock = generator.genLabel();

    condition.foreach(_.code(generator));
    generator.append(generator.JIF);
    generator.appendLabel(endOfBlock);
    body.foreach(_.code(generator));

    generator.assignLabelToNextByte(endOfBlock);
  }
}

class AstWhile(statement: AstExpr, body: List[AstUnit]) extends AstUnit {
  override def code(generator: CodeGenerator): Unit = {
    //TODO
  }
}

abstract class AstExpr extends Ast

class AstNumber(num: Byte) extends AstExpr {
  override def code(generator: CodeGenerator): Unit = {
    generator.append(generator.PUSHC);
    generator.append(generator.getConst(num));
  }
}

class AstVar(name: String) extends AstExpr {
  override def code(generator: CodeGenerator): Unit = {
    generator.append(generator.PUSH);
    generator.append(generator.getLocal(name));
  }
}

class AstOp(val name: Char) extends AstExpr {
  override def code(generator: CodeGenerator): Unit = {
    generator.append(name match {
      case '+' => generator.ADD;
      case '-' => generator.SUB;
      case '*' => generator.MUL;
      case '/' => generator.DIV;
    });
  }
}

class AstCall(name: String) extends AstExpr {
  override def code(generator: CodeGenerator): Unit = {
    generator.append(generator.CALL);
    generator.appendLabel(generator.getFunc(name));
  }
}