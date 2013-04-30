using System;       // for Console
using System.IO;    // for the StreamReader
using System.Collections.Generic;   // for generic type lists
using System.Text;  // for StringBuiilder

namespace Networking_Project3
{
    public class BellmanFord
    {
        private List<Router> graph;
        private const int INF = 999;

        public BellmanFord()
        {
            this.graph = new List<Router>();
        }

        public void buildGraph(string filename)
        {
            using (StreamReader file = new StreamReader(filename))
            {
                string line = null;
                char[] spaceDelim = { ' ' };
                int numRouters = int.Parse(file.ReadLine());

                for (int i = 0; i < numRouters; i++)
                {
                    line = file.ReadLine();
                    Router r = new Router(numRouters);
                    r.distance[i] = 0;      // distance to itself is 0
                    r.nextHop[i] = i;       // next hop to itself is itself

                    string[] neighbors = line.Split(spaceDelim);
                    for (int j = 1; j < neighbors.Length; j += 2)
                    {
                        r.addNeighbor(neighbors[j][0] - 'A', neighbors[j + 1][0] - '0');
                    }

                    this.graph.Add(r);
                }
            }
        }

        public void printGraph()
        {
            for (int i = 0; i < graph.Count; i++)
            {
                Console.WriteLine((char)('A' + i));
                Console.WriteLine(graph[i]);
            }
        }

        public void findShortestPaths()
        {
            Console.WriteLine("----- Finding paths -----");
            for (int i = 0; i < graph.Count; i++)
            {
                Router r = graph[i];
                for (int j = 0; j < r.distance.Length; j++)
                {
                    if (i == j) continue;

                    for (int k = 0; k < r.neighbors.Count; k++)
                    {
                        int neighbor = r.neighbors[k];
                        if (r.distance[j] > r.distance[neighbor] + graph[j].distance[neighbor])
                        {
                            r.distance[j] = r.distance[neighbor] + graph[j].distance[neighbor];
                            r.nextHop[j] = neighbor;
                        }
                    }
                }
            }
            Console.WriteLine("----- Found paths -----");
        }

        private class Router
        {
            public int[] distance;
            public int[] nextHop;
            public List<int> neighbors;

            public Router(int numRouters)
            {
                this.distance = new int[numRouters];
                this.nextHop = new int[numRouters];
                this.neighbors = new List<int>();

                for (int i = 0; i < numRouters; i++)
                {
                    this.distance[i] = INF;
                    this.nextHop[i] = -1;
                }
            }

            public void addNeighbor(int neighbor, int distance)
            {
                this.neighbors.Add(neighbor);
                this.distance[neighbor] = distance;
                this.nextHop[neighbor] = neighbor;
            }

            public override string ToString()
            {
                StringBuilder sb = new StringBuilder();

                sb.Append("distance: ");
                for (int i = 0; i < distance.Length; i++)
                {
                    sb.Append(String.Format("{0, 4}", distance[i]));
                }
                sb.Append("\n");

                sb.Append("nextHop: ");
                for (int i = 0; i < distance.Length; i++)
                {
                    sb.Append(String.Format("{0, 4}", nextHop[i]));
                }
                sb.Append("\n");

                sb.Append("neighbors:\n");
                for (int i = 0; i < neighbors.Count; i++)
                {
                    sb.Append((char)(neighbors[i] + 'A') + " " + distance[neighbors[i]] + " " + nextHop[neighbors[i]] + "\n");
                }

                return sb.ToString();
            }
        }
    }
}
