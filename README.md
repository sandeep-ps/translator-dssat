# AgMIP DSSAT Translator

This translator is a simple package used to translate from a generic JSON
string into a DSSAT readable format. More information to come.


## Build / Install Instructions

### Prerequisites

 - Java
 - Maven
 
### Instructions

Building the DSSAT Translator JAR with dependencies using the following command:

```mvn package```

The relevant JAR files will be created in the ```target``` directory.

## Run

### Convert from JSON to DSSAT model

```java -jar target/translator-dssat-1.2.20-jar-with-dependencies.jar input_file.json```


### Convert from DSSAT model to JSON

```java -jar target/translator-dssat-1.2.20-jar-with-dependencies.jar input_file.SQX.zip```

Here, input_file.SQX.zip is a ZIP file containing the SQX file.
