package benworks.comet.common.model;

import java.util.Comparator;

public class OrderComparator implements Comparator<Object> {

	private static final OrderComparator instance = new OrderComparator();

	public static OrderComparator getInstance() {
		return instance;
	}

	private OrderComparator() {

	}

	@Override
	public int compare(Object o1, Object o2) {
		int v1 = 10000, v2 = 10000;
		if (o1 != null && o1 instanceof Orderable)
			v1 = ((Orderable) o1).order();
		if (o2 != null && o2 instanceof Orderable)
			v2 = ((Orderable) o2).order();
		return v1 - v2;
	}

}
