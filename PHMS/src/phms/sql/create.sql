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
    Pat_FeltSickOn DATE,
    CONSTRAINT PATIENT_PK PRIMARY KEY (Pat_Person),
    CONSTRAINT PATIENT_FK FOREIGN KEY (Pat_Person) REFERENCES Person(Per_Id)
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
CREATE TABLE Diagnosis (
	Di_Patient NUMBER(16),
	Di_DiseaseName VARCHAR(200),
	CONSTRAINT Di_PK PRIMARY KEY(Di_Patient, Di_DiseaseName),
	CONSTRAINT Di_P FOREIGN KEY(Di_Patient) REFERENCES Person(Per_Id),
	CONSTRAINT Di_D FOREIGN KEY(Di_DiseaseName) REFERENCES Disease(Dis_DiseaseName)
);
CREATE OR REPLACE TRIGGER Di_PatMustBeSick
BEFORE INSERT 
    ON Diagnosis
    REFERENCING OLD as DI_OLD NEW as Di_NEW
    FOR EACH ROW 
BEGIN
    UPDATE Patient Set PAT_SICK = 1 where PAT_PERSON = :Di_New.Di_Patient;
END;
/
CREATE OR REPLACE TRIGGER Di_PatMustHaveHS
BEFORE INSERT OR UPDATE ON Diagnosis
FOR EACH ROW 
WHEN (NEW.Di_DiseaseName <> OLD.Di_DiseaseName)
Declare
    CountOfSupporters Number(2);
BEGIN
	SELECT 
        COUNT(*) INTO CountOfSupporters 
    from Health_Supporter 
    where HS_Patient = :NEW.Di_Patient;
    
	IF (CountOfSupporters = 0) THEN
        raise_application_error(-20101, 'User Requires a Health Supporter');
    END IF;
END;
/
CREATE TABLE Health_Observation_Type(
    Hot_Id NUMBER(16),
    Hot_Name VARCHAR(255),
    Hot_Disease VARCHAR(200),
    Hot_UpperLimit NUMBER(16),
    Hot_LowerLimit NUMBER(16),
    Hot_Frequency NUMBER(16),
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
CREATE TABLE Health_Observation(
    Ho_Patient NUMBER(16),
    Ho_ObservationType Number(16),
    Ho_Value Number(16),
    Ho_ObservedDateTime Date,
    Ho_RecordedDateTime Date default sysdate,
    CONSTRAINT HO_PK PRIMARY KEY (Ho_Patient, Ho_ObservationType, Ho_ObservedDateTime, Ho_RecordedDateTime),
    CONSTRAINT HO_P_FK FOREIGN KEY (Ho_Patient) REFERENCES Patient(Pat_Person),
    CONSTRAINT HO_HOT_FK FOREIGN KEY (Ho_ObservationType) REFERENCES Health_Observation_Type(Hot_Id)
);
CREATE TABLE Recommendation(
    Rec_HS_Supporter NUMBER(16),
    Rec_HS_Patient Number(16),
    Rec_HOT_Type Number(16),
    Rec_DoctorComment VARCHAR(999),
    CONSTRAINT REC_PK PRIMARY KEY (Rec_HS_Supporter, Rec_HS_Patient, Rec_HOT_Type),
    CONSTRAINT REC_FK_SUP FOREIGN KEY (Rec_HS_Supporter, Rec_HS_Patient) REFERENCES Health_Supporter(HS_Supporter, HS_Patient),
    CONSTRAINT REC_FK_OBST FOREIGN KEY (Rec_HOT_Type) references Health_Observation_Type(HoT_Id)
);
CREATE TABLE ALERT(
    Al_HS_Patient Number(16),
    Al_HOT_Type Number(16),
    Al_Read Number(1),
    Al_Sent Date,
    Al_Alert VARCHAR(999),
    Al_Sys Date DEFAULT SYSDATE,
    CONSTRAINT ALERT_PK PRIMARY KEY (Al_HS_Patient, Al_Sys, Al_HOT_Type),
    CONSTRAINT ALERT_FK_P FOREIGN KEY (Al_HS_Patient) REFERENCES Person(Per_Id),
    CONSTRAINT ALERT_FK_H FOREIGN KEY (Al_HOT_Type) REFERENCES Health_Observation_Type(Hot_Id)
);

/*
-- Query for Health Observation Types that have broken their "recommendation" rule (and generate an alert for them).
-- 1) Query for recommendations
-- 2) Query for Health Observations
-- 3) For each Health Observation that does not meet the minimum/maximum OR is outside the threshold, 
-- 4) Generate an alert

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

CREATE OR REPLACE TRIGGER ALERT_RANGE 
AFTER INSERT OR UPDATE OF HO_VALUE ON HEALTH_OBSERVATION 
REFERENCING OLD AS HO_OLD NEW AS HO_NEW 
FOR EACH ROW
DECLARE 
    u_limit NUMBER(16); 
    l_limit NUMBER(16);
    HS_support NUMBER(16);
    out_of_bounds NUMBER(16) := 1;
BEGIN
  select Hot_UpperLimit, Hot_LowerLimit INTO u_limit, l_limit FROM Health_Observation_Type
  WHERE :HO_NEW.Ho_ObservationType = Health_Observation_Type.Hot_Id;
  IF (:HO_NEW.Ho_Value > l_limit AND :HO_NEW.Ho_Value < u_limit) THEN
      out_of_bounds := 0;
  END IF;
  select HS_Supporter INTO HS_support FROM Health_Supporter
  WHERE :HO_NEW.Ho_Patient = Health_Supporter.HS_Patient;
  IF (out_of_bounds = 1) THEN
    INSERT INTO ALERT VALUES (HS_support, :HO_NEW.Ho_Patient, :HO_NEW.Ho_ObservationType, 0, :HO_NEW.Ho_ObservedDateTime, (:HO_NEW.Ho_ObservationType || 'for' || :HO_NEW.Ho_Patient || 'is not in the specified range. Immediate action required.'));
  END IF;
END;
/

-- Trigger preventing users from having more than two health supporters
CREATE OR REPLACE TRIGGER TWO_SUPPORTERS_MAX
BEFORE INSERT OR UPDATE ON Health_Supporter 
REFERENCING OLD AS HS_OLD  NEW AS HS_NEW
For Each Row
Declare
    CountOfSupporters Number(2);
BEGIN
    SELECT 
        count(HS_Patient) INTO CountOfSupporters 
    from Health_Supporter 
    where 
        HS_Patient = :HS_OLD.HS_PATIENT AND 
        HS_DateAuthorized <= SYSDATE AND
        (HS_DateUnAuthorized IS NULL OR HS_DateUnAuthorized >= SYSDATE);

    IF( CountOfSupporters >= 2) THEN
        raise_application_error(-20001, 'A patient may only have up to two supporters.');
    END IF;
END;
/
create or replace function deleteDiagnosis(DiseaseName varchar, Patient number) return number is
    rows_ number;
    di_patient number;
    Numdiseases number;
begin
    <<del>> begin 
        delete Diagnosis where Di_Patient= deleteDiagnosis.Patient AND Di_DiseaseName= deleteDiagnosis.DiseaseName
        returning Diagnosis.di_patient into deleteDiagnosis.di_patient
        ;
        rows_ := sql%rowcount;
        if rows_ > 1 then raise too_many_rows; end if;
    end del;
    select count(1) into deleteDiagnosis.numDiseases from Diagnosis where Di_Patient = deleteDiagnosis.di_patient;
    if deleteDiagnosis.numdiseases = 0 then <<upd>> begin 
        update Patient set Pat_Sick = 0 where Pat_Person = deleteDiagnosis.di_patient;
        exception when others then 
            dbms_output.put_line('Cannot update Patient di_patient='||di_patient);
            raise;
    end upd; end if;
    return rows_;
end;
/
