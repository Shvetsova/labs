package fact;

public class Factorial implements Runnable {
	private int[] mas;

	public int calculate(int a) {
		int result = 1;
		for (int i = 1; i < a; i++) {
			result = i * result;
		}
		return result;
	}

	public void print(int a, int result) {
		System.out.println(a + "!" + "=" + result);
	}

	public void print(String message) {
		System.out.println(message);
	}

	public boolean check(int a) {
		if (a > 17 || a < 0) {
			return false;
		}
		return true;
	}

	public Factorial(int[] mas) {
		this.mas = mas;
	}

	@Override
	public void run() {
		for (int a : mas) {
			if (!check(a)) {
				print("Can't calculate this:" + a);
				continue;
			}
			int result = calculate(a);
			print(a, result);
		}

	}

}
