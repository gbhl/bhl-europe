<?php
/**
 * Description of AutoDOMDocument
 * DOMDocument which has a handler function for looking up the correct namespace prefixes
 *
 * @author wkoller
 */
class AutoDOMDocument extends DOMDocument {
    /**
     * 
     * @param DOMElement $p_parentElement
     * @param type $p_namespace
     * @param type $p_tagName
     * @param type $p_tagContent
     * @return DOMElement 
     */
    public function appendChild( $p_parentElement, $p_namespace, $p_tagName, $p_tagContent = '' ) {
        $element = null;

        // check if namespace is defined for the parent
        $prefix = $p_parentElement->lookupPrefix($p_namespace);
        if( !empty($prefix) ) {
            $element = $this->createElement($prefix . ':' . $p_tagName);
        }
        // .. if not we have to create it using a new namespace prefix
        else {
            $element = $this->createElementNS($p_namespace, $p_tagName);
        }
        $element->nodeValue = $p_tagContent;
        
        $p_parentElement->appendChild($element);
        
        return $element;
    }
}
