import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution
{
    public static int[] RescueTheBunnies(int[][] times, int times_limit)
    {
        ArrayList<Integer> bunniesResult = null;
        int timeLimit = times_limit;
        if(IsCycleInfinite(times, FindCycleIfExists(times)))
        {
            bunniesResult = new ArrayList<>();
            for(int i = 0; i < times.length-2; i++)
                bunniesResult.add(i);
        }
        else
        {
            bunniesResult = new ArrayList<>();
            int rowWithMinimumCycleCosts = FindMinimumCycleRow(times);
            while(timeLimit-times[rowWithMinimumCycleCosts][times.length-1] >= times[rowWithMinimumCycleCosts][times.length-1])
            {
                for(int i = 0; i < times.length; i++)
                {
                    if(i != rowWithMinimumCycleCosts && i != 0 && i != times.length-1)
                    {
                        timeLimit -= times[rowWithMinimumCycleCosts][i] + times[i][rowWithMinimumCycleCosts];
                        bunniesResult.add(i);
                    }
                }
            }

        }
        int[] result = null;
        if(bunniesResult.size() > 1)
            result = bunniesResult.stream().mapToInt(i->i).toArray();
        return result;
    }

    private static int FindMinimumCycleRow(int[][] graph)
    {
        int row = 0;
        int maximum = Integer.MAX_VALUE;
        for(int i = 0; i < graph.length; i++)
        {
            int sumOfCycles = 0;
            for(int j = 0; j < graph[i].length; j++)
            {
                sumOfCycles += graph[i][j] + graph[j][i];
            }
            if(sumOfCycles < maximum)
            {
                maximum = sumOfCycles;
                row = i;
            }
        }
        return row;
    }

    public static int FindCycleIfExists(int[][] graph)
    {
        int cycleRow = -1;
        for(int i = 0; i < graph.length && cycleRow == -1; i++)
        {
            if(graph[i][i] != 0)
                cycleRow = i;
        }
        return cycleRow;
    }

    public static boolean IsCycleInfinite(int[][] graph, int row)
    {
        if(row == -1)
            return  false;
        else
            return graph[row][row] < 0 ;
    }
    static class Graph
    {
        private final Map<Integer, Vertex> adjVertices;
        Graph(int[][] asIntArr)
        {
            adjVertices = new HashMap<>();
            for(int i = 0; i < asIntArr.length; i++)
            {
                addVertex(i);
                for(int j = 0; j < asIntArr[i].length; j++)
                {
                    if(asIntArr[i][j] != 0)
                    {
                        Edge newEdge = addEdge(i, j, asIntArr[i][j]);
                    }
                }
            }
        }
        void addVertex(int room)
        {
            adjVertices.putIfAbsent(room, new Vertex(room));
        }
        Edge addEdge(int from, int to, int cost)
        {
            return adjVertices.get(from).AddEdge(to, cost);
        }
    }
    static class Edge
    {
        int leadsToRoom;
        int startsFromRoom;
        int cost;
        Edge(int fromNumber, int toRoomNumber, int weight)
        {
            startsFromRoom = fromNumber;
            leadsToRoom = toRoomNumber;
            cost = weight;
        }
    }
    static class Vertex
    {
        int room;
        List<Edge> edges;
        Vertex(int roomNumber)
        {
            this.room = roomNumber;
            edges = new ArrayList<>();
        }
        public Edge AddEdge(int to, int cost)
        {
            Edge newEdge = new Edge(room,to,cost);
            edges.add(newEdge);
            return newEdge;
        }
        public Edge GetEdge(int to)
        {
            Edge found = null;
            for (Edge edge: edges)
            {
                if(edge.leadsToRoom == to)
                    found = edge;
            }
            return found;
        }
    }
}
