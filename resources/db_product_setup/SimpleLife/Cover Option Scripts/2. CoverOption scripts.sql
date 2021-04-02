
-- Delete 5 Simple Life addon from ADDON Table and PRODUCT_ADDON_LINK
--DELETE FROM ADDON WHERE NAME LIKE 'SIMPLE%';
--DELETE FROM PRODUCT_ADDON_LINK WHERE ADDONID=''; -- have to delete related addon id

-- Adding product keyfactor
INSERT INTO [dbo].[PRODUCT_KEYFACTOR_LINK] ([PRODUCTID], [KEYFACTORID]) VALUES (N'ISPRD003000009609328102020          ', N'ISSYS013000009607714122020          ')

-- Adding product premium rate
--INSERT INTO [dbo].[PRODUCT_PREMIUMRATE_LINK] ([ID], [PREMIUMRATE], [PRODUCTID], [CREATEDUSERID], [CREATEDDATE], [UPDATEDUSERID], [UPDATEDDATE], [VERSION], [SUMINSUREDTYPE]) VALUES (N'ISPRD002000012887914122020          ', CAST(150.0000 AS Numeric(18, 4)), N'ISPRD003000009609328102020          ', N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T20:35:32.427' AS DateTime), NULL, NULL, 1, NULL)
--INSERT INTO [dbo].[PRODUCT_RATEKEYFACTOR] ([ID], [PRODUCTPREMIUMRATEID], [KEYFACTORID], [FROMVALUE], [TOVALUE], [VALUE], [REFERENCENAME], [VERSION]) VALUES (N'149824                              ', N'ISPRD002000012887914122020          ', N'ISSYS0130001000000000129032013      ', CAST(1000000.0000 AS Numeric(18, 4)), CAST(10000000.0000 AS Numeric(18, 4)), NULL, NULL, 1)
--INSERT INTO [dbo].[PRODUCT_RATEKEYFACTOR] ([ID], [PRODUCTPREMIUMRATEID], [KEYFACTORID], [FROMVALUE], [TOVALUE], [VALUE], [REFERENCENAME], [VERSION]) VALUES (N'149823                              ', N'ISPRD002000012887914122020          ', N'ISSYS013000009607714122020          ', CAST(0.0000 AS Numeric(18, 4)), CAST(0.0000 AS Numeric(18, 4)), N'COVER OPTION 1', NULL, 1)
--INSERT INTO [dbo].[PRODUCT_RATEKEYFACTOR] ([ID], [PRODUCTPREMIUMRATEID], [KEYFACTORID], [FROMVALUE], [TOVALUE], [VALUE], [REFERENCENAME], [VERSION]) VALUES (N'149806                              ', N'ISPRD002000012887914122020          ', N'ISSYS013000009605728112020          ', CAST(0.0000 AS Numeric(18, 4)), CAST(0.0000 AS Numeric(18, 4)), N'1', NULL, 1)


-- Adding keyfactors for cover option
INSERT INTO [dbo].[KEYFACTOR] ([ID], [VALUE], [KEYFACTORTYPE], [CREATEDUSERID], [CREATEDDATE], [UPDATEDUSERID], [UPDATEDDATE], [VERSION], [MAPKEY]) VALUES (N'ISSYS013000009606714122020          ', N'Cover Option 1', N'FIXED', N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T11:27:20.083' AS DateTime), N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T16:09:45.780' AS DateTime), 2, NULL)
INSERT INTO [dbo].[KEYFACTOR] ([ID], [VALUE], [KEYFACTORTYPE], [CREATEDUSERID], [CREATEDDATE], [UPDATEDUSERID], [UPDATEDDATE], [VERSION], [MAPKEY]) VALUES (N'ISSYS013000009606814122020          ', N'Cover Option 2', N'FIXED', N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T11:27:41.580' AS DateTime), N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T16:10:01.897' AS DateTime), 2, NULL)
INSERT INTO [dbo].[KEYFACTOR] ([ID], [VALUE], [KEYFACTORTYPE], [CREATEDUSERID], [CREATEDDATE], [UPDATEDUSERID], [UPDATEDDATE], [VERSION], [MAPKEY]) VALUES (N'ISSYS013000009606914122020          ', N'Cover Option 3', N'FIXED', N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T11:27:49.890' AS DateTime), N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T16:10:18.950' AS DateTime), 2, NULL)
INSERT INTO [dbo].[KEYFACTOR] ([ID], [VALUE], [KEYFACTORTYPE], [CREATEDUSERID], [CREATEDDATE], [UPDATEDUSERID], [UPDATEDDATE], [VERSION], [MAPKEY]) VALUES (N'ISSYS013000009607014122020          ', N'Cover Option 4', N'FIXED', N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T11:27:58.557' AS DateTime), N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T16:10:31.637' AS DateTime), 2, NULL)
INSERT INTO [dbo].[KEYFACTOR] ([ID], [VALUE], [KEYFACTORTYPE], [CREATEDUSERID], [CREATEDDATE], [UPDATEDUSERID], [UPDATEDDATE], [VERSION], [MAPKEY]) VALUES (N'ISSYS013000009607114122020          ', N'Cover Option 5', N'FIXED', N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T11:28:07.623' AS DateTime), N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T16:10:43.170' AS DateTime), 2, NULL)
INSERT INTO [dbo].[KEYFACTOR] ([ID], [VALUE], [KEYFACTORTYPE], [CREATEDUSERID], [CREATEDDATE], [UPDATEDUSERID], [UPDATEDDATE], [VERSION], [MAPKEY]) VALUES (N'ISSYS013000009607714122020          ', N'Cover Option', N'FIXED', N'INUSR0010001000000000129032013      ', CAST(N'2020-12-14T20:17:33.560' AS DateTime), NULL, NULL, 1, NULL)

-- add 2 more columns for InsuredPerson
ALTER TABLE LIFEPROPOSAL_INSUREDPERSON_LINK
ADD PERIODOFYEAR INT NULL;

ALTER TABLE LIFEPROPOSAL_INSUREDPERSON_LINK
ADD PERIODOFWEEK INT NULL;