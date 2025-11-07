CREATE DATABASE IF NOT EXISTS GRP35_db;

USE GRP35_db;

CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    address VARCHAR(50) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE Menus (
    menu_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    menu_description VARCHAR(200) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    preparation_time TIME NOT NULL,
    is_available TINYINT DEFAULT 1 NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
    -- Paadd image ty tyy
);

CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    preparation_time TIME NOT NULL,
    delivery_time TIME NOT NULL,
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    status ENUM('Pending', 'Preparing', 'Delivered', 'Cancelled') DEFAULT 'Pending' NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    customer_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

CREATE TABLE Order_lines (
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
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    delivery_fee DECIMAL(10,2) NOT NULL CHECK (delivery_fee >= 0),
    tax_amount DECIMAL(10,2) NOT NULL CHECK (tax_amount >= 0),
    amount_paid DECIMAL(10,2) NOT NULL CHECK (amount_paid >= 0),
    reference_number INT UNIQUE NOT NULL,
    is_paid TINYINT DEFAULT 0 NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
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
