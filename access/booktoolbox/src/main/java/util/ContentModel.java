package util;

public enum ContentModel {
	BOOK_CMODEL(GlobalParameter.PID_BOOK_CMODEL), PAGE_CMODEL(GlobalParameter.PID_PAGE_CMODEL);
	
	private String pid;
	private ContentModel(String pid) {
		this.pid = pid;
	}
	
	public String getPID(){
		return pid;
	}
}
