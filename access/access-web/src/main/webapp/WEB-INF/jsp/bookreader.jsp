<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>BHL-Europe Book Reader</title>
    
    <link rel="stylesheet" type="text/css" href="../style/BookReader.css"/>
    <!-- Custom CSS overrides -->
    <link rel="stylesheet" type="text/css" href="../style/BHLEBookReader.css"/>

    <script type="text/javascript" src="../script/include/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="../script/include/jquery-ui-1.8.5.custom.min.js"></script>

    <script type="text/javascript" src="../script/include/dragscrollable.js"></script>
    <script type="text/javascript" src="../script/include/jquery.colorbox-min.js"></script>
    <script type="text/javascript" src="../script/include/jquery.bt.min.js"></script>

    <script type="text/javascript" src="../script/BookReader_modified.js"></script>
</head>
<body style="background-color: ##939598;">

<div id="BookReader">
    <noscript>
    <p>
        The BookReader requires JavaScript to be enabled. Please check that your browser supports JavaScript and that it is enabled in the browser settings.</a>.
    </p>
    </noscript>
</div>

<script type="text/javascript">
	var bookInfo = ${it};
</script>

<script type="text/javascript" src="../script/BHLEBookReader_patch.js"></script>
<script type="text/javascript" src="../script/BHLEBookReader.js"></script>

</body>
</html>
