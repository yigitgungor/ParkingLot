# ParkingLot
Parking lot project for Software Engineering (CS431)

ReadMe at:
https://docs.google.com/document/d/1HA8nx6g8Ltf96T_8YNRSFqmxps6ZXxDZFDM79IanhPo/edit?usp=sharing
Documentation at: 
https://docs.google.com/a/scarletmail.rutgers.edu/document/d/1Ut8PRiIQkKAP8Pt6mlKKI-pMn9Hpf7o2yxpGHhfwEqk/edit?usp=sharing

## Project Structure

1. *Root Module Parking Lot*
    1. Module **gate**: Gate component
    1. Module **monitor**: Monitor component
    1. Module **parkingspace**: Parking Space component 
    1. Module **trafficgen**: TrafficGenerator component
    1. Module **proto**: contains objects for inter-component communication 
    1. Module **util**: contains common utility class
        1. SyncClock: a synchronized clock that abstracts Traffic Generator's chronos service    
    1. libs: contains externally imported jars
    
    
To use code from another module, include the following dependency to your component's pom.xml  
.i.e a component wants to use edu.rutgers.cs431.teamchen.util


```xml
            [...]
            <dependency>
                <groupId>${project.groupId}</groupId>
                <artifactId>util</artifactId>
                <version>${project.version}</version>
            </dependency>
            [...]
 ```
 
 ### Build 
 
 From the project base directory, execute command
 
 ```
 mvn install
 ```
It would build and assemble all the executable jar with all dependencies in each jar ready to be executed.

#### Built jars and corresponding arguments:

The following list demonstrates the arguments of each jar and example for each arguments

```bash
    java -jar xxx.jar [arg1  [arg2  [...]]]
```

1. monitor.jar
    1. http port number: the port number the http server listens on .ie 8080
    1. strategy: either 1, 2, or 3
    1. max gate: the maximum number of gates .ie 6
    1. max parking tokens: the capacity of the parking lot .ie 200
1. traffic.jar
    1. monitor hostname: the hostname or ip address of the monitor .ie localhost
1. parkspc.jar
    1. monitor http addr: the monitor's http address .ie http://localhost:8080/
    1. http port: the port number for the parking space's http service .ie 1234
1. gate.jar (x6 processes using different ports)
    1. monitor http addr: the monitor's http address .ie http://localhost:8080/
    1. tcp port: the port number the traffic generator sends car to .ie 8000
    1. http port: the port number for this gate's http service .ie 1235
    1. transfer duration: the time it takes to transfer a car to the parking space in milliseconds .ie 6000
