CREATE OR REPLACE PROCEDURE ALERT_FREQ
IS
  HOT  Health_Observation_Type%ROWTYPE;
  HOT_TABLE IS TABLE OF Health_Observation_Type%ROWTYPE;
BEGIN
FOR temp IN (SELECT Per_Id from Person) LOOP
select * INTO HOT_TABLE from (
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h, Recommendation r, Person p
where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = temp.Per_Id
UNION
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h
where h.Hot_Disease IN (select d.Di_DiseaseName from Person p, Diagnosis d where d.Di_Patient=p.Per_Id AND p.Per_Id = temp.Per_Id)
            AND 
            h.Hot_Id NOT IN (
                             SELECT Rec_HOT_Type from Recommendation) AND h.hot_name NOT IN (
                                  SELECT h.Hot_Name
                                from Health_Observation_Type h, Recommendation r, Person p
                                where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = temp.Per_Id)
union
SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
from Health_Observation_Type h
where h.Hot_Disease IS NULL AND h.hot_name NOT IN
(SELECT h.Hot_Name
from Health_Observation_Type h, Recommendation r, Person p
where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = temp.Per_Id
Union
SELECT h.Hot_Name
from Health_Observation_Type h
where h.Hot_Disease IN (select d.Di_DiseaseName from Person p, Diagnosis d where d.Di_Patient=p.Per_Id AND p.Per_Id = temp.Per_Id)
            AND 
            h.Hot_Id NOT IN (
                             SELECT Rec_HOT_Type from Recommendation) AND h.hot_name NOT IN (
                                  SELECT h.Hot_Name
                                from Health_Observation_Type h, Recommendation r, Person p
where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = temp.Per_Id)));

END LOOP;
END ALERT_FREQ;
/
