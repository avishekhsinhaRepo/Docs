-- Lab - T-SQL - Aggregate Functions

SELECT count(ProductID) FROM SalesLT.Product where Name Like '%Silver%'
SELECT MAX(ProductID) FROM SalesLT.Product where Name Like '%Silver%'
SELECT MIN(ProductID) FROM SalesLT.Product where Name Like '%Silver%'
SELECT SUM(ProductID) FROM SalesLT.Product where Name Like '%Silver%'
SELECT AVG(ProductID) FROM SalesLT.Product where Name Like '%Silver%'