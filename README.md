# MILESTONE2

You first download the Dockerfile and run.sh in the environment folder and  
run the following commands:  
**$ docker build -t image_name /path/to/Dockerfile**  
**$ docker run -it image_name**

Inside the docker container, you can run:  
**root@containerID$ sh run.sh**

In run.sh, MongoDB wants to know our space.  
**You should enter "6" and "69".**

The container will execute a bash shell by default when the built image is launched.  
It takes **10 minutes** in our local machine. Please wait for building completely.

## Features
Our web application manages electronic device rental service for UNIST dormitory residents. In below, it explains about 
features required to achieve the goal of our application, and APIs to implement each feature.

### Register & Login
Our application is only for UNIST residents. Therefore, if user cannot authenticate itself by student ID and identification
number, our app doesn't allow the user to register.
1. registerNewUser   
This method expects two attributes "user" and "idNum" as a json body of the request. If the studentId in "user" exists in 
dormResident table, and "idNum" matches to its identification number, it allows to register a new user.
- curl -X POST http://localhost:8080/register -H "Content-type:application/json" -d '{"user":{"id":"rnjsdydals01", 
"password":"rnjsdydals0131!", "email":"rnjsdydals01@unist.ac.kr", "studentId":"20181111"}, "idNum":"1111111111111"}'  
The expected output of it will be   
**{
  "authenticationId": "6461b4e01b7d2d614f9ccccb",
  "id": "rnjsdydals01",
  "password": "rnjsdydals0131!",
  "email": "rnjsdydals01@unist.ac.kr",
  "studentId": "20181111",
  "waitingType": null,
  "currentUsingDevice": null,
  "writtenInquiries": null,
  "admin": false
  }**

2. loginUser  
Most of our methods in our web application require "Authentication ID" which is automatically generated by Mongo DB when 
registering to authenticate the user. Once registering, to get the authentication ID, if given ID exists and password is
matched correctly, this method will return your authentication id.

- curl -X GET http://localhost:8080/login -H "Content-type:application/json" -d '{"id":"rnjsdydals01", 
"password":"rnjsdydals0131!"}'  
The expected output of it will be  
**6461b4e01b7d2d614f9ccccb**

### Device Rental Status Board
Before renting the device, user should know which device is available. Our app provides two ways to get status of 
the devices.
1. getAllDevices  
This method returns the status of all devices.
- curl -X GET http://localhost:8080/devices  
The expected output of it will be like  
**[
  {
  "id": "device1",
  "name": "ipad1",
  "type": "tablet",
  "startDate": [
  2023,
  5,
  15
  ],
  "endDate": [
  2023,
  8,
  15
  ],
  "currentUser": "bbb"
  },
  {
  "id": "device2",
  "name": "ipad2",
  "type": "tablet",
  "startDate": null,
  "endDate": null,
  "currentUser": null
  },
  {
  "id": "device3",
  "name": "monitor1",
  "type": "monitor",
  "startDate": null,
  "endDate": null,
  "currentUser": null
  },
  {
  "id": "device4",
  "name": "monitor2",
  "type": "monitor",
  "startDate": null,
  "endDate": null,
  "currentUser": null
  }
  ]**

2. getAllTypeDevices  
This method returns the status of devices which included in given type by path variable. There are two types of devices
 in our app, monitor and tablet.
- curl -X GET http://localhost:8080/devices/{type}  
  The expected output of it will be like  
**{
  "id": "device3",
  "name": "monitor1",
  "type": "monitor",
  "startDate": null,
  "endDate": null,
  "currentUser": null
  },
  {
  "id": "device4",
  "name": "monitor2",
  "type": "monitor",
  "startDate": null,
  "endDate": null,
  "currentUser": null
  }**

### Rent & Return Device

### Inquiry Board for using the service better

