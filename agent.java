import java.io.*;
import java.util.*;

public class agent{
	List<Node> nodes = new ArrayList<Node>();		
	List<List<Integer>> adjList = new ArrayList<List<Integer>>();
	String sourceNode;
	String destinationNode;
	int sourceIndex, destinationIndex;
	List<Node> visitedNodes = new ArrayList<Node>();
	HashMap<Integer, Integer> added = new HashMap<Integer, Integer>();
	Queue<Integer> dfsVisited = new LinkedList<Integer>();
		
	public void BreadthFirst(){
		int elements = destinationIndex-sourceIndex+1;
		PriorityQueue<Node> queue = new PriorityQueue<Node>(elements,new SortBDQueueCostName());
		Node node = nodes.get(sourceIndex);
		
		queue.add(node);
		added.put(sourceIndex, 0);
		while(queue.size() != 0){
			Node wNode = queue.remove();
			visitedNodes.add(wNode);
			if(wNode.id == destinationIndex){
				queue.clear();
				break;
			}
			for(Node neighbor: wNode.neighbors){
				if(!added.containsKey(neighbor.id)){
					Node tNode = nodes.get(neighbor.id);
					tNode.pathCost = neighbor.pathCost+wNode.pathCost;
					tNode.depthPathCost = wNode.depthPathCost+1;
					tNode.parent = wNode.id;
					queue.add(tNode);
					added.put(neighbor.id, tNode.pathCost);
				}
			}
			
		}
		DisplayResult();
	}
	
	public void DepthFirst(){
		Node node = nodes.get(sourceIndex);
		added.put(sourceIndex, 0);
		dfsVisited.add(sourceIndex);
		DepthFirstRec(node);
		
		DisplayResult();
		
	}
	
	public void DepthFirstRec(Node node){
		if(!dfsVisited.isEmpty()){
			visitedNodes.add(node);
			node.children = new LinkedList<Node>();
			if(node.neighbors.isEmpty()||node.id==destinationIndex){
				dfsVisited.clear();
				return;
			}
			for(Node neighbor: node.neighbors){
				if(!added.containsKey(neighbor.id)){
					dfsVisited.add(neighbor.id);
					dfsVisited.remove();
					Node wNode = nodes.get(neighbor.id);
					wNode.pathCost = neighbor.pathCost+node.pathCost;
					wNode.parent = node.id;	
					added.put(neighbor.id, wNode.pathCost);
					node.children.add(wNode);			
				}
			}
			
			while(!node.children.isEmpty())
			{
				Node child = node.children.remove();
				DepthFirstRec(child);
			}
		}
	}
		
	public void UniformFirst(){
		int elements = destinationIndex-sourceIndex+1;
		PriorityQueue<Node> queue = new PriorityQueue<Node>(elements,new SortQueueCostName());
		Node node = nodes.get(sourceIndex);
		
		queue.add(node);//open
		added.put(sourceIndex, 0);
		while(!queue.isEmpty()){
			Node wNode = queue.remove();
			visitedNodes.add(wNode);
			if(wNode.id==destinationIndex)
				break;
			for(Node neighbor: wNode.neighbors){
				if(!added.containsKey(neighbor.id)|| 
					  (added.containsKey(neighbor.id) && 
							  added.get(neighbor.id)>(neighbor.pathCost+wNode.pathCost))){
					if(added.containsKey(neighbor.id)){
						added.remove(neighbor.id);
						queue.remove(nodes.get(neighbor.id));
					}
					
					Node tNode = nodes.get(neighbor.id);
					tNode.pathCost = neighbor.pathCost+wNode.pathCost;
					tNode.parent = wNode.id;
					added.put(neighbor.id, tNode.pathCost);					
					queue.add(tNode);
				}
			}
		}
		DisplayResult();
	}
	
	public static void main(String[] args){
		List<String> inDocument = new ArrayList<String>();
		int searchType;
		int nPeople;
		List<String> people = new ArrayList<String>();		
		
		agent aObj = new agent();
		try {
            Scanner sc = new Scanner(new File("input.txt"));
            while(sc.hasNextLine()) 
            {
               String next = sc.nextLine();
               inDocument.add(next);
            }
            sc.close();
        } 
        catch (FileNotFoundException ex) 
        {
        	System.out.println("make sure the name of the input file is input.txt and it is placed in the same directory as the .java and makefile files are");
        	return;
        }
		
		searchType = Integer.parseInt((inDocument.get(0)));
		aObj.sourceNode = inDocument.get(1);
		aObj.destinationNode = inDocument.get(2);
		nPeople = Integer.parseInt(inDocument.get(3));		
		for(int i = 4; i < (4+nPeople); i++){
			people.add(inDocument.get(i));
		}
		
		aObj.sourceIndex = people.indexOf(aObj.sourceNode);
		aObj.destinationIndex = people.indexOf(aObj.destinationNode);

		for(int i = 4+nPeople; i< 4+2*nPeople; i++){
			
			List<Integer> rowValues = BuildAdjList(inDocument.get(i));
			aObj.adjList.add(rowValues);
		}
		
		for(int i=0; i<people.size();i++){
			Node node = new Node();
			node.id=i;
			node.name = people.get(i);
			node.parent = 99999;
			node.pathCost = 0;
			node.depthPathCost = 0;
			if(searchType==3)
				node.neighbors = BuildNeighbors(aObj.adjList.get(i), people);
			else
				node.neighbors = BuildNeighborsBD(aObj.adjList.get(i), people);
			aObj.nodes.add(node);			
		}
		switch(searchType){
		case 1:
			aObj.BreadthFirst();
			break;
		case 2:
			aObj.DepthFirst();
			break;
		case 3:
			aObj.UniformFirst();
			break;
		default:
			System.out.println("kindly verify the first entry in the input file, it should be 1 or 2 or 3.");
			break;
		}
	}
	
	public static List<Integer> BuildAdjList(String str){
		List<Integer> rowValues = new ArrayList<Integer>();
		String[] splitValues = str.split(" ");
		for(int i=0; i < splitValues.length; i++){
			rowValues.add(Integer.parseInt(splitValues[i]));
		}
		return rowValues;
	}
	
	public static List<Node> BuildNeighborsBD(List<Integer> neighborList, List<String> people){
		List<Node> neighbors = new ArrayList<Node>();
		for(int i=0; i< neighborList.size();i++){
			if(neighborList.get(i)!=0){
				Node node = new Node();
				node.id = i;
				node.name = people.get(i);
				node.pathCost = neighborList.get(i);
				neighbors.add(node);
			}			
		}

		List<Node> neighbors1 = SortNeighborsName(neighbors);
		return neighbors1;
	}
	
	public static List<Node> BuildNeighbors(List<Integer> neighborList, List<String> people){
		List<Node> neighbors = new ArrayList<Node>();
		for(int i=0; i< neighborList.size();i++){
			if(neighborList.get(i)!=0){
				Node node = new Node();
				node.id = i;
				node.name = people.get(i);
				node.pathCost = neighborList.get(i);
				neighbors.add(node);
			}			
		}
		List<Node>neighbors1=SortNeighborsCostName(neighbors);
		return neighbors1;
	}
	
	public static List<Node> SortNeighborsCostName(List<Node> neighbors){
		Collections.sort(neighbors, new SortCostName());
		return neighbors;
	}
	
	public static List<Node> SortNeighborsName(List<Node> neighbors){
		Collections.sort(neighbors, new SortName());
		return neighbors;
	}
	
	public void DisplayResult(){
		try{
			File file = new File("output.txt");
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			ArrayList<Node> finalList = new ArrayList<Node>();
			Node n = visitedNodes.get(visitedNodes.size()-1);
			finalList.add(n);
			
			if(finalList.get(0).id == destinationIndex){
				
				for(int i=0; i< visitedNodes.size();i++){
					System.out.print(visitedNodes.get(i).name);
					bufferedWriter.write(visitedNodes.get(i).name);
					if(i<visitedNodes.size()-1){
						System.out.print("-");
						bufferedWriter.write("-");
					}
				}
				System.out.println();
				bufferedWriter.newLine();
				
				int pathCost = finalList.get(0).pathCost;
				int parent = finalList.get(0).parent;
				while(parent!=99999){
					for(Node visitedNode: visitedNodes){
						if(visitedNode.id==parent){
							parent = visitedNode.parent;
							finalList.add(visitedNode);					
						}
					}
					
				}
				Collections.reverse(finalList);
				
				for(int i=0; i< finalList.size();i++){
					System.out.print(finalList.get(i).name);
					bufferedWriter.write(finalList.get(i).name);
					if(i<finalList.size()-1){
						System.out.print("-");
						bufferedWriter.write("-");
					}
				}
				System.out.println();
				bufferedWriter.newLine();
				System.out.println(pathCost);
				bufferedWriter.write(String.valueOf(pathCost));
			}
			else{
				System.out.println("NoPathAvailable");
				bufferedWriter.write("NoPathAvailable");
			}
			bufferedWriter.close();
		}catch(IOException e){
			System.out.println("this is embarrasing");
		}
	}
}

class SortCostName implements Comparator<Node>{
	public int compare(Node obj1, Node obj2){
		if(obj1.pathCost<obj2.pathCost)
			return -1;
		else if(obj1.pathCost>obj2.pathCost)
			return 1;
		else{
			return obj1.name.compareToIgnoreCase(obj2.name);
		}
	}
}

class SortName implements Comparator<Node>{
	public int compare(Node obj1, Node obj2){
		return obj1.name.compareToIgnoreCase(obj2.name);
	}
}

class SortQueueCostName implements Comparator<Node>{
	public int compare(Node obj1, Node obj2){
		if(obj1.pathCost<obj2.pathCost)
			return -1;
		else if(obj1.pathCost>obj2.pathCost)
			return 1;
		else{
			return obj1.name.compareToIgnoreCase(obj2.name);
		}
	}
}

class SortBDQueueCostName implements Comparator<Node>{
	public int compare(Node obj1, Node obj2){
		if(obj1.depthPathCost<obj2.depthPathCost)
			return -1;
		else if(obj1.depthPathCost>obj2.depthPathCost)
			return 1;
		else{
			return obj1.name.compareToIgnoreCase(obj2.name);
		}
	}
}

class Node{
	Integer parent;
	Integer id;
	String name;
	List<Node> neighbors;
	Integer pathCost;
	Queue<Node> children;
	Integer depthPathCost;
}