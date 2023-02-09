-- Lab - T-SQL - Creating Tables with Keys

CREATE TABLE Customer (
    CustomerID varchar(100) NOT NULL,
    CustomerName varchar(1000),    
    PRIMARY KEY (CustomerID)
);


CREATE TABLE Course (
    CourseID varchar(100) NOT NULL,
    CourseName varchar(1000),
    Price real,    
    PRIMARY KEY (CourseID)
);


CREATE TABLE Orders (
    OrderID varchar(100) NOT NULL,
    CourseID varchar(100),
    CustomerID varchar(100),    
    Discountpercent int,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID)
);

INSERT INTO Customer(CustomerID,CustomerName) values ('C1','UserA');
INSERT INTO Customer(CustomerID,CustomerName) values ('C2','UserB');
INSERT INTO Customer(CustomerID,CustomerName) values ('C3','UserC');

SELECT * FROM Customer;

INSERT INTO COURSE(CourseID,CourseName,Price) values ('D1','AZ-900',99.99);
INSERT INTO COURSE(CourseID,CourseName,Price) values ('D2','DP-900',100.99);
INSERT INTO COURSE(CourseID,CourseName,Price) values ('D3','AZ-104',89.99);

SELECT * FROM Course;

INSERT INTO Orders(OrderID,CourseID,CustomerID,Discountpercent) values ('O1','D2','C1',90);
INSERT INTO Orders(OrderID,CourseID,CustomerID,Discountpercent) values ('O2','D1','C2',50);
INSERT INTO Orders(OrderID,CourseID,CustomerID,Discountpercent) values ('O3','D3','C3',60);

SELECT * FROM Orders;

INSERT INTO Orders(OrderID,CourseID,CustomerID,Discountpercent) values ('O4','D3','C4',60);
