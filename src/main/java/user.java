import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class user {
    private static String nick;
    private static String username;
    private static String realName;
    private static String chatRoom;
    private static String chatRoomTemp;
    private static Map<String, String> preferences;
    private static Scanner console;


    public user() {

        System.out.println("Hi I am the constructor, I am running.");
        console = new Scanner(System.in);
        chatRoom = "##ece-usask";


        //Initialize preferences
        preferences = new HashMap<String, String>();
        preferences.put("NICK", "");
        preferences.put("USER", "bootyButts");
        preferences.put("RNAME", "Peebles Jim");
        preferences.put("CHATROOM", chatRoom);

        //TODO: if preferences != real {Get user input}
        getInput();
    }

    private static void getInput() {
        System.out.println("Enter a nickname please");
        preferences.put("NICK", console.nextLine());

        //TODO: Add random usernames / realnames, shits too much effort. random number from 1-26, matches with letter, gg.
        //System.out.println("Enter a username please");
        //username = console.nextLine();

        //System.out.println("Enter a realname please");
        //realName = console.nextLine();

        System.out.println("Enter a desired chatroom. Default: " + chatRoom);
        chatRoomTemp = console.nextLine();
        if (chatRoomTemp.length() > 2) {
            chatRoomTemp = console.nextLine();
        }
    }

    public static String getPref(String key) {
        return preferences.get(key);
    }
}
