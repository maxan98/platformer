main:
	touch foo.class
	rm *.class
	javac Game.java
	java Game default_level
