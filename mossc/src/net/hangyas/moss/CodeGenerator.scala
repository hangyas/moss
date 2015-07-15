package net.hangyas.moss

import java.io.OutputStream

import scala.collection.mutable;

/**
 * Created by hangyas on 2015-07-13
 */
class CodeGenerator {

  // positions of labels insterted in the code
  private val labelPositions = mutable.MutableList[Int]();
  private val labels = mutable.Map[Int, Byte]();
  private var labelCount: Byte = 0;

  def genLabel(): Label = {
    val r = new Label(labelCount);
    labelCount = (labelCount + 1).toByte;
    return r;
  }

  def assignLabelToNextByte(label: Label): Unit = labels += ((label.id, (codeBuffer.size - 1).toByte));

  private val codeBuffer = mutable.Buffer[Byte]();

  // key: constant values, value: constant's address
  private val constants = mutable.Map[Byte, Byte]();
  def getConst(num: Byte): Byte = constants.getOrElseUpdate(num, constants.size.toByte);

  // key: name, value: label
  private val functions = mutable.Map[String, Label]();
  def getFunc(name: String): Label = functions.getOrElseUpdate(name, {
    val label = genLabel();
    label;
  });

  // key: name, value: address
  private val locals = mutable.Map[String, Byte]();
  def getLocal(name: String): Byte = locals.getOrElseUpdate(name, {
    val local: Byte = codeBuffer(currentFuncAddr);
    codeBuffer.update(currentFuncAddr, (local + 1).toByte);
    local;
  });

  private var currentFuncAddr: Byte = -1;

  /**
   * build a function header
   *    localsize   :1 byte
   *    argssize    :1 byte
   * */
  def appendFunc(name: String, args: List[String]) {
    currentFuncAddr = codeBuffer.size.toByte;

    //save real address for labels
    assignLabelToNextByte(getFunc(name));

    //clear previous locals
    locals.clear()

    //append header
    codeBuffer += 0.toByte;
    codeBuffer += args.size.toByte;
  }

  def append(byte: Byte) {
    codeBuffer += byte;
  }

  def appendLabel(label: Label) {
    labelPositions += codeBuffer.size;
    codeBuffer += label.id
  }

  /**
   * change all label to the real address
   * */
  private def linkLabels(): Unit ={
    labelPositions.foreach(pos => codeBuffer.update(pos, labels(codeBuffer(pos))));
  }

  /**
   * moss file:
   *    magic number "fi" : 2 bytes
   *    constants_size    : 1 bytes
   *    program_size      : 1 bytes
   *    constants         : constants_size bytes
   *    program           : program_size bytes
   * */
  def save(out: OutputStream): Unit ={
    linkLabels();

    //"fi"
    out.write(Array(0x66.toByte, 0x69.toByte));

    out.write(Array(constants.size.toByte, codeBuffer.size.toByte));

    //sort by value, then write out as expected by the code
    out.write(constants.toSeq.sortBy(_._2).map(_._1).toArray);

    out.write(codeBuffer.toArray);
  }

  val PUSH:  Byte = 0x01;
  val PUSHC: Byte = 0x02;
  val POP:   Byte = 0x03;

  val CALL:  Byte = 0x04;
  val RET:   Byte = 0x05;

  val JMP:   Byte = 0x06;
  val JIT:   Byte = 0x07;
  val JIF:   Byte = 0x08;

  val AND:   Byte = 0x11;
  val OR:    Byte = 0x12;
  val NOT:   Byte = 0x13;

  val ADD:   Byte = 0x14;
  val SUB:   Byte = 0x15;
  val MUL:   Byte = 0x16;
  val DIV:   Byte = 0x17;
}