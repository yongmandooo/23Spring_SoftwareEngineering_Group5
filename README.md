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
the devices. Each method in below checks the end date of rented devices. If the end date has passed today, the device will be returned automatically and other users can rent the device.
1. getAllDevices  
This method returns the status of all devices.
- curl -X GET http://localhost:8080/devices  
The expected output of it will be like  
**{
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
  }**

2. getAllTypeDevices  
This method returns the status of devices included in given type by path variable. There are two types of devices
 in our app, monitor and tablet.
- curl -X GET http://localhost:8080/devices/monitor  
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
You can rent and return device with your "Authentication ID" to verify your registration. You should keep your "Authentication ID" from loginUser method.
1. rentDevice  
This method is for rent request from a user. User can rent only one device. By this method, user can rent a device for 3 months. It expects one attribute "authenticationId" as a json body of the request. 
There will be 4 devices in the DB by default: device1, device2, device3 and device4. You can choose the device and put the device id on
 your URL. The startDate of renting device is today's date. The endDate of renting device is 3 months from today.
There are 4 conditions to rent the device. 1. Right "authenticationId" 2. User have no device. Only one device can be
rented per user. 3. Right device id 4. The device is not rented by anyone. If four conditions are met, you can rent 
the device. Otherwise, error message will occur.

- curl -X PUT http://localhost:8080/device/rent/device1 "Content-type:application/json" -d '{"authenticationId": "6461b4e01b7d2d614f9ccccb"}'  
The expected output of it will be  
**{
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
    "currentUser": "rnjsdydals01"
}**  

2. returnDevice  
Although the device will be returned automatically after 3 months, you can return the device before that by this method.
It expects one attribute "authenticationId" as a json body of the request. You should put the device id of your rented device on your URL to return the device.
There are 4 conditions to return the device. 1. Right "authenticationId" 2. User have one device. 3. Right device id 4. The device ID should match the device ID you rented. If four conditions are met, you can return the device. Otherwise, error message will occur.

- curl -X PUT http://localhost:8080/device/return/device1 "Content-type:application/json" -d '{"authenticationId": "6461b4e01b7d2d614f9ccccb"}'  
The expected output of it will be  
**{
    "id": "device1",
    "name": "ipad1",
    "type": "tablet",
    "startDate": null,
    "endDate": null,
    "currentUser": null
}**  

### Inquiry Board for using the service better
The application serves as a inquiry board for logged-in users to conveniently receive suggestions from customers regarding device failures, device needs, etc. Therefore, permissions became an important component, like customers except for the admin having the right to edit and delete only their own posts.

1. getAllInquiry
If you want to see all inquiry board, using this method. 
This method accesses the inquiry Database in our MongoDB and shows us all the inquiries we have.
- curl -X GET http://localhost:8080/inquirys  
The expected output of it will be  
**{
{“id”=”inquiry1”, “title”=”test1”, “contents”=”content1”, “writer”=”aaa”, “confirmed”, false},
{“id”=”inquiry2”, “title”=”test2”, “contents”=”content2”, “writer”=”aaa”, “confirmed”, false},
{“id”=”inquiry3”, “title”=”test3”, “contents”=”content3”, “writer”=”bbb”, “confirmed”, false},
{“id”=”inquiry4”, “title”=”test4”, “contents”=”content4”, “writer”=”bbb”, “confirmed”, false},
{“id”=”inquiry5”, “title”=”test5”, “contents”=”content5”, “writer”=”ccc”, “confirmed”, false},
{“id”=”inquiry6”, “title”=”test6”, “contents”=”content6”, “writer”=”ccc”, “confirmed”, false}
}**

2. writerBoard
when you want to see your inquiry board,  
This method takes data about a “writer”.  We search same "writer" in InquiryRepository,  stores all inquiries written by that writer in a list, and shows them all at once. This has the effect of reminding the user if their inquiry has been resolved or what they wrote.

- curl -X GET http://localhost:8080/writer/aaa  
The expected output of it will be  
**{{“id”=”inquiry1”, “title”=”test1”, “contents”=”content1”, “writer”=”aaa”, “confirmed”, false},
{“id”=”inquiry2”, “title”=”test2”, “contents”=”content2”, “writer”=”aaa”, “confirmed”, false}
}**


3. insertBoard  

 This method expects two attributes "inquiry" and "athenticationId" as a json body of the request. This "inquiry" must have id, title, contents, confirmed, then its data will be record at InquiryRepository. 
First, check if this user is a registered user in mongo DB through "athenticationId". If it is, save the data received from the inquiry to the DB and specify "writer" as "athenticationId".

- curl -X POST http://localhost:8080/inquiry/write -H "Content-type:application/json" -d '{"inquiry":{"id":"inquiry7", "title":"testchan", "contents":"content7", "confirmed":false}, "athenticationId":"6461b4e01b7d2d614f9ccccb"}'  
The expected output of it will be  
**{"id":"inquiry7", "title":"testchan", "contents":"content7", “writer”:"6461b4e01b7d2d614f9ccccb", "confirmed":false}**


4. getBoard

This method expects only one attributes "id" as a pathvarible data. When we get id, our method fine "inquiry" 
- curl -X GET http://localhost:8080/inquiry/inquiry7  
The expected output of it will be  
  **{"id":"inquiry7", "title":"testchan", "contents":"content7", “writer”:"6461b4e01b7d2d614f9ccccb", "confirmed":false}**

5. editBoard  
This method expects two attributes "inquiry" and "athenticationId" as a json body of the request. And pathvariable is “id”
First, verify that the user is logged in via "athenticationId". If the login is confirmed, we check to see if the user has the same "athenticationId" as the user stored as an “writer”, or if no posts match the initially received ID. If this user is different from the “writer”, we'll check to see if they have admin permissions, and if so, they can edit. If no posts match the ID, a new post is created and saved to the DB.  

- curl -X PUT http://localhost:8080/inquiry/inquiry7 -H "Content-type:application/json" -d '{"inquiry":{"id":"inquiry7", "title":"testchan is change", "contents":"content is changed", "confirmed":false}, "athenticationId":"6461b4e01b7d2d614f9ccccb"}'  
The expected output of it will be  
**{"id":"inquiry7", "title":"testchan is change ", "contents":"content is changed", “writer”:"6461b4e01b7d2d614f9ccccb", "confirmed":false}**


6. solved  
This method is an intuitive way to check if the inquiry has been resolved. This is a very important part of the application and can only be used by the admin account. In the DB, admin ID is 'aaa' and admin passwrod is '1234'. You first login the admin and get athenticationId of the admin. This method takes the "athenticationId" and the inquiry id, first checks to see if the user has admin privileges, then finds the inquiry and returns "isconfirmed" as TRUE..  

- curl -X PUT http://localhost:8080/inquiry/change/inquiry7 -H "Content-type:application/json" -d '{"athenticationId":**"{athenticationId of the admin}"**}'  
The expected output of it will be  
**{"id":"inquiry7", "title":"testchan is change", "contents":" content is changed", "writer":"6461b4e01b7d2d614f9ccccb", "confirmed":true}**

7. deleteBoard  
Delete board This method takes only the athenticationId and uses it for deletion. First, it checks if the user can log in with the athenticationId, and if the athenticationId of the user stored as "writer" is the same, it deletes the user. However, if this user and "writer" are different, it checks if the user has admin privileges, and if so, proceeds to delete.  

- curl -X DELETE http://localhost:8080/inquiry/inquiry7 -H "Content-type:application/json" -d '{"athenticationId":"6461b4e01b7d2d614f9ccccb"}'  
The expected output of it will be  
**{}**


