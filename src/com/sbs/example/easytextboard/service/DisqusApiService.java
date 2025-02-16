package com.sbs.example.easytextboard.service;

import java.util.HashMap;
import java.util.Map;

import com.sbs.example.easytextboard.apidto.DisqusApiDataListThread;
import com.sbs.example.easytextboard.container.Container;
import com.sbs.example.easytextboard.dto.Article;
import com.sbs.example.easytextboard.util.Util;

public class DisqusApiService {
	
	public Map<String, Object> getArticleData(Article article) {
		
		String fileName = Container.buildService.getArticleDetailFileName(article);
		String url = "https://disqus.com/api/3.0/forums/listThreads.json";
		DisqusApiDataListThread disqusApiDataListThread = (DisqusApiDataListThread) Util.callApiResponseTo(
				DisqusApiDataListThread.class, url, "api_key=" + Container.config.getDisqusApiKey(),
				"forum=" + Container.config.getDisqusForumName(), "thread:ident=" + fileName);

		if (disqusApiDataListThread == null) {
			return null;
		}

		Map<String, Object> rs = new HashMap<>();
		rs.put("likesCount", disqusApiDataListThread.response.get(0).likes);
		rs.put("commentsCount", disqusApiDataListThread.response.get(0).posts);

		return rs;
	}
}
