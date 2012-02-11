package com.firefly.server.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.mvc.web.DispatcherController;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class FileDispatcherController implements DispatcherController {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private Config config;
	
	public FileDispatcherController(Config config) {
		this.config = config;
	}



	@Override
	public void dispatcher(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub

	}

}
