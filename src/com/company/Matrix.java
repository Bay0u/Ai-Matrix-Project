package com.company;
import java.util.*;
import static java.lang.Math.sqrt;
class Node {
    public String state;
    // = "Nx,Ny;NeoDamage;Carried:H1,H2;TotalAgents:A1:A1X:A1Y,A2,H4;"
    // + "TotalHostages:H1:H1X:H1Y:100,H2:70,H3:30,H4:100,H6:50,H5:10;Tx,Ty;"
    //+ "HostagesSaved:H2,H3;Killed:H1,A3;PAD:SP1X,SP1Y,FP1X,FP2X;PILL:L1X,L1Y;CarryNumber";
    public Node parent;
    public int H1;
    public int H2;
    public int SumAStar1;
    public int SumAStar2;
    public String operator;// up:cost+10,down:cost+10,left:cost+10,right:cost+10,carry:cost-10,drop:-10,takePill:-10,kill:+10,fly:+10";
    public int depth;
    public int cost;
    public String path;// = parent.operator + operator;

    Node() {
        this.state = "";
        this.parent = null;
        this.operator = "";
        this.depth = 0;
        this.cost = 0;
        this.path = operator;
    }

    Node(String state, Node parent, String operator, int depth, int cost, String path) {
        this.state = state;
        this.parent = parent;
        this.operator = operator;
        this.depth = depth;
        this.cost = cost;
        if (parent != null && parent.path != null)
            this.path = parent.path + "," + operator;
        else
            this.path = operator;
    }
    // Example Input Grid:
    // 5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;
    // 0,0,30,3,0,80,4,4,80
}
class searchProblems {
    // Operators
    String Operators = "up,down,left,right,carry,drop,takePill,kill,fly";
    // initial state
    public Node InitialState;

    // goal test
    // path cost
    public boolean isNeoinHome(String[] stringarray) {
        String Neoplace = stringarray[0];
        String Telephone = stringarray[5];
        if (Neoplace == Telephone) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNeoDead(String[] stringarray) {
        if (stringarray[1] == "100") {
            return true;
        } else {
            return false;
        }
    }

    public boolean areHostagesSaved(String[] stringarray) {
        String Hostages = stringarray[4];// [H1:30,H2:50,H3:10]
        String[] HostageArray = Hostages.split(",");//
        String Saved = stringarray[6];// HostagesSaved:H1,H2,H3
        String[] SavedHostageArray = Saved.split(",");// H1,H2,H3
        int numberOfKilledHostages = 0;
        for (int i = 0; i < HostageArray.length; i++) {
            String[] Hostage = HostageArray[i].split(":");// [H1][30]
            if (Hostage[1] == "100") { // CHECKS THE Damage
                String myHostage = Hostage[0];
                String Agents = stringarray[3];// TotalAgents:A1,A2,H4
                String[] agent = Agents.split(",");
                String Killed = stringarray[7];// Killed:H1,A3
                String[] killed = Killed.split(",");
                for (int j = 0; j < agent.length; j++) {
                    if (myHostage == agent[j]) {
                        return false; // as there is a converted hostage is still alive not saved or killed
                    }
                }
                for (int k = 0; k < agent.length; k++) {
                    if (myHostage == killed[k]) {
                        numberOfKilledHostages++; // as there is a converted hostage is killed
                    }
                }
            }
        }
        if (SavedHostageArray.length + numberOfKilledHostages == HostageArray.length /*
         * && Hostages.length()==0
         */) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isGoalState(Node node) {
        // chick place neo x,y = telephone
        // chick total number of Hostages == [saved Hostages + Killed Hostages whose
        // changed to agent]
        // saved hostage ignored carried and Hostages info in state
        // Chick neo hp isn't 0
        String[] stringarray = node.state.split(";");
        if (isNeoinHome(stringarray) && areHostagesSaved(stringarray) && !isNeoDead(stringarray)) {
            node.H1 = 0;
            node.H2 = 0;
            return true;
        }
        return false;
    }

    public static void pathCost(Node node) {
        // operator
        // up:cost+10,down:cost+10,left:cost+10,right:cost+10,carry:cost-20,drop:-20,takePill:-20,kill:+20,fly:+10";
        // hostage died : cost+10
        String[] stringarray = node.state.split(";");
        switch (node.operator) {
            case "up":
                node.cost = node.cost + 10;
                break;
            case "down":
                node.cost = node.cost + 10;
                break;
            case "left":
                node.cost = node.cost + 10;
                break;
            case "right":
                node.cost = node.cost + 10;
                break;
            case "carry":
                if (stringarray[0] != stringarray[5]) // carry shouldn't be in the telphone
                    node.cost = node.cost - 20;
                else
                    node.cost = node.cost + 10;
                break;
            case "drop":
                if (stringarray[0] == stringarray[5]) // drop should be in the telphone only
                    node.cost = node.cost - 20;
                else
                    node.cost = node.cost + 10;
                break;
            case "takePill":
                node.cost = node.cost - 20;
                break;
            case "kill":
                node.cost = node.cost + (20 * node.depth);
                break;
            case "fly":
                node.cost = node.cost + 10;
                break;
            default:
                break;
        }
    }

    public static void Calculateheuristic1(Node n) {
        String[] state = n.state.split(";");
        String[] NeoCord = state[0].split(",");// [x,y]
        String[] Hostages = state[4].split(",");
        String[] TB = state[5].split(",");
        String[] CarriedHostages = state[2].split(",");
        String[] SavedHostages = state[6].split(",");
        String[] killed = state[7].split(",");
        int NeoX = Integer.parseInt(NeoCord[0]);
        int NeoY = Integer.parseInt(NeoCord[1]);
        int TBX = Integer.parseInt(TB[0]);
        int TBY = Integer.parseInt(TB[1]);
        //int numberoFSaved = (state[6].split(",")).length;
        int distance = (int) sqrt((NeoY - NeoX) * (NeoY - NeoX) + (TBX - TBY) * (TBX - TBY));
        int numberOfKilledHostages = 0;
        for (int i = 0; i < Hostages.length; i++) {
            String[] Hostage = Hostages[i].split(":");// [H1][30]
            if (Hostage[1] == "100") { // CHECKS THE Damage
                String myHostage = Hostage[0];
                for (int k = 0; k < killed.length; k++) {
                    if (myHostage == killed[k]) {
                        numberOfKilledHostages++; // as there is a converted hostage is killed
                    }
                }
            }
        }
        n.H1 = distance + (Hostages.length - (CarriedHostages.length + SavedHostages.length + numberOfKilledHostages));
        n.SumAStar1 = n.H1 + n.cost;

    }

    public static void Calculateheuristic2(Node n) {
        String[] state = n.state.split(";");
        String[] Hostages = state[4].split(",");
        String[] TB = state[5].split(",");
        int TBX = Integer.parseInt(TB[0]);
        int TBY = Integer.parseInt(TB[1]);
        for (int i = 0; i < Hostages.length; i++) {// H1:H1X:H1Y:H1D,H2....
            String[] HostagesHelper = Hostages[i].split(":");
            int HX = Integer.parseInt(HostagesHelper[1]);
            int HY = Integer.parseInt(HostagesHelper[2]);
            if (HostagesHelper[3] != "100") {
                int distance = (int) sqrt((TBY - TBX) * (TBY - TBX) + (HX - HY) * (HX - HY));
                n.H2 = n.H2 + distance;
            }
        }
        n.SumAStar2 = n.H2 + n.cost;
    }
}
class Matrix {

    // GLOBAL VARIABLES
    String[] Operators = "up,down,left,right,carry,drop,takePill,kill,fly".split(",");
    static int M = randomGenerator(5, 15); // width of grid
    static int N = randomGenerator(5, 15); // height of grid
    static boolean[][] gridCoordinates = new boolean[M][N];

    // METHODS
    public static void genGrid() {

        int C = randomGenerator(1, 4); // maximum number of members Neo can carry at a time

        String str = occupy();
        int NeoX = Integer.parseInt(str.split(";")[0]); // Neo's X coordinate
        int NeoY = Integer.parseInt(str.split(";")[1]); // Neo's Y coordinate

        str = occupy();
        int TelephoneX = Integer.parseInt(str.split(";")[0]); // Telephone X coordinate
        int TelephoneY = Integer.parseInt(str.split(";")[1]); // Telephone Y coordinate

        int w = randomGenerator(3, 10); // number of hostages
        int[] HostageX = new int[w]; // size = w
        int[] HostageY = new int[w]; // size = w
        for (int i = 0; i < w; i++) {
            str = occupy();
            HostageX[i] = Integer.parseInt(str.split(";")[0]);
            HostageY[i] = Integer.parseInt(str.split(";")[1]);
        }

        int[] HostageDamage = new int[w]; // size = w
        for (int i = 0; i < w; i++)
            HostageDamage[i] = randomGenerator(1, 99);

        int g = randomGenerator(1, w); // number of pills
        int[] PillX = new int[g]; // size = g
        int[] PillY = new int[g]; // size = g
        for (int i = 0; i < g; i++) {
            str = occupy();
            PillX[i] = Integer.parseInt(str.split(";")[0]);
            PillY[i] = Integer.parseInt(str.split(";")[1]);
        }

        int emptySlots = 0;
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                if (gridCoordinates[i][j] == false)
                    emptySlots++;

        int l = randomGenerator(1, (emptySlots / 10) * 2); // number of launch pads
        int[] StartPadX = new int[l]; // size = l
        int[] StartPadY = new int[l]; // size = l
        int[] FinishPadX = new int[l]; // size = l
        int[] FinishPadY = new int[l]; // size = l
        String[] Pairs = new String[l];
        for (int i = 0; i < l; i++) {
            str = occupy();
            StartPadX[i] = Integer.parseInt(str.split(";")[0]);
            StartPadY[i] = Integer.parseInt(str.split(";")[1]);
            str = occupy();
            FinishPadX[i] = Integer.parseInt(str.split(";")[0]);
            FinishPadY[i] = Integer.parseInt(str.split(";")[1]);
            // System.out.println(StartPadX[i]+";"+StartPadY[i]+";"+FinishPadX[i]+";"+FinishPadY[i]+";");
            Pairs[i] = "Pair " + i + ": " + StartPadX[i] + "," + StartPadY[i] + ";" + FinishPadX[i] + ","
                    + FinishPadY[i];
        }

        emptySlots = 0;
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                if (gridCoordinates[i][j] == false)
                    emptySlots++;

        int k = randomGenerator(1, emptySlots / 2); // number of agents
        int[] AgentX = new int[k]; // size = k
        int[] AgentY = new int[k]; // size = k
        for (int i = 0; i < k; i++) {
            str = occupy();
            AgentX[i] = Integer.parseInt(str.split(";")[0]);
            AgentY[i] = Integer.parseInt(str.split(";")[1]);
        }

        String grid = M + "," + N + ";" + C + ";" + NeoX + "," + NeoY + ";" + TelephoneX + "," + TelephoneY + ";";
        for (int i = 0; i < k; i++)
            grid += AgentX[i] + "," + AgentY[i] + ",";
        grid = grid.substring(0, grid.length() - 1);
        grid += ";";

        for (int i = 0; i < g; i++)
            grid += PillX[i] + "," + PillY[i] + ",";
        grid = grid.substring(0, grid.length() - 1);
        grid += ";";

        for (int i = 0; i < l; i++)
            grid += StartPadX[i] + "," + StartPadY[i] + "," + FinishPadX[i] + "," + FinishPadY[i] + ",";
        for (int i = 0; i < l; i++) // Remove?
            grid += FinishPadX[i] + "," + FinishPadY[i] + "," + StartPadX[i] + "," + StartPadY[i] + ",";
        grid = grid.substring(0, grid.length() - 1);
        grid += ";";

        for (int i = 0; i < w; i++)
            grid += HostageX[i] + "," + HostageY[i] + "," + HostageDamage[i] + ",";
        grid = grid.substring(0, grid.length() - 1);

        // System.out.println("String: " + grid);
        // visualize(grid);
    }

    public static String solve(String grid, String strategy, boolean visualize) {
        String[] OurGrid = grid.split(";");
        searchProblems p = new searchProblems();
        Node n = new Node();
        String hostages = "";
        String agents = "";
        String pads = "";
        String pills = "";
        Queue<Node> Q = new LinkedList<>();

        // String GridCord[] = OurGrid[0].split(",");
        // String M = GridCord[0];
        // String N = GridCord[1];
        int CarryNumber = Integer.parseInt(OurGrid[1]);
        // String Carried[] = OurGrid[2].split(",");
        for (int i = 0; i < OurGrid[7].length(); i++) {
            String[] Hostages = OurGrid[7].split(",");
            hostages = "H" + i + ":" + Hostages[i] + ":" + Hostages[i + 1] + ":" + Hostages[i + 2] + ",";
        }
        hostages = hostages.substring(0, hostages.length() - 1);
        for (int i = 0; i < OurGrid[4].length(); i++) {
            String[] Agents = OurGrid[4].split(",");
            agents = "A" + i + ":" + Agents[i] + ":" + Agents[i + 1] + ",";
        }
        agents = agents.substring(0, agents.length() - 1);
        for (int i = 0; i < OurGrid[6].length(); i++) {
            String[] Pads = OurGrid[6].split(",");
            pads = "SP" + i + ":" + Pads[i] + ":" + Pads[i + 1] + "," + "FP" + i + ":" + Pads[i + 2] + ":" + Pads[i + 3]
                    + ",";
        }
        pads = pads.substring(0, pads.length() - 1);
        for (int i = 0; i < OurGrid[5].length(); i++) {
            String[] Pills = OurGrid[5].split(",");
            pills = "L" + i + ":" + Pills[i] + ":" + Pills[i + 1] + ",";
        }
        pills = pills.substring(0, pills.length() - 1);
        n.state = OurGrid[2] + ";0;;" + agents + ";" + hostages + ";" + OurGrid[3] + ";" + ";" + pads + ";" + pills
                + ";" + CarryNumber;
        searchProblems.Calculateheuristic1(n);
        searchProblems.Calculateheuristic2(n);
        p.InitialState = n;
        Q.add(n);

        String out = "";
        switch (strategy) {
        case "BF":
            out = breadthFirst(Q);
        case "DF":
            out = depthFirst(Q);
        case "ID":
            out = iterativeDeepeningSearch(Q);
        case "UC":
            out = uniformCostSearch(Q);
        case "GR1":
            out = greedySearchOne(Q);
        case "GR2":
            out = greedySearchTwo(Q);
        case "AS1":
            out = aStarOne(Q);
        case "AS2":
            out = aStarTwo(Q);
        default:
            out = "";
        }

        if (visualize){
            String[] path = out.split(";")[0].split(",");
            
        }

        return out;
    }

    // HELPERS
    public static int randomGenerator(int m, int n) {
        return m + (int) (Math.random() * (n + 1));
    }

    public static String occupy() {
        boolean occupied = true;
        String s = "";
        while (occupied) {
            int potentialX = randomGenerator(0, M - 1);
            int potentialY = randomGenerator(0, N - 1);
            if (!gridCoordinates[potentialX][potentialY]) {
                gridCoordinates[potentialX][potentialY] = true;
                s += potentialX + ";" + potentialY;
                occupied = false;
            }
        }
        return s;
    }

    public static void visualize(String s) {
        String[] table = s.split(";");

        // GRID DIMENSIONS
        int M = Integer.parseInt(table[0].split(",")[0]);
        int N = Integer.parseInt(table[0].split(",")[1]);
        String[][] grid = new String[M][N];

        // CARRY CAPACITY
        // int C = Integer.parseInt(table[1]);

        // NEO
        int NeoX = Integer.parseInt(table[2].split(",")[0]);
        int NeoY = Integer.parseInt(table[2].split(",")[1]);
        grid[NeoX][NeoY] = "Neo";

        // TELEPHONE BOOTH
        int TelephoneX = Integer.parseInt(table[3].split(",")[0]);
        int TelephoneY = Integer.parseInt(table[3].split(",")[1]);
        grid[TelephoneX][TelephoneY] = "TB";

        // AGENTS
        for (int i = 0; i < table[4].split(",").length; i += 2)
            grid[Integer.parseInt(table[4].split(",")[i])][Integer.parseInt(table[4].split(",")[i + 1])] = "A";

        // PILLS
        for (int i = 0; i < table[5].split(",").length; i += 2)
            grid[Integer.parseInt(table[5].split(",")[i])][Integer.parseInt(table[5].split(",")[i + 1])] = "P";

        // LAUNCH PADS
        for (int i = 0; i < table[6].split(",").length; i += 4) {
            String[] pads = table[6].split(",");
            grid[Integer.parseInt(pads[i])][Integer.parseInt(pads[i + 1])] = "Pad (" + pads[i + 2] + "," + pads[i + 3]
                    + ")";
            grid[Integer.parseInt(pads[i + 2])][Integer.parseInt(pads[i + 3])] = "Pad (" + pads[i] + "," + pads[i + 1]
                    + ")";
        }

        // HOSTAGES
        for (int i = 0; i < table[7].split(",").length; i += 3) {
            String[] hostages = table[7].split(",");
            grid[Integer.parseInt(hostages[i])][Integer.parseInt(hostages[i + 1])] = "H (" + hostages[i + 2] + ")";
        }

        //System.out.print(Arrays.deepToString(grid));
        for (int i = 0; i < grid.length; i++) {
        System.out.println();
        for (int j = 0; j < grid[0].length; j++)
        System.out.print(grid[i][j] + ", ");
        }
    }

    public static Queue<Node> stateSpace(Node n) {
        Queue<Node> Children = new LinkedList<>();
        String[] NodeList = n.state.split(";");
        String[] NeoCord = NodeList[0].split(",");// [x,y]
        String[] Agents = NodeList[3].split(",");
        String[] Hostages = NodeList[4].split(",");
        String NeoX = NeoCord[0];
        String NeoY = NeoCord[1];
        String[] Pads = NodeList[8].split(",");
        String[] Pills = NodeList[9].split(",");
        String[] CarriedHostages = NodeList[2].split(",");
        // String[] SavedHostages = NodeList[6].split(",");
        // int NeoDamage = Integer.parseInt(NodeList[1]);
        int CarryNumber = Integer.parseInt(NodeList[10]);

        if (NeoX != "0") { // UP
            for (int i = 0; i < Agents.length; i++) {
                String[] agentHelper = Agents[i].split(":");
                if ((Integer.parseInt(NeoX) - 1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2])
                    Children.add(calculateMove("kill", n));
            }
            Children.add(calculateMove("up", n));
        }
        if (Integer.parseInt(NeoY) != N) { // DOWN
            for (int i = 0; i < Agents.length; i++) {
                String[] agentHelper = Agents[i].split(":");
                if ((Integer.parseInt(NeoX) + 1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2])
                    Children.add(calculateMove("kill", n));
            }
            Children.add(calculateMove("down", n));
        }
        if (NeoY != "0") { // LEFT
            for (int i = 0; i < Agents.length; i++) {
                String[] agentHelper = Agents[i].split(":");
                if (NeoX == agentHelper[1] && (Integer.parseInt(NeoY) - 1) == Integer.parseInt(agentHelper[2]))
                    Children.add(calculateMove("kill", n));
            }
            Children.add(calculateMove("left", n));
        }
        if (Integer.parseInt(NeoX) != M) { // RIGHT
            for (int i = 0; i < Agents.length; i++) {
                String[] agentHelper = Agents[i].split(":");
                if (NeoX == agentHelper[1] && (Integer.parseInt(NeoY) + 1) == Integer.parseInt(agentHelper[2]))
                    Children.add(calculateMove("kill", n));
            }
            Children.add(calculateMove("right", n));
        }

        for (int i = 0; i < Hostages.length; i++) { // CARRY
            String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
            if (NeoX == HostagesHelper[1] && NeoY == HostagesHelper[2])
                if (CarryNumber > CarriedHostages.length)
                    Children.add(calculateMove("carry", n));
        }

        if (NodeList[0] == NodeList[5]) { // DROP
            if (CarriedHostages.length != 0)
                Children.add(calculateMove("drop", n));
        }

        for (int i = 0; i < Pills.length; i = i + 2) // takePill
            if (NeoX == Pills[i] && NeoY == Pills[i + 1])
                if (Integer.parseInt(NodeList[1]) > 50)
                    Children.add(calculateMove("takePill", n));

        for (int i = 0; i < (Pads.length / 4); i = i + 4) { // [sp1x,sp1y,fp1x,fp1y]
            if ((NeoX == Pads[0] && NeoY == Pads[1]) || (NeoX == Pads[2] && NeoY == Pads[3]))
                Children.add(calculateMove("fly", n));
        }
        return Children;
    }

    public static Node calculateMove(String action, Node parent) {//
        Node child = new Node("", parent, "", parent.depth + 1, 0, ""); // STATE PARENT OPERATOR DEPTH COST PATH
        // SPLITTING PARENT STATE
        String[] NodeList = parent.state.split(";");
        String[] NeoCord = NodeList[0].split(",");// [x,y]
        String[] Agents = NodeList[3].split(",");
        String[] Hostages = NodeList[4].split(",");
        String NeoX = NeoCord[0];
        String NeoY = NeoCord[1];
        String[] Pads = NodeList[8].split(",");
        String[] Pills = NodeList[9].split(",");
        String[] CarriedHostages = NodeList[2].split(",");
        String[] SavedHostages = NodeList[6].split(",");
        int NeoDamage = Integer.parseInt(NodeList[1]);
        // int CarryNumber = Integer.parseInt(NodeList[10]);

        String NeoPlace = NodeList[0];
        String Killed = NodeList[7];
        String TotalAgents = NodeList[3];
        String NewCarriedHostages = NodeList[2];
        String NewSavedHostages = NodeList[6];
        String NewPills = NodeList[9];

        switch (action) {
        // WE LOOPING IN CARRIED HOSTAGES TO UPDATE THEIR POSITIONS IN CASE WE MOVE UP
        // DOWN LEFT RIGHT
        case "up":
            NeoPlace = (Integer.parseInt(NeoX) - 1) + "," + NeoY;
            if (CarriedHostages.length != 0) {
                for (int i = 0; i < Hostages.length; i++) {// H1:H1X:H1Y:H1D,H2....
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length; j++) {
                        if (HostagesHelper[0] == CarriedHostages[j])
                            HostagesHelper[1] = String.valueOf(Integer.parseInt(HostagesHelper[1]) - 1);
                    }
                    String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                            + HostagesHelper[3];
                    Hostages[i] = hostage;
                }
            }
            child.operator = "up";
            break;
        case "down":
            NeoPlace = (Integer.parseInt(NeoX) + 1) + "," + NeoY;
            if (CarriedHostages.length != 0) {
                for (int i = 0; i < Hostages.length; i++) {
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length; j++) {
                        if (HostagesHelper[0] == CarriedHostages[j])
                            HostagesHelper[1] = String.valueOf(Integer.parseInt(HostagesHelper[1]) + 1);
                    }
                    String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                            + HostagesHelper[3];
                    Hostages[i] = hostage;
                }
            }
            child.operator = "down";
            break;
        case "left":
            NeoPlace = NeoX + "," + (Integer.parseInt(NeoY) - 1);
            if (CarriedHostages.length != 0) {
                for (int i = 0; i < Hostages.length; i++) {
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length; j++) {
                        if (HostagesHelper[0] == CarriedHostages[j])
                            HostagesHelper[2] = String.valueOf(Integer.parseInt(HostagesHelper[2]) - 1);
                    }
                    String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                            + HostagesHelper[3];
                    Hostages[i] = hostage;
                }
            }
            child.operator = "left";
            break;
        case "right":
            NeoPlace = NeoX + "," + (Integer.parseInt(NeoY) + 1);
            if (CarriedHostages.length != 0) {
                for (int i = 0; i < Hostages.length; i++) {
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length; j++) {
                        if (HostagesHelper[0] == CarriedHostages[j])
                            HostagesHelper[2] = String.valueOf(Integer.parseInt(HostagesHelper[2]) + 1);
                    }
                    String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                            + HostagesHelper[3];
                    Hostages[i] = hostage;
                }
            }
            child.operator = "right";
            break;
        case "carry":
            for (int i = 0; i < Hostages.length; i++) {
                String[] HostagesHelper = Hostages[i].split(":");
                if (NeoX == HostagesHelper[1] && NeoY == HostagesHelper[2]) {
                    if (NewCarriedHostages.length() != 0)
                        NewCarriedHostages += ",";
                    NewCarriedHostages += HostagesHelper[0];
                }
            }
            child.operator = "carry";
            break;
        case "drop":
            for (int i = 0; i < CarriedHostages.length; i++) {
                SavedHostages[SavedHostages.length] = CarriedHostages[i];
                CarriedHostages[i] = "";
                NewSavedHostages += CarriedHostages[i];
            }
            NewCarriedHostages = "";
            child.operator = "drop";
            break;
        case "takePill":
            for (int i = 0; i < Pills.length; i = i + 2)
                if (NeoDamage > 50) {
                    Pills[i] = "";
                    Pills[i + 1] = "";
                    // neo damage and hostages damage resets
                    NeoDamage = NeoDamage - 20;
                    if (NeoDamage < 0)
                        NeoDamage = 0;
                    for (int j = 0; j < (Hostages.length / 4); j++) {
                        boolean flag = false;
                        String[] HostagesHelper = Hostages[j].split(":");
                        if (Integer.parseInt(HostagesHelper[3]) >= 100) {
                            for (int k = 0; k < SavedHostages.length; k++)
                                if (SavedHostages[k] == HostagesHelper[0])
                                    flag = true;
                            if (!flag) {
                                HostagesHelper[3] = (Integer.parseInt(HostagesHelper[3]) - 22) + "";
                                if (Integer.parseInt(HostagesHelper[3]) < 0)
                                    HostagesHelper[3] = 0 + "";
                            }

                        }
                    }
                } else
                    NewPills += Pills[i] + "," + Pills[i + 1] + ",";
            NewPills = NewPills.substring(0, NewPills.length() - 1);
            child.operator = "takePill";
            break;
        case "fly":
            for (int i = 0; i < (Pads.length / 4); i = i + 4) { // [sp1x,sp1y,fp1x,fp1y]
                if (NeoX == Pads[0] && NeoY == Pads[1]) {
                    NeoPlace = Pads[2] + "," + Pads[3];
                }
                if (NeoX == Pads[2] && NeoY == Pads[3]) {
                    NeoPlace = Pads[0] + "," + Pads[1];
                }
            }
            NeoPlace = (Integer.parseInt(NeoX) - 1) + "," + NeoY;
            child.operator = "fly";
            break;
        case "kill":
            for (int i = 0; i < Agents.length; i++) {
                String[] agentHelper = Agents[i].split(":");
                if ((Integer.parseInt(NeoX) - 1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2]
                        && NeoDamage <= 100) {
                    if (Killed.length() != 0)
                        Killed += ",";
                    Killed += agentHelper[0];
                    NeoDamage += 20;
                }
                if ((Integer.parseInt(NeoX) + 1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2]
                        && NeoDamage <= 100) {
                    if (Killed.length() != 0)
                        Killed += ",";
                    Killed += agentHelper[0];
                    NeoDamage += 20;
                }
                if (NeoX == agentHelper[1] && (Integer.parseInt(NeoY) - 1) == Integer.parseInt(agentHelper[2])
                        && NeoDamage <= 100) {
                    if (Killed.length() != 0)
                        Killed += ",";
                    Killed += agentHelper[0];
                    NeoDamage += 20;
                }
                if (NeoX == agentHelper[1] && (Integer.parseInt(NeoY) + 1) == Integer.parseInt(agentHelper[2])
                        && NeoDamage <= 100) {
                    if (Killed.length() != 0)
                        Killed += ",";
                    Killed += agentHelper[0];
                    NeoDamage += 20;
                } else
                    TotalAgents = agentHelper[i] + ":" + agentHelper[i + 1] + ":" + agentHelper[i + 2] + ",";
            }
            child.operator = "kill";
            break;
        default:
            break;
        }

        for (int i = 0; i < Hostages.length; i++) { // EveryTick
            String[] HostagesHelper = Hostages[i].split(":");
            HostagesHelper[3] = String.valueOf(Integer.parseInt(HostagesHelper[3]) + 2);
            if (Integer.parseInt(HostagesHelper[3]) >= 100) {
                if (TotalAgents.length() != 0)
                    TotalAgents += ",";
                TotalAgents += HostagesHelper[0];
                child.cost += 10; // Penalty if any hostage died
            }
            String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                    + HostagesHelper[3];
            Hostages[i] = hostage;
        }

        String newState = NeoPlace + ";" + NeoDamage + ";" + NewCarriedHostages + ";" + TotalAgents + ";";
        for (int i = 0; i < Hostages.length; i++)
            newState += Hostages[i] + ",";
        newState = newState.substring(0, newState.length() - 1);
        newState += ";" + NodeList[5] + ";" + NewSavedHostages + ";" + Killed + ";" + NodeList[8] + ";" + NewPills + ";"
                + NodeList[10];
        child.state = newState;
        searchProblems.pathCost(child);
        searchProblems.Calculateheuristic1(child);
        searchProblems.Calculateheuristic2(child);
        return child;

    }

    public static void FIFO(Queue<Node> Q, Node n) {
        Queue<Node> Q2 = new LinkedList<>();
        Q2.add(n);
        while (!Q.isEmpty())
            Q2.add(Q.remove());
        while (!Q2.isEmpty())
            Q.add(Q2.remove());
    }

    public static Node elementAt(Queue<Node> Q, int index) {
        if (index == 0)
            return Q.peek();
        Queue<Node> Q2 = new LinkedList<>();
        for (int i = 0; i < Q.size(); i++) {
            Q2.add(Q.peek());
            Q.add(Q.remove());
        }
        for (int i = 0; i < Q2.size(); i++) {
            if (i == index)
                return Q2.peek();
            else {
                Q2.add(Q2.remove());
            }
        }
        return new Node();
    }

    public static Node removeAt(Queue<Node> Q, int index) {
        Node removed = new Node();
        if (index < Q.size()) {
            for (int i = 0; i < Q.size() + 1; i++) { // 01234 2 | 12340 23401 3401 4013 0134
                if (i == index) {
                    removed = Q.remove();
                } else {
                    Q.add(Q.remove());
                }
            }
        }
        return removed;
    }

    public static Queue<Node> sortQueue(Queue<Node> Qnew, Queue<Node> Q) {
        if (Q.size() == 1)
            Qnew.add(Q.remove());
        else {
            Node minValue = new Node();
            minValue.cost = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < Q.size(); i++) {
                if (elementAt(Q, i).cost <= minValue.cost) {
                    minValue = elementAt(Q, i);
                    minIndex = i;
                }
            }
            Qnew.add(minValue);
            removeAt(Q, minIndex);
            sortQueue(Qnew, Q);
        }
        return Qnew;
    }

    public static String breadthFirst(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            Queue<Node> children = stateSpace(Q.remove());
            if (!children.isEmpty()) {
                nodes++;
                Q.add(children.remove());
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    public static String depthFirst(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            Queue<Node> children = stateSpace(Q.remove());
            if (!children.isEmpty()) {
                nodes++;
                FIFO(Q, children.remove());
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    public static String iterativeDeepeningSearch(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        int limit = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            while (Q.peek().depth == limit) {
                Queue<Node> children = stateSpace(Q.remove());
                if (!children.isEmpty()) {
                    nodes++;
                    Q.add(children.remove());
                }
            }
            limit++;
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    public static String uniformCostSearch(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            Queue<Node> children = stateSpace(Q.remove());
            if (!children.isEmpty()) {
                nodes++;
                Q.add(children.remove());
                sortQueue(new LinkedList<>(), Q);
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    public static String greedySearchOne(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            Queue<Node> children = stateSpace(Q.remove());
            if (!children.isEmpty()) {
                nodes++;
                Q.add(children.remove());
                greedySortQueue1(new LinkedList<>(), Q);
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    private static Queue<Node> greedySortQueue1(Queue<Node> Qnew, Queue<Node> Q) {
        if (Q.size() == 1)
            Qnew.add(Q.remove());
        else {
            Node minValue = new Node();
            minValue.H1 = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < Q.size(); i++) {
                if (elementAt(Q, i).H1 <= minValue.H1) {
                    minValue = elementAt(Q, i);
                    minIndex = i;
                }
            }
            Qnew.add(minValue);
            removeAt(Q, minIndex);
            greedySortQueue1(Qnew, Q);
        }
        return Qnew;
    }

    public static String greedySearchTwo(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            Queue<Node> children = stateSpace(Q.remove());
            if (!children.isEmpty()) {
                nodes++;
                Q.add(children.remove());
                greedySortQueue2(new LinkedList<>(), Q);
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    private static Queue<Node> greedySortQueue2(Queue<Node> Qnew, Queue<Node> Q) {
        if (Q.size() == 1)
            Qnew.add(Q.remove());
        else {
            Node minValue = new Node();
            minValue.H2 = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < Q.size(); i++) {
                if (elementAt(Q, i).H2 <= minValue.H2) {
                    minValue = elementAt(Q, i);
                    minIndex = i;
                }
            }
            Qnew.add(minValue);
            removeAt(Q, minIndex);
            greedySortQueue2(Qnew, Q);
        }
        return Qnew;
    }

    public static String aStarOne(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            Queue<Node> children = stateSpace(Q.remove());
            if (!children.isEmpty()) {
                nodes++;
                Q.add(children.remove());
                aStarSortQueue1(new LinkedList<>(), Q);
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    public static String aStarTwo(Queue<Node> Q) {
        searchProblems p = new searchProblems();
        Queue<Node> Qnew = stateSpace(Q.remove());
        int nodes = 1;
        while (!Qnew.isEmpty())
            Q.add(Qnew.remove());
        while (!p.isGoalState(Q.peek())) {
            Queue<Node> children = stateSpace(Q.remove());
            if (!children.isEmpty()) {
                nodes++;
                Q.add(children.remove());
                aStarSortQueue2(new LinkedList<>(), Q);
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        int deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;
        int kills = stateAttributes[7].split(",").length;
        return Q.peek().path + ";" + deaths + ";" + kills + ";" + nodes;
    }

    private static Queue<Node> aStarSortQueue1(Queue<Node> Qnew, Queue<Node> Q) {
        if (Q.size() == 1)
            Qnew.add(Q.remove());
        else {
            Node minValue = new Node();
            minValue.SumAStar1 = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < Q.size(); i++) {
                if (elementAt(Q, i).SumAStar1 <= minValue.SumAStar1) {
                    minValue = elementAt(Q, i);
                    minIndex = i;
                }
            }
            Qnew.add(minValue);
            removeAt(Q, minIndex);
            aStarSortQueue1(Qnew, Q);
        }
        return Qnew;
    }

    private static Queue<Node> aStarSortQueue2(Queue<Node> Qnew, Queue<Node> Q) {
        if (Q.size() == 1)
            Qnew.add(Q.remove());
        else {
            Node minValue = new Node();
            minValue.SumAStar2 = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < Q.size(); i++) {
                if (elementAt(Q, i).SumAStar2 <= minValue.SumAStar2) {
                    minValue = elementAt(Q, i);
                    minIndex = i;
                }
            }
            Qnew.add(minValue);
            removeAt(Q, minIndex);
            aStarSortQueue2(Qnew, Q);
        }
        return Qnew;
    }

    public static void main(String[] args) {
        String example =
        "5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
        visualize(example);
        // genGrid();

        // for (Node s : Qnew)
        // System.out.print(s.cost);
    }
}