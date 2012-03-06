-- -----------------------------------
-- UPGRADE DATABASE TO CURRENT VERSION
-- -----------------------------------
ALTER TABLE `int_pi_pi`.`content` CHANGE COLUMN `content_type` `content_type` 
ENUM('unknown','monograph','serial','other') NULL DEFAULT 'monograph'  ;

update  int_pi_pi.content set content_type='monograph' where content_type is null or content_type='';

