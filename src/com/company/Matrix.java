package com.company;
import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;
import org.omg.Messaging.SyncScopeHelper;

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

    public boolean isGoalState(Node node) {
        // chick place neo x,y = telephone
        // chick total number of Hostages == [saved Hostages + Killed Hostages whose
        // changed to agent]
        // saved hostage ignored carried and Hostages info in state
        // Chick neo hp isn't 0
        String[] stringarray = node.state.split(";");

        boolean areHostagesSaved = false;
        boolean isNeoDead = false;
        boolean isNeoinHome = false;

        String Hostages = stringarray[4];//
        String[] HostageArray = Hostages.split(",");//
        String Saved = stringarray[6];// HostagesSaved:H1,H2,H3
        String[] SavedHostageArray = Saved.split(",");// H1,H2,H3
        String Killed = stringarray[7];// Killed:H1,A3
        String[] killed = Killed.split(",");
        String Neoplace = stringarray[0];
        String Telephone = stringarray[5];
        int numberOfKilledHostages = 0;
        //System.out.println("saved"+SavedHostageArray.length);
        for (int i = 0; i < killed.length &&!Killed.isEmpty() ; i++) {
            if(killed[i].contains("H")){
                numberOfKilledHostages++;
            }
//            String[] Hostage = HostageArray[i].split(":");// [H1][30]
//            if (Hostage[3] == "100") { // CHECKS THE Damage
//                String myHostage = Hostage[0];
//                String Agents = stringarray[3];// TotalAgents:A1,A2,H4
//                String[] agent = Agents.split(",");
//                String Killed = stringarray[7];// Killed:H1,A3
//                String[] killed = Killed.split(",");
//                for (int j = 0; j < agent.length; j++) {
//                    if (myHostage == agent[j]) {
//                        return false; // as there is a converted hostage is still alive not saved or killed
//                    }
//                }
//                for (int k = 0; k < killed.length; k++) {
//                    if (myHostage == killed[k]) {
//                        numberOfKilledHostages++; // as there is a converted hostage is killed
//                    }
//                }
//            }
        }
        if (stringarray[1].equals("100")) {
            isNeoDead =  true;
        }
        //System.out.println(Saved.length() +" " + numberOfKilledHostages +" "+ HostageArray.length);

        if ((SavedHostageArray.length + numberOfKilledHostages )== HostageArray.length && !Saved.isEmpty() && !Hostages.isEmpty()) {
            areHostagesSaved = true;
        }
        if (Neoplace.equals(Telephone)) {
            isNeoinHome = true;
        }
        //System.out.println(isNeoinHome +" " +areHostagesSaved +" "+ !isNeoDead);
        //System.out.println("savedddd"+stringarray[6].length());
        if (isNeoinHome && areHostagesSaved && !isNeoDead) {
            //System.out.println("hi");
            return true;

        }else{
        return false;
        }
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
                if (!stringarray[0].equals(stringarray[5])) // carry shouldn't be in the telphone
                    node.cost = node.cost - 20;
                break;
            case "drop":
                if (stringarray[0].equals(stringarray[5])) // drop should be in the telphone only
                    node.cost = node.cost - 20;
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
        for (int i = 0; i < Hostages.length && !state[4].isEmpty() ; i++) {
            String[] Hostage = Hostages[i].split(":");// [H1][30]
            if (Hostage[1].equals("100")) { // CHECKS THE Damage
                String myHostage = Hostage[0];
                for (int k = 0; k < killed.length && !state[7].isEmpty() ; k++) {
                    if (myHostage.equals(killed[k])) {
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
        for (int i = 0; i < Hostages.length && !state[4].isEmpty(); i++) {// H1:H1X:H1Y:H1D,H2....
            String[] HostagesHelper = Hostages[i].split(":");
            int HX = Integer.parseInt(HostagesHelper[1]);
            int HY = Integer.parseInt(HostagesHelper[2]);
            if (!HostagesHelper[3].equals("100")) {
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
    static HashSet<String> stateSet;

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
        stateSet = new HashSet();
        String[] OurGrid = grid.split(";");
        searchProblems p = new searchProblems();
        Node n = new Node();
        String hostages = "";
        String agents = "";
        String pads = "";
        String pills = "";
        Queue<Node> Q = new LinkedList<>();

        String CarryNumber = OurGrid[1];

        if(!OurGrid[7].isEmpty()) {
                String[] Hostages = OurGrid[7].split(",");
            for (int i = 0; i < Hostages.length ; i=i+3) {
                hostages = hostages + "H" + i + ":" + Hostages[i] + ":" + Hostages[i+1] + ":" + Hostages[i+2] + ",";
            }

        hostages = hostages.substring(0, hostages.length() - 1);
        }
        if(!OurGrid[4].isEmpty()) {
            String[] Agents = OurGrid[4].split(",");
            for (int i = 0; i < Agents.length; i=i+2) {
                agents = agents +"A" + i + ":" + Agents[i] + ":" + Agents[i+1] + ",";
            }
        agents = agents.substring(0, agents.length() - 1);
        }
        if(!OurGrid[6].isEmpty()){
            String[] Pads = OurGrid[6].split(",");
            for (int i = 0; i < Pads.length; i=i+4) {
            pads = pads +"SP" + i + ":" + Pads[i] + ":" + Pads[i+1] + "," + "FP" + i + ":" + Pads[i + 2] + ":" + Pads[i + 3]
                    + ",";
        }
        pads = pads.substring(0, pads.length() - 1);
        }
        if(!OurGrid[5].isEmpty()) {
            String[] Pills = OurGrid[5].split(",");
            for (int i = 0; i < Pills.length; i=i+2) {
                pills = pills + Pills[i] + "," + Pills[i + 1] + ",";
            }
            pills = pills.substring(0, pills.length() - 1);
        }
        //state = "Nx,Ny;NeoDamage;Carried:H1,H2;TotalAgents:A1:A1X:A1Y,A2,H4;"
        // + "TotalHostages:H1:H1X:H1Y:100,H2:70,H3:30,H4:100,H6:50,H5:10;Tx,Ty;"
        //+ "HostagesSaved:H2,H3;Killed:H1,A3;PAD:SP1X,SP1Y,FP1X,FP2X;PILL:L1X,L1Y;CarryNumber";

        //grid = "M,N; C; NEOX,NEOY;TX,TY;
        //AGENTX,AGENTY,AGENT2X,AGENTY;
        //PILLX1,PILLY1,PILLX2,PILLY2;
        //SP1X,SP1Y,FP1X,FP1Y,..;
        //HX,HY,HD
        n.state = OurGrid[2] + ";0;;" + agents + ";" + hostages + ";" + OurGrid[3] + ";" + ";" + ";" +pads + ";" + pills
                + ";" + CarryNumber;
        searchProblems.Calculateheuristic1(n);
        searchProblems.Calculateheuristic2(n);
        stateSet.add(n.state);
        p.InitialState = n;
        Q.add(n);

        String out = "";

        switch (strategy) {
        case "BF":
            out = breadthFirst(Q);
            break;
        case "DF":
            out = depthFirst(Q);
            break;
        case "ID":
            out = iterativeDeepeningSearch(Q);
            break;
        case "UC":
            out = uniformCostSearch(Q);
            break;
        case "GR1":
            out = greedySearchOne(Q);
            break;
        case "GR2":
            out = greedySearchTwo(Q);
            break;
        case "AS1":
            out = aStarOne(Q);
            break;
        case "AS2":
            out = aStarTwo(Q);
            break;
        default:
            out = "";
        }

//        if (visualize){
//            String[] path = out.split(";")[0].split(",");
//
//        }
        //System.out.println(out);
        return out;
    }
    public static boolean checkstate(Node n){

        if(stateSet.contains(n.state)){
            return true;
        }
        else {
            return false;
        }
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
        String TB = NodeList[5];
        String NeoC = NodeList[0];
        // String[] SavedHostages = NodeList[6].split(",");
        int NeoDamage = Integer.parseInt(NodeList[1]);
        int CarryNumber = Integer.parseInt(NodeList[10]);
        Node action;
        //System.out.println(stateSet.size());
        boolean agentkilled = false;
        if (Integer.parseInt(NeoX) > 0) { // UP
                for (int i = 0; i < Agents.length && !NodeList[3].isEmpty() ; i++) {
                    String[] agentHelper = Agents[i].split(":");
                    //System.out.println(Agents[i] + " " + NeoX +" "+ NeoY +" "+Agents.length);
                    if ((Integer.parseInt(NeoX) - 1) == Integer.parseInt(agentHelper[1]) && NeoY.equals(agentHelper[2])) {
                        if (NeoDamage > 20) {
                            action = calculateMove("kill", n);
                            if (!checkstate(action)) {
                                agentkilled =true;
                                Children.add(action);
                                stateSet.add(action.state);
                            }
                        }
                    }
                    //System.out.println();
                }
                if(!agentkilled) {
                    action = calculateMove("up", n);
                    if (!checkstate(action)) {
                        Children.add(action);
                        stateSet.add(action.state);
                        agentkilled =false;
                    }
                }
        }
        if (Integer.parseInt(NeoX) < N) { // DOWN
            for (int i = 0; i < Agents.length && !NodeList[3].isEmpty(); i++) {
                String[] agentHelper = Agents[i].split(":");
                if ((Integer.parseInt(NeoX) + 1) == Integer.parseInt(agentHelper[1]) && NeoY.equals(agentHelper[2]))
                    if(NeoDamage>20){
                        action = calculateMove("kill", n);
                        if(!checkstate(action)){
                            agentkilled =true;
                            Children.add(action);
                            stateSet.add(action.state);
                        }
                    }
                //System.out.println();

            }

            if(!agentkilled) {
                action = calculateMove("down", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    stateSet.add(action.state);
                    agentkilled =false;
                }
            }
        }
        if (Integer.parseInt(NeoY) > 0) { // LEFT
            for (int i = 0; i < Agents.length  && !NodeList[3].isEmpty(); i++) {
                String[] agentHelper = Agents[i].split(":");
                if (NeoX.equals(agentHelper[1]) && (Integer.parseInt(NeoY) - 1) == Integer.parseInt(agentHelper[2]))
                    if(NeoDamage>20){
                        action = calculateMove("kill", n);
                        if(!checkstate(action)){
                            agentkilled =true;
                            Children.add(action);
                            stateSet.add(action.state);
                        }
                    }
            }
            if(!agentkilled) {
                action = calculateMove("left", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    stateSet.add(action.state);
                    agentkilled =false;
                }
            }
        }
        if (Integer.parseInt(NeoY) < M) {// RIGHT
            for (int i = 0; i < Agents.length  && !NodeList[3].isEmpty(); i++) {
                String[] agentHelper = Agents[i].split(":");
                if (NeoX.equals(agentHelper[1]) && (Integer.parseInt(NeoY) + 1) == Integer.parseInt(agentHelper[2]))
                    if(NeoDamage>20){
                        action = calculateMove("kill", n);
                        if(!checkstate(action)){
                            agentkilled =true;
                            Children.add(action);
                            stateSet.add(action.state);
                        }
                    }
            }
            if(!agentkilled) {
                action = calculateMove("right", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    stateSet.add(action.state);
                    agentkilled =false;
                }
            }
        }

        for (int i = 0; i < Hostages.length  && !NodeList[4].isEmpty(); i++) { // CARRY
            String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
            if (NeoX.equals(HostagesHelper[1]) && NeoY.equals(HostagesHelper[2]) & !TB.equals(NeoC)){
                if (CarryNumber > CarriedHostages.length || NodeList[2].isEmpty()){
                    action = calculateMove("carry", n);
                    if(!checkstate(action)){
                        Children.add(action);
                        stateSet.add(action.state);
                        }
                }

            }
        }
        if (NodeList[0].equals(NodeList[5])) { // DROP
            if (!NodeList[2].isEmpty()) {
                action = calculateMove("drop", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    stateSet.add(action.state);
                }
            }
        }

        for (int i = 0; i < Pills.length && !NodeList[9].isEmpty(); i = i + 2) {// takePill
            //System.out.println("PILL" + i + "," + Pills[i] + "," + Pills[i+1]); //P1X,P1Y,P2X,P2Y
            if (NeoX.equals(Pills[i]) && NeoY.equals(Pills[i + 1])) {
                if (Integer.parseInt(NodeList[1]) < 100) {
                    action = calculateMove("takePill", n);
                    if (!checkstate(action)) {
                        Children.add(action);
                        stateSet.add(action.state);
                    }
                }
            }
        }

        for (int i = 0; i <Pads.length && !NodeList[8].isEmpty() ; i = i + 4) { // [sp1x,sp1y,fp1x,fp1y]
            if ((NeoX.equals(Pads[i]) && NeoY.equals(Pads[i+1])) || (NeoX.equals(Pads[i+2]) && NeoY.equals(Pads[i+3]))){
                action =calculateMove("fly", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    stateSet.add(action.state);
                }
            }
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
        int CarryNumber = Integer.parseInt(NodeList[10]);

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
            if (NodeList[2].length() != 0) {
                for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty() ; i++) {// H1:H1X:H1Y:H1D,H2....
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length && !NodeList[2].isEmpty(); j++) {
                        if (HostagesHelper[0].equals(CarriedHostages[j]))
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
            if (NodeList[2].length() != 0) {
                for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) {
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length && !NodeList[2].isEmpty(); j++) {
                        if (HostagesHelper[0].equals(CarriedHostages[j]))
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
            if (NodeList[2].length() != 0) {
                for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) {
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length && !NodeList[2].isEmpty(); j++) {
                        if (HostagesHelper[0].equals(CarriedHostages[j]))
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
            if (NodeList[2].length() != 0) {
                for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) {
                    String[] HostagesHelper = Hostages[i].split(":");
                    for (int j = 0; j < CarriedHostages.length && !NodeList[2].isEmpty(); j++) {
                        if (HostagesHelper[0].equals(CarriedHostages[j]))
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
            //System.out.println("oldCarry"+NewCarriedHostages);
            //NewCarriedHostages="";
            for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) {
                String[] HostagesHelper = Hostages[i].split(":");
                if (NeoX.equals(HostagesHelper[1]) && NeoY.equals(HostagesHelper[2]) && !NewCarriedHostages.contains(HostagesHelper[0])) {
                    if (NewCarriedHostages.length() != 0)
                        NewCarriedHostages += ",";
                    NewCarriedHostages += HostagesHelper[0];
                }
            }
            //System.out.println("newCarry"+NewCarriedHostages);
            child.operator = "carry";
            break;
        case "drop":
            //System.out.println("oldDrop"+NewSavedHostages);
            //NewSavedHostages="";
            for (int i = 0; i < CarriedHostages.length && !NodeList[2].isEmpty(); i++) {
                if (NewSavedHostages.length() != 0)
                    NewSavedHostages += ",";
                NewSavedHostages += CarriedHostages[i];
                CarriedHostages[i] = "";
            }
            //System.out.println("newDrop"+NewSavedHostages);
            NewCarriedHostages = "";
            child.operator = "drop";
            break;
        case "takePill":
            NewPills = "";
            for (int i = 0; i < Pills.length && !NodeList[9].isEmpty(); i = i + 2) {
                if (NeoX.equals(Pills[i]) && NeoY.equals(Pills[i+1])) {
                    // neo damage and hostages damage minus + remove pill
                    Pills[i] = "";
                    Pills[i + 1] = "";
                    NeoDamage = NeoDamage - 20;
                    if (NeoDamage < 0){
                        NeoDamage = 0;
                    }
                    for (int j = 0; j < Hostages.length && !NodeList[4].isEmpty(); j++) {
                        boolean flag = false;
                        String[] HostagesHelper = Hostages[j].split(":");
                        if (Integer.parseInt(HostagesHelper[3]) >= 100) {
                            for (int k = 0; k < SavedHostages.length && !NodeList[6].isEmpty(); k++){
                                if (SavedHostages[k].equals(HostagesHelper[0]))
                                    flag = true;
                            }
                            if (!flag) {
                                HostagesHelper[3] = (Integer.parseInt(HostagesHelper[3]) - 22) + "";
                                if (Integer.parseInt(HostagesHelper[3]) < 0)
                                    HostagesHelper[3] = 0 + "";
                            }
                        }
                    }
                }else {
                    NewPills += Pills[i] + "," + Pills[i + 1] + ",";
                }
            }
            System.out.println(NewPills);
            if(!NewPills.isEmpty()){
            NewPills = NewPills.substring(0, NewPills.length() - 1);}
            child.operator = "takePill";
            break;
        case "fly":
            for (int i = 0; i < (Pads.length / 4) && !NodeList[8].isEmpty(); i = i + 4) { // [sp1x,sp1y,fp1x,fp1y]
                if (NeoX.equals(Pads[0]) && NeoY.equals(Pads[1])) {
                    NeoPlace = Pads[2] + "," + Pads[3];
                }
                if (NeoX.equals(Pads[2]) && NeoY.equals(Pads[3])) {
                    NeoPlace = Pads[0] + "," + Pads[1];
                }
            }
            NeoPlace = (Integer.parseInt(NeoX) - 1) + "," + NeoY;
            child.operator = "fly";
            break;
        case "kill":
            if(Integer.parseInt(NodeList[1])<100) {
                if(!NodeList[3].isEmpty()){
                    TotalAgents="";
                for (int i = 0; i < Agents.length && !NodeList[3].isEmpty(); i++) { //[A0...,A1...]
                    String[] agentHelper = Agents[i].split(":"); //A0:AX:AY
                    if ((Integer.parseInt(NeoX) - 1) == Integer.parseInt(agentHelper[1]) && NeoY.equals(agentHelper[2])
                            && NeoDamage <= 100) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
                        child.operator = "kill";

                    }
                    if ((Integer.parseInt(NeoX) + 1) == Integer.parseInt(agentHelper[1]) && NeoY.equals(agentHelper[2])
                            && NeoDamage <= 100) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
                        child.operator = "kill";

                    }
                    if (NeoX.equals(agentHelper[1]) && (Integer.parseInt(NeoY) - 1) == Integer.parseInt(agentHelper[2])
                            && NeoDamage <= 100) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
                        child.operator = "kill";

                    }
                    if (NeoX.equals(agentHelper[1]) && (Integer.parseInt(NeoY) + 1) == Integer.parseInt(agentHelper[2])
                            && NeoDamage <= 100) {
                        if (Killed.length() != 0)
                            Killed += ",";
                        Killed += agentHelper[0];
                        child.operator = "kill";
                    } else
                        if(!Agents[i].isEmpty()){
                        TotalAgents = TotalAgents + "," + Agents[i];
                        }
                }
            }
            if(child.operator.equals("kill")){
                NeoDamage += 20;
            }
            }
            break;
        default:
            break;
        }
        for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) { // EveryTick
            String[] HostagesHelper = Hostages[i].split(":");
            String[] killed = NodeList[7].split(",");
            boolean wasagent = false;
            boolean wasCarried = false;
            boolean isKilled = false;
            boolean wasSaved = false;
            //minus hostages health
            boolean isCarried = false;
            boolean isSaved = false;
            boolean wasKilled = false;

            for(int J = 0 ; J< Agents.length && !NodeList[3].isEmpty() ; J++){ //if he is already agent or he will be killed
                String[] agentHelper = Agents[J].split(":");
                if (((Integer.parseInt(NeoX) - 1) == Integer.parseInt(agentHelper[1]) && NeoY.equals(agentHelper[2])
                        && NeoDamage <= 100) || ((Integer.parseInt(NeoX) + 1) == Integer.parseInt(agentHelper[1]) && NeoY.equals(agentHelper[2])
                        && NeoDamage <= 100) ||(NeoX.equals(agentHelper[1]) && (Integer.parseInt(NeoY) - 1) == Integer.parseInt(agentHelper[2])
                        && NeoDamage <= 100) || (NeoX.equals(agentHelper[1]) && (Integer.parseInt(NeoY) + 1) == Integer.parseInt(agentHelper[2])
                        && NeoDamage <= 100) ){
                    isKilled = true;
                }
                if(agentHelper[0].equals(HostagesHelper[0])){ //if he was agent
                        wasagent = true;
                    }
            }
            for(int K = 0 ; K<CarriedHostages.length && !NodeList[2].isEmpty() ; K++){ //if he was carried
                    if(CarriedHostages[K].equals(HostagesHelper[0])){
                        wasCarried = true;
                        break;
                    }
            }
            for(int L = 0 ; L< killed.length && !NodeList[7].isEmpty() ; L++){ //if he was killed
                    if(killed[L].equals(HostagesHelper[0])){
                        wasKilled = true;
                        break;
                    }
            }
            for(int P = 0 ; P< SavedHostages.length && !NodeList[6].isEmpty() ; P++){ //if he was saved
                    if(SavedHostages[P].equals(HostagesHelper[0])){
                        wasSaved = true;
                        break;
                    }
            }
            if (NeoX.equals(HostagesHelper[1]) && NeoY.equals(HostagesHelper[2]) & !NodeList[0].equals(NodeList[5])){ //if he will be carried
                if (CarryNumber > CarriedHostages.length || NodeList[2].isEmpty()){
                    isCarried = true;
                }
            }

            if((!wasCarried && !wasagent &&!isKilled && !wasSaved)|| wasCarried){
                HostagesHelper[3] = String.valueOf(Integer.parseInt(HostagesHelper[3]) + 2);
            }

            if(NodeList[0].equals(NodeList[5]) && !NodeList[2].isEmpty()){ // if he will be saved
                isSaved = true;
            }


            if ((Integer.parseInt(HostagesHelper[3]) >= 100) ) {
                HostagesHelper[3] = "100";
                if(!wasCarried && !wasagent &&!wasKilled && !wasSaved && !isCarried && !isKilled && !isSaved) {//update TotalAgents
                    if (TotalAgents.length() != 0){
                        TotalAgents += ",";
                    }
                    TotalAgents += HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2];
                    child.cost += 100; // Penalty if any hostage died
                }
            }
            String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                    + HostagesHelper[3];
            Hostages[i] = hostage;
        }
        String newState = NeoPlace + ";" + NeoDamage + ";" + NewCarriedHostages + ";" + TotalAgents + ";";
        for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++)
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
        Node Initial = Q.peek();
        System.out.println(Initial.state);
        String path="";
        Queue<Node> Qnew = stateSpace(Q.remove());

        int nodes = 1;
        while (!Qnew.isEmpty()){
            Q.add(Qnew.remove());
        }
        //System.out.println("safe"+Q.peek().state.split(";")[6]);
        while (!p.isGoalState(Q.peek())) {
            //System.out.println("d5lt tany");
            Queue<Node> children = stateSpace(Q.remove());
            nodes++;
            while (!children.isEmpty()) {
                Q.add(children.remove());
            }
        }
        String[] stateAttributes = Q.peek().state.split(";");
        String Hostages = stateAttributes[4];//
        String[] HostageArray = Hostages.split(",");//
        String Saved = stateAttributes[6];// HostagesSaved:H1,H2,H3
        String[] SavedHostageArray = Saved.split(",");// H1,H2,H3
        int deaths=0;
        int kills=0;
        for (int i = 0; i < HostageArray.length &&!Hostages.isEmpty() ; i++) { //if carried hostage died
            String[] HostagesHelper = HostageArray[i].split(":");
//            for(int j=0 ; j<SavedHostageArray.length && !Saved.isEmpty() ; j++){
//                if(SavedHostageArray[j].equals(HostagesHelper[0])&& Integer.parseInt(HostagesHelper[3])>=100){
//                    deaths++;
//                }
//            }
            if(HostagesHelper[3].equals("100")){
                deaths++;
            }
        }

        if(!stateAttributes[7].isEmpty()){
            kills = stateAttributes[7].split(",").length;}
        else{
            kills = 0;
        }
        Node lastNode = Q.peek();
        //System.out.println(lastNode.state);
        while (!p.isGoalState(Q.peek())) {
            System.out.println(lastNode.state);
            if(!path.isEmpty()){
            path = lastNode.operator + "," +path;
            }
            else{
                path = lastNode.operator;
            }
            lastNode = lastNode.parent;
        }
        //System.out.println(path + ";" + deaths + ";" + kills + ";" + nodes);
        return path + ";" + deaths + ";" + kills + ";" + nodes;
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
        int kills;
        int deaths;
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
        if(!stateAttributes[6].isEmpty()){
        deaths = stateAttributes[4].split(",").length - stateAttributes[6].split(",").length;}
        else{
            deaths = stateAttributes[4].split(",").length;
        }
        if(!stateAttributes[7].isEmpty()){
        kills = stateAttributes[7].split(",").length;}
        else{
        kills = 0;
        }
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
        //String example =
        //"5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
        //visualize(example);
        // genGrid();
        //String ss = "a,b";
        //System.out.println(ss.split(",").length);
        // for (Node s : Qnew)
        // System.out.print(s.cost);
    }
}