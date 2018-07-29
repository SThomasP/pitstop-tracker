echo "Building \"PitStopTracker\""
mvn clean package assembly:single > /dev/null
cp target/PitStopTracker-1.0-SNAPSHOT-jar-with-dependencies.jar PitStopTracker.jar
echo "Done"
