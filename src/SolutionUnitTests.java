import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SolutionUnitTests
{
    @Test
    public void ShouldReturnNull()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0}};
        int timeLimit = -1;
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertNull(actualResult);
    }
    @Test
    public void ShouldReturnTwoBunnies()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0}};
        int timeLimit = 3;
        int[] expectedResult = {0,1};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnAllBunnies()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1, 2, 2},
                {1, 0, 1, 1, 1, 1, 1},
                {1, 1, 0, 1, 1, 1, 3},
                {1, -3, 1, 0, 1, 1, 1},
                {1, 1, 1, 1, 0, 1, 2},
                {1, 1, 1, 1, 1, 0, 2},
                {1, 1, 1, 1, 1, 1, 0},};
        int timeLimit = 1;
        int[] expectedResult = {0,1,2,3,4};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnAll2Then3_1_5_4_6()
    {
        int[][]graph = {
                {0, 1, 1, 4, 1, 2, 2},
                {1, 0, 1, 3, 1, 1, 1},
                {1, 1, 0, 2, 1, 1, 3},
                {1, -3, 1, 0, 1, 1, 1},
                {1, 1, 1, 4, 0, 1, 1},
                {1, 4, 1, 5, 1, 0, 2},
                {1, 1, 1, 2, 1, 1, 0},};
        int timeLimit = 3;
        int[] expectedResult = {0,1,2,3,4};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnTwoBunniesWithCycles()
    {
        int[][]graph = {
                {0, 2, 2, 2, -1},
                {9, 0, 2, 2, -1},
                {9, 3, 0, 2, -1},
                {9, 3, 2, 0, -1},
                {9, 3, 2, 2, 0}
        };
        int timeLimit = 1;
        int[] expectedResult = {1,2};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnThreeBunniesOnBasicGraph()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, -1, 1},
                {1, 1, 1, 1, 1}};
        int timeLimit = 1;
        int[] expectedResult = {0,1,2};
        int[] actualResult = Solution.RescueTheBunnies(graph,timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnAllPossibleBunnies()
    {
        int[][]graph = {
                {-1, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0}};
        int timeLimit = -100;
        int[] expectedResult = {0,1,2};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnThreeBunnies()
    {
        int[][]graph = {
                {0, 1, 1, 4, 1, 2, 2},
                {1, 0, 1, 3, 1, 1, 1},
                {1, 1, 0, 2, 1, 1, 3}, //-
                {1, -1, 1, 0, 1, 1, 1}, //-
                {1, 1, 1, 4, 0, 1, 1},
                {1, 4, 1, 5, 1, 0, 2},
                {1, 1, 1, 2, 1, 1, 0},};
        int timeLimit = 3;
        int[] expectedResult = {0,1,2};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnNullOrNothing()
    {
        int[][]graph = {
                {0, 1,},
                {1, 0}};
        int timeLimit = 3;
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertNull(actualResult);
    }
    @Test
    public void ShouldNavigateWith0TimeLimit()
    {
        int[][]graph = {
                {0,0,1},
                {0,0,0},
                {1,0,0}};
        int timeLimit = 0;
        int[]expectedResult = {0};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult,actualResult);
    }
    @Test
    public void ShouldReturnOnlyOneBunny()
    {
        int[][]graph = {
                {0, 1, 1, 4, 1, 2, 2},
                {1, 0, 1, 3, 1, 1, 1},
                {1, 1, 0, 2, 1, 1, 3},
                {1, -1, 1, 0, 1, 1, 1},
                {1, 1, 1, 4, 0, 1, 1},
                {1, 4, 1, 5, 1, 0, 2},
                {1, 1, 1, 2, 1, 1, 0},};
        int timeLimit = 2;
        int[]expectedResult = {0};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult,actualResult);
    }

}
