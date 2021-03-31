# Board Game - Software Engineering Project 

This game was developed as a computer game for the final examination of the Software engineering course at Politecnico di Milano (A.Y. 2019/2020) - Bachelor of Science thesis project.
<br>
<br>
Final score was ***30 cum laude / 30*** (A+)
## Group members
- **Alessandro Lisi** ([@PrivateAbstractAleLisi](https://github.com/PrivateAbstractAleLisi)) <br>
- **Gabriele Marra** ([@gabrielemarra](https://github.com/gabrielemarra)) <br>
- **Matteo Miceli** ([@micelimatteo](https://github.com/micelimatteo)) <br>
## Project Requirements
Develop an online multiplayer board game with Java using the MVC pattern.<br>
We developed the server and the client, with both CLI and GUI (JavaFX) interfaces.<br>
To transmit data we used TCP protocol between server and clients along with JSON format and Java serialization.<br>
The server supports multiple matches at the same time and waiting players are handled in a lobby.<br><br>
For the full specification visit this link: [Specification (Italian only)](github_readme_media/Requisiti_Progetto.pdf)

## Screenshots
### Homepage
![Homepage](github_readme_media/1_Homepage.jpg?raw=true)
### Match creation
![Match creation](github_readme_media/2_NumberPlayersSelection.jpg?raw=true)
### Character Selection
![Character Selection](github_readme_media/3_CharactersSelection.jpg?raw=true)
### Character Selection - 2
![Character Selection - 2](github_readme_media/4_CharactersSelection-2.jpg?raw=true)
### Move Phase
![Move Command](github_readme_media/5_Moving.jpg?raw=true)
### Build Phase
![Build Command](github_readme_media/6_Building.jpg?raw=true)
### Win!
![Win!](github_readme_media/7_WinningPage.jpg?raw=true)

## Functionalities Implemented

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

