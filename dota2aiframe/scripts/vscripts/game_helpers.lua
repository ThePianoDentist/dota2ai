--
-- Created by IntelliJ IDEA.
-- User: jdog
-- Date: 01/10/16
-- Time: 01:05
-- To change this template use File | Settings | File Templates.
--

function distance_between_sqd(position_one, position_two)
    return (position_two.x - position_one.x)^2 +
            (position_two.y - position_one.y)^2 +
            (position_two.z - position_one.z)^2
end

function closest_entity(my_entitry, entity_name, world)
    for entities in world do

    end
end

