import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas {
    BufferStrategy bf;
    int xSize, ySize;
    int xRes, yRes;
    Tiles tiles;
    InputListener input;
    Player player;
    Editor editor;
    boolean editorActive;
    BufferedImage render;
    Camera camera;
    long mainFPS, renderFPS;

    private void gameInit(GraphicsDevice device) {
        this.xRes = 20*16;
        this.yRes = 20*16;

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
        
        // Set up the render. We draw to the render, which is then clipped
        // by the camera and drawn to the screen
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        render = gc.createCompatibleImage(xSize, ySize,
                                          Transparency.BITMASK);
        
        camera = new Camera(xRes, yRes, 0, 0, Camera.SMART);

        // Set up input listeners
        input = new InputListener(this);
        
        // Set up the world editor
        editor = new Editor(tiles);
    }

    public Game(GraphicsDevice device, String filename) {
        tiles = new Tiles("./assets/levels/" + filename);
        this.xSize = tiles.getWidth() * tiles.getPixPerTile();
        this.ySize = tiles.getHeight() * tiles.getPixPerTile();

        player = new Player(tiles);
        gameInit(device);
    }

    public Game(GraphicsDevice device, int xTiles, int yTiles) {
        // Set up game world
        tiles = new Tiles(xTiles, yTiles, "./assets/tilesets/default.txt");
        for (int i = 0; i < tiles.getWidth(); i++) {
            for (int j = 0; j < tiles.getWidth(); j++) {
                if (i == 0 || i == tiles.getWidth() - 1 
                    || j == 0 || j == tiles.getHeight() - 1) {
                    tiles.setTile(i, j, 1);
                } else {
                    tiles.setTile(i, j, 0);
                }
            }
        }

        this.xSize = xTiles * tiles.getPixPerTile();
        this.ySize = yTiles * tiles.getPixPerTile();
        
        player = new Player(tiles);

        gameInit(device);
    }

    public void handleInput(long delta) {
        if (input.getKeyTyped(InputListener.MODE_SWITCH)) {
            editorActive = !editorActive;
            input.clearAllKeysTyped();
        }

        if (editorActive) {
            editor.handleInput(input, camera);
        } else {
            player.handleInput(input, delta);
        }
    }

    public void update(long delta) {
        if (editorActive) { 
            
        } else {
            player.updatePhysics(delta);
            player.move(delta);            
        }

        camera.update(player, tiles, delta);
    }

    public void draw() {
        Graphics gRender = render.getGraphics();
        tiles.draw(gRender);
        player.draw(gRender);
        
        if (editorActive) {
            editor.draw(gRender);
        }
    }

    public void drawFPS(Graphics g) {
        String mainFPSString = String.format("FPS (main): %d", mainFPS);
        String renderFPSString = String.format("FPS (rendering): %d", renderFPS);
        
        g.drawChars(mainFPSString.toCharArray(),
                    0, mainFPSString.length(),
                    20, 20);
        g.drawChars(renderFPSString.toCharArray(),
                    0, renderFPSString.length(),
                    150, 20);
    }
    
    public void render() {
        Graphics g = null;
        
        try {
            g = bf.getDrawGraphics();
            camera.render(g, render);
            drawFPS(g);

        } finally {
            g.dispose();
        }

        bf.show();        
    }

    static Game game;

    public static void init(String filename) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        if (filename != null) {
            game = new Game(device, filename);
        } else {
            game = new Game(device, 80, 50);
        }
        Thread mainLoop = new Thread()
            {
                public void run()
                {
                    mainLoop();
                }
            };

        Thread renderLoop = new Thread()
            {
                public void run()
                {
                    renderLoop();
                }
            };

        mainLoop.start();
        renderLoop.start();
    }

    public static void mainLoop() {
        long millisecondsPerFrame = 5;
        long beginningTime;
        long endTime;
        long lastFrameTime = millisecondsPerFrame;
        for ( ; ; ) {
            beginningTime = System.currentTimeMillis();
            
            game.handleInput(lastFrameTime);
            game.update(lastFrameTime);
            
            while (System.currentTimeMillis() - beginningTime < millisecondsPerFrame)
                {
                }
            
            lastFrameTime = System.currentTimeMillis() - beginningTime; 
            
            game.mainFPS = 1000 / lastFrameTime;
        }
    }

    public static void renderLoop() {
        long millisecondsPerFrame = 16;
        long beginningTime;
        long endTime;
        long lastFrameTime = millisecondsPerFrame;
        for ( ; ; ) {
            beginningTime = System.currentTimeMillis();
            
            game.draw();
            game.render();
            
            while (System.currentTimeMillis() - beginningTime < millisecondsPerFrame)
                {
                }
            
            lastFrameTime = System.currentTimeMillis() - beginningTime; 
            
            game.renderFPS = 1000 / lastFrameTime;
         }
    }

    public static void main(String args[]) {
        final boolean has_level = args.length > 0;
        String levelfoo = null;
        if (has_level) {
            levelfoo= args[0];
        }
        final String level = levelfoo;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    init(level);
                }
            });
    }
}