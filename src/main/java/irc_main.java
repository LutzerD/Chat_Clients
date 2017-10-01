import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class irc_main {

    private static String nick;
    private static String username;
    private static String realName;
    private static PrintWriter out;
    private static Scanner in;
    private static String fullMessage;

    public static void main(String[] args) throws IOException {
        System.out.println("What's good world");
        Scanner console = new Scanner(System.in);

        System.out.println("Enter a nickname please");
        nick = console.nextLine();

        System.out.println("Enter a username please");
        username = console.nextLine();

        System.out.println("Enter a realname please");
        realName = console.nextLine();

        Socket socket = new Socket("chat.freenode.net", 6667);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        write("NICK",nick);
        write("USER",username + " 0 * :" + realName);
        write("JOIN", "##ece-usask");

        while (in.hasNext()){
            String serverMessage = in.nextLine();
            System.out.println("<<<" + serverMessage);
            if (serverMessage.startsWith("PING")){
                String m = " ";
                String pingContents = serverMessage.split(m,2)[1];
                write("PONG",pingContents);
                write("MSG","/LUSER");
            }

        }
        in.close();
        out.close();
        socket.close();


        System.out.println("Done!");
    }

    private static void write(String command, String msg) {
        if (command == "MSG"){

            fullMessage = "PRIVMSG ##ece-usask " + msg;
            out.print(fullMessage  + "\r\n");
        }else {

            fullMessage = command +" "+ msg;
            out.print(fullMessage + "\r\n");
        }
        System.out.println(">>>" + fullMessage);

        out.flush();
    }
}
