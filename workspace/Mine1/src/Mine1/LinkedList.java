package Mine1;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class LinkedList<E> extends AbstractList<E> {

	private static class Node<E> {
		private E element;
		private Node<E> next;
		private Node<E> prev;

		public Node() {
			this(null, null, null);
		}

		public Node(E item, Node<E> next, Node<E> prev) {
			this.element = item;
			this.next = next;
			this.prev = prev;
		}
	}

	private class ListItr implements ListIterator<E> {
		private Node<E> lastReturned = header;
		private Node<E> next;
		private int nextIndex;

		ListItr(int index) {
			if (index < 0 || index > size) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
			}
			if (index < (size >> 1)) {
				next = header.next;
				for (nextIndex = 0; nextIndex < index; nextIndex++)
					next = next.next;
			} else {
				next = header;
				for (nextIndex = size; nextIndex > index; nextIndex--)
					next = next.prev;
			}
		}

		public boolean hasNext() {
			return nextIndex != size;
		}

		public E next() {
			if (nextIndex == size) throw new NoSuchElementException();
			lastReturned = next;
			next = next.next;
			nextIndex++;
			return lastReturned.element;
		}

		public boolean hasPrevious() {
			return nextIndex != 0;
		}

		public E previous() {
			if (nextIndex == 0) throw new NoSuchElementException();
			lastReturned = next = next.prev;
			nextIndex--;
			return lastReturned.element;
		}

		public int nextIndex() {
			return nextIndex;
		}

		public int previousIndex() {
			return nextIndex - 1;
		}

		public void remove() {
			Node<E> lastNext = lastReturned.next;
			try {
				LinkedList.this.remove(lastReturned);
			} catch (NoSuchElementException e) {
				throw new IllegalStateException();
			}
			if (next == lastReturned)
				next = lastNext;
			else
				nextIndex--;
			lastReturned = header;
		}

		public void set(E e) {
			if (lastReturned == header) throw new IllegalStateException();
			lastReturned.element = e;
		}

		public void add(E e) {
			lastReturned = header;
			addBefore(e, next);
			nextIndex++;
		}	
	}

	private Node<E> header = new Node<E>();
	private int size = 0;

	public LinkedList() {
		header.next = header.prev = header;
	}

	public void addLast(E e) {
		addBefore(e, header);
	}

	public void addFirst(E e) {
		addBefore(e, header.next);
	}

	public void addBefore(E e, Node<E> node) {
		Node<E> newNode = new Node<E>(e, node, node.prev);
		newNode.prev.next = newNode;
		newNode.next.prev = newNode;
		size++;
	}

	public E getLast() {
		if (isEmpty()) throw new NoSuchElementException();
		return header.prev.element;
	}

	public E getFirst() {
		if (isEmpty()) throw new NoSuchElementException();
		return header.next.element;
	}
	
	@Override
	public ListIterator<E> listIterator(int i) {
		return new ListItr(i);
	}

	public void removeLast() {
		remove(header.prev);
	}

	public void removeFirst() {
		remove(header.next);
	}

	private void remove(Node<E> node) {
		if (node == header || node == null) throw new NoSuchElementException();
		node.prev.next = node.next;
		node.next.prev = node.prev;
		size--;
	}

	// override method Get

	@Override
	public E get(int index) {
		if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
		Node<E> node = header;

		if (index < (size >> 1)) {
			for (int i = 0; i <= index; i++) {
				node = node.next;
			}
		} else {
			for (int i = size; i > index; i--) {
				node = node.prev;
			}
		}
		return node.element;

	}

	// return true if list is empty
	public boolean isEmpty() {
		return size == 0;
	}

	// return size of list
	@Override
	public int size() {
		return size;
	}

	// remove all elements
	public void clear() {
		header.next = header.prev = header;
		size = 0;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("");
		if (!isEmpty()) {
			Iterator<E> it = listIterator(0);
			while (it.hasNext()) {
				sb.append(it.next() + " ");
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.addFirst(158);
		list.addFirst(999);
		list.addFirst(777);
		list.addFirst(111);
		list.addLast(null);
		list.addLast(588);
		
		System.out.println(list);				
		System.out.println("Is empty: " + list.isEmpty());
		System.out.println("Remove the first element:" + list.getFirst());
		list.removeFirst();
		System.out.println("Remove the last element:" + list.getLast());
		list.removeLast();
		try {
			ListIterator<Integer> it = list.listIterator(0);			
			it.next();
			System.out.println("Remove this element:" + it.next());
			it.remove();
		} catch(Exception e) {
			System.err.print(e);
		}
		System.out.println("After removing:");
		System.out.println(list);
		System.out.println("After clear:");
		list.clear();
		System.out.println(list);
		System.out.println("Is empty: " + list.isEmpty());
	}
}
