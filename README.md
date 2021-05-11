# ContactSystem

This program uses the Google gson library to convert JSON strings to Java objects. The jar file has been added to this repository.

Data is stored in an H2 database.

The four constants in DatabaseUtility.java can be updated to connect to a newly installed H2 database. For this technical exercise they are storerd in the Java file instead of a separate secure file.

Call ContactSystem.main with three arguments. The first argument is the http method (GET, POST, etc.). The second argument is the relative url. The third argument is the JSON string. 

StartTest.java contains the JUnit tests.
