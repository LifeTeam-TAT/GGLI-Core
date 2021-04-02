-- 1 Expend Keyfactor Value column allocation size
ALTER TABLE KEYFACTOR
ALTER COLUMN VALUE NVARCHAR(100) NULL;
----------------------------------------------------------------------------------------------------------
-- 2 Updating cover option values in Keyfactor
UPDATE KEYFACTOR
SET VALUE = 'Death Cover'
WHERE VALUE = 'Cover Option 1';

UPDATE KEYFACTOR
SET VALUE = 'Death and Total Permanent Disable (TPD)'
WHERE VALUE = 'Cover Option 2';

UPDATE KEYFACTOR
SET VALUE = 'Death, Additional Cover for Accidental Death and TPD'
WHERE VALUE = 'Cover Option 3';

UPDATE KEYFACTOR
SET VALUE = 'Death and TPD, Additional Cover for Accidental Death and TPD'
WHERE VALUE = 'Cover Option 4';

UPDATE KEYFACTOR
SET VALUE = 'Accidental Death and Accidental TPD Only'
WHERE VALUE = 'Cover Option 5';
----------------------------------------------------------------------------------------------------------
-- 3. Updating cover option values in PRODUCT_RATEKEYFACTOR

ALTER TABLE PRODUCT_RATEKEYFACTOR
ALTER COLUMN VALUE NVARCHAR(100) NULL;


UPDATE PRODUCT_RATEKEYFACTOR
SET VALUE = 'Death Cover'
WHERE VALUE = 'Cover Option 1';

UPDATE PRODUCT_RATEKEYFACTOR
SET VALUE = 'Death and Total Permanent Disable (TPD)'
WHERE VALUE = 'Cover Option 2';

UPDATE PRODUCT_RATEKEYFACTOR
SET VALUE = 'Death, Additional Cover for Accidental Death and TPD'
WHERE VALUE = 'Cover Option 3';

UPDATE PRODUCT_RATEKEYFACTOR
SET VALUE = 'Death and TPD, Additional Cover for Accidental Death and TPD'
WHERE VALUE = 'Cover Option 4';

UPDATE PRODUCT_RATEKEYFACTOR
SET VALUE = 'Accidental Death and Accidental TPD Only'
WHERE VALUE = 'Cover Option 5';
