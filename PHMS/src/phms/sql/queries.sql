-- Add queries required for the application

--Find patients who have two health supporters--
SELECT * FROM Person p, PATIENT pa
WHERE p.Per_Id = pa.Pat_Person
	AND 2 = (SELECT COUNT(*) FROM Health_Supporter h
			WHERE h.HS_Patient = pa.Pat_Person);
			
--Find all sick patients whose health supporters are also sick patients--
SELECT * FROM Person p, PATIENT pa
WHERE p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 1
	AND EXISTS(SELECT * FROM Health_Supporter h, PATIENT pa2
				WHERE h.HS_Patient = pa.Pat_Person AND h.HS_Supporter = pa2.Pat_Person AND pa2.Pat_Sick = 1);
				
--Find patients who belong to more than one sick patient class--
SELECT * FROM Person p, PATIENT pa
WHERE p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 1 AND 2 = (SELECT COUNT(*) FROM PatientDisease pd WHERE pd.Pd_Patient = pa.Pat_Person);

--List the patients who are not ‘sick’--
SELECT * FROM Person p, PATIENT pa
WHERE p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 0;

--List the health supporters who themselves are patients.--
SELECT * FROM Person p, Health_Supporter h
WHERE p.Per_Id = h.HS_Supporter AND 1 = (SELECT COUNT(*) FROM PATIENT pa WHERE pa.Pat_Person = h.HS_Supporter);

--Retrieve the user's diseases--
SELECT Pd_DiseaseName FROM PATIENT ps, PatientDisease pd
WHERE pd.Pd_Patient = pa.Pat_Person;

--Retrieve the user's diseases As HS (w/ HS ssn passed in)--
SELECT pd.Pd_Patient, pd.Pd_DiseaseName FROM PatientDisease pd
WHERE pd.Pd_Patient IN (SELECT p.Per_Id FROM Person p, PATIENT pa, Health_Supporter h
						WHERE h.HS_Supporter = ? 
						AND p.Per_Id = pa.Pat_Person
						AND pa.Pat_Sick = 1
						AND h.HS_Patient = pa.Pat_Person);

--Retrieve patients for a specific health supporter (HS ssn passed in)
SELECT * FROM Person p, PATIENT pa, Health_Supporter h
WHERE h.HS_Supporter = ? AND p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 1 AND h.HS_Patient = pa.Pat_Person;

--Remove/Delete current HealthSupporter Account (w/ hs ssn passed in)--
DELETE FROM Health_Supporter WHERE HS_Supporter = ?

-- Retrieve people who could be the current user's health supporters
-- Patient can not support themselves and current supporters can not support
select 
    * 
from 
    Person
where
    Per_ID != 1
    AND
    Per_Id NOT IN ( select HS_Supporter
                      from  Health_Supporter
                      where HS_Patient = 1);

-- Retrieve recommendations for a particular patient
select * from Recommendation where Rec_HS_Patient = 1;

-- Retrieve all alerts for a user
select
    * 
from
    alert
where
    Al_OBS_Patient = 1
    AND
    AL_READ = 0

-- Retrieve Health Observations (thresholds) for a user
select 
    Hot_Name,
    Hot_UpperLimit,
    Hot_LowerLimit,
    Hot_Frequency
from
    Health_Observation_Type
where
    Hot_Id in (
        SELECT 
            Ho_ObservationType
        from
            Health_Observation
        where
            Ho_Patient = 1
    )
