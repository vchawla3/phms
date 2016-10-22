-- First drop data tables
DROP TABLE ALLERT;
DROP TABLE Recommendation;
DROP SEQUENCE Health_Obs_Seq;
DROP Trigger HealthObs_AI;
DROP TABLE Health_Observation;
-- Then drop lookup tables
DROP TABLE Health_Observation_Type;
DROP TABLE Disease;
-- Then drop user tables
DROP TABLE Health_Supporter;
DROP TABLE Patient;
DROP TABLE Person;
