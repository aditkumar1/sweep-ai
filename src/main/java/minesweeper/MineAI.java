package minesweeper;


public class MineAI {
	Minesweeper minesweeper;
	MineReader fieldReader;

	int threshhold = 3;
	int coord[] = new int[2];

	int[][] guessElligible;
	boolean fieldChanged = false;

	public int numGuesses;
	public int lastNumGuesses;
	public int numIterations;
	public int bombsCleared;

	public int flagEvents;
	public int clearEvents;

	long startTime;
	long endTime;

	public MineAI(Minesweeper minesweeper){
		this.minesweeper = minesweeper;
		fieldReader = new MineReader(this.minesweeper);
	}

	public void initialize()
	{
		endTime=startTime;
		fieldReader.init();
		startTime = System.currentTimeMillis();
		digRandom(false);
		numGuesses=0;
		lastNumGuesses=0;
		numIterations=0;
	}
	public void mainLoop()
	{

		while(!fieldReader.won && !fieldReader.dead)
		{
			System.out.println("\n ITERATING AGAIN: FIELD CHANGE STATUS: "+fieldChanged+"");
			processeEntireField();
			//makeMove();
		}
		fieldReader.updateField();
		System.out.print("DONE!" + " --STATUS: ");
		if(fieldReader.dead)
			System.out.println("DEFEAT. ");
		else
			System.out.println("VICTORY!!! ");
		System.out.println("stats: " + numIterations +" iterations with "+numGuesses
				+" guesses. Time: " + (double)(endTime-startTime)/1000.0 + " sec");
	}

	public void processeEntireField()
	{
		flagEvents=0;
		clearEvents=0;
		numIterations++;
		int[][] ticketArray = new int[fieldReader.columns+2][fieldReader.rows+2];
		if(fieldChanged)
		{
			fieldChanged=false;
			//fieldReader.updateField();
			//fieldReader.printField();

			System.out.println("processing field");
			processField();

			//fieldReader.printField();
		}
		else
		{
			fieldReader.updateField();
			System.out.println("processing field");
			processField();


			if(!fieldChanged && !fieldReader.dead && !fieldReader.won) {
                if(!makeMove(ticketArray))
					digRandom(true);
			}
		}
		lastNumGuesses = numGuesses-lastNumGuesses;
		System.out.println("iteration " +numIterations + ": " + flagEvents
				+ " flag events, " + clearEvents + " clear events, and "
				+ lastNumGuesses + " guesses!");
	}

	public void processField()
	{
		for(int column=1;column<fieldReader.columns+1;column++)
		{
			for(int row=1;row<fieldReader.rows+1;row++)
			{
				//System.out.print("processing tile: "+column+", "+row);
				processTile(column,row);
			}
		}
	}

	public void processTile(int x, int y)
	{
		int number = fieldReader.field[x][y];

		//System.out.print(" number: " + number);
		//System.out.print(" bombs adjacent: "+numBombsNear(x,y));
		//System.out.print(" tiles adjacent: "+numFreshSquaresNear(x,y));
		if(number>0)
		{
			if(numBombsNear(x,y)>=number && numFreshSquaresNear(x,y)>0)
				clearSurrounding(x,y);
			else if ((numFreshSquaresNear(x,y)+numBombsNear(x,y))==number && numBombsNear(x,y)!=number)
				flagSurrounding(x,y);
		}
		//System.out.println();
	}

	public void clearSurrounding(int x, int y)
	{
		clearEvents++;
		//System.out.println(" - CLEARING BOMBS NEAR "+x+", "+y);
		for(int row=-1;row<2;row++)
		{
			for (int column=-1;column<2;column++)
			{
				if(fieldReader.field[x+column][y+row]==0)
					dig(x+column,y+row);
			}
		}
	}

	public void flagSurrounding(int x, int y)
	{
		flagEvents++;
		//System.out.println(" - FLAGGING BOMBS NEAR "+x+", "+y);
		for(int row=-1;row<2;row++)
		{
			for (int column=-1;column<2;column++)
			{
				//System.out.print("	type at " + (x+column)+", "+(y+row)+": " + fieldReader.field[x+column][y+row]+", ");
				if(fieldReader.field[x+column][y+row]==0)
				{
					//System.out.print(" -flagging");
					flag(x+column,y+row);
				}
				//System.out.println();
			}
		}
	}

	public int numBombsNear(int x, int y)
	{
		int n=0;

		for(int row=-1;row<2;row++)
		{
			for (int column=-1;column<2;column++)
			{
				if(fieldReader.field[x+column][y+row]==-2)
					n++;
			}
		}

		return n;
	}

	public int numFreshSquaresNear(int x, int y)
	{
		int n=0;

		for(int row=-1;row<2;row++)
		{
			for (int column=-1;column<2;column++)
			{
				try{
					if(fieldReader.field[x+column][y+row]==0)
					{
						n++;
					}
				} catch(Exception e){}
			}
		}

		return n;
	}

	public void dig(int x, int y)
	{
		if(fieldReader.field[x][y]==0)
			fieldReader.click(x,y,false);
		fieldChanged=true;
		fieldReader.field[x][y]=-3;
		endTime=System.currentTimeMillis();
	}

	public void flag(int x, int y)
	{
		if(fieldReader.field[x][y]==-2)
			System.out.println("		we have got a problem...");
		fieldReader.click(x,y,true);
		fieldReader.field[x][y]=-2;
		fieldChanged=true;
	}

	public Boolean digRandom(Boolean makeGuessonNeighbor)
	{
		System.out.println(" -- MAKING RANDOM GUESS -- ");

		if(fieldReader.numEmpty<2)
		{
			System.out.println("no seeds. guessing completely randomly");
			int xToDig = (int)(Math.random()*fieldReader.columns);
			int yToDig = (int)(Math.random()*fieldReader.rows);

			System.out.println("   guessing square: " + xToDig + ", "+yToDig);

			dig(xToDig,yToDig);
			return true;
		}
		else
		{
			numGuesses++;
			System.out.println("BEGINNING GUESS PROCESS...");
			boolean dug=false;
			int iteration=1;
			do{
				for(int column=1;column<fieldReader.columns;column++)
				{
					for(int row=1;row<fieldReader.rows;row++)
					{
						//System.out.println("	checking guess eligibility of: "+column+", "+row);
						boolean numberNear=numberNear(column,row);
						if(!makeGuessonNeighbor)numberNear=!numberNear;
						System.out.println("row-"+row+"column-"+column+"number near-"+numberNear);
						if(fieldReader.field[column][row]==0 && numberNear)
						{
							dig(column,row);
							dug=true;
							row=99999;
							column=99999;
						}
					}
				}
				iteration++;
			}while(!dug);
			System.out.println("completed guess with " + iteration + " iterations.");
			return dug;
		}
	}

	public boolean numberNear(int x, int y)
	{
		for(int row=-1;row<2;row++)
		{
			for (int column=-1;column<2;column++)
			{
				if(fieldReader.field[x+column][y+row]>0)
				{
					return true;
				}
			}
		}
		return false;
	}

	public void digRandomSurrounding(int x, int y)
	{
		int xOffset;
		int yOffset;

		do
			xOffset=(int)(Math.random()*3)-2;
		while(xOffset==0);

		do
			yOffset=(int)(Math.random()*3)-2;
		while(yOffset==0);

		dig(x+xOffset+1,y+yOffset+1);
	}

	public void test()
	{
		dig(8,8);
	}

	public void ticketSurrounding(int ticketArray[][], int x, int y) {
		for(int row = -1; row < 2;row++) {
			for (int column = -1; column < 2; column++) {
				if (x+row >= 0 && y+column >= 0 && x+row < fieldReader.columns+2 && y+column < fieldReader.rows+2) {
					if (ticketArray[x + row][y + column] == 10 && fieldReader.field[x + row][y + column] == 0) {
						ticketArray[x + row][y + column] = 0;
					}
					if (fieldReader.field[x + row][y + column]  == 0) {
						ticketArray[x + row][y + column] += 1;
					}
				}
			}
		}
	}

	public void fillMatrix(int ticketArray[][]){
		for(int x = 0; x < ticketArray.length; x++){
			for (int y = 0; y < ticketArray[x].length; y++) {
				ticketArray[x][y] = 10;
			}
		}
		for(int x = 0; x < ticketArray.length; x++){
			for (int y = 0; y < ticketArray[x].length; y++) {
				if (fieldReader.field[x][y] > 0) {
					ticketSurrounding(ticketArray,x, y);
				}
			}
		}
	}

	public int[] findMin (int ticketArray[][]) {
		int minTicket = 10;
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
			coord[0] = 10;
			coord[1] = 10;
		}
		return coord;
	}

	public boolean makeMove(int ticketArray[][]) {
		fillMatrix(ticketArray);
		coord = findMin(ticketArray);
//		printField(ticketArray);
		if(coord[0] != 10){
			dig(coord[0],coord[1]);
			clearEvents++;
			return true;
		}
		System.out.println("false");
		return false;
	}

	public void printField(int ticketArray[][])
	{
		for(int y=ticketArray.length-1;y>=0;y--)
		{
			System.out.print("[");
			for(int x=0;x<ticketArray[y].length-1;x++)
			{
				//System.out.println("column: "+x+" row: "+y);
				System.out.print(ticketArray[x][y]+", ");
			}
			System.out.println("]");
		}
	}
}