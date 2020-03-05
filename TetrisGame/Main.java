//Autor : Piotr Piechowicz
package com.TetrisGame;

import javax.swing.*;
import java.awt.event.*;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        final GameProject gierka=new GameProject();
        JFrame f=new JFrame("TETRIS");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12*(gierka.width+1)+14,(gierka.height+1)*23+39);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);

        gierka.init();
        f.add(gierka);

        Thread t=new Thread(new Runnable()  {
            public void run(){
                int k=1;
                int czas=1000;
                while(true)
                {
                    try {
                        sleep(czas);
                        if(gierka.getScore()-k*1000>0)//przyspieszanie gry po danego uzyskaniu wyniku
                        {
                            k++;
                            czas=czas-czas/6;
                        }
                    }
                    catch (InterruptedException e)//obsługa pauzy
                    {

                        while (gierka.lock>0)
                        {
                            //System.out.println(gierka.lock);
                        }
                    }

                    try {
                        gierka.drop(Thread.currentThread());
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        gierka.rotate(-1);//obracanie klocka
                        break;
                    case KeyEvent.VK_DOWN:
                        try{
                            gierka.drop(t);}//opuszczanie klocka
                        catch(InterruptedException x) {
                            x.printStackTrace();
                        }
                        gierka.score+=1;
                        break;
                    case KeyEvent.VK_LEFT:
                        gierka.move(-1);//przesuwanie klocka
                        break;
                    case KeyEvent.VK_RIGHT:
                        gierka.move(1);
                        break;
                    case KeyEvent.VK_SPACE:
                        try{
                            gierka.down(t);}//spuszczanie klocka na sam dół
                        catch(InterruptedException x)
                        {
                            x.printStackTrace();
                        }
                        gierka.score+=15;
                        break;
                    case KeyEvent.VK_ESCAPE://pauza
                        gierka.pause(t,"Pauza");
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
}
