ALTER TABLE [LIFEPROPOSAL] ADD ISCHANNEL bit;

DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE NOT EXISTS (SELECT * FROM [ggli].[dbo].[PRODUCT_PREMIUMRATE_LINK] PPRL WHERE PRODUCTPREMIUMRATEID = PPRL.ID)

DELETE  FROM [PRODUCT_RATEKEYFACTOR] WHERE ID='147951' OR ID='147952' OR ID='147960';



