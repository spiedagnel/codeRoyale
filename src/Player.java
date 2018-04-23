import java.util.*;
import java.util.function.Predicate;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
 
class Point{
    
    int x;
    int y;
    int radius;
    
    Point(int x, int y, int radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    
    int distance2(Point p){
        return (this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y);
    }
}

class Site extends Point {
    private int structureType;
    private int id;
    private int owner;
    private int creepType;
    private int param1;
    private int param2;

    Site(int x, int y, int id, int type, int radius) {
        super(x, y, radius);
        this.structureType = type;
        this.id = id;
    }

    void setStructureType(int structureType) {
        this.structureType = structureType;
    }

    int getId() {
        return this.id;
    }

    int getStructureType() {
        return this.structureType;
    }

    void setCreepType(int type) {
        this.creepType = type;
    }

    int getCreepType() {
        return creepType;
    }

    int getParam1() {
        return param1;
    }

    public int getParam2() {
        return param2;
    }

    void update(int structureType, int owner, int param1, int param2) {
        this.structureType = structureType;
        this.owner = owner;
        this.param1 = param1;
        this.param2 = param2;
    }

    int getOwner() {
        return this.owner;
    }

    @Override
    public String  toString() {
        return "Site{" +
                "x=" + x +
                ", y=" + y +
                ", radius=" + radius +
                ", structureType=" + structureType +
                ", id=" + id +
                ", owner=" + owner +
                ", creepType=" + creepType +
                '}';
    }
}

class Unit extends Point{

    private int owner;
    private int type;
    private int health;

    Unit(int x, int y, int owner, int type, int health) {
        super(x, y, 30);
        this.owner = owner;
        this.type = type;
        this. health = health;
    }

    boolean isMine(){
        return this.owner == 0;
    }

    boolean isQueen(){
        return this.type == -1;
    }

    int getType(){
        return this.type;
    }
}

class Player {
    
    private Site[] sites;
    private ArrayList<Unit> units = new ArrayList<>();
    private Unit myQueen;
    private Unit ennemyQueen;

    private int gold = 0;
    private int touchedSite;
    private int nbArchers;
    private int nbArchersSites;
    private int nbGiantsSites;
    private int nbKnights;
    private int nbGiants;
    private int nbKnightsSites;
    private boolean buildArchers = true;
    private int nbMines;
    private int nbEnnemyMines;
    private int nbTowers;

    private final int KNIGHT = 0;
    private final int ARCHER = 1;
    private final int GIANT = 2;
    private final int OWNER_NONE = -1;
    private final int OWNER_FRIENDLY = 0;
    private final int OWNER_ENNEMY = 1;
    private final int STRUCTURE_TYPE_NONE = -1;
    private final int STRUCTURE_TYPE_MINE = 0;
    private final int STRUCTURE_TYPE_TOWER = 1;
    private final int STRUCTURE_TYPE_BARRACK = 2;
    private boolean hasIncreasedRate;


    private void setSites(Site[] sites){
        this.sites = sites;
    }

    private void updateSite(int siteId, int structureType, int owner, int param1, int param2){
        Site site = this.sites[siteId];
        site.update(structureType, owner, param1, param2);
        if(site.getOwner() == OWNER_FRIENDLY){
            if(site.getStructureType() == STRUCTURE_TYPE_BARRACK) {
                if (site.getCreepType() == KNIGHT)
                    nbKnightsSites++;
                else if (site.getCreepType() == ARCHER)
                    nbArchersSites++;
                else if (site.getCreepType() == GIANT)
                    nbGiantsSites++;
            }else if (site.getStructureType()==STRUCTURE_TYPE_MINE){
                nbMines ++;
            }else
                nbTowers++;
        }else if (site.getStructureType()==STRUCTURE_TYPE_MINE){
                nbEnnemyMines ++;
        }
    }

    private void setGold(int gold){
        this.gold = gold;
    }

    private void addUnit(Unit u){
        this.units.add(u);
        if(u.isQueen()) {
            if(u.isMine())
                myQueen = u;
            else
                ennemyQueen = u;
        }else if (u.isMine()){
            if(u.getType() == ARCHER)
                nbArchers ++;
            else if(u.getType() == KNIGHT)
                nbKnights ++;
            else if(u.getType() == GIANT)
                nbGiants ++;
        }
    }

    private Site findClosestSiteFromQueen(Unit queen, Predicate<Site> p){
        Site closest = null;
        int dist = Integer.MAX_VALUE;
        for (Site site : sites) {
            int i1 = queen.distance2(site);
            if (i1 < dist && p.test(site)) {
                closest = site;
                dist = i1;
            }
        }
        return closest;
    }

    private Site findMostDistantSiteFromQueen(Unit queen, Predicate<Site> p){
        Site closest = null;
        int dist = 0;
        for (Site site : sites) {
            int i1 = queen.distance2(site);
            if (i1 > dist && p.test(site)) {
                closest = site;
                dist = i1;
            }
        }
        return closest;
    }

    private void printQueenAction(){
        if(this.touchedSite != -1  && sites[touchedSite].getOwner()!= OWNER_FRIENDLY ) {
            if (nbGiantsSites == 0 ) {
                System.out.println("BUILD " + touchedSite + " BARRACKS-GIANT");
                sites[touchedSite].setCreepType(GIANT);
            }else if(nbKnightsSites == 0){
                System.out.println("BUILD " + touchedSite + " BARRACKS-KNIGHT");
                sites[touchedSite].setCreepType(KNIGHT);
            } else {
                if (nbMines < nbTowers) {
                    System.out.println("BUILD " + touchedSite + " MINE");
                    hasIncreasedRate = false;
                }else
                    System.out.println("BUILD " + touchedSite + " TOWER");
            }
        }else if(this.touchedSite != -1 && sites[touchedSite].getStructureType() == STRUCTURE_TYPE_MINE && !hasIncreasedRate) {
            System.out.println("BUILD " + touchedSite + " MINE");
            hasIncreasedRate = true;
        }else if(this.touchedSite != -1 && sites[touchedSite].getStructureType() == STRUCTURE_TYPE_TOWER &&sites[touchedSite].getParam1() < 700 ){
                System.out.println("BUILD " + touchedSite + " TOWER");
                hasIncreasedRate = true;
        }else{
           // if(nbArchersSites==0 || nbKnightsSites==0) {
           if(nbMines>nbEnnemyMines){
                Site dest = findMostDistantSiteFromQueen(ennemyQueen, s -> s.getOwner() == OWNER_FRIENDLY && s.getStructureType() == STRUCTURE_TYPE_TOWER);
                if (dest != null) {
                    System.out.println("MOVE " + (dest.x) + " " + (dest.y));
                    return;
                }
           }
                Site dest = findClosestSiteFromQueen(myQueen, s -> s.getOwner() != OWNER_FRIENDLY && s.getStructureType() != STRUCTURE_TYPE_TOWER);
                System.out.println("MOVE " + (dest.x) + " " + (dest.y));

//            }else{
//                Site dest = findClosestSiteFromQueen(myQueen, s -> s.getOwner() == 0 && s.getCreepType() == 1);
//                System.out.println("MOVE " + (dest.x) + " " + (dest.y));
//            }
        }
    }

    private void resetUnits(){
        this.units.clear();
        nbKnights  = 0;
        nbKnightsSites  = 0;
        nbGiantsSites  = 0;
        nbArchers = 0;
        nbGiants = 0;
        nbArchersSites = 0;
        nbMines=0;
        nbEnnemyMines=0;
        nbTowers=0;
    }

    private void printTrainingAction() {
        boolean actionTaken;
        System.err.println(nbArchers+" "+nbKnights);
        if(nbGiants==0){
            actionTaken = printTrainingGiants();
        }else{
            actionTaken = printTrainingKnights();
       }
        if(!actionTaken)
            System.out.println("TRAIN");
    }

    private boolean printTrainingKnights() {
        Site site = findClosestSiteFromQueen(ennemyQueen, s -> s.getOwner() == OWNER_FRIENDLY && s.getStructureType() == STRUCTURE_TYPE_BARRACK && s.getCreepType() == KNIGHT );
        if (site !=null) {
            System.out.println("TRAIN " + site.getId());
            return true;
        }
        return false;
    }

    private boolean printTrainingGiants() {
        Site site = findClosestSiteFromQueen(ennemyQueen, s -> s.getOwner() == OWNER_FRIENDLY && s.getStructureType() == STRUCTURE_TYPE_BARRACK && s.getCreepType() == GIANT );
        if (site !=null) {
            System.out.println("TRAIN " + site.getId());
            return true;
        }
        return false;
    }

    private boolean printTrainingArchers() {
        Site site = findClosestSiteFromQueen(myQueen, s -> s.getOwner() == OWNER_FRIENDLY && s.getCreepType() == ARCHER );
        if (site !=null) {
            System.out.println("TRAIN " + site.getId());
            return true;
        }
        return false;
    }

    private void setTouchedSite(int touchedSite) {
        this.touchedSite = touchedSite;
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numSites = in.nextInt();

        Player p = new Player();

        Site[] sites = new Site[numSites];
        for (int i = 0; i < numSites; i++) {
            int siteId = in.nextInt();
             int x = in.nextInt();
            int y = in.nextInt();
            int radius = in.nextInt();
            sites[siteId] = new Site(x, y, siteId, -1, radius);
        }
        p.setSites(sites);

        int structureType = -1;
        int x = 0;
        int y =0;
        int siteId = 0;
        int touchedSite = 0;
        int trainingSite =0;
        // game loop
        while (true) {
            int gold = in.nextInt();
            p.setGold(gold);
            p.resetUnits();
            touchedSite = in.nextInt(); // -1 if none
            p.setTouchedSite(touchedSite);
            for (int i = 0; i < numSites; i++) {
                siteId = in.nextInt();
                int ignore1 = in.nextInt(); // used in future leagues
                int ignore2 = in.nextInt(); // used in future leagues
                structureType = in.nextInt(); // -1 = No structure, 2 = Barracks
                int owner = in.nextInt(); // -1 = No structure, 0 = Friendly, 1 = Enemy
                int param1 = in.nextInt();
                int param2 = in.nextInt();
                p.updateSite(siteId, structureType, owner, param1, param2);
            }
            int numUnits = in.nextInt();
            for (int i = 0; i < numUnits; i++) {
                x = in.nextInt();
                y = in.nextInt();
                int owner = in.nextInt();
                int unitType = in.nextInt(); // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
                int health = in.nextInt();
                p.addUnit(new Unit(x, y, owner, unitType, health));
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // First line: A valid queen action
            // Second line: A set of training instructions
            p.printQueenAction();
            p.printTrainingAction();

        }
    }


}