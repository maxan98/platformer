main:
	touch foo.class
	rm *.class
	javac Game.java
	java -ea Game default_level
