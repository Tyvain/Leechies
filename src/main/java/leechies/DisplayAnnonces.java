package leechies;

public class DisplayAnnonces {
	public static void main(String[] args) {
		DBManager.getAllAnnonces().values().stream().forEach(s -> System.out.println(s));
	}
}
