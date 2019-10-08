public class Const{
    public final static int WHITE = -1;
	public final static int BLACK = 1;
	public final static int EMPTY = 0;

    public final static short[][] DIR = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    enum Player{HUMAN, COMPUTER};
	
	public final static String[] strNowPlay = {". Black : ", ". White : "};
}
