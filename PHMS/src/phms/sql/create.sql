CREATE TABLE Person (
    Per_Id number(16),
    Per_FirstName VARCHAR(25) NOT NULL,
    Per_LastName VARCHAR(25) NOT NULL,
    Per_DateOfBirth DATE NOT NULL,
    Per_Address VARCHAR(255),
    Per_Phone VARCHAR(10),
    Per_Sex VARCHAR(1),
    Per_Password VARCHAR(16),
    CONSTRAINT Person_PK PRIMARY KEY(Per_Id)
);
CREATE TABLE PATIENT(
    Pat_Person NUMBER(16),
    Pat_Sick NUMBER(1),
    CONSTRAINT PATIENT_PK PRIMARY KEY (Pat_Person),
    CONSTRAINT PATIENT_FK FOREIGN KEY (Pat_Person) REFERENCES Person(Per_Id),
    CONSTRAINT HS_Constraints 
        CHECK(SELECT 
                COUNT(*)
              from Health_Supporter 
              where Health_Supporter.HS_Patient = Pat_Person) <= 2)
    CONSTRAINT HS_SickPatient
        CHECK(
            (SELECT 
                COUNT(*)
             from Health_Supporter 
             where Pat_Sick=1 AND Health_Supporter.HS_Patient = Pat_Person) BETWEEN 1 AND 2 
        )
);
CREATE TABLE Health_Supporter(
    HS_Supporter NUMBER(16),
    HS_Patient NUMBER(16),
    HS_DateAuthorized DATE,
    HS_DateUnauthorized DATE,
    CONSTRAINT HS_PK PRIMARY KEY(HS_Supporter, HS_Patient),
    CONSTRAINT HS_HS FOREIGN KEY(HS_Supporter) REFERENCES Person(Per_Id),
    CONSTRAINT HS_P FOREIGN KEY(HS_Patient) REFERENCES Patient(Pat_Person)
);
CREATE TABLE Disease(
    Dis_DiseaseName VARCHAR(200),
    CONSTRAINT DISEASE_PK PRIMARY KEY(Dis_DiseaseName)
);
CREATE TABLE Health_Observation_Type(
    Hot_Id NUMBER(16),
    Hot_Name VARCHAR(255),
    Hot_Disease VARCHAR(200),
    Hot_UpperLimit LONG,
    Hot_LowerLimit LONG,
    Hot_Frequency Long,
    CONSTRAINT HOT_PK PRIMARY KEY(Hot_Id),
    CONSTRAINT HOT_FK_D FOREIGN KEY (Hot_Disease) REFERENCES Disease(Dis_DiseaseName)
);
CREATE SEQUENCE Health_Obs_Seq START WITH 1;
CREATE OR REPLACE TRIGGER HealthObs_AI 
BEFORE INSERT ON Health_Observation_Type 
FOR EACH ROW
BEGIN
  SELECT Health_Obs_Seq.NEXTVAL
  INTO   :new.hot_id
  FROM   dual;
END;
/
-- TODO: What's Santa's favorite table?
CREATE TABLE Health_Observation(
    Ho_Patient NUMBER(16),
    Ho_ObservationType Number(16),
    Ho_Value LONG,
    Ho_DateTaken Date,
    CONSTRAINT HO_PK PRIMARY KEY (Ho_Patient, Ho_ObservationType),
    CONSTRAINT HO_P_FK FOREIGN KEY (Ho_Patient) REFERENCES Patient(Pat_Person),
    CONSTRAINT HO_HOT_FK FOREIGN KEY (Ho_ObservationType) REFERENCES Health_Observation_Type(Hot_Id)
);
CREATE TABLE Recommendation(
    Rec_HS_Supporter NUMBER(16),
    Rec_HS_Patient Number(16),
    Rec_OBS_Type Number(16),
    Rec_OBS_Patient Number(16),
    Rec_DoctorComment VARCHAR(999),
    CONSTRAINT REC_PK PRIMARY KEY (Rec_HS_Supporter, Rec_HS_Patient, Rec_OBS_Patient, Rec_OBS_Type),
    CONSTRAINT REC_FK_SUP FOREIGN KEY (Rec_HS_Supporter, Rec_HS_Patient) REFERENCES Health_Supporter(HS_Supporter, HS_Patient),
    CONSTRAINT REC_FK_OBST FOREIGN KEY (Rec_OBS_Type, Rec_OBS_Patient) references Health_Observation(Ho_ObservationType, Ho_Patient)
);
CREATE TABLE ALERT(
-- TODO : Discuss whether Al_HS_Supporter is required or not
    Al_HS_Supporter NUMBER(16),
    Al_HS_Patient Number(16),
    Al_OBS_Type Number(16),
    Al_Read Number(1),
    Al_Sent Date,
    Al_Alert VARCHAR(999),
    CONSTRAINT ALLERT_PK PRIMARY KEY (Al_HS_Supporter, Al_HS_Patient, Al_OBS_Type, Al_OBS_Patient),
    CONSTRAINT ALLERT_FK_P FOREIGN KEY (Al_HS_Supporter, Al_HS_Patient, Al_OBS_Type, Al_OBS_Patient) REFERENCES Recommendation(Rec_HS_Supporter, Rec_HS_Patient, Rec_OBS_Type, Rec_OBS_Patient)
);

/*
-- Query for Health Observation Types that have broken their "recommendation" rule (and generate an allert for them).
-- 1) Query for recommendations
-- 2) Query for Health Observations
-- 3) For each Health Observation that does not meet the minimum/maximum OR is outside the threshold, 
-- 4) Generate an allert

-- So we really need two triggers, one for Thresholds and one for Frequencies.

    1) Query for health observations that break their threshold for a particular user

    Select health observations for a user WHERE EXISTS(Recommendation for user) AND 


-- This Trigger is executed whenever a VALUE is added in Table Health_Observation.
-- It will compare HO_Value with Hot_UpperLimit & Hot_LowerLimit. If the HO_Value is not 
-- in the specified range. It will generate an ALERT and populate the Al_Alert field in table
-- ALERT with something on the lines "Hot_Name(observationtype) for patient(Per_Id) is not in 
-- the specified range. Immediate action required."
*/

-- Test Pending

CREATE TRIGGER alert_range
AFTER INSERT OR UPDATE OF Ho_Value ON Health_Observation
WHEN
    DECLARE out_of_bounds INT;
    DECLARE u_limit, l_limit LONG;
    SET out_of_bounds = 1;
    select Hot_UpperLimit, Hot_LowerLimit INTO u_limit, l_limit FROM Health_Observation_Type
    WHERE Ho_ObservationType = Health_Observation_Type.Hot_Id;
    IF (NEW.Ho_Value > l_limit AND NEW.Ho_Value < u_limit) THEN
        out_of_bounds = 0
    END IF;
    IF (out_of_bounds = 1) THEN
    -- TODO : Add health supporter into ALERT or change tables accordingly.
        INSERT INTO ALERT VALUES(NEW.Ho_Patient, NEW.Ho_ObservationType, 0, NEW.Ho_DateTaken, CONCAT(NEW.Ho_ObservationType,'for', NEW.Ho_Patient, 'is not in the specified range. Immediate action required.'));
    END IF;
END;
\

