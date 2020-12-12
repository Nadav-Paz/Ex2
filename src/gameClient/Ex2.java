package gameClient;

import api.*;
import Server.Game_Server_Ex2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Iterator;

public class Ex2 {

    public static edge_location[] pokemons(String json, directed_weighted_graph graph) {
        JSONParser parser = new JSONParser();

        try {
            JSONObject obj = (JSONObject) parser.parse(json);
            JSONArray pokemons = (JSONArray) obj.get("Pokemons");

            EdgeLocation[] edges = new EdgeLocation[pokemons.size()];
            int edge_counter = 0;

            for (Object o : pokemons.toArray()) {
                for (Object poke : ((JSONObject) o).keySet()) {
                    if (poke.toString().equals("Pokemon")) {

                        double x = 0, y = 0, z = 0, value = 0;
                        int type = 0;

                        JSONObject pokemon = (JSONObject) parser.parse(((JSONObject) o).toJSONString());
                        pokemon = (JSONObject) pokemon.get(poke);

                        for (Object param : pokemon.keySet()) {
                            if (param.toString().equals("pos")) {
                                String[] coordinates = ((JSONObject) pokemon).get(param).toString().split(",");
                                x = Double.parseDouble(coordinates[0]);
                                y = Double.parseDouble(coordinates[1]);
                                z = Double.parseDouble(coordinates[2]);
                            }
                            if (param.toString().equals("type")) {
                                type = Integer.parseInt(pokemon.get(param).toString());
                            }
                            if (param.toString().equals("value")) {
                                value = Double.parseDouble(pokemon.get(param).toString());
                            }
                        }

                        //finding the relevant edge
                        edges[edge_counter] = new EdgeLocation(graph, new Geo(x, y, z), value);
                        boolean flag = false;
                        Iterator<edge_data> iterator = ((DWGraph_DS) graph).getEdges().iterator();
                        while (iterator.hasNext() && !flag) {
                            edge_data edge = iterator.next();
                            if (type == 1 && edge.getSrc() < edge.getDest()) {
                                flag = edges[edge_counter].setEdge(edge);
                            }
                            if (type == -1 && edge.getSrc() > edge.getDest()) {
                                flag = edges[edge_counter].setEdge(edge);
                            }
                        }
                        edge_counter++;
                    }
                }
            }
            return edges;
        } catch (ParseException e) {
            System.err.println("An error occured during parsing of JSON!");
            e.printStackTrace();
            return null;
        }
    }

    public static agent_data[] agents(String json) {
        JSONParser parser = new JSONParser();

        try {
            JSONObject obj = (JSONObject) parser.parse(json);
            JSONArray agents = (JSONArray) obj.get("Agents");

            agent_data[] agents_array = new agent_data[agents.size()];
            int agent_counter = 0;

            for (Object o : agents.toArray()) {
                for (Object ag : ((JSONObject) o).keySet()) {
                    if (ag.toString().equals("Agent")) {

                        int id = 0;
                        double value = 0;
                        int src = 0;
                        int dest = 0;
                        double speed = 0;
                        double x = 0, y = 0, z = 0;

                        JSONObject agent = (JSONObject) parser.parse(((JSONObject) o).toJSONString());
                        agent = (JSONObject) agent.get(ag);

                        for (Object param : agent.keySet()) {

                            if (param.toString().equals("id")) {
                                id = Integer.parseInt(agent.get(param).toString());
                            }
                            if (param.toString().equals("value")) {
                                value = Double.parseDouble(agent.get(param).toString());
                            }
                            if (param.toString().equals("src")) {
                                src = Integer.parseInt(agent.get(param).toString());
                            }
                            if (param.toString().equals("dest")) {
                                dest = Integer.parseInt(agent.get(param).toString());
                            }
                            if (param.toString().equals("speed")) {
                                speed = Double.parseDouble(agent.get(param).toString());
                            }
                            if (param.toString().equals("pos")) {
                                String[] coordinates = ((JSONObject) agent).get(param).toString().split(",");
                                x = Double.parseDouble(coordinates[0]);
                                y = Double.parseDouble(coordinates[1]);
                                z = Double.parseDouble(coordinates[2]);
                            }
                        }

                        //finding the relevant edge
                        agents_array[agent_counter] = new agent_data(id, value, src, dest, speed, null);
                        geo_location pos = new Geo(x,y,z);
                        agents_array[agent_counter++].setPos(pos);
                    }
                }
            }
            return agents_array;
        } catch (ParseException e) {
            System.err.println("An error occured during parsing of JSON!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        int level = 3;
        game_service game = Game_Server_Ex2.getServer(level);
        //System.out.println(game.getGraph());

        directed_weighted_graph game_graph = new DWGraph_DS(game.getGraph());

        System.out.println(game.getPokemons());
        pokemons(game.getPokemons(), game_graph);
        game.addAgent(0);
        System.out.println("/***********************************************************************************************************************************************************************************************************************************************************************/");
        game.startGame();
        System.out.println(game.timeToEnd());
        System.out.println("/***********************************************************************************************************************************************************************************************************************************************************************/");
        DWGraph_DS G = new DWGraph_DS();
        DWGraph_Algo A = new DWGraph_Algo();
        A.init(G);
        int i = 0;
        while (game.isRunning() && i < 10) {
            game.chooseNextEdge(0, i);
            game.move();
            System.out.println(game.getAgents());
            i = i + 1;
            System.out.println(agents(game.getAgents())[0].toString());

        }

    }

}
