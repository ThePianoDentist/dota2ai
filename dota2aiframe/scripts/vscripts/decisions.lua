--
-- Created by IntelliJ IDEA.
-- User: Johnny
-- Date: 15/10/2016
-- Time: 14:23
-- To change this template use File | Settings | File Templates.
--

function Dota2AI:MakeDecision(hero, world)
    local action
    local eHero = hero.state
    -- if
--    local allUnits = Entities:FindByName(nil, "npc_dota_creep_goodguys")(eHero:GetTeamNumber(),
--        eHero:GetOrigin(),
--        nil,
--        FIND_UNITS_EVERYWHERE,
--        DOTA_UNIT_TARGET_TEAM_BOTH,
--        DOTA_UNIT_TARGET_ALL,
--        DOTA_UNIT_TARGET_FLAG_FOW_VISIBLE,
--        FIND_ANY_ORDER,
--        true)
    local vertex_pos = {x = -6154.0, y = 4431.0}
    --local creeps = Entities:FindByName(nil, "npc_dota_creep_goodguys_melee") --goodguys badguys _ranged _melee
    local creeps_radiant_top = world.entities.creeps_radiant_top
    print("creeps_radiant_top")
    for k,v in pairs(creeps_radiant_top) do
        print(k, v)
    end

    -- 4588 -5293
    local next = next
    if eHero:GetHealth() < hero.lastHP then
        action = {command = "MOVE", vec = Entities:FindByName(nil, "dota_badguys_tower1_top"):GetOrigin()} -- move to tower
    else
        if next(creeps_radiant_top) ~= nil then
            local move_to
            local closest_creep_key = closest(VectorToArray(eHero:GetOrigin()), creeps_radiant_top)
            local lowest_health_creep_key, lowest_health_creep = next(lowest_health(creeps_radiant_top))
            --if lowest_health_creep.health < eHero:GetAverageTrueAttackDamage()
            if lowest_health_creep.health < eHero:GetBaseDamageMin() then
                action = {command = "ATTACK", target = lowest_health_creep_key }
            else
                action = {command = "MOVE", x = lowest_health_creep.origin.x, y = lowest_health_creep.origin.y, z = lowest_health_creep.origin.z }
            end

--            local d = distance_between(creeps_radiant_top[closest_creep_key].origin, vertex_pos)
--            local dist_us_creep = distance_between(creeps_radiant_top[closest_creep_key].origin, VectorToArray(eHero:GetOrigin()))
--            print ("Distance to vertex" .. d)
--            -- Assume if they are closer than the distance between mid map and top vertex, must be top lane creeps
--            action = {command = "ATTACK", target = closest_creep_key }
            print ("aggro target")
            print(eHero:GetAggroTarget())

    --        if d > 5000 then  --rough guess for now
    --        print ("1")
    --        action = {command = "MOVE", x = vertex_pos.x, y = vertex_pos.y, z = 0}
    --        else
    --            action = {command = "MOVE", x = vertex_pos.x, y = vertex_pos.y, z = 0}
    --            action = {command = "ATTACK", target = closest_creep_key }
    --            print ("2")
    --        end

        end
    end

    if action ~= hero.current_action then
        hero.current_action = action -- I think this is that bad side-efecty thing to avoid with functions. maybe just return action and set hero current action outside?
        return action
    else
        return nil
    end
end

function lowest_health(creeps)
    local lowest_health = 9000
    local lowest_creep
    local lowest_key
    for key, creep in pairs(creeps) do
        if creep.health < lowest_health then
            lowest_health = creep.health
            lowest_creep = creep
            lowest_key = key
        end
    end

    local out = {}
    out[lowest_key] = lowest_creep
    return out
end

function closest(hero_pos, creeps)
    local closest_creep_dist = 999999
    local key_closest
    local tmp_closest
    for key, creep in pairs(creeps) do
        local d = distance_between(hero_pos, creep.origin)
        if d < closest_creep_dist then
            closest_creep_dist = d
            tmp_closest = key
            key_closest = key
        end
    end
    local out = {}
    out[key_closest] = tmp_closest
    return tmp_closest
end

function distance_between(obj_one, obj_two)
    -- should I ignore z?
    return ((obj_one.x - obj_two.x)^2 + (obj_one.y - obj_two.y)^2)^(0.5)
end