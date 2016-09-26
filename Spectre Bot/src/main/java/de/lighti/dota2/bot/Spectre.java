package de.lighti.dota2.bot;

import java.util.*;
import java.util.stream.Collectors;

import se.lu.lucs.dota2.framework.bot.BaseBot;
import se.lu.lucs.dota2.framework.bot.Bot;
import se.lu.lucs.dota2.framework.bot.BotCommands.LevelUp;
import se.lu.lucs.dota2.framework.bot.BotCommands.Select;
import se.lu.lucs.dota2.framework.game.Ability;
import se.lu.lucs.dota2.framework.game.BaseEntity;
import se.lu.lucs.dota2.framework.game.BaseNPC;
import se.lu.lucs.dota2.framework.game.ChatEvent;
import se.lu.lucs.dota2.framework.game.Hero;
import se.lu.lucs.dota2.framework.game.Tower;
import se.lu.lucs.dota2.framework.game.World;

public class Spectre extends BaseBot {

    private String debugEntitySearch;

    private enum Mode {
        ENABLED, DISABLED, PULL, LANE,
    }

    private static final String MY_HERO_NAME = "npc_dota_hero_spectre";

    private static float distance( BaseEntity a, BaseEntity b ) {
        final float[] posA = a.getOrigin();
        final float[] posB = b.getOrigin();
        return distance( posA, posB );
    }

    private static float distance( float[] posA, float[] posB ) {
        return (float) Math.hypot( posB[0] - posA[0], posB[1] - posA[1] );
    }

    private static Set<BaseEntity> findEntitiesInRange( World world, BaseEntity center, float range ) {
        final Set<BaseEntity> result = world.getEntities().values().stream().filter( e -> distance( center, e ) < range ).collect( Collectors.toSet() );
        result.remove( center );
        return result;
    }

    private int[] myLevels;

    private Mode mode = Mode.DISABLED;
    private boolean shouldRetreat;
    private boolean shouldBuyTango;
    private Integer currentTarget = null;
    private int targetLastHealth;

    public Spectre() {
        System.out.println( "Creating Spectre" );
        myLevels = new int[5];

    }

    @Override
    public LevelUp levelUp() {
        LEVELUP.setAbilityIndex( -1 );

        System.out.println("Levels: " + Arrays.toString(myLevels));

        if (myLevels[0] < 4) {
            LEVELUP.setAbilityIndex( 0 );
        }
        else if (myLevels[1] < 4) {
            LEVELUP.setAbilityIndex( 1 );
        }
        else if (myLevels[2] < 4) {
            LEVELUP.setAbilityIndex( 2 );
        }
        else if (myLevels[3] < 3) {
            LEVELUP.setAbilityIndex( 3 );
        }
        else if (myLevels[4] < 10) {
            LEVELUP.setAbilityIndex( 4 );
        }
        System.out.println( "LevelUp " + LEVELUP.getAbilityIndex() );
        return LEVELUP;
    }

    @Override
    public void onChat( ChatEvent e ) {
        String text = e.getText();
        System.out.println("Got chat: " + text);
        switch (text) {
            case "spectre go":
                mode = Mode.ENABLED;
                break;
            case "spectre stop":
                shouldRetreat = true;
                mode = Mode.DISABLED;
                break;
            case "spectre buy tango":
                shouldBuyTango = true;
                break;
            case "spec twiddle my ding dong":
                mode = Mode.PULL;
                break;
            default:
                if (text.startsWith("spectre entity ")) {
                    final String search = text.substring("spectre entity ".length());
                    System.out.println("Requesting entity search for " + search);
                    debugEntitySearch = search;
                }
                break;
        }
    }

    @Override
    public void reset() {
        System.out.println( "Resetting" );
        myLevels = new int[5];
        mode = Mode.DISABLED;
    }

    @Override
    public Select select() {
        SELECT.setHero( MY_HERO_NAME );
        return SELECT;
    }

    @Override
    public Command update( World world ) {
        // Update internal state

        if (debugEntitySearch != null) {
            System.out.println("Performing entity search for " + debugEntitySearch);
            world.getEntities().values().stream()
                    .filter(ent -> ent.getName().matches(debugEntitySearch))
                    .forEach(System.out::println);
            debugEntitySearch = null;
        }

        final int myIndex = world.searchIndexByName( MY_HERO_NAME );

        if (myIndex < 0) {
            //I'm probably dead
            System.out.println( "I'm dead?" );
            reset();
            return NOOP;
        }

        final Hero hero = (Hero) world.getEntities().get( myIndex );
        for (final Ability a : hero.getAbilities().values()) {
            myLevels[a.getAbilityIndex()] = a.getLevel();
//            System.out.println( a );
        }

        // Perform AI tasks
//        System.out.println( "I see " + world.searchIndexByClass( Tree.class ).size() + " trees" );
        if (shouldBuyTango) {
            shouldBuyTango = false;
            return buy( "item_tango" );
        }
        switch (mode) {
            case DISABLED:
                if (shouldRetreat) {
                    shouldRetreat = false;
                    return retreat( world );
                }
                return NOOP;
            case PULL:
                return pullEasy(world);
            case LANE:
                return goToLane(world);
            case ENABLED:
                break; // Do everything below
        }

//        System.out.println( world.getEntities().size() + " present" );
//        world.getEntities().values().stream().filter( e -> e.getClass() == Building.class ).forEach( e -> System.out.println( e ) );
//        world.getEntities().values().stream().filter( e -> e.getClass() == Tower.class ).forEach( e -> System.out.println( e ) );


        if (hero.getHealth() <= hero.getMaxHealth() * 0.4) {
            return retreat( world );
        }

        final float range = hero.getAttackRange();
        final Set<BaseEntity> e = findEntitiesInRange( world, hero, range ).stream().filter( p -> p instanceof BaseNPC )
                        .filter( p -> ((BaseNPC) p).getTeam() == 3 ).collect( Collectors.toSet() );
        if (!e.isEmpty()) {
            return attack( hero, e, world );
        }
        else {
            return move( hero, world );
        }
    }

    private Command attack( Hero hero, Set<BaseEntity> e, World world ) {
        final BaseEntity target = e.stream().sorted( ( e1, e2 ) -> Integer.compare( ((BaseNPC) e1).getHealth(), ((BaseNPC) e2).getHealth() ) )
                        .filter( f -> ((BaseNPC) f).getTeam() != hero.getTeam() ).findFirst().orElse( null );
        if (target == null) {
            //Nothing in range
            System.out.println( "No enemy in range" );
            return NOOP;
        }

        //If hero has enough mana, there's a 30 % chance that she'll cast a spell
        if (hero.getMana() > hero.getMaxMana() * 0.5 && Math.random() > 0.3) {
            return castSpell( hero, target, world );
        }
        else {
            //Otherwise she just attacks
            final int targetindex = world.indexOf( target );
            ATTACK.setTarget( targetindex );
            System.out.println( "Attacking" );

            return ATTACK;
        }
    }

    private Command buy( String item ) {
        BUY.setItem( item );

        return BUY;
    }

    private Command castSpell( Hero hero, BaseEntity target, World world ) {
        final Random r = new Random();
        final int index = r.nextInt( 4 );
        final Ability a = hero.getAbilities().get( index );
        if (a.getAbilityDamageType() == Ability.DOTA_ABILITY_BEHAVIOR_POINT) {
            return NOOP;
        }
        System.out.println( "will cast a spell" );
        System.out.println( "Will try " + a.getName() );
        if (a.getLevel() < 1) {
            System.out.println( "Not learned yet" );
            return NOOP;
        }
        if (a.getCooldownTimeRemaining() > 0f) {
            System.out.println( "On cooldown" );
            return NOOP;
        }
        CAST.setAbility( index );
        if ((a.getBehavior() & Ability.DOTA_ABILITY_BEHAVIOR_UNIT_TARGET) > 0) {
            CAST.setX( -1 );
            CAST.setY( -1 );
            CAST.setZ( -1 );
            CAST.setTarget( world.indexOf( target ) );
        }
        else {
            CAST.setTarget( -1 );
            final float[] pos = target.getOrigin();
            CAST.setX( pos[0] );
            CAST.setY( pos[1] );
            CAST.setZ( pos[2] );
        }

        return CAST;
    }

    private Command move( Hero hero, World world ) {
        //Walk up to the nearest enemy
        final Set<BaseEntity> en = findEntitiesInRange( world, hero, Float.POSITIVE_INFINITY ).stream().filter( p -> p instanceof BaseNPC )
                        .filter( p -> ((BaseNPC) p).getTeam() == 3 ).collect( Collectors.toSet() );
        final BaseEntity target = en.stream().sorted( ( e1, e2 ) -> Float.compare( distance( hero, e1 ), distance( hero, e2 ) ) )
                        .filter( f -> f.getClass() != Tower.class ).findFirst().orElse( null );
        if (target == null) {
            //Nothing in range
            System.out.println( "No enemy in sight" );
            return NOOP;
        }
        final BaseNPC targetEntity = (BaseNPC) target;
        final float[] targetPos = targetEntity.getOrigin();
        MOVE.setX( targetPos[0] );
        MOVE.setY( targetPos[1] );
        MOVE.setZ( targetPos[2] );

        System.out.println( "Moving" );

        return MOVE;
    }

    private Command retreat( World world ) {
        //Retreat at 30% health
        System.out.println( "Spectre is retreating" );
        final BaseNPC fountain = (BaseNPC) world.getEntities().entrySet().stream().filter( p -> p.getValue().getName().equals( "ent_dota_fountain_good" ) )
                        .findAny().get().getValue();
        final float[] targetPos = fountain.getOrigin();
        System.out.println("Retreating to " + Arrays.toString(targetPos));
        MOVE.setX( targetPos[0] );
        MOVE.setY( targetPos[1] );
        MOVE.setZ( targetPos[2] );

        return MOVE;
    }

    private Command pullEasy(World world) {
        System.out.println( "Spec is pulling" );

        printNeutralCreeps(world);

        //npc_dota_creep_neutral
        Map<Integer, BaseEntity> entities = world.getEntities();
        BaseEntity targetEntity = entities.get(currentTarget);
        if (targetEntity == null) {
            // Target is dead or out of range
            currentTarget = entities.entrySet().stream()
                    .filter(entry -> entry.getValue().getName().contains("npc_dota_creep_neutral"))
                    .map(Map.Entry::getKey)
                    .findAny()
                    .orElse(null);
            targetEntity = entities.get(currentTarget);
        } else if (targetEntity.getHealth() < targetLastHealth) {
            // Forget target once attack lands
            currentTarget = null;

            mode = Mode.LANE;
            return goToLane(world);
        }

        if (currentTarget != null) {
//        final BaseEntity target = entities.entrySet().stream().filter(p -> p.getValue().getName().equals( "ent_dota_fountain_good" ) )
//                .findAny().get().getValue();
            ATTACK.setTarget(currentTarget);
            targetLastHealth = targetEntity.getHealth();
            return ATTACK;
        } else {
            MOVE.setX(3248);
            MOVE.setY(-5600);
            return MOVE;
        }
    }

    private Command goToLane(World world) {
        final float[] targetPos = {3248, -5800};

        BaseEntity hero = world.getEntities().values().stream()
                .filter(ent -> ent.getName().equals(MY_HERO_NAME))
                .findAny().orElse(null);

        if (hero == null) {
            System.err.println("Can't find hero :^)");
        } else if (distance(hero.getOrigin(), targetPos) < 20) {
            System.out.println("Back in lane");
            mode = Mode.ENABLED;
        }

        MOVE.setX(3248);
        MOVE.setY(-5800);
        return MOVE;
    }

    private void printNeutralCreeps(World world) {
        System.out.println();

        for (Map.Entry<Integer, BaseEntity> entry : world.getEntities().entrySet()) {
            if (entry.getValue().getName().contains("npc_dota_creep_neutral")) {
                System.out.println(entry.getKey().toString() + " => " + entry.getValue().toString());
            }
        }
    }
}
