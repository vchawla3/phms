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
AFTER INSERT ON Diagnosis
FOR EACH ROW WHEN (NEW.Di_DiseaseName <> OLD.Di_DiseaseName)
BEGIN
	UPDATE Patient SET Pat_Sick = 1 WHERE Pat_Person = :NEW.Di_Patient;
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
    Ho_Value LONG,
    Ho_ObservedDateTime Date,
    Ho_RecordedDateTime Date,
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
    Al_HS_Supporter NUMBER(16),
    Al_HS_Patient Number(16),
    Al_HOT_Type Number(16),
    Al_Read Number(1),
    Al_Sent Date,
    Al_Alert VARCHAR(999),
    CONSTRAINT ALERT_PK PRIMARY KEY (Al_HS_Supporter, Al_HS_Patient, AL_Sent),
    CONSTRAINT ALERT_FK_P FOREIGN KEY (Al_HS_Supporter, Al_HS_Patient, Al_HOT_Type) REFERENCES Recommendation(Rec_HS_Supporter, Rec_HS_Patient, Rec_HOT_Type)
);