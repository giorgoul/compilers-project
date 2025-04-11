# compilers-project-1

Name: Γιώργος Ουλής  
ΑΜ: 1115202100131

## How to run
- `make all`
    - For part 1: `java part1.calculator.Main < input`
    - For part 2: `cd part2; java -cp java-cup-11b-runtime.jar:. Main < input`

## Remarks
I have included the test cases as well as the script provided [here](https://piazza.com/class/m76690qi24h356/post/58). Below I present which tests ran successfully and which failed.

| Test case | OK? |   | Test case       | OK? |
|-----------|-----|---|-----------------|-----|
| error1    | ✅   |   | example8        | ❌   |
| error2    | ✅   |   | example9        | ❌   |
| error3    | ✅   |   | example10       | ❌   |
| error4    | ✅   |   | example11       | ✅   |
| error5    | ✅   |   | example12       | ✅   |
| error6    | ✅   |   | example13       | ✅   |
| error7    | ✅   |   | example14       | ✅   |
| example1  | ✅   |   | example15       | ✅   |
| example2  | ✅   |   | example16       | ✅   |
| example3  | ✅   |   | example17       | ✅   |
| example4  | ❌   |   | example18       | ❌   |
| example5  | ❌   |   | example19       | ❌   |
| example6  | ❌   |   | example20       | ❌   |
| example7  | ❌   |   | precedence_test | ❌   |

I made the grammars intuitively; a better approach could be to first make them as close to an LL(1) form as possible, using techniques like left factoring, which would help e.g. with the precedence of `if`, `concat`, `reverse` operator expressions, like in part 1, instead of me enforcing them afterwards.

Surprisingly in my case the reduce/reduce conflicts were easier to resolve than the shift/resolve ones as they required minor tweaks in both my grammars (e.g. duplicate rules). I couldn't solve a lot of shift/reduce conflicts by just using the `precedence` instruction in CUP, and a lot of the time they resulted in wrong results.

I had major problems trying to implement top-level expressions, especially `if` statements and nested `if` statements by extension. At first I tried to use an `eval()` function:
```
eval(x) {
    x
}
``` 
While it worked for function calls, I couldn't figure out how to make it work for if statements, so I left the idea altogether. I also attempted splitting rules depending on where they were used, for example an if statement within a function's body works differently than an if statement on the "main" segment (i.e. after the function declarations), same with `expr` and `expr_func`, with `expr` not including identifiers, for instance. Unfortunately I still ran into a lot of problems with the if statements, as I couldn't understand how they worked in the "main" context (e.g. How were nested if expressions evaluated? Was `System.out.println` needed?). 