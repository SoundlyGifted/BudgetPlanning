-- resources.sql.planningPeriodsConfig --
-- update.planningPeriodsHorizon.byFreq.sql --
update PLANNING_HORIZON_CONFIG
set PL_PER_HORIZON = ?
where PL_PER_FREQ = ?