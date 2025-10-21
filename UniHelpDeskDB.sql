CREATE DATABASE UniHelpDeskDB;

-- users Table (Base Table for all user types)
CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY(1,1), 
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,      
    university_id VARCHAR(20) NOT NULL UNIQUE, 
    password_hash VARCHAR(255) NOT NULL,     
    role VARCHAR(20) NOT NULL CHECK (role IN ('Student', 'Lecturer', 'Staff', 'Admin')), 
    created_at DATETIME DEFAULT GETDATE()   
);
GO

-- students Table (Specialized table for students)
CREATE TABLE students (
    user_id INT PRIMARY KEY,                 
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO

-- lecturers Table (Specialized table for lecturers)
CREATE TABLE lecturers (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO

-- support_staff Table (Specialized table for staff members)
CREATE TABLE support_staff (
    user_id INT PRIMARY KEY,
    staff_type VARCHAR(20) NOT NULL CHECK (staff_type IN ('IT_Support', 'Help_Desk', 'Counselor')),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO
-----------------------------------------------------------------------------------------------------------------
--add Faculty TABLE

CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY IDENTITY(1,1),
    faculty_name VARCHAR(100) NOT NULL UNIQUE
);
GO


ALTER TABLE students
ADD faculty_id INT,
FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id);
GO


ALTER TABLE lecturers
ADD faculty_id INT,
FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id);
GO


INSERT INTO faculty (faculty_name) VALUES
('Faculty of Computing'),
('Faculty of Humanities & Sciences'),
('Faculty of Business'),
('Faculty of Engineering'),
('Faculty of Architecture');
GO

-----------------------------------------------------------------------------------------------------------------
CREATE TABLE modules (
    module_id INT PRIMARY KEY IDENTITY(1,1),
    module_code VARCHAR(20) NOT NULL UNIQUE,
    module_name VARCHAR(100) NOT NULL,
    lecturer_id INT, 
    faculty_id INT,  
    FOREIGN KEY (lecturer_id) REFERENCES users(user_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id)
);
GO

-- module_id (IDENTITY) 

INSERT INTO modules (module_code, module_name) VALUES
('CS1001', 'Introduction to Programming'),
('CS1002', 'Data Structures and Algorithms'),
('EE2010', 'Circuit Theory'),
('EE2020', 'Digital Electronics'),
('CS2005', 'Database Management Systems');
GO
-----------------------------------------------------------------------------------------------------------------

CREATE TABLE tickets (
    ticket_id INT PRIMARY KEY IDENTITY(1001,1), 
    student_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    category VARCHAR(20) NOT NULL CHECK (category IN ('IT_Support', 'Academic_Support')),
    module_id INT, 
    status VARCHAR(20) NOT NULL DEFAULT 'Unassigned',
    assigned_to INT, 
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    student_contact VARCHAR(10),
    FOREIGN KEY (student_id) REFERENCES users(user_id),
    FOREIGN KEY (module_id) REFERENCES modules(module_id),
    FOREIGN KEY (assigned_to) REFERENCES users(user_id)
);v
GO
-----------------------------------------------------------------------------------------------------------------
CREATE TABLE ticket_attachments (
    attachment_id INT PRIMARY KEY IDENTITY(1,1),
    ticket_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(100),
    uploaded_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE
);
GO
-----------------------------------------------------------------------------------------------------------------
CREATE TABLE ticket_responses (
    response_id INT PRIMARY KEY IDENTITY(1,1),
    ticket_id INT NOT NULL,
    responder_id INT NOT NULL,  
    response_message TEXT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE,
    FOREIGN KEY (responder_id) REFERENCES users(user_id)
);
GO

-----------------------------------------------------------------------------------------------------------------

-- Knowledge Base
CREATE TABLE knowledge_base_articles (

article_id INT PRIMARY KEY IDENTITY(1,1),
title NVARCHAR(255) NOT NULL,
content NVARCHAR(MAX) NOT NULL,
author_id INT NOT NULL,
category VARCHAR(50) NOT NULL,
created_at DATETIME DEFAULT GETDATE(),
updated_at DATETIME,
FOREIGN KEY (author_id) REFERENCES users(user_id)
);
GO


ALTER TABLE knowledge_base_articles
    ADD CONSTRAINT CK_kb_category CHECK (category IN ('IT_Support', 'Academic_Support', 'Counseling_Support', 'General_Info'));
GO

----------------------------------------------------------------------------------------------------------------
--      Notification System 


CREATE TABLE notifications (
    notification_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    message NVARCHAR(255) NOT NULL,
    link NVARCHAR(255),
    is_read BIT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO
----------------------------------------------------------------------------------------------------------------

SELECT * FROM students;
SELECT * FROM lecturers;
SELECT * FROM support_staff;
SELECT * FROM faculty;

SELECT * FROM modules;
SELECT * FROM tickets;
SELECT * FROM ticket_attachments;
SELECT * FROM ticket_responses;

SELECT * FROM knowledge_base_articles;
SELECT * FROM notifications;

SELECT * FROM users;

---------------------------------------------------------------------------------

----------------------------------------------------------------------------------------
SET IDENTITY_INSERT users ON;

INSERT INTO users 
(user_id, created_at, email, name, password_hash, role, university_id) 
VALUES 
(7, GETDATE(), 'testuser7@example.com', 'Test User 7', 'password123', 'STUDENT', 'U001');

SET IDENTITY_INSERT users OFF;

------------------------------------------------------------------------------------------
SET IDENTITY_INSERT users ON;

INSERT INTO users 
(user_id, name, email, university_id, password_hash, role, created_at) 
VALUES 
(1, 'Admin User', 'admin@uni.ac.lk', 'ADMIN001', 'adminpass', 'Admin', GETDATE());

SET IDENTITY_INSERT users OFF;
-----------------------------------------------------------------------------------------
SET IDENTITY_INSERT users ON;

INSERT INTO users 
(user_id, created_at, email, name, password_hash, role, university_id) 
VALUES 
(15, GETDATE(), 'testuser15@example.com', 'Test User 15', 'password123', 'STUDENT', 'U015');

SET IDENTITY_INSERT users OFF;


-----------------------------------------------------------------------------------
DELETE FROM ticket_attachments 
WHERE ticket_id IN (
    SELECT ticket_id FROM tickets WHERE student_id NOT IN (SELECT user_id FROM users)
);

DELETE FROM ticket_responses 
WHERE ticket_id IN (
    SELECT ticket_id FROM tickets WHERE student_id NOT IN (SELECT user_id FROM users)
);

DELETE FROM tickets 
WHERE student_id NOT IN (SELECT user_id FROM users);

DELETE FROM notifications 
WHERE user_id NOT IN (SELECT user_id FROM users);

DELETE FROM knowledge_base_articles
WHERE author_id NOT IN (SELECT user_id FROM users);