-- resources.sql.planningPeriodsConfig --
-- select.planningPeriodsHorizon.byFreq.sql --
select PL_PER_HORIZON 
from PLANNING_HORIZON_CONFIG
where PL_PER_FREQ = ?
