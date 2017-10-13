import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by oc on 2017/10/12.
 */
public class Node {


    // 节点类型
    int type;

    // 当前各种属性
    // int hp, atk, def, mdef, yellow, blue, red, pickaxe, bomb;
    Hero hero=null;
    Item item=null;
    ArrayList<Integer> doors;
    ArrayList<Monster> monsters;

    boolean valid;

    // 坐标
    int f,x,y;

    HashSet<Node> linked;

    /*
    public Node(int _hp, int _atk, int _def, int _mdef, int _type, int _f, int _x, int _y) {
        hp=_hp; atk=_atk; def=_def; mdef=_mdef; type=_type; f=_f; x=_x; y=_y;
        yellow=0; blue=0; red=0; pickaxe=0; bomb=0;
        monsters=new ArrayList<>();
        linked=new HashSet<>();
        valid=true;
    }
    */
    public Node(int _type, Hero _hero, Monster monster, Item _item, int door, int _f, int _x, int _y) {
        type=_type;
        hero=_hero;
        monsters=new ArrayList<>();
        if (monster!=null) monsters.add(monster);
        item=_item;
        doors=new ArrayList<>();
        if (door!=0) doors.add(door);
        f=_f; x=_x; y=_y;
        linked=new HashSet<>();
        valid=true;
    }

    public void addNode(Node another) {
        linked.add(another);
    }

    public Node merge(Node another, boolean[][][] visited) {
        Node node=null;

        if (hero!=null) {
            // merge hero
            node = new Node(type, new Hero(hero), null, null, 0, another.f, another.x, another.y);

            // get item
            if (another.item!=null)
                node.hero.getItem(another.item);

            // open doors...
            for (int v: another.doors) {
                if (v==1) node.hero.yellow--;
                if (v==2) node.hero.blue--;
                if (v==3) node.hero.red--;
            }

            // beat monsters...
            for (Monster monster: another.monsters) {
                node.hero.hp -= Util.getDamage(node.hero.atk, node.hero.def, node.hero.mdef, monster.hp,
                        monster.atk, monster.def, monster.special);
            }

            if (node.hero.yellow<0 || node.hero.blue<0 || node.hero.red<0 || node.hero.hp<=0)
                return null;

            // 加边
            for (Node to: linked) {
                if (!visited[to.f][to.x][to.y])
                    node.addNode(to);
            }
            for (Node to: another.linked) {
                if (!visited[to.f][to.x][to.y])
                    node.addNode(to);
            }

            return node;
        }

        /*

        // 非怪物
        if (!another.isMonster()) {
            node = new Node(hp+another.hp, atk+another.atk, def+another.def,
                    mdef+another.mdef, type, another.f, another.x, another.y);
        }
        // 怪物
        else {
            int damage = Util.getDamage(atk, def, mdef, another.hp, another.atk, another.def, another.mdef);
            node = new Node(hp-damage, atk, def, mdef, type, another.f, another.x, another.y);
        }

        node.yellow = yellow+another.yellow;
        node.red = red+another.red;
        node.blue = blue+another.blue;
        node.pickaxe=pickaxe+another.pickaxe;
        node.bomb=bomb+another.bomb;

        // 加边
        for (Node to: linked) {
            if (!visited[to.f][to.x][to.y])
                node.addNode(to);
        }
        for (Node to: another.linked) {
            if (!visited[to.f][to.x][to.y])
                node.addNode(to);
        }

        */

        return node;
    }

    public boolean equals(Object another) {
        return (another instanceof Node) && hashCode()==another.hashCode();
    }

    public int hashCode() {
        return 1000000*f+1000*x+y;
    }

    /*
    public boolean isDoor() {
        return type==Graph.DOOR_YELLOW || type==Graph.DOOR_BLUE || type==Graph.DOOR_RED;
    }

    public boolean isMonster() {
        return type>=Graph.MONSTER_BOUND;
    }

    public boolean isItem() {
        return !isDoor() && !isMonster();
    }
    */

    public int getScore() {
        // return hp+1000*(atk+def)+60*mdef+300*yellow+450*blue+600*red;
        return hero==null?0:hero.getScore();
    }

    public boolean shouldEat(Hero hero) {
        if (this.hero!=null) return false;
        if (item!=null) return true;
        if (!doors.isEmpty()) return false;
        int damage=0;
        for (Monster monster: monsters)
            damage+=Util.getDamage(hero.atk, hero.def, hero.mdef, monster.hp, monster.atk, monster.def, monster.special);
        return damage==0;
    }

    public String toString() {
        StringBuilder builder=new StringBuilder();
        builder.append(String.format("(%s,%s,%s)", f, x, y));
        if (hero!=null) {
            builder.append(" -- Hero: ").append(hero.toString());
        }
        if (item!=null) {
            builder.append(" -- Item: ").append(item.toString());
        }
        if (doors.size()>0) {
            builder.append(" -- Doors: ").append(doors.toString());
        }
        if (monsters.size()>0) {
            builder.append(" -- Monsters: ").append(monsters.toString());
        }
        builder.append(" -- ").append(linked.size()).append(" Linked");
        return builder.toString();
    }

}
