package main;

import com.nsprod.engine.core.GameObject;
import com.nsprod.engine.core.Handler;
import com.nsprod.engine.helpers.ID;
import networking.ConnectedClient;
import networking.GameServer;
import networking.PacketBallPos;

import java.awt.*;
import java.util.Random;

public class Ball extends GameObject {
    private final boolean isHost;
    public Ball(ID id, boolean isHost) {
        super(id);

        this.isHost = isHost;

        if(isHost)
        {
            init();
        }
    }

    private void init()
    {
        Random random = new Random();
        int speedX = random.nextInt(2, 4);
        int speedY = random.nextInt(-3, 3);

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
                boolean hConst = player.isSelf() ? x <= player.getX() + player.getWidth() : x >= player.getX();

                if(vConst && hConst)
                    velX *= -1;
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
            System.out.println("X sent: " + x);
            System.out.println("Y sent: " + y);
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
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
}
