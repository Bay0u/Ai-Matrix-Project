# Ai-Matrix-Project

<!-- Introduction to Artificial Intelligence
Project 1 - The Matrix: Escaped
Team 40
MohammadEhab 43-11068 T17
MalikSohile 43-12688
George Nicola 43-1355 T11
AhmedSalah 43-18637 T14 -->
#A discussion of your implementation of the search-tree node ADT.
The Node ADT has ten variables:

1. state
2. parent
3. operator
4. depth
5. cost
6. path
7. H1
8. H2
9. SumAStar1
10. SumAStar2

state is a String representing the current status of the Node, it contains all the relevant
information about the Node and takes the following form:
"NeoPlace:Nx,Ny;NeoDamage:20;CarriedHostages:H1,H2;TotalAgents:A1:A1X:A1Y,A2:A2X:A2Y,H4:H4X:H4Y;Tot
alHostages:H1:H1X:H1Y:100,H2:H2X:H2Y:70,H3:H3X:H3Y:30,H4:H4X:H4Y:100;Tx,Ty;Host
agesSaved:H2,H3;Killed:H1,A3;Pad:SP1X,SP1Y,FP1X,FP2X;Pill:L1X,L1Y;CarryNumber:2";
Nx, Ny represent Neo’s coordinates (that is affected by moving up down left or right); 
NeoDamage is how much damage Neo has sustained so far (that is affected by taking pills or killing agents);
Carried is the list of Hostages Neo is carrying in the current state(that is affected once Neo carry or drop somone);
TotalAgents is the list of agents where each element separated by ‘:’ is the agent’s name, their X coordinate,
and their Y coordinate (that is affected by killing someone or when a hostage dies); 
TotalHostages is the list of hostages where each element separated by ‘:’ is the hostage’s name,
their X coordinate, their Y coordinate, and their damage (and their damage are affected after every action and their
places are effected if they are carried); Tx, Ty represent the Telephone Booth’s coordinates; HostagesSaved is the 
list of hostages that Neo successfully carried to the telephone booth; Killed is the list of agents that Neo killed;
Pad is the list of launching pads in the format of Starting Pad X coordinate, Starting Pad Y
coordinate, Finishing Pad X coordinate, and Finishing Pad Y coordinate; Pill is the list of pills
in the format of Pill1 X coordinate, Pill1 Y coordinate (that is affected if Neo took pill); CarryNumber is the number of
hostages Neo can carry at once. state is initially set to “”.
parent is a Node representing the previous Node which the current Node was derived from.
In the case that the current Node is the initial node the search problem starts off with, the
parent variable is set to null.
operator is a String representing the operation the current node was derived from. In the
case that the current Node is the initial node the search problem starts off with, the
operator variable is set to “”. operator can have any value from {up, down, left, right, carry,
drop, takePill, kill, fly}.
depth is an integer representing how many expansions it took from the initial node to reach
the current node. In the case that the current Node is the initial node the search problem
starts off with, the depth variable is set to 0.
cost is an integer representing the cost of reaching the current node from the initial node.
The cost is added cumulatively from parent to child and its value is determined according
to the operator. In the case that the current Node is the initial node the search problem
starts off with, the cost variable is set to 0.
up:cost+10,down:cost+10,left:cost+10,right:cost+10,carry:cost-10,drop:-10,t
akePill:-10,kill:+10,fly:+10";
path is a String representing the path followed from the initial node to get to the current
node. The path is a list of operator values. In the case that the current Node is the initial
node the search problem starts off with, the path variable is set to “”.
H1 is an integer representing the calculation of h(n) according to the first heuristic function,
where h is the function and n is the current node.
H2 is an integer representing the calculation of h(n) according to the second heuristic
function, where h is the function and n is the current node.
SumAStar1 is an integer representing the calculation of h(n) + g(n) where h is the first
heuristic function, n is the current node, and g is the cost of the current node.
SumAStar2 is an integer representing the calculation of h(n) + g(n) where h is the second
heuristic function, n is the current node, and g is the cost of the current node.

#A discussion of your implementation of the Matrix problem.
The Matrix ADT has six variables:

1. Operators
2. M
3. N
4. gridCoordinates
5. stateSet
6. goalStatePath
Operators is a String[] defined as
{“up”,”down”,”left”,”right”,”carry”,”drop”,”takePill”,”kill”,”fly”}
M is a randomly generated int with a value from 5 to 15 inclusive. It represents the number
of rows in our matrix.
N is a randomly generated int with a value from 5 to 15 inclusive. It represents the number
of columns in our matrix.
gridCoordinates is a boolean[][] with size M*N where each element is a representation of
whether or not the element of the same position in the matrix is occupied.
stateSet is a HashSet<String> contains all of the states that Neo has been in previously.
This variable is checked later on in the search methods to make sure no states are repeated
needlessly.
goalStatePath is an ArrayList<String> that contains the states leading up to the final goal
state, starting with the initial state from the first grid.
The Matrix is implemented as an ADT and is divided into 3 parts: Global Variables, Methods,
and Helpers. The global variables are variables that might’ve been needed in more than
one method or helper method. The methods are the required methods from the
description, i.e. genGrid() and solve(String grid, String strategy, boolean visualize), and the
helper methods are the other methods we needed to implement and used later in the two
methods. The most important helper functions are described in detail in the following
point.
  
#A description of the main functions you implemented.
visualize(String s): 
  
  Method with a void return value. This method takes a String s in the
format of a grid, which is:
M,N;C;NeoX,NeoY;TelephoneX,TelehoneY;
AgentX1,AgentY1,...,AgentXk,AgentYk;
PillX1,PillY1,...,PillXg,PillYg;
StartPadX1,StartPadY1,FinishPadX1,FinishPadY1,...,
StartPadXl,StartPadYl,FinishPadXl,FinishPadYl;
HostageX1,HostageY1,HostageDamage1,...,HostageXw,HostageYw,HostageDamagew;
The method will format this string into the proper squared grid format and displays it in the
console.
visualize(ArrayList<String> path, String grid): Method with a void return value. This method
takes an ArrayList of String path and a String grid. The path is filled with node states of
the goal state and its parents during the execution of the search functions. This method will
take each state and parse it into a grid, then recursively call the first visualize function with
the parsed grid.
checkstate(Node n): Method with a boolean return value. Returns true if the input Node n
has a state that was previously explored and is stored in the variable HashSet<String>
stateSet, false otherwise.
randomGenerator(int m, int n): Method with an int return value. Returns a value between
maximum int m and minimum int n, inclusive.
occupy(): Method with a String return value. Returns a potential pair of coordinates that
aren’t already occupied, used in genGrid().
stateSpace(Node n): Method with a Queue<Node> return value. Returns the set of states
reachable from the initial Node n by any sequence of actions.
calculateMove(String action, Node parent): Method with a Node return value. Returns the
Node which results from executing String action on Node parent.
FIFO(Queue<Node> Q, Node n): Method with a void return value. Adds the Node n in the
peek of Queue<Node> Q. Used in depth first searching.
elementAt(Queue<Node> Q, int index): Method with a node return value. Returns the element
in Queue<Node> Q at int index.
removeAt(Queue<Node> Q, int index): Method with a node return value. Returns the element
in Queue<Node> Q at int index and removes it from the queue.
sortQueue(Queue<Node> Qnew, Queue<Node> Q): Method with a Queue<Node> return value.
Sorts Queue<Node> Q recursively and returns the final form of Queue<Node> Qnew.
A discussion of how you implemented the various search algorithms.
breadthFirst(Queue<Node> Q): Method with a String return value. We are given
Queue<Node> Q and we create a new queue that includes the state space nodes. We keep
removing from the state space queue and adding onto our new queue Q. As long as our
current node is NOT the goal state, the children to that node are going to be the next
removed item from the state space queue, and as long as there are children remaining we’ll
add them to our queue.
depthFirst(Queue<Node> Q): Method with a String return value. We are given
Queue<Node> Q and we create a new queue that includes the state space nodes. We keep
removing from the state space queue and adding onto our new queue Q. As long as our
current node is NOT the goal state, the children to that node are going to be the next
removed item from the state space queue, and as long as there are children remaining we’ll
add them to our queue. However, in Depth-First the children are added to the beginning of
the queue, using the FIFO(First In First Out) function.
iterativeDeepeningSearch(Queue<Node> Q): Method with a String return value. We are given
Queue<Node> Q and we create a new queue that includes the state space nodes. We keep
removing from the state space queue and adding onto our new queue Q. As long as our
current node is NOT the goal state and we have not reached our depth limit set, the
children to that node are going to be the next removed item from the state space queue,
and as long as there are children remaining we’ll add them to our queue and increase the
limit by one then repeat the search.
uniformCostSearch(Queue<Node> Q): Method with a String return value. We are given
Queue<Node> Q and we create a new queue that includes the state space nodes. We keep
removing from the state space queue and adding onto our new queue Q. As long as our
current node is NOT the goal state, the children to that node are going to be the next
removed item from the state space queue, and as long as there are children remaining we’ll
add them to our queue. Then, that queue is sorted in terms of the costs of each node,
afterwards this is repeated again with the next child and the sorting occurs again.
greedySearchOne(Queue<Node> Q): Method with a String return value. We are given
Queue<Node> Q and we create a new queue that includes the state space nodes. We keep
removing from the state space queue and adding onto our new queue Q. As long as our
current node is NOT the goal state, the children to that node are going to be the next
removed item from the state space queue, and as long as there are children remaining we’ll
add them to our queue. Then, that queue is sorted in terms of the first heuristic cost of
each node, afterwards this is repeated again with the next child and the sorting occurs
again.
greedySearchTwo(Queue<Node> Q): Method with a String return value. We are given
Queue<Node> Q and we create a new queue that includes the state space nodes. We keep
removing from the state space queue and adding onto our new queue Q. As long as our
current node is NOT the goal state, the children to that node are going to be the next
removed item from the state space queue, and as long as there are children remaining we’ll
add them to our queue. Then, that queue is sorted in terms of the second heuristic cost of
each node, afterwards this is repeated again with the next child and the sorting occurs
again.
greedySortQueue1(Queue<Node> Qnew, Queue<Node> Q): Method with a Queue<Node> return
value. Sorts Queue<Node> Q recursively based on the first heuristic cost H1 of each Node in
the Queue and returns the final form of Queue<Node> Qnew. Nodes are sorted in ascending
order.
greedySortQueue2(Queue<Node> Qnew, Queue<Node> Q): Method with a Queue<Node> return
value. Sorts Queue<Node> Q recursively based on the second heuristic cost H2 of each Node
in the Queue and returns the final form of Queue<Node> Qnew. Nodes are sorted in
ascending order.
aStarOne(Queue<Node> Q): Method with a String return value. We are given Queue<Node>
Q and we create a new queue that includes the state space nodes. We keep removing from
the state space queue and adding onto our new queue Q. As long as our current node is
NOT the goal state, the children to that node are going to be the next removed item from
the state space queue, and as long as there are children remaining we’ll add them to our
queue. Then, that queue is sorted in terms of the first heuristic cost of each node + its cost,
afterwards this is repeated again with the next child and the sorting occurs again.
aStarTwo(Queue<Node> Q): Method with a String return value. We are given Queue<Node>
Q and we create a new queue that includes the state space nodes. We keep removing from
the state space queue and adding onto our new queue Q. As long as our current node is
NOT the goal state, the children to that node are going to be the next removed item from
the state space queue, and as long as there are children remaining we’ll add them to our
queue. Then, that queue is sorted in terms of the second heuristic cost of each node + its
cost, afterwards this is repeated again with the next child and the sorting occurs again.
aStarSortQueue1(Queue<Node> Qnew, Queue<Node> Q): Method with a Queue<Node> return
value. Sorts Queue<Node> Q recursively based on the cost + the first heuristic cost H1 of
each Node in the Queue and returns the final form of Queue<Node> Qnew. Nodes are sorted
in ascending order.
aStarSortQueue2(Queue<Node> Qnew, Queue<Node> Q): Method with a Queue<Node> return
value. Sorts Queue<Node> Q recursively based on the cost + the second heuristic cost H2 of
each Node in the Queue and returns the final form of Queue<Node> Qnew. Nodes are sorted
in ascending order.
A discussion of the heuristic functions you employed and, in the case of A*, an
argument for their admissibility.
We have 2 heuristic numbers for each node that depends on the state for the greedy
search methods. We have 2 sum numbers that are representing the summation of the heuristic
numbers and the costs for the A* search methods. We have the functions Calculateheuristic1
and Calculateheuristic2, which are called in the calculateMove function, which declares the
node as well as pathcost.
Calculateheuristic1 takes the node and calculates the distance between Neo’s current
position and the Telephone Booth’s position and it checks if all the hostages are saved or
killed or both by summing the carried ones + the saved ones + the killed ones and
subtracting them from the total hostages number, then add that value to the distance
between Neo from the Telephone Booth. The goal state will have that distance 0 + 0 as all
hostages should be killed or saved and Neo should be at the telephone booth.
Calculateheuristic2 sums all the distances of the hostages that have lower damage than 100
which means they are alive, not killed or dead and can be carried or just stay at their places,
so we are calculating its distance to the telephone booth. The goal state will have zeros for
all the hostages that have damage lower than 100 as they would be saved or we won't
calculate the distances of the dead ones or the killed ones or the converted ones so it will
be 0 as all of them should be at the telephone booth.
A comparison of the performance of the implemented search strategies on your
running examples in terms of completeness, optimality, RAM usage, CPU
utilization, and the number of expanded nodes. You should comment on the
differences in the RAM usage, CPU utilization, and the number of expanded nodes
between the implemented search strategies.
Text
