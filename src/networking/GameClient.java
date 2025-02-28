package networking;

import main.Ball;
import main.GamePong;
import main.PLayer;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread
{
    private InetAddress host_ip;
    private DatagramSocket socket;

    private Ball ball;
    private PLayer hostPlayer;
    private final int port = 1300;

    public GameClient(String host_ip, Ball ball, PLayer hostPlayer)
    {
        this.hostPlayer = hostPlayer;
        this.ball = ball;
        try{
            socket = new DatagramSocket();
            this.host_ip = InetAddress.getByName(host_ip);
        }
        catch (SocketException | UnknownHostException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

            try {
                socket.receive(packet);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    public void sendData(byte[] data)
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, host_ip, port);
        try {
            socket.send(packet);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parsePacket(byte[] data, InetAddress ipAddress, int port)
    {
        String message = new String(data).trim();

        Packet packet = null;
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        switch (type) {
            case INVALID:
                System.out.println("INVALID PACKET from the server");
                break;
            case LOGIN:
                packet = new PacketLogin(data);
                System.out.println("[" + ipAddress.getHostAddress() + ":" + port + "]" + ((PacketLogin) packet).getMSG() + " has joined...");
                break;
            case BALLPOSITION:
                packet = new PacketBallPos(data);
                ball.setPosition(((PacketBallPos) packet).getPosX(), ((PacketBallPos) packet).getPosY());
                break;
            case PLAYERPOSITION:
                packet = new PacketPlayerPos(data);
                hostPlayer.setPosition((int)((PacketPlayerPos) packet).getPosX(), (int)((PacketPlayerPos) packet).getPosY());
                break;
            case SCOREBOARD:
                packet = new PacketScoreboard(data);
                GamePong.clientScore = Integer.valueOf (((PacketScoreboard) packet).getClientScore());
                GamePong.hostScore = Integer.valueOf (((PacketScoreboard) packet).getHostScore());
                break;
        }
    }
}
