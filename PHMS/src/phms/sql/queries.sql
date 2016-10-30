-- Add queries required for the application

--Find patients who have two health supporters-- [Tested by Zach]
SELECT 
    Per_Id, 
    Per_FirstName, 
    Per_LastName 
FROM 
    Person p, 
    PATIENT pa
WHERE p.Per_Id = pa.Pat_Person
	AND 2 = (SELECT COUNT(*) 
            FROM Health_Supporter h
			WHERE h.HS_Patient = pa.Pat_Person);
			
--Find all sick patients whose health supporters are also sick patients-- [Tested by Zach]
SELECT * 
FROM 
    Person p, 
    PATIENT pa
WHERE 
    p.Per_Id = pa.Pat_Person AND 
    pa.Pat_Sick = 1 AND
	EXISTS(SELECT * 
           FROM Health_Supporter h, PATIENT pa2
			WHERE 
                h.HS_Patient = pa.Pat_Person AND 
                h.HS_Supporter = pa2.Pat_Person AND 
                pa2.Pat_Sick = 1);
				
--Find patients who belong to more than one sick patient class--  [Tested by Zach]
SELECT * 
FROM 
    Person p, 
    PATIENT pa
WHERE 
    p.Per_Id = pa.Pat_Person AND 
    pa.Pat_Sick = 1 AND 
    2 = (SELECT COUNT(*) 
        FROM Diagnosis pd 
        WHERE pd.Di_Patient = pa.Pat_Person);

--List the patients who are not ‘sick’-- [Tested by Zach]
SELECT * 
FROM 
    Person p, 
    PATIENT pa
WHERE 
    p.Per_Id = pa.Pat_Person AND 
    pa.Pat_Sick = 0;

--List the health supporters who themselves are patients.-- [Tested by Zach]
SELECT * 
FROM 
    Person p, 
    Health_Supporter h
WHERE 
    p.Per_Id = h.HS_Supporter AND 
    1 = (SELECT COUNT(*) 
        FROM PATIENT pa 
        WHERE pa.Pat_Person = h.HS_Supporter);

--Retrieve the user's diseases-- [Tested by Zach]
SELECT Di_DiseaseName 
FROM Patient ps, Diagnosis pd
WHERE pd.Di_Patient = ps.Pat_Person;

--Retrieve the user's diseases As HS (w/ HS ssn passed in)-- [Tested by Zach]
-- We will already know which patients they support, so we can just query for a specific patient --
-- REPLACE 1 WITH SOME CONSTANT --
select 
    di_diseasename
from
    diagnosis
where
    di_patient = 1;

--Retrieve patients for a specific health supporter (HS ssn passed in) (REPLACE 4) [Tested by Zach]
SELECT 
    * 
FROM 
    Person p, 
    PATIENT pa, 
    Health_Supporter h
WHERE 
    h.HS_Supporter = 4 AND 
    p.Per_Id = pa.Pat_Person AND 
    pa.Pat_Sick = 1 AND 
    h.HS_Patient = pa.Pat_Person;

--Remove/Delete current HealthSupporter Account (w/ hs ssn passed in)--
DELETE FROM Health_Supporter WHERE HS_Supporter = ?

-- Retrieve people who could be the current user's health supporters
-- Patient can not support themselves and current supporters can not support [Tested by Zach]
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
 
(SELECT 
    h.Hot_Id, 
    h.Hot_Name, 
    h.Hot_Disease, 
    h.Hot_UpperLimit, 
    h.Hot_LowerLimit, 
    h.Hot_Frequency 
FROM 
    Recommendation r, 
    Health_Observation_Type h
WHERE 
    r.Rec_Hot_Type = h.Hot_Id AND 
    h.Hot_Disease = null AND 
    r.Rec_HS_Patient = 1)
UNION
(SELECT 
    ho.Hot_Id, 
    ho.Hot_Name, 
    ho.Hot_Disease, 
    ho.Hot_UpperLimit, 
    ho.Hot_LowerLimit, 
    ho.Hot_Frequency 
FROM 
    Health_Observation_Type ho
WHERE 
    ho.Hot_Disease IN (SELECT Di_DiseaseName
                       FROM Diagnosis d
                       WHERE d.Di_Patient = 1));
    
--GET HOTypes where disease is NOT in patients diseases AND HO NAME not in patients recommendation basically THESE ARE GENERIC--

-- Generic Recommendations
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h
where h.Hot_Disease IS NULL;

-- Disease Recommendations
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h
where h.Hot_Disease IS NOT NULL AND 
            h.Hot_Id NOT IN (
                             SELECT Rec_HOT_Type from Recommendation);

-- Recommended HoT for Patients
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h, Recommendation r, PATIENT p
where h.Hot_Id = r.Rec_HOT_Type AND p.Pat_Person=r.Rec_HS_Patient;

--All HoT for a particular patient --- START ---

with tmp as
(SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h, Recommendation r, Person p
where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = 3
UNION
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h
where h.Hot_Disease IN (select d.Di_DiseaseName from Person p, Diagnosis d where d.Di_Patient=p.Per_Id AND p.Per_Id = 3)
            AND 
            h.Hot_Id NOT IN (
                             SELECT Rec_HOT_Type from Recommendation) AND h.hot_name NOT IN (
                                  SELECT h.Hot_Name
                                from Health_Observation_Type h, Recommendation r, Person p
                                where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = 3))
select * from tmp
union
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h
where h.Hot_Disease IS NULL AND h.hot_name NOT IN
(SELECT h.Hot_Name
from Health_Observation_Type h, Recommendation r, Person p
where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = 3
Union
SELECT h.Hot_Name
from Health_Observation_Type h
where h.Hot_Disease IN (select d.Di_DiseaseName from Person p, Diagnosis d where d.Di_Patient=p.Per_Id AND p.Per_Id = 3)
            AND 
            h.Hot_Id NOT IN (
                             SELECT Rec_HOT_Type from Recommendation) AND h.hot_name NOT IN (
                                  SELECT h.Hot_Name
                                from Health_Observation_Type h, Recommendation r, Person p
                                where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = 3));


--All HoT for a particular patient --- END ---

SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h, Recommendation r, PATIENT p, Diagnosis d

-- Test query
select hot_name, hot_disease from Health_Observation_Type h where h.hot_name='Weight';

--Union w/ HOTypes that are patients disease BUT not in reccommends so these freq/thresh supercede--
(SELECT h1.Hot_Id, h1.Hot_Name, h1.Hot_Disease, h1.Hot_UpperLimit, h1.Hot_LowerLimit, h1.Hot_Frequency 
FROM Health_Observation_Type h1
WHERE h1.Hot_Id NOT IN(SELECT r.r.Rec_OBS_Type FROM Recommendation r) AND h1.Hot_Disease IN (SELECT Di_DiseaseName FROM Diagnosis d
WHERE d.Di_Patient = ?))

SELECT * from
	(SELECT * from Health_Observation_Type WHERE NOT EXISTS ())

-- select *
--     fom
--     (select * from health_observation_type
--         where
--         NOT EXISTS (in recommendation table)
--         AND DiseaseType=null),
--     (select * from recommendations where Rec_HS_Patient = 1),
--     (select * the set of health observations where health_observation_disease IN
--         (select the set of disease the user has))

-- For each patient and each month of 2015, list allerts generated
select 
    al_hs_supporter,
    al_hs_patient,
    extract(year from al_sent) as year,
    extract(month from al_sent) as month
from
    alert
where 
    extract(year from al_sent) = 2015
order by al_hs_patient, month;

--IN PROGRESS: For each month of 2015, list the patients with the most alerts
select
    AL_HS_Patient,
    extract(month from al_sent) as Month
from
    Alert
group by extract(month from al_sent), AL_HS_Patient
having count(Distinct AL_HS_Patient) = (select max( count( Distinct AL_HS_Patient) )
                                   from Alert
                                   group by extract(month from AL_sent) );

--List the number of health supporters that were authorized in the month of September 2016 by patients suffering from heart disease.
select 
   HS_Supporter
from 
    Health_Supporter
where HS_Patient in (select 
                        Di_Patient
                    from
                        Diagnosis
                    where
                        Di_DiseaseName = 'Heart Disease') AND
     extract(month from HS_DateAuthorized) = 9 AND
     extract(year from HS_DateAuthorized) = 2016;

--How many patients have different observation time and recording time (of the observation).
select 
	count(*) 
from (
	select 
		ho_patient 
	from 
		Health_Observation
    where 
    	HO_OBSERVEDDATETIME != HO_RECORDEDDATETIME);

-- Give the number of patients who were not complying with the recommended frequency of recording observations
-- Count patients who are in the set of patients who have frequency alerts

select 
   count( distinct al_per_patient)
from
    alert
where
    al_alert like '%frequency%%';

-- Example query using function to delete a row from diagnosis.
declare
    x number;
begin
    x := deleteDiagnosis('HIV', 2);
end;
/
