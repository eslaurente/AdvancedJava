AdvancedJava
============

Source code for CS410J: Advanced Java Programming (Summer 2014) projects


**airline-gwt** 


This is a web application that allows a user to add an airline and add invidivual flights information. The page updates a list of stored flights for that airline. The user can search for flights via source and destination airport codes and flight numbers with the page updating a result table. The user can select any item on the result table and delete the flight from storage.

About the application ![Alt-text](https://raw.githubusercontent.com/eslaurente/AdvancedJava/master/Screen%20Shot%202014-11-21%20at%205.37.26%20PM.png "About the application")

Adding an airline and a flight ![Adding an airline and a flight](https://github.com/eslaurente/AdvancedJava/blob/master/Screen%20Shot%202014-11-21%20at%205.36.43%20PM.png "Adding an airline and an associated flight")

Searching/filtering flights by flight number, source, and destination ![Alt-text](https://raw.githubusercontent.com/eslaurente/AdvancedJava/master/Screen%20Shot%202014-11-21%20at%205.38.15%20PM.png "Searching/filtering flights by flight number, source, and destination")

Deleting a flight entry ![Alt-text](https://raw.githubusercontent.com/eslaurente/AdvancedJava/master/Screen%20Shot%202014-11-21%20at%205.37.06%20PM.png "Deleting a flight entry")


**HOW TO RUN**

(1) Ensure that you have the Java JDK version 1.7 or higher installed on your machine
(2) Install Apache Maven on your system: 
 > Download the latest version of Maven at: http://maven.apache.org/download.cgi
 > Unzip apache-maven3.x.y archive
 > Add the location of the extracted archive folder to your environment path
 > Ensure that the JAVA_HOME variable is pointing to actual JDK path (in Linux: /usr/libexec/java_home)
    > in Linux, in your ~/.bash_profile file, add the following:
    >   export JAVA_HOME=$(/usr/libexec/java_home)
    >   export PATH=/usr/local/apache-maven-3.2.3/bin:$PATH
    > in the terminal, enter: source ~/.bash_profile to add
    

