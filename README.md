## CSC-645 COMPUTER NETWORKS

Project: Socket Programming

### 1. Goal

This project was part of the SFSU CSC645 Computer Networks class and aimed to develop simple client and server console applications for text messaging using socket programming.

### 2. Description

It consists of two parts: the server program and the client program. The server manages a number of client accounts, each having a username and a password. The server program starts first. When a client process is started, it repeatedly shows the following menu to the user and asks the user for an option:

0. Connect to the server
1. Get the user list
2. Send a message
3. Get my messages
4. Exit

If the user chooses Option 0, the client program establishes a TCP connection with the server. Then, it prompts the user to log in with their username and password. The client program then sends the credentials to the server for verification. If what the user entered matches the username and password stored on the server, grant the user access by printing "Access Granted" at the client. Otherwise, print "Access Denied – Username/Password Incorrect" and ask the user for the credentials until the user gets them correct to successfully log onto the server. If the user chooses Option 1, the client program receives the list of usernames from the server and prints them out to the user. If the user chooses Option 2, the user can leave a text message to another user. The client program prompts the user for the message's recipient and sends this text message to the server, and the server stores it for the recipient.
If the user chooses Option 3, they can retrieve their own text messages left by other users on the server. The client program will display those messages to the user. If the user chooses Option 4, the client program terminates the TCP connection with the server and exits.
