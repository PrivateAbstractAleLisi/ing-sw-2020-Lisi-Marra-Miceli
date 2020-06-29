# ing-sw-2020-Lisi-Marra-Miceli - Group SPS58
## Group members
- Alessandro Lisi (@PrivateAbstractAleLisi)
		Codice Persona: 10621058 - Matricola: 887426
		Email Polimi: alessandro.lisi@mail.polimi.it
- Gabriele Marra(@gabrielemarra)
		Codice Persona: 10572983 - Matricola: 887167
		Email Polimi: gabriele.marra@mail.polimi.it
- Matteo Miceli (@micelimatteo)
		Codice Persona: 10560901 - Matricola: 888156
		Email Polimi: matteo.miceli@mail.polimi.it

## Functionality Added
- Complete rules 
- Socket
- CLI
- GUI

Advanced Functionality
- Persistence
- Advanced Gods 

## Execution of  Jar Files
### Requirements
Requires Java 11 or more recent versions

### Server
To execute the Server download the server.jar and execute the following command into the terminal:
 java -jar server.jar

### Client
(Recommended) To execute the Client with GUI download the client.jar and execute the following command into the terminal:
 java -jar client.jar -gui

To execute the Client with CLI download the client.jar and execute the following command into the terminal:
 java -jar client.jar -cli

If you want, you can also select the desired UI at the start of the program. To select the UI during runtime please execute this command:
 java -jar client.jar 

### Online Server

An Online Server is running h24 on an Amazon AWS EC2 Instance and is ready to accept your client but, obviusly, you can't access to the output of the server.
To use this Online Server please open the client with the GUI and select the "Online Server" option in the first screen.

### Advanced line arguments
The program accept different line arguments to set few main settings of the program itself.

Server:
- Use -pingOff to disable the Ping System and the Socket Timeout
- Use -ping-stamp to print information about each Ping Event received

Client:
- Use -pingOff to disable the Ping System and the Socket Timeout
- Use -cli to select the CLI as Program UI
- Use -gui to select the GUI as Program UI

If both (-cli and -gui) commands are used at the same time, the program will ask you to choose the desired UI at the beginning of the execution.


## Compile with Maven
To compile the program use Intellij Idea, select the desired profile (Server, Client-Win or Client-Mac), execute the following Maven command: "mvn clean package"

In the folder Target you'll find the .jar file for the desired profile.

Please note that Client-Win and Client-Mac profiles are almost identical: the only difference between them is the order wich the javafx dependencies are added.


