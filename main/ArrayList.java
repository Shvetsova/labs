package main;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

public class ArrayList<E> extends AbstractList<E> {
	private class MyItr implements ListIterator<E> {
		int tail = size;
		int currentIndex = -1;

		@Override
		public boolean hasNext() {

			return currentIndex < tail - 1;
		}

		@Override
		public E next() {

			return array[++currentIndex];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasPrevious() {

			return currentIndex > 0;
		}

		@Override
		public E previous() {

			return array[--currentIndex];
		}

		@Override
		public int nextIndex() {
			return ++currentIndex;
		}

		@Override
		public int previousIndex() {
			return --currentIndex;
		}

	}

	private E[] array;
	private int size;

	@SuppressWarnings("unchecked")
	public ArrayList(int capacity) {
		array = (E[]) new Object[capacity];
		size = 0;
	}

	public boolean add(E value) {
		if (size == array.length)
			array = Arrays.copyOf(array, array.length * 2);
		array[size] = value;
		size++;

		return true;
	}

	@Override
	public E get(int index) {
		if (index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();
		return array[index];
	}

	public E remove(int index) {
		if (index >= size || index < 0)
			throw new ArrayIndexOutOfBoundsException();
		E c = array[index];
		for (int i = index; i < size - 1; i++) {
			array[i] = array[i + 1];
		}
		array[size - 1] = null;
		size--;

		return c;
	}

	public boolean remove(Object o) {
		for (int i = 0; i < size; i++) {
			if (o == null) {
				if (array[i] == null) {
					remove(i);
					return true;
				}
			} else {
				if (o.equals(array[i])) {
					remove(i);
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public ListIterator<E> listIterator() {
		return new MyItr();
	}

	@Override
	public int size() {
		return size;
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			array[i] = null;
		}
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (E value : this) {
			sb.append(value + " ");
		}
		if (size == 0) {
			return "";
		}
		int i = sb.lastIndexOf(" ");
		sb.deleteCharAt(i);
		return sb.toString();
	}

}
