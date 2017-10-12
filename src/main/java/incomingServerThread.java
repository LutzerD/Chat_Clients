import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class incomingServerThread extends Thread {

    private static PrintWriter socketOutput;
    private static Scanner socketInput;
    private static user you;
    private static Socket s;
    private static String chatRoom;
    private static serverUtills utils;
    private static outgoingServerThread serverThreadO;
    private static boolean key;

    public void run(){

        utils = new serverUtills(socketInput,socketOutput,you,s,chatRoom);
        String serverMessageListTemp[] = new String[4];
        String serverMessageList[] = new String[4];
        String[] serverMessageTemp;
        String incomingUser;
        String incomingHostname;
        String prevMsg;

        //System.out.println("In thread, waiting to join?");
        serverMessage servermessage = new serverMessage(chatRoom);
        key = false;
        prevMsg = "";
        while (socketInput.hasNext()){
            serverMessage.setMessage(socketInput.nextLine());

            serverMessageListTemp = new String[]{serverMessageList[1], serverMessageList[2], serverMessageList[3], serverMessage.getMessage()};
            serverMessageList = serverMessageListTemp;
            //System.out.println(servermessage.getMessage());

             /*
            Current returns of .checkServerMessage():
            {
            "Joined" : You have entered chatroom
            "NickUsed" : Nickname is currently in use
            "TimeOut" : chatroom is timedout (force booted?)
            "MessageRecieved" : incoming message from other user
            String pingcontents : The unique server-based string that is responded to so there is no timeout.
            "n/a" : None of the above clicked, this is the default response
            }
            */




            switch(serverMessage.checkServerMessage()){
                case "Joined" : //System.out.println("Joined room " + chatRoom); //Concern for joining multiple rooms?
                    //this only works if you are good...
                    serverMessageTemp = serverMessage.getMessage().split(":");
                    incomingUser = serverMessageTemp[1].split("!",2)[0];
                    try {
                        incomingHostname = serverMessageTemp[1].split("freenode/ip.")[1].split(" ")[0];
                    }
                    catch(Exception e){
                        incomingHostname = serverMessageTemp[1].split("@")[1].split(" ")[0];
                    }
                    if (key == false) {
                        serverThreadO = new outgoingServerThread(socketInput, socketOutput, you, s, "main_threadO", chatRoom);
                        key = true;
                    }else{
                        System.out.println(incomingHostname + "@ " + incomingUser + " has joined " + chatRoom); //person has joined the room.
                    }
                    break;
                case "NickUsed" : System.out.println("Nickname in use.");//Idk if this is a problem? since doesn't it just put @ infront of name.
                    try {
                        utils.closeConn(s,"stop", serverMessageList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "TimeOut" :
                    try {
                        utils.closeConn(s,"stop", serverMessageList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "MessageRecieved" :
                    //System.out.println("Message Recieved: " + serverMessage.getMessage());
                    //Split at ! and take ! on? maybe format ip@ name$?
                    serverMessageTemp = serverMessage.getMessage().split(":");
                    incomingUser = serverMessageTemp[1].split("!",2)[0];
                    try {
                        incomingHostname = serverMessageTemp[1].split("freenode/ip.")[1].split(" ")[0];
                    }
                    catch(Exception e){
                        incomingHostname = serverMessageTemp[1].split("@")[1].split(" ")[0];
                    }
                    System.out.println("->" + incomingHostname + "@ " + incomingUser + "$ " + serverMessageTemp[2]);
                    //utils.ircInput("MSG", "please");
                    break;
                case "badNick":
                    //restart chat with " " replaced with ""
                    break;
                case "MemberList":
                    System.out.println("Current users in room: " + prevMsg.split(":")[2].replace(" ", ", "));
                    break;
                case "quitter":
                    serverMessageTemp = serverMessage.getMessage().split(":");
                    incomingUser = serverMessageTemp[1].split("!",2)[0];
                    try {
                        incomingHostname = serverMessageTemp[1].split("freenode/ip.")[1].split(" ")[0];
                    }
                    catch(Exception e) {
                        incomingHostname = serverMessageTemp[1].split("@")[1].split(" ")[0];
                    }
                    System.out.println(incomingHostname + "@ " + incomingUser + " has quit " + chatRoom); //person has joined the room.

                case "n/a" :
                    break;
                //Having the pong response as default is probably pretty sloppy programming but life is tough.
                default: utils.ircInput("PONG", serverMessage.checkServerMessage());

            }
            prevMsg = serverMessage.getMessage();
        }
        try {
            utils.closeConn(s,"stop" , serverMessageList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Thread runner;
    public incomingServerThread(Scanner scanner, PrintWriter printWriter, user u, Socket socket, String threadName, String roomName){
        socketOutput = printWriter;
        socketInput = scanner;
        you = u;
        s = socket;
        chatRoom = roomName;
        runner = new Thread(this, threadName);
        runner.start();
    }
}
