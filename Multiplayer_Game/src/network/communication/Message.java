package network.communication;

public class Message {
    // Use these to send messages of different "types".
    // With bytes you can only have 255 different Messages.
    // Use "int" for example, to enable ~4.294.967.300 different Messages.
    public static final byte DISCONNECT = 1;
    public static final byte FIRSTCONNECT = 2;
    public static final byte PLAYERID = 3;

    // The Message itself, so either 1 or 2.
    public byte msgType;
    public byte msgValue;
}