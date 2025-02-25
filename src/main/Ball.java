package main;

import com.nsprod.engine.core.GameObject;
import com.nsprod.engine.core.Handler;
import com.nsprod.engine.helpers.ID;
import networking.ConnectedClient;
import networking.GameServer;
import networking.PacketBallPos;
import networking.PacketScoreboard;

import java.awt.*;
import java.util.Random;

public class Ball extends GameObject {
    private final boolean isHost;
    public Ball(ID id, boolean isHost) {
        super(id);

        this.isHost = isHost;

        if(isHost)
        {
            initPos();
            initVel();
        }
    }

    public void initPos()
    {
        x = GamePong.WIDTH/2;
        y = GamePong.HEIGHT/2;
    }

    private void initVel()
    {
        Random random = new Random();

        int speedX = random.nextInt(2) < 1 ? random.nextInt(-3, -1) :  random.nextInt(1, 3);
        int speedY = random.nextInt(2) < 1 ? random.nextInt(-3, -1) :  random.nextInt(1, 3);

        velX = -speedX;
        velY = speedY;
    }

    private void horizontalCollision()
    {
//        if (x >= GamePong.WIDTH || x <= 0)
//            velX *= -1;
        for(GameObject go : Handler.getGameObjects())
        {
            if(go.getID() == ID.Player)
            {
                PLayer player = (PLayer)go;
                boolean vConst = y > player.getY() && y < player.getY() + player.getHeight();
                boolean hConst = player.isSelf() ? x <= player.getX() + player.getWidth() : x >= player.getX() - width;

                if(vConst && hConst) {
                    velX *= -2;
                }
                else if(hConst && !vConst)
                {
                    initPos();
                    initVel();
                    if(player.isSelf())
                        GamePong.clientScore += 1;
                    else
                        GamePong.hostScore += 1;

                    GameServer gameServer = GamePong.gameServer;
                    if(gameServer != null && gameServer.getConnectedClients().size() > 0)
                    {
                        ConnectedClient client = gameServer.getConnectedClients().get(0);
                        GamePong.gameServer.sendData(new PacketScoreboard(GamePong.hostScore + "-" + GamePong.clientScore).getData(),client.ip() , client.port());
                    }
                }
            }
        }
    }

    private void verticalCollision(){
        if(y <= 0 || y >= (GamePong.HEIGHT - 3 * height))
        {
            System.out.println("height: " + height);
            velY *= -1;
        }
    }

    private void move()
    {
        x += velX;
        y += velY;
    }

    private void sendPos()
    {
        GameServer gameServer = GamePong.gameServer;

        if(gameServer != null && gameServer.getConnectedClients().size() > 0)
        {
            ConnectedClient client = gameServer.getConnectedClients().get(0);
            GamePong.gameServer.sendData(new PacketBallPos(x, y).getData(),client.ip() , client.port());

        }
    }

    @Override
    public void tick() {
        // if the instance is a host do the stuff otherwise just get the positions and render.
        if(isHost)
        {
            move();
            horizontalCollision();
            verticalCollision();
            sendPos();
        }

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillOval(x, y, width, height);
    }
}
