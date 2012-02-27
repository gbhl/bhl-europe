SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `int_pi_pi` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `int_pi_pi` ;

-- -----------------------------------------------------
-- Table `int_pi_pi`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`users` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`users` (
  `user_id` INT UNSIGNED NOT NULL ,
  `user_name` VARCHAR(45) NULL ,
  `user_pwd` VARCHAR(45) NULL ,
  `user_content_home` VARCHAR(255) NULL ,
  `user_content_id` VARCHAR(45) NULL ,
  `is_admin` TINYINT UNSIGNED ZEROFILL NULL DEFAULT 0 ,
  `user_config` TEXT NULL ,
  `user_config_smt` TEXT NULL ,
  `user_memo` TEXT NULL ,
  `queue_mode` TINYINT UNSIGNED NULL DEFAULT 0 ,
  `metadata_ws` VARCHAR(1024) NULL ,
  PRIMARY KEY (`user_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `int_pi_pi`.`ingest_log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`ingest_log` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`ingest_log` (
  `log_id` INT UNSIGNED NOT NULL ,
  `ingest_id` INT UNSIGNED NULL ,
  `log_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  `log_text` LONGTEXT NULL ,
  PRIMARY KEY (`log_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `int_pi_pi`.`ingest_structure`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`ingest_structure` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`ingest_structure` (
  `ingest_structure_id` INT UNSIGNED NOT NULL ,
  `ingest_id` INT UNSIGNED NULL ,
  `ingest_structure` MEDIUMTEXT NULL ,
  `ingest_structure_version` INT UNSIGNED NULL DEFAULT 1 ,
  `ingest_structure_status` ENUM('valid','invalid') NULL DEFAULT 'invalid' ,
  PRIMARY KEY (`ingest_structure_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `int_pi_pi`.`ingests`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`ingests` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`ingests` (
  `ingest_id` INT UNSIGNED NOT NULL ,
  `content_id` INT UNSIGNED NULL ,
  `user_id` INT UNSIGNED NULL ,
  `ingest_structure_id` INT UNSIGNED NULL ,
  `ingest_alias` VARCHAR(100) NULL ,
  `ingest_time` DATETIME NULL ,
  `ingest_status` ENUM('unknown','prepared','queued','success','failed') NULL DEFAULT 'prepared' ,
  `ingest_last_successful_step` TINYINT UNSIGNED NULL DEFAULT 0 ,
  `ingest_version` TINYINT UNSIGNED NULL DEFAULT 1 ,
  `ingest_do_ocr` TINYINT UNSIGNED NULL DEFAULT 1 ,
  `ingest_do_taxon` TINYINT UNSIGNED NULL DEFAULT 1 ,
  `ingest_do_sm` TINYINT UNSIGNED NULL DEFAULT 1 ,
  PRIMARY KEY (`ingest_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `int_pi_pi`.`content`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`content` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`content` (
  `content_id` INT UNSIGNED NOT NULL ,
  `content_root` VARCHAR(1024) NULL ,
  `content_name` VARCHAR(255) NULL ,
  `content_alias` VARCHAR(100) NULL ,
  `content_type` ENUM('unknown','book','journal','magazine','other') NULL DEFAULT 'book' ,
  `content_status` ENUM('not prepared','in preparation','ready for ingest','ingested') NULL DEFAULT 'not prepared' ,
  `content_ctime` DATETIME NULL ,
  `content_atime` DATETIME NULL ,
  `content_blob` BINARY NULL ,
  `content_size` BIGINT UNSIGNED NULL ,
  `content_pages` INT UNSIGNED NULL ,
  `content_last_succ_step` TINYINT UNSIGNED NULL DEFAULT 0 ,
  `content_olef` MEDIUMTEXT NULL ,
  `content_guid` VARCHAR(255) NULL ,
  `content_pages_text` LONGTEXT NULL ,
  `content_pages_tiff` LONGTEXT NULL ,
  `content_pages_taxon` LONGTEXT NULL ,
  `content_pages_formatinfo` LONGTEXT NULL ,
  `content_ipr` TEXT NULL ,
  PRIMARY KEY (`content_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `int_pi_pi`.`user_session`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`user_session` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`user_session` (
  `session_id` INT UNSIGNED NOT NULL ,
  `session_id_raw` VARCHAR(255) NULL ,
  `user_id` INT UNSIGNED NOT NULL ,
  `last_action` DATETIME NULL ,
  PRIMARY KEY (`session_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `int_pi_pi`.`content_guid`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`content_guid` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`content_guid` (
  `content_id` INT UNSIGNED NOT NULL ,
  `guid` VARCHAR(255) NOT NULL ,
  `released` DATETIME NOT NULL ,
  `last_action` DATETIME NULL ,
  PRIMARY KEY (`content_id`, `guid`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `int_pi_pi`.`page_object`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `int_pi_pi`.`page_object` ;

CREATE  TABLE IF NOT EXISTS `int_pi_pi`.`page_object` (
  `page_id` BIGINT UNSIGNED NOT NULL ,
  `content_id` INT UNSIGNED NULL ,
  `page_type` VARCHAR(100) NULL ,
  `realpages` MEDIUMINT UNSIGNED NULL ,
  `sequence` MEDIUMINT UNSIGNED NULL ,
  `section` VARCHAR(255) NULL ,
  `volume` VARCHAR(255) NULL ,
  `article` VARCHAR(255) NULL ,
  PRIMARY KEY (`page_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `int_pi_pi`.`users`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
USE `int_pi_pi`;
INSERT INTO `int_pi_pi`.`users` (`user_id`, `user_name`, `user_pwd`, `user_content_home`, `user_content_id`, `is_admin`, `user_config`, `user_config_smt`, `user_memo`, `queue_mode`, `metadata_ws`) VALUES ('1', 'mehrrath', '*D37C49F9CBEFBF8B6F4B165AC703AA271E079004', 'at-nhmw', 'nhmw', '1', NULL, NULL, NULL, '1', NULL);
INSERT INTO `int_pi_pi`.`users` (`user_id`, `user_name`, `user_pwd`, `user_content_home`, `user_content_id`, `is_admin`, `user_config`, `user_config_smt`, `user_memo`, `queue_mode`, `metadata_ws`) VALUES ('2', 'nhmw', '*D37C49F9CBEFBF8B6F4B165AC703AA271E079004', 'at-nhmw', 'nhmw', '1', NULL, NULL, NULL, '1', NULL);
INSERT INTO `int_pi_pi`.`users` (`user_id`, `user_name`, `user_pwd`, `user_content_home`, `user_content_id`, `is_admin`, `user_config`, `user_config_smt`, `user_memo`, `queue_mode`, `metadata_ws`) VALUES ('5', 'nbgb', '*D37C49F9CBEFBF8B6F4B165AC703AA271E079004', 'be-nbgb/Upload_20110930', 'nbgb', '1', NULL, '-m c -cm 4 -if <input_file> -of <output_file> -ife ISO-8859-15', 'The metadata looks fine, all plain MARC21 records which convert fine.', '1', NULL);
INSERT INTO `int_pi_pi`.`users` (`user_id`, `user_name`, `user_pwd`, `user_content_home`, `user_content_id`, `is_admin`, `user_config`, `user_config_smt`, `user_memo`, `queue_mode`, `metadata_ws`) VALUES ('4', 'admin', '*D37C49F9CBEFBF8B6F4B165AC703AA271E079004', 'testdata/spices_prepared', 'admin', '1', NULL, '-m c -cm 5 -if <input_file> -of <output_file>', 'The metadata looks fine, all plain MARC21 records which convert fine.', '0', NULL);
INSERT INTO `int_pi_pi`.`users` (`user_id`, `user_name`, `user_pwd`, `user_content_home`, `user_content_id`, `is_admin`, `user_config`, `user_config_smt`, `user_memo`, `queue_mode`, `metadata_ws`) VALUES ('3', 'test', '*D37C49F9CBEFBF8B6F4B165AC703AA271E079004', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

COMMIT;
