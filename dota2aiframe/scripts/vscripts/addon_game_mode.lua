-- Generated from template
-- Thanks for helping out Perry,  #dota2mods on gamesurge IRC
-- Thanks for testing VicFrank,  #dota2mods on gamesurge IRC

if Dota2AI == nil then
    _G.Dota2AI = class({})
end

heros = {"spectre", "lion", "zeus", "bloodseeker", "natures prophet"}
------------------------------------------------------------------------------------------------------------------------------------------------------
-- Required .lua files, which just exist to help organize functions contained in our addon.  Make sure to call these beneath the mode's class creation.
------------------------------------------------------------------------------------------------------------------------------------------------------
require( "json_helpers" )
require( "hero_tools" )
require( "utility_functions" )
require( "events" )
require( "hero_ai")
require("decisions")

function Precache( context )
	--[[
		Precache things we know we'll use.  Possible file types include (but not limited to):
			PrecacheResource( "model", "*.vmdl", context )
			PrecacheResource( "soundfile", "*.vsndevts", context )
			PrecacheResource( "particle", "*.vpcf", context )
			PrecacheResource( "particle_folder", "particles/folder", context )
	]]
end

-- Create the game mode when we activate
function Activate()
	GameRules.Dota2AI = Dota2AI()
    GameRules:SetPreGameTime(20.0)
	GameRules.Dota2AI:InitGameMode()
end

function Dota2AI:InitGameMode()
	print( "Template addon is loaded." )
	GameRules:GetGameModeEntity():SetThink( "OnThink", self, "GlobalThink", 2 )
	--GameRules:GetGameModeEntity():SetThink( "OnAIStuff", "ai stuff", 2 )
end


function Dota2AI:OnAIStuff()

    print( "test2" )
    local cmdPlayer = Convars:GetCommandClient()
    if cmdPlayer then
      if cmdPlayer:GetAssignedHero() ~= nil then
        cmdPlayer:GetAssignedHero():MoveToPosition( cmdPlayer:GetAssignedHero().spawnPos ) 
      end
    end

    return 1
end

-- Evaluate the state of the game
function Dota2AI:OnThink()

    local world
    -- LANE_CENTER = Vector( -647, -287, 17 )
    print( "test1" )
    for key, hero in pairs(HeroList:GetAllHeroes()) do
        if not world then world = Dota2AI:JSONWorld(hero) end
        if (hero:GetTeamNumber() == DOTA_TEAM_BADGUYS) then
            local result =  {command = "MOVE", x = 1000, y = 1000, z = 0 }
            local decision = heroes[1].current_decision
            heroes[1].state = hero
            result = self:MakeDecision(heroes[1], world)

            self:ParseHeroCommand(heroes[1].state, result)
            heroes[1].lastHP = hero:GetHealth()
            break
        else
        end

        --local serverIP = "http://192.168.1.189" --use this if somehow the HTTPloc doesnt work
        local serverHTTPLoc = "http://localhost/"
        --CreateHTTPRequest( "GET", serverHTTPLoc .. "test.php?hpvalue=" .. hero:GetHealth() .. "&unitid=" .. hero:GetEntityIndex() ):Send( function( result )
        --print( "Done." )
    end

           --hero:MoveToPosition(  Vector( 6733, 6116, 385 )  )



	if GameRules:State_Get() == DOTA_GAMERULES_STATE_GAME_IN_PROGRESS then
		--print( "Template addon script is running." )
	elseif GameRules:State_Get() >= DOTA_GAMERULES_STATE_POST_GAME then
		return nil
	end
	return 1
end