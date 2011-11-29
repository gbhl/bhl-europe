<?PHP
// **********************************************
// ** FILE:    GETMYVALS.PHP                   **
// ** PURPOSE: MYSQL RESULT ROW -> VARIABLEN   **
// ** DATE:    17.03.2005                      **
// ** AUTHOR:  ANDREAS MEHRRATH         	   **
// **********************************************

// Benoetigt:    1. guilty mySQL $result und current $line

// BEISPIEL:	$line = mysql_fetch_array($result = mysql_select($query));

	// ERGEBNIS ARRAY IN VARIABLEN SCHREIBEN
	for ($resultsetIndex=0;$resultsetIndex<mysql_num_fields($result);$resultsetIndex++)
		eval("\$".mysql_field_name($result,$resultsetIndex)."=\$line['".mysql_field_name($result,$resultsetIndex)."'];");


?>