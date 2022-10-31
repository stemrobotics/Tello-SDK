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

**Note**: This project is intended for use on Windows PCs as the OpenCV implementation included with the
project is Windows specific.

### How to Download

On the github repo, click the **green Clone or Download** button. Then click **download zip**.
Once the zip file (approx 35mb) is on your PC, copy the **Tello-SDK-master** directory in the zip 
to your Eclipse workspace. Rename the directory to **Tello-SDK**. Next import that directory
into Eclipse as an *existing project into workspace* under the General import category.
The import and preparation of the project will take some time so be patient.

*****************************************************************************************
Version 1.0.1

*	Fix design error in locking scheme in the TelloCamera class.
*	Fix search function in Javadoc.

Nov 25, 2020

Version 1.0.0

*	Development completed, released June 2020.

Version 0.0.0

*	Under development spring 2020.
