SOAP Server
===========
The SOAP Server module allows a Drupal site to access services using the same
callback code.

Soap Server for Services 7.x-3.x

WARNING:  This module is a prototype and is not secure.  DO NOT USE FOR PRODUCTION SITES

GETTING STARTED
===============

Enable the module
Add an endpoint at admin/structure/services/add choosing SOAP as the Server
Enable the node and user retrieve methods at admin/structure/services/{endpoint_name}/resources
View the generated WSDL at soap_server/debug_wsdl/{endpoint_name}
Give anonymous user 'access soap server' permission  - NOT FOR PRODUCTION USE
Use SOAPUI or ther test client to import the WSDL from http://example.com/{endpoint_path}?wsdl
or
Enable devel module
View a node object and user object debug client at soap_server/debug_client/{endpoint_name}/{nid}
The debug client will show:
ARGS
Endpoint object
Node object

Available client functions
The XML of the request
The XML of the response
The node object retrieved from the node.retrieve service from the supplied nid
The user object for user 1

NOTES
+++++

If an endpoint is configured to use one or more Hose XML endpoints then Hose XML will take over 
WSDL generation for the endpoint which may have unexpected results for Services (or other) resources. 
