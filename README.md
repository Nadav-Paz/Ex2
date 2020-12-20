# Ex2
Ex2 Nadav Paz (308161769) Ron Kappel (205542897)


In this project we implemented a directional weighted graph and the Pokemon game.

The graph DS contains two HashMaps - one for the Verticies and one for the Edges.

The nodes do not hold the address of the edges but the key to the edge in the main HashMap.
The keys of the edges is a long which is a concatination of the src and dest integers (src << 32 | dst)

Calculating the shortests pathes etc. is done using Dijkstra algorithm, and connectivity is tested using DFS algorithm once regularly and another time when all edges are reversed.

We implemented the Pokemon game, and it could run from external jar file via GUI or via parsing the arguments from the command line.
The inputs that are expected: level number (0-23) and ID (9 digits). will default to level 0 and id 205542897 if illegal input is parsed.
