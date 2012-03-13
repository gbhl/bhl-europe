# roman-arabic.awk
#
# converts 
# 
# Adapted from a clever converter found here
# http://scripts.mit.edu/~yfarjoun/homepage/index.php?title=Code_Snippets
# and secondly 
# from http://www.unix.com/shell-programming-scripting/156805-sort-roman-numerals.html

BEGIN	{
		R["I"]=1;	R["V"]=5;	R["X"]=10;	R["L"]=50;
		R["C"]=100;	R["D"]=500;	R["M"]=1000;

		E["iv"]="IIII";		E["ix"]="VIIII";
		E["xl"]="XXXX";		E["xc"]="LXXXX";
		E["cd"]="CCCC";		E["cm"]="DCCCC";
		E["iix"]="VIII";	E["xxc"]="LXXX";
		E["ccm"]="DCCC";	E["vl"]="XXXXV";
		E["ld"]="CCCCL";
	}

	function roman_arabic(RN)
	{
		SUM=0;
		RN=tolower(RN);

		# Substitute roman numeral forms into things we can count.
		# Substitue lower case for upper case so substitutions
		# don't happen twice by accident.
		for(K in E)	while(sub(K, E[K], RN));

		# Convert anything that didn't get substituted to uppercase.
		RN=toupper(RN);

		for(K in R) while(sub(K, "", RN)) SUM+=R[K];

		return(SUM);
	}

	{

		print roman_arabic($0);
	}