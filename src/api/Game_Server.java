package api;

public class Game_Server implements game_service 
{
	public DWGraph_Algo A;
	
	public Game_Server()
	{
		A=new DWGraph_Algo();
	}

	@Override
	public String getGraph()
	{
		DWGraph_DS G=(DWGraph_DS)A.getGraph();
		return G.toJson();
	}

	@Override
	public String getPokemons() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addAgent(int start_node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long startGame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long stopGame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long chooseNextEdge(int id, int next_node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long timeToEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String move() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean login(long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
