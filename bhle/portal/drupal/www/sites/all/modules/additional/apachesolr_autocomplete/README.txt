
Apache Solr Autocomplete module for Drupal.

-- SUMMARY --

Add-on module to Apache Solr Search Integration that adds simple autocomplete
functionality. It enforces node access, meaning that all suggestions are only
from nodes that the user actually has access to.

For a full description of the module, visit the project page:
  http://drupal.org/project/apachesolr_autocomplete

To submit bug reports and feature suggestions, or to track changes:
  http://drupal.org/project/issues/apachesolr_autocomplete


-- REQUIREMENTS --

Apache Solr Search Integration module.
See http://drupal.org/project/apachesolr


-- INSTALLATION --

* Install as usual, see http://drupal.org/node/70151 for further information.


-- CONFIGURATION  --

For configuration, go to:

  Administration >> Settings >> Apache Solr

and look for the "Advanced Options" fieldset. The setting is:

  "Autocomplete widget to use:"

where you can choose between a custom Javascript widget (included with the
module) or fall back to the core Drupal autocomplete widget. The default is to
use the custom widget.

-- TROUBLESHOOTING --

If you are having trouble with the autocomplete suggestions not working correctly,
try changing the configuration to use the core Drupal autocomplete widget.

If you encounter other problems, please post to the project issue queue:
  http://drupal.org/project/issues/apachesolr_autocomplete

--