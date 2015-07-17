# ifndef MOSSVM_H
# define MOSSVM_H

//instruction set

# define PUSH     0x01
# define PUSHC    0x02
# define POP      0x03

# define CALL     0x04
# define RET      0x05

# define JMP      0x06
# define JIT      0x07
# define JIF      0x08

# define AND      0x11
# define OR       0x12
# define NOT      0x13

# define ADD      0x14
# define SUB      0x15
# define MUL      0x16
# define DIV      0x17

# define PRNT     0xf1


/**
 * representation of the machines state for stacking, use virtual addresses
 * */
struct State{
  unsigned char prev;		//previoud state's address
  unsigned char mp;		//Memory Pointer: first address of the frame's memory
  				//also the sp of the previous frame (after the return)
  unsigned char ip;		//Instruction Pointer
  unsigned char args_size;
  unsigned char locals_size;
};

int stack_size;
unsigned char constants_size;
unsigned char program_size;

char *stack;
char *constants;
unsigned char *program;

unsigned char *ip;
char *sp;
char *mp;
struct State *state;

void init();
void run();

void call(unsigned char *);
void ret();

# endif
