import javax.swing.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class PlayAI implements Runnable{
    CFrame gameUI;
    int loop;
    boolean step;

    public PlayAI(CFrame gameUI, int loop){
        this.gameUI = gameUI;
        this.loop = loop;
    }

    public void run(){
        int pos;
        // times of computer player runs
        while(loop > 0){
            // if both player are computer, don't stop
            while(gameUI.p[gameUI.turn + 1] == Const.Player.COMPUTER){
                // run and wait computer player's calculate
                try{
                    Process process = Runtime.getRuntime().exec(gameUI.cmd[gameUI.turn + 1]);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
	                    System.out.println(line);
                    }
                    process.waitFor();
                }catch(InterruptedException e){
                    JOptionPane.showMessageDialog(null, "Process Interrupt Error !!", "Error", JOptionPane.ERROR_MESSAGE);
                }catch(IOException e){
                    JOptionPane.showMessageDialog(null, "Process IO Error !!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                pos = gameUI.getMove();
                //PASS
                if(pos == -1){
                    gameUI.switchPlayer(pos);
                    gameUI.saveGame("board.txt");
                    gameUI.updateBoard();
                }
                // normal move
                else if(gameUI.game.doMove(gameUI.turn, pos / 8, pos % 8)){
                    if(gameUI.game.getResult() != Game.Result.NONE){
                        gameUI.setCount(gameUI.game.getResult());
                        gameUI.updateBoard();
                        Date date = new Date();
						DateFormat df = new SimpleDateFormat("yyMMdd_HHmmss");
						gameUI.saveGame("Log_" + df.format(date) + ".txt");
                        // show the game result if one or both of player are human
                        if(gameUI.p[Const.BLACK + 1] == Const.Player.HUMAN || gameUI.p[Const.WHITE + 1] == Const.Player.HUMAN ){
                            gameUI.showResult(gameUI.game.getResult());
                        }
                        break;
                    }
                    else{
                        gameUI.switchPlayer(pos);
                        gameUI.saveGame("board.txt");
                        gameUI.updateBoard();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, "Computer play an illegal move !!", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                try{
                    Thread.sleep(50);
                }catch(InterruptedException e){

                }
            }
            loop--;
            if(loop > 0){
                gameUI.initGame();
                gameUI.initTime();
    			gameUI.saveGame("board.txt");
    			gameUI.updateBoard();
            }
        }
    }
}
