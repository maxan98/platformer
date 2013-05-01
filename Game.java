import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas {
    BufferStrategy bf;
    int xRes, yRes;
    Tiles tiles;
    InputListener input;
    Player player;

    public Game(GraphicsDevice device) {
        
        this.xRes = 25*32;
        this.yRes = 18*32;
        
        JFrame container = new JFrame("Game");
        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(xRes, yRes));
        panel.setLayout(null);
        
        setBounds(0,0,xRes,yRes);
        panel.add(this);
        setIgnoreRepaint(true);        

        container.pack();
        container.setResizable(false);
        container.setVisible(true);
        
        createBufferStrategy(2);            
        bf = getBufferStrategy();

        // Set up game world
        player = new Player(50,50);

        tiles = new Tiles(25, 18, 32);
        for (int i = 0; i < tiles.getWidth(); i++) {
            for (int j = 0; j < tiles.getWidth(); j++) {
                if (i == 0 || i == tiles.getWidth() - 1 
                    || j == 0 || j == tiles.getHeight() - 1) {
                    tiles.setTile(i, j, "./assets/tiles/block.png", true);
                } else {
                    tiles.setTile(i, j, "./assets/tiles/background.png", false);
                }
            }
        }

        // Set up input listeners
        input = new InputListener(this);
    }

    public void handleInput() {
        if (input.mouseDown()) {
            int x = input.getMouseX();
            int y = input.getMouseY();
            tiles.setTile(tiles.pixToTile(x), tiles.pixToTile(y),
                          "./assets/tiles/block.png", true);
        }

        player.handleInput(tiles, input);
    }

    public void update(long delta) {
        player.updatePhysics(tiles, delta);
        player.move(tiles, delta);
    }

    public void draw() {
        Graphics g = null;
        
        try {
            g = bf.getDrawGraphics();

            tiles.draw(g);
            player.draw(g);
            
        } finally {
            g.dispose();
        }

        bf.show();        
    }

    static Game game;

    public static void init() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        game = new Game(device);

        Thread loop = new Thread()
            {
                public void run()
                {
                    loop();
                }
            };
        loop.start();
    }

    public static void loop() {
        long millisecondsPerFrame = 1;
        long beginningTime;
        long endTime;
        long lastFrameTime = millisecondsPerFrame;
        for ( ; ; ) {
            beginningTime = System.currentTimeMillis();
            
            game.handleInput();
            game.update(lastFrameTime);
            game.draw();
            
            
            while (System.currentTimeMillis() - beginningTime < millisecondsPerFrame)
                {
                }
            
            lastFrameTime = System.currentTimeMillis() - beginningTime; 
            
            StdOut.printf("FPS = %d\n", 1000 / lastFrameTime);
 
        }
    }

    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    init();
                }
            });
    }
}