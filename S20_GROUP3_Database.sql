CREATE DATABASE IF NOT EXISTS S20_GROUP3_Database;

USE S20_GROUP3_Database;
SELECT * FROM cities;
CREATE TABLE Menu_Category (
    menu_category_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_category_name VARCHAR(50) NOT NULL,
    time_start TIME NOT NULL DEFAULT '00:00:00',
    time_end TIME NOT NULL DEFAULT '23:59:59',
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_time_order CHECK (time_start < time_end)
);

CREATE TABLE City_Delivery_Groups (
    city_delivery_group_id INT AUTO_INCREMENT PRIMARY KEY,
    city_delivery_fee DECIMAL(10,2) NOT NULL CHECK (city_delivery_fee >= 0),
    city_delivery_time_minutes INT NOT NULL,
    is_available TINYINT(1) DEFAULT 1 NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE Cities (
    city_id INT AUTO_INCREMENT PRIMARY KEY,
    city_name VARCHAR(50) NOT NULL UNIQUE,
    city_delivery_group_id INT NOT NULL,
    is_available TINYINT(1) DEFAULT 1 NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (city_delivery_group_id) REFERENCES City_Delivery_Groups(city_delivery_group_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL, -- cipher the password
    address VARCHAR(50) NOT NULL,
    city_id INT,
    is_active TINYINT(1) DEFAULT 1 NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (city_id) REFERENCES Cities(city_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE Menus (
    menu_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    menu_description VARCHAR(200) NOT NULL,
    menu_category_id INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    preparation_time TIME NOT NULL,
    image VARCHAR(200) NOT NULL,
    is_available TINYINT DEFAULT 1 NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (menu_category_id) REFERENCES Menu_Category(menu_category_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    preparation_time TIME NOT NULL,
    delivery_time TIME NOT NULL,
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    -- Pending = Not Paid / "Pay Later" option
    -- adjust CustomerCartPageController in the insert statement
    status ENUM('Pending', 'Preparing', 'In Transit', 'Delivered', 'Cancelled') DEFAULT 'Pending' NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    customer_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

CREATE TABLE Order_Lines (
    order_line_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_quantity INT NOT NULL CHECK (menu_quantity > 0),
    menu_price DECIMAL(10,2) NOT NULL CHECK (menu_price >= 0),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    order_id INT NOT NULL,
    menu_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES Menus(menu_id)
);

CREATE TABLE Payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    delivery_fee DECIMAL(10,2) NOT NULL CHECK (delivery_fee >= 0),
	total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    amount_paid DECIMAL(10,2) NOT NULL CHECK (amount_paid >= 0),
    reference_number VARCHAR(10) UNIQUE NOT NULL,
    is_paid TINYINT DEFAULT 0 NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    paid_date DATETIME,
    order_id INT NOT NULL,
    customer_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

CREATE TABLE Admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

INSERT INTO City_Delivery_Groups (city_delivery_fee, city_delivery_time_minutes) VALUES
(0.00, 1),   -- Manila
(10.00, 2),  -- Adjacent
(20.00, 3),  -- Far from Adjacent
(30.00, 4);  -- Farthest

INSERT INTO Cities (city_name, city_delivery_group_id) VALUES
('Manila', 1),
('Makati', 2),
('Mandaluyong', 2),
('Pasay', 2),
('San Juan', 2),
('Quezon', 2),
('Caloocan', 3),
('Malabon', 3),
('Navotas', 3),
('Pasig', 3),
('Marikina', 3),
('Taguig', 3),
('Las Piñas', 4),
('Parañaque', 4),
('Muntinlupa', 4),
('Valenzuela', 4);

-- Confirmations
SELECT * FROM Customers;
SELECT * FROM Menus;
SELECT * FROM Payments;
SELECT * FROM Admins;
SELECT * FROM Cities;
SELECT * FROM City_Delivery_Groups;
SELECT * FROM Order_Lines;
SELECT * FROM Orders;

DESCRIBE Customers;
DESCRIBE Menus;
DESCRIBE Payments;
DESCRIBE Admins;
DESCRIBE Cities;
DESCRIBE City_Delivery_Groups;
DESCRIBE Order_Lines;
DESCRIBE Orders;


