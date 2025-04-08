all: compile

compile:
	jflex scanner_ir.flex
	java -jar java-cup-11b.jar -interface -parser ParserIR parser_ir.cup
	javac -cp java-cup-11b-runtime.jar *.java

execute:
	java -cp java-cup-11b-runtime.jar:. Main

clean:
	rm -f *.class *~