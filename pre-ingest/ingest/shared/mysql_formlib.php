<?php

// NUR MYSQL FUNKTIONEN !




// *************
// *** F0003 ***
// *************
function drop_down($src_tab,$variable='',$wert='',$title='',$js_action='',$text_col=1,
$db = _DB_MAIN,$width=0,$unbekannt = true)
{

    if ($db=='') $db = _DB_MAIN;

    // OPTIONS AUS TABELLE ODER AUS WERTELISTE
    if (instr($src_tab,",")) 	$array_source = true;
    else 						$array_source = false;


    // TITEL
    if ($title != '')	echo "<font class=eingabetitel>".$title."</font>";

    if ( (!instr(strtolower($title),"<br>"))  and (!instr(strtolower($title),"<td")) )
    echo "&nbsp;";


    // FALLS KEINE VARIABLE DANN ZEIGE NUR AKTUELLEN WERT UEBERSETZT IN TEXTFELD AN
    if (($variable == '') and (!$array_source) )
    {
        $text = abfrage("select distinct bezeichnung from ".$src_tab." where id= ".$wert,$db);
        textfeld('',$text,strlen($text),'',$title,'readonly '.$js_action);
    }

    // VARIABLE GESETZT DANN RICHTIGE LISTE ERZEUGEN ...
    else
    {
        echo "<select name='".$variable."' class=eingabefeld ";

        // JAVA SCRIPT AKTION
        if (strlen($js_action)>2)	echo " ".$js_action." ";

        echo " style='z-index: 0; ";

        // BREITE IN PIXEL
        if (($width > 0) and ($width != ""))
        echo " width: ".$width."px; ";


        echo "'>\n";

        // STANDARDOPTIONEN
        if ($wert=="xxx") echo "\n<option value='xxx' selected></option>\n";

        if ($unbekannt)	echo "\n<option value='0'>unbekannt/keine</option>\n";


        if (instr(strtolower($src_tab),"where")) 	$where_clause = true;
        else 										$where_clause = false;

        if ((!$array_source) or ($where_clause))  // keine beistriche oder where clause vorhanden
        {

            $arrTables = array();


            // ***********************
            // MULTI TABLE DROP DOWN ?
            // ***********************
            if (!instr($src_tab,"|")) $arrTables[0] = $src_tab;
            else						 $arrTables    = explode("|", $src_tab);

            $anz_tabs = count($arrTables);

            for ($i=0;$i<$anz_tabs;$i++)
            {
                $src_tab = $arrTables[$i];

                // JEDE DROPDOWN LIST BESTEHT AUS 2 SPALTEN (INDEX,NAME), K�NNEN GLEICH SEIN)
                $src_tab = $src_tab." ";									// trick damit strpos jedenfalls was findet
                while ($src_tab[0]== " ") $src_tab=substr($src_tab,1,255);	// f�hrende nullen weg

                $tab_name = substr($src_tab,0,strpos($src_tab," "));

                $query = "desc ".$tab_name;

                $res = mysql_select($query,$db);

                $j = 0;
                while ($row = mysql_fetch_array($res))
                {
                    if ($j==0)			$col1 = $row[0];
                    if ($j==$text_col)	$col2 = $row[0];
                    $j += 1;
                }


                // OPTIONS ARRAY AUFBAUEN
                $query = "select distinct ".$col1.",".$col2." from ".$src_tab." ";


                // FALLS KEINE ORDER BY CLAUSE NACH TABELLENNAME IN APP_SET_TABLE DANN NACH col1 SORTIEREN
                $pos = strpos($src_tab, "order by");
                if ($pos === false)	$query .= " order by ".$col1;

                // js_alert($query);

                $res = mysql_select ($query,$db);

                // PLATZHALTER FALLS DIES NAECHSTE TABELLE IST
                if ((mysql_num_rows($res)>0) and ($i>0))
                echo "\n<option value=''>----------</option>\n";


                while ($line = mysql_fetch_array($res))
                {
                    echo "<option value='".$line[0]."' ";
                    if ($line[0] == $wert)	echo " selected ";
                    echo ">".urldecode($line[$col2])."</option>\n";
                }

            } // for i .. multi table

        } // tabellen source



        else // *** WERTELISTE ***
        {
            $strOptions = $src_tab;
            for($i=0;$i<10;$i++) // array von lz bei , saeubern
            {
                $strOptions = str_replace(" ,",",",$strOptions);
                $strOptions = str_replace(", ",",",$strOptions);
            }

            $arrOptions = explode(",",$strOptions); reset($arrOptions);
            $anzOptions = count($arrOptions); reset($arrOptions);

            for($i=0;$i<$anzOptions;$i++) // array von lz bei , saeubern
            {
                $option = $arrOptions[$i];
                echo "<option value='".$option."' ";
                if ($option == $wert)	echo " selected ";
                echo ">".$option."</option>\n";
            }

        }  // END WERTELISTE


        echo "</select>\n";

    }

}









// ***********************
// *** MULTIPLE SELECT ***
// ***********************
/*
mehrfachauswahl mit query um gespeicherte daten zu holen
und query um diese anzuzeigen
fuer m:n beziehungen gedacht

*/
function multiselect($query4data,$query2view,$arr_name,$title="",$js="",$size=3,$db1="",$db2="")
{
    global $user;

    if ($db1 == "") $db1 = $user;
    if ($db2 == "") $db2 = $user;
    if ($size== "") $size= 3;


    if ($title != '')	echo "<font class=eingabetitel>".$title."</font>";

    if ( (!instr(strtolower($title),"<br>"))  and (!instr(strtolower($title),"<td")) )
    echo "&nbsp;";


    echo "\n\n<select class=eingabefeld multiple name='".$arr_name."[]' size='".$size."' ".$js.">\n";

    $arr_data = build_array ($query4data,$db1); // die bereits selektierten

    $result   = mysql_select($query2view,$db2);

    // selektierte zuerst

    while ($line = mysql_fetch_row($result))
    {

        if (in_array($line[0],$arr_data))
        echo "<option value='".$line[0]."' selected >".$line[1]."</option>\n";
    }

    unset($line);
    mysql_data_seek ($result,0);


    // nach selektierten alle anderen

    while ($line = mysql_fetch_row($result))
    {

        if (!in_array($line[0],$arr_data))
        echo "<option value='".$line[0]."' >".$line[1]."</option>\n";

    }

    echo "</select>\n\n";

    unset($arr_data);
    mysql_free_result($result);

}







// ***************************
// *** SPEICHERT 1:N DATEN ***
// ***************************
// EXAMPLE:	multiselect_save("tablename","col1","col2",$firstvalue,$arr_secondvalues);
// *************************************************************************************
function multiselect_save($table,$col1,$col2,$firstvalue,$arr_secondvalues,$db=_DB_MAIN)
// *************************************************************************************
{
    $retval = 0;

    if ( (strlen($table)>2) and (strlen($col1)>2) and (strlen($col2)>2) and ($firstvalue > 0) )
    {

        $qry_delete = "delete from ".$table." where ".$col1."=".$firstvalue;
        $qry_insert = "insert into ".$table." (".$col1.",".$col2.") values ";

        $anz2insert = 0;
        $anz_values = count($arr_secondvalues);


        // **** 1. WERTE VORHANDEN ****
        if ($anz_values > 0)
        {

            for ($i=0;$i<$anz_values;$i++)
            {
                $val = $arr_secondvalues[$i];
                if($val != "")
                {
                    $qry_insert .= "(".$firstvalue.",".$val."),";
                    $anz2insert += 1;
                }
            }

            $qry_insert = substr($qry_insert,0,strlen($qry_insert)-1);

            // ALTE DATEN LOESCHEN
            mysql_select($qry_delete,$db);

            // NEUE DATEIN EINFUEGEN
            if ($anz2insert > 0)
            $retval = mysql_select($qry_insert,$db);
        }

        // *** 2. KEINE WERTE VORHANDEN ABER ORDENTLICH AUFGERUFEN - ALLES LOESCHEN ***
        else
        {
            $retval = mysql_select($qry_delete,$db);
        }

    }

    return $retval;

}


?>