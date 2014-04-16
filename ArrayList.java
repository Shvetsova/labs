package main;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayList extends AbstractList<Integer> {
	private class MyItr implements Iterator<Integer>{
		int tail = size;
		int currentIndex = -1;
		
		
		@Override
		public boolean hasNext() {
			
			return currentIndex < tail - 1;
		}

		@Override
		public Integer next() {
			
			return array[++currentIndex];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	Integer[] array;
	int size;
	

    private ArrayList(int capacity) {
		array = new Integer[capacity];
		size = 0;
	}

	public boolean add(Integer value) {
		if (size == array.length)
			array = Arrays.copyOf(array, array.length * 2);
		array[size] = value;
		size++;

		return true;
	}

	@Override
	public Integer get(int index) {
		if (index >= size || index < 0) throw new ArrayIndexOutOfBoundsException();
		return array[index];
	}

	

	public Integer remove(int index) {
		if (index >= size || index < 0) throw new ArrayIndexOutOfBoundsException();
		Integer deletedElement = array[index];
		
		for (int i = index; i < size - 1; i++){
			array[i]=array[i+1];			
		}
		array[size-1]=null;
		size--;
		return deletedElement;

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
	public Iterator<Integer> iterator(){
		return new MyItr();	
	}
	
	@Override
	public int size() {
		return size;
	}
	
	public void clear(){
		for ( int i = 0; i < size; i++){
			array[i] = null;
		}
		size = 0;
	}
	
	public boolean isEmpty(){
		return size == 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Integer value : this) {
			sb.append(value + " ");
		}
		int i = sb.lastIndexOf(" ");
		sb.deleteCharAt(i);
		return sb.toString(); 
	}
	public static void main(String[] args) {
		ArrayList list = new ArrayList(10);
		list.add(158);
		list.add(999);
		list.add(777);
		list.add(111);
		list.add(null);
		list.add(588);

		System.out.println(list);
		System.out.println("Is empty: " + list.isEmpty());
		Iterator<Integer> it = list.iterator();
		it.next();
		System.out.println("Remove this element:" + it.next());
		it.remove();
		System.out.println("Remove this element:" + list.remove(2));
		System.out.println("Remove this element:" + null);
		list.remove(null);
		System.out.println("After removing:");
		System.out.println(list);
		System.out.println("After clear:");
		list.clear();
		System.out.println(list);
		System.out.println("Is empty: " + list.isEmpty());
	}
}
