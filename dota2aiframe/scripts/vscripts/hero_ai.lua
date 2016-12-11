--
-- Created by IntelliJ IDEA.
-- User: Johnny
-- Date: 29/09/2016
-- Time: 21:50
-- To change this template use File | Settings | File Templates.
--

team_decisions =  {five_man_push, defensive_trilane }
individual_decisions = {farm_safelane}
spec_decisions = {farm_safelane = 1.0}
local hero1 = {name = "spectre", current_decision = farm_safelane, decisions = spec_decisions, current_action={command="NOOP"}, state = nil, lastHP=0}
local hero2 = {name = "spectre", current_decision = farm_safelane, decisions = spec_decisions, current_action={command="NOOP"}, state = nil, lastHP=0 }
local hero3 = {name = "spectre", current_decision = farm_safelane, decisions = spec_decisions, current_action={command="NOOP"}, state = nil, lastHP=0 }
local hero4 = {name = "spectre", current_decision = farm_safelane, decisions = spec_decisions, current_action={command="NOOP"}, state = nil, lastHP=0 }
local hero5 = {name = "spectre", current_decision = farm_safelane, decisions = spec_decisions, current_action={command="NOOP"}, state = nil, lastHP=0}
heroes = {hero1, hero2, hero3, hero4, hero5}