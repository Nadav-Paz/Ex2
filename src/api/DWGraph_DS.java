package api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


public class DWGraph_DS implements directed_weighted_graph {

	public static int counter = 0;

	private HashMap <Integer, node_data> verticies;
	private HashMap <Long, EdgeData> edges;
//	private int id;
	private int mode_counter;

	public class NodeData implements node_data, Comparable<NodeData>{

		private int key;
		private int node_tag;
		private geo_location geo;
		private double node_weight;
		private String node_info;
		// A set of all the keys for all the edges this node points at
		private HashSet<Long> out_set;
		// A set of all the keys for all the edges that point at this node
		private HashSet<Long> in_set;

		public NodeData(int key, geo_location geo, int tag, double weight, String info){
			this.key = key;
			this.geo = new Geo(geo);
			this.node_tag = tag;
			this.node_weight = weight;
			this.node_info = new String(info);
			this.out_set = new HashSet<>();
			this.in_set = new HashSet<>();

//			id++;
		}

		public NodeData(node_data node) {
			this(node.getKey(), node.getLocation(), node.getTag(), node.getWeight(), node.getInfo());
		}

		public NodeData(int key){
			this(key, new Geo(), 0, 0, "");
		}

		@Override
		public int getKey() {
			return this.key;
		}

		@Override
		public geo_location getLocation() {
			return geo;
		}

		@Override
		public void setLocation(geo_location p) {
			this.geo = new Geo(p);
		}

		@Override
		public double getWeight() {
			return node_weight;
		}

		@Override
		public void setWeight(double w) {
			this.node_weight = w;
		}

		@Override
		public String getInfo() {
			return this.node_info;
		}

		@Override
		public void setInfo(String s) {
			this.node_info = new String(s);
		}

		@Override
		public int getTag() {
			return this.node_tag;
		}

		@Override
		public void setTag(int t) {
			this.node_tag = t;
		}

		@Override
		public String toString()
		{
			return (String.format("{\"key\": %o, \"geo\": %s, \"tag\": %o, \"weight\": %f, \"info\": \"%s\"}", 
					this.key, this.geo, this.node_tag, this.node_weight, this.node_info));
		}

		@Override
		public int compareTo(NodeData N) 	 
		{
			if(node_weight<N.getWeight()) return -1;
			else if(node_weight>N.getWeight())return 1;
			else return 0;
		}

	}

	private class EdgeData implements edge_data{

		private NodeData src, dst;
		private int edge_tag;
		private double edge_weight;
		private String edge_info;
		private long edge_key;

		private EdgeData(node_data src, node_data dst, double weight, int tag, String info){
			this.src = (NodeData) src;
			this.dst = (NodeData) dst;
			this.edge_tag = tag;
			this.edge_info = new String(info);
			this.edge_key = keyCalcuclate(this.src.key, this.dst.key);

			setWeight(weight);

			this.src.out_set.add(this.edge_key);
			this.dst.in_set.add(this.edge_key);
			edges.put(this.edge_key, this);
		}
		public EdgeData(node_data src, node_data dst, double weight) {
			this(src, dst, weight, 0, "");
		}

//		public EdgeData(EdgeData edge) {
//			this(new NodeData(edge.src), new NodeData(edge.dst), edge.edge_weight, edge.edge_tag, edge.edge_info);
//		}

		@Override
		public int getSrc() {
			return this.src.key;
		}

		@Override
		public int getDest() {
			return this.dst.key;
		}

		@Override
		public double getWeight() {
			return this.edge_weight;
		}

		public void setWeight(double weight) {
			weight = weight < 0 ? 0 : weight;
			this.edge_weight = weight;
		}

		@Override
		public String getInfo() {
			return this.edge_info;
		}

		@Override
		public void setInfo(String s) {
			this.edge_info = new String(s);
		}

		@Override
		public int getTag() {
			return this.edge_tag;
		}

		@Override
		public void setTag(int t) {
			this.edge_tag = t;
		}

		@Override
		public String toString() {
			return String.format("{\"src\" = %o, \"dst\"=  %o, \"weight\" = %f, \"tag\" = %o, \"info\": \"%s\"}", 
					this.src.key, this.dst.key, this.edge_weight, this.edge_tag, this.edge_info);
		}

	}

	private class EdgeLocation implements edge_location {

		EdgeData E;
		public edge_data getEdge()
		{
			if(E!=null)return E;
			else
			{
				System.err.println("Null edge");
				return null;
			}
		}
		@Override
		public double getRatio()
		{
			node_data src=verticies.get(E.getSrc());
			node_data dst=verticies.get(E.getDest());
			if(dst!=null && src!=null && !src.equals(dst))
			{
				geo_location srcLoc=src.getLocation();
				geo_location srcdst=dst.getLocation();
				return srcLoc.distance(srcdst);
			}
			else
			{
				System.err.println("null data");
				return -1;
			}
		}
	}

	public DWGraph_DS(){
		this.verticies = new HashMap<>();
		this.edges = new HashMap<>();
		this.mode_counter = 0;
//		this.id = 0;
	}

	public DWGraph_DS(DWGraph_DS graph) {
		this();
		for(node_data node : graph.getV())
			addNode(node);
		for(EdgeData edge : graph.getE())
			connect(edge);
	}

	public DWGraph_DS(String json) {
		this();
		JSONParser parser = new JSONParser();

		try {
			JSONObject graph = (JSONObject)parser.parse(json);
			JSONObject nodes = (JSONObject)graph.get("nodes");
			JSONObject edges = (JSONObject)graph.get("edges");

			String s = nodes.toJSONString();
			JSONObject node_json = (JSONObject)parser.parse(s);

			for(Object o : node_json.keySet()) {
				int key = Integer.valueOf((String)o);
				JSONObject node_info = (JSONObject)parser.parse(((JSONObject)node_json.get(o)).toJSONString());
				addNode(key);
				node_data node = this.verticies.get(key);

				for(Object info : node_info.keySet()) {
					if((String)info == "tag") node.setTag(Integer.valueOf((String)node_info.get(info)));
					if((String)info == "weight") node.setWeight(Double.valueOf((String)node_info.get(info)));
					if((String)info == "info") node.setInfo((String)node_info.get(info));
					if((String)info == "geo") {
						JSONObject geo = (JSONObject)parser.parse(((JSONObject)node_json.get("geo")).toJSONString());
						int x = 0, y = 0, z = 0;
						for(Object geo_info : geo.keySet()) {
							if((String)geo_info == "x") x = Integer.valueOf((String)geo.get(geo_info));
							if((String)geo_info == "y") y = Integer.valueOf((String)geo.get(geo_info));
							if((String)geo_info == "z") z = Integer.valueOf((String)geo.get(geo_info));
						}
						node.setLocation(new Geo(x, y, z));
					} 
				}// end node_info for(Object info : node_info.keySet())
			}// end node_json for(Object o : node_json.keySet()) 

			s = edges.toJSONString();
			JSONObject edge_json = (JSONObject)parser.parse(s);
			for(Object o : edge_json.keySet()) {
				long key = Long.valueOf((String)o);
				JSONObject edge_info = (JSONObject)parser.parse(((JSONObject)edge_json.get(o)).toJSONString());
				int tag = 0, src = 0, dst = 0;
				double weight = 0;
				String info = "";

				for(Object param : edge_info.keySet()) {
					if(param.toString().equals("src")) src = Integer.parseInt(edge_info.get(param).toString());
					if(param.toString().equals("dst")) dst = Integer.parseInt(edge_info.get(param).toString());
					if(param.toString().equals("tag")) tag = Integer.parseInt(edge_info.get(param).toString());
					if(param.toString().equals("weight")) weight = Double.parseDouble(edge_info.get(param).toString());
					if(param.toString().equals("info")) info = (edge_info.get(param).toString());
				} 
				connect(src, dst, weight);
				EdgeData edge = this.edges.get(key);
				edge.setTag(tag);
				edge.setInfo(info);
			}
		} catch (ParseException e) {
			System.err.println("An error occured during parsing of JSON!");
			e.printStackTrace();
		}
	}

	@Override
	public node_data getNode(int key) {
		return this.verticies.get(key);
	}

	@Override
	public edge_data getEdge(int src, int dest) {		
		return this.edges.get(keyCalcuclate(src, dest));
	}

	@Override
	public void addNode(node_data n) {
		if(n == null) {
			return;
		}
		this.mode_counter++;
		this.verticies.put(n.getKey(), new NodeData(n));
	}

	public void addNode(int key){
		addNode(new NodeData(key));
	}

	public boolean canEdge(int src,int dest)
	{
		return (src != dest && getNode(src) != null && getNode(dest) != null);
	}

	@Override
	public void connect(int src, int dest, double w) {
		if(!canEdge(src, dest))
			return;

		long key = keyCalcuclate(src, dest);
		EdgeData edge = (EdgeData)this.edges.get(key);
		if(edge == null) {
			edge = new EdgeData(getNode(src), getNode(dest), w);
		}
		else
			edge.setWeight(w);
		this.mode_counter++;
	}

	public void connect(EdgeData e) 
	{
		if(e==null || !canEdge(e.src.key, e.dst.key))return;
		else 
		{
			connect(e.getSrc(),e.getDest(),e.getWeight());
		}
		//		if(edge == null) {
		//			edge = new EdgeData(e);
		//		}
		//		else {
		//			edge.edge_weight = e.edge_weight;
		//			edge.edge_tag = e.edge_tag;
		//			edge.edge_info = e.edge_info;
		//		}
		//		this.mode_counter++;

	}

	@Override
	public Collection<node_data> getV() {
		return this.verticies.values();
	}

	public Collection<EdgeData> getE() {
		return this.edges.values();
	}

	@Override
	public Collection<edge_data> getE(int node_id) {
		NodeData node = (NodeData)getNode(node_id);
		if(node == null) {
			return null;
		}
		Collection<Long> edge_keys = ((NodeData)getNode(node_id)).out_set;
		Collection<edge_data> collection = new ArrayList<edge_data>(edge_keys.size());
		for(long key : edge_keys) {
			collection.add(this.edges.get(key));
		}
		return collection;
	}

	@Override
	public node_data removeNode(int key) {
		NodeData node = (NodeData)getNode(key);

		if(node == null) {
			return null;
		}

		for(long l : node.in_set) {
			EdgeData edge = (EdgeData)this.edges.remove(l);
			edge.src.out_set.remove(l);
			this.mode_counter++;
		}

		for(long l : node.out_set) {
			EdgeData edge = (EdgeData)this.edges.remove(l);	
			edge.dst.in_set.remove(l);
			this.mode_counter++;
		}
		node.out_set = new HashSet<>();
		node.in_set = new HashSet<>();

		this.mode_counter++;
		return this.verticies.remove(key);
	}

	@Override
	public edge_data removeEdge(int src, int dest) 
	{
		EdgeData E=(EdgeData)this.getEdge(src, dest) ;
		if(E!=null)
		{
			long key=keyCalcuclate(src,dest);
			edges.remove(key);
		}
		this.mode_counter++;
		return E;
	}

	@Override
	public int nodeSize() {
		return this.verticies.size();
	}

	@Override
	public int edgeSize() {
		return this.edges.size();
	}

	@Override
	public int getMC() {
		return this.mode_counter;
	}

	@Override
	public String toString() {
		String str = "";
		for(node_data node : this.verticies.values())
			str += node;
		return str;
	}

	public static long keyCalcuclate(int src, int dst) 
	{
		return ((long)src << 32 | dst);
	}
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof directed_weighted_graph))	return false;
		DWGraph_DS g = (DWGraph_DS)o;
		if(g.nodeSize() != this.nodeSize())
		{
			System.err.println("Diffrent Node Size");
			return false;
		}
		if(g.edgeSize() != this.edgeSize())
		{
			System.err.println("Diffrent edge Size");
			return false;
		}
		for(node_data node : this.verticies.values())
		{
			node_data Tmp=g.getNode(node.getKey());
			if(!node.getLocation().equals(Tmp.getLocation()))
			{
				System.err.println("Diffrent Geo Location");
				return false;
			}
		}
		for(EdgeData E : this.edges.values())
		{
			int Dst=E.getDest();
			int Src=E.getSrc();
			EdgeData TmpE=(EdgeData) g.getEdge(Src,Dst);
			if(TmpE==null || TmpE.getWeight()!=this.getEdge(Src,Dst).getWeight())
			{
				System.err.println("Diffrent edge at Src Node Key "+Src+" Dest Node key "+ Dst);
				return false;
			}
		}

		System.out.println();
		return true;
	}

	@SuppressWarnings("unchecked")
	public String toJson() {
		JSONObject json = new JSONObject();

		JSONObject nodes = new JSONObject();
		for(int key : verticies.keySet()) {
			JSONObject node_json = new JSONObject();
			NodeData node = (NodeData)verticies.get(key);

			JSONObject geo_json = new JSONObject();
			geo_json.put("x", node.geo.x());
			geo_json.put("y", node.geo.y());
			geo_json.put("z", node.geo.z());

			node_json.put("geo", geo_json);
			node_json.put("tag", node.node_tag);
			node_json.put("weight", node.node_weight);
			node_json.put("info", node.node_info);

			nodes.put(key, node_json);
		}

		JSONObject edges = new JSONObject();
		for(long key : this.edges.keySet()) {
			JSONObject edge_json = new JSONObject();
			EdgeData edge = this.edges.get(key);

			edge_json.put("src", edge.src.key);
			edge_json.put("dst", edge.dst.key);
			edge_json.put("weight", edge.edge_weight);
			edge_json.put("tag", edge.edge_tag);
			edge_json.put("info", edge.edge_info);

			edges.put(key, edge_json);
		}

		json.put("nodes", nodes);
		json.put("edges", edges);
		return json.toJSONString();
	}
	
	public String GetData()
	{
		return "["+nodeSize()+","+edgeSize()+"]";
	}
}