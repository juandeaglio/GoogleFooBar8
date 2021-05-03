import java.util.*;

public class Solution
{
    public static int[] RescueTheBunnies(int[][] times, int times_limit)
    {
        int[][] paths = new int[times.length][times.length];
        int[][] floydWarshallGraph = convertToFloydWarshall(times);
        int[] result = null;
        if(times.length > 2)
        {
            if (HasNegativeCycle(floydWarshallGraph))
            {
                result = new int[times.length-2];
                for (int i = 0; i < times.length - 2; i++)
                    result[i] = i;
            }
            else
            {
                result = BFSFloydWarshall(paths, times_limit, times);
            }
        }
        return result;
    }

    private static boolean HasNegativeCycle(int[][] floydWarshallGraph)
    {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < floydWarshallGraph.length; i++)
            min = Math.min(min, floydWarshallGraph[i][i]);
        return min < 0;
    }

    private static int[][] convertToFloydWarshall(int[][] times)
    {
        int[][] distances = new int[times.length][times.length];
        for(int i = 0; i < distances.length; i++)
        {
            distances[i][i] = 0;
        }
        for(int i = 0; i < distances.length; i++)
        {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
            System.arraycopy(times[i], 0, distances[i], 0, distances.length);
        }
        for(int i = 0; i < distances.length; i++)
        {
            for(int j = 0; j < distances.length; j++)
            {
                for(int k = 0; k < distances.length; k++)
                {
                    if(distances[j][k] > distances[j][i] + distances[i][k])
                    {
                        distances[j][k] = distances[j][i] + distances[i][k];
                    }
                }
            }
        }
        return distances;
    }
    private static int[] BFSFloydWarshall(int[][] paths, int timeLimit, int[][] times)
    {
        Queue<HashMap<Integer,HashSet<Integer>>>visitedQueue = new ArrayDeque<>();
        Queue<Path> pathTaken = new ArrayDeque<>();
        pathTaken.add(new Path(0, 0, null));
        visitedQueue.add(new HashMap<>());
        HashMap<Integer,HashSet<Integer>> visitedTemp = visitedQueue.poll();
        visitedTemp.put(0, new HashSet<>());
        visitedQueue.add(visitedTemp);
        int max = 0;
        for(int i = 0; i < times.length; i++)
            max = Math.max(max, paths[0][i])+1;
        Path temp = null;
        Path bestPath = null;
        int verticesExitedWith = 0;
        int iterations = 0;
        while ((!pathTaken.isEmpty() || temp != null) && verticesTouched(bestPath, times.length) != times.length-2 && iterations < 2000 )
        {
            if(pathTaken.peek() != null && pathTaken.peek().edgesTotal > max)
            {
                pathTaken.poll();
                visitedQueue.poll();
            }
            else
            {
                temp = pathTaken.poll();
                HashMap<Integer,HashSet<Integer>> currentVisited = visitedQueue.poll();
                if (temp != null && currentVisited != null)
                {
                    for (int i = 0; i < times.length; i++)
                    {
                        int current = getLastVertexOf(temp);
                        HashMap<Integer,Path> availablePaths = BellmanFordWithAGivenStart(times,current);
                        currentVisited.putIfAbsent(current, new HashSet<>());
                        if ( ( (i != current ) || (i == times.length-1) ) && !currentVisited.get(current).contains(i))
                        {
                            HashMap<Integer,HashSet<Integer>> visited = CopyHashMap(currentVisited);
                            Path pathContinuation = availablePaths.get(i).CreateCopy();
                            Path newPath = temp.CreateCopy();
                            newPath.edgesTotal+= pathContinuation.edgesTotal;
                            newPath.cost += pathContinuation.cost;
                            Path currentPath = newPath;
                            while (currentPath.nextEdge != null)
                            {
                                currentPath.nextEdge.edgesTotal+=pathContinuation.edgesTotal;
                                currentPath.nextEdge.cost += pathContinuation.cost;
                                currentPath = currentPath.nextEdge;
                            }
                            currentPath.nextEdge = pathContinuation.nextEdge;
                            while (currentPath != null)
                            {
                                visited.get(current).add(i);
                                currentPath = currentPath.nextEdge;
                            }
                            pathTaken.add(newPath);
                            visitedQueue.add(visited);
                            if (i == times.length-1 && verticesTouched(newPath, times.length) > verticesExitedWith && newPath.cost <= timeLimit)
                            {
                                bestPath = newPath;
                                verticesExitedWith = verticesTouched(newPath, times.length);
                            }
                        }
                    }
                }
            }
            iterations++;
        }

        int[] result = null;
        HashSet<Integer> bunniesFound = new HashSet<>();
        if(bestPath != null && bestPath.edgesTotal > 1)
        {
            int count = 0;
            while(bestPath != null)
            {
                if(bestPath.currentVertex != 0 && bestPath.currentVertex != times.length-1)
                {
                    bunniesFound.add(bestPath.currentVertex-1);
                }
                bestPath = bestPath.nextEdge;
            }
            result = new int[bunniesFound.size()];
            for (Integer vertex: bunniesFound)
            {
                result[count] = vertex;
                count++;
            }
            Arrays.sort(result);
        }
        return result;
    }

    private static int verticesTouched(Path newPath, int length)
    {
        HashSet<Integer> bunniesFound = new HashSet<>();
        Path path1 = newPath;
        if(newPath != null && newPath.edgesTotal > 1)
        {
            while (path1 != null)
            {
                if (path1.currentVertex != 0 && path1.currentVertex != length - 1)
                {
                    bunniesFound.add(path1.currentVertex - 1);
                }
                path1 = path1.nextEdge;
            }
        }
        return bunniesFound.size();
    }

    private static HashMap<Integer, HashSet<Integer>> CopyHashMap(HashMap<Integer, HashSet<Integer>> currentVisited)
    {
        HashMap<Integer, HashSet<Integer>> newMap = new HashMap<>();
        currentVisited.forEach((k,v) ->
        {
            HashSet<Integer> copy = new HashSet<>(v);
            newMap.put(k,copy);
        });
        return newMap;
    }

    private static int getLastVertexOf(Path temp)
    {
        Path newPath = temp;
        int lastVertex = 0;
        while(newPath != null)
        {
            lastVertex = newPath.currentVertex;
            newPath = newPath.nextEdge;
        }
        return lastVertex;
    }
    private static HashMap<Integer,Path> BellmanFordWithAGivenStart(int[][] times, int start) {
        HashMap<Integer,Path> bellmanVertexArr = new HashMap<>();
        for(int i = 0; i< times[0].length; i++)
        {
            bellmanVertexArr.put(i, new Path(i, Integer.MAX_VALUE,null));
        }
        bellmanVertexArr.put(start,  new Path(start,0,null));
        int timesDone = 0;
        while(timesDone < times[0].length )
        {
            for(int i = 0; i < times.length; i++)
            {
                if(bellmanVertexArr.get(i).cost != Integer.MAX_VALUE)
                {
                    for(int j = 0; j < times[i].length; j++)
                    {
                        if(j != i && ((bellmanVertexArr.get(i).cost + times[i][j] < bellmanVertexArr.get(j).cost) ))
                        {
                            Path newPath;
                            newPath = bellmanVertexArr.get(i).CreateCopy();
                            newPath.cost = newPath.cost + times[i][j];
                            newPath.edgesTotal++;
                            Path currentEdge = newPath;
                            while(currentEdge.nextEdge != null)
                            {
                                currentEdge.nextEdge.cost += times[i][j];
                                currentEdge = currentEdge.nextEdge;
                                currentEdge.edgesTotal++;
                            }
                            currentEdge.nextEdge = new Path(j, 0, null);
                            bellmanVertexArr.put(j, newPath);
                        }
                    }
                }
            }
            timesDone++;
        }
        return bellmanVertexArr;
    }
    static class Path
    {
        int currentVertex;
        int cost;
        Path nextEdge;
        int edgesTotal;
        Path(int currentVertex, int totalCost, Path next)
        {
            this.currentVertex = currentVertex;
            this.nextEdge = next;
            this.cost = totalCost;
        }

        public Path CreateCopy()
        {
            Path currentPath = this;
            Path newPath = new Path(this.currentVertex, this.cost, null);
            newPath.edgesTotal = this.edgesTotal;
            Path newCurrentPath = newPath;
            currentPath = currentPath.nextEdge;
            while(currentPath != null)
            {
                newCurrentPath.nextEdge = new Path(currentPath.currentVertex, currentPath.cost, null);
                newCurrentPath.nextEdge.edgesTotal = currentPath.edgesTotal;
                newCurrentPath = newCurrentPath.nextEdge;
                currentPath = currentPath.nextEdge;
            }
            return newPath;
        }
    }

}
