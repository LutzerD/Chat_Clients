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
        System.out.println("What's good world");
        Scanner console = new Scanner(System.in);

        System.out.println("Enter a nickname please");
        nick = console.nextLine();

        //TODO: Add random usernames / realnames, shits too much effort. random number from 1-26, matches with letter, gg.
        System.out.println("Enter a username please");
        username = console.nextLine();

        System.out.println("Enter a realname please");
        realName = console.nextLine();
        //realName = "Peebles Jim";

        chatRoom = "##ece-usask";
        System.out.println("Enter a desired chatroom. Default: " + chatRoom);
        chatRoomTemp = console.nextLine();
        if (chatRoomTemp.length() > 2){
            chatRoomTemp = console.nextLine();
        }

        System.out.println("Joining room.");

        ircInput("NICK",nick);
        ircInput("USER",username + " 0 * :" + realName);
        ircInput("JOIN", chatRoom);

        boolean MOTDswitch = false;
        String serverMessageTemp;

        /*
        This is the where the chat input is collected via serverMessage.
        MOTDswitch is used so that the bulk of the original content is skipped, up until the room is joined.

        */

        String serverMessage;
        String targetMessage = "JOIN " + chatRoom;

        while (socketInput.hasNext()){
            serverMessage = socketInput.nextLine();
            System.out.println("<<<" + serverMessage);
            /*
            This is placed before the MOTDswitch is flipped because the message it's flipped on is still unwanted.
            Once the switch is flipped, the input can be cleaned / ponged.
             */
            if(MOTDswitch == true) {
                if (serverMessage.startsWith("PING")) {
                    String spaceBuffer = " ";
                    String pingContents = serverMessage.split(spaceBuffer, 2)[1];
                    ircInput("PONG", pingContents);
                    ircInput("LUSER"," ");
                    ircInput("NAMES"," ");

                }else{
                    System.out.println("<<<" + serverMessage);
                }
            }

            /*
            Once the keyword is reached, the switch (MOTDswitch) is flipped, and messages can continue to be cleaned and formatted.
            This is wrapped in a try/catch because sometimes the server message length < targetmessage length, thus causing it to check
            e.g. index(-2)
            */
            try{
                serverMessageTemp = serverMessage.substring(serverMessage.length() - targetMessage.length());
                if (serverMessageTemp.equals(targetMessage) && MOTDswitch == false){
                    MOTDswitch = true;
                    System.out.println("Entered chat room " + chatRoom);
                } }catch (Exception e){}
        }

        //Data cleanup upon completion
        socketInput.close();
        socketOutput.close();
        s.close();
    }

    private static void ircInput(String directive, String msg) {

        if (directive == "MSG"){
            fullMessage = "PRIVMSG ##ece-usask " + msg;
        }else {
            fullMessage = directive +" "+ msg;
        }

        socketOutput.print(fullMessage + "\r\n");
        socketOutput.flush();
    }
}
