package net.hangyas.moss

/**
 * temporary representation of addresses, which can be calculated only at the end (functions)
 * must be appended with appendLabel
 * at the end of the code generation they will be replaced with the real addresses
 * */
class Label(val id: Byte) extends AnyVal