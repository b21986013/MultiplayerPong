package networking;

import com.nsprod.engine.core.Handler;
import com.nsprod.engine.helpers.ID;
import main.Ball;
import main.PLayer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread
{
    private DatagramSocket socket;
    private final int port = 1300;

    private PLayer clientPlayer;
    public List<ConnectedClient> getConnectedClients() {
        return connectedClients;
    }

    private List<ConnectedClient> connectedClients = new ArrayList<>();

    public GameServer( )
    {

        try {
            socket = new DatagramSocket(port);
        }
        catch (SocketException e) {
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

    public void sendData(byte[] data, InetAddress client, int port)
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, client, port);

        try {
            socket.send(packet);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data)
    {
        for(ConnectedClient connectedClient : connectedClients)
        {
            sendData(data, connectedClient.ip(), connectedClient.port());
        }
    }

    private void parsePacket(byte[] data, InetAddress ipAddress, int port)
    {
        String message = new String(data).trim();

        Packet packet = null;
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        switch (type) {
            case INVALID:
                System.out.println("INVALID PACKET from a client");
                break;
            case LOGIN:
                packet = new PacketLogin(data);

                System.out.println("[" + ipAddress.getHostAddress() + ":" + port + "]" + ((PacketLogin) packet).getMSG() + " has connected...");

                connectedClients.add(new ConnectedClient(ipAddress, port));
                int playerWidth = 15, playerHeight = 75;
                clientPlayer = new PLayer("Player", false);
                clientPlayer.setSize(playerWidth, playerHeight);

                int ballWidth = 15, ballHeight = 15;

                Ball ball = new Ball("Ball", true);
                ball.setSize(ballWidth, ballHeight);

                Handler.addGameObject(clientPlayer);
                Handler.addGameObject(ball);

                break;
            case PLAYERPOSITION:
                packet = new PacketPlayerPos(data);
                clientPlayer.setPosition((int)((PacketPlayerPos) packet).getPosX(), (int)((PacketPlayerPos) packet).getPosY());
                break;
        }
    }

}
