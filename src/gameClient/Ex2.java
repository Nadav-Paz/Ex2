package gameClient;

import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.game_service;
import Server.Game_Server_Ex2;

public class Ex2 
{
	 public static void main(String[] args) 
	 {
		 int level=0;
		 game_service game=Game_Server_Ex2.getServer(level);
		 //System.out.println(game.getGraph());
		 System.out.println(game.getPokemons());
		 game.addAgent(0);
		 System.out.println("/***********************************************************************************************************************************************************************************************************************************************************************/");
		 game.startGame();
		 System.out.println(game.timeToEnd());
		 System.out.println("/***********************************************************************************************************************************************************************************************************************************************************************/");
		 DWGraph_DS G=new DWGraph_DS();
		 DWGraph_Algo A=new DWGraph_Algo();
		 A.init(G);
		 int i=0;
		 while(game.isRunning() && i<10)
		 {
		 	game.chooseNextEdge(0,10);
		 	game.move();
			 System.out.println(game.getAgents());
		 	i=i+1;
		 }

	 }
}
