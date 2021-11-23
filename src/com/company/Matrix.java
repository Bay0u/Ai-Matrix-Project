package com.company;

import java.util.Arrays;

class Node{
    String state="Nx,Ny;NeoDamage;Carried:H1,H2;TotalAgents:A1,A2,H4;TotalHostages:H1:100,H2:70,H3:30,H4:100,H6:50,H5:10;Tx,Ty;HostagesSaved:H2,H3;Killed:H1,A3"; // size current position
    Node parent;
    String operator="up";//up:cost+10,down:cost+10,left:cost+10,right:cost+10,carry:cost-10,drop:-10,takePill:-10,kill:+10,fly:+10";
    int depth; // ?
    int cost;
    //Node Goal;//THIS SHOULD BE IN SEARCH PROBLEM done
    // Cost penalty if hostage dies done
    // Cost penalty if Neo dies done
    // Cost penalty if carried hostage in TB done
    // Cost penalty dropped outside of TB done
    // Cost penalty if agent killed done
    // Cost penalty if move out of bound ?
    // Cost reward if hostage dropped at TB done
    // Cost reward if pill taken saved a hostage done

    String[] stringarray = state.split(";");    //we can use dot, whitespace, any character


    boolean AgentKilled(){
        String Agents = stringarray[3];//[A1:30,A2:50,A3:10]
        String[] AgentsArray = Agents.split(",");//[[A1:30],[A2:50],[A3:10]]
        for (int i =0;i<AgentsArray.length;i++){
            String[] Hostage = AgentsArray[i].split(":");//[A1],[30]
            if(Hostage[1]=="100"){ //CHECKS THE HP
                return true;//1) cost+ 2)remove from state
            }
        }
        return false;
    }



    //breadthFirst
    //depthFirst
    //iterativeDeepeningSearch
    //uniformCostSearch
    //greedySearch
    //aStarSearch
//    Example Input Grid: 5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;
//0,0,30,3,0,80,4,4,80
}

class searchProblems{
    //Operators
    String Operators = "up,down,left,right,carry,drop,takePill,kill,fly";
    //initial state
    Node InitialState;
    //goal test
    //path cost
    boolean isNeoinHome(String [] stringarray){
        String Neoplace = stringarray[0];
        String Telephone = stringarray[5];
        if(Neoplace == Telephone){
            return true;
        }else{
            return false;
        }
    }
    boolean isNeoDead(String [] stringarray){
        if(stringarray[1]=="100"){
            return true;
        }else{
            return false;
        }
    }
    boolean areHostagesSaved(String [] stringarray){
        String Hostages = stringarray[4];//[H1:30,H2:50,H3:10]
        String[] HostageArray = Hostages.split(",");//[H1:30],[H2:50],[H3:10]
        String Saved = stringarray[6];//HostagesSaved:H1,H2,H3
        String[] SavedHostageArray = Saved.split(",");//H1,H2,H3
        int numberOfKilledHostages = 0;
        for (int i =0;i<HostageArray.length;i++){
            String[] Hostage = HostageArray[i].split(":");//[H1][30]
            if(Hostage[1]=="100"){ //CHECKS THE Damage
                String myHostage = Hostage[0];
                String Agents = stringarray[3];//TotalAgents:A1,A2,H4
                String[] agent = Agents.split(",");
                String Killed = stringarray[7];//Killed:H1,A3
                String[] killed = Killed.split(",");
                for(int j = 0;j<agent.length;j++){
                    if(myHostage == agent[j]){
                        return false; // as there is a converted hostage is still alive not saved or killed
                    }
                }
                for(int k = 0;k<agent.length;k++){
                    if(myHostage == killed[k]){
                        numberOfKilledHostages++; // as there is a converted hostage is killed
                    }
                }
            }
        }
        if(SavedHostageArray.length + numberOfKilledHostages == HostageArray.length /*&&
        Hostages.length()==0*/){
            return true;
        }
        else {
            return false;
        }
    }
    public boolean isGoalState(Node node){
        //chick place neo x,y = telephone
        //chick total number of Hostages == [saved Hostages + Killed Hostages whose changed to agent]
        //saved hostage ignored carried and Hostages info in state
        //Chick neo hp isn't 0
        String[] stringarray = node.state.split(";");
        if(isNeoinHome(stringarray) && areHostagesSaved(stringarray) && !isNeoDead(stringarray)){
            return true;
        }
        return false;
    }
    public void pathCost(Node node){
        //operator up:cost+10,down:cost+10,left:cost+10,right:cost+10,carry:cost-20,drop:-20,takePill:-20,kill:+20,fly:+10";
        //hostage died : cost+10
        String[] stringarray = node.state.split(";");
        switch (node.operator){
            case "up":node.cost=node.cost+10;
            case "down":node.cost=node.cost+10;
            case "left":node.cost=node.cost+10;
            case "right":node.cost=node.cost+10;
            case "carry":if(stringarray[0]!=stringarray[5]){ //carry shouldn't be in the telphone
                node.cost=node.cost-20;}else{
                node.cost=node.cost+10;}
            case "drop":if(stringarray[0]==stringarray[5]){ //drop should be in the telphone only
                node.cost=node.cost-20;}else{
                node.cost=node.cost+10;}
            case "takePill":node.cost=node.cost-20;
            case "kill":node.cost=node.cost+20;
            case "fly":node.cost=node.cost+10;
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

    public static String solve(String grid, String strategy, boolean visualize) {//
        String [] OurGrid = grid.split(";");
        searchProblems p1;
        Node n;

        String plan = ""; // up, down, left, right, carry, drop, takePill, kill, and fly
        String deaths = "";
        String kills = "";
        String nodes = "";
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

    public static void main(String[] args) {
        // String example =
        // "5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
        // visualize(example);
        // genGrid();
    }
}