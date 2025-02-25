package main;

import com.nsprod.engine.core.GameObject;
import com.nsprod.engine.helpers.ID;
import networking.*;

import java.awt.*;

public class PLayer extends GameObject {
    private boolean isSelf;
    private KeyInput keyInput;
    private int speed = 5;

    public PLayer(ID id, boolean isSelf) {
        super(id);

        this.isSelf = isSelf;

        velY = 0;
    }

    @Override
    public void tick() {

        if(isSelf)
        {
            y += velY;
            if(keyInput != null){
                if(keyInput.isDown())
                    velY = speed;
                else if (keyInput.isUp())
                    velY = -speed;
                else
                    velY = 0;
            }

            if(GamePong.gameServer != null)
            {
                GameServer gameServer = GamePong.gameServer;
                if(gameServer.getConnectedClients().size() > 0) {
                    ConnectedClient client = gameServer.getConnectedClients().get(0);
                    gameServer.sendData(new PacketPlayerPos(x, y).getData(), client.ip(), client.port());
                }
            }
            else if (GamePong.gameClient != null)
            {
                GameClient gameClient = GamePong.gameClient;
                gameClient.sendData(new PacketPlayerPos(x, y).getData());
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public void setKeyInput(KeyInput keyInput)
    {
        this.keyInput = keyInput;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public boolean isSelf(){
        return isSelf;
    }

}
