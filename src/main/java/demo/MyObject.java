package demo;

public class MyObject {
	private String id;
	private String data;
	
	public MyObject (String id, String data) {
		this.id = id;
		this.data = data;
	}

	@Override
	public String toString() {
		return "MyObject [id=" + id + ", data=" + data + "]";
	}	
}
