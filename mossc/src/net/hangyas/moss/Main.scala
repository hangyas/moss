package net.hangyas.moss

import java.io.{File, FileOutputStream}
import java.nio.file.Files

/**
 * Created by hangyas on 2015-07-15
 */
object Main {
  def main(args: Array[String]) {
    val file = new File(args(0));

    if (!file.exists()){
      println("No such file (" + file.toPath + ")")
      return;
    }

    val code = new String(Files.readAllBytes(file.toPath));[]
    val tokens = LexicalParser.parse(code);
    val ast = AstBuilder.parse(tokens);
    val generator = new CodeGenerator;
    ast.code(generator);

    generator.save(new FileOutputStream(file.getParentFile.getAbsoluteFile + "/" + file.getName .split("\\.")(0) + ".mb"));
  }

}
