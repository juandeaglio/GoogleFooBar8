import java.util.*;

public class Solution
{
    public static int[] RescueTheBunnies(int[][] times, int times_limit)
    {
        ArrayList<Integer> bunniesResult = null;
        int timeLimit = times_limit;
        if(IsCycleInfinite(times, FindCycleIfExists(times))) // TODO: will not work if the cycle is not reachable w/ the timelimit
        {
            bunniesResult = new ArrayList<>();
            for(int i = 0; i < times.length-2; i++)
                bunniesResult.add(i);
        }
        else
        {
            int[] bellmanVertexArr = BellmanFordWithAGivenStart(times, 0);
        }
        int[] result = null;
        if(bunniesResult != null && bunniesResult.size() > 1)
            result = bunniesResult.stream().mapToInt(i->i).toArray();
        return result;
    }

    private static int[] BellmanFordWithAGivenStart(int[][] times, int start) {
        int[] bellmanVertexArr = new int[times.length];
        Arrays.fill(bellmanVertexArr, Integer.MAX_VALUE);
        bellmanVertexArr[0] = start;
        boolean changesWereMade = true;
        while(changesWereMade)
        {
            boolean wasThereAChange = false;
            for(int i = 0; i < bellmanVertexArr.length; i++)
            {
                if(bellmanVertexArr[i] != Integer.MAX_VALUE)
                {
                    for(int j = 0; j < times[i].length; j++)
                    {
                        if(j != i && times[i][j] < bellmanVertexArr[j])
                        {
                            bellmanVertexArr[j] = times[i][j];
                            wasThereAChange = true;
                        }

                    }
                }
            }
            changesWereMade = wasThereAChange;
        }
        return bellmanVertexArr;
    }

    private static int FindMinimumCycleRow(int[][] graph)
    {
        int rowToReturn = -1;
        int row = 0;
        int maximum = 0;
        for(int j = 0; j < graph[0].length; j++)
        {
            maximum += graph[0][j] + graph[j][0];
        }
        int sum = 0;
        for(int i = 0; i < graph.length; i++)
        {
            int sumOfCycles = 0;
            for(int j = 0; j < graph[i].length; j++)
            {
                sumOfCycles += graph[i][j] + graph[j][i];
            }
            if(sumOfCycles <= maximum)
            {
                sum = sumOfCycles;
                row = i;
            }
        }
        if(sum != maximum)
            rowToReturn = row;
        return rowToReturn;
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
