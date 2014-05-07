package main;

import java.io.*;
import java.util.List;

public class Main {

	public static void process(FileReader fileReader, FileWriter fileWriter,
			List<Integer> list) throws IOException, SyntaxException {

		BufferedReader in = new BufferedReader(fileReader);
		PrintWriter out = new PrintWriter(fileWriter);
		int counter = 0;

		while (in.ready()) {
			counter++;
			String command = in.readLine();
			String[] input = command.split(" ");
			out.println("Current command - " + command);
			if (input[0].equals("add")) {
				if (input.length < 2 || !isNumber(input[1])) {
					throw new SyntaxException(
							"FormatException on line number: " + counter);
				}
				out.println(list.add(Integer.parseInt(input[1])));
			} else if (input[0].equals("remove")) {
				if (input.length < 2 || !isNumber(input[1])) {
					throw new SyntaxException(
							"FormatException on line number: " + counter);
				}
				out.println(list.remove(Integer.parseInt(input[1])));
			} else if (input[0].equals("toString")) {
				out.println(list);
			}

		}
	}

	private static boolean isNumber(String string) {
		if (string == null || string.length() == 0)
			return false;

		int i = 0;
		if (string.charAt(0) == '-') {
			if (string.length() == 1) {
				return false;
			}
			i = 1;
		}

		char c;
		for (; i < string.length(); i++) {
			c = string.charAt(i);
			if (!(c >= '0' && c <= '9')) {
				return false;
			}
		}
		return true;

	}

	public static void main(String[] args) throws IOException {
		FileReader in = null;
		FileWriter out = null;
		List<Integer> list = new ArrayList<Integer>(10);

		try {
			in = new FileReader("in.txt");
			out = new FileWriter("out.txt");
			process(in, out, list);

		} catch (SyntaxException e) {

			out.write(e.getMessage());

		} catch (IOException e) {
			System.out.println("File not found");

		} finally {

			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
