all: compile

compile:
	jflex scanner_ir.flex
	java -jar java-cup-11b.jar -interface -parser ParserIR parser_ir.cup
	java -jar java-cup-11b.jar -interface -parser ParserIRJava parser_ir_java.cup
	@rm Translated.java || true
	javac -cp java-cup-11b-runtime.jar *.java

execute:
	@java -cp java-cup-11b-runtime.jar:. Main

run:
	@javac Translated.java
	@java Translated

clean:
	@rm -f *.class *~
	@rm ParserIR.java || true
	@rm ParserIRJava.java || true
	@rm ScannerIR.java || true
	@rm sym.java || true