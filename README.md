# DevicesCheckBot

This is an application for computer clubs that will help them keep track of devices connected to computers.

This application works as a server for the telegram bot and at the same time receives messages from the client part located on the computers in the computer club.

The application occupies port 4242, so it must be open in the firewall.
For the bot to work you need to create an application.properties file which will contain the telegram bot token.

Used technologies and libraries: Spring, lombok, netty, telegrambots.


The client part is located in the repository at the link: https://github.com/vbrks/DevicesCheckClient
