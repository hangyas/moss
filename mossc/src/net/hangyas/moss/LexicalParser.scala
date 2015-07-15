package net.hangyas.moss

import java.io.FileOutputStream

import scala.collection.mutable

/**
 * Created by hangyas on 15-5-23
 */
object LexicalParser {
  
  var char: Char = ' ';
  var index = -1;
  var str: String = null;

  def parse(code: String): List[Token] = {
    val r = new mutable.MutableList[Token]();

    str = code;
    next();
    while (index < str.length){
      r += nextToken();
    }

    return r.toList;
  }

  private def next(): Unit = {
    index += 1;
    if (index >= str.length){
      char = 0;
      return;
    }
    char = str.charAt(index);
  }

  private def nextToken(): Token = {

    while (char.isWhitespace && char != 0)
      next();

    //identifier
    if (char.isLetter || char == '_'){
      var identifier: String = "";
      while (char.isLetterOrDigit || char == '_'){
        identifier += char;
        next();
      }

      return identifier match {
        case "func" => TokenFunc();
        case "if" => TokenIf();
        case "while" => TokenWhile();
        case "let" => TokenLet();
        case "end" => TokenEnd();
        case "return" => TokenReturn();
        case _ => TokenIdentifier(identifier);
      }
    }

    //digit
    if (char.isDigit){
      var number = 0;
      while (char.isDigit){
        number = number * 10 + (char - '0');
        next();
      }

      return TokenNumber(number);
    }

    val r = char;
    next();
    return TokenOther(r);
  }

}
