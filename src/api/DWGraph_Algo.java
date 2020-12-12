package api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;



import java.util.Stack;

import api.DWGraph_DS.NodeData;


public class DWGraph_Algo implements dw_graph_algorithms 
{
	directed_weighted_graph G;
	private final double Inf=Double.POSITIVE_INFINITY;
	private int NumOfBlack=0;
	
	public DWGraph_Algo()
	{
		G=new DWGraph_DS();
	}
	@Override
	public void init(directed_weighted_graph g) 
	{
		G=g;
	}

	@Override
	public directed_weighted_graph getGraph()
	{
		if(G!=null)return G;
		else 
		{
			System.err.println("Null Graph");
			return null;
		}
	}
	public void SetInit()// initialization of the graph 
	{
		NumOfBlack=0;
		Iterator<node_data> ite=G.getV().iterator(); 
	      while(ite.hasNext())
	      {
	    	  NodeData N=(NodeData)ite.next();
	    	  N.setWeight(Inf);
	    	  N.setTag(-1);
	    	  N.setInfo("White");	
	      }
	}
	public void Dijkstra(NodeData S)// Dijkstra Algo with start node 
	{
		PriorityQueue<NodeData> Q = new PriorityQueue<NodeData>();
		S.setInfo("Black");
		S.setWeight(0);
		Q.add(S);
		while(!Q.isEmpty())
		{
			NodeData poll=Q.poll();
			poll.setInfo("Black");
			Iterator<edge_data> ite=G.getE(poll.getKey()).iterator();
			while(ite.hasNext() )
			{
				edge_data EdgeTmp=ite.next();
				//System.out.println(EdgeTmp);
				NodeData Tmp=(NodeData)G.getNode(EdgeTmp.getDest());
				if(Tmp.getWeight()>poll.getWeight()+G.getEdge(poll.getKey(),Tmp.getKey()).getWeight())
				{
					Tmp.setWeight(poll.getWeight()+G.getEdge(poll.getKey(),Tmp.getKey()).getWeight());
					Tmp.setTag(poll.getKey());

				}
				if(Tmp.getInfo().equals("White"))
				{
					Tmp.setInfo("Grey");
					Q.add(Tmp);
				}	
			}
		}
	}
	@Override
	public directed_weighted_graph copy()
	{
		return new DWGraph_DS((DWGraph_DS)G);
	}

	@Override
	public boolean isConnected() 
	{
		if(G.nodeSize()==1 || G.nodeSize()==0) return true;
		else 
		{
			SetInit();
			Iterator<node_data> ite=G.getV().iterator(); 
		    if(ite.hasNext())
		    {
		    	node_data N=ite.next();
		    	DFS(N);
		    }
		     if(NumOfBlack==G.nodeSize())return true;
		     else return false;
		}
	}
	public void DFS(node_data N)
	{
		Stack<node_data> S=new Stack<>();
		N.setInfo("Black");
		NumOfBlack++;
		S.add(N);
		while(!S.isEmpty())
		{
			node_data pop=S.pop();
			Iterator<edge_data> ite=G.getE(pop.getKey()).iterator();
			while(ite.hasNext())
			{
				edge_data EdgeTmp=ite.next();
				node_data Tmp=G.getNode(EdgeTmp.getDest());
				if(Tmp.getInfo().equals("White"))
				{
					Tmp.setInfo("Grey");
					S.push(Tmp);
					NumOfBlack++;
				}
			}
		}
		
	}

	@Override
	public double shortestPathDist(int src, int dest) 
	{
			NodeData S=(NodeData)G.getNode(src);
			NodeData E=(NodeData)G.getNode(dest);
			if(S==null || E==null) return -1;
			else if(src==dest)return 0;
			else
			{
				SetInit();
				Dijkstra(S);
				return E.getWeight();
			}
	}

	@Override
	public List<node_data> shortestPath(int src, int dest) 
	{
		node_data S=G.getNode(src);
		node_data E=G.getNode(dest);
		double L=shortestPathDist(src,dest);
		List<node_data> arr = new ArrayList<node_data>();
		if(L==-1|| S==null||E==null||L==Inf)return null;
		else 
		{
			arr.add(E);
			if(src==dest)return arr;
			else
			{
				while(E.getTag()!=-1)
				{
					E=G.getNode(E.getTag());
					arr.add(E);
				}
				return CounterList(arr);
			}
		}
	}
	public List<node_data> CounterList(List<node_data> L)// Crate a new List that is in reverse direction  of the giving list
	{
		List<node_data> L1=new ArrayList<node_data>();
		for (int i = L.size()-1; i >= 0; i--) 
		{
			L1.add(L.get(i));
		}
		return L1;
	}


	@Override
	public boolean save(String file) 
	{
		try {
			FileWriter file_writer = new FileWriter(file);
			PrintWriter output = new PrintWriter(file_writer);
			output.println(((DWGraph_DS)this.G).toJson());
			output.close();
			file_writer.close();
		}
		catch (Exception e) {
			System.err.println("The file failed to save!");
			return false;
		}
		return true;
	}

	@Override
	public boolean load(String file)
	{
		try {
			FileReader file_reader = new FileReader(file);
			BufferedReader input = new BufferedReader(file_reader);
			String s = input.readLine();
			DWGraph_DS g = new DWGraph_DS(s);
			this.G = g;
			input.close();
			file_reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("The file failed to load!");
			return false;
		}
		return true;
	}
}
