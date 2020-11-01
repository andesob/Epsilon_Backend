/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  rojahno
 * Created: Aug 30, 2020
 */

DROP TABLE IF EXISTS AUSER CASCADE;
CREATE TABLE IF NOT EXISTS AUSER (
phoneNumber CHAR PRIMARY KEY,
username VARCHAR ( 50 ) UNIQUE NOT NULL,
password VARCHAR ( 50 ) NOT NULL,
email VARCHAR ( 255 ) UNIQUE NOT NULL
);

INSERT INTO AUSER (phoneNumber, username, password, email)
values('1', 'sander', 'passordet', 'sander@hotmail.com'); 

INSERT INTO AUSER (phoneNumber, username, password, email)
values('2', 'dunde', 'passordet2', 'dunde@hotmail.com'); 

INSERT INTO AUSER (phoneNumber, username, password, email)
values('3', 'anders', 'passordet3', 'anders@hotmail.com'); 
INSERT INTO AUSER (phoneNumber, username, password, email)
values('4', 'eskil', 'passordet4', 'eskil@hotmail.com'); 


select *
from AUSER;