# ing-sw-2020-Lisi-Marra-Miceli - Group SPS58
## Group members
- **Alessandro Lisi** ([@PrivateAbstractAleLisi](https://github.com/PrivateAbstractAleLisi)) <br>*Codice Persona*: 10621058 - *Matricola*: 887426 <br>*Email Polimi*: alessandro.lisi@mail.polimi.it
- **Gabriele Marra** ([@gabrielemarra](https://github.com/gabrielemarra)) <br>*Codice Persona*: 10572983 - *Matricola*: 887167<br>*Email Polimi*: gabriele.marra@mail.polimi.it
- **Matteo Miceli** ([@micelimatteo](https://github.com/micelimatteo)) <br>*Codice Persona*: 10560901 - *Matricola*: 888156 <br>*Email Polimi*: matteo.miceli@mail.polimi.it

## Functionality

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Simple rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#)|
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Advanced Gods | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Undo | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

## Execution of Jar Files
### *Requirements*
Requires Java 11 or a more recent version.

### *Server*
To execute the **Server** download the *server.jar* and execute the following command into the terminal:
``` 
java -jar server.jar
``` 

### *Client*
(**Recommended**) To execute the **Client with GUI** download the *client.jar* and execute the following command into the terminal:
``` 
java -jar client.jar -gui
``` 

To execute the **Client with CLI** download the *client.jar* and execute the following command into the terminal:
```
java -jar client.jar -cli
``` 

If you want, you can also select the desired UI at the start of the program. To select the UI during runtime please execute this command:
``` 
java -jar client.jar
```

### Online Server

An **Online Server** is running h24 on an *Amazon AWS EC2 Instance* and is ready to accept your client but, obviusly, you can't access to the output of the server.
To use this **Online Server** please open the *client* with the *GUI* and select the *"Online Server" option* in the first screen.

### Advanced line arguments
The program accept different line arguments (flags) to set main settings of the program itself.
<br>
**Server**:
| Flag | Result|
|:--------| :-------------|
| ``-pingOff`` | Disable the Ping System and the Socket Timeout |
| ``-ping-stamp``  | Print deatils about each Ping Event received |

**Client**:
| Flag | Result|
|:--------| :-------------|
| ``-pingOff`` | Disable the Ping System and the Socket Timeout |
| ``-cli``  | Select the CLI as Program UI |
| ``-gui``  | Select the GUI as Program UI |

<br>If both (``-cli`` and ``-gui``) commands are used at the same time, the program will ask you to choose the desired UI at the beginning of the execution.


## Compile with Maven
To compile the program use Intellij Idea, select the desired profile (*Server*, *Client-Win* or *Client-Mac*), execute the following Maven command:
```
mvn clean package
``` 
<br>In the folder *Target* you'll find the .jar file for the desired profile.
<br>Please note that *Client-Win* and *Client-Mac* profiles are almost identical: the only difference between them is the order wich the ``javafx`` dependencies are added.

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

