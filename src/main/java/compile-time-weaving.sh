#!/bin/bash

# Prepare
echo "Preparing the environment..."
rm -rf ./target 2>/dev/null
CLASSES_DIR=./target/classes/compile-time
COUNTER=1
CURRENT_DIR=
while :
do
    DIR=`echo "$CLASSES_DIR" | cut -d'/' -f $COUNTER`
    test "$DIR" = "" && break
    CURRENT_DIR=${CURRENT_DIR}${DIR}/
    mkdir $CURRENT_DIR 2>/dev/null
    COUNTER=`expr $COUNTER + 1`
done

CLASSPATH=./src/main/java
for i in "aspectjtools.jar" "aspectjrt.jar" "plantuml.jar"
do
    CLASSPATH=$CLASSPATH:./src/main/resources/$i
    echo "Classpath is --->"$CLASSPATH
done

# Compile the sources
echo "Compiling..."
echo java -cp $CLASSPATH org.aspectj.tools.ajc.Main -source 1.5 -d $CLASSES_DIR src/main/java/*.java

java -cp $CLASSPATH org.aspectj.tools.ajc.Main -source 1.5 -d $CLASSES_DIR src/main/java/*.java

# Run the example and check that aspect logic is applied
echo "Running the sample..."

echo java -cp $CLASSPATH:$CLASSES_DIR com.aspectj.Main
 
java -cp $CLASSPATH:$CLASSES_DIR com.aspectj.Main
