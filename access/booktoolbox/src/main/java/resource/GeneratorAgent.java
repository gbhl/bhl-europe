package resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import exception.UnsupportedCModelException;
import generator.Generator;
import generator.GeneratorFactory;
import generator.GeneratorThreadPool;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import consumer.OfflineConsumer;

import util.ContentModel;
import util.EmailSender;
import util.FedoraObjectService;
import util.FileNameExtractor;
import util.Format;
import util.GlobalParameter;
import util.OfflineEmailHtml;
import util.Product;
import util.Resolution;
import util.ResourceIndex;

@Path("/")
public class GeneratorAgent extends FedoraObjectService {
	GeneratorThreadPool threadPoolService = new GeneratorThreadPool(
			GlobalParameter.MAX_THREAD);

	@GET
	@Path("{pid}")
	public Response dispatchRequest(
			@PathParam("pid") String pid,
			@QueryParam("format") String format,
			@DefaultValue("medium") @QueryParam("resolution") Resolution resolution,
			@DefaultValue("") @QueryParam("ranges") String ranges,
			@DefaultValue("en") @QueryParam("lang") Locale lang)
			throws UnsupportedCModelException {

		List<String> pidList = generatePidList(pid, ranges);

		Generator generator = GeneratorFactory.getGenerator(
				Format.getFormat(format), pidList, resolution);

		if (!canGenerateNow(pidList)) {
			OfflineEmailHtml html = new OfflineEmailHtml(lang);
			return html.askEmail(format, resolution, pidList);
		} else {
			Product product = Product.newRealtimeProduct(pid, lang);
			threadPoolService.execute(generator);
			try {
				product = threadPoolService.waitAndGetProduct();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return packProduct(product);
		}
	}

	private boolean canGenerateNow(List<String> pidList) {
		return !threadPoolService.isFull() && pidList.size() < 30;
	}

	private Response packProduct(Product product) {
		if (product != null) {
			return Response.ok(product.getOutputStream())
					.type(product.getFormat().getMIME()).build();
		} else {
			return Response.serverError().build();
		}
	}

	private List<String> generatePidList(String pid, String ranges)
			throws UnsupportedCModelException {
		List<String> modelList = ResourceIndex.getModels(pid);
		List<String> pidList = null;
		if (modelList.contains(ContentModel.BOOK_CMODEL.getPID())) {
			pidList = pidExtracoteor.getPIDs(pid, ranges);
		} else if (modelList.contains(ContentModel.PAGE_CMODEL.getPID())) {
			pidList = new ArrayList<String>();
			pidList.add(pid);
		} else {
			throw new UnsupportedCModelException();
		}
		return pidList;
	}

	@POST
	@Path("offline")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response offlineGeneration(@FormParam("email") String email,
			@FormParam("format") String format,
			@FormParam("resolution") Resolution resolution,
			@FormParam("ranges") String ranges,
			@DefaultValue("en") @FormParam("lang") Locale lang) {

		List<String> pids = Arrays.asList(ranges.split(","));
		String totalName = FileNameExtractor.abstractName(pids);
		System.out.println("Generating file: " + totalName);

		Product offlineProduct = Product.newOfflineProduct(totalName, email, lang);

		Generator generator = GeneratorFactory.getGenerator(
				Format.getFormat(format), pids, resolution);
		generator.setProduct(offlineProduct);
		generator.addConsumerListener(new OfflineConsumer());

		threadPoolService.execute(generator);

		return new OfflineEmailHtml(lang).responseRequest();
	}

}
