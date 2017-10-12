import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class outgoingServerThread extends Thread{

    private static PrintWriter socketOutput;
    private static Scanner socketInput;
    private static user you;
    private static Socket s;
    private static String chatRoom;
    private static serverUtills utils;
    private static Scanner console;
    String t;

    public void run(){

        console = new Scanner(System.in);
        utils = new serverUtills(socketInput,socketOutput,you,s,chatRoom);
        System.out.println("In thread, ready to msg.");
        try {
            while (console.hasNextLine()) {
                t = console.nextLine();
                //System.out.println("Sending message:"  + t);
                utils.ircInput("MSG", t);
                while(!console.hasNextLine()){}
            }
        }catch(Exception e){
            //utils.ircInput("MSG", t);
            run();
        }
        System.out.println("Exiting scanner");
    }

    Thread runner;
    public outgoingServerThread(Scanner scanner, PrintWriter printWriter, user u, Socket socket, String threadName, String roomName) {
        socketOutput = printWriter;
        socketInput = scanner;
        you = u;
        s = socket;
        chatRoom = roomName;
        runner = new Thread(this, threadName);
        runner.start();
    }
}
