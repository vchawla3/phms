CREATE TABLE Person (
    Id number(16),
    FirstName VARCHAR(25) NOT NULL,
    LastName VARCHAR(25) NOT NULL,
    DateOfBirth DATE NOT NULL,
    Address VARCHAR(255),
    Phone VARCHAR(10),
    Sex VARCHAR(1),
    Password VARCHAR(16),
    CONSTRAINT Person_PK PRIMARY KEY(Id)
);
CREATE TABLE PATIENT(
    Person NUMBER(16),
    Sick NUMBER(1),
    CONSTRAINT PATIENT_PK PRIMARY KEY (Person),
    CONSTRAINT PATIENT_FK FOREIGN KEY (Person) REFERENCES Person(Id)
);
CREATE TABLE Health_Supporter(
    Supporter NUMBER(16),
    Patient NUMBER(16),
    DateAuthorized DATE,
    DateUnauthorized DATE,
    CONSTRAINT HS_PK PRIMARY KEY(Supporter, Patient),
    CONSTRAINT HS_HS FOREIGN KEY(Supporter) REFERENCES Person(Id),
    CONSTRAINT HS_P FOREIGN KEY(Patient) REFERENCES Patient(Person)
);
CREATE TABLE Disease(
    DiseaseName VARCHAR(200),
    CONSTRAINT DISEASE_PK PRIMARY KEY(DiseaseName)
);
CREATE TABLE Health_Observation_Type(
    Id NUMBER(16),
    Name VARCHAR(255),
    Disease VARCHAR(200),
    UpperLimit NUMBER(16),
    LowerLimit NUMBER(16),
    Frequency Long,  --Hourly
    CONSTRAINT HOT_PK PRIMARY KEY(Id),
    CONSTRAINT HOT_FK_D FOREIGN KEY (Disease) REFERENCES Disease(DiseaseName)
);
CREATE SEQUENCE Health_Obs_Seq START WITH 1;
CREATE OR REPLACE TRIGGER HealthObs_AI 
BEFORE INSERT ON Health_Observation_Type 
FOR EACH ROW
BEGIN
  SELECT Health_Obs_Seq.NEXTVAL
  INTO   :new.id
  FROM   dual;
END;
/
CREATE TABLE Health_Observation(
    Patient NUMBER(16),
    ObservationType Number(16),
    Value LONG,
    DateTaken Date,
    CONSTRAINT HO_PK PRIMARY KEY (Patient, ObservationType),
    CONSTRAINT HO_P_FK FOREIGN KEY (Patient) REFERENCES Person(Id),
    CONSTRAINT HO_HOT_FK FOREIGN KEY (ObservationType) REFERENCES Health_Observation_Type(Id)
);
CREATE TABLE Recommendation(
    HS_Supporter NUMBER(16),
    HS_Patient Number(16),
    OBS_Type Number(16),
    OBS_Patient Number(16),
    DoctorComment VARCHAR(999),
    CONSTRAINT REC_PK PRIMARY KEY (HS_Supporter, HS_Patient, OBS_Patient, OBS_Type),
    CONSTRAINT REC_FK_SUP FOREIGN KEY (HS_Supporter, HS_Patient) REFERENCES Health_Supporter(Supporter, Patient),
    CONSTRAINT REC_FK_OBST FOREIGN KEY (OBS_Type, OBS_Patient) references Health_Observation(ObservationType, Patient)
);
CREATE TABLE ALLERT(
    HS_Supporter NUMBER(16),
    HS_Patient Number(16),
    OBS_Type Number(16),
    OBS_Patient Number(16),
    Read Number(1),
    Sent Date,
    CONSTRAINT ALLERT_PK PRIMARY KEY (HS_Supporter, HS_Patient, OBS_Type, OBS_Patient),
    CONSTRAINT ALLERT_FK_P FOREIGN KEY (HS_Supporter, HS_Patient, OBS_Type, OBS_Patient) REFERENCES Recommendation(HS_Supporter, HS_Patient, OBS_Type, OBS_Patient)
);
