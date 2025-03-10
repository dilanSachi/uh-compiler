## Simple Compiler
This is a simple compiler written for the University of Helsinki [Compilers course](https://hy-compilers.github.io/spring-2025/project/). Compiler is written using Java 23.

[![Build](https://github.com/dilanSachi/uh-compiler/actions/workflows/pull_request.yaml/badge.svg)](https://github.com/dilanSachi/uh-compiler/actions/workflows/pull_request.yaml)
[![Code Coverage](https://codecov.io/gh/dilanSachi/uh-compiler/branch/master/graph/badge.svg)](https://codecov.io/gh/dilanSachi/uh-compiler)

### Sample
A simple program written in this language would be as follows.
```
var n: Int = read_int();
print_int(n);
while n > 1 do {
    if n % 2 == 0 then {
        n = n / 2;
    } else {
        n = 3*n + 1;
    }
    print_int(n);
}
```

### Language Spec
**Note** Copied from the course page.

An expression is defined recursively as follows, where `E`, `E1`, `E2`, `…` `En` represent some other arbitrary expression.

* Integer literal: a positive whole number.
* Negative numbers should be composed of token `-` followed by an integer literal token.
* Boolean literal: either `true` or `false`.
* Identifier: a word consisting of letters, underscores or digits, but the first character must not be a digit.
* Unary operator: either `-E` or `not E`.
* Binary operator: `E1 op E2` where `op` is one of the following: `+`, `-`, `*`, `/`, `%`, `==`, `!=`, `<`, `<=`, `>`, `>=`, `and`, `or`, `=`.
  * Operator `=` is right-associative.
  * All other operators are left-associative.
  * Precedences are defined below.
* Parentheses: `(E)`, used to override precedence.
* Block: `{ E1; E2; ...; En }` or `{ E1; E2; ...; En; }` (may be empty, last semicolon optional).
  * Semicolons after subexpressions that end in `}` are optional.
* Untyped variable declaration: `var ID = E` where `ID` is an identifier.
* Typed variable declaration: `var ID: T = E` where `ID` is an identifier and `T` is `Int`, `Bool` or `Unit`.
* If-then conditional: `if E1 then E2`
* If-then-else conditional: `if E1 then E2 else E3`
* While-loop: `while E1 do E2`
* Function call: `ID(E1, E2, ..., En)` where ID is an identifier

Variable declarations (`var ...`) are allowed only directly inside blocks `({ ... }`) and in top-level expressions.

#### Precedences

1. `=`
2. `or`
3. `and`,
4. `==`, `!=`
5. `<`, `<=`, `>`, `>=`
6. `+`, `-`
7. `*`, `/`, `%`
8. Unary `-` and `not`
9. All other constructs: literals, identifiers, if, while, var, blocks, parentheses, function calls.

The program consists of a single top-level expression. If the program text has multiple expressions separated by semicolons, they are treated like the contents of a block, and that block becomes the top-level expression. The last expression may be optionally followed by a semicolon.

Arbitrary amounts of whitespace are allowed between tokens. One-line comments starting with `#` or `//` are supported.

### Progress
These are the main parts of the implementation stages/progress of the compiler.
- [x] Tokenizer
  - [x] Basic tokenization
  - [x] Basic test cases
  - [x] Edge test cases
  - [x] Negative test cases
- [x] Parser
  - [x] Integer literal
  - [x] Identifiers
  - [x] Boolean literal
  - [x] If then else blocks
  - [x] Comparison operators (=, ==, !=, <=, >=, >, <, and, or)
  - [x] While block
  - [x] Function call
  - [x] Type declaration
- [x] Interpreter - This is an optional part of the compiler which is done for learning
  - [x] Basic recursion
  - [x] Symbol table
  - [x] All operators
  - [x] Function call
  - [x] Conditional block
  - [x] While block
- [x] Type Checker
  - [x] Positive test cases
  - [ ] Negative test cases
- [ ] IR Generator
  - [x] Integer literal
  - [x] Identifiers
  - [x] Boolean literal
  - [x] If then else blocks
  - [x] Comparison operators (=, ==, !=, <=, >=, >, <, and, or)
  - [x] While block
  - [x] Function call
  - [x] Type declaration
- [ ] Assembly Generator
  - [ ] Operators
  - [x] Assemble
  - [x] Generate native executable
- [ ] Analysis & Optimization
