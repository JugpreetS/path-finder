agent: agent.class

agent.class: agent.java
	javac -g agent.java

run: agent.class
	java agent
