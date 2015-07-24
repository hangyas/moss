moss
======
A toy language with a small 8-bit toy vm. 

moss language
-------------------
Currenty the language is very limited, it's only handles byte as variable type. There are no void functions, no stdin/stdout, ot any other io, no heap.
```
func main()
  v = asd(3)
  return 5 + v
end

func asd(a)
  return a * 2
end
```
The compiler is written in scala. Most of the techniques are probably terrible, but it was fun to play around with.

The VM
---------
The VM is very basic too. Byte-by-byte interpreter, using one stack, no registers, no heap.
Currently has problems with multiplication due to bytes want to become ints, I think.

Running
----------
If you want to try it out from some crazy reasons:

 - clone the repo
 - run `make`
 - for tests: `make test`
 - compile & run program: `dist/moss helloworld.moss`