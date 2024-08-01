package edu.wm.cs.cs301.slidingpuzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SimplePuzzleState implements PuzzleState {
	
	private int tiles[][] = new int[11][11];
	
	private SimplePuzzleState parent;
	private Operation op; 
	private int dimension;
	private int pathLength;
	private int numberOfEmptySlots;
	
	private Operation firstOp = null; // first operation held for drag()
	
	public SimplePuzzleState() {
		this.parent = null;
		this.op = null;
		this.dimension = 0;
		this.pathLength = 0;
	}
	
	@Override
	public void setToInitialState(int dimension, int numberOfEmptySlots) {
		
		int tileNum = 1;
		
		// creates array of initial values
		for (int row = 0; row < dimension; row++) {
			for (int col = 0; col < dimension; col++) {
				this.tiles[row][col] = tileNum;
				tileNum++;
			}
		}
		
		// creates empty slot(s)
		if (numberOfEmptySlots == 1) {
			this.tiles[dimension-1][dimension-1] = 0;
		} else if (numberOfEmptySlots == 2) {
			this.tiles[dimension-1][dimension-1] = 0;
			this.tiles[dimension-1][dimension-2] = 0;
		} else if (numberOfEmptySlots == 3) {
			this.tiles[dimension-1][dimension-1] = 0;
			this.tiles[dimension-1][dimension-2] = 0;
			this.tiles[dimension-1][dimension-3] = 0;
		}
		
		this.dimension = dimension;
		this.numberOfEmptySlots = numberOfEmptySlots;
		this.parent = null;
		this.op = null;

	}

	@Override
	public int getValue(int row, int column) {
		return this.tiles[row][column];
		
	}

	@Override
	public PuzzleState getParent() {
		
		if (parent != null) {
			return this.parent;
		} else {
			return null;
		}
		
	}

	@Override
	public Operation getOperation() {
		return this.op;
		
	}

	@Override
	public int getPathLength() {
		return this.pathLength;
		
	}

	@Override
	public PuzzleState move(int row, int column, Operation op) {
		
	    SimplePuzzleState board = new SimplePuzzleState();
	    
	    board.parent = this;
	    
	    // 2D array copying information from user NawaMan at https://stackoverflow.com/questions/1686425/copy-a-2d-array-in-java
	    int tilesCopy[][] = new int[tiles.length][];
	    for (int i = 0; i < tiles.length; i++) {
	    	tilesCopy[i] = tiles[i].clone();
	    }
	    
	    board.tiles = tilesCopy;
	    
	    board.pathLength = board.parent.pathLength;
	    board.dimension = board.parent.dimension;
	    board.numberOfEmptySlots = board.parent.numberOfEmptySlots;
		
		switch (op) {
		
			case MOVERIGHT:
				if (column < this.dimension-1) {
					if (board.isEmpty(row, column + 1)) {
						board.tiles[row][column + 1] = board.tiles[row][column];
						board.tiles[row][column] = 0;
						board.op = op;
						board.pathLength++;					
						
						return board;
					} 
				}
				
				break;
		
			case MOVELEFT:
				if (column > 0) {
					if (board.isEmpty(row, column - 1)) {
						board.tiles[row][column - 1] = board.tiles[row][column];
						board.tiles[row][column] = 0;
						board.op = op;
						board.pathLength++;
						
						return board;
					}	
				}
				
				break;
		
			case MOVEUP:
				if (row > 0) {
					if (board.isEmpty(row - 1, column)) {
						board.tiles[row - 1][column] = board.tiles[row][column];
						board.tiles[row][column] = 0;
						board.op = op;
						board.pathLength++;
						
						return board;
					}
				}
				
				break;
		
			case MOVEDOWN:
				if (row < this.dimension-1) {
					if (board.isEmpty(row + 1, column)) {
						board.tiles[row + 1][column] = board.tiles[row][column];
						board.tiles[row][column] = 0;
						board.op = op;
						board.pathLength++;

						return board;
					}
				}
				
				break;
				
			default:
				return null;
				
		}
		
		return null;

	}

	@Override
	public PuzzleState drag(int startRow, int startColumn, int endRow, int endColumn) {
		
		SimplePuzzleState board = new SimplePuzzleState();
		
		boolean deadEnd;
		boolean deadEndTwo;
		
		// 2D array copying information from user NawaMan at https://stackoverflow.com/questions/1686425/copy-a-2d-array-in-java
		int tilesCopy[][] = new int[tiles.length][];
	    for (int i = 0; i < tiles.length; i++) {
	    	tilesCopy[i] = tiles[i].clone();
	    }
	    
	    board.tiles = tilesCopy;
	    
	    board.dimension = this.dimension;
	    board.numberOfEmptySlots = this.numberOfEmptySlots;
	    
	    // checks that initial tile is there and destination tile is empty
	    if (tiles[startRow][startColumn] == 0 | tiles[endRow][endColumn] != 0) {
	    	return null;
	    }

	    switch (numberOfEmptySlots) {
	    
	    	case 1:
	    		
	    		board = (SimplePuzzleState) board.oneDrag(board, startRow, startColumn, endRow, endColumn);
	    		return board;
	    			    		
	    	case 2:
	    		
	    		// dead end check
	    		deadEnd = board.deadEndCheck(board, endRow, endColumn);

	    		if (deadEnd == true) {
		    		board = (SimplePuzzleState) board.oneDrag(board, startRow, startColumn, endRow, endColumn);
		    		return board;
	    		}
	    		
	    		// no dead ends
	    		board = (SimplePuzzleState) board.twoDrag(board, startRow, startColumn, endRow, endColumn);
	    		return board;
	    		
	    	case 3: 
	    		
	    		// one slot dead end check
	    		deadEnd = board.deadEndCheck(board, endRow, endColumn);
	    		
	    		if (deadEnd == true) {
		    		board = (SimplePuzzleState) board.oneDrag(board, startRow, startColumn, endRow, endColumn);
		    		return board;
	    		}
	    		
	    		// two slots dead end check
	    		deadEndTwo = board.deadEndTwoCheck(board, endRow, endColumn);

	    		if (deadEndTwo == true) {
	    			firstOpHunt(board, startRow, startColumn, endRow, endColumn);
		    		board = (SimplePuzzleState) board.twoDrag(board, startRow, startColumn, endRow, endColumn);
		    		return board;
	    		}
	    		
	    		// no dead ends
	    		board.firstOp = null;
	    		board = (SimplePuzzleState) board.threeDrag(board, startRow, startColumn, endRow, endColumn);
	    		return board;
	    		
	    }		
		
		return null;
		
	}

	private boolean deadEndCheck(SimplePuzzleState board, int endRow, int endColumn) {
		
		boolean deadEnd = true;
		
		if (endColumn+1 < board.dimension) {
			if (board.tiles[endRow][endColumn+1] == 0) {
				deadEnd = false;
			}
		}
		if (endColumn-1 >= 0) {
			if (board.tiles[endRow][endColumn-1] == 0) {
				deadEnd = false;
			}
		}
		if (endRow+1 < board.dimension) {
			if (board.tiles[endRow+1][endColumn] == 0) {
				deadEnd = false;
			}
		}
		if (endRow-1 >= 0) {
			if (board.tiles[endRow-1][endColumn] == 0) {
				deadEnd = false;
			}
		}
		
		return deadEnd;
		
	}
	
	private boolean deadEndTwoCheck(SimplePuzzleState board, int endRow, int endColumn) {
		
		boolean deadEndTwo = true;		

		for (int row = 0; row < board.dimension; row++) {
			for (int col = 0; col < board.dimension; col++) {
				if (board.tiles[row][col] == 0) {
					
					// checks for available path to the right
					if (col+1 < board.dimension) {
						if (board.tiles[row][col+1] == 0) {
							
							if (col+2 < board.dimension) {
								if (board.tiles[row][col+2] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (row-1 >= 0) {
								if (board.tiles[row-1][col+1] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (row+1 < board.dimension) {
								if (board.tiles[row+1][col+1] == 0) {
									deadEndTwo = false;
								}
							}
						}
					}
					
					// checks for available path to the left
					if (col-1 >= 0) {
						if (board.tiles[row][col-1] == 0) {
							
							if (col-2 >= 0) {
								if (board.tiles[row][col-2] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (row-1 >= 0) {
								if (board.tiles[row-1][col-1] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (row+1 < board.dimension) {
								if (board.tiles[row+1][col-1] == 0) {
									deadEndTwo = false;
								}
							}
						}
					}
					
					// checks for available path from down
					if (row+1 < board.dimension) {
						if (board.tiles[row+1][col] == 0) {
							
							if (col-1 >= 0) {
								if (board.tiles[row+1][col-1] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (col+1 < board.dimension) {
								if (board.tiles[row+1][col+1] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (row+2 < board.dimension) {
								if (board.tiles[row+2][col] == 0) {
									deadEndTwo = false;
								}
							}
						}
					}
					
					// checks for available path from up
					if (row-1 >= 0) {
						if (board.tiles[row-1][col] == 0) {
							
							if (col-1 >= 0) {
								if (board.tiles[row-1][col-1] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (col+1 < board.dimension) {
								if (board.tiles[row-1][col+1] == 0) {
									deadEndTwo = false;
								}
							}
							
							if (row-2 >= 0) {
								if (board.tiles[row-2][col] == 0) {
									deadEndTwo = false;
								}
							}
						}
					}
				}
			}
		}

		return deadEndTwo;
		
	}

	private void firstOpHunt(SimplePuzzleState board, int startRow, int startColumn, int endRow, int endColumn) {
		
		if (startColumn-1 >= 0) {
			if (startColumn > endColumn & board.tiles[startRow][startColumn-1] == 0) {
				board.firstOp = Operation.MOVELEFT;
			}
		}
		
		if (startColumn+1 < board.dimension) {
			if (startColumn < endColumn & board.tiles[startRow][startColumn+1] == 0) {
				board.firstOp = Operation.MOVERIGHT;
			}
		}
		
		if (startRow-1 >= 0) {
			if (startRow > endRow & board.tiles[startRow-1][startColumn] == 0) {
				board.firstOp = Operation.MOVEUP;
			}
		}
		
		if (startRow+1 < board.dimension) {
			if (startRow < endRow & board.tiles[startRow+1][startColumn] == 0) {
				board.firstOp = Operation.MOVEDOWN;
			}
		}
	}

	private PuzzleState oneDrag(SimplePuzzleState board, int startRow, int startColumn, int endRow, int endColumn) {
		
		if (startColumn+1 < board.dimension) {
			if (board.tiles[startRow][startColumn+1] == 0 & startRow == endRow & startColumn+1 == endColumn) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVERIGHT);
				return board;
			}
		}
		
		if (startColumn-1 >= 0) {
			if (board.tiles[startRow][startColumn-1] == 0 & startRow == endRow & startColumn-1 == endColumn) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVELEFT);
				return board;
			}
		}
		
		if (startRow+1 < board.dimension) {
			if (board.tiles[startRow+1][startColumn] == 0 & startRow+1 == endRow & startColumn == endColumn) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVEDOWN);
				return board;
			}
		}
		
		if (startRow-1 >= 0) {
			if (board.tiles[startRow-1][startColumn] == 0 & startRow-1 == endRow & startColumn == endColumn) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVEUP);
				return board;
			}
		}
		
		return null;
		
	}
	
	private SimplePuzzleState twoDrag(SimplePuzzleState board, int startRow, int startColumn, int endRow, int endColumn) {
		
		// right first moves
		if (startColumn+1 < board.dimension & (board.firstOp == null | board.firstOp == Operation.MOVERIGHT)) {
			if (board.tiles[startRow][startColumn+1] == 0) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVERIGHT);
				
				if (startRow == endRow & startColumn+1 == endColumn) {
					return board;
				}
				
				board = (SimplePuzzleState) board.oneDrag(board, startRow, startColumn+1, endRow, endColumn);
	    		return board;
			}
		}
		
		// left first moves
		if (startColumn-1 >= 0 & (board.firstOp == null | board.firstOp == Operation.MOVELEFT)) {
			if (tiles[startRow][startColumn-1] == 0) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVELEFT);
				
				if (startRow == endRow & startColumn-1 == endColumn) {
					return board;
				}
				
				board = (SimplePuzzleState) board.oneDrag(board, startRow, startColumn-1, endRow, endColumn);
	    		return board;
			}
		}
		
		// down first moves
		if (startRow+1 < board.dimension & (board.firstOp == null | board.firstOp == Operation.MOVEDOWN)) {
			if (tiles[startRow+1][startColumn] == 0 ) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVEDOWN);
				
				if (startRow+1 == endRow & startColumn == endColumn) {
					return board;
				}
				
				board = (SimplePuzzleState) board.oneDrag(board, startRow+1, startColumn, endRow, endColumn);
	    		return board;
			}
		}
		
		// up first moves
		if (startRow-1 >= 0 & (board.firstOp == null | board.firstOp == Operation.MOVEUP)) {
			if (tiles[startRow-1][startColumn] == 0) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVEUP);
				
				if (startRow-1 == endRow & startColumn == endColumn) {
					return board;
				}
				
				board = (SimplePuzzleState) board.oneDrag(board, startRow-1, startColumn, endRow, endColumn);
	    		return board;
			}
		}
		
		return null;
		
	}
	
	private SimplePuzzleState threeDrag(SimplePuzzleState board, int startRow, int startColumn, int endRow, int endColumn) {
		
		boolean deadEndTwo = false;
		
		// right first moves
		if (startColumn+1 < board.dimension & (board.firstOp == null | board.firstOp == Operation.MOVERIGHT)) {
			if (board.tiles[startRow][startColumn+1] == 0) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVERIGHT);
				
				if (startRow == endRow & startColumn+1 == endColumn) {
					return board;
				}
				
				firstOpHunt(board, startRow, startColumn+1, endRow, endColumn);
				board = (SimplePuzzleState) board.twoDrag(board, startRow, startColumn+1, endRow, endColumn);
	    		return board;
			}
		}
		
		// left first moves
		if (startColumn-1 >= 0 & (board.firstOp == null | board.firstOp == Operation.MOVELEFT)) {
			if (tiles[startRow][startColumn-1] == 0) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVELEFT);
				
				if (startRow == endRow & startColumn-1 == endColumn) {
					return board;
				}
				
				firstOpHunt(board, startRow, startColumn-1, endRow, endColumn);
				
				board = (SimplePuzzleState) board.twoDrag(board, startRow, startColumn-1, endRow, endColumn);
	    		return board;
			}
		}
		
		// down first moves
		if (startRow+1 < board.dimension & (board.firstOp == null | board.firstOp == Operation.MOVEDOWN)) {
			if (tiles[startRow+1][startColumn] == 0 ) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVEDOWN);
				
				if (startRow+1 == endRow & startColumn == endColumn) {
					return board;
				}
				
				firstOpHunt(board, startRow+1, startColumn, endRow, endColumn);
				board = (SimplePuzzleState) board.twoDrag(board, startRow+1, startColumn, endRow, endColumn);
	    		return board;
			}
		}
		
		// up first moves
		if (startRow-1 >= 0 & (board.firstOp == null | board.firstOp == Operation.MOVEUP)) {
			if (tiles[startRow-1][startColumn] == 0) {
				board = (SimplePuzzleState) board.move(startRow, startColumn, Operation.MOVEUP);
				
				if (startRow-1 == endRow & startColumn == endColumn) {
					return board;
				}
				
				firstOpHunt(board, startRow-1, startColumn, endRow, endColumn);
				board = (SimplePuzzleState) board.twoDrag(board, startRow-1, startColumn, endRow, endColumn);
	    		return board;
			}
		}
		
		// two slots dead end check
		deadEndTwo = board.deadEndTwoCheck(board, endRow, endColumn);

		if (deadEndTwo == true) {
			firstOpHunt(board, startRow, startColumn, endRow, endColumn);
    		board = (SimplePuzzleState) board.twoDrag(board, startRow, startColumn, endRow, endColumn);
    		return board;
		}
		
		return null;
		
	}

	@Override
	public PuzzleState shuffleBoard(int pathLength) {
		
		if (pathLength == 0) {
			return this;
		}
		
		SimplePuzzleState board = new SimplePuzzleState();
		
		int randRow;
		
		// 2D array copying information from user NawaMan at https://stackoverflow.com/questions/1686425/copy-a-2d-array-in-java
		int tilesCopy[][] = new int[tiles.length][];
	    for (int i = 0; i < tiles.length; i++) {
	    	tilesCopy[i] = tiles[i].clone();
	    }
	    
	    board.tiles = tilesCopy;
	    
		board.dimension = this.dimension;
		board.numberOfEmptySlots = this.numberOfEmptySlots;
		
		int moves = 0;
		
		while (moves < pathLength) {
			
			randRow = getRand(board.dimension);
			
			// gets empty tile from random row, column
			for (int col = 0; col < board.dimension; col++) {
				if (board.tiles[randRow][col] == 0) {
					
					// gets possible operations of surrounding tiles
					int randOp;
					
					boolean rightPossible = false;
					boolean leftPossible = false;
					boolean upPossible = false;
					boolean downPossible = false;
					
					if (col-1 >= 0) {
						rightPossible = true;
					}
					
					if (col+1 <= board.dimension-1) {
						leftPossible = true;
					}
					
					if (randRow-1 >= 0) {
						downPossible = true;
					}
					
					if (randRow+1 <= board.dimension-1) {
						upPossible = true;
					}
					
					// gets and moves random operation
					randOp = getRand(4);
					
					if (randOp == 0 & rightPossible) {
						if (board.tiles[randRow][col-1] != 0) {
							board = (SimplePuzzleState) board.move(randRow, col-1, Operation.MOVERIGHT);
							moves++;
						}
					}
					
					else if (randOp == 1 & leftPossible) {
						if (board.tiles[randRow][col+1] != 0) {
							board = (SimplePuzzleState) board.move(randRow, col+1, Operation.MOVELEFT);
							moves++;
						}
					}
					
					else if (randOp == 2 & upPossible) {
						if (board.tiles[randRow+1][col] != 0) {
							board = (SimplePuzzleState) board.move(randRow+1, col, Operation.MOVEUP);
							moves++;
						}
					}
					
					else if (randOp == 3 & downPossible) {
						if (board.tiles[randRow-1][col] != 0) {
							board = (SimplePuzzleState) board.move(randRow-1, col, Operation.MOVEDOWN);
							moves++;
						}
					}
				}
			}
		}		
	
		board.pathLength = pathLength;
		
		return board;
	
	}
	
	private int getRand(int rows) {
		
		Random random = new Random();
		
		int moveOp;
		
		moveOp = random.nextInt(rows);
		
		return moveOp;
		
	}

	@Override
	public boolean isEmpty(int row, int column) {
		
		if (tiles[row][column] == 0) {
			return true;
		} else { 
			return false; 
		}
		
	}

	@Override
	public PuzzleState getStateWithShortestPath() {
		return this;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		
		boolean tileEquality = true;
		
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		SimplePuzzleState other = (SimplePuzzleState) obj;
		
        ArrayList<Integer> otherTiles = new ArrayList<Integer>();
        ArrayList<Integer> objTiles = new ArrayList<Integer>();
        
		for (int row = 0; row < this.dimension; row++) {
			for (int col = 0; col < this.dimension; col++) {
				otherTiles.add(other.tiles[row][col]);
			}
		}
		
		for (int row = 0; row < this.dimension; row++) {
			for (int col = 0; col < this.dimension; col++) {
				objTiles.add(tiles[row][col]);
			}
		}
		
		for (int num = 0; num < otherTiles.size(); num++) {
			if (otherTiles.get(num) != objTiles.get(num)) {
				tileEquality = false;
			}
		}
		
		return tileEquality;
				
	}
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		
		result = prime * result + Arrays.deepHashCode(tiles);
		return result;
		
	}
	
//	public void printBoard() {
//		for (int rows = 0; rows < this.dimension; rows++) {
//			for (int cols = 0; cols < this.dimension; cols++) {
//				System.out.print(this.tiles[rows][cols]+ " ");
//			}
//			System.out.println(" ");
//		}
//	}
	
}