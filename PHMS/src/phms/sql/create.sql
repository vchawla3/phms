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
CREATE TABLE Health_Observation_Type(
    Hot_Id NUMBER(16),
    Hot_Name VARCHAR(255),
    Hot_Disease VARCHAR(200),
    Hot_UpperLimit NUMBER(16),
    Hot_LowerLimit NUMBER(16),
    Hot_Frequency Long,  --Hourly
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
CREATE TABLE ALLERT(
    All_HS_Supporter NUMBER(16),
    All_HS_Patient Number(16),
    All_OBS_Type Number(16),
    All_OBS_Patient Number(16),
    All_Read Number(1),
    All_Sent Date,
    CONSTRAINT ALLERT_PK PRIMARY KEY (All_HS_Supporter, All_HS_Patient, All_OBS_Type, All_OBS_Patient),
    --Todo: Can we simplify the FK's here?
    CONSTRAINT ALLERT_FK_P FOREIGN KEY (All_HS_Supporter, All_HS_Patient, All_OBS_Type, All_OBS_Patient) REFERENCES Recommendation(Rec_HS_Supporter, Rec_HS_Patient, Rec_OBS_Type, Rec_OBS_Patient)
);
