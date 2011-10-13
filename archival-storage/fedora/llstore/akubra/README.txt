BHL-Europe: Low Level Storage Strategy based on Akubra
 The strategy of low level storage depends on DataStream IDs, which is declared in ${FEDORA_HOME}/server/conf/akubra-llstore.xml.

How to compile
1.	Run "mvn build"

How to install
1.	Purge all objects in Fedora Repository, then shut down the server;
2.	Place akubra-mux-0.3.jar and bhle-llstore-0.0.1.jar in ${FEDORA_HOME}/tomcat/webapps/fedora/WEB-INF/lib;
3.	Replace akubra-llstore.xml with the one in the install package, and modify the store paths and DataStream IDs according to your needs;
4.  Restart the server.

How it works
A subclass of org.akubraproject.mux.AbstractMuxConnection overrides the getStore method to provide BlobStore according to the keywords of DataStream IDs in akubra-llstore.xml. And the filesystem storage is reused from akubra-fs (simple filesystem implementatio) and akubra-map (wraps an existing BlobStore to provide a blob id mapping layer) without any modification. Therefore, all the path mappings for objects and datastreams are still based on MD5 mapping.
