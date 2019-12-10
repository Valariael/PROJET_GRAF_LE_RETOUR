JVC=javac
sources = $(wildcard *.java)
classes = $(sources:.java=.class)
PRODUCT_NAME = BibliGraf

all: program jar

program: $(classes)
	
%.class: %.java
	$(JVC) $<
	
jar: $(classes)
	@echo "Manifest-Version: 1.0" > manifest.txt
	@echo "Class-Path: ." >> manifest.txt
	@echo "Main-Class: Main" >> manifest.txt
	@echo "" >> manifest.txt
	
	jar -cmf manifest.txt $(PRODUCT_NAME).jar $(classes)
	
clean:
	rm -f *.class
	rm manifest.txt
