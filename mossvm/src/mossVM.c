# include "mossVM.h"

# include <stdio.h>
# include <stdlib.h>

// helper functions

void cp(char *in, char *out, int size){
  for (int i = 0; i < size; ++i)
    out[i] = in[i];
}

void _printm(){
  for (int i = 0; stack + i <= sp; ++i)
    printf("%d ", (int)stack[i]);
  printf("\n");
  fflush(stdout);
}

void init(){
  stack = calloc(0x100, 1);

  //setup initial frame

  ip = program;
  sp = stack + sizeof(struct State) - 1;
  mp = stack; //only in the initial frame

  state = (struct State *)stack;
  state->mp = 0;
}

void call(unsigned char * fn){

  //save current state
  state->ip = (unsigned char)(ip - program);
  struct State *old = state;

  //setup new frame
  struct State *next = (struct State *)(sp + old->locals_size + old->args_size + 1);
  next->prev = (unsigned char)((char *)state - stack);
  next->locals_size = *fn;
  next->args_size = *(++fn);

  //switch the the new frame
  state = next;
  mp = sp - next->args_size + 1;
  state->mp = (unsigned char)(mp - stack);
  sp = ((char *)state) + next->locals_size + next->args_size + sizeof(struct State) - 1;
  ip = fn;

//  printf("call %i \n", fn - program - 1);
//  _printm();
}

void ret(){
  unsigned char *result = sp; //save result's position

  //load previous state
  state = (struct State *)(stack + state->prev);

  ip = program + state->ip;
  sp = mp;
  mp = stack + state->mp;

  *sp = *result; //put result to the top of the stack
}

void run(){
  call(ip);

  char * a;

  while(mp != stack){
    ++ip;

    switch(*ip){

      case PUSH:
      	*(++sp) = mp[*(++ip)];
      break;

      case PUSHC:
      	*(++sp) = constants[*(++ip)];
      break;

      case POP:
      	mp[*(++ip)] = *sp;
        --sp;
      break;

      case CALL:
      	call(program + *(++ip));
      break;

      case RET:
        ret();
      break;

      case JMP:
        ++ip;
      	ip = program + (*ip);
      break;

      case JIT:
      	++ip;
      	if (*sp)
      	  ip = program + (*ip);
      break;

      case JIF:
      	++ip;
      	if (!(*sp))
      	  ip = program + (*ip);
      break;

      case AND:
	      a = sp;
        sp--;
        *sp = *sp & *a;
      break;

      case OR:
 	      a = sp;
        sp--;
        *sp = *sp | *a;
      break;

      case NOT:

      break;

      case ADD:
	      a = sp;
        --sp;
        *sp = *sp + *a;
      break;

      case SUB:
        a = sp;
        sp--;
        *sp = *sp - *a;
      break;

      case MUL:
	      a = sp;
        sp--;
        *sp = *sp * *a;
      break;

      case DIV:
	      a = sp;
        sp--;
        *sp = *sp / *a;
      break;

      case PRNT:
        printf("%d\n", (int)(*sp));
        fflush(stdout);
      break;

      default:
        printf("unexpected byte [%i]", ip - program);
        return;
    }
  }
}

