package minesweeper;

/**
 * Created by Saif on 12/9/2016.
 */
public class TicketSystem {

    public MineAI mineAI;
    int threshhold = 2;

    public void ticketSurrounding(int ticketArray[][], int x, int y)
    {
        for(int row=-1;row<2;row++)
        {
            for (int column=-1;column<2;column++)
            {
                if (x+column > 0 && y+row > 0 && x+column < MineReader.rows-2 && y+row < MineReader.columns-2) {
                    if (ticketArray[x + column][y + row] == Integer.MAX_VALUE) {
                        ticketArray[x + column][y + row] = 0;
                    }
                    if (MineReader.field[x][y] == 0) {
                        ticketArray[x + column][y + row] += 1;
                    }
                }
            }
        }
    }

    public void fillMatrix(int ticketArray [][]){
        for(int x = 0; x < ticketArray.length; x++){
            for (int y = 0; y < ticketArray[x].length; y++) {
                ticketArray[x][y] = Integer.MAX_VALUE;
            }
        }
        for(int x = 0; x < ticketArray.length; x++){
            for (int y = 0; y < ticketArray[x].length; y++) {
                if (MineReader.field[x][y] > 0){
                    ticketSurrounding(ticketArray,x,y);
                }
            }
        }


    }

    public int[] findMin (int ticketArray [][]) {
        int coord[] = new int[2];
        int minTicket = Integer.MAX_VALUE;
        for(int x = 0; x < ticketArray.length; x++){
            for (int y = 0; y < ticketArray[x].length; y++) {
                if(ticketArray[x][y] < minTicket){
                    minTicket = ticketArray[x][y];
                    coord[0] = x;
                    coord[1] = y;
                }
            }
        }
        if(minTicket > threshhold){
            coord[0] = Integer.MAX_VALUE;
            coord[1] = Integer.MAX_VALUE;
            return coord;
        }
        return coord;
    }

    public void makeMove () {
        System.out.println("hi");
        int ticketArray[][] = new int[MineReader.rows - 2][MineReader.columns - 2];
        fillMatrix(ticketArray);
        int coord[] = findMin(ticketArray);
        System.out.println(coord[0]);
        System.out.println(coord[1]);
        if(coord[0] != Integer.MAX_VALUE){

        }

    }
}
