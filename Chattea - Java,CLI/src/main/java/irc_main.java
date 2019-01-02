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
    private static user u;
    private static incomingServerThread serverThreadI;

    private static serverUtills utils;

    public static void main(String[] args) throws IOException {

        Socket s = new Socket("chat.freenode.net", 6667);

        socketOutput = new PrintWriter(s.getOutputStream(), true);
        socketInput = new Scanner(s.getInputStream());

        System.out.println("Terminal Chat V.XY");
        System.out.println("Author: David Lutzer(tm)");

        //TODO: Include input validation -> alpha numeric
        //TODO: Edit names such that nickname is always ok. i.e, make nickname alphabetica
        //TODO: Make user class
        //TODO: PGP encryption for the messages -> would be cool to learn
        //TODO: BOT? Or some sort of help command like list users
        // the append random string to it, concatenated with character such ad ~. from
        // there you may .split to recieve real name.

        u = new user();

        //do these need to all be done? or is just chatRoom used? idk life mate

        username = user.getPref("USER");
        nick = user.getPref("NICK");
        realName = user.getPref("rName");
        chatRoom = user.getPref("CHATROOM");

        System.out.println("Loading room... (10s Please)");
        utils = new serverUtills(socketInput,socketOutput,u,s,chatRoom);

        utils.ircInput("NICK",nick);
        utils.ircInput("USER",username + " 0 * :" + realName);
        utils.ircInput("JOIN", chatRoom);

        serverThreadI = new incomingServerThread(socketInput,socketOutput,u,s,"main_threadI",chatRoom);
        //


    }
}
