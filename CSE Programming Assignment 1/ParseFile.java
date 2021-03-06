import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ParseFile {
    //this method generates a ProcessGraph and store in ProcessGraph Class
    public static void generateGraph(File inputFile) {
        try{
            Scanner fileIn=new Scanner(inputFile);
            ArrayList<String> listOfChildrenId = new ArrayList<String>();
            int index=0;
            while(fileIn.hasNext()){
                String line=fileIn.nextLine();
                String[] quatiles= line.split(":"); // name arg: children : input : output
                if (quatiles.length!=4) {
                    System.out.println("Wrong input format!");
                    throw new Exception();
                }
                for(int i = 0; i<quatiles.length;i++){
                	quatiles[i] = quatiles[i].trim();
                }

                //add this node
                ProcessGraph.addNode(index);
                //handle Children
                listOfChildrenId.add(quatiles[1]);	// Stores all the children of current node in arraylist, where index of storage represents nodeID
                
                //setup command
                ProcessGraph.nodes.get(index).setCommand(quatiles[0]);
                //setup input
                ProcessGraph.nodes.get(index).setInputFile(new File(quatiles[2]));
                //setup output
                ProcessGraph.nodes.get(index).setOutputFile(new File(quatiles[3]));
                
                index++;
            }
            int childId = 0;
            for(int j = 0;j<index;j++){																							// Iterate through all the nodes
            	if (!listOfChildrenId.get(j).equals("none")){ 																	// If there are children nodes for this particular node
                    String[] childrenStringArray=listOfChildrenId.get(j).split(" ");											// split up the given children IDs into IDs of each Child Node from 1 string to an Array
                    for (int i = 0; i < childrenStringArray.length; i++) { 														// Iterates through the children IDs
                    	childId = Integer.parseInt(childrenStringArray[i]);
                    	if(childId>=0 && childId<index){
                    		ProcessGraph.nodes.get(j).addChild(ProcessGraph.nodes.get(childId));								// and update all the nodes in the tree with each of their respective children if they are valid
                    	}else{
                    		System.out.println("Process "+j+" : There is no such child "+childId+" in the graph! Continuing...");
                    	}
                    }
                }
            }
            
          //setup parent
            for (ProcessGraphNode node : ProcessGraph.nodes) {
                for (ProcessGraphNode childNode : node.getChildren()) {
                    ProcessGraph.nodes.get(childNode.getNodeId()).addParent(ProcessGraph.nodes.get(node.getNodeId()));
                }
            }
            
          //mark initial runnable
            for (ProcessGraphNode node:ProcessGraph.nodes) {
                if (node.getParents().isEmpty()){
                    node.setRunnable();
                }
            }
            
            fileIn.close();
        } catch (Exception e){
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }


}