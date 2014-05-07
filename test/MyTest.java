package test;

import static org.junit.Assert.*;

import java.util.ListIterator;

import main.ArrayList;

import org.junit.Test;

public class MyTest {
	@Test
	public void testSize() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		assertEquals(0, list.size());
		list.add(1);
		list.add(2);
		assertEquals(2, list.size());
		list.remove(0);
		assertEquals(1, list.size());
	}

	@Test
	public void testIsEmpty() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		assertTrue(list.isEmpty());
		list.add(1);
		list.add(2);
		assertFalse(list.isEmpty());
		list.remove(0);
		list.remove(0);
		assertTrue(list.isEmpty());
	}

	@Test
	public void testClear() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		list.add(1);
		list.add(2);
		list.clear();
		assertSame(0, list.size());
		// assertTrue(Integer.valueOf(0) == Integer.valueOf(list.size()));
		// assertTrue(Integer.valueOf(0).equals(list.size()));
		assertEquals(0, list.size());
	}

	@Test
	public void testAdd() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		assertTrue(list.add(8));
		assertTrue(list.add(8));
	}

	@Test
	public void testGet() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		list.add(1);
		list.add(2);
		list.add(3);
		assertEquals(1, (int) list.get(0));
		list.remove(0);
		assertEquals(2, (int) list.get(0));
	}

	@Test
	public void testRemoveIndex() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		list.add(1);
		list.add(2);
		list.add(3);
		assertEquals(1, list.remove(0).intValue());
		assertEquals(2, list.remove(0).intValue());
	}

	@Test
	public void testRemoveObject() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		list.add(1);
		assertTrue(list.remove((Integer) 1));
		assertFalse(list.remove((Integer) 1));
	}

	@Test
	public void testIterator() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		list.add(0);
		list.add(1);
		ListIterator<Integer> it = list.listIterator();
		assertEquals(0, (int) it.next());
		assertEquals(1, (int) it.next());
		assertEquals(0, (int) it.previous());

	}

	@Test
	public void testToString() {
		ArrayList<Integer> list = new ArrayList<Integer>(10);
		assertEquals("", list.toString());
		list.add(0);
		list.add(1);
		assertEquals("0 1", list.toString());
	}

}
