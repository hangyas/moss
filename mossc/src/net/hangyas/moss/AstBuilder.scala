package net.hangyas.moss

import scala.collection.mutable

/**
 * Created by hangyas on 2015-07-13
 */
object AstBuilder {

  var tokens: List[Token] = null;
  var token: Token = null;
  var prevToken: Token = null;

  var currentVars: mutable.MutableList[String] = null;

  private def next(): Token = {
    prevToken = token;

    if (tokens.isEmpty){
      token = null;
      return null;
    }

    token = tokens.head;
    tokens = tokens.tail;
    return token;
  }

  private def next(expected: Char): Token = {
    if (next != TokenOther(expected))
      throw new NotFoundException(expected.toString);
    return token;
  }

  private def peek: Token = {
    return tokens.head;
  }

  private def peek(expected: Char): Boolean = {
    return tokens.head == TokenOther(expected);
  }

  def parse(tokens: List[Token]): AstProgram = {
    AstBuilder.tokens = tokens;
    next();

    val body = mutable.MutableList[AstFunc]();

    while (token.isInstanceOf[TokenFunc]){
      currentVars = new mutable.MutableList[String]();
      body += parseFunc();
    }

    return new AstProgram(body.toList);
  }

  private def parseFunc(): AstFunc = {
    next();//eat func
    val name = token.asInstanceOf[TokenIdentifier].identifier;
    next('('); //eat name

    val args = new mutable.MutableList[String]();

    next(); //eat (
    while (token != TokenOther(')')){
      args += token.asInstanceOf[TokenIdentifier].identifier;
      if (next() == TokenOther(','))
        next(); //eat ','
    }

    next(); //eat ")"

    return new AstFunc(name, args.toList, parseBody());
  }

  /**
   * Eats all tokens until TokenEnd
   * */
  private def parseBody(): List[AstUnit] = {
    val body = new mutable.MutableList[AstUnit]();

    while (token != TokenEnd()){
      val l: AstUnit = token match {
        case _: TokenIf => parseIf();
//        case _: TokenWhile => parseWhile();
        case _: TokenReturn => parseReturn();
        case _: TokenIdentifier if peek('=') => parseAssign();
      }
      body += l;
    }

    next(); //eat end

    return body.toList;
  }

  def parseIf(): AstIf = {
    next(); //eat 'if'
    val condition = parseExpr();
    val body = parseBody();

    return new AstIf(condition, body);
  }

  def parseAssign(): AstAssign = {
    val id = token.asInstanceOf[TokenIdentifier].identifier;
    next('='); //eat variable name
    next();    //eat '='
    val exprl = parseExpr();

    return new AstAssign(id, exprl);
  }

  private val priories = Map(
    '+' -> 1,
    '-' -> 1,
    '*' -> 2,
    '/' -> 2
  );

  /**
   * returns the expression in polish annotation
   * when the next token doesn't make sense in the expression it's handled as end of line
   * function calls handled as highest priority operators so they will be poped from the stack in the right time
   * */
  def parseExpr(): List[AstExpr] = {
    val exprl = mutable.MutableList[AstExpr]();
    val stack = mutable.Stack[AstExpr]();
    //null in the stack means '('

    do {
      token match {
        case TokenNumber(num) => exprl += new AstNumber(num.toByte);
        case TokenIdentifier(name) if peek != TokenOther('(') => exprl += new AstVar(name); // it can't be a function
        case TokenIdentifier(name) if peek == TokenOther('(') => {
          //must be a function call
          // handled as high priority operator
          while (stack.nonEmpty && stack.top != null){
            exprl += stack.top;
            stack.pop();
          }
          stack.push(new AstCall(name));
        }
        case TokenOther('(') => stack.push(null);
        case TokenOther(')') => {
          //pop out everything until the '('
          while (stack.nonEmpty && stack.top != null){
            exprl += stack.top;
            stack.pop();
          }
          stack.pop();
        }
        case TokenOther(',') => {
          while (stack.nonEmpty && stack.top != null) {
            exprl += stack.top;
            stack.pop();
          }
        }
        case TokenOther(name) => {
          val priority = priories(name);
          // pop out higher priority operators
          while (stack.nonEmpty && stack.top != null
            && (stack.top.isInstanceOf[AstCall] || priories(stack.top.asInstanceOf[AstOp].name) > priority)){
            exprl += stack.top;
            stack.pop();
          }
          stack.push(new AstOp(name));
        }
      }
      next();
    }while(!(prevToken == TokenOther(')') && token.isInstanceOf[TokenIdentifier]
      || prevToken.isInstanceOf[TokenIdentifier] && token.isInstanceOf[TokenIdentifier]
      || token.isInstanceOf[TokenKeyWord]));

    exprl ++= stack.toList.filter(_ != null);

    return exprl.toList;
  }

  def parseReturn(): AstReturn = {
    next(); //eat return

    return new AstReturn(parseExpr());
  }

  class NotFoundException(expected: String) extends Exception{
    override def getMessage = "expected: " + expected + " found: " + token;
  }

}
