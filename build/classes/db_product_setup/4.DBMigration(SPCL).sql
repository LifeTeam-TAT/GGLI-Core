ALTER TABLE  [INSUREDPERSON_BENEFICIARIES_LINK] ADD CUSTOMERID VARCHAR(36), ORGANIZATIONID VARCHAR(36), 
      PHONE NVARCHAR(100), FAX NVARCHAR(100), EMAIL NVARCHAR(100), MOBILE NVARCHAR(100);

  ALTER TABLE  [LIFEPOLICY_INSUREDPERSON_BENEFICIARIES_LINK] ADD CUSTOMERID VARCHAR(36), ORGANIZATIONID VARCHAR(36), 
      PHONE NVARCHAR(100), FAX NVARCHAR(100), EMAIL NVARCHAR(100), MOBILE NVARCHAR(100);

  ALTER TABLE  [LPOLICY_INSU_BENEFI_HISTORY] ADD CUSTOMERID VARCHAR(36), ORGANIZATIONID VARCHAR(36), 
      PHONE NVARCHAR(100), FAX NVARCHAR(100), EMAIL NVARCHAR(100), MOBILE NVARCHAR(100);

  ALTER TABLE  [LPOLICY_INSU_BENEFI_EDITHISTORY] ADD CUSTOMERID VARCHAR(36), ORGANIZATIONID VARCHAR(36), 
      PHONE NVARCHAR(100), FAX NVARCHAR(100), EMAIL NVARCHAR(100), MOBILE NVARCHAR(100);




ALTER TABLE  [PRODUCTPROCESS] ADD OPTIONTYPE [varchar](30) NULL;


