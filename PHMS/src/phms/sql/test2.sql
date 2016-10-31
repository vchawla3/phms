drop procedure alert_freq;
CREATE OR REPLACE PROCEDURE ALERT_FREQ
AS
BEGIN
FOR temp IN (SELECT DISTINCT h1.Ho_Patient, h1.Ho_ObservationType from Health_Observation h1 where (SYSDATE - h1.Ho_ObservedDateTime) >= (SELECT h2.Hot_Frequency from Health_Observation_Type h2 where h1.Ho_ObservationType=h2.Hot_Id))
LOOP
INSERT INTO ALERT VALUES (temp.Ho_Patient, temp.Ho_ObservationType, 0, SYSDATE, 'Kindly enter your health indicators, frequency is off', SYSDATE);
END LOOP;

FOR temp2 IN (SELECT Pat_Person from PATIENT where Pat_Person NOT IN (SELECT Ho_Patient from Health_Observation) AND Pat_Sick=1)
LOOP
INSERT INTO ALERT VALUES (temp2.Pat_Person, 1, 0, SYSDATE, ('Kindly enter your health indicators, frequency is off'), SYSDATE);
END LOOP;
END ALERT_FREQ;
/
