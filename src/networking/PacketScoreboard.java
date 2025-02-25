package networking;

public class PacketScoreboard extends Packet{
    private byte[] data;
    private String scoreboard;

    // Use this for parsing incoming packets.
    public PacketScoreboard(byte[] data)
    {
        super(04);
        this.scoreboard = readMessage(data);
    }

    // Use this for sending packets.
    public PacketScoreboard(String msg)
    {
        super(04);
        this.scoreboard = msg;
    }

    @Override
    public byte[] getData()
    {
        data = ("04" + scoreboard).getBytes();
        return data;
    }

    public String getHostScore()
    {
        return scoreboard.split("-")[0];
    }

    public String getClientScore()
    {
        return scoreboard.split("-")[1];
    }

}
