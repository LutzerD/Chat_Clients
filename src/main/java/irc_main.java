import com.sun.org.apache.bcel.internal.generic.SWITCH;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class irc_main {

    private static String nick;
    private static String username;
    private static String realName;
    private static PrintWriter socketOutput;
    private static Scanner socketInput;
    private static String fullMessage;
    private static String chatRoom;
    private static String chatRoomTemp;



    public static void main(String[] args) throws IOException {

        Socket s = new Socket("chat.freenode.net", 6667);
        socketOutput = new PrintWriter(s.getOutputStream(), true);
        socketInput = new Scanner(s.getInputStream());

        //TODO: Include input validation -> alpha numeric
        //TODO: Edit names such that nickname is always ok. i.e, make nickname alphabetica
        //TODO: Make user class
        //TODO: PGP encryption for the messages -> would be cool to learn
        // the append random string to it, concatenated with character such ad ~. from
        // there you may .split to recieve real name.
        System.out.println("What's good world");
        Scanner console = new Scanner(System.in);

        System.out.println("Enter a nickname please");
        nick = console.nextLine();

        //TODO: Add random usernames / realnames, shits too much effort. random number from 1-26, matches with letter, gg.
        //System.out.println("Enter a username please");
        //username = console.nextLine();
        username = "bootyButts";

        //System.out.println("Enter a realname please");
        //realName = console.nextLine();
        realName = "Peebles Jim";

        chatRoom = "##ece-usask";
        System.out.println("Enter a desired chatroom. Default: " + chatRoom);
        chatRoomTemp = console.nextLine();
        if (chatRoomTemp.length() > 2){
            chatRoomTemp = console.nextLine();
        }

        System.out.println("Loading room... (10s Please)");

        ircInput("NICK",nick);
        ircInput("USER",username + " 0 * :" + realName);
        ircInput("JOIN", chatRoom);

        boolean MOTDswitch = false;
        String[] serverMessageTemp;
        String[] serverMessageList = new String[4];
        String[] serverMessageListTemp;

        String serverMessageType;
        String incomingUser;
        String incomingHostname;
        /*
        This is the where the chat input is collected via serverMessage.
        MOTDswitch is used so that the bulk of the original content is skipped, up until the room is joined.
        */

        serverMessage servermessage = new serverMessage(chatRoom);
        while (socketInput.hasNext()){
            serverMessage.setMessage(socketInput.nextLine());

            serverMessageListTemp = new String[]{serverMessageList[1], serverMessageList[2], serverMessageList[3], serverMessage.getMessage()};
            serverMessageList = serverMessageListTemp;



            //This gotta stay in until im good :(
            //Maybe keep track of last 4 server messages
            //And print them when we r booted?
            //System.out.println("<<<" + serverMessage.getMessage());

            /*
            This is placed before the MOTDswitch is flipped because the message it's flipped on is still unwanted.
            Once the switch is flipped, the input can be cleaned / ponged.
            The main types of messages I will be getting afaik is the ping, privmsg and join(?)
            This currently only deals with ping
             */

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
                case "Joined" : System.out.println("Joined room " + chatRoom); //Concern for joining multiple rooms?
                    break;
                case "NickUsed" : System.out.println("Nickname in use.");//Idk if this is a problem? since doesn't it just put @ infront of name.
                                    closeConn(s,"stop", serverMessageList);
                    break;
                case "TimeOut" : closeConn(s,"stop", serverMessageList);
                    break;
                case "MessageRecieved" :
                    //System.out.println("Message Recieved: " + serverMessage.getMessage());
                    //Split at ! and take ! on? maybe format ip@ name$?
                    serverMessageTemp = serverMessage.getMessage().split(":");
                    incomingUser = serverMessageTemp[1].split("!",2)[0];
                    incomingHostname = serverMessageTemp[1].split("freenode/ip.")[1].split(" ")[0];
                    System.out.println(incomingHostname + "@ " + incomingUser + "$ " + serverMessageTemp[2]);
                    break;
                case "n/a" :
                    break;
                //Having the pong response as default is probably pretty sloppy programming but life is tough.
                default: ircInput("PONG", serverMessage.checkServerMessage());

            }
        }
        closeConn(s,"stop" , serverMessageList);


    }

        /*
        Once the keyword is reached, the switch (MOTDswitch) is flipped, and messages can continue to be cleaned and formatted.
        This is wrapped in a try/catch because sometimes the server message length < targetmessage length, thus causing it to check
        e.g. index(-2)
        */



    private static void closeConn(Socket s, String directive, String[] serverMessageList) throws IOException {
        //Data cleanup upon completion
        socketInput.close();
        socketOutput.close();
        s.close();
        switch(directive.toLowerCase()){
            case "r":
                break;
            case "stop":
                for (String sm : serverMessageList){
                    System.out.println(sm);
                }
                System.exit(0);
                break;
        }
    }

    /*
    This function is used to communicate with the server.
    Communication is done through the socket output.
    Directive is typically the freenode command, unless it is just a message, then MSG is fine.
     */
    private static void ircInput(String directive, String msg) {

        if (directive == "MSG"){
            fullMessage = "PRIVMSG " + chatRoom + msg;
        }else {
            fullMessage = directive +" "+ msg;
        }

        socketOutput.print(fullMessage + "\r\n");
        socketOutput.flush();
    }
}
