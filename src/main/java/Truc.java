import java.util.concurrent.ConcurrentMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Truc {
	public static void main(String[] args) {
		DB db = DBMaker.memoryDB().make();
		ConcurrentMap map = db.hashMap("map").make();
		map.put("something", "here");
	}
}
