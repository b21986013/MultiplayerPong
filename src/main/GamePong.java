package main;

import com.nsprod.engine.core.Game;

import com.nsprod.engine.core.Handler;
import com.nsprod.engine.helpers.ID;
import networking.GameClient;
import networking.GameServer;
import networking.PacketLogin;

import javax.swing.*;
import java.awt.*;
public class GamePong extends Game {
    public static int WIDTH, HEIGHT;
    public static GameServer gameServer;
    public static GameClient gameClient;

    public GamePong(int w, int h, String title) {
        super(w, h, title);

        WIDTH = w;
        HEIGHT = h;

        int ans = JOptionPane.showConfirmDialog(null, "Play as a HOST ?");
        if(ans == 0)
            initHost();
        else if(ans == 1)
            initClient();
        else
            System.exit(0);
    }


    private void initHost()
    {

        KeyInput keyInput = new KeyInput();

        int playerWidth = 15, playerHeight = 75;
        int playerStartPosX = 50, playerStartPosY = HEIGHT/2-playerHeight/2;

        PLayer self = new PLayer(ID.Player, true);
        self.setKeyInput(keyInput);
        self.setSize(playerWidth,playerHeight);
        self.setPosition(playerStartPosX, playerStartPosY);

        int ballWidth = 15, ballHeight = 15;
        int ballStartPosX = WIDTH/2, ballStartPosY = HEIGHT/2;

        Ball ball = new Ball(ID.Object, true);

        ball.setSize(ballWidth, ballHeight);
        ball.setPosition(ballStartPosX, ballStartPosY);

        Handler.addGameObject(self);
        Handler.addGameObject(ball);

        this.addKeyListener(keyInput);

        gameServer = new GameServer( );
        gameServer.start();
    }

    private void initClient(){
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter host IP:");
        JTextField ipField = new JTextField(10);
        panel.add(label);
        panel.add(ipField);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Host to connect ?",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        if(option == 0)
        {
            KeyInput keyInput = new KeyInput();

            int playerWidth = 15, playerHeight = 75;
            int playerStartPosX = WIDTH - 50, playerStartPosY = HEIGHT/2-playerHeight/2;

            PLayer self = new PLayer(ID.Player, true);
            self.setPosition(playerStartPosX, playerStartPosY);
            self.setSize(playerWidth, playerHeight);
            self.setKeyInput(keyInput);

            PLayer hostPlayer = new PLayer(ID.Player, false);
            hostPlayer.setSize(playerWidth, playerHeight);

            int ballWidth = 15, ballHeight = 15;

            Ball ball = new Ball(ID.Object, false);
            ball.setSize(ballWidth, ballHeight);

            gameClient = new GameClient(ipField.getText(), ball, hostPlayer);
            gameClient.start();

            PacketLogin packetLogin = new PacketLogin("enes");
            gameClient.sendData(packetLogin.getData());

            Handler.addGameObject(ball);
            Handler.addGameObject(hostPlayer);
            Handler.addGameObject(self);

            this.addKeyListener(keyInput);
        }
        else
            System.exit(0);
    }

    @Override
    public void tick() {
        Handler.tick();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        Handler.render(g);
    }

    public static void main(String[] args) {
        new GamePong(800, 600, "Pong Multiplayer");
    }
}
