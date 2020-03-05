//Autor : Piotr Piechowicz
package com.TetrisGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;

public class GameProject extends JPanel {

    private final Point[][][] bricks={//klocuchy
            {
                    //I
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(3,1)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3)},
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(3,1)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3)}
            },
            {
                    //L
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(2,0)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(0,0)},
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(0,2)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(2,2)}
            },
            {
                    //J
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(2,2)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(2,0)},
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(0,0)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(0,2)}
            },
            {
                    //O
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)},
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)},
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)},
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)}
            },
            {
                    //Z
                    {new Point(0,0),new Point(1,1),new Point(1,0),new Point(2,1)},
                    {new Point(1,2),new Point(1,1),new Point(2,1),new Point(2,0)},
                    {new Point(0,0),new Point(1,1),new Point(1,0),new Point(2,1)},
                    {new Point(1,2),new Point(1,1),new Point(2,1),new Point(2,0)}
            },
            {
                    //Z odwrocone
                    {new Point(2,1),new Point(1,1),new Point(1,2),new Point(0,2)},
                    {new Point(1,2),new Point(1,1),new Point(0,1),new Point(0,0)},
                    {new Point(2,1),new Point(1,1),new Point(1,2),new Point(0,2)},
                    {new Point(1,2),new Point(1,1),new Point(0,1),new Point(0,0)}
            },
            {
                    //T
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(1,0)},
                    {new Point(0,1),new Point(1,1),new Point(1,2),new Point(1,0)},
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(1,2)},
                    {new Point(2,1),new Point(1,1),new Point(1,2),new Point(1,0)}
            },
    };
    private final Color[] myColor={Color.CYAN,Color.MAGENTA,Color.ORANGE,Color.YELLOW,
            Color.RED,Color.GREEN,Color.BLUE};//kolory klocuchów
    private Point pt;//aktualny punkt w ktorym jest klocuch
    private int currentPiece;//aktualny rodzaj klocuhca
    private int rotation;//w jakiej rotacji aktualnie jest klocuch
    private ArrayList<Integer> nextPiece = new ArrayList<>();//lista nastepnych klockow
    long score;
    int width=30;
    int height=30;
    private Color[][] well;//tło
    public volatile int lock=0;//zamek (dzieki niemu dziala pauza) do komunikacji miedzy watkami dlatefo volatile
    public static Object locky=new Object();

    void init()//utworzenie tła i pierwszego klocka
    {
        well=new Color[12][24];
        for(int i=0;i<12;i++)
        {
            for(int j=0;j<23;j++)
            {
                if(i==0||i==11||j==22)
                    well[i][j]=Color.pink;
                else
                    well[i][j]=Color.black;
            }
        }
    newPiece();
    }

    public void newPiece()//tworzenie nowego klocka
    {
        pt=new Point(5,-1);
        rotation=0;
        if(nextPiece.isEmpty())
        {
            Collections.addAll(nextPiece,0,1,2,3,4,5,6);
            Collections.shuffle(nextPiece);//losowanie kolejnosci klockow
        }
        currentPiece=nextPiece.get(0);
        nextPiece.remove(0);
    }

    private boolean collidesAt(int x,int y,int rotation)//wykrycie kolizji
    {
        for(Point p:bricks[currentPiece][rotation])
        {
            if(well[p.x+x][p.y+y+1]!=Color.black)
                return true;
        }
        return false;
    }
    private boolean collidesAtX(int x,int y,int rotation)//wykrycie kolizji
    {

        for(Point p:bricks[currentPiece][rotation])
        {
            if(p.y+y<0)
                return false;
            else if((well[p.x+x][p.y+y])!=(Color.black)) {
                return true;
            }
        }
        return false;
    }

    void rotate(int i)//obracanie klockiem
    {
        int newRotation=(rotation+i)%4;
        if(newRotation<0)
            newRotation=3;
        if(!collidesAt(pt.x,pt.y,newRotation))
        {
            rotation=newRotation;
        }
        repaint();
    }

    public void move(int i)//poruszanie klockiem
    {
        if(!collidesAtX(pt.x+i,pt.y,rotation))
            pt.x+=i;
        repaint();
    }
    public void down(Thread w) throws InterruptedException//spuszczenie klocka na sam dół
    {
        int k=0;
        while(true){
        if(!collidesAt(pt.x,pt.y+k,rotation))
        {
            k++;
        }
        else
            break;
        }
        pt.y+=k;
        fixToWell(w);
        repaint();
    }
    public void drop(Thread k)throws InterruptedException//opuszczenie klocka o 1
    {
        if(!collidesAt(pt.x,pt.y,rotation))
            pt.y+=1;
        else {
            //lock=1;
            //k.interrupt();
            sleep(100);
            //lock=0;
            fixToWell(k);
        }
        repaint();
    }

    public void fixToWell(Thread t) throws InterruptedException {//doklejenie klocka do juz ułożonych
        for(Point p:bricks[currentPiece][rotation]) {
            if (pt.y + p.y < 0) {
                //t.interrupt();
                pause(t,"Przegranko!");
                break;

            }
            else
                well[pt.x + p.x][pt.y + p.y] = myColor[currentPiece];
        }
        clearRows();
        newPiece();
    }

    public void deleteRow(int row)//usunięcie rzędu
    {
        for(int j=row-1;j>0;j--)
        {
            for(int i=1;i<11;i++)
                well[i][j+1]=well[i][j];
        }
    }
    public long getScore()
    {
        return score;
    }
    public void clearRows()//usuwanie rzedów jesli ułożone i dodawanie wyniku
    {
        boolean gap;
        int numClear=0;
        for(int j=21;j>0;j--)
        {
            gap=false;
            for(int i=1;i<11;i++) {
                if (well[i][j] == Color.black) {
                    gap = true;
                    break;
                }
            }
            if(!gap)
            {
                deleteRow(j);
                j+=1;
                numClear+=1;
            }
        }
        switch (numClear){
            case 1:
                score+=100;
                break;
            case 2:
                score+=300;
                break;
            case 3:
                score+=500;
                break;
            case 4:
                score+=800;
                break;
            default:
                break;
        }
    }

    private void drawPiece(Graphics g)//rysowanie klocucha
    {
        g.setColor(myColor[currentPiece]);
        for(Point p:bricks[currentPiece][rotation])
            g.fillRect((p.x+pt.x)*(width+1),(p.y+pt.y)*(height+1),width,height);

        int k=0;//rysowanie gdzie spadnie
        while(true){
            if(!collidesAt(pt.x,pt.y+k,rotation))
            {
                k++;
            }
            else
                break;
        }
        pt.y+=k;
        for(Point p:bricks[currentPiece][rotation])
            g.drawRect((p.x+pt.x)*(width+1),(p.y+pt.y)*(height+1),width,height);
        pt.y-=k;
    }

    public void paintComponent(Graphics g)//rysowanie
    {
        g.fillRect(0,0,(width+1)*12,(height+1)*23);
        for(int i=0;i<12;i++)
        {
            for(int j=0;j<23;j++)
            {
                g.setColor(well[i][j]);
                g.fillRect((width+1)*i,(height+1)*j,width,height);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("Wynik :"+score,height*9,25);
        drawPiece(g);
    }
    public void pause(Thread k,String message)//obsługa pauzy ( pod klawiszem esc) i przegranej
    {
        Thread pause=new Thread(new Runnable()  {
            public void run(){

                lock++;
                k.interrupt();
                JFrame pauz=new JFrame("Pauza");
                pauz.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        lock--;
                        pauz.dispose();
                    }
                });
                pauz.setSize(12*(width+1)+14,(height+1)*23+39);
                JLabel label = new JLabel(message,SwingConstants.CENTER);
                label.setFont(new Font("Verdana", Font.BOLD, 40));
                JButton resumeButton  = new JButton("Kontynuuj");
                JButton closeButton  = new JButton("Zamknij grę");
                pauz.add(label);
                pauz.add(resumeButton);
                pauz.add(closeButton);
                pauz.setLayout(new GridLayout(3,1));
                pauz.setLocationRelativeTo(null);

                closeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lock--;
                        exit(0);
                    }
                });
                resumeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lock--;
                        pauz.dispose();
                    }
                });
                pauz.setVisible(true);
                pauz.setResizable(false);
            }

        });
        pause.start();
    }

}

