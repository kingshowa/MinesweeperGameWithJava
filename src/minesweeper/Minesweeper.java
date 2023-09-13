package minesweeper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper implements ActionListener
{
	int xZero;
	int yZero;
	int[][] solution;
	boolean[][] flagged;
	
	Random random;
	
	JFrame frame;
	JPanel textPanel;
	JPanel buttonPanel;
	JButton[][] buttons;	
	JLabel textfield;
	JButton resetButton;
	JButton flag;
	
	@SuppressWarnings("rawtypes")
	JComboBox choice; 
	//Drop Down to choose beginner, intermediate or hard
	String[] options= {"9x9","16x16","22x22", "Modify"}; //Drop Down List options
	
	int size;
	int bombs;
	
	ImageIcon icon;
	File iconImage;
	
	ArrayList<Integer> xPositions;
	ArrayList<Integer> yPositions;
	
	boolean flagging;
	int count=0;
	int lastXchecked;
	int lastYchecked;
	
	int position;
	int customBombs, customSize=0;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Minesweeper(int level, int mines)
	{		
		position=level;
		
		size=level;
		bombs=mines;
		
		lastXchecked=size+1;
		lastYchecked=size+1;
		
		flagged=new boolean[size][size]; //To know which places are flagged
		random=new Random();
		xPositions=new ArrayList<Integer>();
		yPositions=new ArrayList<Integer>();
		
		for(int i=0;i<bombs;i++)
		{
			int randomX=random.nextInt(size);
			int randomY=random.nextInt(size);

			xPositions.add(randomX);
			yPositions.add(randomY);
		}
		
		 //To prevent bomb overlapping- Very IMP*
		//Otherwise sometimes one place would hold 2 bombs & give wrong count
		for(int i=0; i<bombs; i++)
		{
			for(int j=i+1; j<bombs; j++)
			{
				if(xPositions.get(i)==xPositions.get(j) && yPositions.get(i)==yPositions.get(j))
				{
					xPositions.set(j, random.nextInt(size));
					yPositions.set(j, random.nextInt(size));
					i=0;
					j=0;
				}
			}
		}
		
		frame=new JFrame();
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		
		
		textPanel=new JPanel();
		textPanel.setVisible(true);
		textPanel.setBackground(Color.BLACK);
		
		buttonPanel=new JPanel();
		buttonPanel.setVisible(true);
		buttonPanel.setLayout(new GridLayout(size,size));
		
		textfield=new JLabel();
		textfield.setHorizontalAlignment(JLabel.CENTER);
		textfield.setFont(new Font("MV Boli",Font.BOLD,20));
		textfield.setForeground(Color.BLUE);
		textfield.setText(bombs+" Bombs");		
		
		resetButton=new JButton();
		resetButton.setForeground(Color.BLUE);
		resetButton.setText("Reset");
		resetButton.setFont(new Font("MV Boli", Font.BOLD, 20));
		resetButton.setBackground(Color.WHITE);
		resetButton.setFocusable(false);
		resetButton.addActionListener(this);
		
		flag=new JButton();
		flag.setForeground(Color.ORANGE);
		flag.setText("1>");
		flag.setFont(new Font("MV Boli", Font.BOLD, 20));
		flag.setBackground(Color.WHITE);
		flag.setFocusable(false);
		flag.addActionListener(this);
		
		buttons=new JButton[size][size];
		solution=new int[size][size];
		for(int i=0; i<buttons.length; i++)
		{
			for(int j=0; j<buttons[0].length; j++)
			{
				buttons[i][j]=new JButton();
				buttons[i][j].setFocusable(false);
				buttons[i][j].setFont(new Font("MV Boli",Font.BOLD,12));
				buttons[i][j].addActionListener(this);
				buttons[i][j].setText("");
				buttonPanel.add(buttons[i][j]);
			}
		}
		
		choice=new JComboBox(options);
		choice.addActionListener(this);
		
		String selectedItem="";
		if(level==9 && bombs==10||level==16 && bombs==40||level==22 && bombs==100)
			selectedItem=String.valueOf(level+"x"+level);
		else 
			selectedItem="Modify";
		choice.setSelectedItem(selectedItem);
		choice.setFont(new Font("MV Boli",Font.BOLD,10)); 
		choice.setBackground(new Color(165, 183, 201));
		choice.setForeground(new Color(255,255,255));
		choice.setBorder(null);
		choice.setEditable(false);
		
		textPanel.add(textfield);
		frame.add(buttonPanel);
		frame.add(textPanel, BorderLayout.NORTH);
		
		if(position==9 && bombs==10)
			frame.setSize(570,570);
		else if(position==16 && bombs==40)
			frame.setSize(900,650);
		else if(position==22 && bombs==100)
			frame.setSize(1250,700);
		else 
		{
			Dimension size=Toolkit.getDefaultToolkit().getScreenSize(); //Getting the size of screen
			int height=(int) size.getHeight();
			int width=(int) size.getWidth();
			
			if(position<=9)
				frame.setSize(570,570);
			else if(position>9 && position<=16)
				frame.setSize(900,650);
			else if(position>16 && position<=22)
				frame.setSize(1250,700);
			else
			{
				frame.setSize(width, height);
				JOptionPane.showMessageDialog(null, "If You Can't See The Number, Your Screen is Small. Try a Smaller Grid", "Customize", JOptionPane.INFORMATION_MESSAGE);		
			}
		}
		
		frame.add(resetButton, BorderLayout.SOUTH);
		frame.add(flag, BorderLayout.WEST);
		frame.add(choice, BorderLayout.EAST);
		frame.revalidate();
		frame.setLocationRelativeTo(null);
		
		getSolution();
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource()==choice)
		{
			if(position==9 && bombs==10)
			{
				if(choice.getSelectedItem()=="16x16")
				{
					frame.dispose();
					new Minesweeper(16, 40);
				}
				else if(choice.getSelectedItem()=="22x22")
				{
					frame.dispose();
					new Minesweeper(22, 100);
				}
				else if(choice.getSelectedItem()=="Modify")
				{
					int success=getCustom();
					if(success==1)
					{
						frame.dispose();
						new Minesweeper(customSize, customBombs);
					}
				}
			}
			else if(position==16 && bombs==40)
			{
				if(choice.getSelectedItem()=="9x9")
				{
					frame.dispose();
					new Minesweeper(9, 10);
				}
				else if(choice.getSelectedItem()=="22x22")
				{
					frame.dispose();
					new Minesweeper(22, 100);
				}
				else if(choice.getSelectedItem()=="Modify")
				{
					int success=getCustom();
					if(success==1)
					{
						frame.dispose();
						new Minesweeper(customSize, customBombs);
					}
				}
			}
			else if(position==22 && bombs==100)
			{
				if(choice.getSelectedItem()=="9x9")
				{
					frame.dispose();
					new Minesweeper(9, 10);
				}
				else if(choice.getSelectedItem()=="16x16")
				{
					frame.dispose();
					new Minesweeper(16, 40);
				}
				else if(choice.getSelectedItem()=="Modify")
				{
					int success=getCustom();
					if(success==1)
					{
						frame.dispose();
						new Minesweeper(customSize, customBombs);
					}
				}
			}
			else 
			{
				if(choice.getSelectedItem()=="9x9")
				{
					frame.dispose();
					new Minesweeper(9, 10);
				}
				else if(choice.getSelectedItem()=="16x16")
				{
					frame.dispose();
					new Minesweeper(16, 40);
				}
				else if(choice.getSelectedItem()=="22x22")
				{
					frame.dispose();
					new Minesweeper(22, 100);
				}
			}
		}
		if(e.getSource()==flag)
		{
			if(flagging)
			{
				flag.setBackground(Color.WHITE);
				flagging=false;
			}
			else
			{
				flag.setBackground(Color.RED);
				flagging=true;
			}
		}
		if(e.getSource()==resetButton)
		{
			frame.dispose();
			new Minesweeper(size, bombs);
		}
		for(int i=0; i<buttons.length; i++)
		{
			for(int j=0; j<buttons[0].length; j++)
			{
				if(e.getSource()==buttons[i][j])
				{
					if(flagging)
					{
						if(flagged[i][j])
						{
							buttons[i][j].setText("");
							buttons[i][j].setBackground(null); //null is default bg color
							flagged[i][j]=false;
						}
						else
						{
							if(buttons[i][j].getText().equals(""))
							{
								buttons[i][j].setForeground(Color.ORANGE);
								buttons[i][j].setBackground(Color.RED);
								buttons[i][j].setText("1>");
								flagged[i][j]=true;
							}
						}						
					}
					else 
					{
						if(!flagged[i][j])
						{
							buttons[i][j].setBackground(null);
							check(i,j);	
						}
					}
				}				
			}
		}
	}
	
	public int getCustom()
	{
		boolean successful=false;
		
		String size= JOptionPane.showInputDialog(null,"Choose Grid Size (Eg: 8 for 8x8 Grid)","Customize", JOptionPane.PLAIN_MESSAGE);				
		try {
			customSize=Integer.parseInt(size);
			
			if(customSize<=30 && customSize>0)
				successful=true;
			else 
			{
				if(customSize>30)
					JOptionPane.showMessageDialog(null, "Grid Too Big!", "Customize", JOptionPane.WARNING_MESSAGE);
				else if(customSize<=0)
					JOptionPane.showMessageDialog(null, "Grid Can't Be Zero Or Negative!", "Customize", JOptionPane.WARNING_MESSAGE);
				successful=false;
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid Input", "Customize", JOptionPane.WARNING_MESSAGE);
			successful=false;
		}
		
		
		if(successful)
		{
			String bombs=JOptionPane.showInputDialog(null,"Number Of Bombs","Customize", JOptionPane.PLAIN_MESSAGE);	
			
			try {
				customBombs=Integer.parseInt(bombs);
				
				if(customBombs<=(Math.pow(customSize, 2)) && customBombs>=0)
					successful=true;
				else 
				{
					if(customBombs>(Math.pow(customSize, 2)))
						JOptionPane.showMessageDialog(null, "More Bombs Than Spaces!", "Customize", JOptionPane.WARNING_MESSAGE);
					else if(customBombs<0)
						JOptionPane.showMessageDialog(null, "Bombs Can't Be Negative!", "Customize", JOptionPane.WARNING_MESSAGE);
					successful=false;
				}
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Invalid Input", "Customize", JOptionPane.WARNING_MESSAGE);
				successful=false;
			}
		}
		
		if(successful)
			return 1;
		else 
			return 0;
	}

	public void getSolution()
	{
		for(int y=0; y<solution.length; y++)
		{
			for(int x=0; x<solution[0].length; x++)
			{
				boolean changed=false;
				int bombsAround=0;
				
				for(int i=0; i<xPositions.size(); i++)
				{
					if(x==xPositions.get(i)&&y==yPositions.get(i))
					{
						solution[y][x]=size+1; //Indicate mine
						changed=true;
					}
				}
				
				if(!changed)
				{
					for(int i=0; i<xPositions.size(); i++)
					{
						if(x-1==xPositions.get(i)&&y==yPositions.get(i))
							bombsAround++;
						if(x+1==xPositions.get(i)&&y==yPositions.get(i))
							bombsAround++;
						if(x==xPositions.get(i)&&y-1==yPositions.get(i))
							bombsAround++;
						if(x==xPositions.get(i)&&y+1==yPositions.get(i))
							bombsAround++;
						if(x-1==xPositions.get(i)&&y-1==yPositions.get(i))
							bombsAround++;
						if(x+1==xPositions.get(i)&&y+1==yPositions.get(i))
							bombsAround++;
						if(x+1==xPositions.get(i)&&y-1==yPositions.get(i))
							bombsAround++;
						if(x-1==xPositions.get(i)&&y+1==yPositions.get(i))
							bombsAround++;
					}
					solution[y][x]=bombsAround;
				}
			}
		}
	}
	
	public void check(int y, int x)
	{		
		boolean over=false;
		if(solution[y][x]==(size+1))
		{
			gameOver(false);
			over=true;
		}
		
		if(!over)
		{
			getColor(y,x);
			
			if(solution[y][x]==0)
			{
				xZero=x;
				yZero=y;
				
				count=0;
				
				display();
			}
			checkWinner();
		}
	}
	
	public void gameOver(boolean won)
	{
		if(!won)
		{
			textfield.setForeground(Color.RED);
			textfield.setText("Game Over!");
		}
		else 
		{
			textfield.setForeground(Color.GREEN);
			textfield.setText("You Win!");
		}
		for(int i=0; i<buttons.length; i++)
		{
			for(int j=0; j<buttons[0].length; j++)
			{
				buttons[i][j].setBackground(null);
				buttons[i][j].setEnabled(false);
				for(int count=0; count<xPositions.size(); count++)
				{
					if(j==xPositions.get(count) && i==yPositions.get(count))
					{
						buttons[i][j].setBackground(Color.BLACK);
						buttons[i][j].setText("*");
					}
				}
			}
		}
		flag.setEnabled(false);
	}
	
	public void checkWinner()
	{
		int buttonsleft=0;
		
		for(int i=0; i<buttons.length; i++)
		{
			for(int j=0; j<buttons[0].length; j++)
			{
				if(buttons[i][j].getText()=="" || buttons[i][j].getText()=="1>")
					buttonsleft++;
			}
		}
		if(buttonsleft==bombs)
			gameOver(true);
	}
	public void display()
	{
		if(count<1)
		{
			if((xZero-1)>=0)
				getColor(yZero,xZero-1);
			if((xZero+1)<size)
				getColor(yZero,xZero+1);
			if((yZero-1)>=0)
				getColor(yZero-1,xZero);
			if((yZero+1)<size)
				getColor(yZero+1,xZero);
			if((yZero+1)<size && (xZero+1)<size)
				getColor(yZero+1,xZero+1);
			if((yZero-1)>=0 && (xZero-1)>=0)
				getColor(yZero-1,xZero-1);
			if((yZero+1)<size && (xZero-1)>=0)
				getColor(yZero+1,xZero-1);
			if((yZero-1)>=0 && (xZero+1)<size)
				getColor(yZero-1, xZero+1);
			
			count++;		
			display();	
		}
		else
		{
			for(int y=0; y<buttons.length; y++)
			{
				for(int x=0; x<buttons[0].length; x++)
				{
					if(buttons[y][x].getText().equals("0"))
					{
						// IMP* Explanation of how it works:
						//If a button is 0 but is empty around, means it needs to reveal everything around so display is called again
						//The if reveals everything around, the else checks which button needs to reveal everything around it if its a zero.
						if(y-1>=0)
						{
							if(buttons[y-1][x].getText().equals("") || buttons[y-1][x].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
						if(y+1<size)
						{
							if(buttons[y+1][x].getText().equals("") || buttons[y+1][x].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
						if(x-1>=0)
						{
							if(buttons[y][x-1].getText().equals("") || buttons[y][x-1].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
						if(x+1<size)
						{
							if(buttons[y][x+1].getText().equals("") || buttons[y][x+1].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
						if(x-1>=0 && y-1>=0)
						{
							if(buttons[y-1][x-1].getText().equals("") || buttons[y-1][x-1].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
						if(x+1<size && y+1<size)
						{
							if(buttons[y+1][x+1].getText().equals("") || buttons[y+1][x+1].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
						if(x+1<size && y-1>=0)
						{
							if(buttons[y-1][x+1].getText().equals("") || buttons[y-1][x+1].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
						if(x-1>=0 && y+1<size)
						{
							if(buttons[y+1][x-1].getText().equals("") || buttons[y+1][x-1].getText().equals("1>"))
							{
								lastXchecked=x;
								lastYchecked=y;
							}
						}
					}
				}
			}
			
			//The reason why the program entered this when there was a single zero the first time was since I made lastXcheked=10, instead of size+1			
			if(lastXchecked<size+1 && lastYchecked<size+1)
			{				
				xZero=lastXchecked;
				yZero=lastYchecked;
				
				count=0;
				
				lastXchecked=size+1;
				lastYchecked=size+1;
				
				display();			
			}
		}
	}
	
	public void getColor(int y, int x)
	{		
		if(solution[y][x]==0)
			buttons[y][x].setEnabled(false);
		if(solution[y][x]==1)
			buttons[y][x].setForeground(Color.BLUE);
		if(solution[y][x]==2)
			buttons[y][x].setForeground(Color.GREEN);
		if(solution[y][x]==3)
			buttons[y][x].setForeground(Color.RED);
		if(solution[y][x]==4)
			buttons[y][x].setForeground(Color.MAGENTA);
		if(solution[y][x]==5)
			buttons[y][x].setForeground(new Color(128,0,128));
		if(solution[y][x]==6)
			buttons[y][x].setForeground(Color.CYAN);
		if(solution[y][x]==7)
			buttons[y][x].setForeground(new Color(42, 13, 93));
		if(solution[y][x]==8)
			buttons[y][x].setForeground(Color.lightGray);
		
		buttons[y][x].setBackground(null);
		buttons[y][x].setText(String.valueOf(solution[y][x]));
	}
}

