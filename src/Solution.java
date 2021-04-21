import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution
{
    public static int[] RescueTheBunnies(int[][] times, int times_limit)
    {
        int bunniesResult[] = null;
        return bunniesResult;
    }
    private static Graph Convert2DArrayToGraph(int[][] graph)
    {
        return new Graph(graph);
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
                        addVertex(j);
                        Edge residualEdge = addEdge(j, i, 0);
                        newEdge.residualEdge = residualEdge;
                        residualEdge.residualEdge = newEdge;
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
        int leadstoRoom;
        int startsFromRoom;
        int totalCapacitySupported;
        Edge residualEdge;
        int currentBunnies;
        Edge(int fromNumber, int toRoomNumber, int capacity)
        {
            startsFromRoom = fromNumber;
            leadstoRoom = toRoomNumber;
            totalCapacitySupported = capacity;
        }
        public boolean IsNowResidual()
        {
            return totalCapacitySupported == 0;
        }
        public void Augment(int bottleNeck)
        {
            currentBunnies += bottleNeck;
            residualEdge.currentBunnies -= bottleNeck;
        }
        public int RemainingCapacity()
        {
            return totalCapacitySupported-currentBunnies;
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
        public Edge GetEdgeNotResidual(int to)
        {
            Edge found = null;
            for (Edge edge: edges)
            {
                if(edge.leadstoRoom == to && !edge.IsNowResidual())
                    found = edge;
            }
            return found;
        }
    }

    private static class Path
    {
        int roomNumber;
        Path next;
        Path(int room, Path nextPath)
        {
            roomNumber = room;
            next = nextPath;
        }
        void AddPath(Path pathToAdd)
        {
            Path currentPath = this;
            while(currentPath.next!=null)
                currentPath = currentPath.next;
            currentPath.next = pathToAdd;
        }
        public Path CreateCopy()
        {
            Path currentPath = this;
            Path newPath = new Path(this.roomNumber, null);
            Path newCurrentPath = newPath;
            currentPath = currentPath.next;
            while(currentPath != null)
            {
                newCurrentPath.next = new Path(currentPath.roomNumber, null);
                newCurrentPath = newCurrentPath.next;
                currentPath = currentPath.next;
            }
            return newPath;
        }
    }
}
