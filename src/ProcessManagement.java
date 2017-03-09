import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ProcessManagement {

    //set the working directory
    private static File currentDirectory = new File(System.getProperty("user.dir"));
    //set the instructions file
    private static File instructionSet = new File("graph-file.txt");
    public static Object lock=new Object();

    public static void main(String[] args) throws InterruptedException {

        //parse the instruction file and construct a data structure, stored inside ProcessGraph class
        ParseFile.generateGraph(new File(currentDirectory +File.separator+instructionSet));

        // Print the graph information
        ProcessGraph.printGraph();
	
        while(ProcessGraph.nodes.size()>0){																	// While there are nodes on the tree
        	Iterator<ProcessGraphNode> iter = ProcessGraph.nodes.iterator();								
        	while(iter.hasNext()){																			// Keep looping through all the nodes on the tree 
        		ProcessGraphNode indvNode = iter.next();													// and check
        		if(!indvNode.isExecuted()){																	// If Node has not been executed
            		if(indvNode.isRunnable()){																// and is runnable
            			execute(indvNode.getCommand(),indvNode.getInputFile(),indvNode.getOutputFile());	// Execute the given command using the input and output file given
            			iter.remove();																		// After executing the command, the current node has been executed, and hence removed from the tree
            		}
            	}else{
            		// Removes completed processes from the Process Graph
            		// This helps to reduce the number of objects iterated through
            		// the graph, and is used to reduce the size of the tree as the processes 
            		// are executed, allowing program to terminate once all processes has been 
            		// executed.
            		iter.remove();
            	}
        	}        	
        }

        System.out.println("All process finished successfully");
    }
    
    /*
     *  This method executes the given linux command given to it by forking a new
     *  process for it via the ProcessBuilder class in Java.
     *  
     *  @param command - The Linux Command to be executed, along with its arguments. 
     *  				It should be passed as a space delimited string to this method, 
     *  				where the first section of the string should represent the command
     *  				to be executed, and the other space delimited sections will be comprised
     *  				of arguments to the given command.
     *  
     *   				e.g. "sleep 10" , "wc inputfile1 inputfile2"
     *   
     * @param inputFile - This represents the specified input file to the given linux command.
     * 					If there is no input file specified, an input file with name "stdin" is
     * 					still required, and the method will handle the standard input file accordingly.
     * 					If there is a specified input file, the method will utilise the given input file.
     * 
     * @param outputfile - This represents the specified output file to the given linux command.
     * 					If there is no output file specified, an output file with name "stdout" is
     * 					still required, and the method will handle the standard output file accordingly.
     * 					If there is a specified output file, the method will utilise the given output file.
     */

	private static void execute(String command, File inputFile, File outputFile) {
		String[] commandList = command.split(" ");					// Split up the command string into it's constituent parts in an array
		ProcessBuilder pBuilder = new ProcessBuilder(commandList);	// and pass this array of command and arguments into a ProcessBuilder, which is used to fork new processes in the Operating System,
		pBuilder.directory(currentDirectory);						// in the current working directory of this program.
		if(!inputFile.getName().equals("stdin")){					// If the input file is specified 
			pBuilder.redirectInput(inputFile);						// set the input of the new process to the specified input file, 
		}															// else, input file will be the standard input file.
		if(!outputFile.getName().equals("stdout")){					// Similarly, if the output file is specified,
			pBuilder.redirectOutput(outputFile);					// set the output of the new process to the specified output
		}															// else, output file will be the standard output file.
		
		try {
			Process p = pBuilder.start();							// Once the directory, input and output are set, try to start the process with the given command and argument
		} catch (IOException e) {									// and print out any error messages should an input/output error be detected.
			e.printStackTrace();
		}
	}

}