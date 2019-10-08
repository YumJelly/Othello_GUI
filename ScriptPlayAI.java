import javax.swing.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class ScriptPlayAI implements Runnable{
    CFrame gameUI;
    int loop;
    boolean step;

    int scriptSize = 4;
    protected int[] script;

    public ScriptPlayAI(CFrame gameUI, int loop){
        this.gameUI = gameUI;
        this.loop = loop;

        script = new int[scriptSize];
        script[0] = 19;
        script[1] = 26;
        script[2] = 44;
        script[3] = 37;
    }

    // load script : play first step, and switch player
    protected void loadScript(int loop){
        gameUI.game.doMove(gameUI.turn, script[(loop - 1) % scriptSize] / gameUI.game.row, script[(loop - 1) % scriptSize] % gameUI.game.row);
        gameUI.switchPlayer(script[(loop - 1) % scriptSize]);
        gameUI.saveGame("board.txt");
        gameUI.updateBoard();
    }

    public void run(){
        int pos;
        // times of computer player runs
        while(loop > 0){
            // initial game and load script
            gameUI.initGame();
            gameUI.initTime();
    		gameUI.saveGame("board.txt");
    		gameUI.updateBoard();
            loadScript(loop);
            while(true){
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
                else if(gameUI.game.doMove(gameUI.turn, pos / gameUI.game.row, pos % gameUI.game.row)){
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
        }
    }


}
