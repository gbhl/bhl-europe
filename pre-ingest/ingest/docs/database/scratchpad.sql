
select * from users

select now() from dual

update users set user_pwd=password('letmein')  // '*D37C49F9CBEFBF8B6F4B165AC703AA271E079004'
select * from users

select password('letmein')

select * from users where user_pwd=password('admin')

delete  from user_session
commit
select * from user_session

EDIT `int_pi_pi`.`users`;

delete from user_session where 
user_id not in (select u.user_id from users u where 1=1) 
or last_action < 1321630327 

use int_pi_pi
