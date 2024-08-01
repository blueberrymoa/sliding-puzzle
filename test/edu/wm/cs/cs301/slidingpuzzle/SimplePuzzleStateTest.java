/**
 * 
 */
package edu.wm.cs.cs301.slidingpuzzle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wm.cs.cs301.slidingpuzzle.PuzzleState.Operation;

/**
 * Junit test cases for SimplePuzzleState implementation of PuzzleState interface.
 * Class contains a number of black box tests.
 * Test scenario: 4x4 board with 1 empty slot
 * Sequence of moves: down, right, right and then reversed as left, left, up 
 * such that cycle back to starting state is closed.
 * 
 * Warning 1: the setUp method performs calls to setToInitialState() and move()
 * which must be implemented to pass even the most basic tests. 
 * If code fails already in setUp method, make it pass test cases in
 * SimplePuzzleStateBasicsTest before working on the more rigid ones here.
 * 
 * Warning 2: the tests use the SimplePuzzleState.equals() method, 
 * which must be implemented as well as the hashcode() method.
 * We assume the following notion of equality: 
 * two states are equal if the tiles on the board are of equal value on all positions. 
 * Parent, path length or operation do not matter for equality.
 * 
 * @author Peter Kemper
 *
 */
public class SimplePuzzleStateTest {

	// Sequence of states and operations of the test scenario
	PuzzleState[] testSequence;
	Operation[] testOps;
	
	// Adjust this flag to true if method getStateWithShortestPath() truly computes
	// the shortest path.
	boolean checkShortestPathCalculation = false;
	
	/**
	 * Junit calls this method automatically each time before executing a method tagged as a test.
	 * It is used here to set up a particular test scenario of a 4x4 board with 1 empty slot
	 * and a sequence of 6 moves that goes a ways from the initial state and then returns.
	 * This gives room to test individual moves, equality of states.
	 * 
	 * Note: it would be sufficient to execute this code just once as the testSequence
	 * and testOps arrays and their objects are not modified by the tests.
	 * @throws java.lang.Exception code was generated automatically, no particular exception thrown
	 */
	@Before
	public void setUp() throws Exception {
		// create a test sequence that starts from the initial state
		// performs 3 steps
		// performs 3 steps in reverse of previous 3 steps 
		// returns to initial state, game over
		int empties = 1;
		int dim = 4;
		testSequence = new PuzzleState[7];
		testOps = new Operation[7];
		testSequence[0] = new SimplePuzzleState();
		testSequence[0].setToInitialState(dim, empties); 
		// 4x4 board with 1 empty slot on last position (3,3)
		// move (2,3)->(3,3), empty (2,3)
		performOneStepForTestScenario(1, Operation.MOVEDOWN, dim-2, dim-1);
		// move (2,2)->(2,3), empty (2,2)
		performOneStepForTestScenario(2, Operation.MOVERIGHT, dim-2, dim-2);
		// move (2,1)->(2,2), empty (2,1)
		performOneStepForTestScenario(3, Operation.MOVERIGHT, dim-2, dim-3);
		// let's reverse this little path back to the starting state
		// move (2,2)->(2,1), empty (2,2)
		performOneStepForTestScenario(4, Operation.MOVELEFT, dim-2, dim-2);
		// move (2,3)->(2,2), empty (2,3)
		performOneStepForTestScenario(5, Operation.MOVELEFT, dim-2, dim-1);
		// move (3,3)->(2,2), empty (3,3)
		performOneStepForTestScenario(6, Operation.MOVEUP, dim-1, dim-1);
	}

	/**
	 * Helper method to perform and store a step of the test scenario in the setup method.
	 * Outcome adds entry to testSequence and testOps arrays.
	 * Code uses given operation op to move a tile from the given slot to the given empty slot.
	 * We assume that this move is possible and legal so we assert that the move operation is
	 * successful. testSequence[stateIndex-1] must have a reference to a PuzzleState.
	 * @param stateIndex the next position in the test sequence of states, 0 is initial state
	 * @param op the operation to get from the  
	 * @param rowIndex row index for the tile to move
	 * @param colIndex column index for the tile to move
	 */
	private void performOneStepForTestScenario(int stateIndex, Operation op, int rowIndex, int colIndex) {
		// test if we have a current state to begin with
		assertTrue(stateIndex > 0 && testSequence[stateIndex-1] != null);
		// check if given slot has a tile to move in the starting state
		assertFalse(testSequence[stateIndex-1].isEmpty(rowIndex, colIndex));
		// would make sense to check if target slot is empty, omitted for now
		
		testSequence[stateIndex] = testSequence[stateIndex-1].move(rowIndex, colIndex, op);
		// move is expected to be successful, there should be a successor state
		// checked here to make code fail fast
		assertTrue(null != testSequence[stateIndex]);
		testOps[stateIndex] = op;
	}

	/**
	 * Junit calls this method automatically each time after executing a method tagged as a test.
	 * Not used here. Left for illustrating purposes.
	 * @throws java.lang.Exception code was generated automatically, no particular exception thrown
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#hashCode()}.
	 * The SimplePuzzleState class must implement (overwrite) the equals method.
	 * If one does so, it is recommended good practice to also overwrite the 
	 * hashCode method to have a consistent class design.
	 */
	@Test
	public void testHashCode() {
		// states that are equal must have the same hash code
		// first and last state in the test sequence are equal
		assertTrue(testSequence[0].equals(testSequence[6]));
		assertTrue(testSequence[0].hashCode() == testSequence[6].hashCode());
		// second and second to last state are equal and they are not the initial state
		assertTrue(testSequence[1].equals(testSequence[5]));
		assertTrue(testSequence[1].hashCode() == testSequence[5].hashCode());
		// third and third to last state are equal and they are not the initial state
		assertTrue(testSequence[2].equals(testSequence[4]));
		assertTrue(testSequence[2].hashCode() == testSequence[4].hashCode());
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#setToInitialState(int, int)}.
	 * The initial state for a given dimension and number of empty slots should list
	 * tiles with numbers in increasing order and have empty slots at the very end.
	 * Empty slots are encoded with zero values.
	 * Tests state with dimension 4 and 1 empty slot.
	 */
	@Test
	public void testSetToInitialState() {
		// create state with initial state
		PuzzleState ps1 = new SimplePuzzleState();
		int empties = 1;
		int dim = 4;
		int len = dim*dim - empties;
		ps1.setToInitialState(dim, empties); // 4x4 board with 1 empty slot
		for (int r=0; r < dim; r++) {
			for (int c=0; c < dim; c++) {
				if (r*dim+c < len) {
					assertEquals(r*dim+c+1, ps1.getValue(r, c));
				}
				else {
					// empties at end positions
					assertEquals(0, ps1.getValue(r, c));
				}
			}
		}
	}
	/**
	 * Test method to check the interaction of {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#setToInitialState(int, int)},
	 * {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#equals(Object)}, and
	 * {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#move(int, int, Operation)}.
	 * Tests states with dimensions 3,4,5 and a number of empty slots between 1 and 2.
	 */
	@Test
	public void testInteractionOfMethods() {
		// create state with initial state
		PuzzleState ps1 = new SimplePuzzleState();
		int empties = 1;
		int dim = 4;
		ps1.setToInitialState(dim, empties); // 4x4 board with 1 empty slot
		// create 2nd state of similar dimension and number of empty slots
		// check equality across initial state settings for different dimension, empty slot settings
		PuzzleState ps2 = new SimplePuzzleState();
		ps2.setToInitialState(dim, empties+1); // 4x4 board with 2 empty slots
		// can't be equal as number of empty slots differs
		assertFalse(ps1.equals(ps2));
		ps2.setToInitialState(dim+1, empties); // 5x5 board with 1 empty slot
		// can't be equal as dimensions differ
		assertFalse(ps1.equals(ps2));
		ps2.setToInitialState(dim, empties); // 4x4 board with 1 empty slot
		// this one should be equal as the initial setting for same dim, empties should match
		assertTrue(ps1.equals(ps2));
		
		// move empty slot on last position, 
		// up and down are inverse operations and lead to same state
		assertTrue(ps2.isEmpty(dim-1, dim-1));
		PuzzleState ps3 = ps2.move(dim-2, dim-1, Operation.MOVEDOWN);
		// resulting state should be different
		assertFalse(ps2.equals(ps3));
		ps3 = ps3.move(dim-1, dim-1, Operation.MOVEUP);
		// resulting state should same again
		assertTrue(ps2.equals(ps3));
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#getOperation()}.
	 * We check if the stored operation for each state matches with
	 * the operation that was used.
	 */
	@Test
	public void testGetOperation() {
		// test with an explicit comparison with individual operations
		assertEquals(testSequence[0].getOperation(),null);
		// move down (2,3)->(3,3), empty (2,3)
		assertEquals(testSequence[1].getOperation(),Operation.MOVEDOWN);
		// move right (2,2)->(2,3), empty (2,2)
		assertTrue(testSequence[2].getOperation() == Operation.MOVERIGHT);
		// move right (2,1)->(2,2), empty (2,1)
		assertTrue(testSequence[3].getOperation() == Operation.MOVERIGHT);
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		assertTrue(testSequence[4].getOperation() == Operation.MOVELEFT);
		// move left (2,3)->(2,2), empty (2,3)
		assertTrue(testSequence[5].getOperation() == Operation.MOVELEFT);
		// move up (3,3)->(2,2), empty (3,3)
		assertTrue(testSequence[6].getOperation() == Operation.MOVEUP);
		// same test but with the help of the testOps array
		for (int i = 0; i <= 6; i++) {
			assertEquals("failed in iteration " + i, testSequence[i].getOperation(), testOps[i]);
		}
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#getParent()}.
	 * We check if the stored parent for each state matches with its predecessor'
	 * in the test sequence.
	 */
	@Test
	public void testGetParent() {
		// the initial state does not have a parent or predecessor
		assertEquals(testSequence[0].getParent(),null);
		// since we stored all states in the sequence, 
		// for each state the parent state is the previous element in the array
		for (int i = 1; i <= 6; i++) {
			assertEquals("failed it iteration " + i, testSequence[i].getParent(),testSequence[i-1]);
		}
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#getPathLength()}.
	 * We check if the path length from the initial state increases when the 
	 * sequence moves away from the initial state and even if 
	 * the sequence reverses direction and gets back to the initial state.
	 * This is because the move operation always extends the path.
	 */
	@Test
	public void testGetPathLength() {
		// create board and move empty slot to middle position
		// 4x4 board with 1 empty slot on last position (3,3)
		assertEquals(0,testSequence[0].getPathLength());
		// move down (2,3)->(3,3), empty (2,3)
		assertEquals(1,testSequence[1].getPathLength());
		// move right (2,2)->(2,3), empty (2,2)
		assertEquals(2,testSequence[2].getPathLength());
		// move right (2,1)->(2,2), empty (2,1)
		assertEquals(3,testSequence[3].getPathLength());
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		assertEquals(4,testSequence[4].getPathLength());
		// move left (2,3)->(2,2), empty (2,3)
		assertEquals(5,testSequence[5].getPathLength());
		// move up (3,3)->(2,2), empty (3,3)
		assertEquals(6,testSequence[6].getPathLength());
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#shuffleBoard(int)}.
	 * The shuffle procedure performs a random walk of a given length.
	 * It works correctly if we can use it to perform a series inverse move operations
	 * that leads back to the initial state.
	 */
	@Test
	public void testShuffleBoard() {
		// set up game with an initial state that has a 4x4 board, one empty tile
		PuzzleState init = new SimplePuzzleState();
		init.setToInitialState(4, 1);
		// evaluate a shuffle path of length 10
		int length = 10;
		PuzzleState current = init.shuffleBoard(length);
		PuzzleState backtracker = current;
		// there is a path with init + 10 states, so total is 11 states
		// that leads from init to current == backtracker.
		
		// Plan of action: 
		// We use backtracker.getParent() to iterate backwards along the shuffle path
		// towards the init state. We expect to finish exactly after length == 10 iterations.
		//
		// We use current=current.move(inverseop) to compute a forward path starting
		// at current that ends exactly after length == 10 iterations at the init state.
		// The operation is carefully selected to reverse the backtracker.getOperation method.
		
		int[] pos = null; // the position of the tile to move for the inverse operation
		Operation op = null; // the inverse operation for backtracker.getOperation
		
		// So, the current state and the backtracker state will be different objects
		// but equal throughout the 2 sequences of operations that we perform together
		//
		// In other words:
		// the current state moves forward with a sequence of operations
		// that reverses how we got to that state and ends at the initial state
		// the backtracker tracks back to the initial state via the parent states
		// the backtracker guides the current state on which operations to perform.
		
		// At this moment:
		assertEquals(current,backtracker);
		// loop needs to make progress on 3 entities: length, current, backtracker
		for (; length > 0; length--) {
			// here: current state and backtracker state have the same arrangement of tiles
			// example: let's call the current state Y and assume there was a transition X -> Y
			// example: from a parent state X with an operation Up on the shuffle path
			
			// the backtracker's distance from the initial state gets shorter with each iteration, 
			// so does loop variable length
			assertEquals("failed at length " + length, length, backtracker.getPathLength());
			
			// determine inverse operation
			// the backtracker walks the shuffle path back so it has a field operation and parent
			// op is the inverse operation of backtracker.getOperation()
			// example: if backtracker.getOperation() == MoveUp then op is computed as MoveDown
			op = getInverseOperation(backtracker);
			assertNotNull("failed at length " + length, op);
			
			// determine which tile to move, 
			// the target state has an empty slot on the position we want
			// update backtracker to target state, 
			// loop progress: backtracker
			// example: lets make the backtracker point to X, the parent state of Y
			backtracker = backtracker.getParent();
			// get position of the tile in the current state that needs to move
			// by finding the empty slot on the parent state, the outcome of the move 
			pos = findEmptySlot(backtracker);
			// example: say, the empty position in X is at (0,3)
			// example: variable current points at Y, so there must be a tile at (0,3)
			// example: such that we can perform moveDown on (0,3) to get from Y to X
			assertNotNull("failed at length " + length, pos);
			// move to previous state
			// note: move always moves forward, so direction is forward and distance increases for state current
			// loop progress: current state
			// example: so let's move current to state Z = Y.move(0,3,movedown)
			current = current.move(pos[0], pos[1], op);
			// compare parent state and newly reached state
			// arrangement of tiles, they should be equal
			// example: so backtracker == X, current == Z, and X and Z need to have the same 
			assertEquals(current,backtracker);
			//loop progress: length decremented in for loop
		}
		// at length 0:
		// current and backtracker must be at an initial state, i.e. a state equal to init
		assertEquals(init,current);
		assertEquals(backtracker,init);
		assertEquals(0,backtracker.getPathLength());
		assertNotEquals(0,current.getPathLength()); // current moves forward, length should be twice the shuffle path
	}

	/**
	 * Find the first empty slot for the given state.
	 * Relies on isEmpty() to work correctly.
	 * @param state the state to analyze
	 * @return int array with [row,column] for position of first empty slot, null if none found
	 */
	private int[] findEmptySlot(PuzzleState state) {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				if (state.isEmpty(r, c)) {
					int[] result = new int[2];
					result[0] = r;
					result[1] = c;
					return result;
				}
			}
		}
		return null;
	}
	/**
	 * Gets the inverse operation for the given state.
	 * Since the state stores the operation how it was reached from its
	 * predecessor state by a move operation. This method
	 * calculates the inverse operation so one can perform
	 * a corresponding inverse move operation to go from the given
	 * state to its predecessor. The initial state does not have this,
	 * so in this case the method returns null.
	 * @param state the state to analyze
	 * @return inverse operation if current state has an operation, null otherwise
	 */
	private Operation getInverseOperation(PuzzleState state) {
		Operation op = state.getOperation();
		if (null == op)
			return null;
		switch (op) {
		case MOVELEFT : 
			return Operation.MOVERIGHT;
		case MOVERIGHT : 
			return Operation.MOVELEFT;
		case MOVEUP : 
			return Operation.MOVEDOWN;
		case MOVEDOWN : 
			return Operation.MOVEUP;
		}
		return null;
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.SimplePuzzleState#equals(java.lang.Object)}.
	 * Two PuzzleStates are equal if they have the tiles arranged on the board in the same way.
	 * Parent, path length and operation do not matter for equality.
	 */
	@Test
	public void testEqualsObject() {
		// try a few things with individual objects
		Object o1 = new SimplePuzzleState();
		Object o2 = null;
		// no object is equal to null, so equals must return false
		assertFalse(o1.equals(null));
		assertFalse(o1.equals(o2));
		o2 = new SimplePuzzleState();
		// here check if the default equals method has been overwritten
		// by a class specific one, 
		// the objects are different, so Object.equals() returns false
		// but the content should be the same, so SimplePuzzleState.equals() 
		// should return true
		assertTrue(o1.equals(o2));
		// Let's initialize the states to a meaningful initial state
		SimplePuzzleState tmp = (SimplePuzzleState)o1;
		tmp.setToInitialState(4, 1);
		// o1 was changed via tmp, so o1 and o2 are different now
		assertFalse(o1.equals(o2));
		tmp = (SimplePuzzleState)o2;
		tmp.setToInitialState(4, 1);
		// o2 was changed via tmp, so o1 and o2 are equal now
		assertTrue(o1.equals(o2));
		tmp.setToInitialState(4, 2);
		// o2 was changed via tmp to different number of empty slots, 
		// so o1 and o2 are different now
		assertFalse(o1.equals(o2));
		
		// Let's test states from the test sequence
		// no object is equal to null, so equals must return false
		assertFalse(testSequence[0].equals(null));
		// move down (2,3)->(3,3), empty (2,3)
		// state 0 and 1 are different
		assertFalse(testSequence[0].equals(testSequence[1]));
		// move right (2,2)->(2,3), empty (2,2)
		// state 1 and 2 are different
		assertFalse(testSequence[1].equals(testSequence[2]));
		// move right (2,1)->(2,2), empty (2,1)
		// state 2 and 3 are different
		assertFalse(testSequence[2].equals(testSequence[3]));
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		// state 2 and 4 are the same
		assertTrue(testSequence[2].equals(testSequence[4]));
		// move left (2,3)->(2,2), empty (2,3)
		// state 1 and 5 are the same
		assertTrue(testSequence[1].equals(testSequence[5]));
		// move up (3,3)->(2,2), empty (3,3)
		// state 0 and 6 are the same
		assertTrue(testSequence[0].equals(testSequence[6]));
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#getValue(int, int)}.
	 */
	@Test
	public void testGetValue() {
		// create board and move empty slot to middle position
		// 4x4 board with 1 empty slot on last position (3,3)
		assertEquals(0,testSequence[0].getValue(3, 3));
		// move down (2,3)->(3,3), empty (2,3)
		assertEquals(0,testSequence[1].getValue(2, 3));
		assertEquals(12,testSequence[1].getValue(3, 3));
		// move right (2,2)->(2,3), empty (2,2)
		assertEquals(0,testSequence[2].getValue(2, 2));
		assertEquals(11,testSequence[2].getValue(2, 3));
		// move right (2,1)->(2,2), empty (2,1)
		assertEquals(0,testSequence[3].getValue(2, 1));
		assertEquals(10,testSequence[3].getValue(2, 2));
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		assertEquals(0,testSequence[4].getValue(2, 2));
		assertEquals(10,testSequence[4].getValue(2, 1));
		// move left (2,3)->(2,2), empty (2,3)
		assertEquals(0,testSequence[5].getValue(2, 3));
		assertEquals(11,testSequence[5].getValue(2, 2));
		// move up (3,3)->(2,2), empty (3,3)
		assertEquals(0,testSequence[6].getValue(3, 3));
		assertEquals(12,testSequence[6].getValue(2, 3));
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#move(int, int, edu.wm.cs.cs301.slidingpuzzle.PuzzleState.Operation)}.
	 * We performed a series of move operations for the test sequence.
	 * We just need to check if the tiles on the board are set up correctly.
	 */
	@Test
	public void testMove() {
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 14 15 0
		// up, right, right
		// 1 2 3 4
		// 5 6 7 8
		// 9 0 10 11
		// 13 14 15 12
		// left, left, down
		int[] correct = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0} ;
		checkTiles(testSequence[0],correct);
		correct[15] = 12;
		correct[11] = 0;
		checkTiles(testSequence[1],correct);
		correct[11] = 11;
		correct[10] = 0;
		checkTiles(testSequence[2],correct);
		correct[10] = 10;
		correct[9] = 0;
		checkTiles(testSequence[3],correct);
		correct[10] = 0;
		correct[9] = 10;
		checkTiles(testSequence[4],correct);
		correct[11] = 0;
		correct[10] = 11;
		checkTiles(testSequence[5],correct);
		correct[11] = 12;
		correct[15] = 0;
		checkTiles(testSequence[6],correct);
	}
	/**
	 * Helper method to check the state of a puzzle state.
	 * The given state has tiles arranged in a 2d fashion,
	 * the correct values are given in a 1d array as if
	 * rows were concatenated, i.e., it lists values by rows.
	 * The length of the correct array should match the dimxdim of the state.
	 * @param state the state to analyze
	 * @param correct the array with correct tile settings
	 */
	private void checkTiles(PuzzleState state, int[] correct) {
		int i = 0; // index of array of correct values
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				assertEquals("failed for entry: row " + r + ", col " + c + " index " + i , state.getValue(r, c), correct[i]);
				i++;
			}
		}
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#drag(int, int, int, int)}.
	 * The drag method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 1 empty slot which is trivial and may serve as a simple smoke test.
	 * For this case drag can only do a single move and must match a move operation.
	 * We use the test scenario and check 5 states with their successor states.
	 */
	@Test
	public void testDrag() {
		// use test sequence and drag from one state to its successor state.
		PuzzleState ps;
		int startRow, startColumn, endRow, endColumn;
		int[] slot;
		for (int i = 0; i < 5; i++) {
			// current state is testSequence[i]
			// prepare position: 
			// start is non-empty tile, which is empty slot in successor state
			slot = findEmptySlot(testSequence[i+1]);
			assertNotNull("failed for state at position " + i, slot);
			startRow = slot[0];
			startColumn = slot[1];
			// end is empty slot in current state
			slot = findEmptySlot(testSequence[i]);
			assertNotNull("failed for state at position " + i, slot);
			endRow = slot[0];
			endColumn = slot[1];
			// get result from dragging 
			ps = testSequence[i].drag(startRow, startColumn, endRow, endColumn);
			// compare with successor state
			assertEquals("failed for state at position " + i, testSequence[i+1], ps);
		}
	}

	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#drag(int, int, int, int)}.
	 * The drag method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested drag is indeed possible.
	 * The series of intermediate states is unique.
	 */
	@Test
	public void testDragForMultipleEmptySlots() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10 to last position, keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		PuzzleState ps3 = ps2.move(3, 1, Operation.MOVERIGHT);
		PuzzleState ps4 = ps3.move(3, 2, Operation.MOVERIGHT);
		// perform drag to move 10 to bottom-right most position
		// drag is equivalent to a sequence of 3 move operations
		PuzzleState test = ps1.drag(2, 1, 3, 3);
		// sequence and drag should match
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		// check if drag produces correct parent relationship with intermediate states
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps3 + " with " + parent, ps3.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps2 + " with " + parent, ps2.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps1 + " with " + parent,ps1.equals(parent));
		
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#drag(int, int, int, int)}.
	 * The drag method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested drag is indeed possible.
	 * The series of intermediate states is not unique, there are 2 possible ways to proceed.
	 */
	@Test
	public void testDragForMultipleEmptySlotsNotUnique() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10 down, shift 11, 12 to the left
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		assertNotNull(ps2);
		PuzzleState ps3 = ps2.move(2, 2, Operation.MOVELEFT);
		assertNotNull(ps3);
		PuzzleState ps4 = ps3.move(2, 3, Operation.MOVELEFT);
		assertNotNull(ps4);
		// 1 2 3 4
		// 5 6 7 8
		// 9 11 12 0
		// 13 10 0 0
		// one can drag 12 to the bottom-right position in 2 ways
		// Variant 1: right, down
		// Variant 2: down, right
		// perform drag to move 12 to bottom-right most position
		PuzzleState test = ps4.drag(2, 2, 3, 3);
		assertNotNull(test);
		// sequence and drag should match
		// V1
		PuzzleState ps5V1 = ps4.move(2, 2, Operation.MOVERIGHT);
		assertNotNull(ps5V1);
		PuzzleState ps6V1 = ps5V1.move(2, 3, Operation.MOVEDOWN);
		assertNotNull(ps6V1);
		// final states agree
		assertTrue("Comparing state " + ps6V1 + " with " + test, ps6V1.equals(test));
		// V2
		PuzzleState ps5V2 = ps4.move(2, 2, Operation.MOVEDOWN);
		assertNotNull(ps5V2);
		PuzzleState ps6V2 = ps5V2.move(3, 2, Operation.MOVERIGHT);
		assertNotNull(ps6V2);
		// intermediate states differ
		assertFalse("Comparing state " + ps5V1 + " with " + ps5V1, ps5V1.equals(ps5V2));
		// final states agree
		assertTrue("Comparing state " + ps6V2 + " with " + test, ps6V2.equals(test));
		// check if drag produced correct parent relationship with intermediate states
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps5V1 + " with " + parent, ps5V1.equals(parent) || ps5V2.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps4 + " with " + parent, ps4.equals(parent));
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#drag(int, int, int, int)}.
	 * The drag method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested drag is indeed possible.
	 * There are 3 possible options, one is the solution.
	 */
	@Test
	public void testDragForMultipleEmptySlotsCross() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10, 12 down 
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		assertNotNull(ps2);
		PuzzleState ps3 = ps2.move(2, 3, Operation.MOVEDOWN);
		assertNotNull(ps3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 0 11 0
		// 13 10 0 12
		// one can drag 11 to 3 possible positions but all are neighbors
		// Variant 1: drag 11 to the right
		// Variant 2: drag 11 to the left
		// Variant 3: drag 11 downwards
		PuzzleState test;
		PuzzleState ps4;
		// test Variant 1:
		test = ps3.drag(2, 2, 2, 3);
		assertNotNull(test);
		// equivalent to move right operation
		ps4 = ps3.move(2, 2, Operation.MOVERIGHT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		assertTrue("Comparing state " + ps3 + " with " + test.getParent(), ps3.equals(test.getParent()));
		// test Variant 2:
		test = ps3.drag(2, 2, 2, 1);
		assertNotNull(test);
		// equivalent to move right operation
		ps4 = ps3.move(2, 2, Operation.MOVELEFT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		assertTrue("Comparing state " + ps3 + " with " + test.getParent(), ps3.equals(test.getParent()));
		// test Variant 1:
		test = ps3.drag(2, 2, 3, 2);
		assertNotNull(test);
		// equivalent to move right operation
		ps4 = ps3.move(2, 2, Operation.MOVEDOWN);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		assertTrue("Comparing state " + ps3 + " with " + test.getParent(), ps3.equals(test.getParent()));
		
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#drag(int, int, int, int)}.
	 * The drag method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested drag is indeed possible.
	 * The series of intermediate states is not unique, there are 2 options to proceed, only 1 works.
	 */
	@Test
	public void testDragForMultipleEmptySlotsDeadEnd() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 10 down 
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 1, Operation.MOVEDOWN);
		assertNotNull(ps2);
		// 1 2 3 4
		// 5 6 7 8
		// 9 0 11 12
		// 13 10 0 0
		// one can drag 11 to the bottom-right position
		// the option to move left is a dead end
		// 
		PuzzleState test = ps2.drag(2, 2, 3, 3);
		assertNotNull(test);
		// equivalent to move down and right operation
		PuzzleState ps3 = ps2.move(2, 2, Operation.MOVEDOWN);
		assertNotNull(ps3);
		PuzzleState ps4 = ps3.move(3, 2, Operation.MOVERIGHT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps3 + " with " + parent, ps3.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps2 + " with " + parent, ps2.equals(parent));
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#drag(int, int, int, int)}.
	 * The drag method is used when an tile is dragged across multiple empty slots.
	 * We test the special case of 3 empty slots where a requested drag is indeed possible.
	 * The series of intermediate states is not unique, there are 2 options to proceed, only 1 works.
	 */
	@Test
	public void testDragForMultipleEmptySlotsDeadEnd2() {
		// set up state with 3 empty slots
		PuzzleState ps1 = new SimplePuzzleState();
		ps1.setToInitialState(4, 3);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 12
		// 13 0 0 0
		// move 12 down 
		// keep intermediate states
		PuzzleState ps2 = ps1.move(2, 3, Operation.MOVEDOWN);
		assertNotNull(ps2);
		// 1 2 3 4
		// 5 6 7 8
		// 9 10 11 0
		// 13 0 0 12
		// one can drag 12 to the bottom-left position
		// the option to move up is a dead end
		// 
		PuzzleState test = ps2.drag(3, 3, 3, 1);
		assertNotNull(test);
		// equivalent to move down and right operation
		PuzzleState ps3 = ps2.move(3, 3, Operation.MOVELEFT);
		assertNotNull(ps3);
		PuzzleState ps4 = ps3.move(3, 2, Operation.MOVELEFT);
		assertNotNull(ps4);
		assertTrue("Comparing state " + ps4 + " with " + test, ps4.equals(test));
		PuzzleState parent = test.getParent();
		assertTrue("Comparing state " + ps3 + " with " + parent, ps3.equals(parent));
		parent = parent.getParent();
		assertTrue("Comparing state " + ps2 + " with " + parent, ps2.equals(parent));
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#isEmpty(int, int)}.
	 * We performed a series of move operations for the test sequence.
	 * We just need to check if the empty slot is on the correct position.
	 */
	@Test
	public void testIsEmptyIntInt() {
		for (int i = 0; i < 7; i++) {
			checkForEmptySlots(testSequence[i]);
		}
	}
	/**
	 * Helper method to check the state of a puzzle state by
	 * checking if getValue() and isEmpty() give consistent answers.
	 * Uses hard coded dimensions for a 4x4 board.
	 * @param state the state to analyze
	 */
	private void checkForEmptySlots(PuzzleState state) {
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				if (0 == state.getValue(r, c)) {
					assertTrue("failed for entry: row " + r + ", col " + c, state.isEmpty(r, c));
				}
				else {
					assertFalse("failed for entry: row " + r + ", col " + c, state.isEmpty(r, c));
				}
			}
		}
	}
	/**
	 * Test method for {@link edu.wm.cs.cs301.slidingpuzzle.PuzzleState#getStateWithShortestPath()}.
	 * The calculated new state must have the same setting of tiles and the current path length is a 
	 * natural upper bound on the path length for the calculated state.
	 * If the checkShortestPathCalculation flag is set, we test if method recognizes shorter path
	 * on the test sequence.
	 */
	@Test
	public void testGetStateWithShortestPath() {
		// we check the sequence of states generated by the setUp method
		PuzzleState state;
		
		// 4x4 board with 1 empty slot on last position (3,3)
		// initial state
		state = testSequence[0].getStateWithShortestPath();
		assertEquals(state,testSequence[0]); // equal arrangement of tiles
		assertTrue(state.getPathLength() <= testSequence[0].getPathLength()); // upper bound
		assertEquals(0,state.getPathLength()); // check this special case, distance is 0
		
		// move down (2,3)->(3,3), empty (2,3)
		state = testSequence[1].getStateWithShortestPath();
		assertEquals(state,testSequence[1]); // equal arrangement of tiles
		assertTrue(state.getPathLength() <= testSequence[1].getPathLength()); // upper bound
		assertEquals(1,state.getPathLength()); // check this special case, distance is 1	
		
		// move right (2,2)->(2,3), empty (2,2)
		state = testSequence[2].getStateWithShortestPath();
		assertEquals(state,testSequence[2]); // equal arrangement of tiles
		assertTrue(state.getPathLength() <= testSequence[2].getPathLength()); // upper bound
		assertEquals(2,state.getPathLength()); // check this special case, distance is 2	
		
		// move right (2,1)->(2,2), empty (2,1)
		state = testSequence[3].getStateWithShortestPath();
		assertEquals(state,testSequence[3]); // equal arrangement of tiles
		assertTrue(state.getPathLength() <= testSequence[3].getPathLength()); // upper bound
		assertEquals(3,state.getPathLength()); // check this special case, distance is 3	
		
		// let's reverse this little path back to the starting state
		// move left (2,2)->(2,1), empty (2,2)
		state = testSequence[4].getStateWithShortestPath();
		assertEquals(state,testSequence[4]); // equal arrangement of tiles
		assertTrue(state.getPathLength() <= testSequence[4].getPathLength()); // upper bound
		if (checkShortestPathCalculation)
			assertEquals(2,state.getPathLength()); // check this special case, distance is 2	
		
		// move left (2,3)->(2,2), empty (2,3)
		state = testSequence[5].getStateWithShortestPath();
		assertEquals(state,testSequence[5]); // equal arrangement of tiles
		assertTrue(state.getPathLength() <= testSequence[5].getPathLength()); // upper bound
		if (checkShortestPathCalculation)
			assertEquals(1,state.getPathLength()); // check this special case, distance is 1	

		// move up (3,3)->(2,2), empty (3,3)
		state = testSequence[6].getStateWithShortestPath();
		assertEquals(state,testSequence[6]); // equal arrangement of tiles
		assertTrue(state.getPathLength() <= testSequence[6].getPathLength()); // upper bound
		if (checkShortestPathCalculation)
			assertEquals(0,state.getPathLength()); // check this special case, distance is 1	

	}
}
