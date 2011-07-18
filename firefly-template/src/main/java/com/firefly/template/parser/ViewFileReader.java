package com.firefly.template.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import com.firefly.template.Config;

public class ViewFileReader {
	private Config config;
	private boolean init = false;

	public void readAndBuild() {
		if (!init)
			read0(new File(config.getViewPath()));
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	private void read0(File file) {
		file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					read0(f);
				} else if (f.getName().endsWith("." + config.getSuffix())) {
					Node page = new PageNode();
					Node currentNode = page;
					Deque<Node> stack = new LinkedList<Node>();
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(f));
						for (String line = null; (line = reader.readLine()) != null;) {
							// TODO 文件分析
							System.out.println(line);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (reader != null)
							try {
								reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
				}
				return false;
			}
		});
		init = true;
	}
}
