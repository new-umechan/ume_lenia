import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.lang.Math;

public class LifeGame2 extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 600;
    private static final int CELL_SIZE = 5;
    private static final int GRID_WIDTH = WIDTH / CELL_SIZE;
    private static final int GRID_HEIGHT = HEIGHT / CELL_SIZE;

    private float[][] grid = new float[GRID_HEIGHT][GRID_WIDTH];
    private boolean runningSimulation = false;
    private Timer timer;

    public LifeGame2() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT + 50));
        this.setBackground(Color.BLACK);

        JButton startButton = new JButton("Start");
        startButton.setBounds(WIDTH / 2 - 110, HEIGHT + 10, 100, 30);
        startButton.addActionListener(e -> {
            runningSimulation = !runningSimulation;
            startButton.setText(runningSimulation ? "Stop" : "Start");
        });

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(WIDTH / 2 + 10, HEIGHT + 10, 100, 30);
        resetButton.addActionListener(e -> {
            createGrid();
            runningSimulation = false;
            startButton.setText("Start");
        });

        this.setLayout(null);
        this.add(startButton);
        this.add(resetButton);

        createGrid();

        timer = new Timer(100, this);
        timer.start();
    }

    private void createGrid() {
        Random rand = new Random();
        // グリッドを初期化
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid[y][x] = 0.0f;
            }
        }

        int side = 15;  /* 一辺 */
        for (int n = 0; n < 16; n++) {
            int startX = rand.nextInt(GRID_WIDTH - side);
            int startY = rand.nextInt(GRID_HEIGHT - side);
            for (int y = startY; y < startY + side; y++) {
                for (int x = startX; x < startX + side; x++) {
                    grid[y][x] = 0.4f + rand.nextFloat() * 0.6f; // 0.4から0.9の乱数
                }
            }
        }
    }

    private float calculateWeight(int dx, int dy, int peakDistance, float sigma) {
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        // 距離がピーク距離で最大になるように重みを計算
        return (float) Math.exp(-Math.pow((distance - peakDistance), 2) / (2 * sigma * sigma));
    }
    
    private float calculateAverageLife(float[][] grid, int x, int y, int maxRadius, int centerX, int centerY, int peakDistance, float sigma) {
        float totalLife = 0.0f;
        float totalWeight = 0.0f;
        for (int i = -maxRadius; i <= maxRadius; i++) {
            for (int j = -maxRadius; j <= maxRadius; j++) {
                int distanceSquared = i * i + j * j;
                if (distanceSquared <= maxRadius * maxRadius) {
                    float weight = calculateWeight(i, j, peakDistance, sigma);
                    int ni = (x + i + GRID_WIDTH) % GRID_WIDTH;
                    int nj = (y + j + GRID_HEIGHT) % GRID_HEIGHT;
                    totalLife += grid[nj][ni] * weight;
                    totalWeight += weight;
                }
            }
        }
        return totalLife / totalWeight; // 合計重みで割って平均を計算
    }

    private void updateGrid() {
        float[][] newGrid = new float[GRID_HEIGHT][GRID_WIDTH];
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                float averageLife = calculateAverageLife(grid, x, y, 12, x, y, 7, 1.8f);
                if (0.25 <= averageLife && averageLife <= 0.35){
                newGrid[y][x] = Math.min(1, grid[y][x] + 0.07f); // 少しずつ生きる
                } else {
                    newGrid[y][x] = Math.max(0, grid[y][x] - 0.07f); // 少しずつ死ぬ
                }
            }
        }
        grid = newGrid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (runningSimulation) {
            updateGrid();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                int shade = (int)(grid[y][x] * 255);
                g.setColor(new Color(shade, shade, shade));
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Life Game");
        LifeGame2 lifeGame2 = new LifeGame2();
        frame.add(lifeGame2);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
