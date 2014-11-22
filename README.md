AdvancedJava
============

Source code for CS410J: Advanced Java Programming (Summer 2014) projects


**airline-gwt** 


This is a web application that allows a user to add an airline and add invidivual flights information. The page updates a list of stored flights for that airline. The user can search for flights via source and destination airport codes and flight numbers with the page updating a result table. The user can select any item on the result table and delete the flight from storage.

This project demonstrates how a rich Internet application is created using Java, and using tools like the Google Web Toolkit (GWT) API, which abstracts most of the JavaScript HTML in wrapper classes.

About the application ![Alt-text](https://raw.githubusercontent.com/eslaurente/AdvancedJava/master/Screen%20Shot%202014-11-21%20at%205.37.26%20PM.png "About the application")

Adding an airline and a flight ![Adding an airline and a flight](https://github.com/eslaurente/AdvancedJava/blob/master/Screen%20Shot%202014-11-21%20at%205.36.43%20PM.png "Adding an airline and an associated flight")

Searching/filtering flights by flight number, source, and destination ![Alt-text](https://raw.githubusercontent.com/eslaurente/AdvancedJava/master/Screen%20Shot%202014-11-21%20at%205.38.15%20PM.png "Searching/filtering flights by flight number, source, and destination")

Deleting a flight entry ![Alt-text](https://raw.githubusercontent.com/eslaurente/AdvancedJava/master/Screen%20Shot%202014-11-21%20at%205.37.06%20PM.png "Deleting a flight entry")


**HOW TO RUN**

1. Ensure that you have the Java JDK version 1.7 or higher installed on your machine
2. Install Apache Maven on your system:  
  1. Download the latest version of Maven at: http://maven.apache.org/download.cgi
  2. Unzip apache-maven3.x.y archive
  3. Add the location of the extracted archive folder to your environment path
  4. Ensure that the JAVA_HOME variable is pointing to actual JDK path (in Linux: /usr/libexec/java_home)
  5. in Linux, in your ~/.bash_profile file, add the following: 
  6. `export JAVA_HOME=$(/usr/libexec/java_home)`
  7. `export PATH=/usr/local/apache-maven-3.x.y/bin:$PATH` (after moving apache-maven folder to /usr/local/)
  8. in the terminal, enter: `source ~/.bash_profile` to add
3. in the terminal, in the *airline-gwt* folder, type in: `mvn package` (Note: this will take a while)
4. in the terminal, type in:`mvn jetty:run-war` (Note: this will take a while, as Java code is converted to JavaScript and HTML)
5. Open a web browser and go to: http://localhost:8080/airline-gwt/airline/

  

