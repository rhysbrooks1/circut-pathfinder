import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail
 */
public class CircuitTracer {

	/** launch the program
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 *  @author RhysBrooks
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			System.out.println("Did not get 3 arguments");
			System.exit(1);
		}
		try {
			new CircuitTracer(args); //create this with args
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** Print instructions for running CircuitTracer from the command line. */
	// prints usage instructions if an error is found in command arguments
	private static void printUsage() {
		System.out.println("Usage: $java CircuitTracer [-s|-q] [-c|-g] [filename]"); 
	}
	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	CircuitTracer(String[] args) {

		CircuitBoard board = null;
		Storage<TraceState> stateStore = null;
		ArrayList<TraceState> bestPaths = new ArrayList<TraceState>();
		
		
		if (args.length != 3) {
			printUsage();
			return; //exit the constructor immediately
		}
		
		// first arg stack or queue
		switch(args[0]) { 
		case "-s":
			stateStore = Storage.getStackInstance();
			break;
		case "-q":
			stateStore = Storage.getQueueInstance();
			break;
		default:
			printUsage();
			System.exit(1);
		}
		// second arg console or gui
		switch(args[1]) { 
		case "-c":
			break;
			// I did not implement this
		case "-g":
			System.out.println("GUI IS NOT SUPPORTED");
			throw new UnsupportedOperationException(); 
		default:
			printUsage();
			System.exit(1);
		}
		
		try {
		    board = new CircuitBoard(args[2]);
		} catch (NullPointerException e) {
		    System.out.println("NullPointerException");
		    e.printStackTrace();
		    System.exit(1);
		} catch (InvalidFileFormatException e) {
		    System.out.println("InvalidFileFormatException");
		    System.exit(1);
		} catch (OccupiedPositionException e) {
		    System.out.println("OccupiedPositionException");
		    e.printStackTrace();
		    System.exit(1);
		} catch (Exception e) {
		    System.out.println("Unexpected Exception");
		    e.printStackTrace();
		    System.exit(1);
		}
		
		
		// search algorithm
		Point startingPoint = board.getStartingPoint();

		// 2d array holding the cardinal directions that the tracer can move in
		int[][] offsets = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };

		for (int[] offset : offsets) {
		    int newRow = startingPoint.x + offset[0];
		    int newCol = startingPoint.y + offset[1];

		    if (board.isOpen(newRow, newCol)) {
		        stateStore.store(new TraceState(board, newRow, newCol));
		    }
		}
		
		TraceState currentPath;
		
		while(!stateStore.isEmpty()) {
			//pop or dequeue into currentPath variable
			currentPath = stateStore.retrieve();
			
			// if not next to endpoint
			if(currentPath.isComplete()) { 
				// Check to see if bestPaths is empty 
				if(bestPaths.size()!=0) { 
					// if equal to current bestPath continue to add onto same arrayList 
					if(currentPath.pathLength()==bestPaths.get(0).pathLength()) { 
						bestPaths.add(currentPath);
						// if a better path is found a new arraylist is made to store it
					} else if(currentPath.pathLength()<=bestPaths.get(0).pathLength()) { 
						bestPaths = new ArrayList<TraceState>();
						bestPaths.add(currentPath);
					}
					}
				// If empty add first working path found (currentPath)
				else { 
					bestPaths.add(currentPath);
				}
				
				// If path has not been completed by this point continue searching
			} else {
				
			    for (int[] offset : offsets) {
			        int newRow = currentPath.getRow() + offset[0];
			        int newCol = currentPath.getCol() + offset[1];

			        if (currentPath.isOpen(newRow, newCol)) {
			            stateStore.store(new TraceState(currentPath, newRow, newCol));
			        }
				}
			}
		}
		// print solutions
		for(TraceState stuff : bestPaths) { 
			System.out.print(stuff.toString());
			System.out.println();
		}
		

	}
	
} // class CircuitTracer