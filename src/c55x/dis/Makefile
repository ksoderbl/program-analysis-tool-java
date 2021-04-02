all: C55xDisParser.class

.PHONY: clean

C55xDisParser.class: C55xDisParser.jj
	javacc C55xDisParser.jj

clean:
	rm -f C55xDisParserConstants.java C55xDisParser.java C55xDisParserTokenManager.java ParseException.java SimpleCharStream.java Token.java TokenMgrError.java *.class *~
