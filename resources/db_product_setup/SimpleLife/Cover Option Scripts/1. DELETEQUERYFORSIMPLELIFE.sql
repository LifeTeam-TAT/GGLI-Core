-- Delete 5 Simple Life addon from ADDON Table and PRODUCT_ADDON_LINK
DELETE FROM PRODUCT_ADDON_LINK where PRODUCTID='ISPRD003000009609328102020'; -- have to delete related addon id

DELETE FROM LIFEPROPOSAL_INSUREDPERSON_ADDON_LINK WHERE LIFEPRODUCTADDONID='ISSYS014000009607829112020'
DELETE FROM LIFEPOLICY_INSUREDPERSON_ADDON_LINK WHERE PRODUCTADDONID='ISSYS014000009607829112020'
DELETE FROM ADDON WHERE ID='ISSYS014000009607829112020'

DELETE FROM LIFEPROPOSAL_INSUREDPERSON_ADDON_LINK WHERE LIFEPRODUCTADDONID='ISSYS014000009607929112020'
DELETE FROM LIFEPOLICY_INSUREDPERSON_ADDON_LINK WHERE PRODUCTADDONID='ISSYS014000009607929112020'
DELETE FROM ADDON WHERE ID='ISSYS014000009607929112020'

DELETE FROM LIFEPROPOSAL_INSUREDPERSON_ADDON_LINK WHERE LIFEPRODUCTADDONID='ISSYS014000009608029112020'
DELETE FROM LIFEPOLICY_INSUREDPERSON_ADDON_LINK WHERE PRODUCTADDONID='ISSYS014000009608029112020'
DELETE FROM ADDON WHERE ID='ISSYS014000009608029112020'


DELETE FROM LIFEPROPOSAL_INSUREDPERSON_ADDON_LINK WHERE LIFEPRODUCTADDONID='ISSYS014000009608129112020'
DELETE FROM LIFEPOLICY_INSUREDPERSON_ADDON_LINK WHERE PRODUCTADDONID='ISSYS014000009608129112020'
DELETE FROM ADDON WHERE ID='ISSYS014000009608129112020'


DELETE FROM LIFEPROPOSAL_INSUREDPERSON_ADDON_LINK WHERE LIFEPRODUCTADDONID='ISSYS014000009608229112020'
DELETE FROM LIFEPOLICY_INSUREDPERSON_ADDON_LINK WHERE PRODUCTADDONID='ISSYS014000009608229112020'
DELETE FROM ADDON WHERE ID='ISSYS014000009608229112020'

DELETE FROM PRODUCT_KEYFACTOR_LINK WHERE KEYFACTORID='ISSYS013000009605928112020'


--DELETE ID FROM [PRODUCT_PREMIUMRATE_LINK] ISPRD002000012894030112020          
DELETE FROM [PRODUCT_PREMIUMRATE_LINK] WHERE PRODUCTID='ISPRD003000009609328102020'

--DELETE ID FROM [PRODUCT_RATEKEYFACTOR]
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887030112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887130112020' 
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887230112020' 
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887330112020'    
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887430112020'  
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887530112020'   
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887630112020' 
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887730112020'  
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887830112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012887930112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888030112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888130112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888230112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888330112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888430112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888530112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888630112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888730112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888830112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012888930112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889030112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889130112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889230112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889330112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889430112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889530112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889630112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889730112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889830112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012889930112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890030112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890130112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890230112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890330112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890430112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890530112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890630112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890730112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890830112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012890930112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891030112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891130112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891230112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891330112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891430112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891530112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891630112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891730112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891830112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012891930112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892030112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892130112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892230112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892330112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892430112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892530112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892630112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892730112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892830112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012892930112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893030112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893130112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893230112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893330112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893430112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD0020000128935530112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893630112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893730112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893830112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012893930112020'
DELETE FROM [PRODUCT_RATEKEYFACTOR] WHERE PRODUCTPREMIUMRATEID='ISPRD002000012894030112020'














