SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency
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
                                where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = 3)
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
