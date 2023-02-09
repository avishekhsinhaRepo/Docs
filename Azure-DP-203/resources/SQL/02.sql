-- Lab - T-SQL - WHERE clause

-- The WHERE clause is used to extract only those records that fulfill a specified condition.

SELECT ProductID, Name, ProductNumber FROM SalesLT.Product where ProductID=680

SELECT ProductID, Name, ProductNumber FROM SalesLT.Product where ProductID>680

SELECT ProductID, Name, ProductNumber FROM SalesLT.Product where ProductID BETWEEN 680 and 800

SELECT ProductID, Name, ProductNumber FROM SalesLT.Product where Name Like '%Silver%'
