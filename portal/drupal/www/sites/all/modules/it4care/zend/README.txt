Zend 1.x for Drupal 7.x
--------------------------
The Zend module provides the complete Zend Framework for Drupal modules to use.
This module is a developer module providing no end user facing functionality
or configuration. Once this module is installed other modules can use any
portion of the Zend Framework starting from hook_init().

For more information on the Zend Framework please see http://framework.zend.com


Installation
------------
There are two steps to installing the Zend module.

First, Install the module.
  - Download the Zend module and place it in your modules directory.
  - Enable the module.

Once the module is installed you can go to the Status Report page at (Administer
> Reports > Status Report) and see the status and version of the Zend Framework.

If the Zend Framework is not installed there are three ways to install it on your
system.

1. Add it to the PHP include path manually or using a tool such as Pear. The
   Pear channel providing the Zend Framework is zend.googlecode.com/svn.
   
2. Using the Libraries API module (http://drupal.org/project/libraries). First,
   install the Libraries API and then download the Zend Framework to a libraries
   directory.
   
3. There is a variable named zend_path. This variable can be set to the root
   of the Zend Library. This vaiable has no UI to set it.
   
Once the Zend Framework is installed it will be reported on the Status Report
page and available to all modules to take advantage of.

Maintainers
-----------
- Matt Farina (mfer) - http://drupal.org/user/25701
- Rob Loach - http://drupal.org/user/61114
- mustafa ulu (mustafau) - http://drupal.org/user/207559