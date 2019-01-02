import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class serverUtills {
    private static PrintWriter socketOutput;
    private static Scanner socketInput;
    private static user you;
    private static Socket s;
    private static String chatRoom;


    public serverUtills(Scanner scanner, PrintWriter printWriter, user u, Socket socket, String roomName){
            socketOutput = printWriter;
            socketInput = scanner;
            you = u;
            s = socket;
            chatRoom = roomName;
    }

    public static void closeConn(Socket s, String directive, String[] serverMessageList) throws IOException {
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

    public static void ircInput(String directive, String msg) {

        String fullMessage;
        if (directive == "MSG"){
            fullMessage = "PRIVMSG " + chatRoom + " :"+  msg;
            //System.out.println("<-localhost@ " + you.getPref("NICK") +"$ "+ fullMessage);
        }else if(directive == "NICK") {
            fullMessage = directive +" :"+ msg;
        } else {
            fullMessage = directive +" "+ msg;
        }

        socketOutput.print(fullMessage + "\r\n");
        socketOutput.flush();
    }
}
