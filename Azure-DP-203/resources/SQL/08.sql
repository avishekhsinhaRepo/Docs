-- Lab - T-SQL - Table Joins

-- JOINS - This is used to combine rows from two or more tables, based on a related column between them

SELECT ProductID,ListPrice,OrderQty,LineTotal
FROM SalesLT.Product JOIN SalesLT.SalesOrderDetail
ON SalesLT.Product.ProductID=SalesLT.SalesOrderDetail.ProductID

SELECT SalesLT.Product.ProductID,ListPrice,OrderQty,LineTotal
FROM SalesLT.Product JOIN SalesLT.SalesOrderDetail
ON SalesLT.Product.ProductID=SalesLT.SalesOrderDetail.ProductID


SELECT SalesLT.Product.ProductID,ListPrice,OrderQty,LineTotal,((ListPrice*OrderQty)-LineTotal) as ' Discount Amount'
FROM SalesLT.Product JOIN SalesLT.SalesOrderDetail
ON SalesLT.Product.ProductID=SalesLT.SalesOrderDetail.ProductID


SELECT SalesLT.Product.ProductID,ListPrice,OrderQty,LineTotal,((ListPrice*OrderQty)-LineTotal) as ' Discount Amount'
FROM SalesLT.Product JOIN SalesLT.SalesOrderDetail
ON SalesLT.Product.ProductID=SalesLT.SalesOrderDetail.ProductID
WHERE OrderQty>10
