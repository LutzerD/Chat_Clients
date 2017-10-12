public class serverMessage {
    private static String serverMessage;
    private static String chatRoom;
    private static int serverMessageLength;

    public serverMessage(String currentChatRoom){
        chatRoom = currentChatRoom;
    }

    public static void setMessage(String m){
        serverMessage = m;
        serverMessageLength = serverMessage.length();
    }

    public static String getMessage(){
        return serverMessage;
    }

    //TODO: dealing with users joining / leaving
    /*
    Current returns:
    {
    "Joined" : You have entered chatroom
    "NickUsed" : Nickname is currently in use
    "TimeOut" : chatroom is timedout (force booted?)
    "MessageRecieved" : incoming message from other user
    String pingcontents : The unique server-based string that is responded to so there is no timeout.
    "n/a" : None of the above clicked, this is the default response
    }
     */
    public static String checkServerMessage() {
        String joinedResponse =  "JOIN " + chatRoom;
        String nickInUseResponse = "Nickname is already in use.";
        String timeOutResponse = "(Connection timed out)";
        String badNick = ":Erroneous Nickname";
        String allMembers = chatRoom + " :End of /NAMES list.";
        String quitterResponse = "QUIT :";
        String serverMessageTemp;
        String responseCode;
        //Separate try catches? but try/catch latency is not optimal, :C
        //TODO: Optimize for different message lengths

        if(serverMessageLength - joinedResponse.length()>=0){
            serverMessageTemp = serverMessage.substring(serverMessage.length() - joinedResponse.length());
            if (serverMessageTemp.equals(joinedResponse)){
                return "Joined";
            }
        }

        //TODO: loop when nick used
        if(serverMessageLength - nickInUseResponse.length()>=0) {
            serverMessageTemp = serverMessage.substring(serverMessage.length() - nickInUseResponse.length());
            if (serverMessageTemp.equals(nickInUseResponse)) {
                return "NickUsed";
            }
        }

        if(serverMessageLength - timeOutResponse.length()>=0) {
            serverMessageTemp = serverMessage.substring(serverMessage.length() - timeOutResponse.length());
            if (serverMessageTemp.equals(timeOutResponse)) {
                return "TimeOut";
            }
        }
        if(serverMessageLength - badNick.length()>=0) {
            serverMessageTemp = serverMessage.substring(serverMessage.length() - badNick.length());
            if (serverMessageTemp.equals(badNick)) {
                return "badNick";
            }
        }

        if (serverMessage.startsWith("PING")) {
            String spaceBuffer = " ";
            String pingContents = serverMessage.split(spaceBuffer, 2)[1];
            return pingContents;
        }

        String spaceBuffer = " ";
        if (serverMessage.split(spaceBuffer,3)[1].equals("PRIVMSG")) {
            return "MessageRecieved";
        }


        if(serverMessageLength - allMembers.length()>=0) {
            serverMessageTemp = serverMessage.substring(serverMessage.length() - allMembers.length());
            if (serverMessageTemp.equals(allMembers)) {
                return "MemberList";
            }
        }

        //:ninnty!~bootyButt@128.233.8.104 QUIT :Remote host closed the connection
        if(serverMessage.indexOf(quitterResponse) != -1) {
                return "quitter";
        }

        return "n/a";
    }
}
