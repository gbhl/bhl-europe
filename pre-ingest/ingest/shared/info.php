<?php
// ***************************************
// ** FILE:    INFO.PHP                 **
// ** PURPOSE: COMMON                   **
// ** DATE:    12.05.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************

?>
<HTML>
<HEAD></HEAD>
<BODY bgcolor='white' text='black'>
<center>
<h2>OS Test Executions</h2>
<PRE>

<?php

echo "User:\n     ".`whoami`."\n";
echo "Path:\n     ".`pwd`."\n";
echo "Hostname:\n ".`hostname`."\n";
echo "UName:\n    ".`uname -a`."\n";
echo "Uptime:\n   ".`uptime`."\n";
echo "IFConfig:\n ".`ifconfig -a`."\n";

?>
</PRE><BR>>
<h2>PHP Information</h2>

<?php
// --------------------

@phpversion();

@phpinfo();

@zend_version();

@phpcredits();

// --------------------

?>

<h2>Extensions loaded</h2><pre>

<?php

print_r(get_loaded_extensions());


?>

</pre>

</CENTER>
</BODY>
</HTML>
