package networking;

public class PacketBallPos extends Packet{

    private byte[] data;
    private float posX, posY;

    // Use this for parsing incoming packets.
    public PacketBallPos(byte[] data)
    {
        super(02);
        String pos = readMessage(data);
        posX = Integer.valueOf(pos.substring(0,3));
        posY = Integer.valueOf(pos.substring(3,6));
    }

    // Use this for sending packets.
    public PacketBallPos(float posX, float posY)
    {
        super(02);
        this.posX = posX;
        this.posY = posY;
    }

    // Use this for sending packets.
    @Override
    public byte[] getData()
    {
        String posXStr = (int)posX >= 100 ? String.valueOf((int)posX) : (int)posX < 100 && (int)posX >= 10 ? "0" + (int)posX : (int)posX < 10 && (int)posX >= 0 ? "00" + (int)posX : "000";
        String posYStr = (int)posY >= 100 ? String.valueOf((int)posY) : (int)posY < 100 && (int)posY >= 10 ? "0" + (int)posY : (int)posY < 10 && (int)posY >= 0 ? "00" + (int)posY : "000";
        data = ("02" + posXStr + posYStr).getBytes();
        return data;
    }

    public float getPosX(){
        return posX;
    }

    public float getPosY(){
        return  posY;
    }

}
