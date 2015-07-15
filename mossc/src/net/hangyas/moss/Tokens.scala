package net.hangyas.moss

/**
 * Created by hangyas on 2015-05-25
 */

abstract class Token()
abstract class TokenKeyWord() extends Token
case class TokenFunc() extends TokenKeyWord
case class TokenIf() extends TokenKeyWord
case class TokenWhile() extends TokenKeyWord
case class TokenReturn() extends TokenKeyWord
case class TokenLet() extends TokenKeyWord
case class TokenEnd() extends TokenKeyWord
case class TokenIdentifier(identifier: String) extends Token
case class TokenNumber(value: Int) extends Token
case class TokenOther(char: Char) extends Token