CREATE TABLE IF NOT EXISTS `ingest_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content_id` int(11) NOT NULL DEFAULT '0',
  `guid` text,
  `sip_path` text,
  `ingest_status` enum('queued','running','finished','error') NOT NULL DEFAULT 'queued',
  PRIMARY KEY (`id`),
  KEY `ingest_status` (`ingest_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;
