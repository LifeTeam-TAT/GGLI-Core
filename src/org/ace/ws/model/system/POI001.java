package org.ace.ws.model.system;

//PhotoImage
public class POI001 {
	private String id;
	private String name;
	private String photo;
	private int order;
	private int version;

	public POI001() {
	}

	public POI001(String id, String name, String photo, int order, int version) {
		this.id = id;
		this.name = name;
		this.photo = photo;
		this.order = order;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
