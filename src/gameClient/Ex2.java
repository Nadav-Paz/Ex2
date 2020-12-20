package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.util.*;
import java.util.List;


public class Ex2 implements Runnable {
    public static MyFrame frame;
    public static Arena AR;
    private static long dt = 33;
    private edge_data AgentTarget[];
    private List<CL_Pokemon> SortedPokemones;
    private DWGraph_Algo dijkstra[];
    private int prev_grade = 0;
    private edge_data AgentLocation[];
    private boolean is_finished = false;

    private static int level = -1;
    private static long id = -1;

    public static void main(String[] args) {
        if (args.length == 2) {
            level = Integer.parseInt(args[0]);
            if (level < 0 || 23 < level) {
                level = 0;
            }
            if (args[1].length() == 9) {
                id = Long.parseLong(args[1]);
            }
            else {
                id = 205542897;
            }
        }
        Thread client = new Thread(new Ex2());
        client.start();
    }


    public int GradeReader(String json) {
        JSONParser parser = new JSONParser();
        int grade = 0;
        try {
            JSONObject ServerData = new JSONObject(json);
            JSONObject GameServer = ServerData.getJSONObject("GameServer");
            grade = GameServer.getInt("grade");
            //System.out.println(Grade.toString());
        }
        catch (JSONException e) {
            System.err.println("An error occured during parsing of JSON!");
            e.printStackTrace();
        }
        return grade;
    }

    public long MovesReader(String json) {
        JSONParser parser = new JSONParser();
        long Moves = 0;
        try {
            JSONObject ServerData = new JSONObject(json);
            JSONObject GameServer = ServerData.getJSONObject("GameServer");
            Moves = GameServer.getLong("moves");
            //System.out.println(Grade.toString());
        }
        catch (JSONException e) {
            System.err.println("An error occured during parsing of JSON!");
            e.printStackTrace();
        }
        return Moves;
    }

    private void init(game_service game, directed_weighted_graph graph, String Pokemons) {
        //gg.init(g);
        AR = new Arena();
        AR.setGraph(graph);

        dijkstra = new DWGraph_Algo[graph.nodeSize()];
        for (int i = 0; i < dijkstra.length; i++) {
            dijkstra[i] = new DWGraph_Algo();
            dijkstra[i].init(new DWGraph_DS((DWGraph_DS) graph));
            if (i == 0) {
                dijkstra[i].shortestPathDist(i, 1);
            }
            else {
                dijkstra[i].shortestPathDist(i, 0);
            }
        }

        AR.setPokemons(Arena.json2Pokemons(Pokemons));
        frame = new MyFrame("Test Ex2");
        frame.setSize(1000, 700);
        frame.update(AR);
        frame.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            AgentTarget = new edge_data[rs];
            AgentLocation = new edge_data[rs];
            //System.out.println(info);
            //System.out.println(game.getPokemons());
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());
            for (int a = 0; a < cl_fs.size(); a++) {
                Arena.updateEdge(cl_fs.get(a), graph);
//                System.err.println("Value: " + cl_fs.get(a).getValue() + ", Edge: " + cl_fs.get(a).get_edge().toString());
            }
            for (int a = 0; a < rs; a++) {
                //int ind = a%cl_fs.size();
                CL_Pokemon c = cl_fs.get(a);
                int nn = c.get_edge().getSrc();
                if (c.getType() < 0) {
                    nn = c.get_edge().getSrc();
                }
                game.addAgent(nn);
//                System.err.println(nn);
                AgentTarget[a] = c.get_edge();
                AgentLocation[a] = c.get_edge();
            }
            frame.SetColoredEdge(AgentLocation);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        game_service game;
        if (Ex2.level == -1 && Ex2.id == -1) {
            GUI gui = new GUI();
            gui.begin();
            while (!gui.IsFinshed()) gui.Delay(100);
            System.err.println(gui.IsFinshed());
            int level = gui.getLevel();
//        int level = 11;
            game = Game_Server_Ex2.getServer(level);
            game.login(gui.getID());
        }
        else {
            game = Game_Server_Ex2.getServer(Ex2.level);
            game.login(Ex2.id);
        }

        String graphData = game.getGraph();
        directed_weighted_graph graph = new DWGraph_DS(graphData);
        String Pokemons = game.getPokemons();
        String Agents = game.getAgents();
        init(game, graph, Pokemons);
        Graphics g = frame.getGraphics();
        long Time = game.timeToEnd() / 1000;// Time in secoends;
        int i = 0;
        String str = game.toString();
        game.startGame();
        while (game.isRunning()) {
            int Grade = GradeReader(str);
            long move = MovesReader(str);
            Time = game.timeToEnd() / 1000;// Time in secoends;
            frame.SetCounter(Grade, move, Time);
            Pokemons = game.getPokemons();
            edge_data[] E = PokemonFinder(Pokemons);
            str = game.toString();
            Agents = game.getAgents();
            if (Time != 0) {
                if (Grade - prev_grade != 0) {
                    prev_grade = Grade;
                    calculate_trajectory(game, graph);
                }
                moveAgants(game, graph, E);
            }
            else {
                if (!is_finished) {
                    is_finished = true;
                    System.err.println(Grade);
                }
            }
            try {
                Thread.sleep(dt);
                //SetTimerBegin(g,t,frame.getWidth(),frame.getHeight(),level,str);
                if (i % 1 == 0) {
                    frame.repaint();
                }
                i++;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            //frame.paintUpdate(frame.getGraphics());
        }
        //StopFrame(g,frame.getWidth(),frame.getHeight());
        game.stopGame();
        System.out.println("Game Stoped");
        System.out.println();
        System.exit(0);
    }

    private void calculate_trajectory(game_service game, directed_weighted_graph g) {
        PokemonFinder(game.getPokemons());
        List<CL_Agent> agents = Arena.getAgents(game.getAgents(), g);
        PriorityQueue<weighted_pokemon> weighted_pokemons = new PriorityQueue<>();

        boolean[] allocated_agents = new boolean[agents.size()];
        boolean[] allocated_pokemons = new boolean[SortedPokemones.size()];

        int already_allocated = 0;
        int allocations = Math.min(allocated_agents.length, allocated_pokemons.length);

        for (int i = 0; i < agents.size(); i++) {
            for (int j = 0; j < SortedPokemones.size(); j++) {
                weighted_pokemons.add(new weighted_pokemon(SortedPokemones.get(j), agents.get(i), j));
            }
        }

        while (already_allocated < allocations) {
            weighted_pokemon next = weighted_pokemons.remove();
            if (!allocated_agents[next.agent.getID()]) {
                if (!allocated_pokemons[next.pokemon_id]) {
                    allocated_agents[next.agent.getID()] = true;
                    allocated_pokemons[next.pokemon_id] = true;
                    already_allocated++;
                    AgentTarget[next.agent.getID()] = next.pokemon.get_edge();
                }
            }
        }
    }

    private void moveAgants(game_service game, directed_weighted_graph g, edge_data[] poke) {
        String lg = game.move();
        List<CL_Agent> log = Arena.getAgents(lg, g);
        AR.setGraph(g);
        AR.setAgents(log);
        DWGraph_Algo A = new DWGraph_Algo();

        for (int i = 0; i < log.size(); i++) {

            CL_Agent ag = log.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();

            A = dijkstra[src];

            if (dest == -1) {
                if (src == AgentTarget[i].getSrc()) {
                    AgentLocation[i] = AgentTarget[i];
                    game.chooseNextEdge(ag.getID(), AgentTarget[i].getDest());
                }
                else {
                    List<node_data> l = A.shortestPath(src, AgentTarget[i].getSrc());
                    if (l != null) {
                        if (l.size() > 1) {
                            dest = l.get(1).getKey();
                            AgentLocation[i] = g.getEdge(src, dest);
                        }
                    }
                    game.chooseNextEdge(ag.getID(), dest);
                }
                frame.SetColoredEdge(AgentLocation);
            }
        }
    }


    public edge_data[] PokemonFinder(String json) {
        List<CL_Pokemon> A = AR.json2Pokemons(json);
        AR.setPokemons(A);
        SortedPokemones = A;
        edge_data EArr[] = new edge_data[A.size()];
        for (int i = 0; i < A.size(); i++) {
            CL_Pokemon Pokemon = A.get(i);
            Arena.updateEdge(Pokemon, AR.getGraph());
            EArr[i] = A.get(i).get_edge();
            //System.out.println("Edge Src :"+EArr[i].getSrc()+" Dest : "+EArr[i].getDest());
            int Src = EArr[i].getSrc();
            int Dest = EArr[i].getDest();
            if (Pokemon.getType() == -1) {
                EArr[i] = AR.getGraph().getEdge(Math.max(Src, Dest), Math.min(Src, Dest));
            }
            else {
                EArr[i] = AR.getGraph().getEdge(Math.min(Src, Dest), Math.max(Src, Dest));
            }
            //	System.out.println("Edge Src :"+EArr[i].getSrc()+" Dest : "+EArr[i].getDest());

        }

        return EArr;
    }

    private class weighted_pokemon implements Comparable<weighted_pokemon>, Comparator<weighted_pokemon> {
        private CL_Pokemon pokemon;
        private CL_Agent agent;
        private double net_value;
        private int pokemon_id;

        private weighted_pokemon(CL_Pokemon pokemon, CL_Agent agent, int pokemon_id, double net_value) {
            this.pokemon = pokemon;
            this.agent = agent;
            this.pokemon_id = pokemon_id;
            this.net_value = net_value;
        }

        private weighted_pokemon(CL_Pokemon pokemon, CL_Agent agent, int pokemon_id) {
            int location = agent.getNextNode() != -1 ? agent.getNextNode() : agent.getSrcNode();
            double speed = agent.getSpeed();
            double value = pokemon.getValue();
            double price = dijkstra[location].shortestPathDist(location, pokemon.get_edge().getSrc());
            this.net_value = speed * value / price;
            this.agent = agent;
            this.pokemon = pokemon;
            this.pokemon_id = pokemon_id;
        }

        private double calculate_net_value() {
            int location = agent.getNextNode() != -1 ? agent.getNextNode() : agent.getSrcNode();
            double speed = agent.getSpeed();
            double value = pokemon.getValue();
            double price = dijkstra[location].shortestPathDist(location, pokemon.get_edge().getSrc());
            return (speed * value / price);
        }

        @Override
        public int compareTo(@NotNull weighted_pokemon o) {
            if (o.net_value < this.net_value) return -1;
            if (o.net_value > this.net_value) return 1;
            return 0;
        }

        @Override
        public int compare(weighted_pokemon o1, weighted_pokemon o2) {
            return o1.compareTo(o2);
        }
    }
}





