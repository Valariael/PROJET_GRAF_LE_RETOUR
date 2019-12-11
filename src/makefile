JFLAGS = -g
JC = javac
PRODUCT_NAME = BibliGrafV2
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Node.java \
	Edge.java \
	SchedulingStrategies.java \
	Graf.java \
	UndirectedGraf.java \
	ShortestPathInfo.java \
	LongestPathInfo.java \
	InvalidFormatException.java \
	Task.java \
	TaskRaw.java \
	PertEdge.java \
	PertGraf.java \
	UserInterface.java \
	MainMenuController.java \
	AddEdgeDialogController.java \
	EditNodeDialogController.java \
	RemoveEdgeDialogController.java \
	RemoveNodeDialogController.java \



sources = $(wildcard *.java)

classes = $(sources:.java=.class)

all: program jar

program: $(classes)

%.class: %.java
	$(JC) $<

jar: $(classes)
	@echo "Manifest-Version: 1.0" > manifest.txt
	@echo "Class-Path: ." >> manifest.txt
	@echo "Main-Class: UserInterface" >> manifest.txt
	@echo "" >> manifest.txt

	jar -cmf manifest.txt $(PRODUCT_NAME).jar $(classes)



clean:
	$(RM) *.class
