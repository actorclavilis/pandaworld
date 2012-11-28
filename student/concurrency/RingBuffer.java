/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package student.concurrency;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 *
 * @author Eileen Liu <el544@cornell.edu>
 */
public class RingBuffer<T> implements BlockingQueue<T> {

    private T[] queue;
    private int headIndex = -1;
    private int tailIndex = -1;

    public RingBuffer(int capacity) {
        queue = (T[]) (Array.newInstance(Object.class, capacity));
        headIndex = 0;
        tailIndex = 0;
    }

    @Override
    public boolean add(T e) {
        if ((tailIndex - headIndex) < (queue.length)) {
            queue[tailIndex % queue.length] = e;
            tailIndex++;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (!(tailIndex == headIndex)) {
            Iterator<T> iter = this.iterator();
            while (iter.hasNext()) {
                T next = iter.next();
                if (o.equals(next)) {
                    iter.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean offer(T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void put(T e) throws InterruptedException {
    }

    @Override
    public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T take() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int remainingCapacity() {
        return (queue.length - (tailIndex - headIndex));
    }

    @Override
    public boolean contains(Object o) {
        Iterator<T> iter = this.iterator();
        while (iter.hasNext()) {
            T next = iter.next();
            if (o.equals(next)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T remove() {
        T removed = this.poll();
        if (removed == null) {
            throw new NoSuchElementException();
        }
        return removed;
    }

    @Override
    public T poll() {
        try{
        T removed = queue[headIndex];
        queue[headIndex] = null;
        headIndex++;
        return removed;
        }
        catch(ArrayIndexOutOfBoundsException a)
        {
           return null; 
        }
    }

    @Override
    public T element() {
        T elem = this.peek();
        if (elem == null) {
            throw new NoSuchElementException();
        }
        return elem;
    }

    @Override
    public T peek() {
        return isEmpty() ? null : queue[headIndex];
    }

    @Override
    public int size() {
        return (tailIndex - headIndex);
    }

    @Override
    public boolean isEmpty() {
        return (queue == null || queue.length == 0 || headIndex == tailIndex);
    }

    @Override
    public Iterator<T> iterator() {
        return new RingIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Iterator<T> objiter = ((RingBuffer)obj).iterator();
        Iterator<T> iter = this.iterator();
        boolean match = true;
        while(iter.hasNext()&&objiter.hasNext())
        {
            match = match&&(iter.next().equals(objiter.next()));
        }
        return (match&&!iter.hasNext()&&!objiter.hasNext());
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOfRange(queue, headIndex, tailIndex);
    }

    @Override
    public void clear() {
        queue = (T[]) (Array.newInstance(Object.class, queue.length));
        headIndex = 0;
        tailIndex = 0;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean wasSuccessful = true;
        if (((tailIndex + c.size() - headIndex) > (queue.length))) {
            throw new IllegalStateException();
        }
        for (T elem : c) {
            wasSuccessful = (wasSuccessful && add(elem));
        }
        return wasSuccessful;
    }

    public boolean addAll(T... c) {
        boolean wasSuccessful = true;
        if (((tailIndex + c.length - headIndex) > (queue.length))) {
            throw new IllegalStateException();
        }
        for (T elem : c) {
            wasSuccessful = (wasSuccessful && add(elem));
        }
        return wasSuccessful;
    }
    ////////////===========UNSUPPORTED METHODS============//////////////

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //////////////////////////////////////////

    private class RingIterator implements Iterator<T> {

        private int index = 0;
        private boolean atStart = true;

        @Override
        public boolean hasNext() {
            if (RingBuffer.this.isEmpty()) {
                return false;
            }
            return atStart || ((index%queue.length) != (tailIndex % queue.length));
        }

        @Override
        public T next() {
            atStart = false;
            try {
                T next = queue[(index+(headIndex%queue.length))%queue.length];
                index++;
                return next;
            } catch (ArrayIndexOutOfBoundsException a) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            try {
                System.arraycopy(queue, index, queue, index - 1, queue.length - index);
                tailIndex--;
            } catch (ArrayIndexOutOfBoundsException a) {
                throw new IllegalStateException();
            }
        }
    }
}
