import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayerSet extends JFrame implements ActionListener{
    JLabel lblOne, lblTwo, lblLoop;
    JCheckBox chkClear;
    JTextField txtCmdOne, txtCmdTwo, txtLoop;
    JRadioButton radHumanOne, radComputerOne, radHumanTwo, radComputerTwo;
    ButtonGroup groupOne, groupTwo;
    JButton btnOk;
    CFrame gameUI;

    PlayerSet(CFrame gameUI){
        super("Player Settings");
		setSize(400, 250);
		setVisible(true);

        this.gameUI = gameUI;
        initUI();
        read();
    }

    /* initialize the interface */
    private void initUI(){
        setLayout(null);

        lblOne = new JLabel("Black");
        lblOne.setBounds(20, 20, 60, 20);
        add(lblOne);
        radHumanOne = new JRadioButton("Human");
        radHumanOne.setBounds(20, 50, 80, 20);
        radComputerOne = new JRadioButton("Computer");
        radComputerOne.setBounds(110, 50, 100, 20);
        groupOne = new ButtonGroup();
        groupOne.add(radHumanOne);
        add(radHumanOne);
        groupOne.add(radComputerOne);
        add(radComputerOne);
        txtCmdOne = new JTextField("Enter your command here");
        txtCmdOne.setBounds(220, 50, 170, 20);
        add(txtCmdOne);

        lblTwo = new JLabel("White");
        lblTwo.setBounds(20, 80, 60, 20);
        add(lblTwo);
        radHumanTwo = new JRadioButton("Human");
        radHumanTwo.setBounds(20, 110, 80, 20);
        radComputerTwo = new JRadioButton("Computer");
        radComputerTwo.setBounds(110, 110, 100, 20);
        groupTwo = new ButtonGroup();
        groupTwo.add(radHumanTwo);
        add(radHumanTwo);
        groupTwo.add(radComputerTwo);
        add(radComputerTwo);
        txtCmdTwo = new JTextField("Enter your command here");
        txtCmdTwo.setBounds(220, 110, 170, 20);
        add(txtCmdTwo);

        lblLoop = new JLabel("Loop times : ");
        lblLoop.setBounds(20, 140, 100, 20);
        add(lblLoop);
        txtLoop = new JTextField("1");
        txtLoop.setBounds(140, 140, 100, 20);
        add(txtLoop);

        chkClear = new JCheckBox("Clear board");
        chkClear.setBounds(20, 170, 130, 20);
        chkClear.setSelected(true);
        add(chkClear);

        btnOk = new JButton("Start");
        btnOk.setBounds(160, 170, 80, 30);
        btnOk.addActionListener(this);
        add(btnOk);
    }

    /* read settings of game interface */
    private void read(){
        txtCmdOne.setText(gameUI.cmd[2]);
        if(gameUI.p[2] == Const.Player.COMPUTER){
            radComputerOne.setSelected(true);
        }
        else if(gameUI.p[2] == Const.Player.HUMAN){
            radHumanOne.setSelected(true);
        }

        txtCmdTwo.setText(gameUI.cmd[0]);
        if(gameUI.p[0] == Const.Player.COMPUTER){
            radComputerTwo.setSelected(true);
        }
        else if(gameUI.p[0] == Const.Player.HUMAN){
            radHumanTwo.setSelected(true);
        }
    }

    /* button actions */
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnOk){
            // modify the settings of game interface
            if(radHumanOne.isSelected()){
                gameUI.p[2] = Const.Player.HUMAN;
            }
            else if(radComputerOne.isSelected()){
                gameUI.p[2] = Const.Player.COMPUTER;
                gameUI.cmd[2] = txtCmdOne.getText();
            }

            if(radHumanTwo.isSelected()){
                gameUI.p[0] = Const.Player.HUMAN;
            }
            else if(radComputerTwo.isSelected()){
                gameUI.p[0] = Const.Player.COMPUTER;
                gameUI.cmd[0] = txtCmdTwo.getText();
            }
            try{
                gameUI.loop = Integer.parseInt(txtLoop.getText());
            }catch(Exception error){
                JOptionPane.showMessageDialog(null, "Please input integer, now play only one times.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            super.dispose();
            // make game interface to start a new game
            // if "Clear board" checkbox is not selceted, will keep current board status
            // useful when load an exist game
            if(chkClear.isSelected()){
                gameUI.initGame();
            }
            gameUI.initTime();
			gameUI.initCount();
			gameUI.saveGame("board.txt");
			gameUI.updateBoard();

            // if both player are computer, then repeat serveral times of computer player games
            if(gameUI.p[2] == Const.Player.COMPUTER && gameUI.p[0] == Const.Player.COMPUTER ){
                gameUI.playAI(gameUI.loop);
            }
            // if black player is computer, then call the computer first
            else if(gameUI.p[2] == Const.Player.COMPUTER || gameUI.p[0] == Const.Player.COMPUTER){
                gameUI.playAI(1);
            }
        }
    }
}
