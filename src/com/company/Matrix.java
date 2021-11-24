package com.company;

import java.util.*;

class Node {
    String state = "Nx,Ny;NeoDamage;Carried:H1,H2;TotalAgents:A1:A1X:A1Y,A2,H4;"
            + "TotalHostages:H1:H1X:H1Y:100,H2:70,H3:30,H4:100,H6:50,H5:10;Tx,Ty;"
            + "HostagesSaved:H2,H3;Killed:H1,A3;PAD:SP1X,SP1Y,FP1X,FP2X;PILL:L1X,L1Y;CarryNumber"; // size current
    // position
    Node parent;
    String operator;// up:cost+10,down:cost+10,left:cost+10,right:cost+10,carry:cost-10,drop:-10,takePill:-10,kill:+10,fly:+10";
    int depth; // ?
    int cost;
    // Node Goal;//THIS SHOULD BE IN SEARCH PROBLEM done
    // Cost penalty if hostage dies done
    // Cost penalty if Neo dies done
    // Cost penalty if carried hostage in TB done
    // Cost penalty dropped outside of TB done
    // Cost penalty if agent killed done
    // Cost penalty if move out of bound ?
    // Cost reward if hostage dropped at TB done
    // Cost reward if pill taken saved a hostage done

    String[] stringarray = state.split(";"); // we can use dot, whitespace, any character

    boolean AgentKilled() {
        String Agents = stringarray[3];// [A1:30,A2:50,A3:10]
        String[] AgentsArray = Agents.split(",");// [[A1:30],[A2:50],[A3:10]]
        for (int i = 0; i < AgentsArray.length; i++) {
            String[] Hostage = AgentsArray[i].split(":");// [A1],[30]
            if (Hostage[1] == "100") { // CHECKS THE HP
                return true;// 1) cost+ 2)remove from state
            }
        }
        return false;
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
                node.cost = node.cost + 20;
                break;
            case "fly":
                node.cost = node.cost + 10;
                break;
            default:
                break;
        }
    }

}

class Matrix {

    // GLOBAL VARIABLES
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
        String plan = ""; // up, down, left, right, carry, drop, takePill, kill, and fly
        String deaths = "";
        String kills = "";
        String nodes = "";

        String[] OurGrid = grid.split(";");
        searchProblems p = null;
        Node n = null;
        String hostages = "";
        String agents = "";
        String pads = "";
        String pills = "";

        String GridCord[] = OurGrid[0].split(",");
        String M = GridCord[0];
        String N = GridCord[1];
        int CarryNumber = Integer.parseInt(OurGrid[1]);
        String Carried[] = OurGrid[2].split(",");
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
        p.InitialState = n;

        while (!p.isGoalState(n)) {
            switch (strategy) {
                case "BF":
                    plan = breadthFirst();
                    break;
                case "DF":
                    plan = depthFirst();
                    break;
                case "ID":
                    plan = iterativeDeepeningSearch();
                    break;
                case "UC":
                    plan = uniformCostSearch();
                    break;
                case "GR1":
                    plan = greedySearchOne();
                    break;
                case "GR2":
                    plan = greedySearchTwo();
                    break;
                case "AS1":
                    plan = aStarOne();
                    break;
                case "AS2":
                    plan = aStarTwo();
                    break;
                default:
                    break;
            }
        }

        return plan + ";" + deaths + ";" + kills + ";" + nodes;
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

        System.out.print(Arrays.deepToString(grid));
        // for (int i = 0; i < grid.length; i++) {
        // System.out.println();
        // for (int j = 0; j < grid[0].length; j++)
        // System.out.print(grid[i][j] + ", ");
        // }
    }

    public void stateSpace(Node n){
        String[] NodeList = n.state.split(";");
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
        int CarryNumber = Integer.parseInt(NodeList[10]);

        ArrayList<Object> Children = new ArrayList<>(); // [NewNode1,NewNode2,...]
        if (NeoX != "0") { // UP
            for(int i = 0; i< Agents.length; i++){
                String[] agentHelper = Agents[i].split(":");
                if((Integer.parseInt(NeoX)-1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2])
                    Children.add(calculateMove("killUp",n));
            }
            Children.add(calculateMove("up",n));
        }
        if (Integer.parseInt(NeoY) != N) { // DOWN
            for(int i = 0; i< Agents.length; i++){
                String[] agentHelper = Agents[i].split(":");
                if((Integer.parseInt(NeoX)+1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2])
                    Children.add(calculateMove("killDown",n));
            }
            Children.add(calculateMove("down",n));
        }
        if (NeoY != "0") { // LEFT
            for(int i = 0; i< Agents.length; i++){
                String[] agentHelper = Agents[i].split(":");
                if(NeoX == agentHelper[1] && (Integer.parseInt(NeoY)-1) == Integer.parseInt(agentHelper[2]))
                    Children.add(calculateMove("killLeft",n));
            }
            Children.add(calculateMove("left",n));
        }
        if (Integer.parseInt(NeoX) != M) { // RIGHT
            for(int i = 0; i< Agents.length; i++){
                String[] agentHelper = Agents[i].split(":");
                if(NeoX == agentHelper[1] && (Integer.parseInt(NeoY)+1) == Integer.parseInt(agentHelper[2]))
                    Children.add(calculateMove("killRight",n));
            }
            Children.add(calculateMove("right",n));
        }

        for (int i = 0; i < Hostages.length; i++) { // CARRY
            String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
            if (NeoX == HostagesHelper[1] && NeoY == HostagesHelper[2])
                if (CarryNumber > CarriedHostages.length)
                    Children.add(calculateMove("carry",n));
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
            if ((NeoX == Pads[0] && NeoY == Pads[1])||(NeoX == Pads[2] && NeoY == Pads[3]))
                Children.add(calculateMove("fly", n));
        }
    }

    public static Node calculateMove(String action, Node parent) {
        Node child = new Node(); // STATE PARENT OPERATOR DEPTH COST
        child.parent = parent;
        child.depth = parent.depth + 1;
        searchProblems.pathCost(child);

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
        int CarryNumber = Integer.parseInt(NodeList[10]);

        String NeoPlace = NodeList[0];
        String Killed = NodeList[7];
        String TotalAgents = NodeList[3];
        String NewCarriedHostages = NodeList[2];
        String NewSavedHostages = NodeList[6];
        String NewPills = NodeList[9];

        switch (action) {
            case "up":
                NeoPlace = (Integer.parseInt(NeoX) - 1) + "," + NeoY;
                if (CarriedHostages.length != 0) {
                    for (int i = 0; i < Hostages.length; i++) {
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
            case "killUp":
                NeoDamage += 20;
                for (int i = 0; i < Agents.length; i++) {
                    String[] agentHelper = Agents[i].split(":");
                    if ((Integer.parseInt(NeoX) - 1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2]) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
                    } else
                        TotalAgents = agentHelper[i] + ":" + agentHelper[i + 1] + ":" + agentHelper[i + 2] + ",";
                }
                child.operator = "kill";
                break;
            case "killDown":
                NeoDamage += 20;
                for (int i = 0; i < Agents.length; i++) {
                    String[] agentHelper = Agents[i].split(":");
                    if ((Integer.parseInt(NeoX) + 1) == Integer.parseInt(agentHelper[1]) && NeoY == agentHelper[2]) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
                    } else
                        TotalAgents = agentHelper[i] + ":" + agentHelper[i + 1] + ":" + agentHelper[i + 2] + ",";
                }
                child.operator = "kill";
                break;
            case "killLeft":
                NeoDamage += 20;
                for (int i = 0; i < Agents.length; i++) {
                    String[] agentHelper = Agents[i].split(":");
                    if (NeoX == agentHelper[1] && (Integer.parseInt(NeoY) - 1) == Integer.parseInt(agentHelper[2])) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
                    } else
                        TotalAgents = agentHelper[i] + ":" + agentHelper[i + 1] + ":" + agentHelper[i + 2] + ",";
                }
                child.operator = "kill";
                break;
            case "killRight":
                NeoDamage += 20;
                for (int i = 0; i < Agents.length; i++) {
                    String[] agentHelper = Agents[i].split(":");
                    if (NeoX == agentHelper[1] && (Integer.parseInt(NeoY) + 1) == Integer.parseInt(agentHelper[2])) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
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
            }
            String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                    + HostagesHelper[3];
            Hostages[i] = hostage;
        }

        String newState = NeoPlace + ";" + NeoDamage + ";" + NewCarriedHostages + ";" + TotalAgents + ";";
        for (int i=0; i < Hostages.length; i++)
            newState += Hostages[i] + ",";
        newState = newState.substring(0, newState.length() - 1);
        newState += ";" + NodeList[5] + ";" + NewSavedHostages + ";" + Killed + ";" + NodeList[8] + ";" + NewPills + ";"
                + NodeList[10];
        child.state = newState;
        return child;
    }

    public static String breadthFirst() {
        Queue<Node> Q = new LinkedList<>();

        return "";
    }

    public static String depthFirst() {
        Queue<Node> Q = new LinkedList<>();
        return "";
    }

    public static String iterativeDeepeningSearch() {
        Queue<Node> Q = new LinkedList<>();
        return "";
    }

    public static String uniformCostSearch() {
        Queue<Node> Q = new LinkedList<>();
        return "";
    }

    public static String greedySearchOne() {
        Queue<Node> Q = new LinkedList<>();
        return "";
    }

    public static String greedySearchTwo() {
        Queue<Node> Q = new LinkedList<>();
        return "";
    }

    public static String aStarOne() {
        Queue<Node> Q = new LinkedList<>();
        return "";
    }

    public static String aStarTwo() {
        Queue<Node> Q = new LinkedList<>();
        return "";
    }

    public static void main(String[] args) {
        // String example =
        // "5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
        // visualize(example);
        // genGrid();
    }
}