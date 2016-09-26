package se.lu.lucs.dota2.framework.game;

import java.util.Arrays;
import java.util.Map;

public class BaseNPC extends BaseEntity {
    protected int level;
    protected float[] origin;
//                    --unit.absOrigin = VectorToString(eunit:GetAbsOrigin())
//                    --unit.center = VectorToString(eunit:GetCenter())

    protected boolean alive;
    protected boolean blind;
    protected boolean dominated;
    protected boolean deniable;
    protected boolean disarmed;
    protected boolean rooted;
    protected String name;
    protected String unitName;
    protected String label;
    protected int aggroTarget;
    protected int team;
    protected float attackRange;
    protected int attackTarget;
    protected float mana;
    protected float maxMana;

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    protected Map<Integer, Ability> abilities;

    protected BaseNPC() {
        origin = new float[3];
    }

    public Map<Integer, Ability> getAbilities() {
        return abilities;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public int getAttackTarget() {
        return attackTarget;
    }

    public int getLevel() {
        return level;
    }

    public float getMana() {
        return mana;
    }

    public float getMaxMana() {
        return maxMana;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float[] getOrigin() {
        return origin;
    }

    public int getTeam() {
        return team;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isBlind() {
        return blind;
    }

    public boolean isDeniable() {
        return deniable;
    }

    public boolean isDisarmed() {
        return disarmed;
    }

    public boolean isDominated() {
        return dominated;
    }

    public boolean isRooted() {
        return rooted;
    }

    public void setAbilities( Map<Integer, Ability> abilities ) {
        this.abilities = abilities;
    }

    public void setAlive( boolean alive ) {
        this.alive = alive;
    }

    public void setAttackRange( float attackRange ) {
        this.attackRange = attackRange;
    }

    public void setAttackTarget( int attackTarget ) {
        this.attackTarget = attackTarget;
    }

    public void setBlind( boolean blind ) {
        this.blind = blind;
    }

    public void setDeniable( boolean deniable ) {
        this.deniable = deniable;
    }

    public void setDisarmed( boolean disarmed ) {
        this.disarmed = disarmed;
    }

    public void setDominated( boolean dominated ) {
        this.dominated = dominated;
    }

    public void setLevel( int level ) {
        this.level = level;
    }

    public void setMana( float mana ) {
        this.mana = mana;
    }

    public void setMaxMana( float maxMana ) {
        this.maxMana = maxMana;
    }

    @Override
    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public void setOrigin( float[] origin ) {
        this.origin = origin;
    }

    public void setRooted( boolean rooted ) {
        this.rooted = rooted;
    }

    public void setTeam( int team ) {
        this.team = team;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getAggroTarget() {
        return aggroTarget;
    }

    public void setAggroTarget(int aggroTarget) {
        this.aggroTarget = aggroTarget;
    }

    @Override
    public String toString() {
        return "BaseNPC{" +
                "level=" + level +
                ", origin=" + Arrays.toString(origin) +
                ", alive=" + alive +
                ", blind=" + blind +
                ", dominated=" + dominated +
                ", deniable=" + deniable +
                ", disarmed=" + disarmed +
                ", rooted=" + rooted +
                ", name='" + name + '\'' +
                ", unitName='" + unitName + '\'' +
                ", label='" + label + '\'' +
                ", aggroTarget=" + aggroTarget +
                ", team=" + team +
                ", attackRange=" + attackRange +
                ", attackTarget=" + attackTarget +
                ", mana=" + mana +
                ", maxMana=" + maxMana +
                ", abilities=" + abilities +
                '}';
    }
}
