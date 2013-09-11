package ch.ethz.iamscience;

public class ScienceApp {

	private String id;
	private String name;
	private int drawable;

	public ScienceApp(String id, String name, int drawable) {
		this.id = id;
		this.name = name;
		this.drawable = drawable;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getDrawable() {
		return drawable;
	}

}
