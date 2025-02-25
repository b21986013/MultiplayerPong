package networking;

public class PacketBallPos extends Packet{

    private byte[] data;
    private int posX, posY;

    // Use this for parsing incoming packets.
    public PacketBallPos(byte[] data)
    {
        super(02);
        String pos = readMessage(data);
        posX = Integer.valueOf(pos.substring(0,3));
        posY = Integer.valueOf(pos.substring(3,6));
    }

    // Use this for sending packets.
    public PacketBallPos(int posX, int posY)
    {
        super(02);
        this.posX = posX;
        this.posY = posY;
    }

    // Use this for sending packets.
    @Override
    public byte[] getData()
    {
        String posXStr = posX >= 100 ? String.valueOf(posX) : posX < 100 && posX >= 10 ? "0" + posX : posX < 10 && posX >= 0 ? "00" + posX : "000";
        String posYStr = posY >= 100 ? String.valueOf(posY) : posY < 100 && posY >= 10 ? "0" + posY : posY < 10 && posY >= 0 ? "00" + posY : "000";
        data = ("02" + posXStr + posYStr).getBytes();
        return data;
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return  posY;
    }

}
