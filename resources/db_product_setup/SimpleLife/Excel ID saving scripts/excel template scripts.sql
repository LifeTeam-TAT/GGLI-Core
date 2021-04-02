-- 1 Creating Table for excel upload record saving
CREATE TABLE [dbo].[AUDITLOG](
	[ID] [char](36) NOT NULL,
	[EXCELFILEID] [char](36) NULL,
	[ORGANIZATIONID] [char](36) NULL,
	[CREATEDUSERID] [char](36) NULL,
	[CREATEDDATE] [datetime] NULL,
	[UPDATEDUSERID] [char](36) NULL,
	[UPDATEDDATE] [datetime] NULL,
	[VERSION] [int] NOT NULL,
	PRIMARY KEY (ID),
	CONSTRAINT FK_ORGANIZATION FOREIGN KEY (ORGANIZATIONID)
	REFERENCES ORGANIZATION(ID)
)

-- 2 Add foreign key column in proposal table for Audit table
ALTER TABLE LIFEPROPOSAL
ADD AUDITLOGID [char](36);

ALTER TABLE LIFEPROPOSAL
ADD CONSTRAINT FK_AUDITLOG
FOREIGN KEY (AUDITLOGID) REFERENCES AUDITLOG(ID);

-- 3 Add AuditLog_Gen in ID_GEN
INSERT INTO [dbo].[ID_GEN](GEN_NAME,GEN_VAL)
VALUES ('AUDITLOG_GEN', 98719)

-- 4 Add Custom id gen for Simple Life Excel
INSERT INTO [dbo].[CUSTOM_ID_GEN] ([ID] ,[GENERATEITEM] ,[MAXVALUE] ,[PREFIX] ,[LENGTH] ,[ISDATEBASED] ,[CREATEDUSERID] ,[VERSION])
VALUES ('CUSID2194', 'SIMPLE_LIFE_EXCEL_PROPOSAL_NO', 8, 'EXL', 5, 1, 'INUSR0010001000000000129032013', 1)