package com.wallissoftware.chessanarchy.server.messages;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class MessageCache {

	@Id private Long id;
	private String json;
	private Long previousId;
	@Index private long creationTime;

	@SuppressWarnings("unused")
	private MessageCache() {
	};

	public MessageCache(final Long previousId, final String json) {
		this.creationTime = System.currentTimeMillis();
		this.previousId = previousId;
		this.json = json;
	}

	public String getJson() {
		return "{\"id\":\"" + id + "\"," + json.substring(1);
	}

	public Long getPreviousId() {
		return previousId;
	}

	public Long getId() {
		return id;
	}

}
