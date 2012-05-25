<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Access - BHL-Europe</title>
</head>
<body>
	<h1>List of Entries</h1>

		<h2>Items</h2>
		<li>
			List of items/derivatives: 
			<a href="./items">./items</a>
		</li>
		<li>
			List datastreams: ./items/<input /><button>Go</button>
		</li>
		<li>List datastreams: ./items/<input />/<input /><button>Go</button> or ./items/{guid}/{datastream_id}/{serialnumber}</li>

		<h2>BookReader</h2>
		<li>
			Open Book Reader: ./stream/<input /><button>Go</button>
		</li>

		<h2>Download</h2>
			<li>PDF: ./download/{guid}/pdf?ranges={ranges}&resolution={resolution}</li>
			<li>JPEG (in zip): ./download/{guid}/jpg?ranges={ranges}&resolution={resolution}</li>
			<li>
				<i>Parameters: </i>
				<li>
					ranges: <i>(select all pages as default if this parameter is left empty)</i>
					<ol>n{pageIndex} e.g. n2</ol>
					<ol>n{pageIndex}-n{pageIndex} e.g. n6-n56</ol>
					<ol>n{pageName} e.g. title</ol>
					<ol>n{pageName}-n{pageName} e.g. cover-9</ol>
				</li>
				<li>
					resolution:
					<ol>high</ol>
					<ol>medium (default)</ol>
					<ol>low</ol>
				</li>
			</li>
		</li>
		<h2><a href="batch-admin/home">Batch-Admin</a></h2>
	</ul>
</body>
</html>