CREATE TABLE Person (
    Id number(16),
    FirstName VARCHAR(25) NOT NULL,
    LastName VARCHAR(25) NOT NULL,
    DateOfBirth DATE NOT NULL,
    Address VARCHAR(255),
    Phone VARCHAR(10),
    Sex VARCHAR(1),
    Password VARCHAR(16),
    SickPatient Boolean,
    CONSTRAINT Person_PK PRIMARY KEY(PersonId)
);
CREATE TABLE Health_Supporter (
    Supporter NUMBER(16),
    Patient NUMBER(16),
    CONSTRAINT HS_PK PRIMARY KEY(Supporter, Patient),
    CONSTRAINT HS_HS FOREIGN KEY(Supporter) REFERENCES Person(Id),
    CONSTRAINT HS_P FOREIGN KEY(Patient) REFERENCES Person(Id)
);
CREATE TABLE Disease(
    DiseaseName VARCHAR(200),
    CONSTRAINT DISEASE_PK PRIMARY KEY(DiseaseName)
);
CREATE TABLE HealthObservationType(
    Name VARCHAR(255),
    CONSTRAINT HO_PK PRIMARY KEY(Name)
);
