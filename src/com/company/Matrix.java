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

    public int isGoalState(Node node) {
        // chick place neo x,y = telephone
        // chick total number of Hostages == [saved Hostages + Killed Hostages whose
        // changed to agent]
        // saved hostage ignored carried and Hostages info in state
        // Chick neo hp isn't 0
        //System.out.println(node.state);
        if(node == null){
            return 0;
        }else {
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
            for (int i = 0; i < killed.length && !Killed.isEmpty(); i++) {
                if (killed[i].contains("H")) {
                    numberOfKilledHostages++;
                }
            }
            if (stringarray[1].equals("100")) {
                isNeoDead = true;
            }
            //System.out.println(Saved.length() +" " + numberOfKilledHostages +" "+ HostageArray.length);

            if ((SavedHostageArray.length + numberOfKilledHostages) == HostageArray.length && !Saved.isEmpty() && !Hostages.isEmpty()) {
                areHostagesSaved = true;
            }
            if (Neoplace.equals(Telephone)) {
                isNeoinHome = true;
            }
            //System.out.println(isNeoinHome +" " +areHostagesSaved +" "+ !isNeoDead);
            //System.out.println("savedddd"+stringarray[6].length());
            //System.out.println(isNeoinHome);
            if (isNeoinHome && areHostagesSaved && !isNeoDead) {
                //System.out.println("hi");
                return 1;

            } else {
                return 0;
            }
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
                    node.cost = node.cost - 20;
                break;
            case "drop":
                    node.cost = node.cost - 20;
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
public class Matrix {

    // GLOBAL VARIABLES
    String[] Operators = "up,down,left,right,carry,drop,takePill,kill,fly".split(",");
    static int M = randomGenerator(5, 15); // width of grid
    static int N = randomGenerator(5, 15); // height of grid
    static boolean[][] gridCoordinates = new boolean[M][N];
    static HashSet<String> stateSet;
    public static ArrayList<String> goalStatePath = new ArrayList<String>();


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
        M = Integer.parseInt(OurGrid[0].split(",")[0]);
        N = Integer.parseInt(OurGrid[0].split(",")[1]);

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

        String out = "";

        switch (strategy) {
            case "BF":
                PriorityQueue<Node> bf = new PriorityQueue<Node>((b,a)->(b.depth-a.depth));
                out = genericSearch(bf , p);
                break;
            case "DF":
                PriorityQueue<Node> df = new PriorityQueue<Node>((a,b)->(b.depth-a.depth));
                out = genericSearch(df ,p );
                break;
            case "ID":
                PriorityQueue<Node> ids = new PriorityQueue<Node>((a,b)->(b.depth-a.depth));
                out = genericSearchWithLimit(ids,p);
                break;
            case "UC":
                PriorityQueue<Node> UC = new PriorityQueue<Node>((a,b)->(b.cost-a.cost));
                out = genericSearch(UC,p);
                break;
            case "GR1":
                PriorityQueue<Node> GR1 = new PriorityQueue<Node>((a,b)->(b.H1-a.H1));
                out = genericSearch(GR1,p);
                break;
            case "GR2":
                PriorityQueue<Node> GR2 = new PriorityQueue<Node>((a,b)->(b.H2-a.H2));
                out = genericSearch(GR2,p);
                break;
            case "AS1":
                PriorityQueue<Node> AS1 = new PriorityQueue<Node>((a,b)->(b.SumAStar1-a.SumAStar1));
                out = genericSearch(AS1,p);
                break;
            case "AS2":
                PriorityQueue<Node> AS2 = new PriorityQueue<Node>((a,b)->(b.SumAStar2-a.SumAStar2));
                out = genericSearch(AS2,p);
                break;
            default:
                out = "";
        }
        if (visualize)
            visualize(goalStatePath, grid);

        return out;
    }
    public static boolean checkstate(Node n){
        String[] NodeList = n.state.split(";");
        String[] Hostages = NodeList[4].split(",");
        int NeoDamage = Integer.parseInt(NodeList[1]);
        String NeoPlace = NodeList[0];
        String Killed = NodeList[7];
        String TotalAgents = NodeList[3];
        String NewCarriedHostages = NodeList[2];
        String NewSavedHostages = NodeList[6];
        String NewPills = NodeList[9];
        String NewHostages="";
//        //state = "Nx,Ny;NeoDamage;Carried:H1,H2;TotalAgents:A1:A1X:A1Y,A2,H4;"
//        // + "TotalHostages:H1:H1X:H1Y:100,H2:70,H3:30,H4:100,H6:50,H5:10;Tx,Ty;"
//        //+ "HostagesSaved:H2,H3;Killed:H1,A3;PAD:SP1X,SP1Y,FP1X,FP2X;PILL:L1X,L1Y;CarryNumber";
////        if(!NodeList[4].isEmpty()){
////            for (int i = 0; i < Hostages.length  && !NodeList[4].isEmpty(); i++) { // CARRY
////                String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
////                NewHostages = HostagesHelper[0]+",";
////            }
////            NewHostages.substring(0,NewHostages.length()-1);
////        }
//

        String NewState = NeoPlace + ";" + NeoDamage +";" + NewCarriedHostages + ";" +TotalAgents+";"+ NewSavedHostages.length() ;

        if(stateSet.contains(n.state)){
            return true;
        }
        else {
            return false;
        }
    }
    public static void addstate(Node n){
        String[] NodeList = n.state.split(";");
        String[] Hostages = NodeList[4].split(",");
        int NeoDamage = Integer.parseInt(NodeList[1]);
        String NeoPlace = NodeList[0];
        String Killed = NodeList[7];
        String TotalAgents = NodeList[3];
        String NewCarriedHostages = NodeList[2];
        String NewSavedHostages = NodeList[6];
        String NewPills = NodeList[9];
        String NewHostages="";
        //state = "Nx,Ny;NeoDamage;Carried:H1,H2;TotalAgents:A1:A1X:A1Y,A2,H4;"
        // + "TotalHostages:H1:H1X:H1Y:100,H2:70,H3:30,H4:100,H6:50,H5:10;Tx,Ty;"
        //+ "HostagesSaved:H2,H3;Killed:H1,A3;PAD:SP1X,SP1Y,FP1X,FP2X;PILL:L1X,L1Y;CarryNumber";
//        if(!NodeList[4].isEmpty()){
//            for (int i = 0; i < Hostages.length  && !NodeList[4].isEmpty(); i++) { // CARRY
//                String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
//                NewHostages = HostagesHelper[0]+",";
//            }
//            NewHostages.substring(0,NewHostages.length()-1);
//        }
        String NewState = NeoPlace + ";" +NeoDamage +";" + NewCarriedHostages + ";" +TotalAgents+";"+ NewSavedHostages.length();
        // kill - total agents , killed , damage
        // takePill - Hostages , Pills , damage
        // hostage died - total agents
        // fly, up , left , right , down - neo place
        // drop - Carried , Saved
        // carry - Carried
        //String NewState = NeoPlace + ";" + NewCarriedHostages + ";" +TotalAgents+";"+ NewSavedHostages  +";" + NewPills ;
        stateSet.add(n.state);
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


    public static void visualize(ArrayList<String> path, String grid) {
        visualize(grid);
        if (path.size() == 0)
            return;

        String MN = grid.split(";")[0];
        String C = grid.split(";")[1];
        String NeoX = grid.split(";")[2].split(",")[0];
        String NeoY = grid.split(";")[2].split(",")[1];
        String TeleXTeleY = grid.split(";")[3];
        String Agents = grid.split(";")[4];
        String Pills = grid.split(";")[5];
        String Pads = grid.split(";")[6];
        String Hostages = grid.split(";")[7];

        // = "Nx,Ny;NeoDamage;Carried:H1,H2;TotalAgents:A1:A1X:A1Y,A2,H4;"
        // + "TotalHostages:H1:H1X:H1Y:100,H2:70,H3:30,H4:100,H6:50,H5:10;Tx,Ty;"
        // +"HostagesSaved:H2,H3;Killed:H1,A3;PAD:SP1X,SP1Y,FP1X,FP2X;PILL:L1X,L1Y;CarryNumber"

        String lastState = path.get(path.size() - 1); // "2,4;0;;A0:0:3,A2:1:4;H0:2:2:93,H3:2:4:64;1,2;;;SP0:4:4,FP0:0:2,SP4:0:2,FP4:4:4;2,3;2"
        NeoX = lastState.split(";")[0].split(",")[0];
        NeoY = lastState.split(";")[0].split(",")[1];

        C = lastState.split(";")[10];

        String[] TotalAgents = lastState.split(";")[3].split(",");
        for (int i = 0; i < TotalAgents.length; i++) {
            TotalAgents[i] = TotalAgents[i].substring(3, TotalAgents[i].length());
            TotalAgents[i] = TotalAgents[i].replace(':', ',');
        }
        Agents = "";
        for (int i = 0; i < TotalAgents.length; i++)
            Agents += TotalAgents[i] + ",";
        Agents = Agents.substring(0, Agents.length() - 1);

        Pills = lastState.split(";")[9];

        String[] TotalHostages = lastState.split(";")[4].split(",");
        for (int i = 0; i < TotalHostages.length; i++) {
            TotalHostages[i] = TotalHostages[i].substring(3, TotalHostages[i].length());
            TotalHostages[i] = TotalHostages[i].replace(':', ',');
        }
        Hostages = "";
        for (int i = 0; i < TotalHostages.length; i++)
            Hostages += TotalHostages[i] + ",";
        Hostages = Hostages.substring(0, Hostages.length() - 1);

        String newGrid = MN + ";" + C + ";" + NeoX + "," + NeoY + ";" + TeleXTeleY + ";" + Agents + ";" + Pills + ";"
                + Pads + ";" + Hostages;
        System.out.println(newGrid);
        path.remove(path.size() - 1);
        visualize(path, newGrid);
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
        if (grid[NeoX][NeoY] != null)
            grid[NeoX][NeoY] += " + Neo";
        else
            grid[NeoX][NeoY] = "Neo";

        // TELEPHONE BOOTH
        int TelephoneX = Integer.parseInt(table[3].split(",")[0]);
        int TelephoneY = Integer.parseInt(table[3].split(",")[1]);
        if (grid[TelephoneX][TelephoneY] != null)
            grid[TelephoneX][TelephoneY] += " + TB";
        else
            grid[TelephoneX][TelephoneY] = "TB";

        // AGENTS
        for (int i = 0; i < table[4].split(",").length; i += 2) {
            if (grid[Integer.parseInt(table[4].split(",")[i])][Integer.parseInt(table[4].split(",")[i + 1])] != null)
                grid[Integer.parseInt(table[4].split(",")[i])][Integer.parseInt(table[4].split(",")[i + 1])] += " + A";
            else
                grid[Integer.parseInt(table[4].split(",")[i])][Integer.parseInt(table[4].split(",")[i + 1])] = "A";
        }

        // PILLS
        for (int i = 0; i < table[5].split(",").length; i += 2) {
            if (grid[Integer.parseInt(table[5].split(",")[i])][Integer.parseInt(table[5].split(",")[i + 1])] != null)
                grid[Integer.parseInt(table[5].split(",")[i])][Integer.parseInt(table[5].split(",")[i + 1])] += " + P";
            else
                grid[Integer.parseInt(table[5].split(",")[i])][Integer.parseInt(table[5].split(",")[i + 1])] = "P";
        }

        // LAUNCH PADS
        for (int i = 0; i < table[6].split(",").length; i += 4) {
            String[] pads = table[6].split(",");
            if (grid[Integer.parseInt(pads[i])][Integer.parseInt(pads[i + 1])] != null)
                grid[Integer.parseInt(pads[i])][Integer.parseInt(pads[i + 1])] += " + Pad (" + pads[i + 2] + ","
                        + pads[i + 3] + ")";
            else
                grid[Integer.parseInt(pads[i])][Integer.parseInt(pads[i + 1])] = "Pad (" + pads[i + 2] + ","
                        + pads[i + 3] + ")";
            if (grid[Integer.parseInt(pads[i + 2])][Integer.parseInt(pads[i + 3])] != null)
                grid[Integer.parseInt(pads[i + 2])][Integer.parseInt(pads[i + 3])] += " + Pad (" + pads[i] + ","
                        + pads[i + 1] + ")";
            else
                grid[Integer.parseInt(pads[i + 2])][Integer.parseInt(pads[i + 3])] = "Pad (" + pads[i] + ","
                        + pads[i + 1] + ")";
        }

        // HOSTAGES
        if (table[7].length() != 0)
            for (int i = 0; i < table[7].split(",").length; i += 3) {
                String[] hostages = table[7].split(",");
                if (grid[Integer.parseInt(hostages[i])][Integer.parseInt(hostages[i + 1])] != null)
                    grid[Integer.parseInt(hostages[i])][Integer.parseInt(hostages[i + 1])] += " + H (" + hostages[i + 2]
                            + ")";
                else
                    grid[Integer.parseInt(hostages[i])][Integer.parseInt(hostages[i + 1])] = "H (" + hostages[i + 2]
                            + ")";
            }

        System.out.println(Arrays.deepToString(grid));
        // for (int i = 0; i < grid.length; i++) {
        // System.out.println();
        // for (int j = 0; j < grid[0].length; j++)
        // System.out.print(grid[i][j] + ", ");
        // }
    }

    public static Queue<Node> stateSpace(Node n) {
        Queue<Node> Children = new LinkedList<>();
        String[] NodeList = n.state.split(";");
        String[] NeoCord = NodeList[0].split(",");// [x,y]
        String[] Agents = NodeList[3].split(",");// [A0,X,Y],[A1,X,Y] A0:A0X:A0Y,A1:
        String[] Hostages = NodeList[4].split(",");
        int NeoX = Integer.parseInt(NeoCord[0]);
        int NeoY = Integer.parseInt(NeoCord[1]);
        String[] Pads = NodeList[8].split(",");
        String[] Pills = NodeList[9].split(",");
        String[] CarriedHostages = NodeList[2].split(",");
        String TB = NodeList[5];
        String NeoC = NodeList[0];
        // String[] SavedHostages = NodeList[6].split(",");
        int NeoDamage = Integer.parseInt(NodeList[1]);
        int CarryNumber = Integer.parseInt(NodeList[10]);
        Node action;
        boolean agentkilled = false;
        boolean isagenthere = false;
        boolean ishostagehere = false;
        boolean haveenoughcarry = false;
        boolean kill = false;

        if(!n.operator.equals("fly")) {
            for (int i = 0; i < Pads.length && !NodeList[8].isEmpty(); i = i + 2) { // [sp1x,sp1y,fp1x,fp1y] pads

                String[] PadsHelper1 = Pads[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                String[] PadsHelper2 = Pads[i + 1].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                int sPx = Integer.parseInt(PadsHelper1[1]);
                int sPy = Integer.parseInt(PadsHelper1[2]);
                int ePx = Integer.parseInt(PadsHelper2[1]);
                int ePy = Integer.parseInt(PadsHelper2[2]);
                if (((NeoX == sPx) && (NeoY == sPy)) || (((NeoX == ePx) && (NeoY == ePy)))) {
                    action = calculateMove("fly", n);
                    if (!checkstate(action)) {
                        Children.add(action);
                        addstate(action);
                    }
                }


            }
        }

        if(!n.operator.equals("drop") && !n.operator.equals("carry")) {
            for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) { // CARRY
                String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]

                String[] killed = NodeList[7].split(",");
                int Hx = Integer.parseInt(HostagesHelper[1]);
                int Hy = Integer.parseInt(HostagesHelper[2]);
                if ((NeoX == Hx && NeoY == Hy) && (!TB.equals(NeoC))
                        && !NodeList[3].contains(HostagesHelper[0])//agent
                        && !NodeList[7].contains(HostagesHelper[0])//killed
                        && !NodeList[6].contains(HostagesHelper[0])//saved
                        && !NodeList[2].contains(HostagesHelper[0])//already carried
                ) {
                    if ((CarryNumber > CarriedHostages.length) || (NodeList[2].isEmpty())) {
                        action = calculateMove("carry", n);
                        if (!checkstate(action)) {
                            Children.add(action);
                            addstate(action);
                        }
                    }
                }

            }
        }
        if(!n.operator.equals("drop") && !n.operator.equals("carry")) {
            if (NodeList[0].equals(NodeList[5])) { // DROP
                if (!NodeList[2].isEmpty()) {
                    action = calculateMove("drop", n);
                    if (!checkstate(action)) {
                        Children.add(action);
                        addstate(action);
                    }
                }
            }
        }
        if(!n.operator.equals("takePill")) {
            for (int i = 0; i < Pills.length && !NodeList[9].isEmpty(); i = i + 2) {// takePill
                int Px = Integer.parseInt(Pills[i]);
                int Py = Integer.parseInt(Pills[i + 1]);
                if (NeoX == Px && NeoY == Py) {
                    if (Integer.parseInt(NodeList[1]) < 100) {
                        action = calculateMove("takePill", n);
                        if (!checkstate(action)) {
                            Children.add(action);
                            addstate(action);
                        }
                    }
                }
            }
        }

        isagenthere =false;
        agentkilled =false;
        ishostagehere = false;
        if (NeoX > 0 && (!n.operator.equals("down"))) { // UP [A0:x:y],[A1:x:y]
            for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) { // CARRY
                String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                int Hx = Integer.parseInt(HostagesHelper[1]);
                int Hy = Integer.parseInt(HostagesHelper[2]);
                if (((NeoX-1) == Hx && NeoY == Hy) && (Integer.parseInt(HostagesHelper[3])==98)) {
                    ishostagehere = true;
                }

            }
            for (int i = 0; i < Agents.length && !NodeList[3].isEmpty(); i++) {
                String[] agentHelper = Agents[i].split(":");//[A0],[X],[Y]
                if (!Agents[i].isEmpty()) {
                    if (((NeoX - 1) == Integer.parseInt(agentHelper[1])) && (NeoY == Integer.parseInt(agentHelper[2]))) {
                        isagenthere =true;
                        //System.out.println("fe agent hena" + n.depth );
                        if (NeoDamage < 80 && !n.operator.equals("kill")) {
                            action = calculateMove("kill", n);//A1,x,y
                            if (!checkstate(action)) {
                                //System.out.println("2tlto : " +agentHelper.length);
                                agentkilled = true;
                                Children.add(action);
                                addstate(action);
                            }
                        }
                    }
                }
            }
            if (!isagenthere && !agentkilled && !ishostagehere) {
                action = calculateMove("up", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    addstate(action);
                    //agentkilled = false;
                }
            }
        }
        isagenthere =false;
        agentkilled =false;
        ishostagehere = false;

        if (NeoX < (M-1) && (!n.operator.equals("up"))) { // DOWN
            for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) { // CARRY
                String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                int Hx = Integer.parseInt(HostagesHelper[1]);
                int Hy = Integer.parseInt(HostagesHelper[2]);
                if (((NeoX+1) == Hx && NeoY == Hy) && (Integer.parseInt(HostagesHelper[3])==98)) {
                    ishostagehere = true;
                }

            }
            for (int i = 0; i < Agents.length && !NodeList[3].isEmpty(); i++) {
                String[] agentHelper = Agents[i].split(":");
                if (!Agents[i].isEmpty()) {
                    if (((NeoX + 1) == Integer.parseInt(agentHelper[1])) && (NeoY ==Integer.parseInt(agentHelper[2]))) {
                        isagenthere =true;
                        if (NeoDamage < 80 && !n.operator.equals("kill")) {
                            action = calculateMove("kill", n);
                            if (!checkstate(action)) {
                                agentkilled = true;
                                Children.add(action);
                                addstate(action);
                            }
                        }
                    }
                }
            }

            if (!isagenthere && !agentkilled && !ishostagehere) {
                action = calculateMove("down", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    addstate(action);
                    //agentkilled = false;
                }
            }
        }
        isagenthere =false;
        agentkilled =false;
        ishostagehere = false;

        if (NeoY > 0  && (!n.operator.equals("right"))) { // LEFT
            for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) { // CARRY
                String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                int Hx = Integer.parseInt(HostagesHelper[1]);
                int Hy = Integer.parseInt(HostagesHelper[2]);
                if ((NeoX == Hx && (NeoY-1) == Hy) && (Integer.parseInt(HostagesHelper[3])==98)) {
                    ishostagehere = true;
                }

            }
            for (int i = 0; i < Agents.length && !NodeList[3].isEmpty(); i++) {
                String[] agentHelper = Agents[i].split(":");
                int Ax = Integer.parseInt(agentHelper[1]);
                int Ay = Integer.parseInt(agentHelper[2]);
                if (!Agents[i].isEmpty()) {

                    if ((NeoX == Ax) && ((NeoY - 1) == Ay)) {
                        isagenthere = true;
                        if (NeoDamage < 80 && !n.operator.equals("kill")) {
                            action = calculateMove("kill", n);
                            if (!checkstate(action)) {
                                agentkilled = true;
                                Children.add(action);
                                addstate(action);
                            }
                        }
                    }
                }
            }
            if (!isagenthere && !agentkilled && !ishostagehere) {
                action = calculateMove("left", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    addstate(action);
                    //agentkilled = false;
                }

            }
        }
        isagenthere =false;
        agentkilled =false;
        ishostagehere = false;

        if (NeoY < (N-1)  && (!n.operator.equals("left"))) {// RIGHT
            for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) { // CARRY
                String[] HostagesHelper = Hostages[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                int Hx = Integer.parseInt(HostagesHelper[1]);
                int Hy = Integer.parseInt(HostagesHelper[2]);
                if ((NeoX == Hx && (NeoY+1) == Hy) && (Integer.parseInt(HostagesHelper[3])==98)) {
                    ishostagehere = true;
                }

            }
            for (int i = 0; i < Agents.length && !NodeList[3].isEmpty(); i++) {
                String[] agentHelper = Agents[i].split(":");
                if (!Agents[i].isEmpty()) {
                    if ((NeoX==Integer.parseInt(agentHelper[1])) && ((NeoY + 1) == Integer.parseInt(agentHelper[2])))
                        isagenthere =true;
                    if (NeoDamage < 80 && !n.operator.equals("kill")) {
                        action = calculateMove("kill", n);
                        if (!checkstate(action)) {
                            agentkilled = true;
                            Children.add(action);
                            addstate(action);
                        }
                    }
                }
            }
            if (!isagenthere && !agentkilled && !ishostagehere) {
                action = calculateMove("right", n);
                if (!checkstate(action)) {
                    Children.add(action);
                    addstate(action);
                    //agentkilled = false;
                }
            }
        }
        return Children;
    }


    public static Node calculateMove(String action, Node parent) {
        Node child = new Node("", parent, "", parent.depth + 1, 0, ""); // STATE PARENT OPERATOR DEPTH COST PATH
        // SPLITTING PARENT STATE
        String[] NodeList = parent.state.split(";");
        String[] NeoCord = NodeList[0].split(",");// [x,y]
        String[] Agents = NodeList[3].split(",");
        String[] Hostages = NodeList[4].split(",");
        int NeoX = Integer.parseInt(NeoCord[0]);
        int NeoY = Integer.parseInt(NeoCord[1]);
        String[] Pads = NodeList[8].split(",");
        String[] Pills = NodeList[9].split(",");
        String[] CarriedHostages = NodeList[2].split(",");
        String[] SavedHostages = NodeList[6].split(",");
        int NeoDamage = Integer.parseInt(NodeList[1]);
        int CarryNumber = Integer.parseInt(NodeList[10]);
        boolean actiondone= false;
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
                NeoPlace = (NeoX - 1) + "," + NeoY;
                actiondone=true;
                child.operator = "up";
                break;
            case "down":
                NeoPlace = (NeoX + 1) + "," + NeoY;
                actiondone=true;
                child.operator = "down";
                break;
            case "left":
                NeoPlace = NeoX + "," + (NeoY - 1);
                actiondone=true;
                child.operator = "left";
                break;
            case "right":
                NeoPlace = NeoX + "," + (NeoY + 1);
                actiondone=true;
                child.operator = "right";
                break;
            case "carry":
                //NewCarriedHostages="";
                for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) {
                    String[] HostagesHelper = Hostages[i].split(":");
                    if (((NeoX==Integer.parseInt(HostagesHelper[1])) && (NeoY == Integer.parseInt(HostagesHelper[2]))) && !NewCarriedHostages.contains(HostagesHelper[0])) {
                        if (NewCarriedHostages.length() != 0)
                            NewCarriedHostages += ",";
                        NewCarriedHostages += HostagesHelper[0];
                    }
                }
                actiondone=true;
                child.operator = "carry";
                break;
            case "drop":
                //NewSavedHostages="";
                for (int i = 0; i < CarriedHostages.length && !NodeList[2].isEmpty(); i++) {
                    if (NewSavedHostages.length() != 0)
                        NewSavedHostages += ",";
                    NewSavedHostages += CarriedHostages[i];
                    CarriedHostages[i] = "";
                }
                NewCarriedHostages = "";
                actiondone=true;
                child.operator = "drop";
                break;
            case "takePill":
                NewPills = "";
                for (int i = 0; i < Pills.length && !NodeList[9].isEmpty(); i = i + 2) {
                    int Px = Integer.parseInt(Pills[i]);
                    int Py = Integer.parseInt(Pills[i+1]);
                    if (NeoX == Px && NeoY == Py) {
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
                                if (NodeList[6].contains(HostagesHelper[0])){
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
                if(!NewPills.isEmpty()){
                    NewPills = NewPills.substring(0, NewPills.length() - 1);}
                actiondone=true;
                child.operator = "takePill";
                break;
            case "fly":
                for (int i = 0; i < Pads.length && !NodeList[8].isEmpty(); i = i + 2) { // [sp1x,sp1y,fp1x,fp1y]
                    String[] PadsHelper1 = Pads[i].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                    String[] PadsHelper2 = Pads[i+1].split(":");// [[H1,H1X,H1Y,H1D],,,,]
                    int sPx = Integer.parseInt(PadsHelper1[1]);
                    int sPy = Integer.parseInt(PadsHelper1[2]);
                    int ePx = Integer.parseInt(PadsHelper2[1]);
                    int ePy = Integer.parseInt(PadsHelper2[2]);
                    if (NeoX == sPx && NeoY == sPy) {
                        NeoPlace = ePx + "," + ePy;
                    }
                    if (NeoX == ePx && NeoY == ePy) {
                        NeoPlace = sPx + "," + sPy;
                    }
                }
                //NeoPlace = (NeoX - 1) + "," + NeoY;
                actiondone=true;
                child.operator = "fly";
                break;
            case "kill":
                if(!NodeList[3].isEmpty()){
                    TotalAgents="";
                    for (int i = 0; i < Agents.length && !NodeList[3].isEmpty(); i++) { //[A0...,A1...]
                        boolean iskilled = false;
                        String[] agentHelper = Agents[i].split(":"); //A0:AX:AY
                        if (!Agents[i].isEmpty() && !NodeList[7].contains(agentHelper[0])) {
                            if (((NeoX - 1) == Integer.parseInt(agentHelper[1]))&& (NeoY == Integer.parseInt(agentHelper[2]))) {
                                if (Killed.length() != 0)
                                    Killed += ",";
                                Killed += agentHelper[0];
                                child.operator = "kill";
                                actiondone=true;
                                iskilled = true;
                            }
                            if (((NeoX + 1) == Integer.parseInt(agentHelper[1]))&& (NeoY == Integer.parseInt(agentHelper[2]))) {
                                if (Killed.length() != 0)
                                    Killed += ",";
                                Killed += agentHelper[0];
                                child.operator = "kill";
                                actiondone=true;
                                iskilled = true;

                            }
                            if ((NeoX == Integer.parseInt(agentHelper[1]))&& ((NeoY - 1) == Integer.parseInt(agentHelper[2]))) {
                                if (Killed.length() != 0)
                                    Killed += ",";
                                Killed += agentHelper[0];
                                child.operator = "kill";
                                actiondone=true;
                                iskilled = true;
                            }
                            if ((NeoX == Integer.parseInt(agentHelper[1]))&& ((NeoY + 1) == Integer.parseInt(agentHelper[2]))) {
                                if (Killed.length() != 0)
                                    Killed += ",";
                                Killed += agentHelper[0];
                                actiondone=true;
                                child.operator = "kill";
                                iskilled = true;

                            }
                            if(!iskilled) {
                                if (TotalAgents.length() != 0) {
                                    TotalAgents += "," +Agents[i];
                                } else {
                                    TotalAgents = Agents[i] ;
                                }
                            }
                        }
                    }
                }
                if(child.operator.equals("kill")){
                    NeoDamage += 20;
                }
                break;
            default:
                break;
        }
        for (int i = 0; i < Hostages.length && !NodeList[4].isEmpty(); i++) { // EveryTick
            String[] HostagesHelper = Hostages[i].split(":");
            //String[] killed = NodeList[7].split(",");
            boolean wasagent = false;
            boolean wasCarried = false;
            boolean isKilled = false;
            boolean wasSaved = false;
            boolean isCarried = false;
            boolean isSaved = false;
            boolean wasKilled = false;
            //boolean isAgent = false;
            if (NodeList[2].length() != 0 && (child.operator.equals("up")
                    ||child.operator.equals("down")
                    ||child.operator.equals("left")
                    ||child.operator.equals("right"))) {
                if (NewCarriedHostages.contains(HostagesHelper[0])){
                    int NewNeoX = Integer.parseInt(NeoPlace.split(",")[0]);
                    int NewNeoY = Integer.parseInt(NeoPlace.split(",")[1]);
                    HostagesHelper[1] = String.valueOf(NewNeoX);
                    HostagesHelper[2] = String.valueOf(NewNeoY);
                }
                String hostage = HostagesHelper[0] + ":" + HostagesHelper[1] + ":" + HostagesHelper[2] + ":"
                        + HostagesHelper[3];
                Hostages[i] = hostage;

            }
            for(int J = 0 ; J< Agents.length && !NodeList[3].isEmpty() ; J++){ //if he is already agent or he will be killed
                String[] agentHelper = Agents[J].split(":");
                if(!Agents[J].isEmpty()) {
                    if ((((NeoX - 1) == Integer.parseInt(agentHelper[1]))&& (NeoY == Integer.parseInt(agentHelper[2])))
                            || (((NeoX + 1) == Integer.parseInt(agentHelper[1]))&& (NeoY == Integer.parseInt(agentHelper[2])))
                            || ((NeoX == Integer.parseInt(agentHelper[1]))&& ((NeoY - 1) == Integer.parseInt(agentHelper[2])))
                            || ((NeoX == Integer.parseInt(agentHelper[1]))&& ((NeoY + 1) == Integer.parseInt(agentHelper[2])))) {
                        isKilled = true;
                    }

                    if (agentHelper[0].equals(HostagesHelper[0])) { //if he was agent
                        wasagent = true;
                    }
                }
            }

            if(NodeList[2].contains(HostagesHelper[0])){//carriedHostages List
                wasCarried = true;
            }
            if(NodeList[7].contains(HostagesHelper[0])){//killedHostages List
                wasKilled = true;
            }
            if(NodeList[6].contains(HostagesHelper[0])){ //SavedHostages List
                wasSaved = true;
            }

            String NeoC = NodeList[0];
            String TB = NodeList[5];
            int Hx = Integer.parseInt(HostagesHelper[1]);
            int Hy = Integer.parseInt(HostagesHelper[2]);
            if ((NeoX == Hx && NeoY == Hy) && (!TB.equals(NeoC)) ){
                if ((CarryNumber > CarriedHostages.length || (NodeList[2].isEmpty() && CarryNumber>0) )&& !wasagent && !wasKilled && !wasCarried){
                    isCarried = true;
                }
            }


            if(NodeList[0].equals(NodeList[5]) && !NodeList[2].isEmpty()){ // if he will be saved
                isSaved = true;
            }

            if(!wasSaved && !isSaved && actiondone){
                HostagesHelper[3] = String.valueOf(Integer.parseInt(HostagesHelper[3]) + 2);
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


    public static String NodePaths(Node n , int nodes){

        String[] stateAttributes = n.state.split(";");
        String Hostages = stateAttributes[4];//
        String[] HostageArray = Hostages.split(",");//
        String path = "";
        int deaths=0;
        int kills=0;
        for (int i = 0; i < HostageArray.length &&!Hostages.isEmpty() ; i++) {
            String[] HostagesHelper = HostageArray[i].split(":");
            if(HostagesHelper[3].equals("100")){
                deaths++;
            }
        }
        if(!stateAttributes[7].isEmpty()){
            kills = stateAttributes[7].split(",").length;
        }
        while(n.parent != null) {
            System.out.println(n.state);
            System.out.println(n.operator);
            if(!path.isEmpty()){
                if(!n.operator.isEmpty()){
                    path = n.operator + "," +path;
                }
            }
            else{
                path = n.operator;
            }
            n = n.parent;
        }
        return path + ";" + deaths + ";" + kills + ";" + nodes;
    }
    public static String genericSearch(PriorityQueue<Node> Q , searchProblems P) {
        //Queue <Node> Q= new LinkedList<>();
        Node Initial = P.InitialState;
        Q.add(Initial);
        int nodes = 1;
        Node node = new Node();
        boolean failure = true;
        while (!Q.isEmpty()){
            if(P.isGoalState(Q.peek())==1){
                node = Q.remove();
                failure = false;
                break;
            }else{
                Queue <Node> newQ = stateSpace(Q.remove());
                while(!newQ.isEmpty()){
                    Q.add(newQ.remove());
                }
            }
            nodes++;
        }
        if(failure){
            return "No Solution";
        }
        else{
            return NodePaths(node,nodes);
        }
    }
    public static String genericSearchWithLimit(PriorityQueue<Node> Q , searchProblems P) {
        //Queue <Node> Q= new LinkedList<>();
        Node Initial = P.InitialState;
        int nodes = 1;
        Node node = new Node();
        boolean failure = true;
        int limit = 0;
        while(failure) {
            nodes = 1;
            Q.add(Initial);
            stateSet = new HashSet<>();
            while (!Q.isEmpty()) {
                if (P.isGoalState(Q.peek()) == 1) {
                    node = Q.remove();
                    failure = false;
                    break;
                } else {
                    Queue<Node> newQ = stateSpace(Q.remove());
                    while (!newQ.isEmpty()) {
                        if(newQ.peek().depth <= limit){
                            Q.add(newQ.remove());
                        }
                        else{
                            newQ.remove();
                        }
                    }
                }
                nodes++;
            }
            limit++;
        }
        return NodePaths(node,nodes);

    }




    public static void main(String[] args) {
        //String example =
        //"5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
        //visualize(example);
        // genGrid();
        // for (Node s : Qnew)
        // System.out.print(s.cost);
    }
}