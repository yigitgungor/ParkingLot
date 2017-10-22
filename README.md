# ParkingLot
Parking lot project for Software Engineering (CS431)

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
 
 
