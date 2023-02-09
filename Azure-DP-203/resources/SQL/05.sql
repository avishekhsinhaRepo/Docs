-- Lab - T-SQL - GROUP BY clause

-- The GROUP BY keyword is used to group the rows into summary rows

-- This can also be used along with the Aggregation functions of (COUNT,MAX,MIN,SUM,AVG)

SELECT count(ProductID) 
FROM SalesLT.Product
GROUP BY Color

SELECT count(ProductID) as 'Product ID Count'
FROM SalesLT.Product
GROUP BY Color


SELECT count(ProductID) as 'Product ID Count', Color
FROM SalesLT.Product
GROUP BY Color

SELECT count(ProductID) as 'Product ID Count', Color
FROM SalesLT.Product
WHERE Color IS NOT NULL
GROUP BY Color

