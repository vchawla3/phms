-- first drop data tables
drop table alert;
drop table recommendation;
drop sequence health_obs_seq;
drop trigger healthobs_ai;
drop table health_observation;
-- then drop lookup tables
drop table health_observation_type;
drop table diagnosis;
drop table disease;
-- then drop user tables
drop table health_supporter;
drop table patient;
drop table person;
