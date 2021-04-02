all: ARMGasParser.class

.PHONY: clean

ARMGasParser.class: ARMGasParser.jj
	javacc ARMGasParser.jj

clean:
	rm -f ARMGasParserConstants.java ARMGasParser.java ARMGasParserTokenManager.java ParseException.java SimpleCharStream.java Token.java TokenMgrError.java *.class *~
