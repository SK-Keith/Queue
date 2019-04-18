package javaUtil.Concurrent.ArrayQueue;

import org.junit.Test;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;


/**
 * 测试ArrayQueue
 * @author YMX
 * @date 2019/4/18 10:44
 */
public class ArrayQueueTest {

    /**
     * 单线程写入消费
     */
    @Test
    public void put(){
        ArrayQueue<String> queue = new ArrayQueue<>(3);
        queue.put("keith");
        queue.put("what");
        queue.put("why");
        System.out.println(queue.size());

        while(!queue.isEmpty()){
            System.out.println(queue.get());
        }
        System.out.println("over");
    }

    /**
     * 从运行结果能看出只有当消费数据后才能接着往队列里写入数据
     */
    @Test
    public void put2(){
        final ArrayQueue<String> queue = new ArrayQueue<>(3);

        new Thread(()->{
            try{
                LOGGER.info("[" + Thread.currentThread().getName()+"]" + queue.get());
            }catch (Exception e){
            }
        }).start();

        queue.put("keith");
        queue.put("what");
        queue.put("why");
        queue.put("when");
        LOGGER.info("size" + queue.size());

        while (!queue.isEmpty()){
            LOGGER.info(queue.get());
        }
    }

    /**
     * 没有消费时，再往队列里写数据则会导致写入线程被阻塞
     */
    @Test
    public void put3(){
        final ArrayQueue<String> queue = new ArrayQueue<>(3);

        queue.put("keith");
        queue.put("what");
        queue.put("why");
        queue.put("when");
        System.out.println(queue.size());

        while (!queue.isEmpty()){
            System.out.println(queue.get());
        }
    }

    /**
     * 三个线程并发写入300条数据，其中一个线程消费一条。
     * 由于不管是写入还是获取方法里的操作都需要获取锁才能操作，所以整个队列是线程安全的。
     * @throws InterruptedException
     */
    @Test
    public void put4() throws  InterruptedException{
        final ArrayQueue<String> queue = new ArrayQueue<>(299);

        Thread t1 = new Thread(() ->{
           for (int i=0; i< 100; i++){
               queue.put(i + "");
           }
        });

        Thread t2 = new Thread(() -> {
            for (int i=0; i< 100; i++){
                queue.put(i + "");
            }
        });

        Thread t3 = new Thread(() -> {
            for (int i=0; i< 100; i++){
                queue.put(i + "");
            }
        });

        Thread t4 = new Thread(() -> {
            System.out.println("=====" + queue.get());
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        System.out.println(queue.size());
    }

}
