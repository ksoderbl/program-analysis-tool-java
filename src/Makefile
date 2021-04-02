
.PHONY: clean

# javac `find . -name '*.java'`
all:
	$(MAKE) -C arm/gas
	$(MAKE) -C c55x/dis
	javac -Xlint */*.java */*/*.java

clean:
	find . -name \*.class -exec rm -f {} \;
	find . -name \*~ -exec rm -f {} \;
	find . -name \.\#\* -exec rm -f {} \;
	find . -name \#\*\# -exec rm -f {} \;
	$(MAKE) -C arm/gas clean
	$(MAKE) -C c55x/dis clean
