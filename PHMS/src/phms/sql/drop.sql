delete from Health_Observation;
delete from Health_Observation_Type;
delete from health_Supporter;
delete from diagnosis;
delete from disease;
delete From Patient;
delete from person;

-- first drop data tables
drop table alert;
drop table recommendation;
drop sequence health_obs_seq;
drop trigger healthobs_ai;
drop table health_observation;
-- then drop lookup tables
drop table health_observation_type;
drop trigger Di_PatMustBeSick;
drop table diagnosis;
drop table disease;
-- then drop user tables
drop table health_supporter;
drop table patient;
drop table person;
-- Drop trigger
drop trigger ALERT_RANGE;
