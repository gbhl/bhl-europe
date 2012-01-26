<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>BHL-Europe</title>
</head>
<body>
	<form action="/access/offline" method="post">
		<input type="hidden" name="guid" value="${it.guid}"/>
		<input type="hidden" name="format" value="${it.format}" />
		<input type="hidden" name="ranges" value="${it.ranges}" />
		<input type="hidden" name="resolution" value="${it.resolution}" />
		<p>
			<input type="text" name="email" />
		</p>
		<input type="submit" />
	</form>
</body>
</html>
