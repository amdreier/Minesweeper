all:

compile:
	mvn compile

run: compile
	mvn exec:java

format:
	mvn formatter:format

clean:
	rm -f src/*.class bin/* test/*.class
	rm -rf *.zip
	mvn clean
