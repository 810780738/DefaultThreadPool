package com.xiaozhu.ThreadPoolHttpServer.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadPool<Jop extends Runnable> implements ThreadPool<Jop> {

    //最大工作线程者数量
    private static final int MAX_WORKER_NUMBERS = 10;
    //默认线程工作者数量
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    //最小线程工作者数量
    private static final int MIN_WORKER_NUMBERS = 1;
    //需要插入的线程工作者
    private final LinkedList<Jop> jops = new LinkedList<Jop>();
    //线程工作者列表
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());

    //工作者线程数量
    private int workerNum = DEFAULT_WORKER_NUMBERS;
    //生成工作者编号
    private AtomicInteger threadNum = new AtomicInteger();


    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(num);
    }

    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum);
            threadNum.incrementAndGet();
            thread.start();
        }
    }

    public void execute(Jop jop) {
        if (null != jop){
            synchronized (jops){
                jops.addLast(jop);
                jop.notify();
            }
        }
    }

    public void shutdown() {
        //停止所有线程
        for (Worker worker: workers) {
            worker.shutdown();
        }
    }

    public void addWorkers(int num) {
        synchronized (jops){
            //限制最大工作数量
            if (num + this.workerNum > MAX_WORKER_NUMBERS){
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    public void removeWorker(int num) {
        synchronized (jops){
            if (num > this.workerNum){
                throw new IllegalArgumentException("beyond workNum");
            }
         //按照给定的数量停止线程工作者
            int count = 0;
            while (count < num){
                Worker worker = workers.get(num);
                if (workers.remove(worker)){
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;
        }
    }

    public int getJopSize() {
        return jops.size();
    }

    class Worker implements Runnable{

        private volatile boolean running = true;
        public void run() {
            while (running){
                Jop jop = null;
                synchronized (jops){
                    while (jops.isEmpty()){
                        try {
                            jops.wait();
                        } catch (InterruptedException e) {
                            //感知到外部对WorkerThread的中断操作，返回
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                    }
                    jop = jops.removeFirst();
                }
                if (jop != null){
                    jop.run();
                }
            }
        }
        public void shutdown(){
            running = false;
        }
    }
}
