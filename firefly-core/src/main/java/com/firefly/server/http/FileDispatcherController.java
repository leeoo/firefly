package com.firefly.server.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.mvc.web.DispatcherController;
import com.firefly.mvc.web.servlet.SystemHtmlPage;
import com.firefly.server.exception.HttpServerException;
import com.firefly.server.io.StaticFileOutputStream;
import com.firefly.utils.RandomUtils;
import com.firefly.utils.StringUtils;
import com.firefly.utils.VerifyUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class FileDispatcherController implements DispatcherController {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	public static final String CRLF = "\r\n";
	private static final String BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static String RANGE_ERROR_HTML = SystemHtmlPage
			.systemPageTemplate(
					416,
					"None of the range-specifier values in the Range request-header field overlap the current extent of the selected resource.");
	private Config config;

	public FileDispatcherController(Config config) {
		this.config = config;
	}

	@Override
	public void dispatcher(HttpServletRequest request,
			HttpServletResponse response) {
		String path = config.getFileAccessFilter().doFilter(request, response);
		if (VerifyUtils.isEmpty(path)) {
			SystemHtmlPage.responseSystemPage(request, response,
					config.getEncoding(), HttpServletResponse.SC_NOT_FOUND,
					request.getRequestURI() + " not found");
			return;
		}

		File file = new File(config.getServerHome(), path);
		if (!file.exists()) {
			SystemHtmlPage.responseSystemPage(request, response,
					config.getEncoding(), HttpServletResponse.SC_NOT_FOUND,
					request.getRequestURI() + " not found");
			return;
		}

		String fileSuffix = getFileSuffix(file.getName()).toLowerCase();
		String contentType = Constants.MIME.get(fileSuffix);
		if (contentType == null) {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ file.getName());
		} else {
			String[] type = StringUtils.split(contentType, '/');
			if ("application".equals(type[0])) {
				response.setHeader("Content-Disposition",
						"attachment; filename=" + file.getName());
			} else if ("text".equals(type[0])) {
				contentType += "; charset=" + config.getEncoding();
			}
			response.setContentType(contentType);
		}

		StaticFileOutputStream out = null;
		try {
			out = ((HttpServletResponseImpl) response)
					.getStaticFileOutputStream();
			long fileLen = file.length();
			String range = request.getHeader("Range");
			if (range == null) {
				out.write(file);
			} else {
				String[] rangesSpecifier = StringUtils.split(range, '=');
				if (rangesSpecifier.length != 2) {
					response.setStatus(416);
					out.write(RANGE_ERROR_HTML.getBytes(config.getEncoding()));
					return;
				}

				String byteRangeSet = rangesSpecifier[1].trim();
				String[] byteRangeSets = StringUtils.split(byteRangeSet, ',');
				if (byteRangeSets.length > 1) { // multipart/byteranges
					String boundary = "ff10" + getRandomString(13);
					if (byteRangeSets.length > config.getMaxRangeNum()) {
						log.error("multipart range more than {}",
								config.getMaxRangeNum());
						response.setStatus(416);
						out.write(RANGE_ERROR_HTML.getBytes(config
								.getEncoding()));
						return;
					}
					// multipart output
					List<MultipartByteranges> tmpByteRangeSets = new ArrayList<MultipartByteranges>(
							config.getMaxRangeNum());
					// long otherLen = 0;
					for (String t : byteRangeSets) {
						String tmp = t.trim();
						String[] byteRange = StringUtils.split(tmp, '-');
						if (byteRange.length == 1) {
							long pos = Long.parseLong(byteRange[0].trim());
							if (pos == 0)
								continue;
							if (tmp.charAt(0) == '-') {
								long lastBytePos = fileLen - 1;
								long firstBytePos = lastBytePos - pos + 1;
								if (firstBytePos > lastBytePos)
									continue;

								MultipartByteranges multipartByteranges = getMultipartByteranges(
										contentType, firstBytePos, lastBytePos,
										fileLen, boundary);
								tmpByteRangeSets.add(multipartByteranges);
							} else if (tmp.charAt(tmp.length() - 1) == '-') {
								long firstBytePos = pos;
								long lastBytePos = fileLen - 1;
								if (firstBytePos > lastBytePos)
									continue;

								MultipartByteranges multipartByteranges = getMultipartByteranges(
										contentType, firstBytePos, lastBytePos,
										fileLen, boundary);
								tmpByteRangeSets.add(multipartByteranges);
							}
						} else {
							long firstBytePos = Long.parseLong(byteRange[0]
									.trim());
							long lastBytePos = Long.parseLong(byteRange[1]
									.trim());
							if (firstBytePos > fileLen
									|| firstBytePos >= lastBytePos)
								continue;

							MultipartByteranges multipartByteranges = getMultipartByteranges(
									contentType, firstBytePos, lastBytePos,
									fileLen, boundary);
							tmpByteRangeSets.add(multipartByteranges);
						}
					}

					if (tmpByteRangeSets.size() > 0) {
						response.setStatus(206);
						response.setHeader("Accept-Ranges", "bytes");
						response.setHeader("Content-Type",
								"multipart/byteranges; boundary=" + boundary);

						for (MultipartByteranges m : tmpByteRangeSets) {
							long length = m.lastBytePos - m.firstBytePos + 1;
							out.write(m.head.getBytes(config.getEncoding()));
							out.write(file, m.firstBytePos, length);
						}

						out.write((CRLF + "--" + boundary + "--" + CRLF)
								.getBytes(config.getEncoding()));
						log.debug("multipart download|{}", range);
					} else {
						response.setStatus(416);
						out.write(RANGE_ERROR_HTML.getBytes(config
								.getEncoding()));
						return;
					}
				} else {
					String tmp = byteRangeSets[0].trim();
					String[] byteRange = StringUtils.split(tmp, '-');
					if (byteRange.length == 1) {
						long pos = Long.parseLong(byteRange[0].trim());
						if (pos == 0) {
							response.setStatus(416);
							out.write(RANGE_ERROR_HTML.getBytes(config
									.getEncoding()));
							return;
						}

						if (tmp.charAt(0) == '-') {
							long lastBytePos = fileLen - 1;
							long firstBytePos = lastBytePos - pos + 1;
							writePartialFile(request, response, out, file,
									firstBytePos, lastBytePos, fileLen);
						} else if (tmp.charAt(tmp.length() - 1) == '-') {
							writePartialFile(request, response, out, file, pos,
									fileLen - 1, fileLen);
						} else {
							response.setStatus(416);
							out.write(RANGE_ERROR_HTML.getBytes(config
									.getEncoding()));
							return;
						}
					} else {
						long firstBytePos = Long.parseLong(byteRange[0].trim());
						long lastBytePos = Long.parseLong(byteRange[1].trim());
						if (firstBytePos > fileLen
								|| firstBytePos >= lastBytePos) {
							response.setStatus(416);
							out.write(RANGE_ERROR_HTML.getBytes(config
									.getEncoding()));
							return;
						}

						if (lastBytePos >= fileLen)
							lastBytePos = fileLen - 1;

						writePartialFile(request, response, out, file,
								firstBytePos, lastBytePos, fileLen);
					}
					log.debug("single range download|{}", range);
				}
			}
		} catch (Throwable e) {
			throw new HttpServerException("get static file output stream error");
		} finally {
			if (out != null)
				try {
					// System.out.println("close~~");
					out.close();
				} catch (IOException e) {
					throw new HttpServerException(
							"static file output stream close error");
				}
		}
	}

	private void writePartialFile(HttpServletRequest request,
			HttpServletResponse response, StaticFileOutputStream out,
			File file, long firstBytePos, long lastBytePos, long fileLen)
			throws Throwable {

		long length = lastBytePos - firstBytePos + 1;
		if (length <= 0) {
			response.setStatus(416);
			out.write(RANGE_ERROR_HTML.getBytes(config.getEncoding()));
			return;
		}
		response.setStatus(206);
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Content-Range", "bytes " + firstBytePos + "-"
				+ lastBytePos + "/" + fileLen);
		out.write(file, firstBytePos, length);
	}

	public static String getFileSuffix(String name) {
		if (name.charAt(name.length() - 1) == '.')
			return "*";

		for (int i = name.length() - 2; i >= 0; i--) {
			if (name.charAt(i) == '.') {
				return name.substring(i + 1, name.length());
			}
		}
		return "*";
	}

	private class MultipartByteranges {
		public String head;
		public long firstBytePos, lastBytePos;
	}

	private MultipartByteranges getMultipartByteranges(String contentType,
			long firstBytePos, long lastBytePos, long fileLen, String boundary) {
		MultipartByteranges ret = new MultipartByteranges();
		ret.firstBytePos = firstBytePos;
		ret.lastBytePos = lastBytePos;
		ret.head = CRLF + "--" + boundary + CRLF + "Content-Type: "
				+ contentType + CRLF + "Content-range: bytes " + firstBytePos
				+ "-" + lastBytePos + "/" + fileLen + CRLF + CRLF;
		return ret;
	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = (int) RandomUtils.random(0, BASE.length() - 1);
			sb.append(BASE.charAt(number));
		}
		return sb.toString();
	}

}
