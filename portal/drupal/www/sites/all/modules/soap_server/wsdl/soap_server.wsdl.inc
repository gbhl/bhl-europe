<?php
/**
 * @file
 * This file is a template for WSDL construction by soap_server.
 */

$wsdl_content = "<?xml version='1.0' encoding='UTF-8' ?>
<definitions name='soap_server'
  targetNamespace='urn:soap_server'
  xmlns:tns='urn:soap_server'
  xmlns:soap='http://schemas.xmlsoap.org/wsdl/soap/'
  xmlns:xsd='http://www.w3.org/2001/XMLSchema'
  xmlns:soapenc='http://schemas.xmlsoap.org/soap/encoding/'
  xmlns='http://schemas.xmlsoap.org/wsdl/'>
  
  <types>
  	<xsd:schema targetNamespace='urn:soap_server'
      xmlns:xsd='http://www.w3.org/2001/XMLSchema' />
  </types>
  
$requests

$responses

  <portType name='soap_server_port_type'>
$port_type_operations
  </portType>

  <binding name='soap_server_binding' type='tns:soap_server_port_type'>
    <soap:binding style='rpc' transport='http://schemas.xmlsoap.org/soap/http' />
$binding_operations
  </binding>

  <service name='soap_server_service'>
    <port name='soap_server_port' binding='tns:soap_server_binding'>
      <soap:address location='$service_endpoint'/>
    </port>
  </service>
</definitions>";
