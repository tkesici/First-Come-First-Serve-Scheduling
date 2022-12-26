# author     Tevfik KESICI
# @studentId  20200808004
# @since      21.12.22
# CSE 303 – Fundamentals of Operating Systems
# Makefile builder for HW#2 FCFS CPU Scheduling with IO

JVM = java
JC = javac
FILE = samplejobs.txt

# For custom input, provide "make FILE=custom.txt"

.SUFFIXES: .java .class

.java.class:
	$(JC) $*.java

default: FCFS.class
	$(JVM) FCFS $(FILE)
		
# For removing all .class files

clean:
	$(RM) FCFS.class