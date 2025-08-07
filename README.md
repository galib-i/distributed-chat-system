# Distributed Chat System
A GUI-based networked distributed system for a group-based client-server communication, for COMP1549:

- Automatic coordinator assignment and failover
- Public messaging and private chat via right-click
- Member details view and real-time message logs
- Activity detection
- Connection fault tolerance

## Installation
### Requirements

- OS: Windows, macOS, or Linux  
- Runtime: Java 17+
- Build tool: Maven or manual compilation

### Steps
1. Download manually or through git:
```bash
git clone https://github.com/galib-i/distributed-chat-system.git
```
2. Compile and run using your preferred IDE or command line
## Usage
1. __Start the server__ (Server.java): the server will run on the configured address:port
2. __Start the client(s)__ (Client.java): select a username and join the configured address:port
   
<img width="293" height="278" alt="image" src="https://github.com/user-attachments/assets/efe838de-d332-49f2-823c-fcaed7440a1b" />

3. __Chat away!__
<img width="686" height="393" alt="image" src="https://github.com/user-attachments/assets/1a9a1a02-f235-48d3-ae59-eec4fb98c6c2" />

<img width="686" height="393" alt="image" src="https://github.com/user-attachments/assets/50824bbe-1a54-48ae-812f-e32a56232070" />

<img width="686" height="393" alt="image" src="https://github.com/user-attachments/assets/7b24d81f-ae93-4365-bded-ace29c82764b" />

## Configuration
IP and Port of the server to be run:
```yaml
default.server.ip=localhost
default.server.port=1549
```
