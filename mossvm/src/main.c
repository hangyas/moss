# include "mossVM.h"

# include <stdio.h>
# include <stdlib.h>

/**
 * moss file:
 *    magic number "fi" : 2 bytes
 *    constants_size    : 1 bytes
 *    program_size      : 1 bytes
 *    constants         : constants_size bytes
 *    program           : program_size bytes
 * */
int read_moss_file(char * filename){
  FILE *fileptr = fopen(filename, "rb");

  //check magic number
  char magic[2];
  fread(magic, 2, 1, fileptr);
  if (magic[0] != 0x66 || magic[1] != 0x69){
    printf("no moss file (missing magic number)\n");
    fclose(fileptr);
    return 1;
  }

  //read content
  fread(&constants_size, 1, 1, fileptr);
  fread(&program_size, 1, 1, fileptr);

  constants = malloc(constants_size);
  fread(constants, constants_size, 1, fileptr);
  program = malloc(program_size);
  fread(program, program_size, 1, fileptr);

  fflush(stdout);

  fclose(fileptr);
  return 0;
}

int main(int argc, char** argv){
  if (argc == 1){
    printf("usage: mossvm FILE\n");
    return 1;
  }

  read_moss_file(argv[1]);

  init();
  run();

  printf("%i\n", *sp);
}
