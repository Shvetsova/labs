package fact;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("input.txt"));

			while (in.ready()) {
				String[] data = in.readLine().split(" ");
				int sizeOfFirstMas = data.length / 2;
				int sizeOfSecondMas = data.length - sizeOfFirstMas;

				int[] firstMas = new int[sizeOfFirstMas];
				int[] secondMas = new int[sizeOfSecondMas];

				for (int i = 0; i < sizeOfFirstMas; i++) {
					firstMas[i] = Integer.parseInt(data[i]);
				}
				for (int i = sizeOfFirstMas, j = 0; i < data.length; i++, j++) {
					secondMas[j] = Integer.parseInt(data[i]);
				}
				(new Thread(new Factorial(firstMas))).start();
				(new Thread(new Factorial(secondMas))).start();

			}

		} catch (NumberFormatException e) {
			System.err.println("File should contain only integer values");
		} catch (FileNotFoundException e) {

			System.err.println("Can't find input file");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {

			}

		}

	}

}
