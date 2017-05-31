package com.viki.crawlConfig.crawl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;

public class ErrorNote {

	private Entry<String, HashSet<String>> entry;

	private String entraceUrl;

	private String note;

	public ErrorNote(Entry<String, HashSet<String>> entry,String entrance_Url,String note){
		this.entry = entry;
		this.entraceUrl = entrance_Url;
		this.note = note;
	}

	public void write(){
		File f = new File("E://personalProject//PostNewNoter//"+this.entraceUrl.replace(".", "").replace("/", "").replace("\\", "").replace(":", "").replace("{", "").replace("}", "")+".txt");
		try {
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.err.println("创建新文档"+this.entraceUrl);
		try {
			FileWriter fileWriter = new FileWriter(f);
			fileWriter.write("Note:"+note);
			fileWriter.write("entraceUrl:"+entraceUrl+"\n");
			fileWriter.write("RegExp:"+entry.getKey()+"\n");
			for(String line : entry.getValue()){
				fileWriter.write(line+"\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
