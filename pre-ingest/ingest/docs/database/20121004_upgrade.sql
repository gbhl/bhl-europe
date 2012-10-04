ALTER TABLE `ingest_queue`  ADD `item_count` INT NOT NULL DEFAULT '0',  ADD `items_done` INT NOT NULL DEFAULT '0';

ALTER TABLE `ingest_queue` CHANGE `ingest_status` `ingest_status` ENUM('queued','waiting','running','finished','error') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'queued';
