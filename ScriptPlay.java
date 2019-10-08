import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScriptPlay extends JFrame implements ActionListener{
    JLabel lblOne, lblTwo, lblLoop;
    JTextField txtCmdOne, txtCmdTwo;
    JButton btnOk;
    CFrame gameUI;

    private int loopTime = 4;

    ScriptPlay(CFrame gameUI){
        super("ScriptPlay Settings");
		setSize(400, 250);
		setVisible(true);

        this.gameUI = gameUI;
        initUI();
    }

    /* initialize the interface */
    private void initUI(){
        setLayout(null);

        lblOne = new JLabel("Black Player : Computer");
        lblOne.setBounds(20, 20, 200, 20);
        add(lblOne);

        txtCmdOne = new JTextField("Enter your command here");
        txtCmdOne.setBounds(20, 50, 270, 20);
        add(txtCmdOne);

        lblTwo = new JLabel("White Player : Computer");
        lblTwo.setBounds(20, 80, 200, 20);
        add(lblTwo);

        txtCmdTwo = new JTextField("Enter your command here");
        txtCmdTwo.setBounds(20, 110, 270, 20);
        add(txtCmdTwo);

        lblLoop = new JLabel("Loop times : " + loopTime);
        lblLoop.setBounds(20, 140, 140, 20);
        add(lblLoop);

        btnOk = new JButton("Start");
        btnOk.setBounds(160, 170, 80, 30);
        btnOk.addActionListener(this);
        add(btnOk);
    }

    /* button actions */
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnOk){
            // modify the settings of game interface
            gameUI.p[2] = Const.Player.COMPUTER;
            gameUI.cmd[2] = txtCmdOne.getText();

            gameUI.p[0] = Const.Player.COMPUTER;
            gameUI.cmd[0] = txtCmdTwo.getText();

            gameUI.loop = loopTime;

            super.dispose();

            gameUI.initGame();
            gameUI.initTime();
			gameUI.initCount();
			gameUI.saveGame("board.txt");
			gameUI.updateBoard();

            // both player are computer, repeat serveral times of computer player games

            gameUI.playScriptAI(gameUI.loop);
        }
    }
}
