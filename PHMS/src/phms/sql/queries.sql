-- Add queries required for the application

--Find patients who have two health supporters--
SELECT * FROM Person p, PATIENT pa
WHERE p.Per_Id = pa.Pat_Person
	AND 2 = (SELECT COUNT(*) FROM Health_Supporter h
			WHERE h.HS_Patient = pa.Pat_Person);
			
