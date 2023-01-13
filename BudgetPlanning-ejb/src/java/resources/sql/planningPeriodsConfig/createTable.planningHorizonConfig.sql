-- resources.sql.planningPeriodsConfig --
-- createTable.planningHorizonConfig.sql --
create table PLANNING_HORIZON_CONFIG (
PL_PER_FREQ varchar(1) not null,
PL_PER_HORIZON int not null
);

-- prespecified default values --
insert into PLANNING_HORIZON_CONFIG (PL_PER_FREQ, PL_PER_HORIZON)
values 
('W', 24),
('M', 6),
('D', 168);