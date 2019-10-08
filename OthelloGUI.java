import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Game{
	enum Result{NONE, BLACK_WIN, WHITE_WIN, DRAW};

	int row, size;
	int[] board;
	boolean[] moveWhite;
	boolean[] moveBlack;

	Game(int row){
		this.size = row * row;
		this.row = row;
		this.board = new int[size];

		for(int r = 0;r < row;r++){
            for(int c = 0;c < row;c++){
                board[r*row + c] = Const.EMPTY;
            }
        }
        board[3*row + 3] = Const.WHITE;
        board[4*row + 4] = Const.WHITE;
        board[3*row + 4] = Const.BLACK;
        board[4*row + 3] = Const.BLACK;

		moveWhite = new boolean[size];
		moveBlack = new boolean[size];

	}

	boolean onBoard(int r, int c){
        if(r < 0 || r >= row || c < 0 || c >= row){
            return false;
        }
        return true;
	}

	boolean legalMove(int turn, int pos_r, int pos_c){
        if(board[pos_r*row + pos_c] != Const.EMPTY){
            return false;
        }
        int r, c;
        for(int i = 0;i < 8;i++){
            r = pos_r + Const.DIR[i][0];
            c = pos_c + Const.DIR[i][1];
            if(onBoard(r, c) == false || board[r*row + c] != -turn){
                continue;
            }
            while(true){
                r += Const.DIR[i][0];
                c += Const.DIR[i][1];
                if(onBoard(r, c) == false){
                    break;
                }
                if(board[r*row + c] == Const.EMPTY){
                    break;
                }
                else if(board[r*row + c] == turn){
                    return true;
                }
            }
        }
        return false;
	}

	void getLegalMove(int turn){
		boolean[] table = moveWhite;
        if(turn == Const.BLACK){
            table = moveBlack;
        }
        for(int i = 0;i < size;i++){
            table[i] = legalMove(turn, i / row, i % row);
        }
	}

	boolean hasMove(int turn){
		boolean[] table = moveWhite;
        if(turn == Const.BLACK){
            table = moveBlack;
        }
        for(int i = 0;i < size;i++){
            if(table[i]){
                return true;
            }
        }
        return false;
	}

	boolean doMove(int turn, int pos_r, int pos_c){
		if(legalMove(turn, pos_r, pos_c) == false){
			return false;
		}
		board[pos_r*row + pos_c] = turn;

		int i, r, c;
		for(i = 0;i < 8;i++){
			r = pos_r + Const.DIR[i][0];
			c = pos_c + Const.DIR[i][1];
			while(onBoard(r, c) && board[r*row + c] != Const.EMPTY){
				if(board[r*row + c] == turn){
					while(r != pos_r || c != pos_c){
						r -= Const.DIR[i][0];
						c -= Const.DIR[i][1];
						board[r*row + c] = turn;
					}
					break;
				}
				r += Const.DIR[i][0];
				c += Const.DIR[i][1];
			}
		}
		return true;
	}

	Result getResult(){
		getLegalMove(Const.WHITE);
		getLegalMove(Const.BLACK);
        if(hasMove(Const.WHITE) || hasMove(Const.BLACK)){
            return Result.NONE;
        }
        int disk = 0;
        for(int i = 0;i < size;i++){
            disk += board[i];
        }
        if(disk > 0){
            return Result.BLACK_WIN;
        }
        else if(disk < 0){
            return Result.WHITE_WIN;
        }
        else{
            return Result.DRAW;
        }
	}

	int getDisk(int pos){
		return board[pos];
	}

	int getDiskCount(int turn){
		int count = 0;
		for(int i = 0;i < size;i++){
			if(board[i] == turn){
				count++;
			}
		}
		return count;
	}
}

class CFrame extends JFrame implements ActionListener{

	JPanel paneGame, paneInfo;
	JButton[] btnDisk;
	JTextField txtDiskNum;
	JToolBar barAction;
	JButton btnNew, btnLoad, btnSave, btnPass, btnScriptPlay;
	JLabel lblNowPlay, lblWhiteWin, lblBlackWin, lblRound, lblWhiteTime, lblBlackTime;
	JLabel[] lblRow;
	JLabel[] lblCol;
	JList<String> listMove;
	ImageIcon diskBlack, diskWhite, empty, available;
	Timer timer;
	int pixelDisk = 45;

	Game game;
	int row = 8;
	int turn;
	int[] moveSeq;
	protected String[] strMoveSeq;
	int step;
	long whiteTime, blackTime, startTime;
	int whiteWin, blackWin, round;
	protected Const.Player[] p;
	protected String[] cmd;
	protected int loop = 1;

    // Get the name of Operating System
    private static String OS = System.getProperty("os.name").toLowerCase();

	CFrame(){
		super("Othello Interface");
		setSize(650, 520);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initUI();
		initGame();
        isLinux();
        initTime();
		saveGame("board.txt");
		initPlayer();
		updateBoard();
	}

    protected void isLinux(){
        System.out.println(OS);

        if(OS.indexOf("linux") >= 0){

        }
    }

	protected void initCount(){
		whiteWin = 0;
		blackWin = 0;
		round = 0;
	}

	protected void initGame(){
		turn = Const.BLACK;
		game = new Game(row);

		step = 0;
		moveSeq = new int[row * row * 2];
		strMoveSeq = new String[1];
		strMoveSeq[0] = "---";
	}

    protected void initTime(){
        whiteTime = 0;
		blackTime = 0;
		startTime = System.currentTimeMillis();
    }

	/* Player and computer player setings */
	protected void initPlayer(){
		p = new Const.Player[3];
		p[0] = Const.Player.HUMAN;
		p[2] = Const.Player.HUMAN;
		cmd = new String[3];
		cmd[0] = "Othello.exe";
		cmd[2] = "Othello.exe";
	}

	/* GUI elements ininitalize */
	private void initUI(){
		setLayout(new BorderLayout());
		// toolbar
		barAction = new JToolBar("Action", JToolBar.HORIZONTAL);
		btnNew = new JButton("New Game");
		btnNew.addActionListener(this);
		btnLoad = new JButton("Load Game");
		btnLoad.addActionListener(this);
		btnSave = new JButton("Save Game");
		btnSave.addActionListener(this);
		btnPass = new JButton("Human Pass");
		btnPass.addActionListener(this);
        btnScriptPlay = new JButton("New Script Game");
        btnScriptPlay.addActionListener(this);
		barAction.add(btnNew);
		barAction.add(btnLoad);
		barAction.add(btnSave);
		barAction.add(btnPass);
        barAction.add(btnScriptPlay);
		add(barAction, BorderLayout.NORTH);

		// board panel
		paneGame = new JPanel();
		paneGame.setPreferredSize(new Dimension(400, 400));
		paneGame.setLayout(new FlowLayout());
		add(paneGame, BorderLayout.CENTER);

		// information panel
		paneInfo = new JPanel();
		paneInfo.setPreferredSize(new Dimension(200, 400));
		paneInfo.setLayout(new FlowLayout());
		add(paneInfo, BorderLayout.EAST);
		// game board
		lblCol = new JLabel[row + 1];
		lblCol[0] = new JLabel(" ");
		lblCol[0].setPreferredSize(new Dimension(pixelDisk/2, pixelDisk/2));
		paneGame.add(lblCol[0]);
		for(int r = 1;r < 9;r++){
			lblCol[r] = new JLabel("   " + (char)(r+64));
			lblCol[r].setPreferredSize(new Dimension(pixelDisk, pixelDisk/2));
			paneGame.add(lblCol[r]);
		}
		lblRow = new JLabel[row];
		btnDisk = new JButton[row * row];
		for(int r = 0;r < 8;r++){
			lblRow[r] = new JLabel("" + (r+1));
			lblRow[r].setPreferredSize(new Dimension(pixelDisk/2, pixelDisk/2));
			paneGame.add(lblRow[r]);
			for(int c= 0 ;c < row;c++){
				btnDisk[r * row + c] = new JButton("");
				btnDisk[r * row + c].setPreferredSize(new Dimension(pixelDisk, pixelDisk));
				btnDisk[r * row + c].addActionListener(this);
				paneGame.add(btnDisk[r * row + c]);
			}
		}
		// game information
		lblNowPlay = new JLabel("Playing : ");
		lblNowPlay.setPreferredSize(new Dimension(160, 20));
		paneInfo.add(lblNowPlay);
		lblWhiteWin = new JLabel("White Win : ");
		lblWhiteWin.setPreferredSize(new Dimension(160, 20));
		paneInfo.add(lblWhiteWin);
		lblBlackWin = new JLabel("Black Win : ");
		lblBlackWin.setPreferredSize(new Dimension(160, 20));
		paneInfo.add(lblBlackWin);
		lblRound = new JLabel("Round : ");
		lblRound.setPreferredSize(new Dimension(160, 20));
		paneInfo.add(lblRound);
		lblWhiteTime = new JLabel("White Time : ");
		lblWhiteTime.setPreferredSize(new Dimension(160, 20));
		paneInfo.add(lblWhiteTime);
		lblBlackTime = new JLabel("Black Time : ");
		lblBlackTime.setPreferredSize(new Dimension(160, 20));
		paneInfo.add(lblBlackTime);

		txtDiskNum = new JTextField();
		txtDiskNum.setPreferredSize(new Dimension(160, 40));
		txtDiskNum.setEditable(false);
		paneInfo.add(txtDiskNum);

		listMove = new JList<String>();
		listMove.setVisibleRowCount(6);
		JScrollPane scroll = new JScrollPane(listMove);
		scroll.setPreferredSize(new Dimension(160, 130));
		paneInfo.add(scroll);

		diskBlack = new ImageIcon(getClass().getResource("disk_b.png"));
		diskWhite = new ImageIcon(getClass().getResource("disk_w.png"));
		empty = new ImageIcon(getClass().getResource("empty.png"));
		available = new ImageIcon(getClass().getResource("available.png"));

		timer = new Timer(true);
	}

	/* update game board and information */
	void updateBoard(){
		// board
		for(int i = 0;i < row * row;i++){
			int r = game.getDisk(i);
			if(r == Const.WHITE){
				btnDisk[i].setIcon(diskWhite);
			}
			else if(r == Const.BLACK){
				btnDisk[i].setIcon(diskBlack);
			}
			else if(game.legalMove(turn, i / 8, i % 8)){
				btnDisk[i].setIcon(available);
			}
			else{
				btnDisk[i].setIcon(empty);
			}
		}
		// information
		txtDiskNum.setText(game.getDiskCount(Const.BLACK) + " : " + game.getDiskCount(Const.WHITE));
		lblWhiteWin.setText("White Win : " + whiteWin);
		lblBlackWin.setText("Black Win : " + blackWin);
		lblRound.setText("Round : " + round + "/" + loop);
		if(turn == Const.BLACK){
			lblNowPlay.setText("Playing : Black");
		}
		else{
			lblNowPlay.setText("Playing : White");
		}
		if(p[turn + 1] == Const.Player.COMPUTER){
			lblNowPlay.setText(lblNowPlay.getText().toString() + "(" + cmd[turn + 1] + ")");
		}
		lblWhiteTime.setText("White Time : " + whiteTime / 1000);
		lblBlackTime.setText("Black Time : " + blackTime / 1000);


		strMoveSeq = new String[step];
		for(int i = 0;i < step;i++){
			String strMove = new String(String.valueOf(i+1) + Const.strNowPlay[i%2] + String.valueOf(squareToString(moveSeq[i])));
			strMoveSeq[step - i - 1] = strMove;
		}
		listMove.setListData(strMoveSeq);
		listMove.setSelectedIndex(0);
	}

	/* switch and record game move */
	void switchPlayer(int move){
		moveSeq[step] = move;
		step++;
		turn *= -1;

		if(step % 2 == 1){
			blackTime = System.currentTimeMillis() - whiteTime - startTime;
		}
		else if(step % 2 == 0){
			whiteTime = System.currentTimeMillis() - blackTime - startTime;
		}
	}

	/* show the game result */
	void showResult(Game.Result r){
		if(r == Game.Result.BLACK_WIN){
			JOptionPane.showMessageDialog(null, "Black win !!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(r == Game.Result.WHITE_WIN){
			JOptionPane.showMessageDialog(null, "White win !!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(r == Game.Result.DRAW){
			JOptionPane.showMessageDialog(null, "Draw !!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void actionPerformed(ActionEvent e){
		if(e.getSource() == btnNew){
			PlayerSet window = new PlayerSet(this);
		}
		else if(e.getSource() == btnLoad){
			JFileChooser chooser = new JFileChooser();
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				initGame();
                initTime();
				initPlayer();
				initCount();
				readGame(chooser.getSelectedFile().getPath());
				saveGame("board.txt");
				updateBoard();
			}
		}
		else if(e.getSource() == btnSave){
			JFileChooser chooser = new JFileChooser();
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				saveGame(chooser.getSelectedFile().getPath());
			}
		}
        else if(e.getSource() == btnScriptPlay){
            ScriptPlay window = new ScriptPlay(this);
        }
		else if(e.getSource() == btnPass){
			switchPlayer(-1);
			saveGame("board.txt");
			updateBoard();
			if(p[turn + 1] == Const.Player.COMPUTER){
				playAI(1);
			}
		}

		for(int i = 0;i < row * row;i++){
			if(e.getSource() == btnDisk[i]){
				if(game.doMove(turn, i / row, i % row)){
					switchPlayer(i);
					saveGame("board.txt");
					updateBoard();
					if(game.getResult() != Game.Result.NONE){
						setCount(game.getResult());
						Date date = new Date();
						DateFormat df = new SimpleDateFormat("yyMMdd_HHmmss");
						saveGame("Log_" + df.format(date) + ".txt");
						showResult(game.getResult());
					}
				}
				if(p[turn + 1] == Const.Player.COMPUTER){
					playAI(1);
				}
				break;
			}
		}

	}

	/* save current move sequence to file */
	protected void saveGame(String path){
		try{

			FileWriter fWrite = new FileWriter(path, false);
			BufferedWriter fOut = new BufferedWriter(fWrite);
			for(int i = 0;i < step;i++){
				fOut.write(squareToString(moveSeq[i]));
			}
			fOut.flush();
			fWrite.close();
		}catch(IOException e){
			JOptionPane.showMessageDialog(null, "Save File Error !!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected int getMove(){
		String data = "A0";
		try{
			FileReader fRead = new FileReader("move.txt");
			BufferedReader fIn = new BufferedReader(fRead);
			data = fIn.readLine();
			fRead.close();
		}catch(IOException e){
			JOptionPane.showMessageDialog(null, "Read File Error !!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return stringToSquare(data);
	}

	private char[] squareToString(int pos){
		if(pos == -1){
			char[] n = {'-', '-'};
			return n;
		}
		char[] n = {(char)(pos % row + 97), (char)(pos / row + 49)};
		return n;
	}

	private int stringToSquare(String pos){
		if(pos.charAt(0) == '-' && pos.charAt(1) == '-'){
			return -1;
		}
		int sum = (pos.charAt(0) - 97) + (pos.charAt(1) - 49) * row;
		return sum;
	}

	private void readGame(String path){
		try{
			String data;
			FileReader fRead = new FileReader(path);
			BufferedReader fIn = new BufferedReader(fRead);
			data = fIn.readLine();
			fRead.close();

			int ans;
			for(int i = 0;i < data.length();i+=2){
				ans = (data.charAt(i) - 97) + (data.charAt(i + 1) - 49) * row;
				game.doMove(turn, ans / 8, ans % 8);
				switchPlayer(ans);
			}
		}catch(IOException e){
			JOptionPane.showMessageDialog(null, "Read File Error !!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void playAI(int loop){
		PlayAI ai = new PlayAI(this, loop);
		Thread play = new Thread(ai);
		play.start();
	}

    protected void playScriptAI(int loop){
        ScriptPlayAI ai = new ScriptPlayAI(this, loop);
		Thread play = new Thread(ai);
		play.start();
    }

	protected void setCount(Game.Result r){
		if(r == Game.Result.BLACK_WIN){
			blackWin++;
		}
		else if(r == Game.Result.WHITE_WIN){
			whiteWin++;
		}
		round++;
	}
}

public class OthelloGUI{
	public static void main(String[] args){
		CFrame frame = new CFrame();
	}
}
