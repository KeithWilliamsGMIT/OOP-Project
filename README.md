# OOP-Project
This is my 3rd year project for my object orientated programming college module. The requirement for this project is to "implement a multi-threaded file server and logging application that allows a client application to download files using a set of options presented in a terminal user interface".

#### Application description
This application consists of two main parts:

##### Client
When run, the client will give the user four options.

1. Connect to Server - This will try to open a socket connection to the server.
2. Print File Listings - This will send a request to the server to retrieve a list of files that the user can download.
3. Download File - This will prompt the user to enter the name of the file they wish to download. If the file exists on the server it will be written to the directory specified in the client-config.xml file. This will override any file with the same name in the directory.
4. Exit - This will send a request to the client informing them that they are cloing the connection so that the server will close it on the other side.

##### Server
Once the server is started it will listen for a clients. When a client connects to the server a new thread will be spawned. This thread will handle all that clients requests. All client threads add each request they recieve to the same blocking queue. The requests in this queue are taken off by another thread and written to a log file. The server can be shutdown by typing "EXIT";

#### Running the application
Clone the repository.
```
git clone https://github.com/KeithWilliamsGMIT/OOP-Project
```

###### Running the server
To run the server, open a new terminal in the bin directory and enter the following command.
```
java ie.gmit.sw.Server 7777 /path/to/myfiles
```

Where 7777 is the port number and /path/to/myfiles is the path to the directory that contains the files the user can download.

###### Running the client
To run a client, open a second terminal in the bin directory and enter the following command.
```
java ie.gmit.sw.Client
```

Ensure that the client-config.xml file is in the same directory. Edit the download-dir element in this xml to represent the directory to which you want the files to be saved.