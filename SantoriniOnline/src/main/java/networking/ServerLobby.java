package networking;

public class ServerLobby {

    private static boolean isRoomAlreadyCreated;

    public ServerLobby() {
        isRoomAlreadyCreated = false;
    }

    public static boolean isRoomAlreadyCreated() {
        return isRoomAlreadyCreated;
    }

    public static void setIsRoomAlreadyCreated(boolean isRoomAlreadyCreated) {
        ServerLobby.isRoomAlreadyCreated = isRoomAlreadyCreated;
    }
}
