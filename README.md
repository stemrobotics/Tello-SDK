## Tello-SDK

Java programming support for the Tello Drone. 

### Project Description

This Eclipse project is a complete example of how to program the Tello & Tello EDU drones
from DJI. The user can add classes to fly the drone or create missions without having
to understand the low level network communications or the Tello command syntax. The
project wraps the network communication and Tello commands in higher level classes making
it easy to get started writing drone programs. The user can peruse the lower classes to
learn the network communication and drone command processing.

The project includes a number of convenience functions, support for live video display
from the drone and using OpenCV for visual recognition and tracking.

This project is the companion to the STEM Robotics [Java for Robots course](https://stemrobotics.cs.pdx.edu/node/4196), Tello off-ramp.

### How to Download

On the github repo, click the **green Clone or Download** button. Then click **download zip**.
Once the zip file (approx 35mb) is on your PC, copy the **Tello-SDK-master** directory in the zip 
to your Visual Studio Code workspace. Rename the directory to **Tello-SDK**.
The import and preparation of the project will take some time so be patient.

*****************************************************************************************

### Getting Started

The Server has already been loaded, so you can test out the project by either running the Main file or the Server file, located in tello.Main or tello.Server.

****************************************************************************************

### How to add new Modes:
All Modes must be put in tello.modes.mode and must be extended from AbstractMode.java, this is because it allows the server to load all the different modes right before it starts.

> A simple Tello Mode will look like so

```Java
    package tello.modes.mode   

    public class SimpleMode extends AbstractMode {

        public SimpleMode() {
            super(); 
        }

        public void run() {
            // First set the stillRunning varaible of the class, gotted from AbstractMode

            stillRunning = true; // This is used to tell the Server not to start a new mode until this stillRunning is false  

            // tellcoms is the TelloCommunication varaible for every Tello Mode and you have access to it from AbstractMode

            tellocoms.setTimeout(50_000); // Give server enough time to connect

            telloControl.connect(); // Connect to the telloDrone.

            telloControl.enterCommandMode(); // No command can be fed to the tello drone until you have entered command mode

            // Now you can start putting custom commands for the mode 

                // Commands goes here

            // 

            // Once you are done, you must call this function 
            stopExecution(); // Tells the server the telloMode is done running
        }

        @Override
        public void execute() {
            run(); // The function you want to run when ever this mode needs to be runned.
        }

        @Override
        public String getName() {
            return "SimpleMode"; // The name you would like to call this mode.
        }

        @Override
        public String getDescription() {
            return "Just a simple tello-mode"; // The description you would like to give your tello-mode
        }
    }
```

Version 2.0.0   

*   Development of simple server, which lets you run different tello-modes.

Version 1.0.1

*	Fix design error in locking scheme in the TelloCamera class.
*	Fix search function in Javadoc.

Nov 25, 2020

Version 1.0.0

*	Development completed, released June 2020.

Version 0.0.0

*	Under development spring 2020.
