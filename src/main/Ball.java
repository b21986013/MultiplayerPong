package main;

import com.nsprod.engine.core.GameObject;
import com.nsprod.engine.core.Handler;
import networking.ConnectedClient;
import networking.GameServer;
import networking.PacketBallPos;
import networking.PacketScoreboard;

import java.awt.*;
import java.util.Random;

public class Ball extends GameObject {
    private final boolean isHost;
    public Ball(String id, boolean isHost) {
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
        for(GameObject go : Handler.getGameObjects())
        {
            if(go.getID().equals("Player"))
            {
                PLayer player = (PLayer)go;
                boolean vConst = y >= player.getY()-height/2 && y <= player.getY() + player.getHeight() + height/2;
                boolean hConst = player.isSelf() ? x <= player.getX() + player.getWidth() : x >= player.getX() - width;

                if(vConst && hConst) {
                    if(player.getPrevY() < player.getY() && velY < 0 || player.getPrevY() > player.getY() && velY > 0){
                        velX *= -1.3;
                        velY *= 1.5;
                    } else if (player.getPrevY() < player.getY() && velY > 0 || player.getPrevY() > player.getY() && velY < 0) {
                        velX *= -1.3;
                        velY *= 1.1;
                    }
                    else{
                        velX *= -1;
                    }

                }
                else if(hConst && !vConst)
                {
                    updateScore(player);
                    sendScore();
                    initPos();
                    initVel();
                }
            }
        }
    }

    private void verticalCollision(){
        if(y <= 0 || y >= (GamePong.HEIGHT - 3 * height))
        {
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

    private void updateScore(PLayer pLayer){
        if(pLayer.isSelf())
            GamePong.clientScore += 1;
        else
            GamePong.hostScore += 1;
    }

    private void sendScore(){
        GameServer gameServer = GamePong.gameServer;
        if(gameServer != null && gameServer.getConnectedClients().size() > 0)
        {
            ConnectedClient client = gameServer.getConnectedClients().get(0);
            GamePong.gameServer.sendData(new PacketScoreboard(GamePong.hostScore + "-" + GamePong.clientScore).getData(),client.ip() , client.port());
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
        g.fillOval((int)x, (int)y, width, height);
    }
}
