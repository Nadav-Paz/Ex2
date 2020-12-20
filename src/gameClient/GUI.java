package gameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI  extends JFrame implements ActionListener {
    private  JFrame F;
    private static ImageIcon Background =new ImageIcon("Imj/GUIbackground1.jpg");
    private static int level;
    private static long ID;
    private static boolean flag=true;
    private static JLabel LevelInput,IDInput;
    private static JTextField IDTextFiled , LevelTextFiled;

    public GUI()
    {
        super();
        F=new JFrame();
        flag=false;
    }
    public int getLevel()
    {
        return level;
    }
    public long getID()
    {
        return  ID;
    }
    public boolean IsFinshed()
    {
        return flag;
    }
    public void begin()
    {
        F.setLayout(new FlowLayout());
        SetButton();
        SetBackgrond();
        SetLebels();
        F.setSize(500,375);
        F.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        F.setTitle("Pokemon game of Ron and Nadav");
        Delay(1000);
        F.setLocationRelativeTo(null);
        F.setVisible(true);
    }
    public void SetBackgrond()
    {
        JLabel Picture=new JLabel(Background);
        Picture.setBounds(1,1,500,375);
        Picture.setIconTextGap(50);
        Picture.setVerticalTextPosition(JLabel.BOTTOM);
        Picture.setFocusable(false);
        F.add(Picture);
    }
    public void SetLebels()
    {
        F.setLayout(null);
//        LevelInput=new JLabel();
//        LevelInput.setForeground(Color.blue);
//        LevelInput.setText("Level");
//        LevelInput.setBounds(50,50,50,25);
//        LevelInput.setVerticalTextPosition(JLabel.TOP);
//        F.add(LevelInput);
        LevelTextFiled=new JTextField(2);
        LevelTextFiled.setBounds(100,50,50,25);
        F.add(LevelTextFiled);

        IDInput=new JLabel();
//        IDInput.setForeground(Color.blue);
//        IDInput.setText("ID");
//        IDInput.setBounds(50,120,100,25);
//        F.add(IDInput);
        IDTextFiled=new JTextField(9);
        IDTextFiled.setBounds(75,105,100,25);
        F.add(IDTextFiled);
    }
    public void SetButton()
    {
        JButton StartButton=new JButton();
        //StartButton.getInputContext().
        StartButton.setBounds(50,175,75,50);
        StartButton.setBackground(Color.CYAN);
        StartButton.setText("Start");
        StartButton.addActionListener(new GUI());
        F.add(StartButton);
    }
    public static void main(String[] args)
    {
        GUI g=new GUI();
        g.begin();
    }
    public void Delay(int x)
    {
        long t0=System.currentTimeMillis();
        long t1=System.currentTimeMillis();
        while(t1-t0<x)t1=System.currentTimeMillis();
    }
    public boolean has9Digitis(long id)
    {
        int i=0;
        while(id>0)
        {
            id=id/10;
            i++;
        }
        if(i==9)return true;
        else return false;
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.flag=true;
        String levelString=LevelTextFiled.getText();
        String IDString=IDTextFiled.getText();
        try
        {
            level=Integer.parseInt(levelString);
            ID=Long.parseLong(IDString);
            if(level<0 || level>23)
            {
                System.err.println("Wrong level input the level will be set as zero");
                level=0;
            }
            long id=ID;
            if(!has9Digitis(id))
            {

                System.err.println("Wrong ID input the level will be set as default (default id :205542897)");
                ID=205542897;
            }
            System.err.println("["+level+","+ID+"]");
        } catch (NumberFormatException numberFormatException)
        {
            numberFormatException.printStackTrace();
            System.err.println("Wrong input");
        }
        F.dispose();
        F.setVisible(false);
        //System.err.println(level+" , "+ID );

    }
}
