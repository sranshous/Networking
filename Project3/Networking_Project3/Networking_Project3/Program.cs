using System;

namespace Networking_Project3
{
    class Program
    {
        static void Main(string[] args)
        {
            BellmanFord bf = new BellmanFord();
            bf.buildGraph("inputgraph.txt");
            bf.printGraph();
            bf.findShortestPaths();
            bf.printGraph();
            Console.ReadKey();
        }
    }
}
