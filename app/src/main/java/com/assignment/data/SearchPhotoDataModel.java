package com.assignment.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchPhotoDataModel {
	@SerializedName("stat")
	private String stat;

	@SerializedName("photos")
	private Photos photos;

	public void setStat(String stat){
		this.stat = stat;
	}

	public String getStat(){
		return stat;
	}

	public void setPhotos(Photos photos){
		this.photos = photos;
	}

	public Photos getPhotos(){
		return photos;
	}

	@Override
 	public String toString(){
		return 
			"SearchPhotoDataModel{" +
			"stat = '" + stat + '\'' + 
			",photos = '" + photos + '\'' + 
			"}";
		}

	public static class Photos{

		@SerializedName("perpage")
		private int perpage;

		@SerializedName("total")
		private String total;

		@SerializedName("pages")
		private int pages;

		@SerializedName("photo")
		private List<PhotoItem> photo;

		@SerializedName("page")
		private int page;

		public void setPerpage(int perpage){
			this.perpage = perpage;
		}

		public int getPerpage(){
			return perpage;
		}

		public void setTotal(String total){
			this.total = total;
		}

		public String getTotal(){
			return total;
		}

		public void setPages(int pages){
			this.pages = pages;
		}

		public int getPages(){
			return pages;
		}

		public void setPhoto(List<PhotoItem> photo){
			this.photo = photo;
		}

		public List<PhotoItem> getPhoto(){
			return photo;
		}

		public void setPage(int page){
			this.page = page;
		}

		public int getPage(){
			return page;
		}

		@Override
		public String toString(){
			return
					"Photos{" +
							"perpage = '" + perpage + '\'' +
							",total = '" + total + '\'' +
							",pages = '" + pages + '\'' +
							",photo = '" + photo + '\'' +
							",page = '" + page + '\'' +
							"}";
		}
	}

	public static class PhotoItem{

		@SerializedName("owner")
		private String owner;

		@SerializedName("server")
		private String server;

		@SerializedName("ispublic")
		private int ispublic;

		@SerializedName("isfriend")
		private int isfriend;

		@SerializedName("farm")
		private int farm;

		@SerializedName("id")
		private String id;

		@SerializedName("secret")
		private String secret;

		@SerializedName("title")
		private String title;

		@SerializedName("isfamily")
		private int isfamily;

		private String photoUrl;

		public void setOwner(String owner){
			this.owner = owner;
		}

		public String getOwner(){
			return owner;
		}

		public void setServer(String server){
			this.server = server;
		}

		public String getServer(){
			return server;
		}

		public void setIspublic(int ispublic){
			this.ispublic = ispublic;
		}

		public int getIspublic(){
			return ispublic;
		}

		public void setIsfriend(int isfriend){
			this.isfriend = isfriend;
		}

		public int getIsfriend(){
			return isfriend;
		}

		public void setFarm(int farm){
			this.farm = farm;
		}

		public int getFarm(){
			return farm;
		}

		public void setId(String id){
			this.id = id;
		}

		public String getId(){
			return id;
		}

		public void setSecret(String secret){
			this.secret = secret;
		}

		public String getSecret(){
			return secret;
		}

		public void setTitle(String title){
			this.title = title;
		}

		public String getTitle(){
			return title;
		}

		public void setIsfamily(int isfamily){
			this.isfamily = isfamily;
		}

		public int getIsfamily(){
			return isfamily;
		}

		public String getPhotoUrl() {
			return photoUrl;
		}

		public void setPhotoUrl(String photoUrl) {
			this.photoUrl = photoUrl;
		}

		@Override
		public String toString(){
			return
					"PhotoItem{" +
							"owner = '" + owner + '\'' +
							",server = '" + server + '\'' +
							",ispublic = '" + ispublic + '\'' +
							",isfriend = '" + isfriend + '\'' +
							",farm = '" + farm + '\'' +
							",id = '" + id + '\'' +
							",secret = '" + secret + '\'' +
							",title = '" + title + '\'' +
							",isfamily = '" + isfamily + '\'' +
							"}";
		}
	}

}