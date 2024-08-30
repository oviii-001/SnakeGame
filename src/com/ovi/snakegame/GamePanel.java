package com.ovi.snakegame;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    
	private static final long serialVersionUID = 1L;
	static final int WIDTH_OF_SCREEN = 600;
    static final int HEIGHT_OF_SCREEN = 600;
    static final int SIZE_OF_UNIT = 20;
    static final int GAME_UNIT = (WIDTH_OF_SCREEN * HEIGHT_OF_SCREEN) / SIZE_OF_UNIT;
    static final int DELAY = 75;

    final int x[] = new int[GAME_UNIT];
    final int y[] = new int[GAME_UNIT];

    int bodyPart = 4;
    int foodAte;
    int foodX;
    int foodY;
    char direction = 'R';
    boolean running = false;

    Timer timer;
    Random rand;

    GamePanel(){
        rand = new Random();
        this.setPreferredSize(new Dimension(WIDTH_OF_SCREEN, HEIGHT_OF_SCREEN));
        this.setBackground(new Color(0x16, 0x32, 0x5B));
        this.setFocusable(true);
        this.addKeyListener(new GameKeyAdapter());
        startGame();
    }

    public void startGame(){
        newFood();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }


    public void draw(Graphics g){
        if(running){
            for(int i=0; i<HEIGHT_OF_SCREEN/SIZE_OF_UNIT; i++){
                g.drawLine(i * SIZE_OF_UNIT, 0, i * SIZE_OF_UNIT, HEIGHT_OF_SCREEN);
                g.drawLine(0, i * SIZE_OF_UNIT, WIDTH_OF_SCREEN, i * SIZE_OF_UNIT);
            }
            g.setColor(new Color(0xFF, 0xDC, 0x7F));
            g.fillOval(foodX, foodY, SIZE_OF_UNIT, SIZE_OF_UNIT);

            for(int i=0; i<bodyPart; i++){
                if(i == 0){
                    g.setColor(new Color(0x78, 0xB7, 0xD0));
                    g.fillRect(x[i], y[i], SIZE_OF_UNIT, SIZE_OF_UNIT);
                }
                else{
                    g.setColor(new Color(0x22, 0x7B, 0x94));
                    g.fillRect(x[i], y[i], SIZE_OF_UNIT, SIZE_OF_UNIT);
                }
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Roboto Mono", Font.BOLD, 30));
            FontMetrics metrics  = getFontMetrics(g.getFont());
            g.drawString("SCORE: " + foodAte, (WIDTH_OF_SCREEN-metrics.stringWidth("SCORE: " + foodAte))/2, g.getFont().getSize());
        }
        else{
            gameOver(g);
        }
    }

    public void move() {
        for (int i = bodyPart; i>0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        Map<Character, Runnable> moves = new HashMap<>();
        moves.put('U', () -> y[0] -= SIZE_OF_UNIT);
        moves.put('D', () -> y[0] += SIZE_OF_UNIT);
        moves.put('L', () -> x[0] -= SIZE_OF_UNIT);
        moves.put('R', () -> x[0] += SIZE_OF_UNIT);
    
        moves.get(direction).run();
    }

    public void newFood(){
        foodX = rand.nextInt((int)(WIDTH_OF_SCREEN / SIZE_OF_UNIT)) * SIZE_OF_UNIT;
        foodY = rand.nextInt((int)(HEIGHT_OF_SCREEN / SIZE_OF_UNIT)) * SIZE_OF_UNIT;
    }

    public void checkFood(){
        if((x[0] == foodX) && (y[0] == foodY)){
            bodyPart++;
            foodAte++;
            newFood();
        }
    }

    public void checkBorderTouch(){

        //friction with body
        for(int i =  bodyPart; i>0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }

        //head -> border
        if(x[0] < 0){
            running = false;
        }

        //head -> right border
        if(x[0] > WIDTH_OF_SCREEN){
            running = false;
        }

        //head -> top border
        if(y[0] < 0){
            running = false;
        }

        //head -> bottom border
        if(y[0] > HEIGHT_OF_SCREEN){
            running = false;
        }

        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(new Font("Roboto Mono", Font.BOLD, 75));
        FontMetrics metrics  = getFontMetrics(g.getFont());
        g.drawString("Game Over!", (WIDTH_OF_SCREEN - metrics.stringWidth("Game Over!")) / 2, HEIGHT_OF_SCREEN / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(running){
            move();
            checkFood();
            checkBorderTouch();
        }
        repaint();
    }

    public class GameKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                case KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                default:
                    break;
            }
        }
    }
}
