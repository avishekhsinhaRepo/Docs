-- Lab - The aggregate functions of SUM,COUNT,AVG,MIN,MAX cannot be used along with the WHERE Clause

-- That is why we have the HAVING clause in SQL

SELECT count(ProductID) as 'Product ID Count', Color
FROM SalesLT.Product
WHERE Color IS NOT NULL
GROUP BY Color
HAVING count(Color)>10
