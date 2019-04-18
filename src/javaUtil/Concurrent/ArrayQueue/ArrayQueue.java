package javaUtil.Concurrent.ArrayQueue;

/**
 * 模拟实现并发包下的ArrayBlockingQueue
 * @author YMX
 * @date 2019/4/18 9:34
 */
public final class ArrayQueue<T> {
    /*
     * 队列数量
     */
    private int count = 0;

    /*
     * 最终的数据存储
     */
    private Object[] items;

    /*
     * 队列满时的阻塞锁
     */
    private Object full = new Object();

    /*
     * 队列空时的阻塞锁
     */
    private Object empty = new Object();

    /*
     * 写入数据时的下标
     */
    private int putIndex;

    /*
     * 获取数据时的下标
     */
    private int getIndex;

    public ArrayQueue(int size) {
        items = new Object[size];
    }

    /**
     * 写入队列
     * 队列为满时会阻塞，直到获取线程消费了队列数据后，唤醒写入线程
     * @param t
     */
    public void put(T t){
        synchronized (full){
            while (count == items.length){
                try{
                    full.wait();
                }catch (InterruptedException e){
                    break;
                }
            }
        }

        synchronized (empty){
            //写入
            items[putIndex] = t;
            count++;

            putIndex++;
            if(putIndex == items.length){
                //超过数组长度后需要从头开始
                putIndex = 0;
            }
            //写入数据成功后就把消费队列的线程唤醒
            empty.notify();

        }
    }

    /**
     * 消费队列
     * 队列为空时会阻塞，直到写入线程写入了队列数据后，唤醒消费线程
     * @return
     */
    public T get(){
        synchronized (empty){
            while (count == 0){
                try{
                    empty.wait();
                }catch (InterruptedException e){
                    return null;
                }
            }
        }

        synchronized (full){
            Object result = items[getIndex];
            items[getIndex] = null;
            count--;

            getIndex++;
            if (getIndex == items.length){
                getIndex = 0;
            }
            full.notify();

            return (T)result;
        }
    }

    public long size() {
        return items.length;
    }

    public boolean isEmpty() {
        if (count == 0)
            return true;
        return false;
    }
}
