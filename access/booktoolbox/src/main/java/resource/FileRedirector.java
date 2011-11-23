package resource;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import util.FilePathCoder;
import util.Format;


@Path("file")
public class FileRedirector {
	@GET
	@Path("{decodedPath}")
	public Response retriveFile(@PathParam("decodedPath") String decodedPath) {
		String path = FilePathCoder.decode(decodedPath);
		File file = new File(path);
		if (!file.exists()) {
			return Response.serverError().build();
		}

		String suffix = path.substring(path.lastIndexOf(".") + 1);
		return Response.ok(file).type(Format.getFormat(suffix).getMIME())
				.build();
	}
}
